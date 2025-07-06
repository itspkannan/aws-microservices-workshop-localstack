.DEFAULT_GOAL := help
ROOT_DIR := $(realpath $(dir $(lastword $(MAKEFILE_LIST)))/../..)
GRADLE_CMD = $(ROOT_DIR)/gradlew

help: ## 📖 Show available commands
	@echo "\033[1;33mAvailable commands for notification-service:\033[0m" && \
	awk -F ':.*?## ' '/^[a-zA-Z0-9_.-]+:.*## / { \
		cmds[$$1] = $$2; \
		if (length($$1) > max_len) max_len = length($$1); \
	} \
	END { \
		for (cmd in cmds) { \
			printf "  \033[36m%-" max_len "s\033[0m %s\n", cmd, cmds[cmd]; \
		} \
	}' $(MAKEFILE_LIST) | sort
	@echo ""

build: ## 🛠️ Build the service
	$(GRADLE_CMD) :services:notification-service:build

run: ## 🚀 Run the Spring Boot app
	$(GRADLE_CMD) :services:notification-service:bootRun

docker-build: ## 🐳 Build Docker image
	@echo "[INFO] Building notification service and docker image"
	$(GRADLE_CMD) :services:notification-service:bootJar
	docker build -t notification-service:latest -f Dockerfile .
