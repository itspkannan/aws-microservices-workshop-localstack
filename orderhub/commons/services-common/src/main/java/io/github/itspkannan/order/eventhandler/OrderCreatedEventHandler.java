package io.github.itspkannan.order.eventhandler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.itspkannan.order.event.OrderCreatedEvent;
import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OrderCreatedEventHandler {
  protected final ObjectMapper mapper = new ObjectMapper();
  private final Consumer<OrderCreatedEvent> callback;

  public OrderCreatedEventHandler(Consumer<OrderCreatedEvent> callback) {
    this.callback = callback;
  }

  public void handle(JsonNode eventJson) {
    try {
      OrderCreatedEvent event = mapper.treeToValue(eventJson, OrderCreatedEvent.class);
      log.info("Handling OrderCreatedEvent: {}", event);
      callback.accept(event);
    } catch (Exception e) {
      log.error("Failed to parse OrderCreatedEvent", e);
    }
  }
}
