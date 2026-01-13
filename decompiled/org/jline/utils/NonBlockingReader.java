package org.jline.utils;

import java.io.IOException;
import java.io.Reader;

public abstract class NonBlockingReader extends Reader {
   public static final int EOF = -1;
   public static final int READ_EXPIRED = -2;

   public void shutdown() {
   }

   @Override
   public int read() throws IOException {
      return this.read(0L, false);
   }

   public int peek(long timeout) throws IOException {
      return this.read(timeout, true);
   }

   public int read(long timeout) throws IOException {
      return this.read(timeout, false);
   }

   @Override
   public int read(char[] b, int off, int len) throws IOException {
      if (b == null) {
         throw new NullPointerException();
      } else if (off < 0 || len < 0 || len > b.length - off) {
         throw new IndexOutOfBoundsException();
      } else if (len == 0) {
         return 0;
      } else {
         int c = this.read(0L);
         if (c == -1) {
            return -1;
         } else {
            b[off] = (char)c;
            return 1;
         }
      }
   }

   public int readBuffered(char[] b) throws IOException {
      return this.readBuffered(b, 0L);
   }

   public int readBuffered(char[] b, long timeout) throws IOException {
      return this.readBuffered(b, 0, b.length, timeout);
   }

   public abstract int readBuffered(char[] var1, int var2, int var3, long var4) throws IOException;

   public int available() {
      return 0;
   }

   protected abstract int read(long var1, boolean var3) throws IOException;
}
