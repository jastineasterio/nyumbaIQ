@echo off
REM NyumbaIQ Database Setup Script
REM Requires PostgreSQL to be installed and running

echo Creating NyumbaIQ database...

psql -U postgres -c "DROP DATABASE IF EXISTS nyumbaiq;" 2>nul
psql -U postgres -c "DROP USER IF EXISTS nyumbaiq;" 2>nul
psql -U postgres -c "CREATE USER nyumbaiq WITH PASSWORD 'nyumbaiq';"
psql -U postgres -c "CREATE DATABASE nyumbaiq OWNER nyumbaiq;"
psql -U postgres -d nyumbaiq -c "GRANT ALL PRIVILEGES ON DATABASE nyumbaiq TO nyumbaiq;"

echo.
echo Database setup complete.
echo Run backend with: cd backend ^& mvn spring-boot:run
pause
