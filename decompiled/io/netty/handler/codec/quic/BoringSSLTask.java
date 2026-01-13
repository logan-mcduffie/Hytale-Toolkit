package io.netty.handler.codec.quic;

abstract class BoringSSLTask implements Runnable {
   private final long ssl;
   protected boolean didRun;
   private int returnValue;
   private volatile boolean complete;

   protected BoringSSLTask(long ssl) {
      this.ssl = ssl;
   }

   @Override
   public final void run() {
      if (!this.didRun) {
         this.didRun = true;
         this.runTask(this.ssl, (ssl, result) -> {
            this.returnValue = result;
            this.complete = true;
         });
      }
   }

   protected void destroy() {
   }

   protected abstract void runTask(long var1, BoringSSLTask.TaskCallback var3);

   interface TaskCallback {
      void onResult(long var1, int var3);
   }
}
