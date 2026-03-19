## RevWorkforce HRM — Angular Frontend

A complete Angular 17 frontend for the RevWorkforce Spring Boot backend.

## Tech Stack

- **Angular 17** — Standalone components, signals, inject()
- **JWT Auth** — HTTP interceptor + localStorage
- **Role-based routing** — ADMIN / MANAGER / EMPLOYEE
- **Base URL** — `http://localhost:8080/api`

---

## Project Structure

```
src/app/
├── core/
│   ├── models/          # TypeScript interfaces (match Java DTOs)
│   ├── services/
│   │   ├── auth.service.ts      # Login, logout, token, role helpers
│   │   └── api.service.ts       # All API calls (Employee, Leave, Performance, Admin)
│   ├── interceptors/
│   │   └── auth.interceptor.ts  # Adds Bearer token to every request
│   └── guards/
│       └── auth.guard.ts        # Route protection + role guard
├── features/
│   ├── auth/login/              # Login page
│   ├── dashboard/               # Role-aware dashboard
│   ├── employees/
│   │   ├── profile/             # My Profile + edit
│   │   └── list/                # Employee list (ADMIN/MANAGER)
│   ├── leaves/                  # Apply, view, approve/reject leaves
│   ├── performance/             # Self-reviews + manager feedback
│   ├── goals/                   # Goal tracking with progress
│   └── admin/                   # Admin panel (quotas, reset balances)
└── shared/
    └── components/shell/        # Sidebar + layout shell
```

---

## Setup & Run

### Prerequisites

- Node.js 18+
- Angular CLI: `npm install -g @angular/cli`
- Backend running on `http://localhost:8080`

### Install & Start

```bash
npm install
ng serve
```

Open `http://localhost:4200`

---

## Features by Role

### All Roles

- Login / Logout
- Dashboard with leave balance, recent requests, active goals
- My Profile — view & edit personal info
- Apply for Leave (Casual / Sick / Paid)
- View leave history
- Create & submit performance self-reviews
- Set and track goals with progress %

### Manager + Admin

- View team members
- Approve / Reject team leave requests
- Review pending team performance reviews
- Add manager feedback and star rating
- View all team performance reviews

### Admin Only

- Create new employees
- Assign managers to employees
- Toggle employee active/inactive status
- Configure leave quotas per type/year
- Reset leave balances for new year
- Full admin dashboard stats

---

## API Endpoints Used

| Service     | Method | Endpoint                                         | Description                        |
| ----------- | ------ | ------------------------------------------------ | ---------------------------------- |
| Auth        | POST   | `/api/auth/login`                                | Login and receive JWT token        |
| Employee    | GET    | `/api/employees/me`                              | Get logged-in employee profile     |
| Employee    | PUT    | `/api/employees/me`                              | Update logged-in employee profile  |
| Employee    | GET    | `/api/employees`                                 | Get all employees                  |
| Employee    | GET    | `/api/employees/my-team`                         | Get team members (Manager/Admin)   |
| Leave       | GET    | `/api/leaves/balance`                            | View leave balance                 |
| Leave       | POST   | `/api/leaves/apply`                              | Apply for leave                    |
| Leave       | GET    | `/api/leaves/my-leaves`                          | View my leave requests             |
| Leave       | PUT    | `/api/leaves/{id}/cancel`                        | Cancel pending leave               |
| Leave       | GET    | `/api/leaves/team`                               | View team leave requests (Manager) |
| Leave       | PUT    | `/api/leaves/{id}/process`                       | Approve/Reject leave (Manager)     |
| Leave       | GET    | `/api/leaves/all`                                | View all leave requests (Admin)    |
| Performance | POST   | `/api/performance/reviews`                       | Create performance review          |
| Performance | PUT    | `/api/performance/reviews`                       | Update performance review          |
| Performance | PUT    | `/api/performance/reviews/{id}/submit`           | Submit review to manager           |
| Performance | PUT    | `/api/performance/reviews/{id}/feedback`         | Manager gives feedback             |
| Performance | GET    | `/api/performance/reviews/my`                    | View my reviews                    |
| Performance | GET    | `/api/performance/reviews/team`                  | View team reviews                  |
| Goals       | POST   | `/api/performance/goals`                         | Create goal                        |
| Goals       | PUT    | `/api/performance/goals`                         | Update goal                        |
| Goals       | DELETE | `/api/performance/goals`                         | Delete goal                        |
| Goals       | GET    | `/api/performance/goals/my`                      | View my goals                      |
| Admin       | GET    | `/api/admin/dashboard`                           | View admin dashboard statistics    |
| Admin       | POST   | `/api/admin/employees`                           | Create employee                    |
| Admin       | PUT    | `/api/admin/employees/{id}/assign-manager/{mId}` | Assign manager                     |
| Admin       | PUT    | `/api/admin/employees/{id}/toggle-status`        | Activate/Deactivate employee       |
| Admin       | POST   | `/api/admin/leave-quotas`                        | Configure leave quotas             |
| Admin       | GET    | `/api/admin/leave-quotas`                        | View leave quotas                  |
| Admin       | POST   | `/api/admin/reset-leave-balances/{year}`         | Reset leave balances for a year    |

## Test Credentials

| Role     | Email                     | Password    |
| -------- | ------------------------- | ----------- |
| Admin    | admin@revworkforce.com    | admin123    |
| Manager  | manager@revworkforce.com  | manager123  |
| Employee | employee@revworkforce.com | employee123 |

> Quick-fill buttons are available on the login screen!
