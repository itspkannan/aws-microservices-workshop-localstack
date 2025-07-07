package io.github.itspkannan.order.dto;

import lombok.Data;

@Data
public class CreateOrderRequest {
  private String userId;
  private String symbol;
  private int quantity;
  private String type; // e.g., BUY or SELL
}
