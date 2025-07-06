output "sns_topic_arn" {
  description = "ARN of the SNS topic for order events"
  value       = aws_sns_topic.order_events_topic.arn
}

output "order_processor_queue_url" {
  description = "URL of the SQS queue for order processor"
  value       = aws_sqs_queue.order_processor_queue.id
}

output "notification_queue_url" {
  description = "URL of the SQS queue for notifications"
  value       = aws_sqs_queue.notification_queue.id
}

output "order_processor_queue_arn" {
  description = "ARN of the order processor SQS queue"
  value       = aws_sqs_queue.order_processor_queue.arn
}

output "notification_queue_arn" {
  description = "ARN of the notification SQS queue"
  value       = aws_sqs_queue.notification_queue.arn
}
