package io.github.itspkannan.order.api.controller;

import io.github.itspkannan.aws.event.AwsEvent;
import io.github.itspkannan.aws.sns.SnsPublisher;
import io.github.itspkannan.order.dto.CreateOrderRequest;
import io.github.itspkannan.order.event.OrderCreatedEvent;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
  private static final Logger log = LoggerFactory.getLogger(OrderController.class);
  private final SnsPublisher snsPublisher;

  @PostMapping
  public ResponseEntity<?> createOrder(@RequestBody CreateOrderRequest request) {
    try {
      // skipping validation, authentication check
      log.info("📦 Received order: {}", request);

      OrderCreatedEvent event =
          new OrderCreatedEvent(
              request.getUserId(), request.getSymbol(), request.getQuantity(), request.getType());

      AwsEvent<OrderCreatedEvent> wrapper = new AwsEvent<>("OrderCreated", event);
      snsPublisher.publish(wrapper);

      return ResponseEntity.ok("Order received for user " + request.getUserId());
    } catch (Exception e) {
      log.error("Unexpected error while processing order: {}", e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "Unexpected server error", "details", e.getMessage()));
    }
  }
}
