package io.netty.handler.codec.http3;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.ChannelInputShutdownEvent;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.quic.QuicStreamChannel;
import io.netty.util.AsciiString;
import io.netty.util.internal.ObjectUtil;
import java.util.List;
import org.jetbrains.annotations.Nullable;

final class QpackEncoderHandler extends ByteToMessageDecoder {
   private final QpackHuffmanDecoder huffmanDecoder;
   private final QpackDecoder qpackDecoder;
   private boolean discard;

   QpackEncoderHandler(@Nullable Long maxTableCapacity, QpackDecoder qpackDecoder) {
      ObjectUtil.checkInRange(maxTableCapacity == null ? 0L : maxTableCapacity, 0L, 4294967295L, "maxTableCapacity");
      this.huffmanDecoder = new QpackHuffmanDecoder();
      this.qpackDecoder = qpackDecoder;
   }

   @Override
   protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> __) throws Exception {
      if (in.isReadable()) {
         if (this.discard) {
            in.skipBytes(in.readableBytes());
         } else {
            byte b = in.getByte(in.readerIndex());
            if ((b & 224) == 32) {
               long capacity = QpackUtil.decodePrefixedInteger(in, 5);
               if (capacity >= 0L) {
                  try {
                     this.qpackDecoder.setDynamicTableCapacity(capacity);
                  } catch (QpackException var13) {
                     this.handleDecodeFailure(ctx, var13, "setDynamicTableCapacity failed.");
                  }
               }
            } else {
               QpackAttributes qpackAttributes = Http3.getQpackAttributes(ctx.channel().parent());

               assert qpackAttributes != null;

               if (qpackAttributes.dynamicTableDisabled() || qpackAttributes.decoderStreamAvailable()) {
                  QuicStreamChannel decoderStream = qpackAttributes.decoderStream();
                  if ((b & 128) == 128) {
                     int readerIndex = in.readerIndex();
                     boolean isStaticTableIndex = QpackUtil.firstByteEquals(in, (byte)-64);
                     int nameIdx = QpackUtil.decodePrefixedIntegerAsInt(in, 6);
                     if (nameIdx >= 0) {
                        CharSequence value = this.decodeLiteralValue(in);
                        if (value == null) {
                           in.readerIndex(readerIndex);
                        } else {
                           try {
                              this.qpackDecoder.insertWithNameReference(decoderStream, isStaticTableIndex, nameIdx, value);
                           } catch (QpackException var14) {
                              this.handleDecodeFailure(ctx, var14, "insertWithNameReference failed.");
                           }
                        }
                     }
                  } else if ((b & 192) == 64) {
                     int readerIndex = in.readerIndex();
                     boolean nameHuffEncoded = QpackUtil.firstByteEquals(in, (byte)96);
                     int nameLength = QpackUtil.decodePrefixedIntegerAsInt(in, 5);
                     if (nameLength < 0) {
                        in.readerIndex(readerIndex);
                     } else if (in.readableBytes() < nameLength) {
                        in.readerIndex(readerIndex);
                     } else {
                        CharSequence name = this.decodeStringLiteral(in, nameHuffEncoded, nameLength);
                        CharSequence value = this.decodeLiteralValue(in);
                        if (value == null) {
                           in.readerIndex(readerIndex);
                        } else {
                           try {
                              this.qpackDecoder.insertLiteral(decoderStream, name, value);
                           } catch (QpackException var15) {
                              this.handleDecodeFailure(ctx, var15, "insertLiteral failed.");
                           }
                        }
                     }
                  } else if ((b & 224) == 0) {
                     int readerIndex = in.readerIndex();
                     int index = QpackUtil.decodePrefixedIntegerAsInt(in, 5);
                     if (index < 0) {
                        in.readerIndex(readerIndex);
                     } else {
                        try {
                           this.qpackDecoder.duplicate(decoderStream, index);
                        } catch (QpackException var16) {
                           this.handleDecodeFailure(ctx, var16, "duplicate failed.");
                        }
                     }
                  } else {
                     this.discard = true;
                     Http3CodecUtils.connectionError(ctx, Http3ErrorCode.QPACK_ENCODER_STREAM_ERROR, "Unknown encoder instruction '" + b + "'.", false);
                  }
               }
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

   private void handleDecodeFailure(ChannelHandlerContext ctx, QpackException cause, String message) {
      this.discard = true;
      Http3CodecUtils.connectionError(ctx, new Http3Exception(Http3ErrorCode.QPACK_ENCODER_STREAM_ERROR, message, cause), true);
   }

   @Nullable
   private CharSequence decodeLiteralValue(ByteBuf in) throws QpackException {
      boolean valueHuffEncoded = QpackUtil.firstByteEquals(in, (byte)-128);
      int valueLength = QpackUtil.decodePrefixedIntegerAsInt(in, 7);
      return valueLength >= 0 && in.readableBytes() >= valueLength ? this.decodeStringLiteral(in, valueHuffEncoded, valueLength) : null;
   }

   private CharSequence decodeStringLiteral(ByteBuf in, boolean huffmanEncoded, int length) throws QpackException {
      if (huffmanEncoded) {
         return this.huffmanDecoder.decode(in, length);
      } else {
         byte[] buf = new byte[length];
         in.readBytes(buf);
         return new AsciiString(buf, false);
      }
   }
}
