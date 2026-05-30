CREATE TABLE users (
    id            VARCHAR(36)  NOT NULL PRIMARY KEY,
    full_name     VARCHAR(255) NOT NULL,
    email         VARCHAR(255) NOT NULL UNIQUE,
    password      VARCHAR(255) NOT NULL,
    phone         VARCHAR(50),
    address       VARCHAR(500),
    role          VARCHAR(20)  NOT NULL DEFAULT 'CONSUMER',
    created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);