CREATE TABLE expenses
(
    id           UUID           NOT NULL,
    house_id     UUID           NOT NULL,
    title        VARCHAR(200)   NOT NULL,
    description  VARCHAR(500),
    amount       NUMERIC(15, 2) NOT NULL,
    paid_by      UUID           NOT NULL,
    split_type   VARCHAR(20)    NOT NULL,
    expense_date DATE           NOT NULL,
    note         VARCHAR(500),
    created_by   UUID           NOT NULL,
    created_at   TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_expenses PRIMARY KEY (id),
    CONSTRAINT fk_expenses_house FOREIGN KEY (house_id) REFERENCES houses (id) ON DELETE CASCADE,
    CONSTRAINT fk_expenses_paid_by FOREIGN KEY (paid_by) REFERENCES users (id) ON DELETE RESTRICT,
    CONSTRAINT fk_expenses_created_by FOREIGN KEY (created_by) REFERENCES users (id) ON DELETE RESTRICT
);

CREATE TABLE expense_participants
(
    id               UUID           NOT NULL,
    expense_id       UUID           NOT NULL,
    user_id          UUID           NOT NULL,
    share_amount     NUMERIC(15, 2) NOT NULL,
    share_percentage NUMERIC(5, 2),
    CONSTRAINT pk_expense_participants PRIMARY KEY (id),
    CONSTRAINT uq_expense_participants UNIQUE (expense_id, user_id),
    CONSTRAINT fk_expense_participants_expense FOREIGN KEY (expense_id) REFERENCES expenses (id) ON DELETE CASCADE,
    CONSTRAINT fk_expense_participants_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE RESTRICT
);

CREATE INDEX idx_expenses_house_id ON expenses (house_id);
CREATE INDEX idx_expenses_expense_date ON expenses (expense_date DESC);
CREATE INDEX idx_expense_participants_expense_id ON expense_participants (expense_id);
