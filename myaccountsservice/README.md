# MyAccounts Service

A personal finance management REST API built with Spring Boot.

## Overview

MyAccounts is a backend service designed to help users track their personal finances. It allows managing multiple accounting periods, tracking fixed/variable income and expenses, and monitoring initial capital.

## Features

- **User Authentication**: Double JWT system (Access Token + Refresh Token)
- **Period Management**: Create and manage accounting periods
- **Financial Tracking**:
  - Fixed income and costs
  - Variable income and costs
  - Initial capital tracking
- **Role-based Access**: User roles support
- **RESTful API**: Clean and consistent API design

## Tech Stack

- **Framework**: Spring Boot 3.x
- **Security**: Spring Security with JWT
- **Database**: JPA (Hibernate) - Compatible with PostgreSQL/MySQL
- **Build Tool**: Maven
- **Language**: Java 17+

## Project Structure

```
src/main/java/com/myaccounts/service/myaccountsservice/
├── components/          # Configuration components
│   └── PropsSesionComponent.java
├── controllers/         # REST controllers
│   ├── UserController.java
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
│   │   └── ErrorDto.java
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
│   └── LoginRepository.java
├── security/            # Security configuration
│   ├── SecurityConfig.java
│   └── filter/
│       └── JwtValidationTokenFilter.java
└── services/            # Business logic
    ├── JwtService.java
    ├── UserService.java
    ├── UserDetailsServiceImp.java
    └── imp/
        ├── JwtServiceImp.java
        └── UserServiceImp.java
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

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/user/login` | User login |
| POST | `/api/user/refresh` | Refresh access token |
| POST | `/api/user/logout` | User logout |
| GET | `/api/user/userinfo` | Get user info (requires auth) |

### Login Flow

1. Send `POST /api/user/login` with username and password
2. Server returns access token in body + login token as HTTP-only cookie
3. Include access token in `Authorization` header for protected requests
4. When access token expires, call `/api/user/refresh` to get new token

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