# Final Security Report — NyumbaIQ

## 1. Authentication
- **JWT**: Access tokens (24h) + refresh tokens (7d)
- **Password Hashing**: BCrypt via Spring Security
- **Token Storage**: HTTP-only refresh tokens in database
- **Token Rotation**: Refresh tokens rotated on use
- **Status**: PASS

## 2. Authorization & RBAC
- Three roles: OWNER, MANAGER, TENANT
- Role-based route guards in both web and mobile
- `@PreAuthorize` on all sensitive endpoints
- Manager scope limited to assigned properties
- Tenant isolation enforced at service layer
- **Status**: PASS

## 3. AI Security
- AI tools never receive database credentials
- AI tools never receive raw SQL capability
- AI tools call application services only
- Role-aware context injected per request
- Tenant cannot access other tenants' data via AI
- Manager cannot access unassigned properties via AI
- Write actions require explicit user confirmation
- Dangerous actions (payments, refunds, lease termination) are not exposed as autonomous tools
- Input validation on all tool parameters
- Audit logging for AI tool actions
- **Status**: PASS

## 4. Data Protection
- KYC documents stored with secure filenames
- Path traversal prevention
- File type and size validation
- Unauthorized download prevention
- Sensitive data filtered from logs
- **Status**: PASS

## 5. API Security
- CORS configured for known origins
- CSRF disabled (stateless API)
- Rate limiting: configured at application layer
- SQL injection: prevented via JPA parameterized queries
- XSS: React/Tailwind auto-escaping + API response types
- Error handling: no stack traces exposed to clients
- **Status**: PASS

## 6. Webhook Security
- Payment webhooks: idempotency via provider_transaction_reference
- Mock provider for development
- **Status**: PARTIAL (real provider integration requires credentials)

## 7. Audit Logging
- AOP-based audit aspect on all controllers
- RabbitMQ async processing
- Events logged: login, KYC changes, tenant creation, lease actions, payments, AI actions
- AI actions include: user, role, tool, timestamp, target, result
- **Status**: PASS

## 8. Known Limitations
- Mock payment and smart-lock providers (no real hardware/credentials)
- Some pre-existing test failures due to Mockito/Java 21 compatibility
- CORS configured for localhost only (needs production origins)
