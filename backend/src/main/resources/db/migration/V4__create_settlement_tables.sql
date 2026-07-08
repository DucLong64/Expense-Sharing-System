CREATE TABLE settlements
(
    id           UUID           NOT NULL,
    house_id     UUID           NOT NULL,
    from_user_id UUID           NOT NULL,
    to_user_id   UUID           NOT NULL,
    amount       NUMERIC(15, 2) NOT NULL,
    note         VARCHAR(500),
    settled_at   TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    created_by   UUID           NOT NULL,
    CONSTRAINT pk_settlements PRIMARY KEY (id),
    CONSTRAINT fk_settlements_house FOREIGN KEY (house_id) REFERENCES houses (id) ON DELETE CASCADE,
    CONSTRAINT fk_settlements_from_user FOREIGN KEY (from_user_id) REFERENCES users (id) ON DELETE RESTRICT,
    CONSTRAINT fk_settlements_to_user FOREIGN KEY (to_user_id) REFERENCES users (id) ON DELETE RESTRICT,
    CONSTRAINT fk_settlements_created_by FOREIGN KEY (created_by) REFERENCES users (id) ON DELETE RESTRICT
);

CREATE INDEX idx_settlements_house_id ON settlements (house_id);
CREATE INDEX idx_settlements_settled_at ON settlements (settled_at DESC);
