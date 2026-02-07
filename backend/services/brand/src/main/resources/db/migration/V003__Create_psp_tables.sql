CREATE TABLE psps (
    id UUID PRIMARY KEY NOT NULL,
    name TEXT NOT NULL,
    description TEXT,
    logo TEXT,
    credential JSONB NOT NULL,
    timeout INTEGER NOT NULL DEFAULT 300,
    block_vpn_access BOOLEAN DEFAULT FALSE,
    block_data_center_access BOOLEAN DEFAULT FALSE,
    failure_rate BOOLEAN DEFAULT FALSE,
    failure_rate_threshold REAL DEFAULT 0,
    failure_rate_duration_minutes INTEGER DEFAULT 60,
    ip_address TEXT[],
    brand_id UUID NOT NULL REFERENCES brands(id),
    environment_id UUID NOT NULL REFERENCES environments(id),
    flow_target_id TEXT NOT NULL REFERENCES flow_targets(id),
    status status NOT NULL DEFAULT 'ENABLED',
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    deleted_at TIMESTAMP NULL,
    created_by INTEGER NOT NULL,
    updated_by INTEGER NOT NULL,
    deleted_by INTEGER NULL
);

CREATE TABLE psp_operations (
    psp_id UUID NOT NULL REFERENCES psps(id),
    flow_action_id TEXT NOT NULL REFERENCES flow_actions(id),
    flow_definition_id TEXT NOT NULL REFERENCES flow_definitions(id),
    currencies TEXT[],
    countries TEXT[],
    status status NOT NULL DEFAULT 'ENABLED',
    PRIMARY KEY (psp_id, flow_action_id, flow_definition_id)
);

CREATE TABLE maintenance_windows (
    id SERIAL PRIMARY KEY NOT NULL,
    psp_id UUID NOT NULL REFERENCES psps(id),
    flow_action_id TEXT NOT NULL REFERENCES flow_actions(id),
    start_at TIMESTAMP NOT NULL,
    end_at TIMESTAMP NOT NULL,
    status status NOT NULL DEFAULT 'ENABLED',
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    deleted_at TIMESTAMP NULL,
    created_by INTEGER NOT NULL,
    updated_by INTEGER NOT NULL,
    deleted_by INTEGER NULL
);

CREATE TABLE psp_groups (
    id SERIAL NOT NULL,
    version INTEGER NOT NULL DEFAULT 1,
    brand_id UUID NOT NULL REFERENCES brands(id),
    environment_id UUID NOT NULL REFERENCES environments(id),
    name TEXT NOT NULL,
    flow_action_id TEXT NOT NULL REFERENCES flow_actions(id),
    currency TEXT NOT NULL,
    status status NOT NULL DEFAULT 'ENABLED',
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    deleted_at TIMESTAMP NULL,
    created_by INTEGER NOT NULL,
    updated_by INTEGER NOT NULL,
    deleted_by INTEGER NULL,
    PRIMARY KEY (id, version)
);

CREATE TABLE psp_group_psps (
    psp_group_id INTEGER NOT NULL,
    psp_group_version INTEGER NOT NULL,
    psp_id UUID NOT NULL,

    CONSTRAINT psp_group_psp_pk PRIMARY KEY (psp_group_id, psp_group_version, psp_id),
    CONSTRAINT fk_psp_group_psps_psp_group FOREIGN KEY (psp_group_id, psp_group_version) REFERENCES psp_groups(id, version),
    CONSTRAINT fk_psp_group_psps_psp FOREIGN KEY (psp_id) REFERENCES psps(id)
);

CREATE UNIQUE INDEX idx_psp_brand_env_flow_target_name ON psps(brand_id, environment_id, flow_target_id, name) WHERE deleted_at IS NULL;
CREATE INDEX idx_psp_brand_env ON psps(brand_id, environment_id);

CREATE INDEX idx_psp_operations_psp_id ON psp_operations(psp_id);

CREATE INDEX idx_maintenance_windows_psp_id ON maintenance_windows(psp_id);

CREATE INDEX idx_psp_groups_brand_env ON psp_groups(brand_id, environment_id);

CREATE INDEX idx_psp_group_psps_psp_group_id_version ON psp_group_psps(psp_group_id, psp_group_version);