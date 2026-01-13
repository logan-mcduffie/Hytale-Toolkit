package org.bouncycastle.mime.encoding;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.util.encoders.Base64Encoder;

public class Base64OutputStream extends FilterOutputStream {
   private static final Base64Encoder ENCODER = new Base64Encoder();
   private static final int INBUF_SIZE = 54;
   private static final int OUTBUF_SIZE = 74;
   private final byte[] inBuf = new byte[54];
   private final byte[] outBuf = new byte[74];
   private int inPos = 0;

   public Base64OutputStream(OutputStream var1) {
      super(var1);
      this.outBuf[72] = 13;
      this.outBuf[73] = 10;
   }

   @Override
   public void write(int var1) throws IOException {
      this.inBuf[this.inPos++] = (byte)var1;
      if (this.inPos == 54) {
         this.encodeBlock(this.inBuf, 0);
         this.inPos = 0;
      }
   }

   @Override
   public void write(byte[] var1, int var2, int var3) throws IOException {
      int var4 = 54 - this.inPos;
      if (var3 < var4) {
         System.arraycopy(var1, var2, this.inBuf, this.inPos, var3);
         this.inPos += var3;
      } else {
         int var5 = 0;
         if (this.inPos > 0) {
            System.arraycopy(var1, var2, this.inBuf, this.inPos, var4);
            var5 += var4;
            this.encodeBlock(this.inBuf, 0);
         }

         int var6;
         while ((var6 = var3 - var5) >= 54) {
            this.encodeBlock(var1, var2 + var5);
            var5 += 54;
         }

         System.arraycopy(var1, var2 + var5, this.inBuf, 0, var6);
         this.inPos = var6;
      }
   }

   @Override
   public void write(byte[] var1) throws IOException {
      this.write(var1, 0, var1.length);
   }

   @Override
   public void close() throws IOException {
      if (this.inPos > 0) {
         int var1 = ENCODER.encode(this.inBuf, 0, this.inPos, this.outBuf, 0);
         this.inPos = 0;
         this.outBuf[var1++] = 13;
         this.outBuf[var1++] = 10;
         this.out.write(this.outBuf, 0, var1);
      }

      this.out.close();
   }

   private void encodeBlock(byte[] var1, int var2) throws IOException {
      ENCODER.encode(var1, var2, 54, this.outBuf, 0);
      this.out.write(this.outBuf, 0, 74);
   }
}
