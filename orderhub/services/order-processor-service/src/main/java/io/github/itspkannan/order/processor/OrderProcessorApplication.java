package io.github.itspkannan.order.processor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication(
    scanBasePackages = {"io.github.itspkannan.order", "io.github.itspkannan.aws"})
public class OrderProcessorApplication {
  public static void main(String[] args) {
    SpringApplication.run(OrderProcessorApplication.class, args);
  }
}
