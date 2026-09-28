# NyumbaIQ - Phase 1

Smart house/property rental management system.

## Prerequisites

- Java 21 (OpenJDK Temurin recommended)
- Maven 3.9+
- Node.js 18+ and npm
- Flutter 3.x
- PostgreSQL 14+
- Redis 7+
- RabbitMQ 3.x

## Database Setup

```bash
createdb nyumbaiq
psql -U postgres -c "CREATE USER nyumbaiq WITH PASSWORD 'nyumbaiq';"
psql -U postgres -d nyumbaiq -c "GRANT ALL PRIVILEGES ON DATABASE nyumbaiq TO nyumbaiq;"
```

Flyway migrations run automatically on backend startup.

## Running Backend

```bash
cd backend
mvn spring-boot:run
```

Backend runs on http://localhost:8080/api/v1

## Running Web

```bash
cd web
npm install
npm run dev
```

Web runs on http://localhost:5173

## Running Mobile

```bash
cd mobile
flutter pub get
flutter run
```

## Phase 1 Features

- Authentication (JWT access + refresh tokens)
- Role-based access control (Owner, Manager, Tenant)
- Property hierarchy (Property -> Building -> Floor -> Unit)
- Manager management with property assignment
- Tenant foundation
- KYC foundation with secure document storage
- Audit logging
- Owner dashboard with real statistics
- Redis caching foundation
- RabbitMQ foundation
- Secure file upload/download (no MinIO)
- React SPA with TanStack Query caching
- Flutter mobile with role-based navigation
- PWA installation support

## Role Overview

### Owner
Full system access, manages properties, buildings, floors, units, tenants, managers, KYC review, audit logs.

### Manager
Scoped access to assigned properties, manages buildings, floors, units, tenants, KYC review within scope.

### Tenant
Access to own profile, own KYC documents.

## Notes

- No biometric/fingerprint functionality
- No payment integration (Phase 2)
- No smart lock integration (Phase 2)
- No AI assistant (Phase 3)
- No Docker/Kubernetes
