# Phase 2 Integration Checklist

## Backend Integration

### Controllers
- [ ] All controllers registered with correct @RequestMapping
- [ ] All endpoints have @PreAuthorize with correct role rules
- [ ] No duplicate endpoint mappings
- [ ] No missing import statements

### Services
- [ ] All services wired with @Service
- [ ] All repositories injected via constructor
- [ ] CurrentUser used for auth context
- [ ] BigDecimal used for all money fields
- [ ] Transactions managed correctly

### DTOs
- [ ] All DTOs have @Data @AllArgsConstructor
- [ ] Request DTOs use jakarta.validation
- [ ] No entity classes exposed directly from controllers

### Security
- [ ] Tenant isolation enforced in services
- [ ] Manager scope enforced in services
- [ ] Payment webhook endpoint has no auth but signature verification
- [ ] No secrets exposed in responses

### Database
- [ ] All Flyway migrations run in order
- [ ] All foreign keys reference correct tables
- [ ] All unique constraints prevent duplicates
- [ ] All indexes on frequently queried columns

### Events
- [ ] RabbitMQ queues declared for notifications
- [ ] Audit events published for critical operations
- [ ] Listeners handle events asynchronously

## Web Integration

### Routes
- [ ] All Phase 2 routes added to router
- [ ] Role guards applied correctly
- [ ] No duplicate routes
- [ ] NotFound page catches unmatched routes

### API Clients
- [ ] All API methods return correct types
- [ ] Error handling consistent
- [ ] No hardcoded credentials

### Components
- [ ] All pages use TanStack Query
- [ ] Skeleton loaders for loading states
- [ ] EmptyState for empty data
- [ ] ErrorState with retry for errors

### Navigation
- [ ] Sidebar links updated for roles
- [ ] No window.location.href usage
- [ ] Active link highlighting works

## Mobile Integration

### Routes
- [ ] All Phase 2 routes added to GoRouter
- [ ] Auth guard works
- [ ] Role-based redirection works

### Providers
- [ ] All providers initialized
- [ ] API client methods added
- [ ] Error handling consistent

### Screens
- [ ] All screens use Consumer/Provider correctly
- [ ] Loading states shown
- [ ] Empty states shown
- [ ] Error states shown

### Drawer
- [ ] Role-based items added
- [ ] Bottom nav updated for roles

## Testing

### Backend
- [ ] mvn compile passes
- [ ] mvn test passes
- [ ] All new services have unit tests
- [ ] Payment scenarios tested
- [ ] Smart access scenarios tested

### Web
- [ ] npx tsc --noEmit passes
- [ ] npm run build passes
- [ ] No console errors in production build

### Mobile
- [ ] flutter pub get succeeds
- [ ] flutter analyze passes
- [ ] No compilation errors

## Performance

### Backend
- [ ] No N+1 queries in new endpoints
- [ ] Pagination on all list endpoints
- [ ] Indexes on foreign keys

### Web
- [ ] TanStack Query stale times configured
- [ ] No unnecessary re-renders
- [ ] Skeleton loaders used

### Mobile
- [ ] Provider rebuilds minimized
- [ ] Local caching used
- [ ] No unnecessary API calls

## Security

- [ ] No payment credentials in frontend/mobile
- [ ] No smart-lock credentials in frontend/mobile
- [ ] All financial operations require authentication
- [ ] Tenant isolation verified
- [ ] Manager scope verified
- [ ] Webhook signatures verified
- [ ] Audit logs created for critical actions
