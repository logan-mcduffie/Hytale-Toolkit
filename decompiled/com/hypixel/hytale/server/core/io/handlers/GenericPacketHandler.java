package com.hypixel.hytale.server.core.io.handlers;

import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.server.core.io.PacketHandler;
import com.hypixel.hytale.server.core.io.ProtocolVersion;
import io.netty.channel.Channel;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nonnull;

public abstract class GenericPacketHandler extends PacketHandler {
   private static final Consumer<Packet> EMPTY_CONSUMER = packet -> {};
   @Nonnull
   protected final List<SubPacketHandler> packetHandlers = new ObjectArrayList<>();
   private Consumer<Packet>[] handlers = newHandlerArray(0);

   @Nonnull
   public static Consumer<Packet>[] newHandlerArray(int size) {
      return new Consumer[size];
   }

   public GenericPacketHandler(@Nonnull Channel channel, @Nonnull ProtocolVersion protocolVersion) {
      super(channel, protocolVersion);
   }

   public void registerSubPacketHandler(SubPacketHandler subPacketHandler) {
      this.packetHandlers.add(subPacketHandler);
   }

   public void registerHandler(int packetId, @Nonnull Consumer<Packet> handler) {
      if (packetId >= this.handlers.length) {
         Consumer<Packet>[] newHandlers = newHandlerArray(packetId + 1);
         System.arraycopy(this.handlers, 0, newHandlers, 0, this.handlers.length);
         this.handlers = newHandlers;
      }

      this.handlers[packetId] = handler;
   }

   public void registerNoOpHandlers(@Nonnull int... packetIds) {
      for (int packetId : packetIds) {
         this.registerHandler(packetId, EMPTY_CONSUMER);
      }
   }

   @Override
   public final void accept(@Nonnull Packet packet) {
      int packetId = packet.getId();
      Consumer<Packet> handler = this.handlers.length > packetId ? this.handlers[packetId] : null;
      if (handler != null) {
         try {
            handler.accept(packet);
         } catch (Throwable var5) {
            throw new RuntimeException("Could not handle packet (" + packetId + "): " + packet, var5);
         }
      } else {
         throw new RuntimeException("No handler is registered for (" + packetId + "): " + packet);
      }
   }
}
