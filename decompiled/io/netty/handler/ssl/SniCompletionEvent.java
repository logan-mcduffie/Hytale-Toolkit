package io.netty.handler.ssl;

public final class SniCompletionEvent extends SslCompletionEvent {
   private final String hostname;

   public SniCompletionEvent(String hostname) {
      this.hostname = hostname;
   }

   public SniCompletionEvent(String hostname, Throwable cause) {
      super(cause);
      this.hostname = hostname;
   }

   public SniCompletionEvent(Throwable cause) {
      this(null, cause);
   }

   public String hostname() {
      return this.hostname;
   }

   @Override
   public String toString() {
      Throwable cause = this.cause();
      return cause == null ? this.getClass().getSimpleName() + "(SUCCESS='" + this.hostname + "'\")" : this.getClass().getSimpleName() + '(' + cause + ')';
   }
}
