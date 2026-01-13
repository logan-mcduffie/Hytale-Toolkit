package io.sentry;

import java.util.concurrent.Callable;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;

public final class SentryWrapper {
   public static <U> Callable<U> wrapCallable(@NotNull Callable<U> callable) {
      IScopes newScopes = Sentry.getCurrentScopes().forkedScopes("SentryWrapper.wrapCallable");
      return () -> {
         ISentryLifecycleToken ignored = newScopes.makeCurrent();

         Object var3;
         try {
            var3 = callable.call();
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

         return (U)var3;
      };
   }

   public static <U> Supplier<U> wrapSupplier(@NotNull Supplier<U> supplier) {
      IScopes newScopes = Sentry.forkedScopes("SentryWrapper.wrapSupplier");
      return () -> {
         ISentryLifecycleToken ignore = newScopes.makeCurrent();

         Object var3;
         try {
            var3 = supplier.get();
         } catch (Throwable var6) {
            if (ignore != null) {
               try {
                  ignore.close();
               } catch (Throwable var5) {
                  var6.addSuppressed(var5);
               }
            }

            throw var6;
         }

         if (ignore != null) {
            ignore.close();
         }

         return (U)var3;
      };
   }

   public static Runnable wrapRunnable(@NotNull Runnable runnable) {
      IScopes newScopes = Sentry.forkedScopes("SentryWrapper.wrapRunnable");
      return () -> {
         ISentryLifecycleToken ignore = newScopes.makeCurrent();

         try {
            runnable.run();
         } catch (Throwable var6) {
            if (ignore != null) {
               try {
                  ignore.close();
               } catch (Throwable var5) {
                  var6.addSuppressed(var5);
               }
            }

            throw var6;
         }

         if (ignore != null) {
            ignore.close();
         }
      };
   }
}
