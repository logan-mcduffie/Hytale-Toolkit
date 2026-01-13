package io.netty.channel.epoll;

import io.netty.channel.DefaultSelectStrategyFactory;
import io.netty.channel.EventLoopTaskQueueFactory;
import io.netty.channel.IoEventLoop;
import io.netty.channel.IoHandlerFactory;
import io.netty.channel.MultiThreadIoEventLoopGroup;
import io.netty.channel.SelectStrategyFactory;
import io.netty.util.concurrent.EventExecutorChooserFactory;
import io.netty.util.concurrent.RejectedExecutionHandler;
import io.netty.util.concurrent.RejectedExecutionHandlers;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;

@Deprecated
public final class EpollEventLoopGroup extends MultiThreadIoEventLoopGroup {
   private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(EpollEventLoopGroup.class);

   public EpollEventLoopGroup() {
      this(0);
   }

   public EpollEventLoopGroup(int nThreads) {
      this(nThreads, (ThreadFactory)null);
   }

   public EpollEventLoopGroup(ThreadFactory threadFactory) {
      this(0, threadFactory, 0);
   }

   public EpollEventLoopGroup(int nThreads, SelectStrategyFactory selectStrategyFactory) {
      this(nThreads, (ThreadFactory)null, selectStrategyFactory);
   }

   public EpollEventLoopGroup(int nThreads, ThreadFactory threadFactory) {
      this(nThreads, threadFactory, 0);
   }

   public EpollEventLoopGroup(int nThreads, Executor executor) {
      this(nThreads, executor, DefaultSelectStrategyFactory.INSTANCE);
   }

   public EpollEventLoopGroup(int nThreads, ThreadFactory threadFactory, SelectStrategyFactory selectStrategyFactory) {
      this(nThreads, threadFactory, 0, selectStrategyFactory);
   }

   @Deprecated
   public EpollEventLoopGroup(int nThreads, ThreadFactory threadFactory, int maxEventsAtOnce) {
      this(nThreads, threadFactory, maxEventsAtOnce, DefaultSelectStrategyFactory.INSTANCE);
   }

   @Deprecated
   public EpollEventLoopGroup(int nThreads, ThreadFactory threadFactory, int maxEventsAtOnce, SelectStrategyFactory selectStrategyFactory) {
      super(nThreads, threadFactory, EpollIoHandler.newFactory(maxEventsAtOnce, selectStrategyFactory), RejectedExecutionHandlers.reject());
      Epoll.ensureAvailability();
   }

   public EpollEventLoopGroup(int nThreads, Executor executor, SelectStrategyFactory selectStrategyFactory) {
      super(nThreads, executor, EpollIoHandler.newFactory(0, selectStrategyFactory), RejectedExecutionHandlers.reject());
      Epoll.ensureAvailability();
   }

   public EpollEventLoopGroup(int nThreads, Executor executor, EventExecutorChooserFactory chooserFactory, SelectStrategyFactory selectStrategyFactory) {
      super(nThreads, executor, EpollIoHandler.newFactory(0, selectStrategyFactory), chooserFactory, RejectedExecutionHandlers.reject());
      Epoll.ensureAvailability();
   }

   public EpollEventLoopGroup(
      int nThreads,
      Executor executor,
      EventExecutorChooserFactory chooserFactory,
      SelectStrategyFactory selectStrategyFactory,
      RejectedExecutionHandler rejectedExecutionHandler
   ) {
      super(nThreads, executor, EpollIoHandler.newFactory(0, selectStrategyFactory), chooserFactory, rejectedExecutionHandler);
      Epoll.ensureAvailability();
   }

   public EpollEventLoopGroup(
      int nThreads,
      Executor executor,
      EventExecutorChooserFactory chooserFactory,
      SelectStrategyFactory selectStrategyFactory,
      RejectedExecutionHandler rejectedExecutionHandler,
      EventLoopTaskQueueFactory queueFactory
   ) {
      super(nThreads, executor, EpollIoHandler.newFactory(0, selectStrategyFactory), chooserFactory, rejectedExecutionHandler, queueFactory);
      Epoll.ensureAvailability();
   }

   public EpollEventLoopGroup(
      int nThreads,
      Executor executor,
      EventExecutorChooserFactory chooserFactory,
      SelectStrategyFactory selectStrategyFactory,
      RejectedExecutionHandler rejectedExecutionHandler,
      EventLoopTaskQueueFactory taskQueueFactory,
      EventLoopTaskQueueFactory tailTaskQueueFactory
   ) {
      super(
         nThreads,
         executor,
         EpollIoHandler.newFactory(0, selectStrategyFactory),
         chooserFactory,
         rejectedExecutionHandler,
         taskQueueFactory,
         tailTaskQueueFactory
      );
      Epoll.ensureAvailability();
   }

   @Deprecated
   public void setIoRatio(int ioRatio) {
      LOGGER.debug("EpollEventLoopGroup.setIoRatio(int) logic was removed, this is a no-op");
   }

   @Override
   protected IoEventLoop newChild(Executor executor, IoHandlerFactory ioHandlerFactory, Object... args) {
      RejectedExecutionHandler rejectedExecutionHandler = (RejectedExecutionHandler)args[0];
      EventLoopTaskQueueFactory taskQueueFactory = null;
      EventLoopTaskQueueFactory tailTaskQueueFactory = null;
      int argsLength = args.length;
      if (argsLength > 1) {
         taskQueueFactory = (EventLoopTaskQueueFactory)args[1];
      }

      if (argsLength > 2) {
         tailTaskQueueFactory = (EventLoopTaskQueueFactory)args[2];
      }

      return new EpollEventLoop(this, executor, ioHandlerFactory, taskQueueFactory, tailTaskQueueFactory, rejectedExecutionHandler);
   }
}
