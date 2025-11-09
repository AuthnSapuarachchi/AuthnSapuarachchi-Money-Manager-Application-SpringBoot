@echo off
echo ========================================
echo JWT Configuration Verification
echo ========================================
echo.

echo Checking application.properties...
findstr /C:"jwt.secret" "src\main\resources\application.properties"
echo.

echo Checking if application is running on port 8081...
netstat -ano | findstr ":8081" | findstr "LISTENING"
if %errorlevel% equ 0 (
    echo WARNING: Application is already running on port 8081
    echo You need to restart it for changes to take effect!
) else (
    echo Port 8081 is FREE - You can start the application
)
echo.

echo ========================================
echo NEXT STEPS:
echo ========================================
echo 1. If app is running, RESTART IT:
echo    - Stop the current application
echo    - Run: .\mvnw.cmd spring-boot:run
echo.
echo 2. After restart, LOGIN AGAIN to get a NEW token
echo    Old tokens will NOT work!
echo.
echo 3. Use the NEW token in your requests
echo.
echo See JWT_SIGNATURE_FIX.md for detailed instructions
echo ========================================
pause

