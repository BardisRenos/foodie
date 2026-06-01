package com.example.foodie.review.service;

import com.example.foodie.order.service.OrderService;
import com.example.foodie.review.dto.ReviewDto;
import com.example.foodie.review.internal.Review;
import com.example.foodie.review.internal.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final OrderService orderService;

    @CacheEvict(value = "reviews", allEntries = true)
    public ReviewDto.ReviewResponse create(ReviewDto.CreateRequest request) {

        // Validate order exists and is DELIVERED
        if (request.orderId() != null) {
            boolean isDelivered = orderService.isOrderDelivered(request.orderId());
            if (!isDelivered) {
                throw new IllegalStateException(
                        "You can only review products from delivered orders"
                );
            }

            // Validate the user owns this order
            boolean isOwner = orderService.isOrderOwner(request.orderId(), request.userId());
            if (!isOwner) {
                throw new IllegalStateException("You can only review your own orders");
            }
        }

        // Prevent duplicate review
        boolean alreadyReviewed = reviewRepository
                .existsByUserIdAndProductIdAndOrderId(
                        request.userId(), request.productId(), request.orderId()
                );
        if (alreadyReviewed) {
            throw new IllegalStateException(
                    "You have already reviewed this product for this order"
            );
        }

        Review r = new Review();
        r.setUserId(request.userId());
        r.setProductId(request.productId());
        r.setFarmerId(request.farmerId());
        r.setOrderId(request.orderId());
        r.setRating(request.rating());
        r.setComment(request.comment());
        return toResponse(reviewRepository.save(r));
    }

    @Cacheable(value = "reviews", key = "'product_' + #productId")
    public List<ReviewDto.ReviewResponse> findByProduct(String productId) {
        return reviewRepository.findByProductId(productId).stream().map(this::toResponse).toList();
    }

    public List<ReviewDto.ReviewResponse> findByFarmer(String farmerId) {
        return reviewRepository.findByFarmerId(farmerId).stream().map(this::toResponse).toList();
    }

    @Cacheable(value = "reviews", key = "'avg_product_' + #productId")
    public Double avgRatingForProduct(String productId) {
        return reviewRepository.avgRatingByProduct(productId);
    }

    public Double avgRatingForFarmer(String farmerId) {
        return reviewRepository.avgRatingByFarmer(farmerId);
    }

    private ReviewDto.ReviewResponse toResponse(Review r) {
        return new ReviewDto.ReviewResponse(
            r.getId(), r.getUserId(), r.getProductId(), r.getFarmerId(),
            r.getRating(), r.getComment(), r.getCreatedAt()
        );
    }
}
