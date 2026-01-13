package org.bouncycastle.util.encoders;

public class BufferedDecoder {
   protected byte[] buf;
   protected int bufOff;
   protected Translator translator;

   public BufferedDecoder(Translator var1, int var2) {
      this.translator = var1;
      if (var2 % var1.getEncodedBlockSize() != 0) {
         throw new IllegalArgumentException("buffer size not multiple of input block size");
      } else {
         this.buf = new byte[var2];
         this.bufOff = 0;
      }
   }

   public int processByte(byte var1, byte[] var2, int var3) {
      int var4 = 0;
      this.buf[this.bufOff++] = var1;
      if (this.bufOff == this.buf.length) {
         var4 = this.translator.decode(this.buf, 0, this.buf.length, var2, var3);
         this.bufOff = 0;
      }

      return var4;
   }

   public int processBytes(byte[] var1, int var2, int var3, byte[] var4, int var5) {
      if (var3 < 0) {
         throw new IllegalArgumentException("Can't have a negative input length!");
      } else {
         int var6 = 0;
         int var7 = this.buf.length - this.bufOff;
         if (var3 > var7) {
            System.arraycopy(var1, var2, this.buf, this.bufOff, var7);
            var6 += this.translator.decode(this.buf, 0, this.buf.length, var4, var5);
            this.bufOff = 0;
            var3 -= var7;
            var2 += var7;
            var5 += var6;
            int var8 = var3 - var3 % this.buf.length;
            var6 += this.translator.decode(var1, var2, var8, var4, var5);
            var3 -= var8;
            var2 += var8;
         }

         if (var3 != 0) {
            System.arraycopy(var1, var2, this.buf, this.bufOff, var3);
            this.bufOff += var3;
         }

         return var6;
      }
   }
}
