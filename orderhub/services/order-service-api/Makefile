
ROOT_DIR := $(realpath $(dir $(lastword $(MAKEFILE_LIST)))/../..)
GRADLE_CMD = $(ROOT_DIR)/gradlew
SERVICE = order-service-api
SERVICE_USER = order-api-user

.PHONY: help
help: ## 📖 Show available commands
	@echo "\033[1;33mAvailable commands for $(SERVICE):\033[0m" && \
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

.PHONY: build
build: ## 🛠️ Build the service
	$(GRADLE_CMD) :services:$(SERVICE):build

.PHONY: run
run: ## 🚀 Run the Spring Boot app
	$(GRADLE_CMD) :services:$(SERVICE):bootRun

.PHONY: docker.build
docker.build: ## 🐳 Build Docker image
	@echo "[INFO] Building $(SERVICE)and docker image"
	$(GRADLE_CMD) :services:$(SERVICE):bootJar
	@docker build --build-arg APP_USER=$(SERVICE_USER) \
			--build-arg SERVICE_NAME=$(SERVICE) \
			-t $(SERVICE):latest -f Dockerfile .
