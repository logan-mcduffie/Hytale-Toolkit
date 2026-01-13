package io.netty.handler.codec.quic;

import io.netty.buffer.ByteBuf;
import io.netty.channel.epoll.SegmentedDatagramPacket;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.internal.ObjectUtil;
import java.net.InetSocketAddress;

public final class EpollQuicUtils {
   private EpollQuicUtils() {
   }

   public static SegmentedDatagramPacketAllocator newSegmentedAllocator(int maxNumSegments) {
      ObjectUtil.checkInRange(maxNumSegments, 1, 64, "maxNumSegments");
      return (SegmentedDatagramPacketAllocator)(SegmentedDatagramPacket.isSupported()
         ? new EpollQuicUtils.EpollSegmentedDatagramPacketAllocator(maxNumSegments)
         : SegmentedDatagramPacketAllocator.NONE);
   }

   private static final class EpollSegmentedDatagramPacketAllocator implements SegmentedDatagramPacketAllocator {
      private final int maxNumSegments;

      EpollSegmentedDatagramPacketAllocator(int maxNumSegments) {
         this.maxNumSegments = maxNumSegments;
      }

      @Override
      public int maxNumSegments() {
         return this.maxNumSegments;
      }

      @Override
      public DatagramPacket newPacket(ByteBuf buffer, int segmentSize, InetSocketAddress remoteAddress) {
         return new io.netty.channel.unix.SegmentedDatagramPacket(buffer, segmentSize, remoteAddress);
      }
   }
}
