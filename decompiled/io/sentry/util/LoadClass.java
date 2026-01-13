package io.sentry.util;

import io.sentry.ILogger;
import io.sentry.SentryLevel;
import io.sentry.SentryOptions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LoadClass {
   @Nullable
   public Class<?> loadClass(@NotNull String clazz, @Nullable ILogger logger) {
      try {
         return Class.forName(clazz);
      } catch (ClassNotFoundException var4) {
         if (logger != null) {
            logger.log(SentryLevel.INFO, "Class not available: " + clazz);
         }
      } catch (UnsatisfiedLinkError var5) {
         if (logger != null) {
            logger.log(SentryLevel.ERROR, "Failed to load (UnsatisfiedLinkError) " + clazz, var5);
         }
      } catch (Throwable var6) {
         if (logger != null) {
            logger.log(SentryLevel.ERROR, "Failed to initialize " + clazz, var6);
         }
      }

      return null;
   }

   public boolean isClassAvailable(@NotNull String clazz, @Nullable ILogger logger) {
      return this.loadClass(clazz, logger) != null;
   }

   public boolean isClassAvailable(@NotNull String clazz, @Nullable SentryOptions options) {
      return this.isClassAvailable(clazz, options != null ? options.getLogger() : null);
   }

   public LazyEvaluator<Boolean> isClassAvailableLazy(@NotNull String clazz, @Nullable ILogger logger) {
      return new LazyEvaluator<>(() -> this.isClassAvailable(clazz, logger));
   }

   public LazyEvaluator<Boolean> isClassAvailableLazy(@NotNull String clazz, @Nullable SentryOptions options) {
      return new LazyEvaluator<>(() -> this.isClassAvailable(clazz, options));
   }
}
