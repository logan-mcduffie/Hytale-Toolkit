package io.netty.util;

import io.netty.util.internal.SystemPropertyUtil;
import java.io.Closeable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Supplier;

public class LeakPresenceDetector<T> extends ResourceLeakDetector<T> {
   private static final String TRACK_CREATION_STACK_PROPERTY = "io.netty.util.LeakPresenceDetector.trackCreationStack";
   private static final boolean TRACK_CREATION_STACK = SystemPropertyUtil.getBoolean("io.netty.util.LeakPresenceDetector.trackCreationStack", false);
   private static final LeakPresenceDetector.ResourceScope GLOBAL = new LeakPresenceDetector.ResourceScope("global");
   private static int staticInitializerCount;

   private static boolean inStaticInitializerSlow(StackTraceElement[] stackTrace) {
      for (StackTraceElement element : stackTrace) {
         if (element.getMethodName().equals("<clinit>")) {
            return true;
         }
      }

      return false;
   }

   private static boolean inStaticInitializerFast() {
      return staticInitializerCount != 0 && inStaticInitializerSlow(Thread.currentThread().getStackTrace());
   }

   public static <R> R staticInitializer(Supplier<R> supplier) {
      if (!inStaticInitializerSlow(Thread.currentThread().getStackTrace())) {
         throw new IllegalStateException("Not in static initializer.");
      } else {
         synchronized (LeakPresenceDetector.class) {
            staticInitializerCount++;
         }

         Object var14;
         try {
            var14 = supplier.get();
         } finally {
            synchronized (LeakPresenceDetector.class) {
               staticInitializerCount--;
            }
         }

         return (R)var14;
      }
   }

   public LeakPresenceDetector(Class<?> resourceType) {
      super(resourceType, 0);
   }

   @Deprecated
   public LeakPresenceDetector(Class<?> resourceType, int samplingInterval) {
      this(resourceType);
   }

   public LeakPresenceDetector(Class<?> resourceType, int samplingInterval, long maxActive) {
      this(resourceType);
   }

   protected LeakPresenceDetector.ResourceScope currentScope() throws LeakPresenceDetector.AllocationProhibitedException {
      return GLOBAL;
   }

   @Override
   public final ResourceLeakTracker<T> track(T obj) {
      return inStaticInitializerFast() ? null : this.trackForcibly(obj);
   }

   @Override
   public final ResourceLeakTracker<T> trackForcibly(T obj) {
      return new LeakPresenceDetector.PresenceTracker<>(this.currentScope());
   }

   @Override
   public final boolean isRecordEnabled() {
      return false;
   }

   public static void check() {
      ResourceLeakDetector<Object> detector = ResourceLeakDetectorFactory.instance().newResourceLeakDetector(Object.class);
      if (!(detector instanceof LeakPresenceDetector)) {
         throw new IllegalStateException(
            "LeakPresenceDetector not in use. Please register it using -Dio.netty.customResourceLeakDetector=" + LeakPresenceDetector.class.getName()
         );
      } else {
         ((LeakPresenceDetector)detector).currentScope().check();
      }
   }

   public static final class AllocationProhibitedException extends IllegalStateException {
      public AllocationProhibitedException(String s) {
         super(s);
      }
   }

   private static final class LeakCreation extends Throwable {
      final Thread thread = Thread.currentThread();
      String message;

      private LeakCreation() {
      }

      @Override
      public synchronized String getMessage() {
         if (this.message == null) {
            if (LeakPresenceDetector.inStaticInitializerSlow(this.getStackTrace())) {
               this.message = "Resource created in static initializer. Please wrap the static initializer in LeakPresenceDetector.staticInitializer so that this resource is excluded.";
            } else {
               this.message = "Resource created outside static initializer on thread '"
                  + this.thread.getName()
                  + "' ("
                  + this.thread.getState()
                  + "), likely leak.";
            }
         }

         return this.message;
      }
   }

   private static final class PresenceTracker<T> extends AtomicBoolean implements ResourceLeakTracker<T> {
      private final LeakPresenceDetector.ResourceScope scope;

      PresenceTracker(LeakPresenceDetector.ResourceScope scope) {
         super(false);
         this.scope = scope;
         scope.checkOpen();
         scope.openResourceCounter.increment();
         if (LeakPresenceDetector.TRACK_CREATION_STACK) {
            scope.creationStacks.put(this, new LeakPresenceDetector.LeakCreation());
         }
      }

      @Override
      public void record() {
      }

      @Override
      public void record(Object hint) {
      }

      @Override
      public boolean close(Object trackedObject) {
         if (this.compareAndSet(false, true)) {
            this.scope.openResourceCounter.decrement();
            if (LeakPresenceDetector.TRACK_CREATION_STACK) {
               this.scope.creationStacks.remove(this);
            }

            this.scope.checkOpen();
            return true;
         } else {
            return false;
         }
      }
   }

   public static final class ResourceScope implements Closeable {
      final String name;
      final LongAdder openResourceCounter = new LongAdder();
      final Map<LeakPresenceDetector.PresenceTracker<?>, Throwable> creationStacks = LeakPresenceDetector.TRACK_CREATION_STACK
         ? new ConcurrentHashMap<>()
         : null;
      boolean closed;

      public ResourceScope(String name) {
         this.name = name;
      }

      void checkOpen() {
         if (this.closed) {
            throw new LeakPresenceDetector.AllocationProhibitedException("Resource scope '" + this.name + "' already closed");
         }
      }

      void check() {
         long n = this.openResourceCounter.sumThenReset();
         if (n != 0L) {
            StringBuilder msg = new StringBuilder("Possible memory leak detected for resource scope '").append(this.name).append("'. ");
            if (n < 0L) {
               msg.append(
                  "Resource count was negative: A resource previously reported as a leak was released after all. Please ensure that that resource is released before its test finishes."
               );
               throw new IllegalStateException(msg.toString());
            } else if (!LeakPresenceDetector.TRACK_CREATION_STACK) {
               msg.append("Please use paranoid leak detection to get more information, or set -Dio.netty.util.LeakPresenceDetector.trackCreationStack=true");
               throw new IllegalStateException(msg.toString());
            } else {
               msg.append("Creation stack traces:");
               IllegalStateException ise = new IllegalStateException(msg.toString());
               int i = 0;

               for (Throwable t : this.creationStacks.values()) {
                  ise.addSuppressed(t);
                  if (i++ > 5) {
                     break;
                  }
               }

               this.creationStacks.clear();
               throw ise;
            }
         }
      }

      public boolean hasOpenResources() {
         return this.openResourceCounter.sum() > 0L;
      }

      @Override
      public void close() {
         this.closed = true;
         this.check();
      }
   }
}
