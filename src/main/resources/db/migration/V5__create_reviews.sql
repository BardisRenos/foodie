CREATE TABLE reviews (
    id         VARCHAR(36) NOT NULL PRIMARY KEY,
    user_id    VARCHAR(36) NOT NULL,
    product_id VARCHAR(36) NOT NULL,
    farmer_id  VARCHAR(36),
    order_id   VARCHAR(36),
    rating     INTEGER     NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comment    VARCHAR(2000),
    created_at TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_review_product_id ON reviews(product_id);
CREATE INDEX idx_review_farmer_id  ON reviews(farmer_id);
CREATE INDEX idx_review_user_id    ON reviews(user_id);
CREATE INDEX idx_review_order_id   ON reviews(order_id);
CREATE INDEX idx_review_rating     ON reviews(rating);

-- Composite index for duplicate review check
CREATE UNIQUE INDEX idx_review_unique ON reviews(user_id, product_id, order_id);