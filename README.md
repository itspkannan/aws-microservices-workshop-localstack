# AWS Microservices Portfolio: Cloud-Native Service Architecture

This repository contains a collection of microservices built using **AWS cloud services**, each designed to demonstrate a specific capability or integration. The goal is to provide hands-on, production-style implementations for building a modern backend cloud-native stack.

## 🧭 Project Overview

Each microservice in this repo focuses on one or more AWS services and follows best practices for clean architecture, security, observability, and modularity.

| Service | Purpose | AWS Services Used |
|--------|---------|-------------------|
| [./orderhub](orderhub) | Event-driven processing - Place Order - Fanout to persist and notification service | SQS, SNS |
| TODO | Cloud storage microservice | S3 |
| TODO | Secrets and configuration | SSM Parameter Store, Secrets Manager |
| TODO | Auth and user isolation | IAM |
| TODO | Audit trail and monitoring | CloudWatch, X-Ray |

---

## 🚀 Project 1: OrderHub - A service to demonstrate event driven processing 

Simple stock order system with limited feature 

### ✅ Features

- Place order using RestAPI
- Send the order request to SNS
- Fan out to two queues - Order Processor to persist and Notification Service

## 🚀 Project 2: `s3-drive` (S3 File Storage Microservice) (TODO)

This microservice mimics the basic functionality of **Dropbox or OneDrive** using AWS S3 as the storage backend.

### ✅ Features

- Upload files to S3 via REST API
- List uploaded files
- Download files
- Delete files
- Containerized using Docker
- Configurable via `.env`

