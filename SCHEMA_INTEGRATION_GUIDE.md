# Database Schema Integration Guide

## Overview
This guide explains how to integrate your MySQL database schema with the JavaFX Library Management System.

## Current Status

### ✅ Already Implemented
- **User Model**: Already has `role` field (String)
- **UserDAO**: Already reads/writes `role` column
- **Login Logic**: Already loads and stores role in SessionManager
- **UI Access Control**: Already uses role to enable/disable buttons (Admin can edit, Staff/Student view-only)

### 📋 What Needs to Be Done

1. **Provide your database schema file** (`.sql`)
2. **Update table/column names** in Java code to match your schema
3. **Run migration scripts** to add role column (if not already present)
4. **Insert seed data** with test users

---

## Step 1: Review Your Schema

Once you provide your `.sql` schema file, I will:
- Read all table names and column names
- Update all SQL queries in the Java code to match exactly
- Update the database connection URL if the database name differs

### Current Table Names Used in Code:
- `Users` (or `users`, `user`, `User`)
- `Author`
- `Book`
- `Borrower`
- `BorrowerType`
- `Loan`
- `LoanPeriod`
- `Publisher`
- `Sale`

### Current Column Names Used:
**Users table:**
- `username` (VARCHAR, PRIMARY KEY)
- `password` (VARCHAR(64), SHA-256 hashed)
- `email` (VARCHAR(100))
- `role` (ENUM('admin','staff','student'))

**Author table:**
- `author_id` (INT, PRIMARY KEY)
- `first_name` (VARCHAR)
- `last_name` (VARCHAR)
- `country` (VARCHAR)
- `bio` (VARCHAR/TEXT)

**Book table:**
- `book_id` (INT, PRIMARY KEY)
- `title` (VARCHAR)
- `publisher_id` (INT, FOREIGN KEY)
- `category` (VARCHAR)
- `book_type` (VARCHAR)
- `original_price` (DOUBLE/DECIMAL)
- `available` (INT)

**Borrower table:**
- `borrower_id` (INT, PRIMARY KEY)
- `first_name` (VARCHAR)
- `last_name` (VARCHAR)
- `type_id` (INT, FOREIGN KEY)
- `contact_info` (VARCHAR)

---

## Step 2: Migration Scripts

### File: `add_role_to_users.sql`
This script adds the `role` column to your users table.

**IMPORTANT:** Adjust the table name (`users`, `user`, `User`, or `Users`) to match your schema.

```sql
ALTER TABLE users
ADD COLUMN role ENUM('admin', 'staff', 'student') NOT NULL DEFAULT 'student';
```

### File: `seed_users_with_roles.sql`
This script inserts test users with different roles.

**IMPORTANT:** 
1. First run `add_role_to_users.sql`
2. Adjust table name if needed
3. Generate correct password hashes using `PasswordHashHelper.java`

---

## Step 3: Generate Password Hashes

### Option A: Use Java Helper (Recommended)
```bash
# Compile
javac src/main/java/org/example/demo/PasswordHashHelper.java

# Run
java -cp src/main/java org.example.demo.PasswordHashHelper
```

This will output:
- Password: admin123
- Hash: [64-character SHA-256 hash]
- SQL INSERT statement ready to use

### Option B: Use MySQL SHA2 Function
```sql
SELECT SHA2('admin123', 256) AS hash;
```

### Option C: Online Tool
Visit: https://emn178.github.io/online-tools/sha256.html

---

## Step 4: Update Database Connection

**File:** `src/main/java/org/example/demo/HelloController.java`

Current connection:
```java
private static final String URL = "jdbc:mysql://localhost:3306/libr";
private static final String USER = "root"; // TODO: Update if needed
private static final String PASSWORD = ""; // TODO: Update if needed
```

**Update:**
- Change `libr` to your actual database name
- Update `USER` and `PASSWORD` if different

---

## Step 5: Run Migrations

### In phpMyAdmin:
1. Select your database
2. Click "SQL" tab
3. Copy and paste `add_role_to_users.sql`
4. Adjust table name if needed
5. Click "Go"
6. Repeat for `seed_users_with_roles.sql` (after generating correct hashes)

### In MySQL Workbench:
1. Connect to your database
2. Open a new SQL tab
3. Copy and paste `add_role_to_users.sql`
4. Execute (Ctrl+Enter)
5. Repeat for `seed_users_with_roles.sql`

---

## Step 6: Test the Application

1. **Compile and run:**
   ```bash
   mvn clean compile
   mvn javafx:run
   ```

2. **Login with test users:**
   - Admin: `admin` / `admin123` (can add/edit/delete)
   - Staff: `staff` / `staff123` (view-only)
   - Student: `student` / `student123` (view-only)

3. **Verify role-based access:**
   - Login as admin → Should see Add/Update/Delete buttons enabled
   - Login as staff/student → Should see Add/Update/Delete buttons disabled

---

## Files That Will Be Updated (After Schema Review)

Once you provide your schema file, I will update:

1. **HelloController.java** - Database connection URL
2. **UserDAO.java** - Table name and column names
3. **AuthorDAO.java** - Table and column names
4. **BookDAO.java** - Table and column names
5. **BorrowerDAO.java** - Table and column names
6. **DataCollector.java** - All SELECT queries
7. **ReportsView.java** - All SQL queries in reports
8. **Model classes** - If column names differ (Author.java, Book.java, etc.)

---

## Current User Model (Already Has Role)

**File:** `src/main/java/org/example/demo/User.java`

```java
public class User {
    private StringProperty username;
    private StringProperty password; // hashed
    private StringProperty email;
    private StringProperty role; // 'admin', 'staff', 'student'
    
    // Constructor, getters, setters all include role
}
```

---

## Current UserDAO (Already Reads Role)

**File:** `src/main/java/org/example/demo/UserDAO.java`

The `authenticateUser()` method already:
- Reads `role` from ResultSet
- Creates User object with role
- Returns User with role included

```java
return new User(
    rs.getString("username"),
    rs.getString("password"),
    rs.getString("email"),
    rs.getString("role")  // ✅ Already reading role
);
```

---

## Current UI Integration (Already Uses Role)

**File:** `src/main/java/org/example/demo/LibraryApp.java`

Role-based access is already implemented:
- `SessionManager.canEdit()` returns `true` only for admin
- `GenericFormBuilder` disables buttons if `canEdit == false`

**File:** `src/main/java/org/example/demo/GenericFormBuilder.java`

```java
// Disable buttons if user doesn't have edit permissions
if (!canEdit) {
    btnAdd.setDisable(true);
    btnUpdate.setDisable(true);
    btnDelete.setDisable(true);
}
```

---

## Next Steps

1. **Upload your `.sql` schema file**
2. I will:
   - Read and analyze the schema
   - Update all table/column names in Java code
   - Adjust migration scripts if needed
   - Provide final updated code

---

## Questions?

If your schema uses different naming conventions:
- Table names: `authors` vs `Author` vs `AUTHOR`
- Column names: `first_name` vs `firstName` vs `FirstName`
- Database name: `library` vs `libr` vs `library_db`

Just provide the schema file and I'll update everything to match exactly!

