package com.hypixel.hytale.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import javax.annotation.Nonnull;

public final class CachedPacket<T extends Packet> implements Packet, AutoCloseable {
   private final Class<T> packetType;
   private final int packetId;
   private final ByteBuf cachedBytes;

   private CachedPacket(Class<T> packetType, int packetId, ByteBuf cachedBytes) {
      this.packetType = packetType;
      this.packetId = packetId;
      this.cachedBytes = cachedBytes;
   }

   public static <T extends Packet> CachedPacket<T> cache(@Nonnull T packet) {
      if (packet instanceof CachedPacket) {
         throw new IllegalArgumentException("Cannot cache a CachedPacket");
      } else {
         ByteBuf buf = Unpooled.buffer();
         packet.serialize(buf);
         return new CachedPacket<>((Class<T>)packet.getClass(), packet.getId(), buf);
      }
   }

   @Override
   public int getId() {
      return this.packetId;
   }

   @Override
   public void serialize(@Nonnull ByteBuf buf) {
      if (this.cachedBytes.refCnt() <= 0) {
         throw new IllegalStateException("CachedPacket buffer was released before serialization completed");
      } else {
         buf.writeBytes(this.cachedBytes, this.cachedBytes.readerIndex(), this.cachedBytes.readableBytes());
      }
   }

   @Override
   public int computeSize() {
      return this.cachedBytes.readableBytes();
   }

   public Class<T> getPacketType() {
      return this.packetType;
   }

   public int getCachedSize() {
      return this.cachedBytes.readableBytes();
   }

   @Override
   public void close() {
      if (this.cachedBytes.refCnt() > 0) {
         this.cachedBytes.release();
      }
   }
}
