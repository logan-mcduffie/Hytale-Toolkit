package io.netty.channel.kqueue;

import io.netty.channel.Channel;
import io.netty.channel.DefaultSelectStrategyFactory;
import io.netty.channel.EventLoopTaskQueueFactory;
import io.netty.channel.IoEventLoop;
import io.netty.channel.IoEventLoopGroup;
import io.netty.channel.IoHandlerFactory;
import io.netty.channel.MultiThreadIoEventLoopGroup;
import io.netty.channel.SelectStrategyFactory;
import io.netty.channel.SingleThreadIoEventLoop;
import io.netty.util.concurrent.EventExecutorChooserFactory;
import io.netty.util.concurrent.RejectedExecutionHandler;
import io.netty.util.concurrent.RejectedExecutionHandlers;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;

@Deprecated
public final class KQueueEventLoopGroup extends MultiThreadIoEventLoopGroup {
   private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(KQueueEventLoopGroup.class);

   public KQueueEventLoopGroup() {
      this(0);
   }

   public KQueueEventLoopGroup(int nThreads) {
      this(nThreads, (ThreadFactory)null);
   }

   public KQueueEventLoopGroup(ThreadFactory threadFactory) {
      this(0, threadFactory, 0);
   }

   public KQueueEventLoopGroup(int nThreads, SelectStrategyFactory selectStrategyFactory) {
      this(nThreads, (ThreadFactory)null, selectStrategyFactory);
   }

   public KQueueEventLoopGroup(int nThreads, ThreadFactory threadFactory) {
      this(nThreads, threadFactory, 0);
   }

   public KQueueEventLoopGroup(int nThreads, Executor executor) {
      this(nThreads, executor, DefaultSelectStrategyFactory.INSTANCE);
   }

   public KQueueEventLoopGroup(int nThreads, ThreadFactory threadFactory, SelectStrategyFactory selectStrategyFactory) {
      this(nThreads, threadFactory, 0, selectStrategyFactory);
   }

   @Deprecated
   public KQueueEventLoopGroup(int nThreads, ThreadFactory threadFactory, int maxEventsAtOnce) {
      this(nThreads, threadFactory, maxEventsAtOnce, DefaultSelectStrategyFactory.INSTANCE);
   }

   @Deprecated
   public KQueueEventLoopGroup(int nThreads, ThreadFactory threadFactory, int maxEventsAtOnce, SelectStrategyFactory selectStrategyFactory) {
      super(nThreads, threadFactory, KQueueIoHandler.newFactory(maxEventsAtOnce, selectStrategyFactory), RejectedExecutionHandlers.reject());
      KQueue.ensureAvailability();
   }

   public KQueueEventLoopGroup(int nThreads, Executor executor, SelectStrategyFactory selectStrategyFactory) {
      super(nThreads, executor, KQueueIoHandler.newFactory(0, selectStrategyFactory), RejectedExecutionHandlers.reject());
      KQueue.ensureAvailability();
   }

   public KQueueEventLoopGroup(int nThreads, Executor executor, EventExecutorChooserFactory chooserFactory, SelectStrategyFactory selectStrategyFactory) {
      super(nThreads, executor, KQueueIoHandler.newFactory(0, selectStrategyFactory), chooserFactory, RejectedExecutionHandlers.reject());
      KQueue.ensureAvailability();
   }

   public KQueueEventLoopGroup(
      int nThreads,
      Executor executor,
      EventExecutorChooserFactory chooserFactory,
      SelectStrategyFactory selectStrategyFactory,
      RejectedExecutionHandler rejectedExecutionHandler
   ) {
      super(nThreads, executor, KQueueIoHandler.newFactory(0, selectStrategyFactory), chooserFactory, rejectedExecutionHandler);
      KQueue.ensureAvailability();
   }

   public KQueueEventLoopGroup(
      int nThreads,
      Executor executor,
      EventExecutorChooserFactory chooserFactory,
      SelectStrategyFactory selectStrategyFactory,
      RejectedExecutionHandler rejectedExecutionHandler,
      EventLoopTaskQueueFactory queueFactory
   ) {
      super(nThreads, executor, KQueueIoHandler.newFactory(0, selectStrategyFactory), chooserFactory, rejectedExecutionHandler, queueFactory);
      KQueue.ensureAvailability();
   }

   public KQueueEventLoopGroup(
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
         KQueueIoHandler.newFactory(0, selectStrategyFactory),
         chooserFactory,
         rejectedExecutionHandler,
         taskQueueFactory,
         tailTaskQueueFactory
      );
      KQueue.ensureAvailability();
   }

   @Deprecated
   public void setIoRatio(int ioRatio) {
      LOGGER.debug("EpollEventLoopGroup.setIoRatio(int) logic was removed, this is a no-op");
   }

   @Override
   protected IoEventLoop newChild(Executor executor, IoHandlerFactory ioHandlerFactory, Object... args) {
      RejectedExecutionHandler rejectedExecutionHandler = null;
      EventLoopTaskQueueFactory taskQueueFactory = null;
      EventLoopTaskQueueFactory tailTaskQueueFactory = null;
      int argsLength = args.length;
      if (argsLength > 0) {
         rejectedExecutionHandler = (RejectedExecutionHandler)args[0];
      }

      if (argsLength > 1) {
         taskQueueFactory = (EventLoopTaskQueueFactory)args[2];
      }

      if (argsLength > 2) {
         tailTaskQueueFactory = (EventLoopTaskQueueFactory)args[1];
      }

      return new KQueueEventLoopGroup.KQueueEventLoop(
         this,
         executor,
         ioHandlerFactory,
         KQueueEventLoopGroup.KQueueEventLoop.newTaskQueue(taskQueueFactory),
         KQueueEventLoopGroup.KQueueEventLoop.newTaskQueue(tailTaskQueueFactory),
         rejectedExecutionHandler
      );
   }

   private static final class KQueueEventLoop extends SingleThreadIoEventLoop {
      KQueueEventLoop(
         IoEventLoopGroup parent,
         Executor executor,
         IoHandlerFactory ioHandlerFactory,
         Queue<Runnable> taskQueue,
         Queue<Runnable> tailTaskQueue,
         RejectedExecutionHandler rejectedExecutionHandler
      ) {
         super(parent, executor, ioHandlerFactory, taskQueue, tailTaskQueue, rejectedExecutionHandler);
      }

      static Queue<Runnable> newTaskQueue(EventLoopTaskQueueFactory queueFactory) {
         return queueFactory == null ? newTaskQueue0(DEFAULT_MAX_PENDING_TASKS) : queueFactory.newTaskQueue(DEFAULT_MAX_PENDING_TASKS);
      }

      @Override
      public int registeredChannels() {
         assert this.inEventLoop();

         return ((KQueueIoHandler)this.ioHandler()).numRegisteredChannels();
      }

      @Override
      public Iterator<Channel> registeredChannelsIterator() {
         assert this.inEventLoop();

         return ((KQueueIoHandler)this.ioHandler()).registeredChannelsList().iterator();
      }
   }
}
