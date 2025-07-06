package io.github.itspkannan.aws.sns;

import io.github.itspkannan.aws.config.AwsProperties;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;

@RequiredArgsConstructor
public class SnsPublisher {
  private final SnsClient snsClient;
  private final AwsProperties props;
  private static final Logger log = LoggerFactory.getLogger(SnsPublisher.class);

  public void publish(String message) {
    PublishRequest req = PublishRequest.builder()
      .topicArn(props.getSnsTopicArn())
      .message(message)
      .build();
    var response = snsClient.publish(req);
    log.info("Published to SNS: {}", response.messageId());
  }
}
