package io.github.itspkannan.aws.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AwsEvent<T> {
  private String eventType;
  private T data;
}
