package io.sentry.util;

import io.sentry.FilterString;
import io.sentry.SentryOpenTelemetryMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

public final class SpanUtils {
   private static final Map<String, Boolean> ignoredSpanDecisionsCache = new ConcurrentHashMap<>();

   @NotNull
   public static List<String> ignoredSpanOriginsForOpenTelemetry(@NotNull SentryOpenTelemetryMode mode) {
      List<String> origins = new ArrayList<>();
      if (SentryOpenTelemetryMode.AGENT == mode || SentryOpenTelemetryMode.AGENTLESS_SPRING == mode) {
         origins.add("auto.http.spring_jakarta.webmvc");
         origins.add("auto.http.spring.webmvc");
         origins.add("auto.http.spring7.webmvc");
         origins.add("auto.spring_jakarta.webflux");
         origins.add("auto.spring.webflux");
         origins.add("auto.spring7.webflux");
         origins.add("auto.db.jdbc");
         origins.add("auto.http.spring_jakarta.webclient");
         origins.add("auto.http.spring.webclient");
         origins.add("auto.http.spring7.webclient");
         origins.add("auto.http.spring_jakarta.restclient");
         origins.add("auto.http.spring.restclient");
         origins.add("auto.http.spring7.restclient");
         origins.add("auto.http.spring_jakarta.resttemplate");
         origins.add("auto.http.spring.resttemplate");
         origins.add("auto.http.spring7.resttemplate");
         origins.add("auto.http.openfeign");
         origins.add("auto.http.ktor-client");
      }

      if (SentryOpenTelemetryMode.AGENT == mode) {
         origins.add("auto.graphql.graphql");
         origins.add("auto.graphql.graphql22");
      }

      return origins;
   }

   @Internal
   public static boolean isIgnored(@Nullable List<FilterString> ignoredOrigins, @Nullable String origin) {
      if (origin == null || ignoredOrigins == null || ignoredOrigins.isEmpty()) {
         return false;
      } else if (ignoredSpanDecisionsCache.containsKey(origin)) {
         return ignoredSpanDecisionsCache.get(origin);
      } else {
         for (FilterString ignoredOrigin : ignoredOrigins) {
            if (ignoredOrigin.getFilterString().equalsIgnoreCase(origin)) {
               ignoredSpanDecisionsCache.put(origin, true);
               return true;
            }
         }

         for (FilterString ignoredOriginx : ignoredOrigins) {
            try {
               if (ignoredOriginx.matches(origin)) {
                  ignoredSpanDecisionsCache.put(origin, true);
                  return true;
               }
            } catch (Throwable var5) {
            }
         }

         ignoredSpanDecisionsCache.put(origin, false);
         return false;
      }
   }
}
