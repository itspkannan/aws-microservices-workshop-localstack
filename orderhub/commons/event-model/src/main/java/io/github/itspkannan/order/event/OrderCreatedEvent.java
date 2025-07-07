package io.github.itspkannan.order.event;

import lombok.*;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class OrderCreatedEvent extends OrderEvent {
  private String userId;
  private String symbol;
  private int quantity;
  private String type;
}
