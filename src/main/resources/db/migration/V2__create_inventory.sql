CREATE TABLE inventory
(
    id          UUID PRIMARY KEY,
    base_id     UUID        NOT NULL,
    supply_type VARCHAR(50) NOT NULL,
    quantity    INTEGER     NOT NULL,

    CONSTRAINT fk_inventory_base
        FOREIGN KEY (base_id)
            REFERENCES bases (id)
);