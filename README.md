# Payflow - Payment Processing Platform

A comprehensive, high-performance payment processing platform built with Spring Boot 4.0.4, designed to handle complex financial transactions, wallet management, risk assessment, and reconciliation at scale.

## 📋 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Technology Stack](#technology-stack)
- [Project Structure](#project-structure)
- [Prerequisites](#prerequisites)
- [Installation & Setup](#installation--setup)
- [Configuration](#configuration)
- [Running the Application](#running-the-application)
- [API Endpoints](#api-endpoints)
- [Architecture & Design](#architecture--design)
- [Concurrency & Performance](#concurrency--performance)
- [Development Guidelines](#development-guidelines)
- [Contributing](#contributing)
- [Troubleshooting](#troubleshooting)
- [License](#license)

## Overview

Payflow is an enterprise-grade payment processing platform that provides:

- **Secure Authentication & Authorization**: JWT-based auth with API key support and refresh tokens
- **Wallet Management**: Multi-currency wallet support with KYC levels and transaction limits
- **Risk Assessment**: Real-time transaction risk evaluation with configurable rules
- **Financial Ledger**: Double-entry accounting system for all financial transactions
- **Reconciliation Engine**: Automated wallet-to-ledger consistency checking
- **Transfer Management**: Atomic transfer execution with ledger recording
- **Webhook Integration**: Event-driven architecture with ModemPay webhook support
- **Notifications**: Multi-channel notifications (SMS, Email) with provider abstraction
- **Audit Trail**: Comprehensive audit logging for compliance and debugging
- **Admin Dashboard**: Administrative controls and user management
- **Metrics & Monitoring**: Prometheus metrics and health checks for production monitoring

## Features

### 🔐 Security
- JWT-based authentication with configurable expiration
- API Key management for service-to-service communication
- Refresh token mechanism for improved security
- Spring Security integration with role-based access control
- Request validation and sanitization
- Global exception handling with security considerations

### 💰 Wallet Management
- Create and manage user wallets
- Real-time balance tracking
- KYC level verification (Level 0-5)
- Transaction limits based on KYC levels
- Wallet status management (ACTIVE, SUSPENDED, CLOSED)
- Wallet transaction history

### ⚖️ Financial Ledger
- Double-entry accounting system
- Transaction categorization and tagging
- Debit/credit management
- Ledger posting with balance tracking
- Transaction reconciliation support

### 📊 Risk Assessment
- Real-time transaction risk scoring
- Configurable risk rules (CRITICAL, HIGH, MEDIUM, LOW)
- Parallel rule evaluation for performance
- Rule-based blocking and approval workflows
- Risk metrics and dashboard

### 🔄 Transfer Management
- Atomic transfer operations
- Concurrent transfer execution
- Ledger integration for financial tracking
- Transfer status management
- Batch transfer processing

### 🔗 Webhook Management
- Event-driven architecture
- ModemPay webhook integration
- Signature validation (HMAC)
- Reliable event delivery
- Event retry mechanisms

### 📬 Notifications
- SMS notifications (Twilio integration)
- Email notifications
- Provider abstraction for easy switching
- Template-based message formatting
- Async notification processing

### 📋 Audit & Compliance
- Automatic entity auditing (CreatedBy, ModifiedBy, timestamps)
- Audit log tracking
- Correlation ID tracking for request tracing
- Comprehensive logging

### 📈 Admin & Monitoring
- Admin user management
- Health checks
- Prometheus metrics
- Application info endpoint
- System metrics (JVM, process, system, Tomcat)

## Technology Stack

| Component | Technology | Version |
|-----------|-----------|---------|
| Framework | Spring Boot | 4.0.4 |
| Java | OpenJDK | 17+ |
| Database | PostgreSQL | 15+ |
| Cache | Redis | 7.0+ |
| ORM | Hibernate | 6.2+ |
| Build Tool | Maven | 3.8+ |
| Auth | Spring Security | 6.2+ |
| JSON | Jackson | 2.15+ |
| Validation | Jakarta Bean Validation | 3.0+ |

### Additional Libraries
- **Lombok**: Reducing boilerplate code
- **Jackson**: JSON processing and mapping
- **Thymeleaf**: Server-side templating (if needed)
- **Spring Data JPA**: Database abstraction
- **Docker Compose**: Local development environment orchestration

## Project Structure

```
payflow/
├── src/
│   ├── main/
│   │   ├── java/com/mamadou/payflow/
│   │   │   ├── PayflowApplication.java          # Main application class
│   │   │   ├── admin/                           # Admin module
│   │   │   │   ├── controller/
│   │   │   │   ├── dto/
│   │   │   │   └── service/
│   │   │   ├── audit/                           # Audit logging module
│   │   │   │   ├── entity/
│   │   │   │   ├── repository/
│   │   │   │   └── service/
│   │   │   ├── auth/                            # Authentication & Authorization
│   │   │   │   ├── controller/
│   │   │   │   ├── dto/
│   │   │   │   ├── entity/
│   │   │   │   ├── mapper/
│   │   │   │   ├── repository/
│   │   │   │   └── service/
│   │   │   ├── common/                          # Shared utilities & config
│   │   │   │   ├── auditing/                    # JPA Auditing configuration
│   │   │   │   ├── config/                      # Spring configurations
│   │   │   │   │   ├── SecurityConfig.java
│   │   │   │   │   ├── AsyncConfig.java
│   │   │   │   │   ├── CacheConfig.java
│   │   │   │   │   ├── JacksonConfig.java
│   │   │   │   │   └── AsyncConfiguration.java
│   │   │   │   ├── filter/                      # HTTP filters
│   │   │   │   ├── health/                      # Health checks
│   │   │   │   ├── metrics/                     # Metrics initialization
│   │   │   │   ├── exception/                   # Exception handling
│   │   │   │   ├── response/                    # Response wrappers
│   │   │   │   └── util/                        # Utility classes
│   │   │   ├── ledger/                          # Financial Ledger Module
│   │   │   │   ├── dto/
│   │   │   │   ├── entity/
│   │   │   │   ├── enums/
│   │   │   │   ├── exception/
│   │   │   │   ├── mapper/
│   │   │   │   ├── repository/
│   │   │   │   └── service/
│   │   │   ├── notification/                    # Notification Module
│   │   │   │   ├── dto/
│   │   │   │   └── service/
│   │   │   ├── reconciliation/                  # Reconciliation Engine
│   │   │   │   ├── controller/
│   │   │   │   ├── dto/
│   │   │   │   ├── entity/
│   │   │   │   ├── enums/
│   │   │   │   ├── job/
│   │   │   │   ├── repository/
│   │   │   │   └── service/
│   │   │   ├── risk/                            # Risk Assessment Module
│   │   │   │   ├── controller/
│   │   │   │   ├── dto/
│   │   │   │   ├── entity/
│   │   │   │   ├── enums/
│   │   │   │   ├── repository/
│   │   │   │   ├── rules/                       # Risk rule implementations
│   │   │   │   └── service/
│   │   │   ├── transaction/                     # Transaction Module
│   │   │   │   ├── controller/
│   │   │   │   ├── dto/
│   │   │   │   ├── entity/
│   │   │   │   ├── enums/
│   │   │   │   ├── exception/
│   │   │   │   ├── repository/
│   │   │   │   └── service/
│   │   │   ├── transfer/                        # Transfer Management Module
│   │   │   │   ├── controller/
│   │   │   │   ├── dto/
│   │   │   │   ├── entity/
│   │   │   │   ├── enums/
│   │   │   │   ├── repository/
│   │   │   │   └── service/
│   │   │   ├── user/                            # User Management Module
│   │   │   │   ├── entity/
│   │   │   │   ├── repository/
│   │   │   │   └── service/
│   │   │   ├── wallet/                          # Wallet Management Module
│   │   │   │   ├── api/
│   │   │   │   ├── dto/
│   │   │   │   ├── entity/
│   │   │   │   ├── enums/
│   │   │   │   ├── exception/
│   │   │   │   ├── repository/
│   │   │   │   └── service/
│   │   │   └── webhook/                         # Webhook Integration
│   │   │       ├── controller/
│   │   │       ├── dto/
│   │   │       ├── entity/
│   │   │       ├── enums/
│   │   │       ├── exception/
│   │   │       ├── repository/
│   │   │       ├── service/
│   │   │       └── validation/
│   │   └── resources/
│   │       ├── application.yaml                 # Main configuration
│   │       ├── static/                          # Static resources
│   │       └── templates/                       # Thymeleaf templates
│   └── test/
│       └── java/com/mamadou/payflow/            # Test classes
├── pom.xml                                      # Maven configuration
├── mvnw & mvnw.cmd                              # Maven wrapper scripts
├── docker-compose.yml                           # Local dev environment
├── compose.yaml                                 # Alternative compose file
├── CONCURRENCY_GUIDE.md                         # Concurrency implementation details
├── HELP.md                                      # Spring Boot help
└── README.md                                    # This file
```

## Prerequisites

### Required
- **Java Development Kit (JDK)**: Version 17 or higher
- **Maven**: Version 3.8.1 or higher
- **PostgreSQL**: Version 15 or higher
- **Redis**: Version 7.0 or higher (optional, for caching)
- **Git**: For version control

### Optional
- **Docker**: For containerized local development
- **Docker Compose**: For orchestrating local services
- **Postman/Insomnia**: For API testing
- **IDE**: IntelliJ IDEA or VS Code recommended

## Installation & Setup

### 1. Clone the Repository

```bash
git clone https://github.com/Mamadou8bah/payflow-server.git
cd payflow-server
```

### 2. Set Up Local Database

#### Using Docker Compose (Recommended)
```bash
docker-compose up -d
```

This starts:
- PostgreSQL on port 5432 (default credentials: postgres/bah12)
- Redis on port 6379

#### Manual Setup
```bash
# Create PostgreSQL database
createdb payflow

# Or using psql
psql -U postgres
CREATE DATABASE payflow;
```

### 3. Configure Environment Variables

Create a `.env` file in the project root:

```bash
# Database Configuration
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/payflow
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=bah12

# Redis Configuration
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=

# JWT Configuration
JWT_SECRET=ZGV2ZWxvcG1lbnQtc2VjcmV0LWtleS1mb3ItcGF5Zmxvdy1hcHAtMzItYnl0ZXM=
JWT_ACCESS_TOKEN_EXPIRATION_MINUTES=15
JWT_REFRESH_TOKEN_EXPIRATION_DAYS=7

# Twilio (SMS Notifications)
TWILIO_ACCOUNT_SID=your_twilio_account_sid
TWILIO_AUTH_TOKEN=your_twilio_auth_token
TWILIO_FROM_NUMBER=+1234567890

# Email Configuration
PAYFLOW_EMAIL_API_KEY=your_email_api_key
PAYFLOW_EMAIL_FROM=no-reply@payflow.local

# Webhook Configuration
MODEMPAY_WEBHOOK_SIGNING_SECRET=your_webhook_secret
MODEMPAY_WEBHOOK_SIGNATURE_HEADER=X-ModemPay-Signature

# Risk Management
PAYFLOW_RISK_CRITICAL_THRESHOLD=9.0
PAYFLOW_RISK_HIGH_THRESHOLD=7.0
PAYFLOW_RISK_MEDIUM_THRESHOLD=5.0

# SMS Provider
PAYFLOW_SMS_PROVIDER=noop  # or 'twilio' for production
```

### 4. Build the Project

```bash
# Using Maven wrapper
./mvnw clean install

# Or using installed Maven
mvn clean install
```

## Configuration

### Application Properties (application.yaml)

Key configuration sections:

#### Server
- **Port**: 5000 (configurable via `server.port`)

#### Database
- **URL**: `jdbc:postgresql://localhost:5432/payflow`
- **DDL**: `create` (drops/recreates schema on startup)

#### Redis Cache
- **TTL**: 10 minutes (600000 ms)
- **Type**: Redis

#### JWT
- **Access Token Expiration**: 15 minutes
- **Refresh Token Expiration**: 7 days

#### Notifications
- **SMS Provider**: Configurable (noop, twilio)
- **Email Provider**: Configurable

#### Risk Management
- **Critical Threshold**: 9.0
- **High Threshold**: 7.0
- **Medium Threshold**: 5.0

#### Rate Limiting
- **Login**: 10 requests per 60 seconds
- **Refresh Token**: 20 requests per 60 seconds

#### Monitoring
- **Metrics Exposure**: health, metrics, prometheus, info
- **Logging Level**: INFO (root), DEBUG (payflow package)

## Running the Application

### 1. Start PostgreSQL and Redis

```bash
# Using Docker Compose
docker-compose up -d

# Or start services individually
docker run -d -p 5432:5432 --name postgres -e POSTGRES_PASSWORD=bah12 postgres:15
docker run -d -p 6379:6379 --name redis redis:7
```

### 2. Run the Application

```bash
# Using Maven wrapper
./mvnw spring-boot:run

# Or build and run the JAR
./mvnw clean package
java -jar target/payflow-0.0.1-SNAPSHOT.jar
```

### 3. Verify Application is Running

```bash
# Health check
curl http://localhost:5000/actuator/health

# Application info
curl http://localhost:5000/actuator/info

# Metrics
curl http://localhost:5000/actuator/metrics
```

## API Endpoints

### Authentication Endpoints

#### Register
```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "user@example.com",
  "password": "securePassword123",
  "firstName": "John",
  "lastName": "Doe"
}
```

#### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "user@example.com",
  "password": "securePassword123"
}

Response:
{
  "accessToken": "jwt_token_here",
  "refreshToken": "refresh_token_here",
  "expiresIn": 900
}
```

#### Refresh Token
```http
POST /api/auth/refresh
Content-Type: application/json

{
  "refreshToken": "refresh_token_here"
}
```

#### Logout
```http
POST /api/auth/logout
Authorization: Bearer {accessToken}
```

### Wallet Endpoints

#### Create Wallet
```http
POST /api/wallets
Authorization: Bearer {accessToken}
Content-Type: application/json

{
  "currency": "USD",
  "kycLevel": 2
}
```

#### Get Wallet Balance
```http
GET /api/wallets/{walletId}/balance
Authorization: Bearer {accessToken}

Response:
{
  "walletId": "uuid",
  "balance": 5000.00,
  "currency": "USD",
  "lastUpdated": "2024-05-28T10:30:00Z"
}
```

#### Update Wallet Limits
```http
PUT /api/wallets/{walletId}/limits
Authorization: Bearer {accessToken}
Content-Type: application/json

{
  "dailyLimit": 10000.00,
  "monthlyLimit": 100000.00,
  "transactionLimit": 5000.00
}
```

### Transfer Endpoints

#### Execute Transfer
```http
POST /api/transfers
Authorization: Bearer {accessToken}
Content-Type: application/json

{
  "fromWalletId": "uuid",
  "toWalletId": "uuid",
  "amount": 100.00,
  "description": "Payment for services"
}

Response:
{
  "transferId": "uuid",
  "status": "COMPLETED",
  "fromWallet": "uuid",
  "toWallet": "uuid",
  "amount": 100.00,
  "createdAt": "2024-05-28T10:30:00Z"
}
```

#### Get Transfer Status
```http
GET /api/transfers/{transferId}
Authorization: Bearer {accessToken}
```

### Transaction Endpoints

#### Create Transaction
```http
POST /api/transactions
Authorization: Bearer {accessToken}
Content-Type: application/json

{
  "walletId": "uuid",
  "type": "DEBIT",
  "amount": 50.00,
  "description": "Purchase",
  "reference": "TXN-2024-001"
}
```

#### Get Transaction History
```http
GET /api/transactions?walletId={walletId}&limit=20&offset=0
Authorization: Bearer {accessToken}
```

### Ledger Endpoints

#### Get Account Balance
```http
GET /api/ledger/accounts/{accountId}/balance
Authorization: Bearer {accessToken}
```

#### Post Ledger Entry
```http
POST /api/ledger/entries
Authorization: Bearer {accessToken}
Content-Type: application/json

{
  "accountId": "uuid",
  "debitAmount": 100.00,
  "creditAmount": 0.00,
  "description": "Deposit",
  "reference": "TXN-2024-001"
}
```

### Risk Assessment Endpoints

#### Evaluate Transaction Risk
```http
POST /api/risk/evaluate
Authorization: Bearer {accessToken}
Content-Type: application/json

{
  "transactionId": "uuid",
  "amount": 1000.00,
  "walletId": "uuid",
  "recipientId": "uuid"
}

Response:
{
  "riskScore": 6.5,
  "riskLevel": "HIGH",
  "rules": [
    {
      "ruleName": "Velocity Check",
      "riskScore": 3.0
    },
    {
      "ruleName": "Amount Threshold",
      "riskScore": 3.5
    }
  ],
  "recommended_action": "REVIEW"
}
```

### Admin Endpoints

#### Get All Users
```http
GET /api/admin/users
Authorization: Bearer {adminAccessToken}
```

#### Suspend User
```http
PUT /api/admin/users/{userId}/suspend
Authorization: Bearer {adminAccessToken}
```

#### Get Audit Logs
```http
GET /api/admin/audit-logs?entity=User&limit=50
Authorization: Bearer {adminAccessToken}
```

### Health & Monitoring

#### Application Health
```http
GET /actuator/health
```

#### Prometheus Metrics
```http
GET /actuator/prometheus
```

#### Application Info
```http
GET /actuator/info
```

## Architecture & Design

### Layered Architecture

The application follows a clean, layered architecture:

```
┌─────────────────────────────────────┐
│       REST Controllers               │  (HTTP endpoints)
├─────────────────────────────────────┤
│       Service Layer                  │  (Business logic)
├─────────────────────────────────────┤
│       Repository Layer               │  (Data access)
├─────────────────────────────────────┤
│       Entity & Domain Models         │  (Data models)
├─────────────────────────────────────┤
│       Database (PostgreSQL)          │  (Persistence)
└─────────────────────────────────────┘
```

### Design Patterns Used

1. **Service Layer Pattern**: Business logic encapsulated in service classes
2. **Repository Pattern**: Data access abstraction using Spring Data JPA
3. **DTO Pattern**: Data transfer objects for API requests/responses
4. **Mapper Pattern**: Entity-to-DTO conversion with custom mappers
5. **Factory Pattern**: Creating complex objects (e.g., risk rules)
6. **Observer Pattern**: Event-driven webhook processing
7. **Singleton Pattern**: Configuration beans and services
8. **Async/Await Pattern**: Non-blocking operations with CompletableFuture

### Module Interactions

```
┌────────────────────────────────────────────────────────────┐
│                   API Controllers                           │
├────────────────────────────────────────────────────────────┤
│  Auth │ Wallet │ Transfer │ Transaction │ Risk │ Webhook  │
├────────────────────────────────────────────────────────────┤
│                    Service Layer                            │
├────────────────────────────────────────────────────────────┤
│ User │ Wallet │ Transfer │ Ledger │ Risk │ Reconciliation │
├────────────────────────────────────────────────────────────┤
│                  Repository/Data Layer                      │
├────────────────────────────────────────────────────────────┤
│  PostgreSQL Database │ Redis Cache │ External APIs        │
└────────────────────────────────────────────────────────────┘
```

## Concurrency & Performance

### Overview
Payflow implements sophisticated concurrency patterns for high-throughput payment processing. See [CONCURRENCY_GUIDE.md](payflow/CONCURRENCY_GUIDE.md) for detailed information.

### Async Executors

#### riskEvaluationExecutor
- **Threads**: 4 core, 8 max
- **Purpose**: Parallel risk rule evaluation
- **Use Case**: Evaluating multiple risk rules concurrently

#### reconciliationExecutor
- **Threads**: 8 core, 16 max
- **Purpose**: Batch wallet reconciliation
- **Use Case**: Parallel wallet-to-ledger consistency checks

#### webhookExecutor
- **Threads**: 6 core, 12 max
- **Purpose**: Webhook processing
- **Use Case**: Parallel webhook validation and processing

#### transferExecutor
- **Threads**: 10 core, 20 max
- **Purpose**: Transfer execution
- **Use Case**: Parallel ledger recording and wallet updates

#### taskExecutor (Default)
- **Threads**: 10 core, 20 max
- **Purpose**: General async operations
- **Use Case**: Notifications, batch jobs, etc.

### Performance Optimizations

1. **Parallel Rule Evaluation**: Risk rules evaluated concurrently instead of sequentially
2. **Connection Pooling**: HikariCP for database connection management
3. **Redis Caching**: 10-minute TTL for frequently accessed data
4. **Batch Processing**: Bulk operations for reconciliation
5. **Lazy Loading**: JPA lazy loading for related entities
6. **Query Optimization**: Custom repository queries to minimize N+1 problems
7. **Rate Limiting**: Prevent abuse and ensure fair resource usage

## Development Guidelines

### Code Style
- Follow Spring Framework conventions
- Use meaningful variable and method names
- Keep methods focused and small (single responsibility)
- Write comprehensive JavaDoc comments
- Use Lombok annotations to reduce boilerplate

### Creating New Entities

```java
@Entity
@Table(name = "my_entities")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MyEntity extends BaseEntity {
    
    @Column(nullable = false)
    private String name;
    
    @Column(name = "description", length = 500)
    private String description;
    
    @Enumerated(EnumType.STRING)
    private MyStatus status;
}
```

### Creating New Services

```java
@Service
@Slf4j
@RequiredArgsConstructor
public class MyService {
    
    private final MyRepository repository;
    private final Executor myExecutor;  // Inject if async needed
    
    public MyEntity create(MyRequest request) {
        MyEntity entity = new MyEntity();
        entity.setName(request.getName());
        // ... set other fields
        return repository.save(entity);
    }
    
    @Async("myExecutor")
    public CompletableFuture<Void> processAsync() {
        // Async processing
        return CompletableFuture.completedFuture(null);
    }
}
```

### Creating New Endpoints

```java
@RestController
@RequestMapping("/api/my-resource")
@Slf4j
@RequiredArgsConstructor
public class MyController {
    
    private final MyService service;
    
    @PostMapping
    public ResponseEntity<ApiResponse<MyResponse>> create(@Valid @RequestBody MyRequest request) {
        MyEntity entity = service.create(request);
        return ResponseEntity.ok(
            ApiResponse.success("Resource created successfully", mapper.toResponse(entity))
        );
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MyResponse>> getById(@PathVariable String id) {
        MyEntity entity = service.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Resource not found"));
        return ResponseEntity.ok(
            ApiResponse.success("Resource retrieved successfully", mapper.toResponse(entity))
        );
    }
}
```

### Testing

Create tests in `src/test/java` following the same package structure:

```java
@SpringBootTest
@ActiveProfiles("test")
class MyServiceTest {
    
    @MockBean
    private MyRepository repository;
    
    @InjectMocks
    private MyService service;
    
    @Test
    void testCreate() {
        MyRequest request = new MyRequest("Test", "Description");
        MyEntity entity = new MyEntity(null, "Test", "Description");
        
        when(repository.save(any(MyEntity.class))).thenReturn(entity);
        
        MyEntity result = service.create(request);
        
        assertNotNull(result);
        assertEquals("Test", result.getName());
        verify(repository).save(any(MyEntity.class));
    }
}
```

## Contributing

### Development Workflow

1. **Create a Feature Branch**
   ```bash
   git checkout -b feature/your-feature-name
   ```

2. **Make Your Changes**
   - Follow code style guidelines
   - Add tests for new functionality
   - Update documentation as needed

3. **Commit Your Changes**
   ```bash
   git add .
   git commit -m "feat: add your feature description"
   ```

4. **Push to Remote**
   ```bash
   git push origin feature/your-feature-name
   ```

5. **Create a Pull Request**
   - Describe the changes
   - Reference any related issues
   - Ensure CI/CD checks pass

### Commit Message Format

```
<type>: <subject>

<body>

<footer>
```

Types: `feat`, `fix`, `docs`, `style`, `refactor`, `perf`, `test`, `chore`

Example:
```
feat: add KYC level verification for wallets

- Implement KYC level checking in wallet service
- Add validation rules based on KYC tier
- Update wallet limits accordingly

Closes #123
```

## Troubleshooting

### Common Issues

#### 1. Database Connection Failed
```
Error: Connection to localhost:5432 refused
```

**Solution**:
```bash
# Verify PostgreSQL is running
docker ps | grep postgres

# Or start it
docker-compose up -d
```

#### 2. Redis Connection Timeout
```
Error: Unable to connect to Redis localhost:6379
```

**Solution**:
```bash
# Start Redis
docker run -d -p 6379:6379 redis:7

# Or via compose
docker-compose up -d
```

#### 3. JWT Token Invalid
```
Error: JWT signature does not match
```

**Solution**: Verify JWT_SECRET is consistent between token generation and validation.

#### 4. Maven Build Failure
```
[ERROR] COMPILATION ERROR
```

**Solution**:
```bash
# Clean and rebuild
./mvnw clean install -X

# Or use JDK 17+ explicitly
export JAVA_HOME=/path/to/jdk17
./mvnw clean install
```

#### 5. Port Already in Use
```
Error: Address already in use: 0.0.0.0:5000
```

**Solution**:
```bash
# Change port in application.yaml
server:
  port: 5001

# Or kill existing process
lsof -i :5000  # macOS/Linux
Get-Process -Id (Get-NetTCPConnection -LocalPort 5000).OwningProcess  # Windows
```

### Debug Mode

Run with debug output:
```bash
./mvnw spring-boot:run -Dspring-boot.run.arguments="--debug"

# Or with custom logging
./mvnw spring-boot:run -Dlogging.level.com.mamadou.payflow=TRACE
```

### Database Schema Reset

```bash
# Delete PostgreSQL container and volume
docker-compose down -v

# Recreate containers
docker-compose up -d

# Application will auto-create schema on startup (ddl-auto: create)
```

## Performance Monitoring

### View Metrics

```bash
# All available metrics
curl http://localhost:5000/actuator/metrics

# Specific metric
curl http://localhost:5000/actuator/metrics/http.server.requests

# Prometheus format
curl http://localhost:5000/actuator/prometheus
```

### Health Checks

```bash
# Detailed health (requires authentication)
curl -H "Authorization: Bearer {token}" http://localhost:5000/actuator/health

# Basic health
curl http://localhost:5000/actuator/health/readiness
```

### Logs

```bash
# View logs
tail -f logs/payflow.log

# Search for errors
grep "ERROR" logs/payflow.log

# Follow logs in real-time
tail -f logs/payflow.log | grep WARN
```

## Security Considerations

1. **Credentials Management**
   - Never commit credentials to repository
   - Use environment variables for secrets
   - Rotate JWT secrets regularly

2. **Database Security**
   - Use strong passwords for database
   - Enable SSL/TLS for database connections
   - Implement row-level security if needed

3. **API Security**
   - Use HTTPS in production
   - Implement rate limiting
   - Validate all inputs
   - Use CORS appropriately

4. **Authentication**
   - Enforce strong password policies
   - Implement MFA for admin accounts
   - Use short-lived access tokens
   - Implement secure token refresh

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Support & Contact

For issues, questions, or contributions:
- **Repository**: https://github.com/Mamadou8bah/payflow-server
- **Issues**: https://github.com/Mamadou8bah/payflow-server/issues
- **Email**: support@payflow.local

## Changelog

### Version 0.0.1 (Current)
- Initial project setup
- Core authentication system
- Wallet management system
- Transfer functionality
- Risk assessment engine
- Ledger system
- Webhook integration
- Notification system
- Admin dashboard
- Monitoring and metrics

---

**Last Updated**: May 28, 2026
**Maintainer**: Mamadou Bah
**Status**: Active Development
