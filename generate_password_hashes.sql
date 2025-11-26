-- INSTRUCTIONS: This file shows how to generate password hashes
-- 
-- OPTION 1: Use the Java helper (Recommended)
--   1. Compile: javac src/main/java/org/example/demo/PasswordHashHelper.java
--   2. Run: java -cp src/main/java org.example.demo.PasswordHashHelper
--   3. Copy the generated hashes into seed_users_with_roles.sql
--
-- OPTION 2: Use MySQL SHA2 function (if available)
--   SELECT SHA2('admin123', 256) AS hash;
--   This will give you the SHA-256 hash
--
-- OPTION 3: Use online SHA-256 generator
--   Visit: https://emn178.github.io/online-tools/sha256.html
--   Enter password, copy hash

-- Example: Generate hash for 'admin123'
-- SELECT SHA2('admin123', 256);

