CREATE TABLE menu.menu_item_modifier_groups (
    menu_item_id UUID NOT NULL,
    modifier_group_id UUID NOT NULL,
    PRIMARY KEY (menu_item_id, modifier_group_id),
    CONSTRAINT fk_mimg_menu_item FOREIGN KEY (menu_item_id) REFERENCES menu.menu_items(id),
    CONSTRAINT fk_mimg_modifier_group FOREIGN KEY (modifier_group_id) REFERENCES menu.modifier_groups(id)
);
