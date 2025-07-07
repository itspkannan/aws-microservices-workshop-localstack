package io.github.itspkannan.order.eventhandler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.sqs.model.Message;

@Slf4j
@RequiredArgsConstructor
public class EventRouter {
  private final ObjectMapper mapper = new ObjectMapper();
  private final OrderCreatedEventHandler orderCreatedHandler;

  public void route(Message message) {
    try {
      JsonNode root = mapper.readTree(message.body());
      String type = root.get("eventType").asText();
      JsonNode dataNode = root.get("data");
      switch (type) {
        case "OrderCreated" -> orderCreatedHandler.handle(dataNode);
        default -> log.warn("Unrecognized eventType: {}", type);
      }

    } catch (Exception e) {
      log.error("Failed to route message: {}", message.body(), e);
    }
  }
}
