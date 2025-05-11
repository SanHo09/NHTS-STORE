GO
ALTER TABLE users
    ADD status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE';
-- Add enum constraint
GO
ALTER TABLE users
    ADD CONSTRAINT chk_user_status CHECK (status IN ('NONE', 'ACTIVE', 'INACTIVE'));
GO
ALTER TABLE users
    ADD avatar VARCHAR(255);
