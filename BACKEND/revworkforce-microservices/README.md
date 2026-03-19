# RevWorkforce - Microservices Backend

## Architecture Overview

```
                          ┌─────────────────────┐
                          │    Eureka Server     │
                          │    (Port: 8761)      │
                          └─────────┬───────────┘
                                    │ Service Discovery
          ┌─────────────────────────┼────────────────────────┐
          │                         │                        │
    ┌─────▼──────┐          ┌───────▼────────┐              │
    │ API Gateway│          │  Auth Service  │              │
    │ (Port 8080)│◄────────►│  (Port 8081)   │              │
    └─────┬──────┘          └────────────────┘              │
          │                                                  │
    ┌─────┼──────────────────────────────────────────┐       │
    │     │                                          │       │
    ▼     ▼              ▼                  ▼        │       │
┌────────────┐  ┌──────────────┐  ┌───────────────┐ │       │
│  Employee  │  │    Leave     │  │  Performance  │ │       │
│  Service   │  │   Service    │  │    Service    │ │       │
│ (Port 8082)│  │ (Port 8083)  │  │ (Port 8084)   │ │       │
└────────────┘  └──────────────┘  └───────────────┘ │       │
      │                │                  │          │       │
      └────────────────┼──────────────────┘          │       │
                       │                             │       │
                ┌──────▼──────┐                      │       │
                │   MySQL DB   │                      │       │
                │revworkforce_ │◄────────────────────┘       │
                │  employees   │                              │
                └─────────────┘                              │
                                                             │
                ┌─────────────┐                              │
                │   MySQL DB   │◄─────────────────────────────┘
                │revworkforce_ │
                │    auth      │
                └─────────────┘
```

## Services & Ports

| Service             | Port | Description                          |
|---------------------|------|--------------------------------------|
| Eureka Server       | 8761 | Service discovery & registry         |
| API Gateway         | 8080 | Single entry point for all requests  |
| Auth Service        | 8081 | JWT Authentication                   |
| Employee Service    | 8082 | Employee & Admin management          |
| Leave Service       | 8083 | Leave management                     |
| Performance Service | 8084 | Performance reviews & goals          |

## Database Strategy

- **`revworkforce_auth`** — Auth service (users table for auth)
- **`revworkforce_employees`** — Shared by Employee, Leave & Performance services (employees, leave_applications, leave_balances, leave_quotas, performance_reviews, goals)

> The shared DB approach avoids complex inter-service communication for user lookups while maintaining service separation.

## Prerequisites

- Java 17+
- Maven 3.6+
- MySQL 8.0+
- Node.js (for Angular frontend)

## Quick Start

### Step 1: Configure MySQL

Update the DB credentials in each service's `application.yml`:
- `auth-service/src/main/resources/application.yml`
- `employee-service/src/main/resources/application.yml`
- `leave-service/src/main/resources/application.yml`
- `performance-service/src/main/resources/application.yml`

Default credentials (update to match yours):
```yaml
spring:
  datasource:
    username: Abhishek
    password: Abhi@123
```

### Step 2: Start Services (in order!)

```bash
# Terminal 1 - Eureka Server (start first!)
cd eureka-server
mvn spring-boot:run

# Wait for Eureka to start, then:

# Terminal 2 - Auth Service
cd auth-service
mvn spring-boot:run

# Terminal 3 - Employee Service
cd employee-service
mvn spring-boot:run

# Terminal 4 - Leave Service
cd leave-service
mvn spring-boot:run

# Terminal 5 - Performance Service
cd performance-service
mvn spring-boot:run

# Terminal 6 - API Gateway (start last!)
cd api-gateway
mvn spring-boot:run
```

### Step 3: Verify Everything is Running

Open Eureka Dashboard: http://localhost:8761

You should see all 4 services registered:
- AUTH-SERVICE
- EMPLOYEE-SERVICE
- LEAVE-SERVICE
- PERFORMANCE-SERVICE

## Default Test Credentials

| Role     | Email                       | Password     |
|----------|-----------------------------|--------------|
| Admin    | admin@revworkforce.com      | admin123     |
| Manager  | manager@revworkforce.com    | manager123   |
| Employee | employee@revworkforce.com   | employee123  |
| Employee | john.doe@revworkforce.com   | employee123  |

## API Endpoints (via Gateway on port 8080)

All requests go through: `http://localhost:8080`

### Auth
- `POST /api/auth/login` — Login (no auth required)

### Employees
- `GET /api/employees/me` — Get my profile
- `PUT /api/employees/me` — Update my profile
- `GET /api/employees` — Get all employees
- `GET /api/employees/{id}` — Get employee by ID
- `GET /api/employees/managers` — Get all managers
- `GET /api/employees/my-team` — Get my team (manager)

### Admin
- `GET /api/admin/dashboard` — Dashboard stats
- `POST /api/admin/employees` — Create employee
- `PUT /api/admin/employees/{id}/assign-manager/{managerId}` — Assign manager
- `PUT /api/admin/employees/{id}/toggle-status` — Toggle active/inactive

### Leaves
- `GET /api/leaves/balance` — My leave balance
- `POST /api/leaves/apply` — Apply for leave
- `GET /api/leaves/my-leaves` — My leave history
- `PUT /api/leaves/{id}/cancel` — Cancel leave
- `GET /api/leaves/team` — Team leaves (manager)
- `GET /api/leaves/team/pending` — Pending team leaves (manager)
- `PUT /api/leaves/{id}/process` — Approve/Reject leave (manager)
- `GET /api/leaves/all` — All leaves (admin)

### Leave Admin (within Leave Service)
- `POST /api/leaves/admin/quotas` — Configure leave quota
- `GET /api/leaves/admin/quotas` — Get all quotas
- `POST /api/leaves/admin/reset-balances/{year}` — Reset balances for year

### Performance
- `POST /api/performance/reviews` — Create review
- `PUT /api/performance/reviews/{id}` — Update review
- `PUT /api/performance/reviews/{id}/submit` — Submit review
- `GET /api/performance/reviews/my` — My reviews
- `GET /api/performance/reviews/team` — Team reviews (manager)
- `GET /api/performance/reviews/team/pending` — Pending reviews (manager)
- `PUT /api/performance/reviews/{id}/feedback` — Add feedback (manager)
- `POST /api/performance/goals` — Create goal
- `PUT /api/performance/goals/{id}` — Update goal
- `GET /api/performance/goals/my` — My goals
- `GET /api/performance/goals/my/{year}` — My goals by year
- `DELETE /api/performance/goals/{id}` — Delete goal

## Frontend Note

Your Angular frontend should connect to:
```
http://localhost:8080
```
This is the **API Gateway** — the single entry point that routes to all microservices.

## Key Design Decisions

1. **API Gateway** validates JWT tokens for all secured routes before forwarding requests. The downstream services receive the user email via `X-User-Email` header, trusting the gateway.

2. **Shared Database** for Leave & Performance services with the Employee service. They all read from `revworkforce_employees` DB, so User data is consistent without inter-service HTTP calls.

3. **Separate Auth DB** (`revworkforce_auth`) for the auth service to keep authentication isolated.

4. **Data Initialization** — Employee Service seeds the test users, Leave Service auto-creates leave balances on startup for any existing employees.
