package com.epam.cryptoinvestment.exceptions;

public class IncorrectDaysOrMonthsValueException extends RuntimeException{

  public IncorrectDaysOrMonthsValueException() {
    super("The value for days/months is incorrect, it shouldn't be 0");
  }

}
