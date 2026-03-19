@echo off
echo ================================================================
echo   RevWorkforce - Starting All Microservices
echo ================================================================
echo.
echo PREREQUISITE: Make sure Redis is running on port 6379
echo   Option 1 - Docker: docker run -d -p 6379:6379 redis:alpine
echo   Option 2 - Download: https://github.com/microsoftarchive/redis/releases
echo.
pause

echo [1/7] Starting Eureka Server (port 8761)...
start "Eureka Server" cmd /k "cd eureka-server && mvnw.cmd spring-boot:run"
timeout /t 25 /nobreak

echo [2/7] Starting Config Server (port 8888)...
start "Config Server" cmd /k "cd config-server && mvnw.cmd spring-boot:run"
timeout /t 20 /nobreak

echo [3/7] Starting Auth Service (port 8081)...
start "Auth Service" cmd /k "cd auth-service && mvnw.cmd spring-boot:run"
timeout /t 8 /nobreak

echo [4/7] Starting Employee Service (port 8082)...
start "Employee Service" cmd /k "cd employee-service && mvnw.cmd spring-boot:run"
timeout /t 8 /nobreak

echo [5/7] Starting Leave Service (port 8083)...
start "Leave Service" cmd /k "cd leave-service && mvnw.cmd spring-boot:run"
timeout /t 8 /nobreak

echo [6/7] Starting Performance Service (port 8084)...
start "Performance Service" cmd /k "cd performance-service && mvnw.cmd spring-boot:run"
timeout /t 8 /nobreak

echo [7/7] Starting API Gateway (port 8080)...
start "API Gateway" cmd /k "cd api-gateway && mvnw.cmd spring-boot:run"

echo.
echo ================================================================
echo   All 7 services started!
echo ================================================================
echo   Eureka Dashboard : http://localhost:8761
echo   Config Server    : http://localhost:8888
echo   API Gateway      : http://localhost:8080
echo   Angular Frontend : http://localhost:4200  (run ng serve separately)
echo ================================================================
pause
