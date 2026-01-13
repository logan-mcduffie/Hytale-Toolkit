package io.netty.channel.nio;

import io.netty.channel.DefaultSelectStrategyFactory;
import io.netty.channel.EventLoopTaskQueueFactory;
import io.netty.channel.IoEventLoop;
import io.netty.channel.IoEventLoopGroup;
import io.netty.channel.IoHandlerFactory;
import io.netty.channel.MultiThreadIoEventLoopGroup;
import io.netty.channel.SelectStrategyFactory;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.EventExecutorChooserFactory;
import io.netty.util.concurrent.RejectedExecutionHandler;
import io.netty.util.concurrent.RejectedExecutionHandlers;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.nio.channels.spi.SelectorProvider;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;

@Deprecated
public class NioEventLoopGroup extends MultiThreadIoEventLoopGroup implements IoEventLoopGroup {
   private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(NioEventLoopGroup.class);

   public NioEventLoopGroup() {
      this(0);
   }

   public NioEventLoopGroup(int nThreads) {
      this(nThreads, (Executor)null);
   }

   public NioEventLoopGroup(ThreadFactory threadFactory) {
      this(0, threadFactory, SelectorProvider.provider());
   }

   public NioEventLoopGroup(int nThreads, ThreadFactory threadFactory) {
      this(nThreads, threadFactory, SelectorProvider.provider());
   }

   public NioEventLoopGroup(int nThreads, Executor executor) {
      this(nThreads, executor, SelectorProvider.provider());
   }

   public NioEventLoopGroup(int nThreads, ThreadFactory threadFactory, SelectorProvider selectorProvider) {
      this(nThreads, threadFactory, selectorProvider, DefaultSelectStrategyFactory.INSTANCE);
   }

   public NioEventLoopGroup(int nThreads, ThreadFactory threadFactory, SelectorProvider selectorProvider, SelectStrategyFactory selectStrategyFactory) {
      super(nThreads, threadFactory, NioIoHandler.newFactory(selectorProvider, selectStrategyFactory), RejectedExecutionHandlers.reject());
   }

   public NioEventLoopGroup(int nThreads, Executor executor, SelectorProvider selectorProvider) {
      this(nThreads, executor, selectorProvider, DefaultSelectStrategyFactory.INSTANCE);
   }

   public NioEventLoopGroup(int nThreads, Executor executor, SelectorProvider selectorProvider, SelectStrategyFactory selectStrategyFactory) {
      super(nThreads, executor, NioIoHandler.newFactory(selectorProvider, selectStrategyFactory), RejectedExecutionHandlers.reject());
   }

   public NioEventLoopGroup(
      int nThreads,
      Executor executor,
      EventExecutorChooserFactory chooserFactory,
      SelectorProvider selectorProvider,
      SelectStrategyFactory selectStrategyFactory
   ) {
      super(nThreads, executor, NioIoHandler.newFactory(selectorProvider, selectStrategyFactory), chooserFactory, RejectedExecutionHandlers.reject());
   }

   public NioEventLoopGroup(
      int nThreads,
      Executor executor,
      EventExecutorChooserFactory chooserFactory,
      SelectorProvider selectorProvider,
      SelectStrategyFactory selectStrategyFactory,
      RejectedExecutionHandler rejectedExecutionHandler
   ) {
      super(nThreads, executor, NioIoHandler.newFactory(selectorProvider, selectStrategyFactory), chooserFactory, rejectedExecutionHandler);
   }

   public NioEventLoopGroup(
      int nThreads,
      Executor executor,
      EventExecutorChooserFactory chooserFactory,
      SelectorProvider selectorProvider,
      SelectStrategyFactory selectStrategyFactory,
      RejectedExecutionHandler rejectedExecutionHandler,
      EventLoopTaskQueueFactory taskQueueFactory
   ) {
      super(nThreads, executor, NioIoHandler.newFactory(selectorProvider, selectStrategyFactory), chooserFactory, rejectedExecutionHandler, taskQueueFactory);
   }

   public NioEventLoopGroup(
      int nThreads,
      Executor executor,
      EventExecutorChooserFactory chooserFactory,
      SelectorProvider selectorProvider,
      SelectStrategyFactory selectStrategyFactory,
      RejectedExecutionHandler rejectedExecutionHandler,
      EventLoopTaskQueueFactory taskQueueFactory,
      EventLoopTaskQueueFactory tailTaskQueueFactory
   ) {
      super(
         nThreads,
         executor,
         NioIoHandler.newFactory(selectorProvider, selectStrategyFactory),
         chooserFactory,
         rejectedExecutionHandler,
         taskQueueFactory,
         tailTaskQueueFactory
      );
   }

   @Deprecated
   public void setIoRatio(int ioRatio) {
      LOGGER.debug("NioEventLoopGroup.setIoRatio(int) logic was removed, this is a no-op");
   }

   public void rebuildSelectors() {
      for (EventExecutor e : this) {
         ((NioEventLoop)e).rebuildSelector();
      }
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

      return new NioEventLoop(this, executor, ioHandlerFactory, taskQueueFactory, tailTaskQueueFactory, rejectedExecutionHandler);
   }
}
