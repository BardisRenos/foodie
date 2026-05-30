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