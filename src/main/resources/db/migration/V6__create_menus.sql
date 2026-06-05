CREATE TABLE menu.menus (
    id UUID PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    start_date DATE,
    end_date DATE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_menus_active ON menu.menus (active);

CREATE TABLE menu.menu_sections (
    id UUID PRIMARY KEY,
    menu_id UUID NOT NULL,
    category_id UUID NOT NULL,
    display_order INTEGER NOT NULL DEFAULT 0,
    CONSTRAINT fk_menu_sections_menu FOREIGN KEY (menu_id) REFERENCES menu.menus(id),
    CONSTRAINT fk_menu_sections_category FOREIGN KEY (category_id) REFERENCES menu.categories(id),
    CONSTRAINT uk_menu_sections_menu_category UNIQUE (menu_id, category_id)
);

CREATE INDEX idx_menu_sections_menu ON menu.menu_sections (menu_id);
