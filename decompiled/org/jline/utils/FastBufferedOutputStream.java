package org.jline.utils;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class FastBufferedOutputStream extends FilterOutputStream {
   protected final byte[] buf = new byte[8192];
   protected int count;

   public FastBufferedOutputStream(OutputStream out) {
      super(out);
   }

   @Override
   public void write(int b) throws IOException {
      if (this.count >= this.buf.length) {
         this.flushBuffer();
      }

      this.buf[this.count++] = (byte)b;
   }

   @Override
   public void write(byte[] b, int off, int len) throws IOException {
      if (len >= this.buf.length) {
         this.flushBuffer();
         this.out.write(b, off, len);
      } else {
         if (len > this.buf.length - this.count) {
            this.flushBuffer();
         }

         System.arraycopy(b, off, this.buf, this.count, len);
         this.count += len;
      }
   }

   private void flushBuffer() throws IOException {
      if (this.count > 0) {
         this.out.write(this.buf, 0, this.count);
         this.count = 0;
      }
   }

   @Override
   public void flush() throws IOException {
      this.flushBuffer();
      this.out.flush();
   }
}
