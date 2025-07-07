package io.github.itspkannan.aws.config;

import io.github.itspkannan.aws.sns.SnsPublisher;
import io.github.itspkannan.aws.sqs.SqsListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import software.amazon.awssdk.auth.credentials.*;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sqs.SqsClient;

import java.net.URI;

@Slf4j
@Configuration
@EnableConfigurationProperties(AwsProperties.class)
public class AwsClientFactory {

  private final Environment env;
  private final AwsProperties props;

  public AwsClientFactory(Environment env, AwsProperties props) {
    this.env = env;
    this.props = props;
  }

  private boolean isLocalProfile() {
    for (String profile : env.getActiveProfiles()) {
      if ("local".equalsIgnoreCase(profile)) return true;
    }
    return false;
  }

  private AwsCredentialsProvider credentialsProvider() {
    if (isLocalProfile() && props.getCredentials() != null) {
      log.info("🔐 Using static credentials for LocalStack");
      return StaticCredentialsProvider.create(
        AwsBasicCredentials.create(
          props.getCredentials().getAccessKey(),
          props.getCredentials().getSecretKey()
        )
      );
    }
    log.info("🔐 Using default AWS credentials provider");
    return DefaultCredentialsProvider.create();
  }

  private Region region() {
    return Region.of(props.getRegion());
  }

  private SnsClient snsClient() {
    var builder = SnsClient.builder()
      .region(region())
      .credentialsProvider(credentialsProvider());

    if (isLocalProfile() && props.getEndpoint() != null) {
      builder.endpointOverride(URI.create(props.getEndpoint()));
    }

    return builder.build();
  }

  private SqsClient sqsClient() {
    var builder = SqsClient.builder()
      .region(region())
      .credentialsProvider(credentialsProvider());

    if (isLocalProfile() && props.getEndpoint() != null) {
      builder.endpointOverride(URI.create(props.getEndpoint()));
    }

    return builder.build();
  }

  @Bean
  public SnsPublisher snsPublisher() {
    if (props.getSns() == null || props.getSns().getTopicArn() == null) return null;
    return new SnsPublisher(snsClient(), props);
  }

  @Bean
  public SqsListener sqsListener() {
    if (props.getSqs() == null || props.getSqs().getQueueUrl() == null) return null;
    return new SqsListener(sqsClient(), props);
  }
}
