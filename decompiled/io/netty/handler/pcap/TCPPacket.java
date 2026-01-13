package io.netty.handler.pcap;

import io.netty.buffer.ByteBuf;

final class TCPPacket {
   private static final short OFFSET = 20480;

   private TCPPacket() {
   }

   static void writePacket(ByteBuf byteBuf, ByteBuf payload, long segmentNumber, long ackNumber, int srcPort, int dstPort, TCPPacket.TCPFlag... tcpFlags) {
      byteBuf.writeShort(srcPort);
      byteBuf.writeShort(dstPort);
      byteBuf.writeInt((int)segmentNumber);
      byteBuf.writeInt((int)ackNumber);
      byteBuf.writeShort(20480 | TCPPacket.TCPFlag.getFlag(tcpFlags));
      byteBuf.writeShort(65535);
      byteBuf.writeShort(1);
      byteBuf.writeShort(0);
      if (payload != null) {
         byteBuf.writeBytes(payload);
      }
   }

   static enum TCPFlag {
      FIN(1),
      SYN(2),
      RST(4),
      PSH(8),
      ACK(16),
      URG(32),
      ECE(64),
      CWR(128);

      private final int value;

      private TCPFlag(int value) {
         this.value = value;
      }

      static int getFlag(TCPPacket.TCPFlag... tcpFlags) {
         int flags = 0;

         for (TCPPacket.TCPFlag tcpFlag : tcpFlags) {
            flags |= tcpFlag.value;
         }

         return flags;
      }
   }
}
