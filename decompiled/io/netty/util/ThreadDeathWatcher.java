package io.netty.util;

import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Deprecated
public final class ThreadDeathWatcher {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(ThreadDeathWatcher.class);
   static final ThreadFactory threadFactory;
   private static final Queue<ThreadDeathWatcher.Entry> pendingEntries = new ConcurrentLinkedQueue<>();
   private static final ThreadDeathWatcher.Watcher watcher = new ThreadDeathWatcher.Watcher();
   private static final AtomicBoolean started = new AtomicBoolean();
   private static volatile Thread watcherThread;

   public static void watch(Thread thread, Runnable task) {
      ObjectUtil.checkNotNull(thread, "thread");
      ObjectUtil.checkNotNull(task, "task");
      if (!thread.isAlive()) {
         throw new IllegalArgumentException("thread must be alive.");
      } else {
         schedule(thread, task, true);
      }
   }

   public static void unwatch(Thread thread, Runnable task) {
      schedule(ObjectUtil.checkNotNull(thread, "thread"), ObjectUtil.checkNotNull(task, "task"), false);
   }

   private static void schedule(Thread thread, Runnable task, boolean isWatch) {
      pendingEntries.add(new ThreadDeathWatcher.Entry(thread, task, isWatch));
      if (started.compareAndSet(false, true)) {
         final Thread watcherThread = threadFactory.newThread(watcher);
         AccessController.doPrivileged(new PrivilegedAction<Void>() {
            public Void run() {
               watcherThread.setContextClassLoader(null);
               return null;
            }
         });
         watcherThread.start();
         ThreadDeathWatcher.watcherThread = watcherThread;
      }
   }

   public static boolean awaitInactivity(long timeout, TimeUnit unit) throws InterruptedException {
      ObjectUtil.checkNotNull(unit, "unit");
      Thread watcherThread = ThreadDeathWatcher.watcherThread;
      if (watcherThread != null) {
         watcherThread.join(unit.toMillis(timeout));
         return !watcherThread.isAlive();
      } else {
         return true;
      }
   }

   private ThreadDeathWatcher() {
   }

   static {
      String poolName = "threadDeathWatcher";
      String serviceThreadPrefix = SystemPropertyUtil.get("io.netty.serviceThreadPrefix");
      if (!StringUtil.isNullOrEmpty(serviceThreadPrefix)) {
         poolName = serviceThreadPrefix + poolName;
      }

      threadFactory = new DefaultThreadFactory(poolName, true, 1, null);
   }

   private static final class Entry {
      final Thread thread;
      final Runnable task;
      final boolean isWatch;

      Entry(Thread thread, Runnable task, boolean isWatch) {
         this.thread = thread;
         this.task = task;
         this.isWatch = isWatch;
      }

      @Override
      public int hashCode() {
         return this.thread.hashCode() ^ this.task.hashCode();
      }

      @Override
      public boolean equals(Object obj) {
         if (obj == this) {
            return true;
         } else if (!(obj instanceof ThreadDeathWatcher.Entry)) {
            return false;
         } else {
            ThreadDeathWatcher.Entry that = (ThreadDeathWatcher.Entry)obj;
            return this.thread == that.thread && this.task == that.task;
         }
      }
   }

   private static final class Watcher implements Runnable {
      private final List<ThreadDeathWatcher.Entry> watchees = new ArrayList<>();

      private Watcher() {
      }

      @Override
      public void run() {
         while (true) {
            this.fetchWatchees();
            this.notifyWatchees();
            this.fetchWatchees();
            this.notifyWatchees();

            try {
               Thread.sleep(1000L);
            } catch (InterruptedException var2) {
            }

            if (this.watchees.isEmpty() && ThreadDeathWatcher.pendingEntries.isEmpty()) {
               boolean stopped = ThreadDeathWatcher.started.compareAndSet(true, false);

               assert stopped;

               if (ThreadDeathWatcher.pendingEntries.isEmpty() || !ThreadDeathWatcher.started.compareAndSet(false, true)) {
                  return;
               }
            }
         }
      }

      private void fetchWatchees() {
         while (true) {
            ThreadDeathWatcher.Entry e = ThreadDeathWatcher.pendingEntries.poll();
            if (e == null) {
               return;
            }

            if (e.isWatch) {
               this.watchees.add(e);
            } else {
               this.watchees.remove(e);
            }
         }
      }

      private void notifyWatchees() {
         List<ThreadDeathWatcher.Entry> watchees = this.watchees;
         int i = 0;

         while (i < watchees.size()) {
            ThreadDeathWatcher.Entry e = watchees.get(i);
            if (!e.thread.isAlive()) {
               watchees.remove(i);

               try {
                  e.task.run();
               } catch (Throwable var5) {
                  ThreadDeathWatcher.logger.warn("Thread death watcher task raised an exception:", var5);
               }
            } else {
               i++;
            }
         }
      }
   }
}
