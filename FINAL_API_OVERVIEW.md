# Final API Overview — NyumbaIQ

**Base URL**: `/api/v1`

## Authentication
- `POST /auth/login` — Login with email/password
- `POST /auth/refresh` — Refresh access token
- `POST /auth/register` — Register new user
- `POST /auth/logout` — Logout (revoke refresh token)

## AI
- `POST /ai/chat` — Send message to AI assistant
- `POST /ai/tools/execute` — Execute confirmed AI tool
- `DELETE /ai/conversations/{id}` — Clear conversation

## Properties
- `GET /properties` — List properties
- `POST /properties` — Create property
- `GET /properties/{id}` — Get property
- `PUT /properties/{id}` — Update property
- `DELETE /properties/{id}` — Delete property

## Buildings
- `GET /properties/{propertyId}/buildings` — List buildings
- `POST /properties/{propertyId}/buildings` — Create building
- `GET /buildings/{id}` — Get building
- `PUT /buildings/{id}` — Update building
- `DELETE /buildings/{id}` — Delete building

## Floors
- `GET /buildings/{buildingId}/floors` — List floors
- `POST /buildings/{buildingId}/floors` — Create floor
- `GET /floors/{id}` — Get floor
- `PUT /floors/{id}` — Update floor
- `DELETE /floors/{id}` — Delete floor

## Units
- `GET /floors/{floorId}/units` — List units
- `POST /floors/{floorId}/units` — Create unit
- `GET /units/{id}` — Get unit
- `PUT /units/{id}` — Update unit
- `DELETE /units/{id}` — Delete unit

## Tenants
- `GET /tenants` — List tenants
- `POST /tenants` — Create tenant
- `GET /tenants/{id}` — Get tenant
- `PUT /tenants/{id}` — Update tenant
- `DELETE /tenants/{id}` — Delete tenant

## Managers
- `GET /managers` — List managers
- `POST /managers` — Create manager
- `PUT /managers/{id}` — Update manager
- `DELETE /managers/{id}` — Delete manager

## KYC
- `GET /kyc` — List KYC profiles
- `POST /kyc/upload` — Upload KYC document
- `GET /kyc/{id}` — Get KYC profile
- `GET /kyc/documents/{id}` — Get KYC document

## Leases
- `GET /leases` — List leases
- `POST /leases` — Create lease
- `GET /leases/{id}` — Get lease
- `PUT /leases/{id}` — Update lease
- `POST /leases/{id}/renew` — Renew lease
- `POST /leases/{id}/terminate` — Terminate lease

## Billing
- `GET /billing` — List billing schedules
- `POST /billing` — Create billing schedule
- `GET /billing/{id}` — Get billing schedule

## Invoices
- `GET /invoices` — List invoices
- `GET /invoices/{id}` — Get invoice

## Payments
- `POST /payments` — Create payment
- `GET /payments` — List payments
- `GET /payments/{id}` — Get payment
- `POST /payments/{id}/allocate` — Allocate payment
- `POST /payments/verify` — Verify payment (webhook)

## Receipts
- `GET /receipts` — List receipts
- `GET /receipts/{id}` — Get receipt
- `GET /receipts/{id}/pdf` — Download receipt PDF

## Statements
- `GET /statements` — List statements
- `POST /statements` — Generate statement
- `GET /statements/{id}` — Get statement

## Expenses
- `GET /expenses` — List expenses
- `POST /expenses` — Create expense
- `GET /expenses/{id}` — Get expense
- `PUT /expenses/{id}` — Update expense
- `DELETE /expenses/{id}` — Delete expense

## Maintenance
- `GET /maintenance` — List maintenance requests
- `POST /maintenance` — Create maintenance request
- `GET /maintenance/{id}` — Get maintenance request
- `PUT /maintenance/{id}` — Update maintenance request
- `POST /maintenance/{id}/assign` — Assign maintenance request
- `POST /maintenance/{id}/complete` — Complete maintenance request

## Extensions
- `GET /extensions` — List rental extensions
- `POST /extensions` — Request extension
- `POST /extensions/{id}/approve` — Approve extension
- `POST /extensions/{id}/reject` — Reject extension

## Smart Access
- `GET /smart-locks` — List smart locks
- `POST /smart-locks` — Create smart lock
- `GET /smart-locks/{id}` — Get smart lock
- `POST /smart-locks/{id}/lock` — Lock
- `POST /smart-locks/{id}/unlock` — Unlock
- `POST /smart-locks/{id}/credentials` — Create access credential
- `GET /smart-locks/{id}/credentials` — List credentials
- `POST /smart-locks/{id}/revoke-credential/{credentialId}` — Revoke credential
- `GET /smart-locks/{id}/events` — List access events
- `GET /smart-locks/{id}/access-policy` — Get access policy

## Notifications
- `GET /notifications` — List notifications
- `GET /notifications/unread-count` — Get unread count
- `POST /notifications/{id}/read` — Mark as read
- `POST /notifications/mark-all-read` — Mark all as read

## Reports
- `GET /reports/financial-summary` — Financial summary
- `GET /reports/occupancy` — Occupancy report
- `GET /reports/maintenance` — Maintenance report

## Dashboard
- `GET /dashboard` — Dashboard stats (role-aware)

## Audit Logs
- `GET /audit-logs` — List audit logs
- `GET /audit-logs/{id}` — Get audit log

## Users
- `GET /users/me` — Get current user
- `PUT /users/me` — Update current user
