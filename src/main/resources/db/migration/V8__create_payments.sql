CREATE TABLE payments (
    id                        VARCHAR(36)   NOT NULL PRIMARY KEY,
    order_id                  VARCHAR(36)   NOT NULL,
    user_id                   VARCHAR(36)   NOT NULL,
    stripe_payment_intent_id  VARCHAR(255),
    stripe_client_secret      VARCHAR(500),
    stripe_refund_id          VARCHAR(255),
    status                    VARCHAR(30)   NOT NULL,
    amount                    DECIMAL(10,2),
    currency                  VARCHAR(10),
    failure_reason            VARCHAR(500),
    created_at                TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_payment_order  ON payments(order_id);
CREATE INDEX idx_payment_user   ON payments(user_id);
CREATE INDEX idx_payment_stripe ON payments(stripe_payment_intent_id);