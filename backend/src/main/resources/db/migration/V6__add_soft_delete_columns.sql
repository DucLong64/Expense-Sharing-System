-- Soft delete support: deleted_at IS NULL means active record.

ALTER TABLE users
    ADD COLUMN deleted_at TIMESTAMPTZ;

ALTER TABLE houses
    ADD COLUMN deleted_at TIMESTAMPTZ;

ALTER TABLE house_members
    ADD COLUMN deleted_at TIMESTAMPTZ;

ALTER TABLE expenses
    ADD COLUMN deleted_at TIMESTAMPTZ;

ALTER TABLE expense_participants
    ADD COLUMN deleted_at TIMESTAMPTZ;

-- Allow re-registration / re-invite / re-participation after soft delete.
ALTER TABLE users
    DROP CONSTRAINT uq_users_email;

CREATE UNIQUE INDEX uq_users_email_active ON users (email) WHERE deleted_at IS NULL;

ALTER TABLE house_members
    DROP CONSTRAINT uq_house_members;

CREATE UNIQUE INDEX uq_house_members_active ON house_members (house_id, user_id) WHERE deleted_at IS NULL;

ALTER TABLE expense_participants
    DROP CONSTRAINT uq_expense_participants;

CREATE UNIQUE INDEX uq_expense_participants_active ON expense_participants (expense_id, user_id)
    WHERE deleted_at IS NULL;

CREATE INDEX idx_users_deleted_at ON users (deleted_at);
CREATE INDEX idx_houses_deleted_at ON houses (deleted_at);
CREATE INDEX idx_house_members_deleted_at ON house_members (deleted_at);
CREATE INDEX idx_expenses_deleted_at ON expenses (deleted_at);
CREATE INDEX idx_expense_participants_deleted_at ON expense_participants (deleted_at);
