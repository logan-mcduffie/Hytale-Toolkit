package io.sentry.opentelemetry;

import io.sentry.NoOpLogger;
import io.sentry.SentryLevel;
import io.sentry.SentryOpenTelemetryMode;
import io.sentry.SentryOptions;
import io.sentry.util.LoadClass;
import io.sentry.util.Platform;
import io.sentry.util.SpanUtils;
import java.util.Collections;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class OpenTelemetryUtil {
   @Internal
   public static void applyIgnoredSpanOrigins(@NotNull SentryOptions options) {
      if (Platform.isJvm()) {
         for (String origin : ignoredSpanOrigins(options)) {
            options.addIgnoredSpanOrigin(origin);
         }
      }
   }

   @Internal
   public static void updateOpenTelemetryModeIfAuto(@NotNull SentryOptions options, @NotNull LoadClass loadClass) {
      if (Platform.isJvm()) {
         SentryOpenTelemetryMode openTelemetryMode = options.getOpenTelemetryMode();
         if (SentryOpenTelemetryMode.AUTO.equals(openTelemetryMode)) {
            if (loadClass.isClassAvailable("io.sentry.opentelemetry.agent.AgentMarker", NoOpLogger.getInstance())) {
               options.getLogger().log(SentryLevel.DEBUG, "openTelemetryMode has been inferred from AUTO to AGENT");
               options.setOpenTelemetryMode(SentryOpenTelemetryMode.AGENT);
               return;
            }

            if (loadClass.isClassAvailable("io.sentry.opentelemetry.agent.AgentlessMarker", NoOpLogger.getInstance())) {
               options.getLogger().log(SentryLevel.DEBUG, "openTelemetryMode has been inferred from AUTO to AGENTLESS");
               options.setOpenTelemetryMode(SentryOpenTelemetryMode.AGENTLESS);
               return;
            }

            if (loadClass.isClassAvailable("io.sentry.opentelemetry.agent.AgentlessSpringMarker", NoOpLogger.getInstance())) {
               options.getLogger().log(SentryLevel.DEBUG, "openTelemetryMode has been inferred from AUTO to AGENTLESS_SPRING");
               options.setOpenTelemetryMode(SentryOpenTelemetryMode.AGENTLESS_SPRING);
               return;
            }
         }
      }
   }

   @NotNull
   private static List<String> ignoredSpanOrigins(@NotNull SentryOptions options) {
      SentryOpenTelemetryMode openTelemetryMode = options.getOpenTelemetryMode();
      return SentryOpenTelemetryMode.OFF.equals(openTelemetryMode) ? Collections.emptyList() : SpanUtils.ignoredSpanOriginsForOpenTelemetry(openTelemetryMode);
   }
}
