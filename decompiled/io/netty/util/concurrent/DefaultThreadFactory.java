package io.netty.util.concurrent;

import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;
import java.util.Locale;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class DefaultThreadFactory implements ThreadFactory {
   private static final AtomicInteger poolId = new AtomicInteger();
   private final AtomicInteger nextId = new AtomicInteger();
   private final String prefix;
   private final boolean daemon;
   private final int priority;
   protected final ThreadGroup threadGroup;

   public DefaultThreadFactory(Class<?> poolType) {
      this(poolType, false, 5);
   }

   public DefaultThreadFactory(String poolName) {
      this(poolName, false, 5);
   }

   public DefaultThreadFactory(Class<?> poolType, boolean daemon) {
      this(poolType, daemon, 5);
   }

   public DefaultThreadFactory(String poolName, boolean daemon) {
      this(poolName, daemon, 5);
   }

   public DefaultThreadFactory(Class<?> poolType, int priority) {
      this(poolType, false, priority);
   }

   public DefaultThreadFactory(String poolName, int priority) {
      this(poolName, false, priority);
   }

   public DefaultThreadFactory(Class<?> poolType, boolean daemon, int priority) {
      this(toPoolName(poolType), daemon, priority);
   }

   public static String toPoolName(Class<?> poolType) {
      ObjectUtil.checkNotNull(poolType, "poolType");
      String poolName = StringUtil.simpleClassName(poolType);
      switch (poolName.length()) {
         case 0:
            return "unknown";
         case 1:
            return poolName.toLowerCase(Locale.US);
         default:
            return Character.isUpperCase(poolName.charAt(0)) && Character.isLowerCase(poolName.charAt(1))
               ? Character.toLowerCase(poolName.charAt(0)) + poolName.substring(1)
               : poolName;
      }
   }

   public DefaultThreadFactory(String poolName, boolean daemon, int priority, ThreadGroup threadGroup) {
      ObjectUtil.checkNotNull(poolName, "poolName");
      if (priority >= 1 && priority <= 10) {
         this.prefix = poolName + '-' + poolId.incrementAndGet() + '-';
         this.daemon = daemon;
         this.priority = priority;
         this.threadGroup = threadGroup;
      } else {
         throw new IllegalArgumentException("priority: " + priority + " (expected: Thread.MIN_PRIORITY <= priority <= Thread.MAX_PRIORITY)");
      }
   }

   public DefaultThreadFactory(String poolName, boolean daemon, int priority) {
      this(poolName, daemon, priority, null);
   }

   @Override
   public Thread newThread(Runnable r) {
      Thread t = this.newThread(FastThreadLocalRunnable.wrap(r), this.prefix + this.nextId.incrementAndGet());

      try {
         if (t.isDaemon() != this.daemon) {
            t.setDaemon(this.daemon);
         }

         if (t.getPriority() != this.priority) {
            t.setPriority(this.priority);
         }
      } catch (Exception var4) {
      }

      return t;
   }

   protected Thread newThread(Runnable r, String name) {
      return new FastThreadLocalThread(this.threadGroup, r, name);
   }
}
