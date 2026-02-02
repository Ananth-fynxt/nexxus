CREATE TABLE users (
    id SERIAL PRIMARY KEY NOT NULL,
    email TEXT NOT NULL UNIQUE,
    password TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    deleted_at TIMESTAMP NULL,
    created_by INTEGER NOT NULL,
    updated_by INTEGER NOT NULL,
    deleted_by INTEGER NULL
);

CREATE TABLE fi (
    id SMALLSERIAL PRIMARY KEY NOT NULL,
    name TEXT NOT NULL,
    email TEXT NOT NULL,
    user_id INTEGER NOT NULL REFERENCES users(id),
    scope scope NOT NULL DEFAULT 'FI',
    status user_status NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    deleted_at TIMESTAMP NULL,
    created_by INTEGER NOT NULL,
    updated_by INTEGER NOT NULL,
    deleted_by INTEGER NULL
);

CREATE TABLE brands (
    id UUID PRIMARY KEY NOT NULL,
    fi_id SMALLINT REFERENCES fi(id),
    name TEXT NOT NULL,
    email TEXT NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    deleted_at TIMESTAMP NULL,
    created_by INTEGER NOT NULL,
    updated_by INTEGER NOT NULL,
    deleted_by INTEGER NULL
);

CREATE TABLE environments (
    id UUID PRIMARY KEY NOT NULL,
    name TEXT NOT NULL,
    secret UUID NOT NULL UNIQUE,
    token UUID NOT NULL UNIQUE,
    origin TEXT,
    success_redirect_url TEXT,
    failure_redirect_url TEXT,
    brand_id UUID NOT NULL REFERENCES brands(id),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    deleted_at TIMESTAMP NULL,
    created_by INTEGER NOT NULL,
    updated_by INTEGER NOT NULL,
    deleted_by INTEGER NULL
);

CREATE INDEX idx_fi_email ON fi(email);
CREATE UNIQUE INDEX idx_fi_name_email ON fi(name, email);

CREATE INDEX idx_brands_fi_id ON brands(fi_id);
CREATE UNIQUE INDEX idx_brands_fi_name ON brands(COALESCE(fi_id::text, ''), name);

CREATE INDEX idx_environments_brand_id ON environments(brand_id);