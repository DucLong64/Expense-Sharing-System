CREATE TABLE activity_logs
(
    id            UUID         NOT NULL,
    house_id      UUID,
    actor_user_id UUID         NOT NULL,
    type          VARCHAR(50)  NOT NULL,
    target_type   VARCHAR(30)  NOT NULL,
    target_id     UUID         NOT NULL,
    message       VARCHAR(500) NOT NULL,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_activity_logs PRIMARY KEY (id),
    CONSTRAINT fk_activity_logs_actor_user FOREIGN KEY (actor_user_id) REFERENCES users (id) ON DELETE RESTRICT
);

CREATE INDEX idx_activity_logs_house_id_created_at ON activity_logs (house_id, created_at DESC);
CREATE INDEX idx_activity_logs_actor_user_id_created_at ON activity_logs (actor_user_id, created_at DESC);
CREATE INDEX idx_activity_logs_type_created_at ON activity_logs (type, created_at DESC);
