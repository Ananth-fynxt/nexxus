CREATE TABLE revinfo (
    rev INTEGER NOT NULL,
    revtstmp BIGINT,
    PRIMARY KEY (rev)
);

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_sequences WHERE sequencename = 'revinfo_seq') THEN
        CREATE SEQUENCE revinfo_seq
            START WITH 1
            INCREMENT BY 50
            MINVALUE 1
            NO MAXVALUE
            CACHE 1;
    END IF;
END $$;

SELECT setval('revinfo_seq', 1, false);

CREATE TABLE psps_aud (
    id UUID NOT NULL,
    rev INTEGER NOT NULL,
    revtype SMALLINT,
    name TEXT,
    description TEXT,
    logo TEXT,
    credential JSONB,
    timeout INTEGER,
    block_vpn_access BOOLEAN,
    block_data_center_access BOOLEAN,
    failure_rate BOOLEAN,
    ip_address TEXT[],
    brand_id UUID,
    environment_id UUID,
    flow_target_id TEXT,
    status status,
    failure_rate_threshold REAL,
    failure_rate_duration_minutes INTEGER,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP,
    created_by INTEGER,
    updated_by INTEGER,
    deleted_by INTEGER,
    PRIMARY KEY (id, rev),
    FOREIGN KEY (rev) REFERENCES revinfo(rev)
);

CREATE TABLE psp_operations_aud (
    psp_id UUID NOT NULL,
    flow_action_id TEXT NOT NULL,
    flow_definition_id TEXT NOT NULL,
    rev INTEGER NOT NULL,
    revtype SMALLINT,
    currencies TEXT[],
    countries TEXT[],
    status status,
    PRIMARY KEY (psp_id, flow_action_id, flow_definition_id, rev),
    FOREIGN KEY (rev) REFERENCES revinfo(rev)
);

CREATE TABLE maintenance_windows_aud (
    id INTEGER NOT NULL,
    rev INTEGER NOT NULL,
    revtype SMALLINT,
    psp_id UUID,
    flow_action_id TEXT,
    start_at TIMESTAMP,
    end_at TIMESTAMP,
    status status,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP,
    created_by INTEGER,
    updated_by INTEGER,
    deleted_by INTEGER,
    PRIMARY KEY (id, rev),
    FOREIGN KEY (rev) REFERENCES revinfo(rev)
);

CREATE INDEX idx_psps_aud_rev ON psps_aud(rev);
CREATE INDEX idx_psps_aud_revtype ON psps_aud(revtype);
CREATE INDEX idx_psps_aud_brand_id ON psps_aud(brand_id);
CREATE INDEX idx_psps_aud_environment_id ON psps_aud(environment_id);

CREATE INDEX idx_psp_operations_aud_rev ON psp_operations_aud(rev);
CREATE INDEX idx_psp_operations_aud_revtype ON psp_operations_aud(revtype);
CREATE INDEX idx_psp_operations_aud_psp_id ON psp_operations_aud(psp_id);

CREATE INDEX idx_maintenance_windows_aud_rev ON maintenance_windows_aud(rev);
CREATE INDEX idx_maintenance_windows_aud_revtype ON maintenance_windows_aud(revtype);
CREATE INDEX idx_maintenance_windows_aud_psp_id ON maintenance_windows_aud(psp_id);
