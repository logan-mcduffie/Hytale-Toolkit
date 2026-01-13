package io.netty.channel.local;

import io.netty.channel.MultiThreadIoEventLoopGroup;
import java.util.concurrent.ThreadFactory;

@Deprecated
public class LocalEventLoopGroup extends MultiThreadIoEventLoopGroup {
   public LocalEventLoopGroup() {
      this(0);
   }

   public LocalEventLoopGroup(int nThreads) {
      this(nThreads, null);
   }

   public LocalEventLoopGroup(ThreadFactory threadFactory) {
      this(0, threadFactory);
   }

   public LocalEventLoopGroup(int nThreads, ThreadFactory threadFactory) {
      super(nThreads, threadFactory, LocalIoHandler.newFactory());
   }
}
