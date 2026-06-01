CREATE TABLE farmers (
    id            VARCHAR(36)   NOT NULL PRIMARY KEY,
    farmer_id     VARCHAR(20)   NOT NULL UNIQUE,
    full_name     VARCHAR(255)  NOT NULL,
    email         VARCHAR(255)  NOT NULL UNIQUE,
    password      VARCHAR(255)  NOT NULL,
    phone         VARCHAR(50),
    farm_name     VARCHAR(255)  NOT NULL,
    farm_location VARCHAR(255),
    description   VARCHAR(1000),
    verified      BOOLEAN       NOT NULL DEFAULT FALSE,
    created_at    TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_farmer_farmer_id    ON farmers(farmer_id);
CREATE INDEX idx_farmer_verified     ON farmers(verified);
CREATE INDEX idx_farmer_farm_location ON farmers(farm_location);
CREATE INDEX idx_farmer_created_at   ON farmers(created_at);