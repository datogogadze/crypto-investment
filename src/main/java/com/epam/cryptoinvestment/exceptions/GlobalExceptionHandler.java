package com.epam.cryptoinvestment.exceptions;

import java.time.DateTimeException;
import java.time.ZonedDateTime;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

/*
  this is global exception handler, so if any exception is thrown in application
  it will be caught here and the ApiError will be returned which will contain
  information about the exception (timestamp, status code, message)
*/
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(value = {NumberFormatException.class})
  protected ApiError handleNumberFormatException(NumberFormatException ex, WebRequest request) {
    return new ApiError(ZonedDateTime.now(),
                        false,
                        400,
                        "Bad request",
                        ex.getMessage());
  }

  @ExceptionHandler(value = {CryptoNotSupportedException.class})
  protected ApiError handleCryptoNotSupportedException(CryptoNotSupportedException ex, WebRequest request) {
    return new ApiError(ZonedDateTime.now(),
                        false,
                        400,
                        "Bad request",
                        ex.getMessage());
  }

  @ExceptionHandler(value = {DateTimeException.class})
  protected ApiError handleDateTimeException(DateTimeException ex, WebRequest request) {
    return new ApiError(ZonedDateTime.now(),
                        false,
                        400,
                        "Bad request",
                        ex.getMessage());
  }

  @ExceptionHandler(value = {HttpMessageNotReadableException.class})
  protected ApiError handleHttpMessageNotReadableException(HttpMessageNotReadableException ex, WebRequest request) {
    return new ApiError(ZonedDateTime.now(),
                        false,
                        400,
                        "Bad request",
                        ex.getMessage());
  }

  @ExceptionHandler(value = {MethodArgumentNotValidException.class})
  protected ApiError handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, WebRequest request) {
    return new ApiError(ZonedDateTime.now(),
                        false,
                        400,
                        "Bad request",
                        ex.getMessage());
  }

  @ExceptionHandler(value = {IncorrectDaysOrMonthsValueException.class})
  protected ApiError handleIncorrectDaysValueException(IncorrectDaysOrMonthsValueException ex, WebRequest request) {
    return new ApiError(ZonedDateTime.now(),
                        false,
                        400,
                        "Bad request",
                        ex.getMessage());
  }
}
