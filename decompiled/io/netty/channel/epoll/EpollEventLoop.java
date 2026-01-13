package io.netty.channel.epoll;

import io.netty.channel.Channel;
import io.netty.channel.EventLoopTaskQueueFactory;
import io.netty.channel.IoEventLoopGroup;
import io.netty.channel.IoHandlerFactory;
import io.netty.channel.SingleThreadIoEventLoop;
import io.netty.util.concurrent.RejectedExecutionHandler;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;

@Deprecated
public class EpollEventLoop extends SingleThreadIoEventLoop {
   private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(EpollEventLoop.class);

   EpollEventLoop(IoEventLoopGroup parent, ThreadFactory threadFactory, IoHandlerFactory ioHandlerFactory) {
      super(parent, threadFactory, ioHandlerFactory);
   }

   EpollEventLoop(IoEventLoopGroup parent, Executor executor, IoHandlerFactory ioHandlerFactory) {
      super(parent, executor, ioHandlerFactory);
   }

   EpollEventLoop(
      IoEventLoopGroup parent,
      Executor executor,
      IoHandlerFactory ioHandlerFactory,
      EventLoopTaskQueueFactory taskQueueFactory,
      EventLoopTaskQueueFactory tailTaskQueueFactory,
      RejectedExecutionHandler rejectedExecutionHandler
   ) {
      super(parent, executor, ioHandlerFactory, newTaskQueue(taskQueueFactory), newTaskQueue(tailTaskQueueFactory), rejectedExecutionHandler);
   }

   private static Queue<Runnable> newTaskQueue(EventLoopTaskQueueFactory queueFactory) {
      return queueFactory == null ? newTaskQueue0(DEFAULT_MAX_PENDING_TASKS) : queueFactory.newTaskQueue(DEFAULT_MAX_PENDING_TASKS);
   }

   @Override
   public int registeredChannels() {
      return ((EpollIoHandler)this.ioHandler()).numRegisteredChannels();
   }

   @Override
   public Iterator<Channel> registeredChannelsIterator() {
      return ((EpollIoHandler)this.ioHandler()).registeredChannelsList().iterator();
   }

   public int getIoRatio() {
      return 0;
   }

   @Deprecated
   public void setIoRatio(int ioRatio) {
      LOGGER.debug("EpollEventLoop.setIoRatio(int) logic was removed, this is a no-op");
   }
}
