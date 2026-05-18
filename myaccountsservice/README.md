# MyAccounts Service

A personal finance management REST API built with Spring Boot.

## Overview

MyAccounts is a backend service designed to help users track their personal finances. It allows managing multiple accounting periods (30 days each), tracking fixed/variable income and expenses, and monitoring initial capital with real-time balance calculations.

## Features

- **User Authentication**: Double JWT system (Access Token + Refresh Token)
- **Period Management**: Create/delete accounting periods (30-day duration)
- **Financial Tracking**:
  - Fixed income and costs (copied to new periods automatically)
  - Variable income and costs
  - Initial capital tracking
- **Real-time Calculations**: Total incomes, costs, and balance per period
- **Data Validation**: Input validation and ownership verification
- **Role-based Access**: USER role support
- **RESTful API**: Clean and consistent API design

## Tech Stack

- **Framework**: Spring Boot 3.x
- **Security**: Spring Security with JWT (BCrypt password encoding)
- **Database**: JPA (Hibernate) - PostgreSQL/MySQL compatible
- **Build Tool**: Maven
- **Language**: Java 17+
- **Validation**: Bean Validation (Jakarta)

## Project Structure

```
src/main/java/com/myaccounts/service/myaccountsservice/
├── components/          # Configuration components
│   └── PropsSesionComponent.java
├── controllers/         # REST controllers
│   ├── UserController.java
│   ├── InitCapitalController.java
│   ├── PeriodController.java
│   └── ExceptionController.java
├── exceptions/          # Custom exceptions
│   ├── RefreshException.java
│   ├── ReLodingException.java
│   └── MyBadRequestException.java
├── models/
│   ├── dtos/            # Data Transfer Objects
│   │   ├── LoginDto.java
│   │   ├── JwtDto.java
│   │   ├── DoubleJwtDto.java
│   │   ├── UserDetailsDto.java
│   │   ├── LoginClaimsDto.java
│   │   ├── UserInfoDto.java
│   │   ├── RegisterDto.java
│   │   ├── ErrorDto.java
│   │   ├── InitCapitalDto.java
│   │   ├── InitCapitalPatchDto.java
│   │   ├── ItemDto.java
│   │   ├── ItemRequestDto.java
│   │   ├── PeriodDetailDto.java
│   │   ├── PeriodSummaryDto.java
│   │   └── PeriodsResponseDto.java
│   └── entities/        # JPA Entities
│       ├── UserEntity.java
│       ├── RoleEntity.java
│       ├── LoginEntity.java
│       ├── PeriodEntity.java
│       ├── InitCapitalEntity.java
│       ├── FixedCostEntity.java
│       ├── FixedIncomeEntity.java
│       ├── VariableCostEntity.java
│       └── VariableIncomeEntity.java
├── repositories/         # Data access layer
│   ├── UserRepository.java
│   ├── RoleRepository.java
│   ├── LoginRepository.java
│   ├── PeriodRepository.java
│   ├── InitCapitalRepository.java
│   ├── FixedCostRepository.java
│   ├── FixedIncomeRepository.java
│   ├── VariableCostRepository.java
│   └── VariableIncomeRepository.java
├── security/            # Security configuration
│   ├── SecurityConfig.java
│   └── filter/
│       └── JwtValidationTokenFilter.java
└── services/            # Business logic
    ├── JwtService.java
    ├── UserService.java
    ├── InitCapitalService.java
    ├── PeriodService.java
    └── imp/
        ├── JwtServiceImp.java
        ├── UserServiceImp.java
        ├── UserDetailsServiceImp.java
        ├── InitCapitalServiceImp.java
        └── PeriodServiceImp.java
```

## Database Schema

```dbml
Table the_user{
  id bigint [primary key]
  id_init_capital bigint
  username varchar(60) [unique]
  nickname varchar(60)
  password varchar(255)
}

Table user_role{
  id_user bigint [primary key]
  id_roles bigint [primary key]
}

Table the_role{
  id bigint [primary key]
  name varchar(10) [unique]
}

Table the_init_capital{
  id bigint [primary key]
  init_value bigdecimal
  created Date
}

Table the_periods{
  id bigint [primary key]
  id_user bigint
  created varchar(60)
}

Table fixed_costs{
  id bigint [primary key]
  id_period bigint
  date date
  value bigdecimal
}

Table fixed_income{
  id bigint [primary key]
  id_period bigint
  date date
  value bigdecimal
}

Table variable_costs{
  id bigint [primary key]
  id_period bigint
  date date
  value bigdecimal
}

Table variable_income{
  id bigint [primary key]
  id_period bigint
  date date
  value bigdecimal
}

REF : the_user.id < user_role.id_user
REF : the_role.id < user_role.id_roles
REF : the_user.id < the_periods.id_user
Ref : the_init_capital.id < the_user.id_init_capital
REF : the_periods.id < fixed_costs.id_period
REF : the_periods.id < fixed_income.id_period
REF : the_periods.id < variable_costs.id_period
REF : the_periods.id < variable_income.id_period
```

## Authentication

This project uses a **Double JWT** authentication system:

1. **Access Token** (7 minutes default): Sent in `Authorization: Bearer <token>` header for each request
2. **Login Token** (Refresh Token, 7 days default): Stored in HTTP-only cookie (`the_cookie`)

### Auth Endpoints

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/api/user/login` | User login | No |
| POST | `/api/user/register` | User registration | No |
| POST | `/api/user/refresh` | Refresh access token | No |
| POST | `/api/user/logout` | User logout | No |
| GET | `/api/user/userinfo` | Get user info | Yes |

### Login Flow

1. Send `POST /api/user/login` with username and password
2. Server returns access token in body + login token as HTTP-only cookie
3. Include access token in `Authorization` header for protected requests
4. When access token expires, call `/api/user/refresh` to get new token

## API Endpoints

### Initial Capital

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/api/initCapital` | Get initial capital | Yes |
| PATCH | `/api/initCapital` | Update initial capital | Yes |

### Periods

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/api/periods` | Get all periods with summary | Yes |
| GET | `/api/periods/{id}` | Get period details | Yes |
| POST | `/api/periods` | Create new period | Yes |
| DELETE | `/api/periods/{id}` | Delete period (only latest) | Yes |

### Fixed Costs

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/api/periods/{id}/costfixed` | Create fixed cost | Yes |
| PATCH | `/api/periods/{id}/costfixed/{costId}` | Update fixed cost | Yes |
| DELETE | `/api/periods/{id}/costfixed/{costId}` | Delete fixed cost | Yes |

### Fixed Income

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/api/periods/{id}/incomefixed` | Create fixed income | Yes |
| PATCH | `/api/periods/{id}/incomefixed/{incomeId}` | Update fixed income | Yes |
| DELETE | `/api/periods/{id}/incomefixed/{incomeId}` | Delete fixed income | Yes |

### Variable Costs

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/api/periods/{id}/costvariable` | Create variable cost | Yes |
| PATCH | `/api/periods/{id}/costvariable/{costId}` | Update variable cost | Yes |
| DELETE | `/api/periods/{id}/costvariable/{costId}` | Delete variable cost | Yes |

### Variable Income

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/api/periods/{id}/incomevariable` | Create variable income | Yes |
| PATCH | `/api/periods/{id}/incomevariable/{incomeId}` | Update variable income | Yes |
| DELETE | `/api/periods/{id}/incomevariable/{incomeId}` | Delete variable income | Yes |

## Input Validation

All POST and PATCH endpoints use Bean Validation:
- `ItemRequestDto`: `value` (required, BigDecimal), `title` (required, max 60 chars)
- `InitCapitalPatchDto`: `initValue` (required, BigDecimal)
- Invalid requests return 400 Bad Request with validation error details

## Data Ownership

All endpoints verify that resources belong to the authenticated user. Attempting to access or modify another user's data returns 400 Bad Request.

## Configuration

Configure the application in `src/main/resources/application.properties`:

```properties
spring.application.name=myaccountsservice
server.port=3000

# Database (uncomment and configure)
# spring.datasource.url=jdbc:postgresql://localhost:5432/mydb
# spring.datasource.username=youruser
# spring.datasource.password=yourpass

# JPA
# spring.jpa.hibernate.ddl-auto=update
# spring.jpa.show-sql=true

# JWT Keys (Base64 encoded, min 256 bits)
# jwt.accesstoken.key=yourbase64key
# jwt.logintoken.key=yourbase64key
```

## Running the Application

```bash
# Build the project
./mvnw clean install

# Run the application
./mvnw spring-boot:run
```

The server will start on `http://localhost:3000`

## Requirements

- Java 17 or higher
- Maven 3.8+
- Database (PostgreSQL/MySQL) - optional for development

## License

Private - Personal use only