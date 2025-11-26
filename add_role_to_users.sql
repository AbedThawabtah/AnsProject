-- Migration script to add 'role' column to users table
-- NOTE: Your schema already has the 'role' column in the 'users' table!
-- This script is only needed if you're starting from scratch or the column is missing.

-- Check if role column exists first:
-- DESCRIBE users;

-- If the role column doesn't exist, run this:
-- ALTER TABLE users
-- ADD COLUMN role ENUM('admin', 'staff', 'student') NOT NULL DEFAULT 'student';

-- Verify the column exists:
-- DESCRIBE users;

