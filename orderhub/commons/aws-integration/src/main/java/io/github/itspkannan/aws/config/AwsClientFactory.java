package io.github.itspkannan.aws.config;

import io.github.itspkannan.aws.sns.SnsPublisher;
import io.github.itspkannan.aws.sqs.SqsListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sqs.SqsClient;

@Configuration
public class AwsClientFactory {

  @Bean
  public AwsProperties awsProperties(
    @Value("${aws.region}") String region,
    @Value("${aws.sns.topic-arn:}") String topicArn,
    @Value("${aws.sqs.queue-url:}") String queueUrl
  ) {
    AwsProperties props = new AwsProperties();
    props.setRegion(region);
    props.setSnsTopicArn(topicArn);
    props.setSqsQueueUrl(queueUrl);
    return props;
  }

  private Region region(AwsProperties props) {
    return Region.of(props.getRegion());
  }

  private SnsClient snsClient(Region region) {
    return SnsClient.builder()
      .region(region)
      .credentialsProvider(DefaultCredentialsProvider.create())
      .build();
  }

  private SqsClient sqsClient(Region region) {
    return SqsClient.builder()
      .region(region)
      .credentialsProvider(DefaultCredentialsProvider.create())
      .build();
  }

  @Bean
  public SnsPublisher snsPublisher(AwsProperties props) {
    if (props.getSnsTopicArn() == null || props.getSnsTopicArn().isBlank()) return null;
    return new SnsPublisher(snsClient(region(props)), props);
  }

  @Bean
  public SqsListener sqsListener(AwsProperties props) {
    if (props.getSqsQueueUrl() == null || props.getSqsQueueUrl().isBlank()) return null;
    return new SqsListener(sqsClient(region(props)), props);
  }
}
