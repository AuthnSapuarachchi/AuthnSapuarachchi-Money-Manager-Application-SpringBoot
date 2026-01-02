# 💰 Smart Money Management App - Backend API

<div align="center">

![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.7-brightgreen?style=for-the-badge&logo=spring)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?style=for-the-badge&logo=mysql)
![JWT](https://img.shields.io/badge/JWT-Authentication-black?style=for-the-badge&logo=jsonwebtokens)
![Maven](https://img.shields.io/badge/Maven-Build-red?style=for-the-badge&logo=apachemaven)

A comprehensive Spring Boot RESTful API for managing personal finances with JWT authentication, email verification, and automated notifications.

[Features](#-features) • [Tech Stack](#-technology-stack) • [Getting Started](#-getting-started) • [API Documentation](#-api-documentation) • [Database Schema](#-database-schema) • [Deployment](#-deployment)

</div>

---

## 📋 Table of Contents

- [Features](#-features)
- [Technology Stack](#-technology-stack)
- [Architecture](#-architecture)
- [Getting Started](#-getting-started)
  - [Prerequisites](#prerequisites)
  - [Installation](#installation)
  - [Configuration](#configuration)
  - [Running the Application](#running-the-application)
- [API Documentation](#-api-documentation)
- [Database Schema](#-database-schema)
- [Security](#-security)
- [Email Notifications](#-email-notifications)
- [Error Handling](#-error-handling)
- [Testing](#-testing)
- [Deployment](#-deployment)
- [Contributing](#-contributing)

---

## ✨ Features

### 🔐 Authentication & Authorization

- ✅ User registration with email verification
- ✅ JWT token-based authentication
- ✅ Secure password encryption (BCrypt)
- ✅ Email activation system
- ✅ Protected API endpoints

### 💳 Financial Management

- ✅ Income tracking with categories
- ✅ Expense tracking with categories
- ✅ Custom category creation (income/expense)
- ✅ Dashboard analytics (balance, totals, recent transactions)
- ✅ Advanced filtering (date range, keyword, sorting)

### 📊 Reporting & Export

- ✅ Excel export for income/expense data
- ✅ Email reports with attachments
- ✅ Daily expense summaries
- ✅ Transaction history

### 🔔 Automated Notifications

- ✅ Daily income/expense reminders (10 PM IST)
- ✅ Daily expense summary emails (11 PM IST)
- ✅ Account activation emails

### 🛡️ Security & Validation

- ✅ Global exception handling
- ✅ Input validation (Bean Validation)
- ✅ CORS configuration
- ✅ SQL injection prevention
- ✅ XSS protection

---

## 🛠️ Technology Stack

### Core Technologies

- **Java**: 21 (LTS)
- **Spring Boot**: 3.5.7
- **Spring Security**: JWT-based authentication
- **Spring Data JPA**: Database operations
- **Spring Mail**: Email services
- **Hibernate**: ORM framework

### Database

- **MySQL**: 8.0+ (Primary)
- **PostgreSQL**: Supported for production

### Build & Tools

- **Maven**: 3.6+
- **Lombok**: Boilerplate reduction
- **Apache POI**: Excel generation (5.2.5)
- **JJWT**: JWT implementation (0.11.5)

### Email Service

- **Brevo (Sendinblue)**: SMTP provider

---

## 🏗️ Architecture

```
smartmoneymanageapp/
├── src/main/java/com/authcodelab/smartmoneymanageapp/
│   ├── config/                 # Security & Configuration
│   │   └── SecurityConfig.java
│   ├── controller/             # REST Controllers
│   │   ├── ProfileController.java
│   │   ├── CategoryController.java
│   │   ├── IncomeController.java
│   │   ├── ExpenseController.java
│   │   ├── DashboardController.java
│   │   ├── FilterController.java
│   │   ├── EmailController.java
│   │   └── ExcelController.java
│   ├── dto/                    # Data Transfer Objects
│   │   ├── AuthDTO.java
│   │   ├── ProfileDTO.java
│   │   ├── CategoryDTO.java
│   │   ├── IncomeDTO.java
│   │   ├── ExpenseDTO.java
│   │   ├── FilterDTO.java
│   │   └── RecentTransactionDTO.java
│   ├── entity/                 # JPA Entities
│   │   ├── ProfileEntity.java
│   │   ├── CategoryEntity.java
│   │   ├── IncomeEntity.java
│   │   └── ExpenseEntity.java
│   ├── repository/             # Data Access Layer
│   │   ├── ProfileRepository.java
│   │   ├── CategoryRepository.java
│   │   ├── IncomeRepository.java
│   │   └── ExpenseRepository.java
│   ├── service/                # Business Logic
│   │   ├── ProfileService.java
│   │   ├── CategoryService.java
│   │   ├── IncomeService.java
│   │   ├── ExpenseService.java
│   │   ├── DashboardService.java
│   │   ├── FilterService.java
│   │   ├── EmailService.java
│   │   ├── ExcelService.java
│   │   └── NotificationService.java
│   ├── security/               # Security Components
│   │   ├── JwtAuthenticationFilter.java
│   │   └── AppUserDetailsService.java
│   ├── util/                   # Utility Classes
│   │   └── JwtUtil.java
│   └── exception/              # Custom Exceptions
│       ├── DuplicateEmailException.java
│       ├── InvalidActivationTokenException.java
│       └── UserAccountNotActivatedException.java
└── src/main/resources/
    ├── application.properties
    └── application-prod.properties
```

---

## 🚀 Getting Started

### Prerequisites

Ensure you have the following installed:

```bash
✓ Java 21 (JDK)
✓ MySQL 8.0+
✓ Maven 3.6+
✓ Git
```

**Verify Installations:**

```bash
java -version    # Should show Java 21
mvn -version     # Should show Maven 3.6+
mysql --version  # Should show MySQL 8.0+
```

---

### Installation

1. **Clone the Repository**

```bash
git clone <repository-url>
cd smartmoneymanageapp
```

2. **Create MySQL Database**

```sql
CREATE DATABASE moneymanage_app;
```

3. **Configure Environment Variables**

Create a `.env` file or set system environment variables:

```properties
# Database
DB_URL=jdbc:mysql://localhost:3306/moneymanage_app
DB_USERNAME=root
DB_PASSWORD=your_password

# Email (Brevo SMTP)
BREVO_HOST=smtp-relay.brevo.com
BREVO_PORT=587
BREVO_USERNAME=your_brevo_username
BREVO_PASSWORD=your_brevo_api_key
BREVO_FROM_EMAIL=your_verified_email@example.com

# Application URLs
MONEY_MANAGER_FRONTEND_URL=http://localhost:5173
MONEY_MANAGER_BACKEND_URL=http://localhost:8081

# JWT Secret (Base64 encoded string)
JWT_SECRET=YXV0aG5tb25leW1hbmFnZWFwcGF1dGhubW9uZXltYW5hZ2VhcHBzZWNyZXRrZXlmb3JzZWN1cml0eQ==
```

4. **Update Application Properties**

Edit `src/main/resources/application.properties`:

```properties
# Database Configuration
spring.datasource.url=${DB_URL:jdbc:mysql://localhost:3306/moneymanage_app}
spring.datasource.username=${DB_USERNAME:root}
spring.datasource.password=${DB_PASSWORD:12345}

# JPA Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect

# Email Configuration
spring.mail.host=${BREVO_HOST}
spring.mail.port=${BREVO_PORT}
spring.mail.username=${BREVO_USERNAME}
spring.mail.password=${BREVO_PASSWORD}
spring.mail.properties.mail.smtp.from=${BREVO_FROM_EMAIL}

# JWT Configuration
jwt.secret=${JWT_SECRET}
jwt.expiration=36000000

# Server Configuration
server.port=8081
server.servlet.context-path=/api/v1.0

# Application URLs
money.manager.frontend.url=${MONEY_MANAGER_FRONTEND_URL}
app.activation.url=${MONEY_MANAGER_BACKEND_URL}
```

---

### Configuration

#### Email Service Setup (Brevo)

1. Sign up at [Brevo (Sendinblue)](https://www.brevo.com/)
2. Verify your sender email
3. Generate API key (Settings → SMTP & API → API Keys)
4. Update environment variables

#### JWT Secret Generation

Generate a secure JWT secret:

```bash
# Using OpenSSL
openssl rand -base64 64

# Using Java
echo -n "your-secret-key" | base64
```

---

### Running the Application

#### Development Mode

**Using Maven:**

```bash
./mvnw spring-boot:run
```

**Using Maven Wrapper (Windows):**

```bash
mvnw.cmd spring-boot:run
```

**Using IDE (IntelliJ IDEA/Eclipse):**

- Open project
- Run `SmartmoneymanageappApplication.java`

#### Production Mode

```bash
# Build JAR
./mvnw clean package -DskipTests

# Run JAR
java -jar target/smartmoneymanageapp-0.0.1-SNAPSHOT.jar
```

**Application will start at:** `http://localhost:8081/api/v1.0`

---

## 📚 API Documentation

### Base URL

```
http://localhost:8081/api/v1.0
```

### Authentication Endpoints

#### 1. Register User

```http
POST /register
Content-Type: application/json

{
  "fullName": "John Doe",
  "email": "john@example.com",
  "password": "password123",
  "profilePictureUrl": "https://example.com/photo.jpg"
}
```

**Response (200 OK):**

```json
{
  "status": "success",
  "message": "Registration successful! Please check your email to activate your account.",
  "user": {
    "id": 1,
    "fullName": "John Doe",
    "email": "john@example.com",
    "profilePictureUrl": "https://example.com/photo.jpg",
    "createdAt": "2026-01-02T10:30:00"
  }
}
```

#### 2. Activate Account

```http
GET /activate?token=<activation-token>
```

**Response (200 OK):**

```json
{
  "status": "success",
  "message": "Profile activated successfully."
}
```

#### 3. Login

```http
POST /login
Content-Type: application/json

{
  "email": "john@example.com",
  "password": "password123"
}
```

**Response (200 OK):**

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "user": {
    "id": 1,
    "fullName": "John Doe",
    "email": "john@example.com",
    "profilePictureUrl": "https://example.com/photo.jpg"
  }
}
```

---

### Protected Endpoints (Require JWT Token)

**Authorization Header:**

```
Authorization: Bearer <your-jwt-token>
```

### Category Management

#### Get All Categories

```http
GET /categories
```

#### Get Categories by Type

```http
GET /categories?type=income
GET /categories?type=expense
```

#### Create Category

```http
POST /categories
Content-Type: application/json

{
  "name": "Salary",
  "type": "income",
  "icon": "💰"
}
```

#### Update Category

```http
PUT /categories/{categoryId}
Content-Type: application/json

{
  "name": "Updated Category",
  "icon": "🎯"
}
```

---

### Income Management

#### Get All Incomes

```http
GET /incomes
```

#### Create Income

```http
POST /incomes
Content-Type: application/json

{
  "name": "Monthly Salary",
  "amount": 5000.00,
  "date": "2026-01-01",
  "categoryId": 1,
  "icon": "💵"
}
```

#### Delete Income

```http
DELETE /incomes/{incomeId}
```

#### Download Income Excel

```http
GET /incomes/excel/download
```

---

### Expense Management

#### Get All Expenses

```http
GET /expenses
```

#### Create Expense

```http
POST /expenses
Content-Type: application/json

{
  "name": "Grocery Shopping",
  "amount": 150.00,
  "date": "2026-01-02",
  "categoryId": 2,
  "icon": "🛒"
}
```

#### Delete Expense

```http
DELETE /expenses/{expenseId}
```

#### Download Expense Excel

```http
GET /expenses/excel/download
```

---

### Dashboard

#### Get Dashboard Data

```http
GET /dashboard
```

**Response:**

```json
{
  "totalBalance": 4850.00,
  "totalIncome": 5000.00,
  "totalExpense": 150.00,
  "recent5Incomes": [...],
  "recent5Expenses": [...],
  "recentTransactions": [...]
}
```

---

### Filter & Search

#### Filter Transactions

```http
POST /filters
Content-Type: application/json

{
  "type": "expense",
  "startDate": "2026-01-01",
  "endDate": "2026-01-31",
  "keyword": "grocery",
  "sortField": "date",
  "sortOrder": "desc"
}
```

---

### Email Reports

#### Send Income Report

```http
POST /email/income
Content-Type: application/json

{
  "recipient": "john@example.com",
  "incomes": [
    {
      "date": "2026-01-01",
      "source": "Salary",
      "amount": 5000.00,
      "description": "Monthly Salary"
    }
  ]
}
```

#### Send Expense Report

```http
POST /email/expense
Content-Type: application/json

{
  "recipient": "john@example.com",
  "expenses": [
    {
      "date": "2026-01-02",
      "source": "Groceries",
      "amount": 150.00,
      "description": "Weekly groceries"
    }
  ]
}
```

---

### Health Check

```http
GET /health
GET /status
```

**Response:**

```
"Smart Money Manage App is running!"
```

---

## 🗄️ Database Schema

### Tables

#### 1. tbl_profiles

| Column              | Type         | Constraints                 |
| ------------------- | ------------ | --------------------------- |
| id                  | BIGINT       | PRIMARY KEY, AUTO_INCREMENT |
| full_name           | VARCHAR(100) | NOT NULL                    |
| email               | VARCHAR(255) | UNIQUE, NOT NULL            |
| password            | VARCHAR(255) | NOT NULL (Encrypted)        |
| profile_picture_url | VARCHAR(255) | NULL                        |
| is_active           | BOOLEAN      | DEFAULT FALSE               |
| activation_token    | VARCHAR(255) | NULL                        |
| created_at          | TIMESTAMP    | NOT NULL                    |
| updated_at          | TIMESTAMP    | NOT NULL                    |

#### 2. tbl_categories

| Column     | Type         | Constraints                    |
| ---------- | ------------ | ------------------------------ |
| id         | BIGINT       | PRIMARY KEY, AUTO_INCREMENT    |
| name       | VARCHAR(100) | NOT NULL                       |
| type       | VARCHAR(20)  | NOT NULL (income/expense)      |
| icon       | VARCHAR(50)  | NULL                           |
| profile_id | BIGINT       | FOREIGN KEY → tbl_profiles(id) |
| created_at | TIMESTAMP    | NOT NULL                       |
| updated_at | TIMESTAMP    | NOT NULL                       |

#### 3. tbl_incomes

| Column      | Type          | Constraints                      |
| ----------- | ------------- | -------------------------------- |
| id          | BIGINT        | PRIMARY KEY, AUTO_INCREMENT      |
| name        | VARCHAR(255)  | NOT NULL                         |
| amount      | DECIMAL(19,2) | NOT NULL                         |
| date        | DATE          | NOT NULL                         |
| icon        | VARCHAR(50)   | NULL                             |
| category_id | BIGINT        | FOREIGN KEY → tbl_categories(id) |
| profile_id  | BIGINT        | FOREIGN KEY → tbl_profiles(id)   |
| created_at  | TIMESTAMP     | NOT NULL                         |
| updated_at  | TIMESTAMP     | NOT NULL                         |

#### 4. tbl_expenses

| Column      | Type          | Constraints                      |
| ----------- | ------------- | -------------------------------- |
| id          | BIGINT        | PRIMARY KEY, AUTO_INCREMENT      |
| name        | VARCHAR(255)  | NOT NULL                         |
| amount      | DECIMAL(19,2) | NOT NULL                         |
| date        | DATE          | NOT NULL                         |
| icon        | VARCHAR(50)   | NULL                             |
| category_id | BIGINT        | FOREIGN KEY → tbl_categories(id) |
| profile_id  | BIGINT        | FOREIGN KEY → tbl_profiles(id)   |
| created_at  | TIMESTAMP     | NOT NULL                         |
| updated_at  | TIMESTAMP     | NOT NULL                         |

### Entity Relationships

```
ProfileEntity (1) ──< (M) CategoryEntity
ProfileEntity (1) ──< (M) IncomeEntity
ProfileEntity (1) ──< (M) ExpenseEntity
CategoryEntity (1) ──< (M) IncomeEntity
CategoryEntity (1) ──< (M) ExpenseEntity
```

---

## 🔐 Security

### JWT Authentication Flow

1. **User registers** → Account created with `isActive = false`
2. **Email sent** → Activation link with UUID token
3. **User clicks link** → Account activated (`isActive = true`)
4. **User logs in** → JWT token generated
5. **Protected requests** → JWT token validated

### Security Features

- **Password Encryption**: BCrypt hashing (strength: 12)
- **JWT Expiration**: 10 hours (configurable)
- **CORS**: Configured for frontend origin
- **XSS Protection**: Input validation & sanitization
- **SQL Injection Prevention**: JPA prepared statements

### JWT Token Structure

```json
{
  "sub": "user@example.com",
  "iat": 1735812000,
  "exp": 1735848000
}
```

---

## 📧 Email Notifications

### Scheduled Jobs (Spring @Scheduled)

#### 1. Daily Reminder (10:00 PM IST)

- Sends reminder to log income/expense
- Recipient: All active users

#### 2. Daily Expense Summary (11:00 PM IST)

- Sends HTML email with expense table
- Recipient: Users with expenses today
- Includes: Transaction details, total amount

### Email Templates

**Activation Email:**

```
Subject: Activate your Smart Money Manage App account

Dear {fullName},

Please activate your account by clicking the link below:
{activationLink}

Best regards,
Smart Money Manage App Team
```

**Daily Reminder:**

```
Subject: Daily Income/Expense Reminder

Hi {fullName},

This is a friendly reminder to log your daily income and expenses...
```

---

## ⚠️ Error Handling

### Global Exception Handler

All exceptions return consistent JSON responses:

```json
{
  "timestamp": "2026-01-02T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Email address is already registered",
  "path": "/api/v1.0/register"
}
```

### Custom Exceptions

- `DuplicateEmailException`: Email already exists
- `InvalidActivationTokenException`: Invalid/expired token
- `UserAccountNotActivatedException`: Login attempt before activation
- `UsernameNotFoundException`: User not found

---

## 🧪 Testing

### Run Tests

```bash
# Run all tests
./mvnw test

# Run with coverage
./mvnw test jacoco:report

# Skip tests during build
./mvnw clean package -DskipTests
```

### Test Structure

```
src/test/java/com/authcodelab/smartmoneymanageapp/
├── controller/     # Controller tests
├── service/        # Service layer tests
├── repository/     # Repository tests
└── integration/    # Integration tests
```

---

## 🚀 Deployment

### Docker Deployment

**Dockerfile:**

```dockerfile
FROM openjdk:21-jdk-slim
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**Build & Run:**

```bash
docker build -t money-manager-backend .
docker run -p 8081:8081 -e DB_URL=... -e BREVO_HOST=... money-manager-backend
```

### Production Configuration

Use `application-prod.properties`:

```properties
# Set active profile
spring.profiles.active=prod

# Production database
spring.datasource.url=${PROD_DB_URL}

# Disable SQL logging
spring.jpa.show-sql=false

# Production email config
spring.mail.host=${PROD_SMTP_HOST}
```

### Environment Variables (Production)

```bash
export DB_URL=jdbc:mysql://prod-server:3306/moneymanage_app
export DB_USERNAME=prod_user
export DB_PASSWORD=secure_password
export BREVO_HOST=smtp-relay.brevo.com
export BREVO_PORT=587
export BREVO_USERNAME=prod_username
export BREVO_PASSWORD=prod_api_key
export BREVO_FROM_EMAIL=noreply@yourdomain.com
export JWT_SECRET=your_production_secret
export MONEY_MANAGER_FRONTEND_URL=https://yourdomain.com
export MONEY_MANAGER_BACKEND_URL=https://api.yourdomain.com
```

---

## 📊 Performance Tips

1. **Database Indexing**: Add indexes on frequently queried columns

```sql
CREATE INDEX idx_profile_email ON tbl_profiles(email);
CREATE INDEX idx_income_date ON tbl_incomes(date);
CREATE INDEX idx_expense_date ON tbl_expenses(date);
```

2. **Connection Pooling**: Configure HikariCP

```properties
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=5
```

3. **Caching**: Enable Spring Cache for dashboard data

---

## 🤝 Contributing

1. Fork the repository
2. Create feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'Add AmazingFeature'`)
4. Push to branch (`git push origin feature/AmazingFeature`)
5. Open Pull Request

---

## 📝 License

This project is licensed under the MIT License.

---

## 👨‍💻 Author

**Your Name**

- GitHub: [@yourusername](https://github.com/yourusername)
- Email: your.email@example.com

---

## 🙏 Acknowledgments

- Spring Boot Team
- Brevo (Sendinblue) Email Service
- All contributors and testers

---

## 📞 Support

For issues, questions, or suggestions:

- Open an issue on GitHub
- Email: support@yourdomain.com

---

<div align="center">

**⭐ Star this repository if you find it helpful!**

Made with ❤️ using Spring Boot

</div>

### Public Endpoints

#### Health Check

```http
GET /api/v1.0/health
GET /api/v1.0/status
```

**Response:**

```json
"Smart Money Manage App is running!"
```

#### User Registration

```http
POST /api/v1.0/register
Content-Type: application/json

{
    "fullName": "John Doe",
    "email": "john.doe@example.com",
    "password": "securePassword123",
    "profilePictureUrl": "https://example.com/profile.jpg"
}
```

**Response:**

```json
{
  "status": "success",
  "message": "Registration successful! Please check your email to activate your account.",
  "user": {
    "id": 1,
    "fullName": "John Doe",
    "email": "john.doe@example.com",
    "profilePictureUrl": "https://example.com/profile.jpg",
    "createdAt": "2025-11-09T10:30:00",
    "updatedAt": "2025-11-09T10:30:00"
  }
}
```

#### Account Activation

```http
GET /api/v1.0/activate?token={activationToken}
```

**Response:**

```json
{
  "status": "success",
  "message": "Profile activated successfully."
}
```

#### User Login

```http
POST /api/v1.0/login
Content-Type: application/json

{
    "email": "john.doe@example.com",
    "password": "securePassword123"
}
```

**Response:**

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer",
  "user": {
    "id": 1,
    "fullName": "John Doe",
    "profilePictureUrl": "https://example.com/profile.jpg",
    "createdAt": "2025-11-09T10:30:00",
    "updatedAt": "2025-11-09T10:30:00"
  }
}
```

### Protected Endpoints

All protected endpoints require the JWT token in the Authorization header:

```http
Authorization: Bearer {your-jwt-token}
```

#### Get Current User Profile

```http
GET /api/v1.0/profile
Authorization: Bearer {your-jwt-token}
```

**Response:**

```json
{
  "status": "success",
  "user": {
    "id": 1,
    "fullName": "John Doe",
    "profilePictureUrl": "https://example.com/profile.jpg",
    "createdAt": "2025-11-09T10:30:00",
    "updatedAt": "2025-11-09T10:30:00"
  }
}
```

#### Test Authentication

```http
GET /api/v1.0/test
Authorization: Bearer {your-jwt-token}
```

**Response:**

```json
"Test Successful"
```

## Error Handling

The application includes comprehensive error handling with standardized error responses:

### Validation Errors

```json
{
  "status": "error",
  "message": "Validation failed",
  "errors": {
    "email": "Please provide a valid email address",
    "password": "Password must be at least 6 characters long"
  }
}
```

### Authentication Errors

```json
{
  "status": "error",
  "message": "Invalid email or password",
  "error_code": "INVALID_CREDENTIALS"
}
```

### Account Not Activated

```json
{
  "status": "error",
  "message": "Account is not activated. Please check your email and activate your account before logging in.",
  "error_code": "ACCOUNT_NOT_ACTIVATED"
}
```

### JWT Token Expired

```json
{
  "status": "error",
  "message": "JWT token has expired",
  "error_code": "TOKEN_EXPIRED"
}
```

### Duplicate Email

```json
{
  "status": "error",
  "message": "Email address is already registered: john.doe@example.com",
  "error_code": "DUPLICATE_EMAIL"
}
```

## Validation Rules

### Registration (ProfileDTO)

- **fullName**: Required, 2-100 characters
- **email**: Required, valid email format
- **password**: Required, 6-100 characters
- **profilePictureUrl**: Optional, max 255 characters

### Login (AuthDTO)

- **email**: Required, valid email format
- **password**: Required, min 6 characters

## Security Features

- **Password Encryption**: BCrypt hashing
- **JWT Authentication**: Stateless authentication with configurable expiration
- **CORS**: Configured for cross-origin requests
- **Input Validation**: Bean validation with custom error messages
- **Exception Handling**: Global exception handler for consistent error responses

## Configuration

### JWT Settings

```properties
# JWT Configuration
jwt.secret=YXV0aG5tb25leW1hbmFnZWFwcGF1dGhubW9uZXltYW5hZ2VhcHBzZWNyZXRrZXlmb3JzZWN1cml0eQ==
jwt.expiration=36000000  # 10 hours in milliseconds
```

## Database Schema

### Profile Entity

```sql
CREATE TABLE tbl_profiles (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    profile_picture_url VARCHAR(255),
    is_active BOOLEAN DEFAULT FALSE,
    activation_token VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

## Future Enhancements

- Password reset functionality
- User profile update endpoint
- Financial transaction management
- Budget tracking
- Expense categorization
- Financial reports and analytics
- Notification preferences
- Two-factor authentication

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the MIT License.
#   A u t h n S a p u a r a c h c h i - M o n e y - M a n a g e r - A p p l i c a t i o n - S p r i n g B o o t 
 
 
