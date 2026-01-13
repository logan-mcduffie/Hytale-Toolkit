package io.sentry;

import io.sentry.util.Objects;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Experimental;
import org.jetbrains.annotations.ApiStatus.Internal;

@Experimental
public final class RequestDetailsResolver {
   private static final String USER_AGENT = "User-Agent";
   private static final String SENTRY_AUTH = "X-Sentry-Auth";
   @NotNull
   private final Dsn dsn;
   @Nullable
   private final String sentryClientName;

   public RequestDetailsResolver(@NotNull String dsn, @Nullable String sentryClientName) {
      Objects.requireNonNull(dsn, "dsn is required");
      this.dsn = new Dsn(dsn);
      this.sentryClientName = sentryClientName;
   }

   @Internal
   public RequestDetailsResolver(@NotNull SentryOptions options) {
      Objects.requireNonNull(options, "options is required");
      this.dsn = options.retrieveParsedDsn();
      this.sentryClientName = options.getSentryClientName();
   }

   @NotNull
   public RequestDetails resolve() {
      URI sentryUri = this.dsn.getSentryUri();
      String envelopeUrl = sentryUri.resolve(sentryUri.getPath() + "/envelope/").toString();
      String publicKey = this.dsn.getPublicKey();
      String secretKey = this.dsn.getSecretKey();
      String authHeader = "Sentry sentry_version=7,sentry_client="
         + this.sentryClientName
         + ",sentry_key="
         + publicKey
         + (secretKey != null && secretKey.length() > 0 ? ",sentry_secret=" + secretKey : "");
      Map<String, String> headers = new HashMap<>();
      headers.put("User-Agent", this.sentryClientName);
      headers.put("X-Sentry-Auth", authHeader);
      return new RequestDetails(envelopeUrl, headers);
   }
}
