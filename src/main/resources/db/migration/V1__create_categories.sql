CREATE SCHEMA IF NOT EXISTS menu;

CREATE TABLE menu.categories (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    display_order INTEGER NOT NULL DEFAULT 0,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_categories_name UNIQUE (name)
);

CREATE INDEX idx_categories_active ON menu.categories (active);
