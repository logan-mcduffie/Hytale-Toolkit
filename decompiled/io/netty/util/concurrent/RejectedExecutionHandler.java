package io.netty.util.concurrent;

public interface RejectedExecutionHandler {
   void rejected(Runnable var1, SingleThreadEventExecutor var2);
}
