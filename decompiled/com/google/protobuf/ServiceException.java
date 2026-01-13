package com.google.protobuf;

public class ServiceException extends Exception {
   private static final long serialVersionUID = -1219262335729891920L;

   public ServiceException(final String message) {
      super(message);
   }

   public ServiceException(final Throwable cause) {
      super(cause);
   }

   public ServiceException(final String message, final Throwable cause) {
      super(message, cause);
   }
}
