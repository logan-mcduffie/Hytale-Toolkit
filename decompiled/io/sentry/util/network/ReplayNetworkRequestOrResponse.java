package io.sentry.util.network;

import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ReplayNetworkRequestOrResponse {
   @Nullable
   private final Long size;
   @Nullable
   private final NetworkBody body;
   @NotNull
   private final Map<String, String> headers;

   public ReplayNetworkRequestOrResponse(@Nullable Long size, @Nullable NetworkBody body, @NotNull Map<String, String> headers) {
      this.size = size;
      this.body = body;
      this.headers = headers;
   }

   @Nullable
   public Long getSize() {
      return this.size;
   }

   @Nullable
   public NetworkBody getBody() {
      return this.body;
   }

   @NotNull
   public Map<String, String> getHeaders() {
      return this.headers;
   }

   @Override
   public String toString() {
      return "ReplayNetworkRequestOrResponse{size=" + this.size + ", body=" + this.body + ", headers=" + this.headers + '}';
   }
}
