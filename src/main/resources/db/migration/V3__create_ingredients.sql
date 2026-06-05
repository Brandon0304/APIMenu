CREATE TABLE menu.ingredients (
    id UUID PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    unit VARCHAR(50),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_ingredients_name UNIQUE (name)
);

CREATE INDEX idx_ingredients_active ON menu.ingredients (active);

CREATE TABLE menu.menu_item_ingredients (
    menu_item_id UUID NOT NULL,
    ingredient_id UUID NOT NULL,
    quantity DECIMAL(10,2) NOT NULL,
    PRIMARY KEY (menu_item_id, ingredient_id),
    CONSTRAINT fk_mii_menu_item FOREIGN KEY (menu_item_id) REFERENCES menu.menu_items(id),
    CONSTRAINT fk_mii_ingredient FOREIGN KEY (ingredient_id) REFERENCES menu.ingredients(id)
);
