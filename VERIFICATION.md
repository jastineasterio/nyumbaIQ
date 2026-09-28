# NyumbaIQ Phase 1 - Verification Checklist

## Backend Verification
- [ ] `cd backend && mvn compile` succeeds
- [ ] `cd backend && mvn test` passes
- [ ] Backend starts on port 8080
- [ ] Flyway migrations run successfully
- [ ] Database tables created: users, properties, buildings, floors, units, tenants, manager_assignments, kyc_profiles, kyc_documents, refresh_tokens, audit_logs
- [ ] `/api/v1/auth/login` returns JWT tokens
- [ ] Owner can login and access `/api/v1/dashboard/stats`
- [ ] Manager can login and access assigned properties
- [ ] Tenant can login and access own data
- [ ] RBAC blocks unauthorized access
- [ ] Redis caching works for dashboard stats
- [ ] RabbitMQ exchange/queues are declared
- [ ] Audit logs are recorded for CRUD operations
- [ ] File upload to `/api/v1/kyc/tenant/{id}/documents` works
- [ ] File download endpoint works
- [ ] Password hashing is BCrypt
- [ ] No plaintext passwords in responses
- [ ] Refresh token rotation works

## Web Verification
- [ ] `cd web && npm install` succeeds
- [ ] `npx tsc --noEmit` passes
- [ ] `npm run dev` starts on port 5173
- [ ] Login page renders
- [ ] Dashboard loads with real stats
- [ ] Navigation between pages does NOT reload the page
- [ ] Properties list loads with pagination
- [ ] Property creation works
- [ ] Buildings list works
- [ ] Floors list works
- [ ] Units list works
- [ ] Tenants list works
- [ ] Managers list works (owner only)
- [ ] KYC upload works
- [ ] Audit logs list works
- [ ] Role-based sidebar items render correctly
- [ ] Cached data is reused on navigation
- [ ] Skeleton loaders show during data fetch
- [ ] PWA manifest is valid
- [ ] Service worker registers
- [ ] Install banner appears (browser support permitting)
- [ ] Logout clears auth state
- [ ] Unauthorized access redirects to login

## Mobile Verification
- [ ] `cd mobile && flutter pub get` succeeds
- [ ] `flutter analyze` passes
- [ ] App launches on emulator/device
- [ ] Login screen works
- [ ] Dashboard shows stats
- [ ] Properties list loads
- [ ] Buildings list loads (scoped)
- [ ] Floors list loads
- [ ] Units list loads
- [ ] Tenants list loads
- [ ] Profile screen works
- [ ] Drawer menu shows role-based items
- [ ] Persistent bottom navigation works (where applicable)
- [ ] Auth state persists across app restarts
- [ ] Logout works

## Security Verification
- [ ] No plaintext passwords in API responses
- [ ] No JWT secret in frontend code
- [ ] No database credentials in frontend
- [ ] CORS configured correctly
- [ ] Unauthorized API requests return 401/403
- [ ] Tenant cannot access another tenant's data
- [ ] Manager cannot access unassigned properties
- [ ] File upload validates type and size
- [ ] Audit logs cannot be modified by users
- [ ] SQL injection protection (JPA parameterized queries)
- [ ] XSS protection (React escaping, backend validation)

## Performance Verification
- [ ] Dashboard loads under 2 seconds with cache
- [ ] Property list paginates correctly
- [ ] No N+1 queries in list endpoints
- [ ] TanStack Query caches and deduplicates requests
- [ ] Redis returns cached dashboard stats
- [ ] No unnecessary full-page reloads in web app
- [ ] Web navigation feels instant (cached data)

## Architecture Verification
- [ ] No MinIO used
- [ ] No biometric/fingerprint code exists
- [ ] No payment gateway code exists
- [ ] No smart lock code exists
- [ ] No AI/Gemini code exists
- [ ] No maintenance module code exists
- [ ] No notification module code exists
- [ ] Payment interfaces prepared for Phase 2
- [ ] Smart lock interfaces prepared for Phase 2
