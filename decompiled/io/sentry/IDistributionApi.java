package io.sentry;

import java.util.concurrent.Future;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.Experimental;

@Experimental
public interface IDistributionApi {
   @NotNull
   UpdateStatus checkForUpdateBlocking();

   @NotNull
   Future<UpdateStatus> checkForUpdate();

   void downloadUpdate(@NotNull UpdateInfo var1);

   boolean isEnabled();
}
