CREATE TABLE products (
    id             VARCHAR(36)   NOT NULL PRIMARY KEY,
    name           VARCHAR(255)  NOT NULL,
    description    VARCHAR(1000),
    price          DECIMAL(10,2) NOT NULL,
    unit           VARCHAR(50),
    stock_quantity INTEGER       NOT NULL DEFAULT 0,
    category       VARCHAR(50),
    available      BOOLEAN       NOT NULL DEFAULT TRUE,
    farmer_id      VARCHAR(36)   NOT NULL,
    image_url      VARCHAR(500),
    created_at     TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_product_farmer_id  ON products(farmer_id);
CREATE INDEX idx_product_category   ON products(category);
CREATE INDEX idx_product_available  ON products(available);
CREATE INDEX idx_product_name       ON products(name);
CREATE INDEX idx_product_price      ON products(price);
CREATE INDEX idx_product_created_at ON products(created_at);

-- Composite index for most common query: available products by category
CREATE INDEX idx_product_available_category ON products(available, category);
-- Composite index for farmer's available products
CREATE INDEX idx_product_farmer_available   ON products(farmer_id, available);