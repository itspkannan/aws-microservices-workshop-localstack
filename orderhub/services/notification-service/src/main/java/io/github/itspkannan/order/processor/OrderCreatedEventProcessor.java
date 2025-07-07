package io.github.itspkannan.order.processor;

import io.github.itspkannan.order.event.OrderCreatedEvent;
import java.util.function.Consumer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@Slf4j
@AllArgsConstructor
public class OrderCreatedEventProcessor implements Consumer<OrderCreatedEvent> {

  private final JavaMailSender mailSender;

  @Override
  public void accept(OrderCreatedEvent event) {
    log.info("Processing OrderCreatedEvent: {}, sending email", event);
    SimpleMailMessage message = new SimpleMailMessage();
    message.setTo("user@example.com");
    message.setSubject("Order Placed");
    message.setText("Your order has been successfully placed.");
    mailSender.send(message);
  }
}
