@docker compose=docker compose
SERVICE_NAME=localstack
TF_DIR := ./terraform
DOCKER_NETWORK ?= orderhub
TF_DOCKER_RUN := docker run --rm \
	-v $(PWD)/$(TF_DIR):/workspace --network $(DOCKER_NETWORK) -w /workspace \
	-e AWS_ACCESS_KEY_ID=test -e AWS_SECRET_ACCESS_KEY=test \
	-e AWS_DEFAULT_REGION=us-east-1 hashicorp/terraform:1.8

AWS_CLI_DOCKER_RUN = docker run --rm --network $(DOCKER_NETWORK) \
  -e AWS_ACCESS_KEY_ID=test -e AWS_SECRET_ACCESS_KEY=test amazon/aws-cli

LOCALSTACK_URL := http://mock-aws-services:4566

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

.PHONY: init.infra
init.infra: ## 🌐 Create the infrastructure
	@docker network create $(DOCKER_NETWORK) || true

.PHONY: terraform.init
terraform.init: ## 🚀 Terraform Init
	@echo "🚀 Terraform Init"
	$(TF_DOCKER_RUN) init

.PHONY: terraform.plan
terraform.plan: ## 🔍 Terraform Plan
	@echo "🔍 Terraform Plan"
	$(TF_DOCKER_RUN) plan

.PHONY: terraform.apply
terraform.apply: ## ✅ Terraform Apply
	@echo "✅ Terraform Apply"
	$(TF_DOCKER_RUN) apply -auto-approve

.PHONY: terraform.destroy
terraform.destroy: ## 🔥 Terraform Destroy
	@echo "🔥 Terraform Destroy"
	$(TF_DOCKER_RUN) destroy -auto-approve

.PHONY: terraform.validate
terraform.validate: ##🔎 Terraform Validate
	@echo "🔎 Terraform Validate"
	$(TF_DOCKER_RUN) validate

.PHONY: terraform.fmt
terraform.fmt: ## 🧹 Terraform Format
	@echo "🧹 Terraform Format"
	$(TF_DOCKER_RUN) fmt

.PHONY: terraform.show
terraform.show: ## 📜 Terraform Show"
	@echo "📜 Terraform Show"
	$(TF_DOCKER_RUN) show

.PHONY: terraform.output
terraform.output:
	@echo "📦 Terraform Outputs"
	$(TF_DOCKER_RUN) output

.PHONY: start.infra
start.infra: ## 🚀 Start localstack services.
	@echo "🚀 Starting LocalStack..."
	@docker compose up -d

.PHONY: stop.infra
stop.infra: ## 🛑 Stop LocalStack services.
	@echo "🛑 Stopping LocalStack..."
	@docker compose down -v

.PHONY: init.infra
clean.infra: ## 🧹 Clean resources created for various services
	@echo "🧹 Removing Temporary resources ..."
	@docker network rm $(DOCKER_NETWORK) || true

.PHONY: logs.infra
logs.infra: ## 📜  Localstack logs
	@echo "📜 infrastructure (localstack) logs..."
	@docker compose logs -f $(SERVICE_NAME)

.PHONY: ps.infra
ps.infra: ## 📦 Container Status
	@echo "📦 Runtime Container Status ..."
	@docker compose ps

s3.list:  ## 📂 List S3 buckets
	@echo "📂 Listing S3 Buckets..."
	@$(AWS_CLI_DOCKER_RUN) s3 ls --endpoint-url $(LOCALSTACK_URL)

sqs.list:  ## 📬 List SQS queues
	@echo "📬 Listing SQS Queues..."
	@$(AWS_CLI_DOCKER_RUN) sqs list-queues --region us-east-1 \
		--endpoint-url $(LOCALSTACK_URL)

sns.list:  ## 📣 List SNS topics
	@echo "📣 Listing SNS Topics..."
	@$(AWS_CLI_DOCKER_RUN) sns list-topics --region us-east-1 \
		--endpoint-url $(LOCALSTACK_URL)

ssm.list:  ## 📦 List SSM parameters
	@echo "📦 Listing SSM Parameters..."
	@$(AWS_CLI_DOCKER_RUN) ssm get-parameters-by-path \
		--path "/config" --region us-east-1 \
		--endpoint-url $(LOCALSTACK_URL)

.PHONY: init
init: init.infra terraform.init ## Initialize development environment prerequisites
	@echo "[INFO] Initializing Gradle wrapper."
	@gradle wrapper

.PHONY: build
build: ## 🏗️ Build all modules
	./gradlew build

.PHONY: test.unit
test.unit: ## 🧪 Run unit tests
	./gradlew test

.PHONY: test.integration
test.integration: ## 🧪 Run integration tests
	./gradlew integrationTest

.PHONY: clean
clean: ## 🧹 Clean all builds
	./gradlew clean


##--------------------------------------------------------------
##  --- Run Services, Stop Services, Look at logs  ---
##--------------------------------------------------------------

orderapi.start: ## 🚀 Start the Order API service
	@echo "🚀 Starting Order API service..."
	@nohup ./gradlew :services:order-service-api:bootRun > /tmp/orderapi.log 2>&1 & echo $$! > /tmp/orderapi.pid && \
	echo "✅ Order API running (log: /tmp/orderapi.log, pid: `cat /tmp/orderapi.pid`)"

orderapi.stop: ## ❌ Stop the Order API service
	@kill `cat /tmp/orderapi.pid` 2>/dev/null || echo "Order API not running"
	@rm -f /tmp/orderapi.pid

orderapi.logs: ## 📄 Tail logs for Order API
	@echo "📄 Tailing /tmp/orderapi.log ..."
	@tail -f /tmp/orderapi.log

orderprocessor.start: ## ⚙️ Start the Order Processor service
	@echo "⚙️  Starting Order Processor service..."
	@nohup ./gradlew :services:order-processor-service:bootRun > /tmp/orderprocessor.log 2>&1 & echo $$! > /tmp/orderprocessor.pid && \
	echo "✅ Order Processor running (log: /tmp/orderprocessor.log, pid: `cat /tmp/orderprocessor.pid`)"

orderprocessor.stop: ## ❌ Stop the Order Processor service
	@kill `cat /tmp/orderprocessor.pid` 2>/dev/null || echo "Order Processor not running"
	@rm -f /tmp/orderprocessor.pid

orderprocessor.logs: ## 📄 Tail logs for Order Processor
	@echo "📄 Tailing /tmp/orderprocessor.log ..."
	@tail -f /tmp/orderprocessor.log

notification.start: ## 📣 Start the Notification service
	@echo "📣 Starting Notification service..."
	@nohup ./gradlew :services:notification-service:bootRun > /tmp/notification.log 2>&1 & echo $$! > /tmp/notification.pid && \
	echo "✅ Notification running (log: /tmp/notification.log, pid: `cat /tmp/notification.pid`)"

notification.stop: ## ❌ Stop the Notification service
	@kill `cat /tmp/notification.pid` 2>/dev/null || echo "Notification not running"
	@rm -f /tmp/notification.pid

notification.logs: ## 📄 Tail logs for Notification Service
	@echo "📄 Tailing /tmp/notification.log ..."
	@tail -f /tmp/notification.log

##--------------------------------------------------------------
