@echo off
echo ==========================================
echo     HRMS Database Setup for Team
echo ==========================================
echo.

cd /d "%~dp0"

echo [1/4] Checking project structure...
if not exist "src\main\resources\" (
    echo ERROR: Not in HRMS project directory!
    echo Please run this script from HRMS folder
    pause
    exit /b 1
)
echo ✓ Project structure OK

echo.
echo [2/4] Creating database configuration...
if not exist "src\main\resources\db.properties" (
    echo Creating db.properties for team...
    (
        echo # MySQL Database Configuration for Team Members
        echo db.driver=com.mysql.cj.jdbc.Driver
        echo db.url=jdbc:mysql://36.50.135.207:3306/hrmsv2?useSSL=false^&serverTimezone=Asia/Ho_Chi_Minh^&allowPublicKeyRetrieval=true^&useUnicode=true^&characterEncoding=UTF-8
        echo db.username=sa
        echo db.password=sa
        echo db.charset=utf8mb4
        echo.
        echo # Connection Pool Settings ^(HikariCP^)
        echo db.pool.maximum=20
        echo db.pool.minimum=5
        echo db.pool.timeout=30000
        echo db.pool.idle=600000
        echo db.pool.lifetime=1800000
        echo db.pool.connection.test.query=SELECT 1
    ) > "src\main\resources\db.properties"
    echo ✓ Created db.properties with team credentials ^(sa/sa^)
) else (
    echo ✓ db.properties already exists
)

echo.
echo [3/4] Testing Maven build...
call mvn clean compile -q
if %ERRORLEVEL% equ 0 (
    echo ✓ Maven build successful
) else (
    echo ✗ Maven build failed
    echo Please check your Maven installation and internet connection
    pause
    exit /b 1
)

echo.
echo [4/4] Building WAR file...
call mvn package -DskipTests -q
if %ERRORLEVEL% equ 0 (
    echo ✓ WAR file created successfully
    echo ✓ File location: target\HRMS-1.0-SNAPSHOT.war
) else (
    echo ✗ WAR packaging failed
    pause
    exit /b 1
)

echo.
echo ==========================================
echo        ✓ SETUP COMPLETED SUCCESSFULLY!
echo ==========================================
echo.
echo 🚀 Next steps:
echo    1. Deploy via NetBeans IDE
echo    2. Or copy target\HRMS-1.0-SNAPSHOT.war to Tomcat webapps
echo    3. Test connection: http://localhost:8080/HRMS/test-db
echo.
echo 🔑 Database credentials:
echo    Server: 36.50.135.207:3306
echo    Database: hrmsv2
echo    Username: sa
echo    Password: sa
echo.
echo 📝 Note: Use /test-db endpoint to verify database connection
echo.
pause