package com.hypixel.hytale.server.core.io.handlers;

import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import java.util.function.Consumer;
import javax.annotation.Nonnull;

public interface IPacketHandler {
   void registerHandler(int var1, @Nonnull Consumer<Packet> var2);

   void registerNoOpHandlers(int... var1);

   @Nonnull
   PlayerRef getPlayerRef();

   @Nonnull
   String getIdentifier();
}
