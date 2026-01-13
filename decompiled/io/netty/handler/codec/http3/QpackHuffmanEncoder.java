package io.netty.handler.codec.http3;

import io.netty.buffer.ByteBuf;
import io.netty.util.AsciiString;
import io.netty.util.ByteProcessor;
import io.netty.util.internal.ObjectUtil;

final class QpackHuffmanEncoder {
   private final int[] codes;
   private final byte[] lengths;
   private final QpackHuffmanEncoder.EncodedLengthProcessor encodedLengthProcessor = new QpackHuffmanEncoder.EncodedLengthProcessor();
   private final QpackHuffmanEncoder.EncodeProcessor encodeProcessor = new QpackHuffmanEncoder.EncodeProcessor();

   QpackHuffmanEncoder() {
      this(QpackUtil.HUFFMAN_CODES, QpackUtil.HUFFMAN_CODE_LENGTHS);
   }

   private QpackHuffmanEncoder(int[] codes, byte[] lengths) {
      this.codes = codes;
      this.lengths = lengths;
   }

   public void encode(ByteBuf out, CharSequence data) {
      ObjectUtil.checkNotNull(out, "out");
      if (data instanceof AsciiString) {
         AsciiString string = (AsciiString)data;

         try {
            this.encodeProcessor.out = out;
            string.forEachByte(this.encodeProcessor);
         } catch (Exception var8) {
            throw new IllegalStateException(var8);
         } finally {
            this.encodeProcessor.end();
         }
      } else {
         this.encodeSlowPath(out, data);
      }
   }

   private void encodeSlowPath(ByteBuf out, CharSequence data) {
      long current = 0L;
      int n = 0;

      for (int i = 0; i < data.length(); i++) {
         int b = data.charAt(i) & 255;
         int code = this.codes[b];
         int nbits = this.lengths[b];
         current <<= nbits;
         current |= code;
         n += nbits;

         while (n >= 8) {
            n -= 8;
            out.writeByte((int)(current >> n));
         }
      }

      if (n > 0) {
         current <<= 8 - n;
         current |= 255 >>> n;
         out.writeByte((int)current);
      }
   }

   int getEncodedLength(CharSequence data) {
      if (data instanceof AsciiString) {
         AsciiString string = (AsciiString)data;

         try {
            this.encodedLengthProcessor.reset();
            string.forEachByte(this.encodedLengthProcessor);
            return this.encodedLengthProcessor.length();
         } catch (Exception var4) {
            throw new IllegalStateException(var4);
         }
      } else {
         return this.getEncodedLengthSlowPath(data);
      }
   }

   private int getEncodedLengthSlowPath(CharSequence data) {
      long len = 0L;

      for (int i = 0; i < data.length(); i++) {
         len += this.lengths[data.charAt(i) & 255];
      }

      return (int)(len + 7L >> 3);
   }

   private final class EncodeProcessor implements ByteProcessor {
      ByteBuf out;
      private long current;
      private int n;

      private EncodeProcessor() {
      }

      @Override
      public boolean process(byte value) {
         int b = value & 255;
         int nbits = QpackHuffmanEncoder.this.lengths[b];
         this.current <<= nbits;
         this.current = this.current | QpackHuffmanEncoder.this.codes[b];
         this.n += nbits;

         while (this.n >= 8) {
            this.n -= 8;
            this.out.writeByte((int)(this.current >> this.n));
         }

         return true;
      }

      void end() {
         try {
            if (this.n > 0) {
               this.current = this.current << 8 - this.n;
               this.current = this.current | 255 >>> this.n;
               this.out.writeByte((int)this.current);
            }
         } finally {
            this.out = null;
            this.current = 0L;
            this.n = 0;
         }
      }
   }

   private final class EncodedLengthProcessor implements ByteProcessor {
      private long len;

      private EncodedLengthProcessor() {
      }

      @Override
      public boolean process(byte value) {
         this.len = this.len + QpackHuffmanEncoder.this.lengths[value & 255];
         return true;
      }

      void reset() {
         this.len = 0L;
      }

      int length() {
         return (int)(this.len + 7L >> 3);
      }
   }
}
