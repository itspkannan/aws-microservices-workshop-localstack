package io.github.itspkannan.aws.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "aws")
public class AwsProperties {
  private String region;
  private String endpoint;
  private Credentials credentials;
  private Sns sns;
  private Sqs sqs;

  @Getter @Setter
  public static class Credentials {
    private String accessKey;
    private String secretKey;
  }

  @Getter @Setter
  public static class Sns {
    private String topicArn;
  }

  @Getter @Setter
  public static class Sqs {
    private String queueUrl;
  }
}
