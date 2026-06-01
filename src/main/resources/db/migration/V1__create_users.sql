CREATE TABLE users (
    id            VARCHAR(36)  NOT NULL PRIMARY KEY,
    user_id       VARCHAR(20)  NOT NULL UNIQUE,
    full_name     VARCHAR(255) NOT NULL,
    email         VARCHAR(255) NOT NULL UNIQUE,
    password      VARCHAR(255) NOT NULL,
    phone         VARCHAR(50),
    address       VARCHAR(500),
    role          VARCHAR(20)  NOT NULL DEFAULT 'CONSUMER',
    created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_user_user_id    ON users(user_id);
CREATE INDEX idx_user_role       ON users(role);
CREATE INDEX idx_user_created_at ON users(created_at);