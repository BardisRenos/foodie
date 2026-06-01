CREATE TABLE orders (
    id               VARCHAR(36)   NOT NULL PRIMARY KEY,
    user_id          VARCHAR(36)   NOT NULL,
    farmer_id        VARCHAR(36)   NOT NULL,
    status           VARCHAR(30)   NOT NULL DEFAULT 'PLACED',
    total_price      DECIMAL(10,2),
    delivery_address VARCHAR(500),
    notes            VARCHAR(1000),
    created_at       TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_order_user_id    ON orders(user_id);
CREATE INDEX idx_order_farmer_id  ON orders(farmer_id);
CREATE INDEX idx_order_status     ON orders(status);
CREATE INDEX idx_order_created_at ON orders(created_at);

-- Composite indexes for most common queries
CREATE INDEX idx_order_user_status    ON orders(user_id, status);
CREATE INDEX idx_order_farmer_status  ON orders(farmer_id, status);

CREATE TABLE order_items (
    id           VARCHAR(36)   NOT NULL PRIMARY KEY,
    order_id     VARCHAR(36)   NOT NULL,
    product_id   VARCHAR(36)   NOT NULL,
    product_name VARCHAR(255),
    quantity     INTEGER       NOT NULL,
    unit_price   DECIMAL(10,2),
    subtotal     DECIMAL(10,2),
    CONSTRAINT fk_order FOREIGN KEY (order_id) REFERENCES orders(id)
);

CREATE INDEX idx_order_item_order_id   ON order_items(order_id);
CREATE INDEX idx_order_item_product_id ON order_items(product_id);