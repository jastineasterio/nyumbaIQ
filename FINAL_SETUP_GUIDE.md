# Final Setup Guide — NyumbaIQ

## Prerequisites
- Java 21+
- Maven 3.8+
- Node.js 18+
- PostgreSQL 14+
- Redis
- RabbitMQ
- Flutter SDK 3.x (for mobile)

## Environment Variables

### Backend (.env)
```env
# Database
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/nyumbaiq
SPRING_DATASOURCE_USERNAME=nyumbaiq
SPRING_DATASOURCE_PASSWORD=nyumbaiq

# JWT
JWT_SECRET=your-very-long-random-secret-key-at-least-256-bits

# Redis
SPRING_REDIS_HOST=localhost
SPRING_REDIS_PORT=6379

# RabbitMQ
SPRING_RABBITMQ_HOST=localhost
SPRING_RABBITMQ_PORT=5672
SPRING_RABBITMQ_USERNAME=guest
SPRING_RABBITMQ_PASSWORD=guest

# Gemini AI (optional)
GEMINI_API_KEY=your-gemini-api-key

# CORS
APP_CORS_ALLOWED_ORIGINS=http://localhost:5173,http://localhost:3000
```

### Web (.env)
```env
VITE_API_URL=http://localhost:8080/api/v1
```

## Installation

### Backend
```bash
cd backend
cp .env.example .env
# Edit .env with your values
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

## Seeded Data
On first startup, the following accounts are created:
- OWNER: owner@nyumbaiq.com / Owner123!
- MANAGER: manager@nyumbaiq.com / Manager123!
- TENANT: tenant@nyumbaiq.com / Tenant123!

## Default Ports
- Backend: http://localhost:8080/api/v1
- Web: http://localhost:5173
- PostgreSQL: localhost:5432
- Redis: localhost:6379
- RabbitMQ: localhost:5672
