package com.epam.cryptoinvestment.exceptions;

import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiError {

  private ZonedDateTime timestamp;
  private boolean success;
  private int status;
  private String message;
  private String error;

}