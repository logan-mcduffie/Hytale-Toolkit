package io.sentry.rrweb;

import io.sentry.ILogger;
import io.sentry.JsonDeserializer;
import io.sentry.JsonSerializable;
import io.sentry.ObjectReader;
import io.sentry.ObjectWriter;
import io.sentry.util.Objects;
import java.io.IOException;
import org.jetbrains.annotations.NotNull;

public abstract class RRWebIncrementalSnapshotEvent extends RRWebEvent {
   private RRWebIncrementalSnapshotEvent.IncrementalSource source;

   public RRWebIncrementalSnapshotEvent(@NotNull RRWebIncrementalSnapshotEvent.IncrementalSource source) {
      super(RRWebEventType.IncrementalSnapshot);
      this.source = source;
   }

   public RRWebIncrementalSnapshotEvent.IncrementalSource getSource() {
      return this.source;
   }

   public void setSource(RRWebIncrementalSnapshotEvent.IncrementalSource source) {
      this.source = source;
   }

   public static final class Deserializer {
      public boolean deserializeValue(
         @NotNull RRWebIncrementalSnapshotEvent baseEvent, @NotNull String nextName, @NotNull ObjectReader reader, @NotNull ILogger logger
      ) throws Exception {
         if (nextName.equals("source")) {
            baseEvent.source = Objects.requireNonNull(reader.nextOrNull(logger, new RRWebIncrementalSnapshotEvent.IncrementalSource.Deserializer()), "");
            return true;
         } else {
            return false;
         }
      }
   }

   public static enum IncrementalSource implements JsonSerializable {
      Mutation,
      MouseMove,
      MouseInteraction,
      Scroll,
      ViewportResize,
      Input,
      TouchMove,
      MediaInteraction,
      StyleSheetRule,
      CanvasMutation,
      Font,
      Log,
      Drag,
      StyleDeclaration,
      Selection,
      AdoptedStyleSheet,
      CustomElement;

      @Override
      public void serialize(@NotNull ObjectWriter writer, @NotNull ILogger logger) throws IOException {
         writer.value((long)this.ordinal());
      }

      public static final class Deserializer implements JsonDeserializer<RRWebIncrementalSnapshotEvent.IncrementalSource> {
         @NotNull
         public RRWebIncrementalSnapshotEvent.IncrementalSource deserialize(@NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
            return RRWebIncrementalSnapshotEvent.IncrementalSource.values()[reader.nextInt()];
         }
      }
   }

   public static final class JsonKeys {
      public static final String SOURCE = "source";
   }

   public static final class Serializer {
      public void serialize(@NotNull RRWebIncrementalSnapshotEvent baseEvent, @NotNull ObjectWriter writer, @NotNull ILogger logger) throws IOException {
         writer.name("source").value(logger, baseEvent.source);
      }
   }
}
