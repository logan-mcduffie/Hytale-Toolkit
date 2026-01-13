package io.netty.util.concurrent;

import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public final class NonStickyEventExecutorGroup implements EventExecutorGroup {
   private final EventExecutorGroup group;
   private final int maxTaskExecutePerRun;

   public NonStickyEventExecutorGroup(EventExecutorGroup group) {
      this(group, 1024);
   }

   public NonStickyEventExecutorGroup(EventExecutorGroup group, int maxTaskExecutePerRun) {
      this.group = verify(group);
      this.maxTaskExecutePerRun = ObjectUtil.checkPositive(maxTaskExecutePerRun, "maxTaskExecutePerRun");
   }

   private static EventExecutorGroup verify(EventExecutorGroup group) {
      for (EventExecutor executor : ObjectUtil.checkNotNull(group, "group")) {
         if (executor instanceof OrderedEventExecutor) {
            throw new IllegalArgumentException("EventExecutorGroup " + group + " contains OrderedEventExecutors: " + executor);
         }
      }

      return group;
   }

   private NonStickyEventExecutorGroup.NonStickyOrderedEventExecutor newExecutor(EventExecutor executor) {
      return new NonStickyEventExecutorGroup.NonStickyOrderedEventExecutor(executor, this.maxTaskExecutePerRun);
   }

   @Override
   public boolean isShuttingDown() {
      return this.group.isShuttingDown();
   }

   @Override
   public Future<?> shutdownGracefully() {
      return this.group.shutdownGracefully();
   }

   @Override
   public Future<?> shutdownGracefully(long quietPeriod, long timeout, TimeUnit unit) {
      return this.group.shutdownGracefully(quietPeriod, timeout, unit);
   }

   @Override
   public Future<?> terminationFuture() {
      return this.group.terminationFuture();
   }

   @Override
   public void shutdown() {
      this.group.shutdown();
   }

   @Override
   public List<Runnable> shutdownNow() {
      return this.group.shutdownNow();
   }

   @Override
   public EventExecutor next() {
      return this.newExecutor(this.group.next());
   }

   @Override
   public Iterator<EventExecutor> iterator() {
      final Iterator<EventExecutor> itr = this.group.iterator();
      return new Iterator<EventExecutor>() {
         @Override
         public boolean hasNext() {
            return itr.hasNext();
         }

         public EventExecutor next() {
            return NonStickyEventExecutorGroup.this.newExecutor(itr.next());
         }

         @Override
         public void remove() {
            itr.remove();
         }
      };
   }

   @Override
   public Future<?> submit(Runnable task) {
      return this.group.submit(task);
   }

   @Override
   public <T> Future<T> submit(Runnable task, T result) {
      return this.group.submit(task, result);
   }

   @Override
   public <T> Future<T> submit(Callable<T> task) {
      return this.group.submit(task);
   }

   @Override
   public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
      return this.group.schedule(command, delay, unit);
   }

   @Override
   public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
      return this.group.schedule(callable, delay, unit);
   }

   @Override
   public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
      return this.group.scheduleAtFixedRate(command, initialDelay, period, unit);
   }

   @Override
   public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
      return this.group.scheduleWithFixedDelay(command, initialDelay, delay, unit);
   }

   @Override
   public boolean isShutdown() {
      return this.group.isShutdown();
   }

   @Override
   public boolean isTerminated() {
      return this.group.isTerminated();
   }

   @Override
   public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
      return this.group.awaitTermination(timeout, unit);
   }

   @Override
   public <T> List<java.util.concurrent.Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
      return this.group.invokeAll(tasks);
   }

   @Override
   public <T> List<java.util.concurrent.Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
      return this.group.invokeAll(tasks, timeout, unit);
   }

   @Override
   public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
      return this.group.invokeAny(tasks);
   }

   @Override
   public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
      return this.group.invokeAny(tasks, timeout, unit);
   }

   @Override
   public void execute(Runnable command) {
      this.group.execute(command);
   }

   private static final class NonStickyOrderedEventExecutor extends AbstractEventExecutor implements Runnable, OrderedEventExecutor {
      private final EventExecutor executor;
      private final Queue<Runnable> tasks = PlatformDependent.newMpscQueue();
      private static final int NONE = 0;
      private static final int SUBMITTED = 1;
      private static final int RUNNING = 2;
      private final AtomicInteger state = new AtomicInteger();
      private final int maxTaskExecutePerRun;
      private final AtomicReference<Thread> executingThread = new AtomicReference<>();

      NonStickyOrderedEventExecutor(EventExecutor executor, int maxTaskExecutePerRun) {
         super(executor);
         this.executor = executor;
         this.maxTaskExecutePerRun = maxTaskExecutePerRun;
      }

      @Override
      public void run() {
         // $VF: Couldn't be decompiled
         // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
         // java.lang.RuntimeException: parsing failure!
         //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
         //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:184)
         //
         // Bytecode:
         // 000: aload 0
         // 001: getfield io/netty/util/concurrent/NonStickyEventExecutorGroup$NonStickyOrderedEventExecutor.state Ljava/util/concurrent/atomic/AtomicInteger;
         // 004: bipush 1
         // 005: bipush 2
         // 006: invokevirtual java/util/concurrent/atomic/AtomicInteger.compareAndSet (II)Z
         // 009: ifne 00d
         // 00c: return
         // 00d: invokestatic java/lang/Thread.currentThread ()Ljava/lang/Thread;
         // 010: astore 1
         // 011: aload 0
         // 012: getfield io/netty/util/concurrent/NonStickyEventExecutorGroup$NonStickyOrderedEventExecutor.executingThread Ljava/util/concurrent/atomic/AtomicReference;
         // 015: aload 1
         // 016: invokevirtual java/util/concurrent/atomic/AtomicReference.set (Ljava/lang/Object;)V
         // 019: bipush 0
         // 01a: istore 2
         // 01b: iload 2
         // 01c: aload 0
         // 01d: getfield io/netty/util/concurrent/NonStickyEventExecutorGroup$NonStickyOrderedEventExecutor.maxTaskExecutePerRun I
         // 020: if_icmpge 041
         // 023: aload 0
         // 024: getfield io/netty/util/concurrent/NonStickyEventExecutorGroup$NonStickyOrderedEventExecutor.tasks Ljava/util/Queue;
         // 027: invokeinterface java/util/Queue.poll ()Ljava/lang/Object; 1
         // 02c: checkcast java/lang/Runnable
         // 02f: astore 3
         // 030: aload 3
         // 031: ifnonnull 037
         // 034: goto 041
         // 037: aload 3
         // 038: invokestatic io/netty/util/concurrent/NonStickyEventExecutorGroup$NonStickyOrderedEventExecutor.safeExecute (Ljava/lang/Runnable;)V
         // 03b: iinc 2 1
         // 03e: goto 01b
         // 041: iload 2
         // 042: aload 0
         // 043: getfield io/netty/util/concurrent/NonStickyEventExecutorGroup$NonStickyOrderedEventExecutor.maxTaskExecutePerRun I
         // 046: if_icmpne 07a
         // 049: aload 0
         // 04a: getfield io/netty/util/concurrent/NonStickyEventExecutorGroup$NonStickyOrderedEventExecutor.state Ljava/util/concurrent/atomic/AtomicInteger;
         // 04d: bipush 1
         // 04e: invokevirtual java/util/concurrent/atomic/AtomicInteger.set (I)V
         // 051: aload 0
         // 052: getfield io/netty/util/concurrent/NonStickyEventExecutorGroup$NonStickyOrderedEventExecutor.executingThread Ljava/util/concurrent/atomic/AtomicReference;
         // 055: aload 1
         // 056: aconst_null
         // 057: invokevirtual java/util/concurrent/atomic/AtomicReference.compareAndSet (Ljava/lang/Object;Ljava/lang/Object;)Z
         // 05a: pop
         // 05b: aload 0
         // 05c: getfield io/netty/util/concurrent/NonStickyEventExecutorGroup$NonStickyOrderedEventExecutor.executor Lio/netty/util/concurrent/EventExecutor;
         // 05f: aload 0
         // 060: invokeinterface io/netty/util/concurrent/EventExecutor.execute (Ljava/lang/Runnable;)V 2
         // 065: return
         // 066: astore 3
         // 067: aload 0
         // 068: getfield io/netty/util/concurrent/NonStickyEventExecutorGroup$NonStickyOrderedEventExecutor.executingThread Ljava/util/concurrent/atomic/AtomicReference;
         // 06b: aload 1
         // 06c: invokevirtual java/util/concurrent/atomic/AtomicReference.set (Ljava/lang/Object;)V
         // 06f: aload 0
         // 070: getfield io/netty/util/concurrent/NonStickyEventExecutorGroup$NonStickyOrderedEventExecutor.state Ljava/util/concurrent/atomic/AtomicInteger;
         // 073: bipush 2
         // 074: invokevirtual java/util/concurrent/atomic/AtomicInteger.set (I)V
         // 077: goto 10f
         // 07a: aload 0
         // 07b: getfield io/netty/util/concurrent/NonStickyEventExecutorGroup$NonStickyOrderedEventExecutor.state Ljava/util/concurrent/atomic/AtomicInteger;
         // 07e: bipush 0
         // 07f: invokevirtual java/util/concurrent/atomic/AtomicInteger.set (I)V
         // 082: aload 0
         // 083: getfield io/netty/util/concurrent/NonStickyEventExecutorGroup$NonStickyOrderedEventExecutor.tasks Ljava/util/Queue;
         // 086: invokeinterface java/util/Queue.isEmpty ()Z 1
         // 08b: ifne 09a
         // 08e: aload 0
         // 08f: getfield io/netty/util/concurrent/NonStickyEventExecutorGroup$NonStickyOrderedEventExecutor.state Ljava/util/concurrent/atomic/AtomicInteger;
         // 092: bipush 0
         // 093: bipush 2
         // 094: invokevirtual java/util/concurrent/atomic/AtomicInteger.compareAndSet (II)Z
         // 097: ifne 10f
         // 09a: aload 0
         // 09b: getfield io/netty/util/concurrent/NonStickyEventExecutorGroup$NonStickyOrderedEventExecutor.executingThread Ljava/util/concurrent/atomic/AtomicReference;
         // 09e: aload 1
         // 09f: aconst_null
         // 0a0: invokevirtual java/util/concurrent/atomic/AtomicReference.compareAndSet (Ljava/lang/Object;Ljava/lang/Object;)Z
         // 0a3: pop
         // 0a4: return
         // 0a5: astore 4
         // 0a7: iload 2
         // 0a8: aload 0
         // 0a9: getfield io/netty/util/concurrent/NonStickyEventExecutorGroup$NonStickyOrderedEventExecutor.maxTaskExecutePerRun I
         // 0ac: if_icmpne 0e1
         // 0af: aload 0
         // 0b0: getfield io/netty/util/concurrent/NonStickyEventExecutorGroup$NonStickyOrderedEventExecutor.state Ljava/util/concurrent/atomic/AtomicInteger;
         // 0b3: bipush 1
         // 0b4: invokevirtual java/util/concurrent/atomic/AtomicInteger.set (I)V
         // 0b7: aload 0
         // 0b8: getfield io/netty/util/concurrent/NonStickyEventExecutorGroup$NonStickyOrderedEventExecutor.executingThread Ljava/util/concurrent/atomic/AtomicReference;
         // 0bb: aload 1
         // 0bc: aconst_null
         // 0bd: invokevirtual java/util/concurrent/atomic/AtomicReference.compareAndSet (Ljava/lang/Object;Ljava/lang/Object;)Z
         // 0c0: pop
         // 0c1: aload 0
         // 0c2: getfield io/netty/util/concurrent/NonStickyEventExecutorGroup$NonStickyOrderedEventExecutor.executor Lio/netty/util/concurrent/EventExecutor;
         // 0c5: aload 0
         // 0c6: invokeinterface io/netty/util/concurrent/EventExecutor.execute (Ljava/lang/Runnable;)V 2
         // 0cb: return
         // 0cc: astore 5
         // 0ce: aload 0
         // 0cf: getfield io/netty/util/concurrent/NonStickyEventExecutorGroup$NonStickyOrderedEventExecutor.executingThread Ljava/util/concurrent/atomic/AtomicReference;
         // 0d2: aload 1
         // 0d3: invokevirtual java/util/concurrent/atomic/AtomicReference.set (Ljava/lang/Object;)V
         // 0d6: aload 0
         // 0d7: getfield io/netty/util/concurrent/NonStickyEventExecutorGroup$NonStickyOrderedEventExecutor.state Ljava/util/concurrent/atomic/AtomicInteger;
         // 0da: bipush 2
         // 0db: invokevirtual java/util/concurrent/atomic/AtomicInteger.set (I)V
         // 0de: goto 10c
         // 0e1: aload 0
         // 0e2: getfield io/netty/util/concurrent/NonStickyEventExecutorGroup$NonStickyOrderedEventExecutor.state Ljava/util/concurrent/atomic/AtomicInteger;
         // 0e5: bipush 0
         // 0e6: invokevirtual java/util/concurrent/atomic/AtomicInteger.set (I)V
         // 0e9: aload 0
         // 0ea: getfield io/netty/util/concurrent/NonStickyEventExecutorGroup$NonStickyOrderedEventExecutor.tasks Ljava/util/Queue;
         // 0ed: invokeinterface java/util/Queue.isEmpty ()Z 1
         // 0f2: ifne 101
         // 0f5: aload 0
         // 0f6: getfield io/netty/util/concurrent/NonStickyEventExecutorGroup$NonStickyOrderedEventExecutor.state Ljava/util/concurrent/atomic/AtomicInteger;
         // 0f9: bipush 0
         // 0fa: bipush 2
         // 0fb: invokevirtual java/util/concurrent/atomic/AtomicInteger.compareAndSet (II)Z
         // 0fe: ifne 10c
         // 101: aload 0
         // 102: getfield io/netty/util/concurrent/NonStickyEventExecutorGroup$NonStickyOrderedEventExecutor.executingThread Ljava/util/concurrent/atomic/AtomicReference;
         // 105: aload 1
         // 106: aconst_null
         // 107: invokevirtual java/util/concurrent/atomic/AtomicReference.compareAndSet (Ljava/lang/Object;Ljava/lang/Object;)Z
         // 10a: pop
         // 10b: return
         // 10c: aload 4
         // 10e: athrow
         // 10f: goto 019
      }

      @Override
      public boolean inEventLoop(Thread thread) {
         return this.executingThread.get() == thread;
      }

      @Override
      public boolean isShuttingDown() {
         return this.executor.isShutdown();
      }

      @Override
      public Future<?> shutdownGracefully(long quietPeriod, long timeout, TimeUnit unit) {
         return this.executor.shutdownGracefully(quietPeriod, timeout, unit);
      }

      @Override
      public Future<?> terminationFuture() {
         return this.executor.terminationFuture();
      }

      @Override
      public void shutdown() {
         this.executor.shutdown();
      }

      @Override
      public boolean isShutdown() {
         return this.executor.isShutdown();
      }

      @Override
      public boolean isTerminated() {
         return this.executor.isTerminated();
      }

      @Override
      public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
         return this.executor.awaitTermination(timeout, unit);
      }

      @Override
      public void execute(Runnable command) {
         if (!this.tasks.offer(command)) {
            throw new RejectedExecutionException();
         } else {
            if (this.state.compareAndSet(0, 1)) {
               this.executor.execute(this);
            }
         }
      }
   }
}
