CREATE TABLE menu.modifier_groups (
    id UUID PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    required BOOLEAN NOT NULL DEFAULT FALSE,
    max_selections INTEGER NOT NULL DEFAULT 1,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_modifier_groups_active ON menu.modifier_groups (active);

CREATE TABLE menu.modifier_options (
    id UUID PRIMARY KEY,
    modifier_group_id UUID NOT NULL,
    name VARCHAR(200) NOT NULL,
    price_adjustment DECIMAL(10,2) NOT NULL DEFAULT 0,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_modifier_options_group FOREIGN KEY (modifier_group_id) REFERENCES menu.modifier_groups(id)
);

CREATE INDEX idx_modifier_options_group ON menu.modifier_options (modifier_group_id);
CREATE INDEX idx_modifier_options_active ON menu.modifier_options (active);
