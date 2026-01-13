package io.netty.channel.nio;

import io.netty.channel.Channel;
import io.netty.channel.EventLoopTaskQueueFactory;
import io.netty.channel.IoHandlerFactory;
import io.netty.channel.IoRegistration;
import io.netty.channel.SingleThreadEventLoop;
import io.netty.channel.SingleThreadIoEventLoop;
import io.netty.util.concurrent.RejectedExecutionHandler;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.Executor;

@Deprecated
public final class NioEventLoop extends SingleThreadIoEventLoop {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(NioEventLoop.class);

   NioEventLoop(
      NioEventLoopGroup parent,
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

   public SelectorProvider selectorProvider() {
      return ((NioIoHandler)this.ioHandler()).selectorProvider();
   }

   public void register(final SelectableChannel ch, final int interestOps, NioTask<?> task) {
      ObjectUtil.checkNotNull(ch, "ch");
      if (interestOps == 0) {
         throw new IllegalArgumentException("interestOps must be non-zero.");
      } else if ((interestOps & ~ch.validOps()) != 0) {
         throw new IllegalArgumentException("invalid interestOps: " + interestOps + "(validOps: " + ch.validOps() + ')');
      } else {
         ObjectUtil.checkNotNull(task, "task");
         if (this.isShutdown()) {
            throw new IllegalStateException("event loop shut down");
         } else {
            final NioTask<SelectableChannel> nioTask = (NioTask<SelectableChannel>)task;
            if (this.inEventLoop()) {
               this.register0(ch, interestOps, (NioTask<SelectableChannel>)task);
            } else {
               try {
                  this.submit(new Runnable() {
                     @Override
                     public void run() {
                        NioEventLoop.this.register0(ch, interestOps, nioTask);
                     }
                  }).sync();
               } catch (InterruptedException var6) {
                  Thread.currentThread().interrupt();
               }
            }
         }
      }
   }

   private void register0(SelectableChannel ch, int interestOps, final NioTask<SelectableChannel> task) {
      try {
         IoRegistration registration = this.register(new NioSelectableChannelIoHandle<SelectableChannel>(ch) {
            @Override
            protected void handle(SelectableChannel channel, SelectionKey key) {
               try {
                  task.channelReady(channel, key);
               } catch (Exception var4) {
                  NioEventLoop.logger.warn("Unexpected exception while running NioTask.channelReady(...)", (Throwable)var4);
               }
            }

            @Override
            protected void deregister(SelectableChannel channel) {
               try {
                  task.channelUnregistered(channel, null);
               } catch (Exception var3) {
                  NioEventLoop.logger.warn("Unexpected exception while running NioTask.channelUnregistered(...)", (Throwable)var3);
               }
            }
         }).get();
         registration.submit(NioIoOps.valueOf(interestOps));
      } catch (Exception var5) {
         throw new IllegalStateException(var5);
      }
   }

   public int getIoRatio() {
      return 0;
   }

   @Deprecated
   public void setIoRatio(int ioRatio) {
      logger.debug("NioEventLoop.setIoRatio(int) logic was removed, this is a no-op");
   }

   public void rebuildSelector() {
      if (!this.inEventLoop()) {
         this.execute(new Runnable() {
            @Override
            public void run() {
               ((NioIoHandler)NioEventLoop.this.ioHandler()).rebuildSelector0();
            }
         });
      } else {
         ((NioIoHandler)this.ioHandler()).rebuildSelector0();
      }
   }

   @Override
   public int registeredChannels() {
      return ((NioIoHandler)this.ioHandler()).numRegistered();
   }

   @Override
   public Iterator<Channel> registeredChannelsIterator() {
      assert this.inEventLoop();

      final Set<SelectionKey> keys = ((NioIoHandler)this.ioHandler()).registeredSet();
      return keys.isEmpty() ? SingleThreadEventLoop.ChannelsReadOnlyIterator.empty() : new Iterator<Channel>() {
         final Iterator<SelectionKey> selectionKeyIterator = ObjectUtil.checkNotNull(keys, "selectionKeys").iterator();
         Channel next;
         boolean isDone;

         @Override
         public boolean hasNext() {
            if (this.isDone) {
               return false;
            } else {
               Channel cur = this.next;
               if (cur == null) {
                  cur = this.next = this.nextOrDone();
                  return cur != null;
               } else {
                  return true;
               }
            }
         }

         public Channel next() {
            if (this.isDone) {
               throw new NoSuchElementException();
            } else {
               Channel cur = this.next;
               if (cur == null) {
                  cur = this.nextOrDone();
                  if (cur == null) {
                     throw new NoSuchElementException();
                  }
               }

               this.next = this.nextOrDone();
               return cur;
            }
         }

         @Override
         public void remove() {
            throw new UnsupportedOperationException("remove");
         }

         private Channel nextOrDone() {
            Iterator<SelectionKey> it = this.selectionKeyIterator;

            while (it.hasNext()) {
               SelectionKey key = it.next();
               if (key.isValid()) {
                  Object attachment = key.attachment();
                  if (attachment instanceof NioIoHandler.DefaultNioRegistration) {
                     NioIoHandle handle = ((NioIoHandler.DefaultNioRegistration)attachment).handle();
                     if (handle instanceof AbstractNioChannel.AbstractNioUnsafe) {
                        return ((AbstractNioChannel.AbstractNioUnsafe)handle).channel();
                     }
                  }
               }
            }

            this.isDone = true;
            return null;
         }
      };
   }

   Selector unwrappedSelector() {
      return ((NioIoHandler)this.ioHandler()).unwrappedSelector();
   }
}
