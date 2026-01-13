package io.sentry;

import io.sentry.util.Objects;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

public final class RequestDetails {
   @NotNull
   private final URL url;
   @NotNull
   private final Map<String, String> headers;

   public RequestDetails(@NotNull String url, @NotNull Map<String, String> headers) {
      Objects.requireNonNull(url, "url is required");
      Objects.requireNonNull(headers, "headers is required");

      try {
         this.url = URI.create(url).toURL();
      } catch (MalformedURLException var4) {
         throw new IllegalArgumentException("Failed to compose the Sentry's server URL.", var4);
      }

      this.headers = headers;
   }

   @NotNull
   public URL getUrl() {
      return this.url;
   }

   @NotNull
   public Map<String, String> getHeaders() {
      return this.headers;
   }
}
