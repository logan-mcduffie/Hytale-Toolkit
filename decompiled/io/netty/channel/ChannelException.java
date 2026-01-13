package io.netty.channel;

import io.netty.util.internal.ThrowableUtil;

public class ChannelException extends RuntimeException {
   private static final long serialVersionUID = 2908618315971075004L;

   public ChannelException() {
   }

   public ChannelException(String message, Throwable cause) {
      super(message, cause);
   }

   public ChannelException(String message) {
      super(message);
   }

   public ChannelException(Throwable cause) {
      super(cause);
   }

   protected ChannelException(String message, Throwable cause, boolean shared) {
      super(message, cause, false, true);

      assert shared;
   }

   static ChannelException newStatic(String message, Class<?> clazz, String method) {
      ChannelException exception = new ChannelException.StacklessChannelException(message, null, true);
      return ThrowableUtil.unknownStackTrace(exception, clazz, method);
   }

   private static final class StacklessChannelException extends ChannelException {
      private static final long serialVersionUID = -6384642137753538579L;

      StacklessChannelException(String message, Throwable cause, boolean shared) {
         super(message, cause, shared);
      }

      @Override
      public Throwable fillInStackTrace() {
         return this;
      }
   }
}
