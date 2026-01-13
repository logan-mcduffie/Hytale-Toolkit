package org.bouncycastle.util.io;

import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.util.Arrays;

public class BufferingOutputStream extends OutputStream {
   private final OutputStream other;
   private final byte[] buf;
   private int bufOff;

   public BufferingOutputStream(OutputStream var1) {
      this.other = var1;
      this.buf = new byte[4096];
   }

   public BufferingOutputStream(OutputStream var1, int var2) {
      this.other = var1;
      this.buf = new byte[var2];
   }

   @Override
   public void write(byte[] var1, int var2, int var3) throws IOException {
      if (var3 < this.buf.length - this.bufOff) {
         System.arraycopy(var1, var2, this.buf, this.bufOff, var3);
         this.bufOff += var3;
      } else {
         int var4 = this.buf.length - this.bufOff;
         System.arraycopy(var1, var2, this.buf, this.bufOff, var4);
         this.bufOff += var4;
         this.flush();
         var2 += var4;

         for (var3 -= var4; var3 >= this.buf.length; var3 -= this.buf.length) {
            this.other.write(var1, var2, this.buf.length);
            var2 += this.buf.length;
         }

         if (var3 > 0) {
            System.arraycopy(var1, var2, this.buf, this.bufOff, var3);
            this.bufOff += var3;
         }
      }
   }

   @Override
   public void write(int var1) throws IOException {
      this.buf[this.bufOff++] = (byte)var1;
      if (this.bufOff == this.buf.length) {
         this.flush();
      }
   }

   @Override
   public void flush() throws IOException {
      this.other.write(this.buf, 0, this.bufOff);
      this.bufOff = 0;
      Arrays.fill(this.buf, (byte)0);
   }

   @Override
   public void close() throws IOException {
      this.flush();
      this.other.close();
   }
}
