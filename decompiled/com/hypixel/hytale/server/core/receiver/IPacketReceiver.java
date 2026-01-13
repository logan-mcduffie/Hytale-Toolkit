package com.hypixel.hytale.server.core.receiver;

import com.hypixel.hytale.protocol.Packet;
import javax.annotation.Nonnull;

public interface IPacketReceiver {
   void write(@Nonnull Packet var1);

   void writeNoCache(@Nonnull Packet var1);
}
