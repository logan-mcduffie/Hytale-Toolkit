package io.netty.handler.codec.quic;

import io.netty.channel.ChannelOption;

public final class QuicChannelOption<T> extends ChannelOption<T> {
   public static final ChannelOption<Boolean> READ_FRAMES = valueOf(QuicChannelOption.class, "READ_FRAMES");
   public static final ChannelOption<QLogConfiguration> QLOG = valueOf(QuicChannelOption.class, "QLOG");
   public static final ChannelOption<SegmentedDatagramPacketAllocator> SEGMENTED_DATAGRAM_PACKET_ALLOCATOR = valueOf(
      QuicChannelOption.class, "SEGMENTED_DATAGRAM_PACKET_ALLOCATOR"
   );

   private QuicChannelOption() {
      super(null);
   }
}
