CREATE TABLE notifications
(
    id                UUID         NOT NULL,
    house_id          UUID         NOT NULL,
    recipient_user_id UUID         NOT NULL,
    actor_user_id     UUID         NOT NULL,
    type              VARCHAR(50)  NOT NULL,
    message           VARCHAR(500) NOT NULL,
    target_type       VARCHAR(30),
    target_id         UUID,
    read_at           TIMESTAMPTZ,
    created_at        TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_notifications PRIMARY KEY (id),
    CONSTRAINT fk_notifications_house FOREIGN KEY (house_id) REFERENCES houses (id) ON DELETE RESTRICT,
    CONSTRAINT fk_notifications_recipient FOREIGN KEY (recipient_user_id) REFERENCES users (id) ON DELETE RESTRICT,
    CONSTRAINT fk_notifications_actor FOREIGN KEY (actor_user_id) REFERENCES users (id) ON DELETE RESTRICT
);

CREATE INDEX idx_notifications_recipient_created_at
    ON notifications (recipient_user_id, created_at DESC);

CREATE INDEX idx_notifications_recipient_unread
    ON notifications (recipient_user_id, created_at DESC)
    WHERE read_at IS NULL;
