package io.netty.handler.codec.protobuf;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;
import java.util.List;

public class ProtobufVarint32FrameDecoder extends ByteToMessageDecoder {
   @Override
   protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
      in.markReaderIndex();
      int preIndex = in.readerIndex();
      int length = readRawVarint32(in);
      if (preIndex != in.readerIndex()) {
         if (length < 0) {
            throw new CorruptedFrameException("negative length: " + length);
         } else {
            if (in.readableBytes() < length) {
               in.resetReaderIndex();
            } else {
               out.add(in.readRetainedSlice(length));
            }
         }
      }
   }

   static int readRawVarint32(ByteBuf buffer) {
      if (buffer.readableBytes() < 4) {
         return readRawVarint24(buffer);
      } else {
         int wholeOrMore = buffer.getIntLE(buffer.readerIndex());
         int firstOneOnStop = ~wholeOrMore & -2139062144;
         if (firstOneOnStop == 0) {
            return readRawVarint40(buffer, wholeOrMore);
         } else {
            int bitsToKeep = Integer.numberOfTrailingZeros(firstOneOnStop) + 1;
            buffer.skipBytes(bitsToKeep >> 3);
            int thisVarintMask = firstOneOnStop ^ firstOneOnStop - 1;
            int wholeWithContinuations = wholeOrMore & thisVarintMask;
            wholeWithContinuations = wholeWithContinuations & 8323199 | (wholeWithContinuations & 2130738944) >> 1;
            return wholeWithContinuations & 16383 | (wholeWithContinuations & 1073676288) >> 2;
         }
      }
   }

   private static int readRawVarint40(ByteBuf buffer, int wholeOrMore) {
      byte lastByte;
      if (buffer.readableBytes() != 4 && (lastByte = buffer.getByte(buffer.readerIndex() + 4)) >= 0) {
         buffer.skipBytes(5);
         return wholeOrMore & 127 | (wholeOrMore >> 8 & 127) << 7 | (wholeOrMore >> 16 & 127) << 14 | (wholeOrMore >> 24 & 127) << 21 | lastByte << 28;
      } else {
         throw new CorruptedFrameException("malformed varint.");
      }
   }

   private static int readRawVarint24(ByteBuf buffer) {
      if (!buffer.isReadable()) {
         return 0;
      } else {
         buffer.markReaderIndex();
         byte tmp = buffer.readByte();
         if (tmp >= 0) {
            return tmp;
         } else {
            int result = tmp & 127;
            if (!buffer.isReadable()) {
               buffer.resetReaderIndex();
               return 0;
            } else if ((tmp = buffer.readByte()) >= 0) {
               return result | tmp << 7;
            } else {
               result |= (tmp & 127) << 7;
               if (!buffer.isReadable()) {
                  buffer.resetReaderIndex();
                  return 0;
               } else {
                  return (tmp = buffer.readByte()) >= 0 ? result | tmp << 14 : result | (tmp & 127) << 14;
               }
            }
         }
      }
   }
}
