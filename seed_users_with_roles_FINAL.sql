-- ============================================================================
-- Seed data: Insert test users with roles
-- ============================================================================
-- IMPORTANT: 
--   1. Your schema already has the 'users' table with 'role' column
--   2. Generate correct password hashes first (see instructions below)
--   3. Replace the placeholder hashes with actual SHA-256 hashes
--
-- TO GENERATE PASSWORD HASHES:
--   Option 1: Run PasswordHashHelper.java
--     javac src/main/java/org/example/demo/PasswordHashHelper.java
--     java -cp src/main/java org.example.demo.PasswordHashHelper
--
--   Option 2: Use MySQL
--     SELECT SHA2('admin123', 256);
--
--   Option 3: Online tool
--     https://emn178.github.io/online-tools/sha256.html
-- ============================================================================

-- Test users (plain text passwords: admin123, staff123, student123)
-- REPLACE THE HASHES BELOW with actual SHA-256 hashes generated using one of the methods above

-- Admin user (username: admin, password: admin123)
-- TODO: Replace 'YOUR_HASH_HERE' with actual SHA-256 hash of 'admin123'
INSERT INTO users (username, password, email, role) VALUES
('admin', 'YOUR_HASH_HERE', 'admin@library.com', 'admin');

-- Staff user (username: staff, password: staff123)
-- TODO: Replace 'YOUR_HASH_HERE' with actual SHA-256 hash of 'staff123'
INSERT INTO users (username, password, email, role) VALUES
('staff', 'YOUR_HASH_HERE', 'staff@library.com', 'staff');

-- Student user (username: student, password: student123)
-- TODO: Replace 'YOUR_HASH_HERE' with actual SHA-256 hash of 'student123'
INSERT INTO users (username, password, email, role) VALUES
('student', 'YOUR_HASH_HERE', 'student@library.com', 'student');

-- Verify the users were inserted
-- SELECT username, email, role FROM users;

