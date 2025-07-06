package io.github.itspkannan.aws.config;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AwsProperties {
  private String region;
  private String snsTopicArn;
  private String sqsQueueUrl;
}
