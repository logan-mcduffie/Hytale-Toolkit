package io.netty.handler.codec.quic;

import io.netty.buffer.ByteBuf;
import io.netty.channel.socket.DatagramPacket;
import java.net.InetSocketAddress;

@FunctionalInterface
public interface SegmentedDatagramPacketAllocator {
   SegmentedDatagramPacketAllocator NONE = new SegmentedDatagramPacketAllocator() {
      @Override
      public int maxNumSegments() {
         return 0;
      }

      @Override
      public DatagramPacket newPacket(ByteBuf buffer, int segmentSize, InetSocketAddress remoteAddress) {
         throw new UnsupportedOperationException();
      }
   };

   default int maxNumSegments() {
      return 10;
   }

   DatagramPacket newPacket(ByteBuf var1, int var2, InetSocketAddress var3);
}
