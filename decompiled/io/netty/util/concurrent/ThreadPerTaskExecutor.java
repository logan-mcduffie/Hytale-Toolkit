package io.netty.util.concurrent;

import io.netty.util.internal.ObjectUtil;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;

public final class ThreadPerTaskExecutor implements Executor {
   private final ThreadFactory threadFactory;

   public ThreadPerTaskExecutor(ThreadFactory threadFactory) {
      this.threadFactory = ObjectUtil.checkNotNull(threadFactory, "threadFactory");
   }

   @Override
   public void execute(Runnable command) {
      this.threadFactory.newThread(command).start();
   }
}
