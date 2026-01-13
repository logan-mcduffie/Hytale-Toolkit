package io.sentry.logger;

import io.sentry.HostnameCache;
import io.sentry.IScope;
import io.sentry.ISpan;
import io.sentry.PropagationContext;
import io.sentry.Scopes;
import io.sentry.SentryAttribute;
import io.sentry.SentryAttributeType;
import io.sentry.SentryAttributes;
import io.sentry.SentryDate;
import io.sentry.SentryLevel;
import io.sentry.SentryLogEvent;
import io.sentry.SentryLogEventAttributeValue;
import io.sentry.SentryLogLevel;
import io.sentry.SentryOptions;
import io.sentry.SpanId;
import io.sentry.protocol.SdkVersion;
import io.sentry.protocol.SentryId;
import io.sentry.protocol.User;
import io.sentry.util.Platform;
import io.sentry.util.TracingUtils;
import java.util.HashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class LoggerApi implements ILoggerApi {
   @NotNull
   private final Scopes scopes;

   public LoggerApi(@NotNull Scopes scopes) {
      this.scopes = scopes;
   }

   @Override
   public void trace(@Nullable String message, @Nullable Object... args) {
      this.log(SentryLogLevel.TRACE, message, args);
   }

   @Override
   public void debug(@Nullable String message, @Nullable Object... args) {
      this.log(SentryLogLevel.DEBUG, message, args);
   }

   @Override
   public void info(@Nullable String message, @Nullable Object... args) {
      this.log(SentryLogLevel.INFO, message, args);
   }

   @Override
   public void warn(@Nullable String message, @Nullable Object... args) {
      this.log(SentryLogLevel.WARN, message, args);
   }

   @Override
   public void error(@Nullable String message, @Nullable Object... args) {
      this.log(SentryLogLevel.ERROR, message, args);
   }

   @Override
   public void fatal(@Nullable String message, @Nullable Object... args) {
      this.log(SentryLogLevel.FATAL, message, args);
   }

   @Override
   public void log(@NotNull SentryLogLevel level, @Nullable String message, @Nullable Object... args) {
      this.captureLog(level, SentryLogParameters.create(null, null), message, args);
   }

   @Override
   public void log(@NotNull SentryLogLevel level, @Nullable SentryDate timestamp, @Nullable String message, @Nullable Object... args) {
      this.captureLog(level, SentryLogParameters.create(timestamp, null), message, args);
   }

   @Override
   public void log(@NotNull SentryLogLevel level, @NotNull SentryLogParameters params, @Nullable String message, @Nullable Object... args) {
      this.captureLog(level, params, message, args);
   }

   private void captureLog(@NotNull SentryLogLevel level, @NotNull SentryLogParameters params, @Nullable String message, @Nullable Object... args) {
      SentryOptions options = this.scopes.getOptions();

      try {
         if (!this.scopes.isEnabled()) {
            options.getLogger().log(SentryLevel.WARNING, "Instance is disabled and this 'logger' call is a no-op.");
            return;
         }

         if (!options.getLogs().isEnabled()) {
            options.getLogger().log(SentryLevel.WARNING, "Sentry Log is disabled and this 'logger' call is a no-op.");
            return;
         }

         if (message == null) {
            return;
         }

         SentryDate timestamp = params.getTimestamp();
         SentryDate timestampToUse = timestamp == null ? options.getDateProvider().now() : timestamp;
         String messageToUse = this.maybeFormatMessage(message, args);
         IScope combinedScope = this.scopes.getCombinedScopeView();
         PropagationContext propagationContext = combinedScope.getPropagationContext();
         ISpan span = combinedScope.getSpan();
         if (span == null) {
            TracingUtils.maybeUpdateBaggage(combinedScope, options);
         }

         SentryId traceId = span == null ? propagationContext.getTraceId() : span.getSpanContext().getTraceId();
         SpanId spanId = span == null ? propagationContext.getSpanId() : span.getSpanContext().getSpanId();
         SentryLogEvent logEvent = new SentryLogEvent(traceId, timestampToUse, messageToUse, level);
         logEvent.setAttributes(this.createAttributes(params, message, spanId, args));
         logEvent.setSeverityNumber(level.getSeverityNumber());
         this.scopes.getClient().captureLog(logEvent, combinedScope);
      } catch (Throwable var15) {
         options.getLogger().log(SentryLevel.ERROR, "Error while capturing log event", var15);
      }
   }

   @NotNull
   private String maybeFormatMessage(@NotNull String message, @Nullable Object[] args) {
      if (args != null && args.length != 0) {
         try {
            return String.format(message, args);
         } catch (Throwable var4) {
            this.scopes.getOptions().getLogger().log(SentryLevel.ERROR, "Error while running log through String.format", var4);
            return message;
         }
      } else {
         return message;
      }
   }

   @NotNull
   private HashMap<String, SentryLogEventAttributeValue> createAttributes(
      @NotNull SentryLogParameters params, @NotNull String message, @NotNull SpanId spanId, @Nullable Object... args
   ) {
      HashMap<String, SentryLogEventAttributeValue> attributes = new HashMap<>();
      String origin = params.getOrigin();
      if (!"manual".equalsIgnoreCase(origin)) {
         attributes.put("sentry.origin", new SentryLogEventAttributeValue(SentryAttributeType.STRING, origin));
      }

      SentryAttributes incomingAttributes = params.getAttributes();
      if (incomingAttributes != null) {
         for (SentryAttribute attribute : incomingAttributes.getAttributes().values()) {
            Object value = attribute.getValue();
            SentryAttributeType type = attribute.getType() == null ? this.getType(value) : attribute.getType();
            attributes.put(attribute.getName(), new SentryLogEventAttributeValue(type, value));
         }
      }

      if (args != null) {
         int i = 0;

         for (Object arg : args) {
            SentryAttributeType type = this.getType(arg);
            attributes.put("sentry.message.parameter." + i, new SentryLogEventAttributeValue(type, arg));
            i++;
         }

         if (i > 0 && attributes.get("sentry.message.template") == null) {
            attributes.put("sentry.message.template", new SentryLogEventAttributeValue(SentryAttributeType.STRING, message));
         }
      }

      SdkVersion sdkVersion = this.scopes.getOptions().getSdkVersion();
      if (sdkVersion != null) {
         attributes.put("sentry.sdk.name", new SentryLogEventAttributeValue(SentryAttributeType.STRING, sdkVersion.getName()));
         attributes.put("sentry.sdk.version", new SentryLogEventAttributeValue(SentryAttributeType.STRING, sdkVersion.getVersion()));
      }

      String environment = this.scopes.getOptions().getEnvironment();
      if (environment != null) {
         attributes.put("sentry.environment", new SentryLogEventAttributeValue(SentryAttributeType.STRING, environment));
      }

      SentryId scopeReplayId = this.scopes.getCombinedScopeView().getReplayId();
      if (!SentryId.EMPTY_ID.equals(scopeReplayId)) {
         attributes.put("sentry.replay_id", new SentryLogEventAttributeValue(SentryAttributeType.STRING, scopeReplayId.toString()));
      } else {
         SentryId controllerReplayId = this.scopes.getOptions().getReplayController().getReplayId();
         if (!SentryId.EMPTY_ID.equals(controllerReplayId)) {
            attributes.put("sentry.replay_id", new SentryLogEventAttributeValue(SentryAttributeType.STRING, controllerReplayId.toString()));
            attributes.put("sentry._internal.replay_is_buffering", new SentryLogEventAttributeValue(SentryAttributeType.BOOLEAN, true));
         }
      }

      String release = this.scopes.getOptions().getRelease();
      if (release != null) {
         attributes.put("sentry.release", new SentryLogEventAttributeValue(SentryAttributeType.STRING, release));
      }

      attributes.put("sentry.trace.parent_span_id", new SentryLogEventAttributeValue(SentryAttributeType.STRING, spanId));
      if (Platform.isJvm()) {
         this.setServerName(attributes);
      }

      this.setUser(attributes);
      return attributes;
   }

   private void setServerName(@NotNull HashMap<String, SentryLogEventAttributeValue> attributes) {
      SentryOptions options = this.scopes.getOptions();
      String optionsServerName = options.getServerName();
      if (optionsServerName != null) {
         attributes.put("server.address", new SentryLogEventAttributeValue(SentryAttributeType.STRING, optionsServerName));
      } else if (options.isAttachServerName()) {
         String hostname = HostnameCache.getInstance().getHostname();
         if (hostname != null) {
            attributes.put("server.address", new SentryLogEventAttributeValue(SentryAttributeType.STRING, hostname));
         }
      }
   }

   private void setUser(@NotNull HashMap<String, SentryLogEventAttributeValue> attributes) {
      User user = this.scopes.getCombinedScopeView().getUser();
      if (user == null) {
         String id = this.scopes.getOptions().getDistinctId();
         if (id != null) {
            attributes.put("user.id", new SentryLogEventAttributeValue(SentryAttributeType.STRING, id));
         }
      } else {
         String id = user.getId();
         if (id != null) {
            attributes.put("user.id", new SentryLogEventAttributeValue(SentryAttributeType.STRING, id));
         }

         String username = user.getUsername();
         if (username != null) {
            attributes.put("user.name", new SentryLogEventAttributeValue(SentryAttributeType.STRING, username));
         }

         String email = user.getEmail();
         if (email != null) {
            attributes.put("user.email", new SentryLogEventAttributeValue(SentryAttributeType.STRING, email));
         }
      }
   }

   @NotNull
   private SentryAttributeType getType(@Nullable Object arg) {
      if (arg instanceof Boolean) {
         return SentryAttributeType.BOOLEAN;
      } else if (arg instanceof Integer) {
         return SentryAttributeType.INTEGER;
      } else {
         return arg instanceof Number ? SentryAttributeType.DOUBLE : SentryAttributeType.STRING;
      }
   }
}
