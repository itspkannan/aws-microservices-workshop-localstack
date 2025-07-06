#!/bin/bash

set -e

if [[ -z "$1" ]]; then
  echo "❌ Service name is required"
  echo "Usage: ./add.service.sh <service-name>"
  exit 1
fi

SERVICE_NAME="$1"
SERVICE_DIR="services/$SERVICE_NAME"

if [[ ! -f .base_package ]]; then
  echo "❌ Base package not found. Please create your monorepo using create.monorepo.sh"
  exit 1
fi

PACKAGE_NAME=$(cat .base_package)
PACKAGE_PATH="${PACKAGE_NAME//.//}/$SERVICE_NAME"

# Create directories
mkdir -p "$SERVICE_DIR/src/main/java/$PACKAGE_PATH"
mkdir -p "$SERVICE_DIR/src/test/java/$PACKAGE_PATH"

# Update settings.gradle
if ! grep -q "include 'services:$SERVICE_NAME'" settings.gradle; then
  echo "include 'services:$SERVICE_NAME'" >> settings.gradle
fi

# Create service build.gradle
cat <<GRADLE > "$SERVICE_DIR/build.gradle"
apply plugin: 'java'
apply plugin: 'org.springframework.boot'

dependencies {
  implementation 'org.springframework.boot:spring-boot-starter-web'
  implementation project(':commons:shared-lib')

  testImplementation 'org.junit.jupiter:junit-jupiter:5.10.2'
  testImplementation 'org.mockito:mockito-core:5.12.0'
}
GRADLE

# Create main application class
cat <<APP > "$SERVICE_DIR/src/main/java/$PACKAGE_PATH/Application.java"
package ${PACKAGE_NAME}.${SERVICE_NAME};

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {
  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }
}
APP

# Create Dockerfile
cat <<DOCKER > "$SERVICE_DIR/Dockerfile"
FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
DOCKER

# Create service Makefile
cat <<SERVICE_MAKE > "$SERVICE_DIR/Makefile"
.DEFAULT_GOAL := help

help: ## 📖 Show available commands
	@echo "\n🔧 Available commands for $SERVICE_NAME:"
	@grep -E '^[a-zA-Z_-]+:.*?## ' \$(MAKEFILE_LIST) | awk 'BEGIN {FS=":.*?## "}; {printf "  \033[36m%-20s\033[0m %s\n", \$$1, \$$2}'

build: ## 🛠️ Build the service
	../../gradlew :services:$SERVICE_NAME:build

run: ## 🚀 Run the Spring Boot app
	../../gradlew :services:$SERVICE_NAME:bootRun

docker-build: ## 🐳 Build Docker image
	../../gradlew :services:$SERVICE_NAME:bootJar
	docker build -t $SERVICE_NAME:latest -f Dockerfile .
SERVICE_MAKE

echo "✅ Service '$SERVICE_NAME' created in $SERVICE_DIR"
