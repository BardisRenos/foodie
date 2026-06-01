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

CREATE INDEX idx_notification_recipient_id   ON notifications(recipient_id);
CREATE INDEX idx_notification_read           ON notifications(read);
CREATE INDEX idx_notification_created_at     ON notifications(created_at);

-- Composite index for unread notifications per recipient
CREATE INDEX idx_notification_recipient_read ON notifications(recipient_id, read);