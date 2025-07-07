package io.github.itspkannan.aws.sqs;

import io.github.itspkannan.aws.config.AwsProperties;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

@RequiredArgsConstructor
public class SqsListener {
  private final SqsClient sqsClient;
  private final AwsProperties props;
  private final ExecutorService executor = Executors.newSingleThreadExecutor();
  private static final Logger log = LoggerFactory.getLogger(SqsListener.class);

  public void start(Consumer<Message> handler) {
    executor.submit(() -> {
      while (true) {
        ReceiveMessageRequest request = ReceiveMessageRequest.builder()
          .queueUrl(props.getSqs().getQueueUrl())
          .maxNumberOfMessages(5)
          .waitTimeSeconds(10)
          .build();

        List<Message> messages = sqsClient.receiveMessage(request).messages();
        for (Message msg : messages) {
          try {
            log.info("Received: {}", msg.body());
            handler.accept(msg);
            sqsClient.deleteMessage(DeleteMessageRequest.builder()
              .queueUrl(props.getSqs().getQueueUrl())
              .receiptHandle(msg.receiptHandle())
              .build());
          } catch (Exception e) {
            log.error("Failed to process message", e);
          }
        }
      }
    });
  }
}
