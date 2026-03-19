#!/bin/bash
echo "================================================================"
echo "  RevWorkforce - Starting All Microservices"
echo "================================================================"
echo ""
echo "PREREQUISITE: Redis must be running on port 6379"
echo "  Run: docker run -d -p 6379:6379 redis:alpine"
echo ""

cd "$(dirname "$0")"

start_service() {
  local name=$1
  local dir=$2
  echo "Starting $name..."
  cd $dir && ./mvnw spring-boot:run &
  cd ..
}

start_service "Eureka Server" eureka-server
echo "Waiting 25s for Eureka..."
sleep 25

start_service "Config Server" config-server
echo "Waiting 20s for Config Server..."
sleep 20

start_service "Auth Service" auth-service
sleep 8

start_service "Employee Service" employee-service
sleep 8

start_service "Leave Service" leave-service
sleep 8

start_service "Performance Service" performance-service
sleep 8

start_service "API Gateway" api-gateway

echo ""
echo "================================================================"
echo "  All services started!"
echo "  Eureka Dashboard : http://localhost:8761"
echo "  Config Server    : http://localhost:8888"
echo "  API Gateway      : http://localhost:8080"
echo "================================================================"
wait
