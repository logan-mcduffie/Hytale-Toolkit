package io.netty.channel;

import io.netty.util.concurrent.EventExecutorChooserFactory;
import io.netty.util.internal.EmptyArrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;

public class MultiThreadIoEventLoopGroup extends MultithreadEventLoopGroup implements IoEventLoopGroup {
   public MultiThreadIoEventLoopGroup(IoHandlerFactory ioHandlerFactory) {
      this(0, ioHandlerFactory);
   }

   public MultiThreadIoEventLoopGroup(int nThreads, IoHandlerFactory ioHandlerFactory) {
      this(nThreads, (Executor)null, ioHandlerFactory);
   }

   public MultiThreadIoEventLoopGroup(ThreadFactory threadFactory, IoHandlerFactory ioHandlerFactory) {
      this(0, threadFactory, ioHandlerFactory);
   }

   public MultiThreadIoEventLoopGroup(Executor executor, IoHandlerFactory ioHandlerFactory) {
      super(0, executor, ioHandlerFactory);
   }

   public MultiThreadIoEventLoopGroup(int nThreads, Executor executor, IoHandlerFactory ioHandlerFactory) {
      super(nThreads, executor, ioHandlerFactory);
   }

   public MultiThreadIoEventLoopGroup(int nThreads, ThreadFactory threadFactory, IoHandlerFactory ioHandlerFactory) {
      super(nThreads, threadFactory, ioHandlerFactory);
   }

   public MultiThreadIoEventLoopGroup(int nThreads, Executor executor, EventExecutorChooserFactory chooserFactory, IoHandlerFactory ioHandlerFactory) {
      super(nThreads, executor, chooserFactory, ioHandlerFactory);
   }

   protected MultiThreadIoEventLoopGroup(int nThreads, Executor executor, IoHandlerFactory ioHandlerFactory, Object... args) {
      super(nThreads, executor, combine(ioHandlerFactory, args));
   }

   protected MultiThreadIoEventLoopGroup(int nThreads, ThreadFactory threadFactory, IoHandlerFactory ioHandlerFactory, Object... args) {
      super(nThreads, threadFactory, combine(ioHandlerFactory, args));
   }

   protected MultiThreadIoEventLoopGroup(
      int nThreads, ThreadFactory threadFactory, IoHandlerFactory ioHandlerFactory, EventExecutorChooserFactory chooserFactory, Object... args
   ) {
      super(nThreads, threadFactory, chooserFactory, combine(ioHandlerFactory, args));
   }

   protected MultiThreadIoEventLoopGroup(
      int nThreads, Executor executor, IoHandlerFactory ioHandlerFactory, EventExecutorChooserFactory chooserFactory, Object... args
   ) {
      super(nThreads, executor, chooserFactory, combine(ioHandlerFactory, args));
   }

   @Override
   protected EventLoop newChild(Executor executor, Object... args) throws Exception {
      IoHandlerFactory handlerFactory = (IoHandlerFactory)args[0];
      Object[] argsCopy;
      if (args.length > 1) {
         argsCopy = new Object[args.length - 1];
         System.arraycopy(args, 1, argsCopy, 0, argsCopy.length);
      } else {
         argsCopy = EmptyArrays.EMPTY_OBJECTS;
      }

      return this.newChild(executor, handlerFactory, argsCopy);
   }

   protected IoEventLoop newChild(Executor executor, IoHandlerFactory ioHandlerFactory, Object... args) {
      return new SingleThreadIoEventLoop(this, executor, ioHandlerFactory);
   }

   @Override
   public IoEventLoop next() {
      return (IoEventLoop)super.next();
   }

   private static Object[] combine(IoHandlerFactory handlerFactory, Object... args) {
      List<Object> combinedList = new ArrayList<>();
      combinedList.add(handlerFactory);
      if (args != null) {
         Collections.addAll(combinedList, args);
      }

      return combinedList.toArray(new Object[0]);
   }
}
