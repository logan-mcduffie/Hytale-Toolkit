package io.sentry.util;

import io.sentry.CheckIn;
import io.sentry.CheckInStatus;
import io.sentry.DateUtils;
import io.sentry.FilterString;
import io.sentry.IScopes;
import io.sentry.ISentryLifecycleToken;
import io.sentry.MonitorConfig;
import io.sentry.Sentry;
import io.sentry.protocol.SentryId;
import java.util.List;
import java.util.concurrent.Callable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Experimental;
import org.jetbrains.annotations.ApiStatus.Internal;

@Experimental
public final class CheckInUtils {
   public static <U> U withCheckIn(
      @NotNull String monitorSlug, @Nullable String environment, @Nullable MonitorConfig monitorConfig, @NotNull Callable<U> callable
   ) throws Exception {
      ISentryLifecycleToken ignored = Sentry.forkedScopes("CheckInUtils").makeCurrent();

      Object t;
      try {
         IScopes scopes = Sentry.getCurrentScopes();
         long startTime = System.currentTimeMillis();
         boolean didError = false;
         TracingUtils.startNewTrace(scopes);
         CheckIn inProgressCheckIn = new CheckIn(monitorSlug, CheckInStatus.IN_PROGRESS);
         if (monitorConfig != null) {
            inProgressCheckIn.setMonitorConfig(monitorConfig);
         }

         if (environment != null) {
            inProgressCheckIn.setEnvironment(environment);
         }

         SentryId checkInId = scopes.captureCheckIn(inProgressCheckIn);

         try {
            t = callable.call();
         } catch (Throwable var22) {
            didError = true;
            throw var22;
         } finally {
            CheckInStatus status = didError ? CheckInStatus.ERROR : CheckInStatus.OK;
            CheckIn checkIn = new CheckIn(checkInId, monitorSlug, status);
            if (environment != null) {
               checkIn.setEnvironment(environment);
            }

            checkIn.setDuration(DateUtils.millisToSeconds(System.currentTimeMillis() - startTime));
            scopes.captureCheckIn(checkIn);
         }
      } catch (Throwable var24) {
         if (ignored != null) {
            try {
               ignored.close();
            } catch (Throwable var21) {
               var24.addSuppressed(var21);
            }
         }

         throw var24;
      }

      if (ignored != null) {
         ignored.close();
      }

      return (U)t;
   }

   public static <U> U withCheckIn(@NotNull String monitorSlug, @Nullable MonitorConfig monitorConfig, @NotNull Callable<U> callable) throws Exception {
      return withCheckIn(monitorSlug, null, monitorConfig, callable);
   }

   public static <U> U withCheckIn(@NotNull String monitorSlug, @Nullable String environment, @NotNull Callable<U> callable) throws Exception {
      return withCheckIn(monitorSlug, environment, null, callable);
   }

   public static <U> U withCheckIn(@NotNull String monitorSlug, @NotNull Callable<U> callable) throws Exception {
      return withCheckIn(monitorSlug, null, null, callable);
   }

   @Internal
   public static boolean isIgnored(@Nullable List<FilterString> ignoredSlugs, @NotNull String slug) {
      if (ignoredSlugs != null && !ignoredSlugs.isEmpty()) {
         for (FilterString ignoredSlug : ignoredSlugs) {
            if (ignoredSlug.getFilterString().equalsIgnoreCase(slug)) {
               return true;
            }
         }

         for (FilterString ignoredSlugx : ignoredSlugs) {
            try {
               if (ignoredSlugx.matches(slug)) {
                  return true;
               }
            } catch (Throwable var5) {
            }
         }

         return false;
      } else {
         return false;
      }
   }
}
