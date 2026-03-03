CREATE TABLE IF NOT EXISTS progress_state (
    id BIGINT PRIMARY KEY,
    behavior_name     VARCHAR(255) NOT NULL,
    step_percent      DOUBLE       NOT NULL,
    progress_percent  DOUBLE       NOT NULL,
    completed_count   BIGINT       NOT NULL,
    created_at        TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

