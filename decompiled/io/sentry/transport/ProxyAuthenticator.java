package io.sentry.transport;

import io.sentry.util.Objects;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.Authenticator.RequestorType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class ProxyAuthenticator extends Authenticator {
   @NotNull
   private final String user;
   @NotNull
   private final String password;

   ProxyAuthenticator(@NotNull String user, @NotNull String password) {
      this.user = Objects.requireNonNull(user, "user is required");
      this.password = Objects.requireNonNull(password, "password is required");
   }

   @Nullable
   @Override
   protected PasswordAuthentication getPasswordAuthentication() {
      return this.getRequestorType() == RequestorType.PROXY ? new PasswordAuthentication(this.user, this.password.toCharArray()) : null;
   }

   @NotNull
   String getUser() {
      return this.user;
   }

   @NotNull
   String getPassword() {
      return this.password;
   }
}
