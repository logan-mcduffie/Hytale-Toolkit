package com.google.common.flogger.backend;

import com.google.common.flogger.AbstractLogger;
import com.google.common.flogger.LogSite;
import com.google.common.flogger.context.ContextDataProvider;
import com.google.common.flogger.context.Tags;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public abstract class Platform {
   private static String DEFAULT_PLATFORM = "com.google.common.flogger.backend.system.DefaultPlatform";
   private static final String[] AVAILABLE_PLATFORMS = new String[]{DEFAULT_PLATFORM};

   public static Platform.LogCallerFinder getCallerFinder() {
      return Platform.LazyHolder.INSTANCE.getCallerFinderImpl();
   }

   protected abstract Platform.LogCallerFinder getCallerFinderImpl();

   public static LoggerBackend getBackend(String className) {
      return Platform.LazyHolder.INSTANCE.getBackendImpl(className);
   }

   protected abstract LoggerBackend getBackendImpl(String var1);

   public static ContextDataProvider getContextDataProvider() {
      return Platform.LazyHolder.INSTANCE.getContextDataProviderImpl();
   }

   protected ContextDataProvider getContextDataProviderImpl() {
      return NoOpContextDataProvider.getInstance();
   }

   public static boolean shouldForceLogging(String loggerName, Level level, boolean isEnabled) {
      return getContextDataProvider().shouldForceLogging(loggerName, level, isEnabled);
   }

   public static Tags getInjectedTags() {
      return getContextDataProvider().getTags();
   }

   public static Metadata getInjectedMetadata() {
      return getContextDataProvider().getMetadata();
   }

   public static long getCurrentTimeNanos() {
      return Platform.LazyHolder.INSTANCE.getCurrentTimeNanosImpl();
   }

   protected long getCurrentTimeNanosImpl() {
      return TimeUnit.MILLISECONDS.toNanos(System.currentTimeMillis());
   }

   public static String getConfigInfo() {
      return Platform.LazyHolder.INSTANCE.getConfigInfoImpl();
   }

   protected abstract String getConfigInfoImpl();

   private static final class LazyHolder {
      private static final Platform INSTANCE = loadFirstAvailablePlatform(Platform.AVAILABLE_PLATFORMS);

      private static Platform loadFirstAvailablePlatform(String[] platformClass) {
         Platform platform = null;

         try {
            platform = PlatformProvider.getPlatform();
         } catch (NoClassDefFoundError var8) {
         }

         if (platform != null) {
            return platform;
         } else {
            StringBuilder errorMessage = new StringBuilder();

            for (String clazz : platformClass) {
               try {
                  return (Platform)Class.forName(clazz).getConstructor().newInstance();
               } catch (Throwable var9) {
                  Throwable e = var9;
                  if (var9 instanceof InvocationTargetException) {
                     e = var9.getCause();
                  }

                  errorMessage.append('\n').append(clazz).append(": ").append(e);
               }
            }

            throw new IllegalStateException(errorMessage.insert(0, "No logging platforms found:").toString());
         }
      }
   }

   public abstract static class LogCallerFinder {
      public abstract String findLoggingClass(Class<? extends AbstractLogger<?>> var1);

      public abstract LogSite findLogSite(Class<?> var1, int var2);
   }
}
