CREATE TABLE houses
(
    id          UUID         NOT NULL,
    name        VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    created_by  UUID         NOT NULL,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_houses PRIMARY KEY (id),
    CONSTRAINT fk_houses_created_by FOREIGN KEY (created_by) REFERENCES users (id) ON DELETE RESTRICT
);

CREATE TABLE house_members
(
    id        UUID        NOT NULL,
    house_id  UUID        NOT NULL,
    user_id   UUID        NOT NULL,
    role      VARCHAR(20) NOT NULL,
    joined_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_house_members PRIMARY KEY (id),
    CONSTRAINT uq_house_members UNIQUE (house_id, user_id),
    CONSTRAINT fk_house_members_house FOREIGN KEY (house_id) REFERENCES houses (id) ON DELETE CASCADE,
    CONSTRAINT fk_house_members_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE INDEX idx_house_members_house_id ON house_members (house_id);
CREATE INDEX idx_house_members_user_id ON house_members (user_id);
