package io.github.itspkannan.aws;

import io.github.itspkannan.aws.config.AwsProperties;
import io.github.itspkannan.aws.sns.SnsPublisher;
import io.github.itspkannan.aws.sqs.SqsListener;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.*;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

import java.lang.reflect.Field;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AwsIntegrationTest {

  private static final DockerImageName LOCALSTACK_IMAGE = DockerImageName.parse("localstack/localstack:latest");
  private static final String TOPIC_NAME = "test-topic";
  private static final String QUEUE_NAME = "test-queue";

  private LocalStackContainer localstack;
  private SnsPublisher snsPublisher;
  private SqsListener sqsListener;

  @BeforeAll
  void setup() throws Exception {
    localstack = new LocalStackContainer(LOCALSTACK_IMAGE)
      .withServices(LocalStackContainer.Service.SNS, LocalStackContainer.Service.SQS)
      .withStartupTimeout(Duration.ofSeconds(60));
    localstack.start();

    Region region = Region.of(localstack.getRegion());
    StaticCredentialsProvider credentialsProvider = StaticCredentialsProvider.create(
      AwsBasicCredentials.create("test", "test")
    );

    SnsClient snsClient = SnsClient.builder()
      .endpointOverride(localstack.getEndpointOverride(LocalStackContainer.Service.SNS))
      .region(region)
      .credentialsProvider(credentialsProvider)
      .build();

    SqsClient sqsClient = SqsClient.builder()
      .endpointOverride(localstack.getEndpointOverride(LocalStackContainer.Service.SQS))
      .region(region)
      .credentialsProvider(credentialsProvider)
      .build();

    // Setup SNS Topic and SQS Queue
    String topicArn = snsClient.createTopic(CreateTopicRequest.builder().name(TOPIC_NAME).build()).topicArn();
    String queueUrl = sqsClient.createQueue(CreateQueueRequest.builder().queueName(QUEUE_NAME).build()).queueUrl();

    String queueArn = sqsClient.getQueueAttributes(GetQueueAttributesRequest.builder()
        .queueUrl(queueUrl)
        .attributeNames(QueueAttributeName.QUEUE_ARN)
        .build())
      .attributes().get(QueueAttributeName.QUEUE_ARN.toString());

    snsClient.subscribe(SubscribeRequest.builder()
      .topicArn(topicArn)
      .protocol("sqs")
      .endpoint(queueArn)
      .attributes(Map.of("RawMessageDelivery", "true"))
      .build());

    String policy = "{ \"Version\": \"2012-10-17\", \"Statement\": [{ \"Effect\": \"Allow\", \"Principal\": \"*\", \"Action\": \"sqs:SendMessage\", \"Resource\": \"" + queueArn + "\", \"Condition\": { \"ArnEquals\": { \"aws:SourceArn\": \"" + topicArn + "\" }}}]}";

    sqsClient.setQueueAttributes(SetQueueAttributesRequest.builder()
      .queueUrl(queueUrl)
      .attributes(Map.of(QueueAttributeName.POLICY, policy))
      .build());

    // Instantiate AwsProperties and inject private fields via reflection
    AwsProperties props = new AwsProperties();
    injectField(props, "region", region.id());
    injectField(props, "snsTopicArn", topicArn);
    injectField(props, "sqsQueueUrl", queueUrl);

    snsPublisher = new SnsPublisher(snsClient, props);
    sqsListener = new SqsListener(sqsClient, props);
  }

  @Test
  void testPublishAndConsume() throws InterruptedException {
    CountDownLatch latch = new CountDownLatch(1);

    sqsListener.start(message -> {
      System.out.println("✅ Received message: " + message.body());
      Assertions.assertEquals("Hello, Testcontainers!", message.body());
      latch.countDown();
    });

    snsPublisher.publish("Hello, Testcontainers!");

    boolean received = latch.await(10, TimeUnit.SECONDS);
    Assertions.assertTrue(received, "Message was not received in time.");
  }

  @AfterAll
  void teardown() {
    if (localstack != null) {
      localstack.stop();
    }
  }

  private static void injectField(Object target, String fieldName, Object value) throws Exception {
    Field field = target.getClass().getDeclaredField(fieldName);
    field.setAccessible(true);
    field.set(target, value);
  }
}
