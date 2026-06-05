CREATE TABLE menu.allergens (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    icon VARCHAR(50),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT uk_allergens_name UNIQUE (name)
);

CREATE INDEX idx_allergens_active ON menu.allergens (active);

CREATE TABLE menu.menu_item_allergens (
    menu_item_id UUID NOT NULL,
    allergen_id UUID NOT NULL,
    PRIMARY KEY (menu_item_id, allergen_id),
    CONSTRAINT fk_mia_menu_item FOREIGN KEY (menu_item_id) REFERENCES menu.menu_items(id),
    CONSTRAINT fk_mia_allergen FOREIGN KEY (allergen_id) REFERENCES menu.allergens(id)
);
