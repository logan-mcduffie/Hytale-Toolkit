package io.netty.handler.codec.compression;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class SnappyFrameEncoder extends MessageToByteEncoder<ByteBuf> {
   private static final short SNAPPY_SLICE_SIZE = 32767;
   private static final int SNAPPY_SLICE_JUMBO_SIZE = 65535;
   private static final int MIN_COMPRESSIBLE_LENGTH = 18;
   private static final byte[] STREAM_START = new byte[]{-1, 6, 0, 0, 115, 78, 97, 80, 112, 89};
   private final Snappy snappy = new Snappy();
   private boolean started;
   private final int sliceSize;

   public SnappyFrameEncoder() {
      this(32767);
   }

   public static SnappyFrameEncoder snappyEncoderWithJumboFrames() {
      return new SnappyFrameEncoder(65535);
   }

   private SnappyFrameEncoder(int sliceSize) {
      super(ByteBuf.class);
      this.sliceSize = sliceSize;
   }

   protected void encode(ChannelHandlerContext ctx, ByteBuf in, ByteBuf out) throws Exception {
      if (in.isReadable()) {
         if (!this.started) {
            this.started = true;
            out.writeBytes(STREAM_START);
         }

         int dataLength = in.readableBytes();
         if (dataLength <= 18) {
            writeUnencodedChunk(in, out, dataLength);
         } else {
            while (true) {
               int lengthIdx = out.writerIndex() + 1;
               if (dataLength < 18) {
                  ByteBuf slice = in.readSlice(dataLength);
                  writeUnencodedChunk(slice, out, dataLength);
                  break;
               }

               out.writeInt(0);
               if (dataLength <= this.sliceSize) {
                  ByteBuf slice = in.readSlice(dataLength);
                  calculateAndWriteChecksum(slice, out);
                  this.snappy.encode(slice, out, dataLength);
                  setChunkLength(out, lengthIdx);
                  break;
               }

               ByteBuf slice = in.readSlice(this.sliceSize);
               calculateAndWriteChecksum(slice, out);
               this.snappy.encode(slice, out, this.sliceSize);
               setChunkLength(out, lengthIdx);
               dataLength -= this.sliceSize;
            }
         }
      }
   }

   private static void writeUnencodedChunk(ByteBuf in, ByteBuf out, int dataLength) {
      out.writeByte(1);
      writeChunkLength(out, dataLength + 4);
      calculateAndWriteChecksum(in, out);
      out.writeBytes(in, dataLength);
   }

   private static void setChunkLength(ByteBuf out, int lengthIdx) {
      int chunkLength = out.writerIndex() - lengthIdx - 3;
      if (chunkLength >>> 24 != 0) {
         throw new CompressionException("compressed data too large: " + chunkLength);
      } else {
         out.setMediumLE(lengthIdx, chunkLength);
      }
   }

   private static void writeChunkLength(ByteBuf out, int chunkLength) {
      out.writeMediumLE(chunkLength);
   }

   private static void calculateAndWriteChecksum(ByteBuf slice, ByteBuf out) {
      out.writeIntLE(Snappy.calculateChecksum(slice));
   }
}
