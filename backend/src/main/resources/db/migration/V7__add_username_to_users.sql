ALTER TABLE users
    ADD COLUMN username VARCHAR(30);

UPDATE users
SET username = LEFT(
        COALESCE(
                NULLIF(REGEXP_REPLACE(LOWER(SPLIT_PART(email, '@', 1)), '[^a-z0-9_.]', '', 'g'), ''),
                'user'
        ),
        30
    )
WHERE username IS NULL;

UPDATE users
SET username = username || '_u'
WHERE LENGTH(username) < 3;

WITH numbered AS (
    SELECT id,
           username AS base_username,
           ROW_NUMBER() OVER (PARTITION BY username ORDER BY created_at) AS row_num
    FROM users
)
UPDATE users u
SET username = CASE
    WHEN n.row_num = 1 THEN LEFT(n.base_username, 30)
    ELSE LEFT(n.base_username, GREATEST(1, 30 - LENGTH(n.row_num::text) - 1)) || '_' || n.row_num::text
END
FROM numbered n
WHERE u.id = n.id;

ALTER TABLE users
    ALTER COLUMN username SET NOT NULL;

CREATE UNIQUE INDEX uq_users_username_active ON users (username) WHERE deleted_at IS NULL;
