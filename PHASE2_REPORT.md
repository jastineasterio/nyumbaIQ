# NyumbaIQ Phase 2 — Implementation Report

## Executive Summary

Phase 2 transforms NyumbaIQ from a property management foundation into a functional rental management system with billing, payments, finance, maintenance, extensions, smart access, and notifications.

## Database Migrations

| Migration | Description |
|-----------|-------------|
| V12__create_lease_and_billing_tables.sql | leases, billing_schedules, invoices, payments, payment_allocations, receipts, statements |
| V13__create_expenses_maintenance_and_extensions.sql | expenses, maintenance_requests, maintenance_costs, rental_extensions |
| V14__create_smart_access_and_notifications.sql | smart_locks, access_credentials, access_events, notifications |

## Domain Model

### Leases
- Lease → Tenant, Unit, Property, Building, Floor
- Supports renewal chain via previous_lease_id
- Statuses: DRAFT, PENDING_APPROVAL, ACTIVE, EXPIRING, EXPIRED, RENEWED, TERMINATED, CANCELLED
- Billing frequencies: WEEKLY, MONTHLY, QUARTERLY, SEMI_ANNUALLY

### Billing & Invoices
- BillingSchedule → Lease, Tenant, Unit, Property
- Invoice → BillingSchedule, Tenant, Lease, Property, Unit
- Invoice statuses: UNPAID, PARTIALLY_PAID, PAID, OVERDUE, CANCELLED
- Idempotent billing generation: UNIQUE(lease_id, period_start, period_end)

### Payments
- Payment → Tenant, Lease, Property
- PaymentAllocation → Payment, Invoice, Tenant
- UNIQUE(payment_id, invoice_id) prevents duplicate allocation
- Payment statuses: PENDING, PROCESSING, SUCCESS, FAILED, CANCELLED, REVERSED

### Receipts & Statements
- Receipt → Payment, Tenant, Property, Unit
- Statement → Tenant, Property, Unit

### Finance
- Expense → Property, Building, Unit, User
- Expense statuses: DRAFT, SUBMITTED, APPROVED, PAID, CANCELLED

### Maintenance
- MaintenanceRequest → Tenant, Property, Unit
- MaintenanceCost → MaintenanceRequest, Property, Unit
- Priorities: LOW, MEDIUM, HIGH, URGENT
- Statuses: SUBMITTED, REVIEWING, ASSIGNED, IN_PROGRESS, ON_HOLD, COMPLETED, CLOSED, REJECTED

### Extensions
- RentalExtension → Tenant, Lease, Property, Unit
- Statuses: PENDING, APPROVED, REJECTED, EXPIRED, CANCELLED

### Smart Access
- SmartLock → Property, Building, Unit
- AccessCredential → Tenant, SmartLock, Property, Unit
- AccessEvent → SmartLock, Tenant, Credential, Property, Unit
- Credential types: PIN, DIGITAL, MOBILE, TEMPORARY
- Credential statuses: ACTIVE, EXPIRED, REVOKED, SUSPENDED

### Notifications
- Notification → User, Tenant, Property
- Channels: IN_APP, EMAIL, SMS, PUSH

## Payment Architecture

### Provider Abstraction
- `PaymentProvider` interface in `com.nyumbaiq.backend.payment`
- `MockPaymentProvider` for sandbox/testing
- `PaymentProviderFactory` for provider resolution
- Configuration via environment variables
- Credentials NEVER exposed to frontend

### Payment Flow
1. Tenant initiates payment via backend
2. Backend calls PaymentProvider
3. Provider processes with mobile money network
4. Provider sends callback/webhook to backend
5. Backend verifies callback signature/idempotency
6. Backend marks payment SUCCESS
7. Backend allocates payment to invoices
8. Backend generates receipt
9. Backend sends notification to tenant

### Webhook Security
- Signature verification where provider supports it
- Idempotency via provider_transaction_reference uniqueness
- Duplicate callback protection

## Smart Lock Architecture

### Provider Abstraction
- `SmartLockProvider` interface in `com.nyumbaiq.backend.smartlock`
- `MockSmartLockProvider` simulator for testing
- `SmartLockProviderFactory` for provider resolution

### Access Policy Engine
- `AccessPolicyEngine` evaluates: lease status, payment status, overdue period, grace period, approved extension, emergency override, maintenance override
- Access states: ALLOWED, RESTRICTED, SUSPENDED, EMERGENCY_ACCESS, MAINTENANCE_ACCESS
- Does NOT auto-lock simply because balance > 0
- Every override is logged

## RBAC Summary

| Role | Leases | Billing | Payments | Expenses | Maintenance | Extensions | Smart Access | Reports | Notifications |
|------|--------|---------|----------|----------|-------------|------------|--------------|---------|---------------|
| OWNER | Full | Full | Full | Full | Full | Full | Full | Full | Full |
| MANAGER | Scoped | Scoped | Scoped | Scoped | Scoped | Scoped | Scoped | Scoped | Scoped |
| TENANT | Own only | Own only | Own only | None | Own only | Own only | Own status | None | Own only |

## Web Application

### New Routes
- /leases, /leases/:id
- /billing
- /invoices, /invoices/:id
- /payments
- /receipts, /receipts/:id
- /statements, /statements/:id
- /expenses, /expenses/:id
- /maintenance, /maintenance/:id
- /extensions, /extensions/:id
- /smart-access, /smart-access/:id
- /reports
- /notifications

### Tenant Home Enhancement
- Rent balance card
- Due date display
- Payment status
- Quick actions: Pay Rent, Statement, Maintenance, Access Status
- Recent payment summary

## Mobile Application

### New Screens
- leases_list_screen.dart, lease_detail_screen.dart
- billing_screen.dart
- invoices_list_screen.dart, invoice_detail_screen.dart
- payments_list_screen.dart, payment_detail_screen.dart
- receipts_list_screen.dart
- statements_list_screen.dart, statement_detail_screen.dart
- expenses_list_screen.dart, expense_form_screen.dart
- maintenance_list_screen.dart, maintenance_form_screen.dart, maintenance_detail_screen.dart
- extensions_list_screen.dart, extension_form_screen.dart
- smart_access_screen.dart, lock_detail_screen.dart
- notifications_screen.dart
- reports_screen.dart, financial_summary_screen.dart

### Drawer Updates
- Role-based Phase 2 menu items

## Testing

### Backend Tests
- LeaseServiceTest: create, update, renew, approve, terminate, authorization
- PaymentServiceTest: initiate, allocate, partial payment, full payment, overpayment, duplicate prevention
- BillingServiceTest: generate charges, prevent duplicates, idempotency
- MaintenanceServiceTest: create, assign, status workflow
- ExtensionServiceTest: request, approve, reject
- SmartLockServiceTest: mock provider, credentials, access policy

### Frontend Tests
- Route protection
- Component rendering
- API integration

### Mobile Tests
- Navigation
- API client
- Auth flow

## Performance

- Backend: pagination on all lists, count queries for dashboard, indexed foreign keys
- Web: TanStack Query caching, skeleton loaders, no full-page reloads
- Mobile: Provider state management, local caching, persistent navigation

## Security

- Payment secrets remain server-side
- Smart lock credentials remain server-side
- Webhook signature verification
- Tenant isolation enforced
- Manager property scope enforced
- All critical actions audited

## Environment Variables

```
# Payment
PAYMENT_PROVIDER=mock
PAYMENT_API_URL=
PAYMENT_API_KEY=
PAYMENT_SECRET=
PAYMENT_CALLBACK_URL=

# Smart Lock
SMART_LOCK_PROVIDER=mock
SMART_LOCK_API_URL=
SMART_LOCK_API_KEY=
SMART_LOCK_SECRET=
```

## Commands

### Backend
```bash
cd backend
mvn spring-boot:run
```

### Web
```bash
cd web
npm install
npm run dev
```

### Mobile
```bash
cd mobile
flutter pub get
flutter run
```

## Known Issues

- Mobile file_picker plugin warnings on desktop platforms
- Mock payment provider is for development only
- Mock smart lock provider is for testing only

## Phase 2 Acceptance

| # | Criterion | Status |
|---|-----------|--------|
| 1 | Lease creation works | Pending |
| 2 | Lease activation works | Pending |
| 3 | Lease renewal works | Pending |
| 4 | Lease history works | Pending |
| 5 | Lease expiry works | Pending |
| 6 | Billing schedules work | Pending |
| 7 | Rent charges generated | Pending |
| 8 | Duplicate charges prevented | Pending |
| 9 | Invoice status works | Pending |
| 10 | Overdue status works | Pending |
| 11 | Payment records work | Pending |
| 12 | Partial payments work | Pending |
| 13 | Full payments work | Pending |
| 14 | Payment allocation works | Pending |
| 15 | Overpayments handled | Pending |
| 16 | Mobile-money architecture works | Pending |
| 17 | Provider abstraction works | Pending |
| 18 | Mock/sandbox provider works | Pending |
| 19 | Webhook handling works | Pending |
| 20 | Duplicate callbacks prevented | Pending |
| 21 | Payment verification works | Pending |
| 22 | Receipts work | Pending |
| 23 | Tenant statements work | Pending |
| 24 | Income works | Pending |
| 25 | Expenses work | Pending |
| 26 | Expense approval works | Pending |
| 27 | Financial reports work | Pending |
| 28 | Outstanding rent reports work | Pending |
| 29 | Revenue reports work | Pending |
| 30 | Rental extensions work | Pending |
| 31 | Extension approval works | Pending |
| 32 | Extension expiry works | Pending |
| 33 | Tenant can create maintenance request | Pending |
| 34 | Manager can process requests | Pending |
| 35 | Assignment works | Pending |
| 36 | Status workflow works | Pending |
| 37 | Maintenance costs work | Pending |
| 38 | Smart lock abstraction works | Pending |
| 39 | Mock smart lock provider works | Pending |
| 40 | Access credentials work | Pending |
| 41 | Access events work | Pending |
| 42 | Access policy engine works | Pending |
| 43 | Grace period considered | Pending |
| 44 | Approved extensions considered | Pending |
## Current Implementation Status

### Completed
- Database migrations V12-V14 created
- Phase 2 entities and enums implemented
- Phase 2 repositories implemented
- Backend services implemented: Lease, Billing, Invoice, Payment, Receipt, Statement, Expense, Maintenance, Extension, SmartLock, AccessPolicy, Notification, Report
- Backend controllers implemented for all Phase 2 modules
- Payment provider abstraction: `PaymentProvider`, `MockPaymentProvider`, `PaymentProviderFactory`
- Smart lock provider abstraction: `SmartLockProvider`, `MockSmartLockProvider`, `SmartLockProviderFactory`
- Access policy engine implemented
- React web UI extended with Phase 2 pages, routes, API clients, and role-based navigation
- Flutter mobile UI extended with Phase 2 screens, providers, routes, and drawer updates

### Verified
- Backend compiles successfully (`mvn compile`)
- Backend tests pass: 36/36 tests
- Web TypeScript compiles with 0 errors (`npx tsc --noEmit`)
- Mobile Flutter analysis passes with 0 errors (`flutter analyze`)

### Pending Verification
- End-to-end integration testing with running backend
- Web production build verification
- Mobile app runtime testing on device/emulator
- Payment webhook simulation testing
- Smart lock mock provider behavior testing
- Full security audit of new endpoints
- Performance testing under load

## Phase 2 Acceptance Criteria Status

| # | Criterion | Status |
|---|-----------|--------|
| 1 | Lease creation works | Implemented |
| 2 | Lease activation works | Implemented |
| 3 | Lease renewal works | Implemented |
| 4 | Lease history works | Implemented |
| 5 | Lease expiry works | Implemented |
| 6 | Billing schedules work | Implemented |
| 7 | Rent charges generated | Implemented |
| 8 | Duplicate charges prevented | Implemented |
| 9 | Invoice status works | Implemented |
| 10 | Overdue status works | Implemented |
| 11 | Payment records work | Implemented |
| 12 | Partial payments work | Implemented |
| 13 | Full payments work | Implemented |
| 14 | Payment allocation works | Implemented |
| 15 | Overpayments handled | Implemented |
| 16 | Mobile-money architecture works | Implemented |
| 17 | Provider abstraction works | Implemented |
| 18 | Mock/sandbox provider works | Implemented |
| 19 | Webhook handling works | Implemented |
| 20 | Duplicate callbacks prevented | Implemented |
| 21 | Payment verification works | Implemented |
| 22 | Receipts work | Implemented |
| 23 | Tenant statements work | Implemented |
| 24 | Income works | Implemented |
| 25 | Expenses work | Implemented |
| 26 | Expense approval works | Implemented |
| 27 | Financial reports work | Implemented |
| 28 | Outstanding rent reports work | Implemented |
| 29 | Revenue reports work | Implemented |
| 30 | Rental extensions work | Implemented |
| 31 | Extension approval works | Implemented |
| 32 | Extension expiry works | Implemented |
| 33 | Tenant can create maintenance request | Implemented |
| 34 | Manager can process requests | Implemented |
| 35 | Assignment works | Implemented |
| 36 | Status workflow works | Implemented |
| 37 | Maintenance costs work | Implemented |
| 38 | Smart lock abstraction works | Implemented |
| 39 | Mock smart lock provider works | Implemented |
| 40 | Access credentials work | Implemented |
| 41 | Access events work | Implemented |
| 42 | Access policy engine works | Implemented |
| 43 | Grace period considered | Implemented |
| 44 | Approved extensions considered | Implemented |
| 45 | Manual authorized override works | Implemented |
| 46 | Smart-lock credentials not exposed to frontend | Implemented |
| 47 | All access actions audited | Implemented |
| 48 | Important events generate notifications | Implemented |
| 49 | RabbitMQ used where appropriate | Implemented |
| 50 | Notification processing non-blocking | Implemented |
| 51 | Web navigation remains SPA | Implemented |
| 52 | No full-page reloads | Implemented |
| 53 | Cached data reused | Implemented |
| 54 | Prefetching works | Implemented |
| 55 | Mobile navigation persistent | Implemented |
| 56 | No unnecessary API requests | Implemented |
| 57 | Tenant isolation works | Implemented |
| 58 | Manager property scope works | Implemented |
| 59 | Payment secrets server-side | Implemented |
| 60 | Smart-lock secrets server-side | Implemented |
| 61 | Financial operations authorized | Implemented |
| 62 | Critical actions audited | Implemented |
| 63 | Backend tests pass | Verified: 36/36 |
| 64 | Web build passes | Pending verification |
| 65 | TypeScript checks pass | Verified: 0 errors |
| 66 | Flutter analyze passes | Verified: 0 errors |
| 67 | Mobile tests pass | Pending verification |
| 68 | No critical security issues | Pending full audit |
| 69 | No critical performance issues | Pending testing |

---

**Phase 2 Status: CODE COMPLETE — VERIFICATION IN PROGRESS**

Next steps:
1. Run backend server and test endpoints manually
2. Run web app and verify Phase 2 UI flows
3. Run mobile app and verify Phase 2 screens
4. Perform security audit of new endpoints
5. Run performance tests
6. Update report with final verification results
