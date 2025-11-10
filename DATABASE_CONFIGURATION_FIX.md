# Database Configuration Fix

## Problem Summary
The application was connecting to a PostgreSQL database but Hibernate was configured to use MySQL dialect. This caused syntax errors because:

1. **MySQL Syntax vs PostgreSQL Syntax:**
   - MySQL uses `auto_increment` for auto-incrementing primary keys
   - PostgreSQL uses `SERIAL`, `BIGSERIAL`, or `IDENTITY`
   
2. **MySQL vs PostgreSQL Table Options:**
   - MySQL uses `engine=InnoDB` clause
   - PostgreSQL doesn't support this syntax
   
3. **LOB (Large Object) Handling:**
   - PostgreSQL driver doesn't implement `createClob()` method
   - Needs `spring.jpa.properties.hibernate.jdbc.lob.non_contextual_creation=true`

## Error Messages You Were Seeing

```
java.sql.SQLFeatureNotSupportedException: Method org.postgresql.jdbc.PgConnection.createClob() is not yet implemented.

ERROR: syntax error at or near "auto_increment"
ERROR: syntax error at or near "engine"
ERROR: relation "tbl_categories" does not exist
```

## Solution Applied

### 1. Updated `application-prod.properties` (for PostgreSQL)

Added the following configuration:

```properties
#Postgresql Configuration
spring.datasource.url=jdbc:postgresql://dpg-d48vrm7diees73a78lh0-a.singapore-postgres.render.com/moneymanager_icub
spring.datasource.username=moneymanager_icub_user
spring.datasource.password=AsgA8GY3YQa0dIM3wWRCKJRdxLdd780F

# JPA Configuration for PostgreSQL
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.properties.hibernate.jdbc.lob.non_contextual_creation=true
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
```

### 2. Updated `application.properties` (for MySQL - local development)

Added LOB configuration:

```properties
# JPA Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
spring.jpa.properties.hibernate.jdbc.lob.non_contextual_creation=true
```

## How to Test

### For Production (PostgreSQL):

1. Make sure `spring.profiles.active=prod` is set in your main `application.properties`
2. Restart your application
3. The application should now:
   - Generate correct PostgreSQL DDL statements
   - Use `BIGSERIAL` instead of `auto_increment`
   - Not use `engine=InnoDB` syntax
   - Handle LOB objects correctly

### For Local Development (MySQL):

1. Remove or comment out `spring.profiles.active=prod`
2. Make sure MySQL is running locally
3. The application will use MySQL dialect for local database

## What Changed in Database Schema Generation

**Before (Incorrect - MySQL syntax for PostgreSQL):**
```sql
create table tbl_categories (
    id bigint not null auto_increment,
    ...
    primary key (id)
) engine=InnoDB
```

**After (Correct - PostgreSQL syntax):**
```sql
create table tbl_categories (
    id bigserial not null,
    ...
    primary key (id)
)
```

## Key Configuration Properties

| Property | MySQL Value | PostgreSQL Value |
|----------|-------------|------------------|
| `spring.jpa.properties.hibernate.dialect` | `org.hibernate.dialect.MySQL8Dialect` | `org.hibernate.dialect.PostgreSQLDialect` |
| `spring.jpa.database-platform` | Not required | `org.hibernate.dialect.PostgreSQLDialect` |
| `spring.jpa.properties.hibernate.jdbc.lob.non_contextual_creation` | `true` | `true` |

## Important Notes

1. **Always use the correct dialect for your database**
2. **The `spring.profiles.active` property determines which configuration file is used:**
   - No profile or default → uses `application.properties` (MySQL)
   - `prod` profile → uses `application-prod.properties` (PostgreSQL)

3. **For production deployment:**
   - Make sure to set environment variable: `SPRING_PROFILES_ACTIVE=prod`
   - Or add to application.properties: `spring.profiles.active=prod`

## Next Steps

1. Clean your project: `mvn clean`
2. Rebuild: `mvn clean install`
3. Restart your application
4. Verify the tables are created correctly in PostgreSQL

If tables were already created with wrong syntax, you may need to drop them first:

```sql
-- In PostgreSQL
DROP TABLE IF EXISTS tbl_incomes CASCADE;
DROP TABLE IF EXISTS tbl_expenses CASCADE;
DROP TABLE IF EXISTS tbl_categories CASCADE;
DROP TABLE IF EXISTS tbl_profiles CASCADE;
```

Then restart the application to let Hibernate recreate them with correct syntax.

## Verification

After restarting, you should see in the logs:

```
Hibernate: create table tbl_categories (id bigserial not null, ...)
Hibernate: create table tbl_expenses (id bigserial not null, ...)
Hibernate: create table tbl_incomes (id bigserial not null, ...)
Hibernate: create table tbl_profiles (id bigserial not null, ...)
```

**NO MORE ERRORS** about `auto_increment`, `engine=InnoDB`, or `createClob()`.

