package com.nimbusds.jwt.proc;

public class ExpiredJWTException extends BadJWTException {
   public ExpiredJWTException(String message) {
      super(message);
   }
}
