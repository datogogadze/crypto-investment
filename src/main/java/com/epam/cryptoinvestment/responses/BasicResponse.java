package com.epam.cryptoinvestment.responses;

import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BasicResponse {
  private ZonedDateTime timestamp = ZonedDateTime.now();
  boolean success;
  private int status;
  private String message;
}
