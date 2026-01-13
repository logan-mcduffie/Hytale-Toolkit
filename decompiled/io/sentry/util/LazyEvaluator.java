package io.sentry.util;

import io.sentry.ISentryLifecycleToken;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class LazyEvaluator<T> {
   @Nullable
   private volatile T value = (T)null;
   @NotNull
   private final LazyEvaluator.Evaluator<T> evaluator;
   @NotNull
   private final AutoClosableReentrantLock lock = new AutoClosableReentrantLock();

   public LazyEvaluator(@NotNull LazyEvaluator.Evaluator<T> evaluator) {
      this.evaluator = evaluator;
   }

   @NotNull
   public T getValue() {
      if (this.value == null) {
         ISentryLifecycleToken ignored = this.lock.acquire();

         try {
            if (this.value == null) {
               this.value = this.evaluator.evaluate();
            }
         } catch (Throwable var5) {
            if (ignored != null) {
               try {
                  ignored.close();
               } catch (Throwable var4) {
                  var5.addSuppressed(var4);
               }
            }

            throw var5;
         }

         if (ignored != null) {
            ignored.close();
         }
      }

      return this.value;
   }

   public void setValue(@Nullable T value) {
      ISentryLifecycleToken ignored = this.lock.acquire();

      try {
         this.value = value;
      } catch (Throwable var6) {
         if (ignored != null) {
            try {
               ignored.close();
            } catch (Throwable var5) {
               var6.addSuppressed(var5);
            }
         }

         throw var6;
      }

      if (ignored != null) {
         ignored.close();
      }
   }

   public void resetValue() {
      ISentryLifecycleToken ignored = this.lock.acquire();

      try {
         this.value = null;
      } catch (Throwable var5) {
         if (ignored != null) {
            try {
               ignored.close();
            } catch (Throwable var4) {
               var5.addSuppressed(var4);
            }
         }

         throw var5;
      }

      if (ignored != null) {
         ignored.close();
      }
   }

   public interface Evaluator<T> {
      @NotNull
      T evaluate();
   }
}
