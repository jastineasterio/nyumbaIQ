@echo off
echo NyumbaIQ Backend API Test
echo ==========================
echo.

echo Creating default owner user...
curl -s -X POST http://localhost:8080/api/v1/auth/login ^
  -H "Content-Type: application/json" ^
  -d "{\"email\":\"owner@nyumbaiq.com\",\"password\":\"Owner@123\"}"
echo.
echo.

echo If login fails, the default owner may not exist. Check backend logs.
pause
