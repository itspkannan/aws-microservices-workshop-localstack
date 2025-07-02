resource "aws_s3_bucket" "my_bucket" {
  bucket = "my-bucket"
}

resource "aws_sqs_queue" "my_queue" {
  name = "my-queue"
}

resource "aws_sns_topic" "my_topic" {
  name = "my-topic"
}

resource "aws_ssm_parameter" "my_param" {
  name  = "/config/app/env"
  type  = "String"
  value = "dev"
}
