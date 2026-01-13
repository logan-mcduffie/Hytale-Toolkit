package org.bouncycastle.util.io;

import java.io.OutputStream;

public class LimitedBuffer extends OutputStream {
   private final byte[] buf;
   private int count;

   public LimitedBuffer(int var1) {
      this.buf = new byte[var1];
      this.count = 0;
   }

   public int copyTo(byte[] var1, int var2) {
      System.arraycopy(this.buf, 0, var1, var2, this.count);
      return this.count;
   }

   public int limit() {
      return this.buf.length;
   }

   public void reset() {
      this.count = 0;
   }

   public int size() {
      return this.count;
   }

   @Override
   public void write(int var1) {
      this.buf[this.count++] = (byte)var1;
   }

   @Override
   public void write(byte[] var1) {
      System.arraycopy(var1, 0, this.buf, this.count, var1.length);
      this.count += var1.length;
   }

   @Override
   public void write(byte[] var1, int var2, int var3) {
      System.arraycopy(var1, var2, this.buf, this.count, var3);
      this.count += var3;
   }
}
