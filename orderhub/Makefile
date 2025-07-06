.DEFAULT_GOAL := help

.PHONY: help
help:  ## 📖 Help message
	@echo ""
	@echo "\033[1;33mAvailable commands:\033[0m" && \
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

.PHONY: init
init: ## Initialize development environment prerequisites
	@echo "[INFO] Initializing Gradle wrapper."
	@gradle wrapper

build-all: ## 🏗️ Build all modules
	./gradlew build

test-all: ## ✅ Run all tests
	./gradlew test

clean: ## 🧹 Clean all builds
	./gradlew clean
