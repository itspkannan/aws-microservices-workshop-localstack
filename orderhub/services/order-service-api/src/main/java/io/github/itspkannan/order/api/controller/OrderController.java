package io.github.itspkannan.order.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.itspkannan.aws.sns.SnsPublisher;
import io.github.itspkannan.order.dto.CreateOrderRequest;
import io.github.itspkannan.order.model.OrderEvent;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
  private static final Logger log = LoggerFactory.getLogger(OrderController.class);
  private final ObjectMapper objectMapper = new ObjectMapper();
  private final SnsPublisher snsPublisher;

  @PostMapping
  public ResponseEntity<?> createOrder(@RequestBody CreateOrderRequest request) {
    try {
      // skipping validation, authentication check
      log.info("📦 Received order: {}", request);

      OrderEvent event = new OrderEvent(
        request.getUserId(),
        request.getSymbol(),
        request.getQuantity(),
        request.getType()
      );

      String json = objectMapper.writeValueAsString(event);
      snsPublisher.publish(json);

      return ResponseEntity.ok("Order received for user " + request.getUserId());
    } catch (JsonProcessingException e) {
      log.error("Error serializing order event: {}", e.getMessage(), e);
      return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(Map.of(
          "error", "Failed to process order",
          "details", e.getMessage()
        ));
    } catch (Exception e) {
      log.error("Unexpected error while processing order: {}", e.getMessage(), e);
      return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(Map.of(
          "error", "Unexpected server error",
          "details", e.getMessage()
        ));
    }
  }

}
