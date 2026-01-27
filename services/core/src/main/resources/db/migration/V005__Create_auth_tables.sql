CREATE TABLE brand_roles (
    id SERIAL PRIMARY KEY NOT NULL,
    brand_id UUID NOT NULL REFERENCES brands(id),
    environment_id UUID NOT NULL REFERENCES environments(id),
    name TEXT NOT NULL,
    permission JSONB NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    deleted_at TIMESTAMP NULL,
    created_by INTEGER NOT NULL,
    updated_by INTEGER NOT NULL,
    deleted_by INTEGER NULL
);

CREATE TABLE brand_users (
    id SERIAL PRIMARY KEY NOT NULL,
    brand_id UUID REFERENCES brands(id),
    environment_id UUID REFERENCES environments(id),
    brand_role_id INTEGER REFERENCES brand_roles(id),
    name TEXT NOT NULL,
    email TEXT NOT NULL,
    user_id INTEGER NOT NULL REFERENCES users(id),
    scope scope NOT NULL DEFAULT 'BRAND',
    status user_status NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    deleted_at TIMESTAMP NULL,
    created_by INTEGER NOT NULL,
    updated_by INTEGER NOT NULL,
    deleted_by INTEGER NULL
);

CREATE TABLE tokens (
    id BIGSERIAL PRIMARY KEY NOT NULL,
    customer_id TEXT NOT NULL,
    token_hash TEXT NOT NULL,
    issued_at TIMESTAMP WITH TIME ZONE NOT NULL,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    status token_status NOT NULL DEFAULT 'ACTIVE',
    token_type token_type NOT NULL DEFAULT 'ACCESS',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_by INTEGER NOT NULL,
    updated_by INTEGER NOT NULL
);

CREATE INDEX idx_tokens_customer_id ON tokens(customer_id);

CREATE INDEX idx_users_email ON users(email);

CREATE INDEX idx_brand_roles_brand_id ON brand_roles(brand_id, environment_id);
CREATE UNIQUE INDEX idx_brand_roles_brand_name_permission ON brand_roles(brand_id, environment_id, name);

CREATE INDEX idx_brand_users_brand_id ON brand_users(brand_id, environment_id);
CREATE INDEX idx_brand_users_email ON brand_users(email);
CREATE UNIQUE INDEX idx_brand_users_brand_name_email ON brand_users(brand_id, environment_id, email);


