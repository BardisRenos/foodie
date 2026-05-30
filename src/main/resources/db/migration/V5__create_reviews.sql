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