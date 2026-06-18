# Money Transfer & Reward Redemption System

A production-grade digital banking microservice and client application built progressively to demonstrate enterprise development best practices, secure transaction handling, and gamified customer engagement.

---

## 🎯 Project Overview

*   **Project Type**: Enterprise Banking Microservice & Angular client
*   **Approach**: Training-Aligned Progressive Build (Modules 1 - 5)
*   **Primary Focus**: Secure transactional integrity, concurrency management, and dynamic loyalty reward processing.

---

## 🏗️ System Architecture

The application is structured as a decoupled three-tier system:

```
                      ┌───────────────────────────────────────────┐
                      │             Angular Frontend              │
                      │           (Signals, Tailwind)             │
                      └───────────────────────────────────────────┘
                                            │
                                  HTTP / JWT / Idempotency
                                            ▼
                      ┌───────────────────────────────────────────┐
                      │           Spring Boot REST API            │
                      │    (Security, AOP, Validation, JPA)       │
                      └───────────────────────────────────────────┘
                                            │
                                    JDBC (MySQL 8)
                                            ▼
                      ┌───────────────────────────────────────────┐
                      │              MySQL Database               │
                      │     (ACID, Locks, Constraints, Ledgers)   │
                      └───────────────────────────────────────────┘
```

---

## 🛠️ Technology Stack

| Category | Technology | Version | Description |
| :--- | :--- | :--- | :--- |
| **Language** | Java | 17 LTS | Core backend programming language |
| **Framework** | Spring Boot | 3.x | Bootstraps REST APIs, dependency injection, and JPA |
| **Frontend** | Angular | 18+ | Standalone components, reactive Signals, routing, and guards |
| **Database** | MySQL | 8.x | Relational storage for accounts, transfers, rewards, and coupons |
| **Security** | Spring Security | 6.x | JWT-based stateless session authentication |
| **AOP** | AspectJ | 3.x | Cross-cutting concerns like logging and performance profiling |
| **Testing** | JUnit / Mockito | 5.x / 5.x | Unit tests for business logic, services, and repository layers |

---

## 📂 Project Structure

```
money-transfer-app/
├── backend/                  # Spring Boot application
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/         # Package controllers, services, entities, repositories
│   │   │   └── resources/    # application.properties & database initialization config
│   │   └── test/             # Service unit tests (Mocking controllers, repositories)
│   └── pom.xml               # Maven configuration
├── frontend/                 # Client UI application
│   └── money-transfer-ui/    # Angular 18 project
│       ├── src/app/
│       │   ├── core/         # Services (HTTP calls, storage) and guards (auth)
│       │   └── features/     # Signup, Login, Dashboard, Transfer, History, Rewards, Redemption
│       └── package.json      # NPM scripts and dependencies
├── database/                 # MySQL Database scripts
│   ├── schema.sql            # Table definitions (Accounts, Logs, Grants, Coupons)
│   └── seed-data.sql         # Dummy user accounts with seeded balances
├── snowflake/                # Analytics configurations (Warehouse pipelines)
└── README.md                 # Project README (Updated)
```

---

## 🌿 Branching Strategy

| Branch | Purpose |
| :--- | :--- |
| `main` | Production-ready stable release |
| `develop` | Integration branch for feature development |
| `feature/domain-models` | Advanced Java Domain & entity definitions |
| `feature/spring-boot-api` | REST API, controllers, and exception handler mapping |
| `feature/angular-ui` | Angular UI, dashboard routing, signals integration |
| `feature/snowflake-analytics` | Snowflake data pipelines and warehousing scripts |

---

## 📊 Core Features & System Modules

### 1. Account & Security Management (OLD)
*   **JWT Stateless Authentication**: Secure user login and registration endpoints (`/auth/login`, `/auth/signup`).
*   **One-Time Password (OTP)**: Email OTP verification integration (`/auth/verify-otp`) to activate accounts.
*   **Account Profiles**: Fetches account details, real-time balances, and transaction logs securely.

### 2. Transactional Money Transfer (OLD)
*   **Fund Execution**: Executes debit and credit between accounts atomically inside transactional boundaries.
*   **Audit Trail**: Logs all transaction attempts (`PENDING`, `SUCCESS`, `FAILURE`) to the database.

### 3. Gamified Reward Points & Redemption (NEW)
*   **Reward Grants Engine**: Grants **1 reward point for every ₹100 transferred** on successful transactions (transfers must be greater than ₹100, and cannot be a self-transfer).
*   **Dynamic Balance Calculation**: Subtracts total points spent from total points granted on-the-fly to guarantee ledger consistency.
*   **Partner Merchant Coupons**: Users redeem points for 5% (5 points) or 10% (10 points) coupons from whitelisted merchants: Myntra, Domino's, and La Pino's.
*   **Coupon Code Generator**: Cryptographically generates randomized 6-character uppercase codes (e.g., `M3L9K2`) with collision safety loops.
*   **History & Clipboard Integration**: Users can view all active and used coupons, check expiry dates, and copy codes to the clipboard.

---

## 🛡️ Built-in System Guardrails

The application implements strict enterprise-grade security and reliability guardrails:

1.  **Race Condition Protection (Optimistic Locking)**:
    The `accounts` table contains a `@Version` column. Any concurrent update to the same account balance fails safely, preventing double-spend attacks.
2.  **Transaction Boundary Control**:
    All database mutations for transfers and rewards are executed inside Spring `@Transactional` blocks, guaranteeing ACID compliance.
3.  **Idempotency Checks**:
    Ensures duplicate transfer calls with the same `X-Idempotency-Key` are rejected (conflict code TRX-409), avoiding double-charges.
4.  **Ownership Mapping**:
    Before initiating a transfer, the system matches the source account holder's name against the username extracted from the JWT token.
5.  **AOP Logging & Masking**:
    Logs are correlation-tracked using Logback MDC (`correlationId`). The `LoggingAspect` intercepts and automatically masks sensitive parameters (passwords, pins, secrets, and authorization tokens) prior to logging.
6.  **Input/Business Validations**:
    Rejects self-transfers, negative amounts, transfers to/from locked or closed accounts, and coupon redemptions exceeding the user's current points.

---

## 🚀 Getting Started

### Prerequisites
*   **Java**: JDK 17+ installed and configured
*   **Node.js**: Node 18+ (for Angular frontend)
*   **MySQL**: Running local instance of MySQL Server 8.0+
*   **Maven**: Build tool for Java

### Database Setup
1. Log into your MySQL database server.
2. Create the target schema:
   ```sql
   CREATE DATABASE money_transfer;
   ```
3. Run the schema and seed scripts located in the `/database` directory:
   ```bash
   mysql -u root -p money_transfer < database/schema.sql
   mysql -u root -p money_transfer < database/seed-data.sql
   ```

### Backend Startup
1. Open [application.properties](file:///c:/Users/user/Desktop/money-transfer-app/backend/src/main/resources/application.properties) and update the MySQL username and password to match your setup.
2. Navigate to the backend directory and run:
   ```bash
   mvn clean spring-boot:run
   ```
3. The API will start on `http://localhost:8080/`.

### Frontend Startup
1. Navigate to the frontend UI folder:
   ```bash
   cd frontend/money-transfer-ui
   ```
2. Install the required Node modules:
   ```bash
   npm install
   ```
3. Launch the Angular development server:
   ```bash
   npm run start
   ```
4. Open your browser and navigate to `http://localhost:4200/`.
