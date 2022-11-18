package com.epam.cryptoinvestment.exceptions;

public class CryptoNotSupportedException extends RuntimeException{

  public CryptoNotSupportedException() {
    super("Crypto not supported");
  }
}
