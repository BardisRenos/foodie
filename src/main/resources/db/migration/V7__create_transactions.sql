CREATE TABLE transactions (
    id                VARCHAR(36)  NOT NULL PRIMARY KEY,
    actor_id          VARCHAR(36)  NOT NULL,
    actor_type        VARCHAR(20)  NOT NULL,
    type              VARCHAR(50)  NOT NULL,
    related_entity_id VARCHAR(36),
    description       VARCHAR(500),
    ip_address        VARCHAR(50),
    status            VARCHAR(20)  NOT NULL,
    created_at        TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_transaction_actor_id        ON transactions(actor_id);
CREATE INDEX idx_transaction_type            ON transactions(type);
CREATE INDEX idx_transaction_status          ON transactions(status);
CREATE INDEX idx_transaction_created_at      ON transactions(created_at);

-- Composite index for actor audit trail
CREATE INDEX idx_transaction_actor_type      ON transactions(actor_id, type);
CREATE INDEX idx_transaction_actor_created   ON transactions(actor_id, created_at);