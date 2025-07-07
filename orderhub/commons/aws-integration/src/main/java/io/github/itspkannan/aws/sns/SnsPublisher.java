package io.github.itspkannan.aws.sns;

import com.fasterxml.jackson.databind.ObjectMapper;
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
  private final ObjectMapper mapper = new ObjectMapper();

  public void publish(Object event) {
    try {
      String json = mapper.writeValueAsString(event);
      snsClient.publish(PublishRequest.builder()
        .topicArn(props.getSns().getTopicArn())
        .message(json)
        .build());
    } catch (Exception e) {
      throw new RuntimeException("Failed to publish event", e);
    }
  }
}
