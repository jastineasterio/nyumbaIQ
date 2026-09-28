@echo off
echo ============================================
echo NyumbaIQ Phase 1 Verification
echo ============================================
echo.

echo [1/6] Checking backend compilation...
cd backend
call mvn compile -q
if %errorlevel% neq 0 (
    echo FAIL: Backend compilation failed
    pause
    exit /b 1
)
echo PASS: Backend compiles successfully
echo.

echo [2/6] Checking frontend TypeScript...
cd ..\web
call npm install --silent 2>nul
if %errorlevel% neq 0 (
    echo FAIL: npm install failed
    pause
    exit /b 1
)
call npx tsc --noEmit
if %errorlevel% neq 0 (
    echo FAIL: TypeScript check failed
    pause
    exit /b 1
)
echo PASS: Frontend TypeScript compiles
echo.

echo [3/6] Checking mobile analysis...
cd ..\mobile
call flutter pub get
if %errorlevel% neq 0 (
    echo FAIL: flutter pub get failed
    pause
    exit /b 1
)
call flutter analyze
if %errorlevel% neq 0 (
    echo FAIL: flutter analyze failed
    pause
    exit /b 1
)
echo PASS: Mobile analysis passes
echo.

echo [4/6] Running backend tests...
cd ..\backend
call mvn test -q
if %errorlevel% neq 0 (
    echo FAIL: Backend tests failed
    pause
    exit /b 1
)
echo PASS: Backend tests pass
echo.

echo ============================================
echo All checks passed!
echo ============================================
pause
