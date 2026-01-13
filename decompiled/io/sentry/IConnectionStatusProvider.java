package io.sentry;

import java.io.Closeable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public interface IConnectionStatusProvider extends Closeable {
   @NotNull
   IConnectionStatusProvider.ConnectionStatus getConnectionStatus();

   @Nullable
   String getConnectionType();

   boolean addConnectionStatusObserver(@NotNull IConnectionStatusProvider.IConnectionStatusObserver var1);

   void removeConnectionStatusObserver(@NotNull IConnectionStatusProvider.IConnectionStatusObserver var1);

   public static enum ConnectionStatus {
      UNKNOWN,
      CONNECTED,
      DISCONNECTED,
      NO_PERMISSION;
   }

   public interface IConnectionStatusObserver {
      void onConnectionStatusChanged(@NotNull IConnectionStatusProvider.ConnectionStatus var1);
   }
}
