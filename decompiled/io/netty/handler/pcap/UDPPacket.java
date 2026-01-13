package io.netty.handler.pcap;

import io.netty.buffer.ByteBuf;

final class UDPPacket {
   private static final short UDP_HEADER_SIZE = 8;

   private UDPPacket() {
   }

   static void writePacket(ByteBuf byteBuf, ByteBuf payload, int srcPort, int dstPort) {
      byteBuf.writeShort(srcPort);
      byteBuf.writeShort(dstPort);
      byteBuf.writeShort(8 + payload.readableBytes());
      byteBuf.writeShort(1);
      byteBuf.writeBytes(payload);
   }
}
