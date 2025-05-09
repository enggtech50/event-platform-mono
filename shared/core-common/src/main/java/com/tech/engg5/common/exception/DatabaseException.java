package com.tech.engg5.common.exception;

public class DatabaseException extends RuntimeException {

  public DatabaseException(String message) { super(message); }

  public DatabaseException(String message, Throwable cause) { super(message, cause); }
}
