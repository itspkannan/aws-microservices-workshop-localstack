package io.github.itspkannan.order.processor;

import io.github.itspkannan.order.event.OrderCreatedEvent;
import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OrderCreatedEventProcessor implements Consumer<OrderCreatedEvent> {

  @Override
  public void accept(OrderCreatedEvent event) {
    log.info("Processing OrderCreatedEvent: {}, persisting db", event);
  }
}
