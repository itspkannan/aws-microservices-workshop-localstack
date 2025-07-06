package io.github.itspkannan.aws;

import io.github.itspkannan.aws.config.AwsProperties;
import io.github.itspkannan.aws.sqs.SqsListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import static org.mockito.Mockito.*;

class SqsListenerTest {

  private SqsClient mockSqsClient;
  private AwsProperties props;
  private SqsListener sqsListener;

  @BeforeEach
  void setUp() {
    mockSqsClient = mock(SqsClient.class);
    props = new AwsProperties();
    props.setSqsQueueUrl("https://sqs.us-east-1.amazonaws.com/123456789012/test-queue");

    sqsListener = new SqsListener(mockSqsClient, props);
  }

  @Test
  void shouldReceiveAndHandleSqsMessage() throws InterruptedException {
    // Arrange
    Message message = Message.builder()
      .body("Mock SQS message")
      .receiptHandle("abc-123")
      .build();

    ReceiveMessageResponse mockResponse = ReceiveMessageResponse.builder()
      .messages(List.of(message))
      .build();

    when(mockSqsClient.receiveMessage(any(ReceiveMessageRequest.class)))
      .thenReturn(mockResponse)
      .thenReturn(ReceiveMessageResponse.builder().messages(List.of()).build()); // stop after one

    AtomicBoolean messageHandled = new AtomicBoolean(false);

    Consumer<Message> handler = msg -> {
      messageHandled.set(true);
      assert msg.body().equals("Mock SQS message");
    };

    // Act
    Thread listenerThread = new Thread(() -> sqsListener.start(handler));
    listenerThread.start();

    Thread.sleep(2000); // allow background thread to process

    // Assert
    assert messageHandled.get();

    verify(mockSqsClient, atLeastOnce()).deleteMessage(any(DeleteMessageRequest.class));
    listenerThread.interrupt(); // stop the thread
  }
}
