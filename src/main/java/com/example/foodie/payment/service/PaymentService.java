package com.example.foodie.payment.service;

import com.example.foodie.order.service.OrderService;
import com.example.foodie.payment.dto.PaymentDto;
import com.example.foodie.payment.internal.Payment;
import com.example.foodie.payment.internal.PaymentRepository;
import com.example.foodie.payment.internal.PaymentStatus;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Refund;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.RefundCreateParams;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderService orderService;

    @Value("${stripe.api-key}")
    private String stripeApiKey;

    @Value("${stripe.currency:eur}")
    private String currency;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeApiKey;
    }

    @Transactional
    public PaymentDto.PaymentResponse createPaymentIntent(PaymentDto.CreatePaymentRequest request) {
        // Get order total from order service
        var order = orderService.findById(request.orderId());

        // Check no existing payment
        paymentRepository.findByOrderId(request.orderId()).ifPresent(p -> {
            if (p.getStatus() == PaymentStatus.COMPLETED) {
                throw new IllegalStateException("Order already paid");
            }
        });

        try {
            // Convert to cents for Stripe
            long amountInCents = order.totalPrice()
                    .multiply(BigDecimal.valueOf(100))
                    .longValue();

            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(amountInCents)
                    .setCurrency(currency)
                    .putMetadata("orderId", request.orderId())
                    .putMetadata("userId", request.userId())
                    .build();

            PaymentIntent intent = PaymentIntent.create(params);

            // Save payment record
            Payment payment = new Payment();
            payment.setOrderId(request.orderId());
            payment.setUserId(request.userId());
            payment.setStripePaymentIntentId(intent.getId());
            payment.setStripeClientSecret(intent.getClientSecret());
            payment.setStatus(PaymentStatus.PENDING);
            payment.setAmount(order.totalPrice());
            payment.setCurrency(currency);
            paymentRepository.save(payment);

            log.info("Payment intent created for order: {}", request.orderId());
            return toResponse(payment);

        } catch (StripeException e) {
            log.error("Stripe error creating payment intent: {}", e.getMessage());
            throw new RuntimeException("Payment failed: " + e.getMessage());
        }
    }

    @Transactional
    public PaymentDto.PaymentResponse refund(PaymentDto.RefundRequest request) {
        Payment payment = paymentRepository.findByOrderId(request.orderId())
                .orElseThrow(() -> new RuntimeException("Payment not found for order: " + request.orderId()));

        if (payment.getStatus() != PaymentStatus.COMPLETED) {
            throw new IllegalStateException("Can only refund completed payments");
        }

        try {
            RefundCreateParams params = RefundCreateParams.builder()
                    .setPaymentIntent(payment.getStripePaymentIntentId())
                    .setReason(RefundCreateParams.Reason.REQUESTED_BY_CUSTOMER)
                    .build();

            Refund refund = Refund.create(params);

            payment.setStripeRefundId(refund.getId());
            payment.setStatus(PaymentStatus.REFUNDED);
            payment.setUpdatedAt(LocalDateTime.now());
            paymentRepository.save(payment);

            log.info("Refund processed for order: {}", request.orderId());
            return toResponse(payment);

        } catch (StripeException e) {
            log.error("Stripe error processing refund: {}", e.getMessage());
            throw new RuntimeException("Refund failed: " + e.getMessage());
        }
    }

    @Transactional
    public void handleWebhook(String payload, String sigHeader) {
        try {
            com.stripe.model.Event event = com.stripe.net.Webhook.constructEvent(
                    payload, sigHeader,
                    System.getProperty("stripe.webhook-secret")
            );

            switch (event.getType()) {
                case "payment_intent.succeeded" -> {
                    PaymentIntent intent = (PaymentIntent) event.getDataObjectDeserializer()
                            .getObject().orElseThrow();
                    handlePaymentSucceeded(intent);
                }
                case "payment_intent.payment_failed" -> {
                    PaymentIntent intent = (PaymentIntent) event.getDataObjectDeserializer()
                            .getObject().orElseThrow();
                    handlePaymentFailed(intent);
                }
                default -> log.debug("Unhandled Stripe event: {}", event.getType());
            }
        } catch (Exception e) {
            log.error("Webhook error: {}", e.getMessage());
            throw new RuntimeException("Webhook processing failed: " + e.getMessage());
        }
    }

    private void handlePaymentSucceeded(PaymentIntent intent) {
        paymentRepository.findByStripePaymentIntentId(intent.getId()).ifPresent(payment -> {
            payment.setStatus(PaymentStatus.COMPLETED);
            payment.setUpdatedAt(LocalDateTime.now());
            paymentRepository.save(payment);

            // Update order status to CONFIRMED
            orderService.updateStatus(payment.getOrderId(), "CONFIRMED");
            log.info("Payment succeeded for order: {}", payment.getOrderId());
        });
    }

    private void handlePaymentFailed(PaymentIntent intent) {
        paymentRepository.findByStripePaymentIntentId(intent.getId()).ifPresent(payment -> {
            payment.setStatus(PaymentStatus.FAILED);
            payment.setFailureReason(intent.getLastPaymentError() != null
                    ? intent.getLastPaymentError().getMessage() : "Unknown");
            payment.setUpdatedAt(LocalDateTime.now());
            paymentRepository.save(payment);
            log.warn("Payment failed for order: {}", payment.getOrderId());
        });
    }

    public PaymentDto.PaymentResponse findByOrder(String orderId) {
        return paymentRepository.findByOrderId(orderId)
                .map(this::toResponse)
                .orElseThrow(() -> new RuntimeException("Payment not found for order: " + orderId));
    }

    public List<PaymentDto.PaymentResponse> findByUser(String userId) {
        return paymentRepository.findByUserId(userId)
                .stream().map(this::toResponse).toList();
    }

    private PaymentDto.PaymentResponse toResponse(Payment p) {
        return new PaymentDto.PaymentResponse(
                p.getId(), p.getOrderId(), p.getUserId(),
                p.getStripeClientSecret(), p.getStripePaymentIntentId(),
                p.getStatus().name(), p.getAmount(), p.getCurrency(),
                p.getCreatedAt()
        );
    }
}