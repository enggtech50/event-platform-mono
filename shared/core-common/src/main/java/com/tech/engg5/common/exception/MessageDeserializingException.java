package com.tech.engg5.common.exception;

public class MessageDeserializingException extends RuntimeException {

  public MessageDeserializingException(String message) { super(message); }

  public MessageDeserializingException(String message, Throwable cause) { super(message, cause); }
}
