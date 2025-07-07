package io.github.itspkannan.order.factory;

import io.github.itspkannan.order.eventhandler.EventRouter;
import io.github.itspkannan.order.eventhandler.OrderCreatedEventHandler;
import io.github.itspkannan.order.processor.OrderCreatedEventProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class EventHandlerFactory {

  @Bean
  public EventRouter eventRouter() {
    return new EventRouter(new OrderCreatedEventHandler(new OrderCreatedEventProcessor()));
  }
}
