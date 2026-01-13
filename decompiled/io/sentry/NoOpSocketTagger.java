package io.sentry;

import org.jetbrains.annotations.NotNull;

public final class NoOpSocketTagger implements ISocketTagger {
   private static final NoOpSocketTagger instance = new NoOpSocketTagger();

   private NoOpSocketTagger() {
   }

   @NotNull
   public static ISocketTagger getInstance() {
      return instance;
   }

   @Override
   public void tagSockets() {
   }

   @Override
   public void untagSockets() {
   }
}
