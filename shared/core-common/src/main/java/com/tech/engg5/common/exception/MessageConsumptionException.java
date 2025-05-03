package com.tech.engg5.common.exception;

public class MessageConsumptionException extends RuntimeException {

  public MessageConsumptionException(String message) { super(message); }

  public MessageConsumptionException(Throwable cause) { super(cause); }

  public MessageConsumptionException(String message, Throwable cause) { super(message, cause); }
}
