package io.github.itspkannan.order.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderEvent {
  private String userId;
  private String symbol;
  private int quantity;
  private String type;
}
