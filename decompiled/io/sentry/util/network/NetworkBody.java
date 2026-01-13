package io.sentry.util.network;

import java.util.List;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class NetworkBody {
   @Nullable
   private final Object body;
   @Nullable
   private final List<NetworkBody.NetworkBodyWarning> warnings;

   public NetworkBody(@Nullable Object body) {
      this(body, null);
   }

   public NetworkBody(@Nullable Object body, @Nullable List<NetworkBody.NetworkBodyWarning> warnings) {
      this.body = body;
      this.warnings = warnings;
   }

   @Nullable
   public Object getBody() {
      return this.body;
   }

   @Nullable
   public List<NetworkBody.NetworkBodyWarning> getWarnings() {
      return this.warnings;
   }

   @Override
   public String toString() {
      return "NetworkBody{body=" + this.body + ", warnings=" + this.warnings + '}';
   }

   public static enum NetworkBodyWarning {
      JSON_TRUNCATED("JSON_TRUNCATED"),
      TEXT_TRUNCATED("TEXT_TRUNCATED"),
      INVALID_JSON("INVALID_JSON"),
      BODY_PARSE_ERROR("BODY_PARSE_ERROR");

      private final String value;

      private NetworkBodyWarning(String value) {
         this.value = value;
      }

      public String getValue() {
         return this.value;
      }
   }
}
