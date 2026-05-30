CREATE TABLE products (
    id             VARCHAR(36)    NOT NULL PRIMARY KEY,
    name           VARCHAR(255)   NOT NULL,
    description    VARCHAR(1000),
    price          DECIMAL(10,2)  NOT NULL,
    unit           VARCHAR(50),
    stock_quantity INTEGER        NOT NULL DEFAULT 0,
    category       VARCHAR(50),
    available      BOOLEAN        NOT NULL DEFAULT TRUE,
    farmer_id      VARCHAR(36)    NOT NULL,
    image_url      VARCHAR(500),
    created_at     TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP
);