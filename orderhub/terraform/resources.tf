#------------------------------------------------------------

# 1. SNS Topic
#------------------------------------------------------------
resource "aws_sns_topic" "order_events_topic" {
  name = "stock-order-events-topic"
}

#------------------------------------------------------------
# 2. SQS Queues (order processor + notification)
#------------------------------------------------------------
resource "aws_sqs_queue" "order_processor_queue" {
  name = "order-processor-queue"
}

resource "aws_sqs_queue" "notification_queue" {
  name = "notification-queue"
}

#------------------------------------------------------------
# 3. SQS Subscriptions to SNS topic
#------------------------------------------------------------
resource "aws_sns_topic_subscription" "order_processor_subscription" {
  topic_arn = aws_sns_topic.order_events_topic.arn
  protocol  = "sqs"
  endpoint  = aws_sqs_queue.order_processor_queue.arn

  # Enable raw message delivery
  raw_message_delivery = true
}

resource "aws_sns_topic_subscription" "notification_subscription" {
  topic_arn = aws_sns_topic.order_events_topic.arn
  protocol  = "sqs"
  endpoint  = aws_sqs_queue.notification_queue.arn

  raw_message_delivery = true
}

#------------------------------------------------------------
# 4. Queue Policies to allow SNS topic to send messages to the queues
#------------------------------------------------------------
resource "aws_sqs_queue_policy" "order_processor_policy" {
  queue_url = aws_sqs_queue.order_processor_queue.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect    = "Allow"
        Principal = "*"
        Action    = "sqs:SendMessage"
        Resource  = aws_sqs_queue.order_processor_queue.arn
        Condition = {
          ArnEquals = {
            "aws:SourceArn" = aws_sns_topic.order_events_topic.arn
          }
        }
      }
    ]
  })
}

resource "aws_sqs_queue_policy" "notification_policy" {
  queue_url = aws_sqs_queue.notification_queue.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect    = "Allow"
        Principal = "*"
        Action    = "sqs:SendMessage"
        Resource  = aws_sqs_queue.notification_queue.arn
        Condition = {
          ArnEquals = {
            "aws:SourceArn" = aws_sns_topic.order_events_topic.arn
          }
        }
      }
    ]
  })
}
