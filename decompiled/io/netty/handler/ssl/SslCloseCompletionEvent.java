package io.netty.handler.ssl;

public final class SslCloseCompletionEvent extends SslCompletionEvent {
   public static final SslCloseCompletionEvent SUCCESS = new SslCloseCompletionEvent();

   private SslCloseCompletionEvent() {
   }

   public SslCloseCompletionEvent(Throwable cause) {
      super(cause);
   }
}
