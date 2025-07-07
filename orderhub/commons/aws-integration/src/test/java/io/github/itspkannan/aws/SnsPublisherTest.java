package io.github.itspkannan.aws;

import io.github.itspkannan.aws.config.AwsProperties;
import io.github.itspkannan.aws.sns.SnsPublisher;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class SnsPublisherTest {

  @Test
  void testPublish_sendsMessageToCorrectTopic() {
    // Arrange
    SnsClient mockClient = mock(SnsClient.class);
    AwsProperties props = new AwsProperties();
    props.setSns(new AwsProperties.Sns());
    props.getSns().setTopicArn("arn:aws:sns:us-east-1:123456789012:my-topic");

    when(mockClient.publish(any(PublishRequest.class)))
      .thenReturn(PublishResponse.builder().messageId("123").build());

    SnsPublisher publisher = new SnsPublisher(mockClient, props);

    // Act
    publisher.publish("test message");

    // Assert
    ArgumentCaptor<PublishRequest> captor = ArgumentCaptor.forClass(PublishRequest.class);
    verify(mockClient).publish(captor.capture());

    PublishRequest request = captor.getValue();
    assertEquals("\"test message\"", request.message());
    assertEquals("arn:aws:sns:us-east-1:123456789012:my-topic", request.topicArn());
  }
}
