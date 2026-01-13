package io.sentry;

import java.io.IOException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class NoOpConnectionStatusProvider implements IConnectionStatusProvider {
   @NotNull
   @Override
   public IConnectionStatusProvider.ConnectionStatus getConnectionStatus() {
      return IConnectionStatusProvider.ConnectionStatus.UNKNOWN;
   }

   @Nullable
   @Override
   public String getConnectionType() {
      return null;
   }

   @Override
   public boolean addConnectionStatusObserver(@NotNull IConnectionStatusProvider.IConnectionStatusObserver observer) {
      return false;
   }

   @Override
   public void removeConnectionStatusObserver(@NotNull IConnectionStatusProvider.IConnectionStatusObserver observer) {
   }

   @Override
   public void close() throws IOException {
   }
}
