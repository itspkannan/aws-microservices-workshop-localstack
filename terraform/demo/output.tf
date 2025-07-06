output "s3_bucket_name" {
  value = aws_s3_bucket.my_bucket.bucket
}

output "sqs_queue_url" {
  value = aws_sqs_queue.my_queue.id
}

output "sns_topic_arn" {
  value = aws_sns_topic.my_topic.arn
}

output "ssm_param_name" {
  value = aws_ssm_parameter.my_param.name
}
