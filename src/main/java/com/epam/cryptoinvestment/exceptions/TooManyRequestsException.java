package com.epam.cryptoinvestment.exceptions;

public class TooManyRequestsException extends RuntimeException {

  public TooManyRequestsException(int num, int time) {
    super(String.format("limit of %s requests per %s minute(s) was reached", num, time));
  }

}
