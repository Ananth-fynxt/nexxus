CREATE TABLE fee (
    id SERIAL NOT NULL,
    version INTEGER NOT NULL DEFAULT 1,
    name TEXT NOT NULL,
    currency TEXT NOT NULL,
    countries TEXT[] NOT NULL,
    charge_fee_type charge_fee_type NOT NULL,
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

    CONSTRAINT fee_version_pk PRIMARY KEY (id, version),
    CONSTRAINT fk_fee_brand FOREIGN KEY (brand_id) REFERENCES brands(id),
    CONSTRAINT fk_fee_environment FOREIGN KEY (environment_id) REFERENCES environments(id),
    CONSTRAINT fk_fee_flow_action FOREIGN KEY (flow_action_id) REFERENCES flow_actions(id)
);

CREATE TABLE fee_components (
    id TEXT NOT NULL,
    fee_id INTEGER NOT NULL,
    fee_version INTEGER NOT NULL,
    fee_component_type fee_component_type NOT NULL,
    amount NUMERIC(18,6) NOT NULL,
    min_value NUMERIC(18,6),
    max_value NUMERIC(18,6),

    CONSTRAINT fee_component_pk PRIMARY KEY (id, fee_id, fee_version),
    CONSTRAINT fk_fee_components_fee FOREIGN KEY (fee_id, fee_version) REFERENCES fee(id, version)
);  

CREATE TABLE fee_psps (
    fee_id INTEGER NOT NULL,
    fee_version INTEGER NOT NULL,
    psp_id UUID NOT NULL,

    CONSTRAINT fee_psp_unique PRIMARY KEY (fee_id, fee_version, psp_id),
    CONSTRAINT fk_fee_psps_fee FOREIGN KEY (fee_id, fee_version) REFERENCES fee(id, version),
    CONSTRAINT fk_fee_psps_psp FOREIGN KEY (psp_id) REFERENCES psps(id)
);

CREATE INDEX idx_fee_brand_id_environment_id ON fee(brand_id, environment_id);

CREATE INDEX idx_fee_components_fee_id ON fee_components(fee_id, fee_version);

CREATE INDEX idx_fee_psps_fee_id ON fee_psps(fee_id, fee_version);