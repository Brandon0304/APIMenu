CREATE TABLE menu.menu_items (
    id UUID PRIMARY KEY,
    category_id UUID NOT NULL,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    image_url VARCHAR(500),
    preparation_time_minutes INTEGER,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_menu_items_category FOREIGN KEY (category_id) REFERENCES menu.categories(id),
    CONSTRAINT uk_menu_items_category_name UNIQUE (category_id, name)
);

CREATE INDEX idx_menu_items_category ON menu.menu_items (category_id);
CREATE INDEX idx_menu_items_active ON menu.menu_items (active);
