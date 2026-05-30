CREATE TABLE notifications (
    id               VARCHAR(36)  NOT NULL PRIMARY KEY,
    recipient_id     VARCHAR(36)  NOT NULL,
    recipient_type   VARCHAR(20)  NOT NULL,
    title            VARCHAR(255),
    message          VARCHAR(1000),
    read             BOOLEAN      NOT NULL DEFAULT FALSE,
    related_order_id VARCHAR(36),
    created_at       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);