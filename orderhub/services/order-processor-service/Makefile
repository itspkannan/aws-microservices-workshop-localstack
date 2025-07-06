.DEFAULT_GOAL := help

help: ## 📖 Show available commands
	@echo "\n🔧 Available commands for order-processor-service:"
	@grep -E '^[a-zA-Z_-]+:.*?## ' $(MAKEFILE_LIST) | awk 'BEGIN {FS=":.*?## "}; {printf "  \033[36m%-20s\033[0m %s\n", $order-processor-service, $}'

build: ## 🛠️ Build the service
	../../gradlew :services:order-processor-service:build

run: ## 🚀 Run the Spring Boot app
	../../gradlew :services:order-processor-service:bootRun

docker-build: ## 🐳 Build Docker image
	../../gradlew :services:order-processor-service:bootJar
	docker build -t order-processor-service:latest -f Dockerfile .
