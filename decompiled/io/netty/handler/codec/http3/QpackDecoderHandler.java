package io.netty.handler.codec.http3;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.ChannelInputShutdownEvent;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.util.List;

final class QpackDecoderHandler extends ByteToMessageDecoder {
   private boolean discard;
   private final QpackEncoder qpackEncoder;

   QpackDecoderHandler(QpackEncoder qpackEncoder) {
      this.qpackEncoder = qpackEncoder;
   }

   @Override
   protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
      if (in.isReadable()) {
         if (this.discard) {
            in.skipBytes(in.readableBytes());
         } else {
            byte b = in.getByte(in.readerIndex());
            if ((b & 128) == 128) {
               long streamId = QpackUtil.decodePrefixedInteger(in, 7);
               if (streamId >= 0L) {
                  try {
                     this.qpackEncoder.sectionAcknowledgment(streamId);
                  } catch (QpackException var8) {
                     Http3CodecUtils.connectionError(
                        ctx, new Http3Exception(Http3ErrorCode.QPACK_DECODER_STREAM_ERROR, "Section acknowledgment decode failed.", var8), true
                     );
                  }
               }
            } else if ((b & 192) == 64) {
               long streamId = QpackUtil.decodePrefixedInteger(in, 6);
               if (streamId >= 0L) {
                  try {
                     this.qpackEncoder.streamCancellation(streamId);
                  } catch (QpackException var9) {
                     Http3CodecUtils.connectionError(
                        ctx, new Http3Exception(Http3ErrorCode.QPACK_DECODER_STREAM_ERROR, "Stream cancellation decode failed.", var9), true
                     );
                  }
               }
            } else if ((b & 192) == 0) {
               int increment = QpackUtil.decodePrefixedIntegerAsInt(in, 6);
               if (increment == 0) {
                  this.discard = true;
                  Http3CodecUtils.connectionError(ctx, Http3ErrorCode.QPACK_DECODER_STREAM_ERROR, "Invalid increment '" + increment + "'.", false);
               } else if (increment >= 0) {
                  try {
                     this.qpackEncoder.insertCountIncrement(increment);
                  } catch (QpackException var10) {
                     Http3CodecUtils.connectionError(
                        ctx, new Http3Exception(Http3ErrorCode.QPACK_DECODER_STREAM_ERROR, "Insert count increment decode failed.", var10), true
                     );
                  }
               }
            } else {
               this.discard = true;
               Http3CodecUtils.connectionError(ctx, Http3ErrorCode.QPACK_DECODER_STREAM_ERROR, "Unknown decoder instruction '" + b + "'.", false);
            }
         }
      }
   }

   @Override
   public void channelReadComplete(ChannelHandlerContext ctx) {
      ctx.fireChannelReadComplete();
      Http3CodecUtils.readIfNoAutoRead(ctx);
   }

   @Override
   public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
      if (evt instanceof ChannelInputShutdownEvent) {
         Http3CodecUtils.criticalStreamClosed(ctx);
      }

      ctx.fireUserEventTriggered(evt);
   }

   @Override
   public void channelInactive(ChannelHandlerContext ctx) {
      Http3CodecUtils.criticalStreamClosed(ctx);
      ctx.fireChannelInactive();
   }
}
