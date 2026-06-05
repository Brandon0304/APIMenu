CREATE TABLE menu.nutritional_info (
    id UUID PRIMARY KEY,
    menu_item_id UUID NOT NULL,
    calories INTEGER,
    protein_grams DECIMAL(7,2),
    carbs_grams DECIMAL(7,2),
    fat_grams DECIMAL(7,2),
    fiber_grams DECIMAL(7,2),
    sodium_mg INTEGER,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_nutritional_info_menu_item FOREIGN KEY (menu_item_id) REFERENCES menu.menu_items(id),
    CONSTRAINT uk_nutritional_info_menu_item UNIQUE (menu_item_id)
);
