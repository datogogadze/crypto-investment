package com.epam.cryptoinvestment.exceptions;

import java.time.DateTimeException;
import java.time.ZonedDateTime;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
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

  private ResponseEntity<Object> toResponse(ApiError apiError, Exception ex,
                                            HttpHeaders headers, WebRequest request
                                           ) {
    return new ResponseEntity<>(apiError, headers, apiError.getStatus());
  }

  @ExceptionHandler(value = {NumberFormatException.class})
  protected ResponseEntity<Object> handleNumberFormatException(NumberFormatException ex, WebRequest request) {
    var apiError = new ApiError(ZonedDateTime.now(),
                        false,
                        400,
                        "Bad request",
                        ex.getMessage());
    return toResponse(apiError, ex, new HttpHeaders(), request);
  }

  @ExceptionHandler(value = {CryptoNotSupportedException.class})
  protected ResponseEntity<Object> handleCryptoNotSupportedException(CryptoNotSupportedException ex, WebRequest request) {
    var apiError = new ApiError(ZonedDateTime.now(),
                        false,
                        400,
                        "Bad request",
                        ex.getMessage());
    return toResponse(apiError, ex, new HttpHeaders(), request);
  }

  @ExceptionHandler(value = {DateTimeException.class})
  protected ResponseEntity<Object> handleDateTimeException(DateTimeException ex, WebRequest request) {
    var apiError =  new ApiError(ZonedDateTime.now(),
                        false,
                        400,
                        "Bad request",
                        ex.getMessage());
    return toResponse(apiError, ex, new HttpHeaders(), request);
  }

  @ExceptionHandler(value = {HttpMessageNotReadableException.class})
  protected ResponseEntity<Object> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex, WebRequest request) {
    var apiError = new ApiError(ZonedDateTime.now(),
                        false,
                        400,
                        "Bad request",
                        ex.getMessage());
    return toResponse(apiError, ex, new HttpHeaders(), request);
  }

  @ExceptionHandler(value = {MethodArgumentNotValidException.class})
  protected ResponseEntity<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, WebRequest request) {
    var apiError =  new ApiError(ZonedDateTime.now(),
                        false,
                        400,
                        "Bad request",
                        ex.getMessage());
    return toResponse(apiError, ex, new HttpHeaders(), request);
  }

  @ExceptionHandler(value = {IncorrectDaysOrMonthsValueException.class})
  protected ResponseEntity<Object> handleIncorrectDaysValueException(IncorrectDaysOrMonthsValueException ex, WebRequest request) {
    var apiError =  new ApiError(ZonedDateTime.now(),
                        false,
                        400,
                        "Bad request",
                        ex.getMessage());
    return toResponse(apiError, ex, new HttpHeaders(), request);
  }

  @ExceptionHandler(value = {TooManyRequestsException.class})
  protected ResponseEntity<Object> handleTooManyRequestsException(TooManyRequestsException ex, WebRequest request) {
    var apiError = new ApiError(ZonedDateTime.now(),
                        false,
                        429,
                        "Too many requests",
                        ex.getMessage());
    return toResponse(apiError, ex, new HttpHeaders(), request);
  }
}
