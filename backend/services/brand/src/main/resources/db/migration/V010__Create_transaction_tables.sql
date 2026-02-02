CREATE TABLE transactions (
    brand_id UUID NOT NULL,
    environment_id UUID NOT NULL,
    txn_id TEXT NOT NULL,
    version INTEGER NOT NULL,
    request_id UUID,
    flow_action_id TEXT,
    flow_target_id TEXT,
    psp_id UUID,
    psp_txn_id TEXT,
    customer_id TEXT,
    customer_tag TEXT,
    customer_account_type TEXT,
    external_request_id TEXT,
    transaction_type TEXT,
    status transaction_status,
    txn_currency TEXT,
    txn_fee DECIMAL(20,8),
    txn_amount DECIMAL(20,8),
    execute_payload JSONB,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by INTEGER,
    updated_by INTEGER,

    PRIMARY KEY (brand_id, environment_id, txn_id, version)
);

CREATE INDEX idx_transactions_brand_env_txn_version ON transactions(brand_id, environment_id, txn_id, version DESC);

CREATE INDEX idx_transactions_brand_env_customer_id ON transactions(brand_id, environment_id, customer_id);

CREATE INDEX idx_transactions_brand_env_transaction_type_status ON transactions(brand_id, environment_id, transaction_type, status);

CREATE INDEX idx_transactions_psp_risk_rules ON transactions (psp_id, brand_id, environment_id, flow_action_id, txn_currency, created_at) WHERE psp_id IS NOT NULL;

CREATE INDEX idx_transactions_status_created_at ON transactions (status, created_at);

CREATE INDEX idx_transactions_brand_env_time ON transactions (brand_id, environment_id, created_at);

CREATE INDEX idx_transactions_routing_calculations ON transactions (psp_id, brand_id, environment_id, flow_action_id, txn_currency, created_at) WHERE psp_id IS NOT NULL;

CREATE INDEX idx_transactions_routing_status ON transactions (status, created_at) WHERE status = 'SUCCESS';

CREATE INDEX idx_transactions_routing_composite ON transactions (brand_id, environment_id, flow_action_id, txn_currency, created_at, psp_id) WHERE psp_id IS NOT NULL;