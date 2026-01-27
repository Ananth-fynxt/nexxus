CREATE TABLE risk_rule (
    id SERIAL NOT NULL,
    version INTEGER NOT NULL DEFAULT 1,
    name TEXT NOT NULL,
    type risk_type NOT NULL,
    action risk_action NOT NULL,
    currency TEXT NOT NULL,
    duration risk_duration NOT NULL,
    criteria_type risk_customer_criteria_type,
    criteria_value TEXT[],
    max_amount NUMERIC(20,8) NOT NULL,
    brand_id UUID NOT NULL,
    environment_id UUID NOT NULL,
    flow_action_id TEXT NOT NULL,
    status status NOT NULL DEFAULT 'ENABLED',
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    deleted_at TIMESTAMP NULL,
    created_by INTEGER NOT NULL,
    updated_by INTEGER NOT NULL,
    deleted_by INTEGER NULL,

    CONSTRAINT risk_rule_version_pk PRIMARY KEY (id, version),
    CONSTRAINT fk_risk_rule_brand FOREIGN KEY (brand_id) REFERENCES brands(id),
    CONSTRAINT fk_risk_rule_environment FOREIGN KEY (environment_id) REFERENCES environments(id),
    CONSTRAINT fk_risk_rule_flow_action FOREIGN KEY (flow_action_id) REFERENCES flow_actions(id)
);

CREATE TABLE risk_rule_psps (
    risk_rule_id INTEGER NOT NULL,
    risk_rule_version INTEGER NOT NULL,
    psp_id UUID NOT NULL,

    CONSTRAINT risk_rule_psp_pk PRIMARY KEY (risk_rule_id, risk_rule_version, psp_id),
    CONSTRAINT fk_risk_rule_psps_risk_rule FOREIGN KEY (risk_rule_id, risk_rule_version) REFERENCES risk_rule(id, version),
    CONSTRAINT fk_risk_rule_psps_psp FOREIGN KEY (psp_id) REFERENCES psps(id)
);

CREATE INDEX idx_risk_rule_brand_id_environment_id ON risk_rule(brand_id, environment_id);

CREATE INDEX idx_risk_rule_psps_risk_rule_id_version ON risk_rule_psps(risk_rule_id, risk_rule_version);