package io.sentry;

import io.sentry.exception.SentryEnvelopeException;
import io.sentry.protocol.SdkVersion;
import io.sentry.protocol.SentryId;
import io.sentry.util.Objects;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

public final class SentryEnvelope {
   @NotNull
   private final SentryEnvelopeHeader header;
   @NotNull
   private final Iterable<SentryEnvelopeItem> items;

   @Internal
   @NotNull
   public Iterable<SentryEnvelopeItem> getItems() {
      return this.items;
   }

   @Internal
   @NotNull
   public SentryEnvelopeHeader getHeader() {
      return this.header;
   }

   @Internal
   public SentryEnvelope(@NotNull SentryEnvelopeHeader header, @NotNull Iterable<SentryEnvelopeItem> items) {
      this.header = Objects.requireNonNull(header, "SentryEnvelopeHeader is required.");
      this.items = Objects.requireNonNull(items, "SentryEnvelope items are required.");
   }

   @Internal
   public SentryEnvelope(@Nullable SentryId eventId, @Nullable SdkVersion sdkVersion, @NotNull Iterable<SentryEnvelopeItem> items) {
      this.header = new SentryEnvelopeHeader(eventId, sdkVersion);
      this.items = Objects.requireNonNull(items, "SentryEnvelope items are required.");
   }

   @Internal
   public SentryEnvelope(@Nullable SentryId eventId, @Nullable SdkVersion sdkVersion, @NotNull SentryEnvelopeItem item) {
      Objects.requireNonNull(item, "SentryEnvelopeItem is required.");
      this.header = new SentryEnvelopeHeader(eventId, sdkVersion);
      List<SentryEnvelopeItem> items = new ArrayList<>(1);
      items.add(item);
      this.items = items;
   }

   @Internal
   @NotNull
   public static SentryEnvelope from(@NotNull ISerializer serializer, @NotNull Session session, @Nullable SdkVersion sdkVersion) throws IOException {
      Objects.requireNonNull(serializer, "Serializer is required.");
      Objects.requireNonNull(session, "session is required.");
      return new SentryEnvelope(null, sdkVersion, SentryEnvelopeItem.fromSession(serializer, session));
   }

   @Internal
   @NotNull
   public static SentryEnvelope from(@NotNull ISerializer serializer, @NotNull SentryBaseEvent event, @Nullable SdkVersion sdkVersion) throws IOException {
      Objects.requireNonNull(serializer, "Serializer is required.");
      Objects.requireNonNull(event, "item is required.");
      return new SentryEnvelope(event.getEventId(), sdkVersion, SentryEnvelopeItem.fromEvent(serializer, event));
   }

   @Internal
   @NotNull
   public static SentryEnvelope from(
      @NotNull ISerializer serializer, @NotNull ProfilingTraceData profilingTraceData, long maxTraceFileSize, @Nullable SdkVersion sdkVersion
   ) throws SentryEnvelopeException {
      Objects.requireNonNull(serializer, "Serializer is required.");
      Objects.requireNonNull(profilingTraceData, "Profiling trace data is required.");
      return new SentryEnvelope(
         new SentryId(profilingTraceData.getProfileId()), sdkVersion, SentryEnvelopeItem.fromProfilingTrace(profilingTraceData, maxTraceFileSize, serializer)
      );
   }
}
