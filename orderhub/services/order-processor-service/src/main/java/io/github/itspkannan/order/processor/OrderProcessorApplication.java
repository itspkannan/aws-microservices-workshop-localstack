package io.github.itspkannan.order.processor;

import io.github.itspkannan.aws.sqs.SqsListener;
import io.github.itspkannan.order.eventhandler.EventRouter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@Slf4j
@SpringBootApplication(
    scanBasePackages = {"io.github.itspkannan.order", "io.github.itspkannan.aws"})
public class OrderProcessorApplication {
  public static void main(String[] args) {
    SpringApplication.run(OrderProcessorApplication.class, args);
  }

  @Bean
  CommandLineRunner startListener(SqsListener sqsListener, EventRouter eventRouter) {
    return args -> {
      log.info("Starting SQS listener with message handler...");
      sqsListener.start(eventRouter::route);
    };
  }
}
