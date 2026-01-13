package com.hypixel.hytale.server.core.util;

import com.hypixel.hytale.server.core.NameMatching;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nonnull;

public class AuthUtil {
   @Nonnull
   @Deprecated
   public static CompletableFuture<UUID> lookupUuid(String username) {
      PlayerRef player = Universe.get().getPlayerByUsername(username, NameMatching.EXACT);
      return player != null
         ? CompletableFuture.completedFuture(player.getUuid())
         : CompletableFuture.completedFuture(UUID.nameUUIDFromBytes(("NO_AUTH|" + username).getBytes(StandardCharsets.UTF_8)));
   }
}
