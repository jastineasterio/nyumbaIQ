# NyumbaIQ Phase 1 — Final Report

## Executive Summary

Phase 1 of NyumbaIQ is complete. The system foundation, authentication, core property management, and cross-platform clients have been implemented and verified.

## What Was Implemented

### Backend (Spring Boot 3.5.x, Java 21)
- **Authentication**: JWT access/refresh tokens, BCrypt password hashing, login/logout/change-password
- **RBAC**: Three roles — OWNER, MANAGER, TENANT — with backend-enforced method security (`@PreAuthorize`)
- **User Management**: Full user entity with status lifecycle (ACTIVE, INACTIVE, SUSPENDED, PENDING_VERIFICATION, DEACTIVATED)
- **Property Hierarchy**: Property → Building → Floor → Unit with full CRUD and scoped access
- **Manager Management**: CRUD + property assignment with uniqueness constraints
- **Tenant Foundation**: Tenant profiles with optional user linkage and role-based isolation
- **KYC Foundation**: KYC profiles, document types (NATIONAL_ID, VOTER_ID, DRIVING_LICENCE, PASSPORT, SUPPORTING, OTHER), review workflow
- **Secure Document Storage**: Application filesystem storage (`./uploads/`), authenticated download endpoints, MIME/size validation
- **Audit Logging**: AOP-based audit aspect capturing all controller method executions with SUCCESS/FAILURE/DENIED results
- **Redis**: Caching foundation with configurable TTLs
- **RabbitMQ**: Topic exchange, audit queue, async event producer/listener foundation
- **Dashboard**: Real statistics endpoint for owners (total properties, buildings, floors, units, occupied, vacant, managers, tenants)
- **Flyway**: 11 migrations (V1–V11) creating all tables with indexes and constraints
- **Error Handling**: Standardized `ApiError` DTO, global exception handler, custom exceptions

### Web Application (React 18 + TypeScript + Vite)
- **SPA Navigation**: React Router with no full-page reloads; persistent app shell with sidebar/drawer
- **TanStack Query**: Caching, deduplication, background refresh, stale-while-revalidate, query invalidation on mutations
- **Role-Based UI**: Sidebar links filtered by OWNER/MANAGER/TENANT
- **PWA**: Web app manifest, service worker (cache-first assets, network-first API), custom install prompt with dismiss/install actions
- **Tailwind CSS**: Mobile-first responsive design with navy/cyan/violet theme
- **Axios Client**: Request/response interceptors with silent access token refresh and automatic retry queue

### Mobile Application (Flutter 3.x, Dart 3.x)
- **Provider State Management**: AuthProvider, feature providers for dashboard, properties, buildings, floors, units, tenants, managers, KYC, audit
- **GoRouter**: Persistent auth guard, role-based redirection
- **API Client**: Secure token storage, multipart uploads, network error handling, 401 refresh flow
- **Role-Based Drawer**: Owner/Manager/Tenant menu items
- **Bottom Navigation**: Role-aware persistent nav bar
- **Material 3 Theme**: Navy/indigo primary, cyan accent, touch-friendly targets

### DevOps & Scripts
- Database setup script (`scripts/setup-db.bat`)
- Backend start script (`scripts/start-backend.bat`, `scripts/start-backend-env.bat`)
- Web start script (`scripts/start-web.bat`)
- Mobile start script (`scripts/start-mobile.bat`)
- Verification script (`scripts/verify.bat`)
- Environment example (`backend/.env.example`)
- Root `README.md` and `VERIFICATION.md`

## Database Tables

| Table | Purpose |
|-------|---------|
| `users` | All system users (owner, manager, tenant) |
| `properties` | Property master data |
| `buildings` | Buildings under properties |
| `floors` | Floors under buildings |
| `units` | Units under floors |
| `tenants` | Tenant profiles |
| `manager_assignments` | Manager → Property scope |
| `kyc_profiles` | KYC verification status per tenant |
| `kyc_documents` | Uploaded KYC document metadata |
| `refresh_tokens` | Refresh token persistence and revocation |
| `audit_logs` | Immutable audit trail |

## API Endpoints

Base path: `/api/v1`

| Module | Endpoints |
|--------|-----------|
| Auth | `POST /auth/login`, `POST /auth/refresh`, `POST /auth/logout`, `POST /auth/change-password` |
| Users | `GET /users/me`, `PUT /users/me`, `GET /users`, `GET /users/{id}`, `PUT /users/{id}/status` |
| Properties | `GET /properties`, `POST /properties`, `GET /properties/{id}`, `PUT /properties/{id}`, `DELETE /properties/{id}` |
| Buildings | `GET /properties/{propertyId}/buildings`, `POST /properties/{propertyId}/buildings`, `GET /buildings/{id}`, `PUT /buildings/{id}`, `DELETE /buildings/{id}` |
| Floors | `GET /buildings/{buildingId}/floors`, `POST /buildings/{buildingId}/floors`, `GET /floors/{id}`, `PUT /floors/{id}`, `DELETE /floors/{id}` |
| Units | `GET /floors/{floorId}/units`, `POST /floors/{floorId}/units`, `GET /units/{id}`, `PUT /units/{id}`, `DELETE /units/{id}`, `GET /units` |
| Tenants | `GET /tenants`, `POST /tenants`, `GET /tenants/{id}`, `PUT /tenants/{id}`, `DELETE /tenants/{id}` |
| Managers | `GET /managers`, `POST /managers`, `GET /managers/{id}`, `PUT /managers/{id}`, `DELETE /managers/{id}`, `POST /managers/{id}/assign-property`, `DELETE /managers/{id}/assignments/{assignmentId}` |
| KYC | `GET /kyc/tenant/{tenantId}/profile`, `POST /kyc/tenant/{tenantId}/documents`, `GET /kyc/tenant/{tenantId}/documents`, `GET /kyc/documents/{id}`, `PUT /kyc/documents/{id}/review`, `DELETE /kyc/documents/{id}`, `GET /kyc/documents/{id}/download` |
| Dashboard | `GET /dashboard/stats` |
| Audit Logs | `GET /audit-logs` |

## Authentication Implementation

- **JWT**: HS256 signed access tokens (1 day) and refresh tokens (7 days)
- **Password Hashing**: BCrypt via Spring Security `PasswordEncoder`
- **Token Storage**: Refresh tokens persisted in PostgreSQL with revocation flag
- **Validation**: `JwtAuthFilter` extracts Bearer token, validates signature/expiry, loads user, rejects inactive users
- **Refresh Flow**: Axios/Flutter interceptors catch 401, call `/auth/refresh`, retry original request with queued concurrency control
- **Secrets**: JWT secret loaded from environment variable, never exposed to frontend

## RBAC Implementation

- **Method Security**: `@EnableMethodSecurity` + `@PreAuthorize` on all controllers
- **Role Isolation**:
  - TENANT users only see their own tenant record
  - MANAGER users scoped to assigned properties
  - OWNER users have full access
- **Frontend Guards**: React route guards + mobile drawer filtering (defense-in-depth, not sole protection)

## Performance Optimizations

- **Backend**:
  - Dashboard stats use `count()` queries instead of loading full pages
  - Flyway indexes on frequently queried columns (email, role, status, foreign keys)
  - HikariCP connection pooling (max 10, min 2)
  - Pagination default size 20 on all list endpoints
- **Web**:
  - TanStack Query `staleTime: 30s`, `gcTime: 5min`
  - Query deduplication and background refetch
  - Skeleton loaders instead of full-screen spinners
  - Code splitting via React Router lazy-ready structure
  - PWA precaching for instant repeat loads
- **Mobile**:
  - Provider-based state prevents unnecessary rebuilds
  - Local caching of user data in `shared_preferences`
  - Secure token persistence avoids repeated login

## Caching Implementation

- **Redis**: Configured via Spring Boot starter; `CacheService` utility for programmatic cache ops
- **Cache Invalidation**: `@CacheEvict` planned on mutation services
- **Web**: TanStack Query in-memory cache with background refresh
- **Mobile**: Local JSON caching of user profile and feature data

## PWA Implementation

- **Manifest**: `public/manifest.json` with theme color `#0f172a`, standalone display, SVG icons
- **Service Worker**: Generated by `vite-plugin-pwa` with Workbox; cache-first for static assets, network-first for API
- **Install Prompt**: Custom `useInstallPrompt` hook + `InstallBanner` component; dismissed state persisted in `localStorage`

## Testing Results

- **Backend**: 16 tests passing
  - `AuthServiceTest` (3 tests)
  - `PropertyServiceTest` (5 tests)
  - `TenantServiceTest` (5 tests)
  - `AuditLogServiceTest` (3 tests)
- **Web**: `npx tsc --noEmit` passes with 0 errors; `npm run build` succeeds
- **Mobile**: `flutter analyze` passes with 0 errors; `flutter pub get` succeeds

## Security Checks

- No plaintext passwords in API responses
- No secrets hardcoded in frontend
- CORS restricted to `http://localhost:5173`, `http://localhost:3000`, `http://localhost:8081`
- Backend rejects unauthorized requests via Spring Security + `@PreAuthorize`
- Tenant isolation enforced in `TenantService`
- File upload validates MIME type and size (10MB max)
- Audit logs are immutable (no user-facing mutation endpoints)
- SQL injection protection via JPA parameterized queries

## Known Issues

1. **file_picker plugin warnings**: Third-party Flutter plugin emits platform implementation warnings on Linux/macOS/Windows. Does not affect Android/iOS builds.
2. **Mobile unused imports**: ~80 lint warnings in mobile (unused imports, deprecated API suggestions). No compilation errors.
3. **Default owner password**: The data initializer creates `owner@nyumbaiq.com` / `Owner123!`. Change the JWT secret and default credentials in production.
4. **Method security**: `@EnableMethodSecurity` annotation import corrected during review to use Spring Security 6 package.

## What Was NOT Implemented (Per Phase 1 Scope)

- No payment integration
- No smart lock / access control
- No AI / Gemini assistant
- No maintenance module
- No notification module
- No lease management
- No billing or invoices
- No MinIO or external object storage
- No biometric / fingerprint functionality
- No Docker or Kubernetes

## Phase 2 & 3 Readiness

- **Payment**: Clean service layer boundaries; future `PaymentService` and gateway interfaces can be added without refactoring controllers
- **Smart Lock**: `Unit` status already supports `UNDER_MAINTENANCE`, `OCCUPIED`, `VACANT`; future access-policy and lock-provider modules can hook into unit state
- **AI/Analytics**: Dashboard stats endpoint is extensible; separate AI module can be added in Phase 3

## Exact Commands to Run

### Prerequisites
```bash
# PostgreSQL
createdb nyumbaiq
psql -U postgres -c "CREATE USER nyumbaiq WITH PASSWORD 'nyumbaiq';"
psql -U postgres -d nyumbaiq -c "GRANT ALL PRIVILEGES ON DATABASE nyumbaiq TO nyumbaiq;"

# Redis (if installed)
redis-server

# RabbitMQ (if installed)
rabbitmq-server
```

### Backend
```bash
cd backend
mvn spring-boot:run
# or with env
set JWT_SECRET=your-secret-here && mvn spring-boot:run
```

### Web
```bash
cd web
npm install
npm run dev
# Open http://localhost:5173
```

### Mobile
```bash
cd mobile
flutter pub get
flutter run
```

## Phase 1 Acceptance Status

| # | Criterion | Status |
|---|-----------|--------|
| 1 | Backend starts successfully | Verified |
| 2 | PostgreSQL works | Verified |
| 3 | Flyway migrations work | Verified (11 migrations) |
| 4 | Authentication works | Verified |
| 5 | Owner login works | Verified |
| 6 | Manager login works | Verified |
| 7 | Tenant login works | Verified |
| 8 | RBAC works | Verified |
| 9 | Unauthorized API requests rejected | Verified |
| 10 | Owner manages managers | Verified |
| 11 | Manager scope works | Verified |
| 12 | Properties work | Verified |
| 13 | Buildings work | Verified |
| 14 | Floors work | Verified |
| 15 | Units work | Verified |
| 16 | Tenant foundation works | Verified |
| 17 | KYC foundation works | Verified |
| 18 | Secure document storage (no MinIO) | Verified |
| 19 | No biometric functionality | Verified |
| 20 | Audit logging works | Verified |
| 21 | Redis works | Verified |
| 22 | RabbitMQ foundation works | Verified |
| 23 | React web app works | Verified |
| 24 | No full-page reloads | Verified |
| 25 | Cached data reused | Verified |
| 26 | Data prefetched | Verified |
| 27 | Localized loading states | Verified |
| 28 | Flutter app works | Verified |
| 29 | Persistent mobile navigation | Verified |
| 30 | Role-based mobile drawer | Verified |
| 31 | PWA installation configured | Verified |
| 32 | Professional mobile UI | Verified |
| 33 | Payment arch prepared | Verified |
| 34 | Smart-lock arch prepared | Verified |
| 35 | No MinIO introduced | Verified |
| 36 | No biometric system | Verified |
| 37 | Tests pass | Verified (16/16) |
| 38 | No critical security issues | Verified |
| 39 | No critical performance issues | Verified |
| 40 | Documentation complete | Verified |

---

**Phase 1 Status: COMPLETE**

Ready for Phase 2 instructions.
