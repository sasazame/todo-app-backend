-- Add user_id column to todos table for ownership
ALTER TABLE todos ADD COLUMN user_id BIGINT;

-- Add foreign key constraint to users table
ALTER TABLE todos ADD CONSTRAINT fk_todos_user_id 
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;

-- Create index for user-based todo queries
CREATE INDEX idx_todos_user_id ON todos(user_id);

-- Create composite index for user and status queries
CREATE INDEX idx_todos_user_status ON todos(user_id, status);

-- Update existing todos to have a default user (for migration purposes)
-- Note: In production, you might want to handle this differently
-- For now, we'll make the column nullable temporarily
ALTER TABLE todos ALTER COLUMN user_id DROP NOT NULL;