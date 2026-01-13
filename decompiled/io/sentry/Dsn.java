package io.sentry;

import io.sentry.util.Objects;
import java.net.URI;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class Dsn {
   @NotNull
   private final String projectId;
   @Nullable
   private final String path;
   @Nullable
   private final String secretKey;
   @NotNull
   private final String publicKey;
   @NotNull
   private final URI sentryUri;

   @NotNull
   public String getProjectId() {
      return this.projectId;
   }

   @Nullable
   public String getPath() {
      return this.path;
   }

   @Nullable
   public String getSecretKey() {
      return this.secretKey;
   }

   @NotNull
   public String getPublicKey() {
      return this.publicKey;
   }

   @NotNull
   URI getSentryUri() {
      return this.sentryUri;
   }

   Dsn(@Nullable String dsn) throws IllegalArgumentException {
      try {
         Objects.requireNonNull(dsn, "The DSN is required.");
         URI uri = new URI(dsn).normalize();
         String scheme = uri.getScheme();
         if (!"http".equalsIgnoreCase(scheme) && !"https".equalsIgnoreCase(scheme)) {
            throw new IllegalArgumentException("Invalid DSN scheme: " + scheme);
         } else {
            String userInfo = uri.getUserInfo();
            if (userInfo != null && !userInfo.isEmpty()) {
               String[] keys = userInfo.split(":", -1);
               this.publicKey = keys[0];
               if (this.publicKey != null && !this.publicKey.isEmpty()) {
                  this.secretKey = keys.length > 1 ? keys[1] : null;
                  String uriPath = uri.getPath();
                  if (uriPath.endsWith("/")) {
                     uriPath = uriPath.substring(0, uriPath.length() - 1);
                  }

                  int projectIdStart = uriPath.lastIndexOf("/") + 1;
                  String path = uriPath.substring(0, projectIdStart);
                  if (!path.endsWith("/")) {
                     path = path + "/";
                  }

                  this.path = path;
                  this.projectId = uriPath.substring(projectIdStart);
                  if (this.projectId.isEmpty()) {
                     throw new IllegalArgumentException("Invalid DSN: A Project Id is required.");
                  } else {
                     this.sentryUri = new URI(scheme, null, uri.getHost(), uri.getPort(), path + "api/" + this.projectId, null, null);
                  }
               } else {
                  throw new IllegalArgumentException("Invalid DSN: No public key provided.");
               }
            } else {
               throw new IllegalArgumentException("Invalid DSN: No public key provided.");
            }
         }
      } catch (Throwable var9) {
         throw new IllegalArgumentException(var9);
      }
   }
}
