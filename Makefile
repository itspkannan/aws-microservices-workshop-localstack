@docker compose=docker compose
SERVICE_NAME=localstack
TF_DIR := ./terraform
DOCKER_NETWORK ?= appnet
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

.PHONY: infra.init
infra.init: ## 🌐 Create the infrastructure
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

.PHONY: output.terraform
output.terraform:
	@echo "📦 Terraform Outputs"
	$(TF_DOCKER_RUN) output

.PHONY: infra.start
infra.start: ## 🚀 Start localstack services.
	@echo "🚀 Starting LocalStack..."
	@docker compose up -d

.PHONY: infra.stop
infra.stop: ## 🛑 Stop LocalStack services.
	@echo "🛑 Stopping LocalStack..."
	@docker compose down -v

.PHONY: init.infra
infra.clean: ## 🧹 Clean resources created for various services
	@echo "🧹 Removing Temporary resources ..."
	@docker network rm $(DOCKER_NETWORK) || true

.PHONY: infra.logs
infra.logs: ## 📜  Localstack logs
	@echo "📜 infrastructure (localstack) logs..."
	@docker compose logs -f $(SERVICE_NAME)

.PHONY: infra.ps
infra.ps: ## 📦 Container Status
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
	@$(AWS_CLI_DOCKER_RUN) sns list-topics--region us-east-1 \
		--endpoint-url $(LOCALSTACK_URL)

ssm.list:  ## 📦 List SSM parameters
	@echo "📦 Listing SSM Parameters..."
	@$(AWS_CLI_DOCKER_RUN) ssm get-parameters-by-path \
		--path "/config" --region us-east-1 \
		--endpoint-url $(LOCALSTACK_URL)