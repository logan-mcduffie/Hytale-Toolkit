package io.netty.handler.codec.http3;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.quic.QuicChannel;
import io.netty.handler.codec.quic.QuicStreamChannel;
import io.netty.handler.codec.quic.QuicStreamType;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;
import org.jetbrains.annotations.Nullable;

final class Http3CodecUtils {
   static final long MIN_RESERVED_FRAME_TYPE = 64L;
   static final long MAX_RESERVED_FRAME_TYPE = 66571993090L;
   static final int HTTP3_DATA_FRAME_TYPE = 0;
   static final int HTTP3_HEADERS_FRAME_TYPE = 1;
   static final int HTTP3_CANCEL_PUSH_FRAME_TYPE = 3;
   static final int HTTP3_SETTINGS_FRAME_TYPE = 4;
   static final int HTTP3_PUSH_PROMISE_FRAME_TYPE = 5;
   static final int HTTP3_GO_AWAY_FRAME_TYPE = 7;
   static final int HTTP3_MAX_PUSH_ID_FRAME_TYPE = 13;
   static final int HTTP3_CANCEL_PUSH_FRAME_MAX_LEN = 8;
   static final int HTTP3_SETTINGS_FRAME_MAX_LEN = 256;
   static final int HTTP3_GO_AWAY_FRAME_MAX_LEN = 8;
   static final int HTTP3_MAX_PUSH_ID_FRAME_MAX_LEN = 8;
   static final int HTTP3_CONTROL_STREAM_TYPE = 0;
   static final int HTTP3_PUSH_STREAM_TYPE = 1;
   static final int HTTP3_QPACK_ENCODER_STREAM_TYPE = 2;
   static final int HTTP3_QPACK_DECODER_STREAM_TYPE = 3;

   private Http3CodecUtils() {
   }

   static long checkIsReservedFrameType(long type) {
      return ObjectUtil.checkInRange(type, 64L, 66571993090L, "type");
   }

   static boolean isReservedFrameType(long type) {
      return type >= 64L && type <= 66571993090L;
   }

   static boolean isServerInitiatedQuicStream(QuicStreamChannel channel) {
      return channel.streamId() % 2L != 0L;
   }

   static boolean isReservedHttp2FrameType(long type) {
      switch ((int)type) {
         case 2:
         case 6:
         case 8:
         case 9:
            return true;
         case 3:
         case 4:
         case 5:
         case 7:
         default:
            return false;
      }
   }

   static boolean isReservedHttp2Setting(long key) {
      return 2L <= key && key <= 5L;
   }

   static int numBytesForVariableLengthInteger(long value) {
      if (value <= 63L) {
         return 1;
      } else if (value <= 16383L) {
         return 2;
      } else if (value <= 1073741823L) {
         return 4;
      } else if (value <= 4611686018427387903L) {
         return 8;
      } else {
         throw new IllegalArgumentException();
      }
   }

   static void writeVariableLengthInteger(ByteBuf out, long value) {
      int numBytes = numBytesForVariableLengthInteger(value);
      writeVariableLengthInteger(out, value, numBytes);
   }

   static void writeVariableLengthInteger(ByteBuf out, long value, int numBytes) {
      int writerIndex = out.writerIndex();
      switch (numBytes) {
         case 1:
            out.writeByte((byte)value);
            break;
         case 2:
            out.writeShort((short)value);
            encodeLengthIntoBuffer(out, writerIndex, (byte)64);
            break;
         case 3:
         case 5:
         case 6:
         case 7:
         default:
            throw new IllegalArgumentException();
         case 4:
            out.writeInt((int)value);
            encodeLengthIntoBuffer(out, writerIndex, (byte)-128);
            break;
         case 8:
            out.writeLong(value);
            encodeLengthIntoBuffer(out, writerIndex, (byte)-64);
      }
   }

   private static void encodeLengthIntoBuffer(ByteBuf out, int index, byte b) {
      out.setByte(index, out.getByte(index) | b);
   }

   static long readVariableLengthInteger(ByteBuf in, int len) {
      switch (len) {
         case 1:
            return in.readUnsignedByte();
         case 2:
            return in.readUnsignedShort() & 16383;
         case 3:
         case 5:
         case 6:
         case 7:
         default:
            throw new IllegalArgumentException();
         case 4:
            return in.readUnsignedInt() & 1073741823L;
         case 8:
            return in.readLong() & 4611686018427387903L;
      }
   }

   static int numBytesForVariableLengthInteger(byte b) {
      byte val = (byte)(b >> 6);
      if ((val & 1) != 0) {
         return (val & 2) != 0 ? 8 : 2;
      } else {
         return (val & 2) != 0 ? 4 : 1;
      }
   }

   static void criticalStreamClosed(ChannelHandlerContext ctx) {
      if (ctx.channel().parent().isActive()) {
         connectionError(ctx, Http3ErrorCode.H3_CLOSED_CRITICAL_STREAM, "Critical stream closed.", false);
      }
   }

   static void connectionError(ChannelHandlerContext ctx, Http3Exception exception, boolean fireException) {
      if (fireException) {
         ctx.fireExceptionCaught(exception);
      }

      connectionError(ctx.channel(), exception.errorCode(), exception.getMessage());
   }

   static void connectionError(ChannelHandlerContext ctx, Http3ErrorCode errorCode, @Nullable String msg, boolean fireException) {
      if (fireException) {
         ctx.fireExceptionCaught(new Http3Exception(errorCode, msg));
      }

      connectionError(ctx.channel(), errorCode, msg);
   }

   static void closeOnFailure(ChannelFuture future) {
      if (future.isDone() && !future.isSuccess()) {
         future.channel().close();
      } else {
         future.addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
      }
   }

   static void connectionError(Channel channel, Http3ErrorCode errorCode, @Nullable String msg) {
      QuicChannel quicChannel;
      if (channel instanceof QuicChannel) {
         quicChannel = (QuicChannel)channel;
      } else {
         quicChannel = (QuicChannel)channel.parent();
      }

      ByteBuf buffer;
      if (msg != null) {
         buffer = quicChannel.alloc().buffer();
         buffer.writeCharSequence(msg, CharsetUtil.US_ASCII);
      } else {
         buffer = Unpooled.EMPTY_BUFFER;
      }

      quicChannel.close(true, errorCode.code, buffer);
   }

   static void streamError(ChannelHandlerContext ctx, Http3ErrorCode errorCode) {
      ((QuicStreamChannel)ctx.channel()).shutdownOutput(errorCode.code);
   }

   static void readIfNoAutoRead(ChannelHandlerContext ctx) {
      if (!ctx.channel().config().isAutoRead()) {
         ctx.read();
      }
   }

   @Nullable
   static Http3ConnectionHandler getConnectionHandlerOrClose(QuicChannel ch) {
      Http3ConnectionHandler connectionHandler = ch.pipeline().get(Http3ConnectionHandler.class);
      if (connectionHandler == null) {
         connectionError(
            ch, Http3ErrorCode.H3_INTERNAL_ERROR, "Couldn't obtain the " + StringUtil.simpleClassName(Http3ConnectionHandler.class) + " of the parent Channel"
         );
         return null;
      } else {
         return connectionHandler;
      }
   }

   static void verifyIsUnidirectional(QuicStreamChannel ch) {
      if (ch.type() != QuicStreamType.UNIDIRECTIONAL) {
         throw new IllegalArgumentException("Invalid stream type: " + ch.type() + " for stream: " + ch.streamId());
      }
   }
}
