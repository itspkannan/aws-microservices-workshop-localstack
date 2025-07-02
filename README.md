# AWS Microservices Portfolio: Cloud-Native Service Architecture

This repository contains a collection of microservices built using **AWS cloud services**, each designed to demonstrate a specific capability or integration. The goal is to provide hands-on, production-style implementations for building a modern backend cloud-native stack.

## 🧭 Project Overview

Each microservice in this repo focuses on one or more AWS services and follows best practices for clean architecture, security, observability, and modularity.

| Service | Purpose | AWS Services Used |
|--------|---------|-------------------|
| [s3-drive](./s3-drive) | Cloud storage microservice (Dropbox/OneDrive clone) | S3 |
| Coming Soon | Event-driven processing | SQS, SNS |
| Coming Soon | Secrets and configuration | SSM Parameter Store, Secrets Manager |
| Coming Soon | Auth and user isolation | IAM |
| Coming Soon | File virus scanning | Lambda, S3 triggers |
| Coming Soon | Audit trail and monitoring | CloudWatch, X-Ray |

---

## 🚀 Project 1: `s3-drive` (S3 File Storage Microservice)

This microservice mimics the basic functionality of **Dropbox or OneDrive** using AWS S3 as the storage backend.

### ✅ Features

- Upload files to S3 via REST API
- List uploaded files
- Download files
- Delete files
- Containerized using Docker
- Configurable via `.env`

