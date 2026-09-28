@echo off
set JWT_SECRET=change-me-in-production-to-a-very-long-random-string-at-least-256-bits
set SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/nyumbaiq
set SPRING_DATASOURCE_USERNAME=nyumbaiq
set SPRING_DATASOURCE_PASSWORD=nyumbaiq
set SPRING_REDIS_HOST=localhost
set SPRING_REDIS_PORT=6379
set SPRING_RABBITMQ_HOST=localhost
set SPRING_RABBITMQ_PORT=5672
set SPRING_RABBITMQ_USERNAME=guest
set SPRING_RABBITMQ_PASSWORD=guest

cd backend
mvn spring-boot:run
