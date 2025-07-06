.DEFAULT_GOAL := help

help: ## 📖 Show available commands
	@echo "\n🔧 Available commands for order-service-api:"
	@grep -E '^[a-zA-Z_-]+:.*?## ' $(MAKEFILE_LIST) | awk 'BEGIN {FS=":.*?## "}; {printf "  \033[36m%-20s\033[0m %s\n", $order-service-api, $}'

build: ## 🛠️ Build the service
	../../gradlew :services:order-service-api:build

run: ## 🚀 Run the Spring Boot app
	../../gradlew :services:order-service-api:bootRun

docker-build: ## 🐳 Build Docker image
	../../gradlew :services:order-service-api:bootJar
	docker build -t order-service-api:latest -f Dockerfile .
