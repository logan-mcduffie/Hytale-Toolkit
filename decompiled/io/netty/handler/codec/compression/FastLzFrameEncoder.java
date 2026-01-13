package io.netty.handler.codec.compression;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import java.util.zip.Adler32;
import java.util.zip.Checksum;

public class FastLzFrameEncoder extends MessageToByteEncoder<ByteBuf> {
   private final int level;
   private final ByteBufChecksum checksum;

   public FastLzFrameEncoder() {
      this(0, null);
   }

   public FastLzFrameEncoder(int level) {
      this(level, null);
   }

   public FastLzFrameEncoder(boolean validateChecksums) {
      this(0, validateChecksums ? new Adler32() : null);
   }

   public FastLzFrameEncoder(int level, Checksum checksum) {
      super(ByteBuf.class);
      if (level != 0 && level != 1 && level != 2) {
         throw new IllegalArgumentException(String.format("level: %d (expected: %d or %d or %d)", level, 0, 1, 2));
      } else {
         this.level = level;
         this.checksum = checksum == null ? null : ByteBufChecksum.wrapChecksum(checksum);
      }
   }

   protected void encode(ChannelHandlerContext ctx, ByteBuf in, ByteBuf out) throws Exception {
      ByteBufChecksum checksum = this.checksum;

      while (in.isReadable()) {
         int idx = in.readerIndex();
         int length = Math.min(in.readableBytes(), 65535);
         int outputIdx = out.writerIndex();
         out.setMedium(outputIdx, 4607066);
         int outputOffset = outputIdx + 4 + (checksum != null ? 4 : 0);
         byte blockType;
         int chunkLength;
         if (length < 32) {
            blockType = 0;
            out.ensureWritable(outputOffset + 2 + length);
            int outputPtr = outputOffset + 2;
            if (checksum != null) {
               checksum.reset();
               checksum.update(in, idx, length);
               out.setInt(outputIdx + 4, (int)checksum.getValue());
            }

            out.setBytes(outputPtr, in, idx, length);
            chunkLength = length;
         } else {
            if (checksum != null) {
               checksum.reset();
               checksum.update(in, idx, length);
               out.setInt(outputIdx + 4, (int)checksum.getValue());
            }

            int maxOutputLength = FastLz.calculateOutputBufferLength(length);
            out.ensureWritable(outputOffset + 4 + maxOutputLength);
            int outputPtr = outputOffset + 4;
            int compressedLength = FastLz.compress(in, in.readerIndex(), length, out, outputPtr, this.level);
            if (compressedLength < length) {
               blockType = 1;
               chunkLength = compressedLength;
               out.setShort(outputOffset, compressedLength);
               outputOffset += 2;
            } else {
               blockType = 0;
               out.setBytes(outputOffset + 2, in, idx, length);
               chunkLength = length;
            }
         }

         out.setShort(outputOffset, length);
         out.setByte(outputIdx + 3, blockType | (checksum != null ? 16 : 0));
         out.writerIndex(outputOffset + 2 + chunkLength);
         in.skipBytes(length);
      }
   }
}
