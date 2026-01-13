package io.sentry;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.Experimental;

@Experimental
public final class NoOpDistributionApi implements IDistributionApi {
   private static final NoOpDistributionApi instance = new NoOpDistributionApi();

   private NoOpDistributionApi() {
   }

   public static NoOpDistributionApi getInstance() {
      return instance;
   }

   @NotNull
   @Override
   public UpdateStatus checkForUpdateBlocking() {
      return UpdateStatus.UpToDate.getInstance();
   }

   @NotNull
   @Override
   public Future<UpdateStatus> checkForUpdate() {
      return new NoOpDistributionApi.CompletedFuture<>(UpdateStatus.UpToDate.getInstance());
   }

   @Override
   public void downloadUpdate(@NotNull UpdateInfo info) {
   }

   @Override
   public boolean isEnabled() {
      return false;
   }

   private static final class CompletedFuture<T> implements Future<T> {
      private final T result;

      CompletedFuture(T result) {
         this.result = result;
      }

      @Override
      public boolean cancel(boolean mayInterruptIfRunning) {
         return false;
      }

      @Override
      public boolean isCancelled() {
         return false;
      }

      @Override
      public boolean isDone() {
         return true;
      }

      @Override
      public T get() throws ExecutionException {
         return this.result;
      }

      @Override
      public T get(long timeout, @NotNull TimeUnit unit) throws ExecutionException, TimeoutException {
         return this.result;
      }
   }
}
