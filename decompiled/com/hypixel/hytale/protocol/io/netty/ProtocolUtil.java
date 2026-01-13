package com.hypixel.hytale.protocol.io.netty;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.quic.QuicChannel;
import io.netty.handler.codec.quic.QuicTransportError;
import javax.annotation.Nonnull;

public final class ProtocolUtil {
   public static final int APPLICATION_NO_ERROR = 0;
   public static final int APPLICATION_RATE_LIMITED = 1;
   public static final int APPLICATION_AUTH_FAILED = 2;
   public static final int APPLICATION_INVALID_VERSION = 3;
   public static final ChannelFutureListener CLOSE_ON_COMPLETE = ProtocolUtil::closeApplicationOnComplete;

   private ProtocolUtil() {
   }

   public static void closeConnection(@Nonnull Channel channel) {
      closeConnection(channel, QuicTransportError.PROTOCOL_VIOLATION);
   }

   public static void closeConnection(@Nonnull Channel channel, @Nonnull QuicTransportError error) {
      int errorCode = (int)error.code();
      if (channel instanceof QuicChannel quicChannel) {
         quicChannel.close(false, errorCode, Unpooled.EMPTY_BUFFER);
      } else {
         if (channel.parent() instanceof QuicChannel quicChannel) {
            quicChannel.close(false, errorCode, Unpooled.EMPTY_BUFFER);
         } else {
            channel.close();
         }
      }
   }

   public static void closeApplicationConnection(@Nonnull Channel channel) {
      closeApplicationConnection(channel, 0);
   }

   public static void closeApplicationConnection(@Nonnull Channel channel, int errorCode) {
      if (channel instanceof QuicChannel quicChannel) {
         quicChannel.close(true, errorCode, Unpooled.EMPTY_BUFFER);
      } else {
         if (channel.parent() instanceof QuicChannel quicChannel) {
            quicChannel.close(true, errorCode, Unpooled.EMPTY_BUFFER);
         } else {
            channel.close();
         }
      }
   }

   private static void closeApplicationOnComplete(ChannelFuture future) {
      closeApplicationConnection(future.channel());
   }
}
