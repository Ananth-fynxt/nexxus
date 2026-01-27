CREATE TABLE sessions (
    id BIGSERIAL PRIMARY KEY,
    brand_id UUID NOT NULL,
    environment_id UUID NOT NULL,
    txn_id TEXT NOT NULL,
    txn_version INTEGER NOT NULL,
    session_token_hash TEXT NOT NULL,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    last_accessed_at TIMESTAMP WITH TIME ZONE,
    timeout_minutes INTEGER NOT NULL DEFAULT 5,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by INTEGER NOT NULL,
    updated_by INTEGER NOT NULL,

    CONSTRAINT fk_sessions_transaction FOREIGN KEY (brand_id, environment_id, txn_id, txn_version) REFERENCES transactions(brand_id, environment_id, txn_id, version)
);

CREATE INDEX idx_sessions_transaction ON sessions (brand_id, environment_id, txn_id, txn_version);
