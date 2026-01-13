package io.sentry.transport;

import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class CurrentDateProvider implements ICurrentDateProvider {
   private static final ICurrentDateProvider instance = new CurrentDateProvider();

   public static ICurrentDateProvider getInstance() {
      return instance;
   }

   private CurrentDateProvider() {
   }

   @Override
   public final long getCurrentTimeMillis() {
      return System.currentTimeMillis();
   }
}
