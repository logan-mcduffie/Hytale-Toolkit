package it.unimi.dsi.fastutil.io;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

public class FastMultiByteArrayInputStream extends MeasurableInputStream implements RepositionableStream {
   public static final int SLICE_BITS = 30;
   public static final int SLICE_SIZE = 1073741824;
   public static final int SLICE_MASK = 1073741823;
   public byte[][] array;
   public byte[] current;
   public long length;
   private long position;

   public FastMultiByteArrayInputStream(MeasurableInputStream is) throws IOException {
      this(is, is.length());
   }

   public FastMultiByteArrayInputStream(InputStream is, long size) throws IOException {
      this.length = size;
      this.array = new byte[(int)((size + 1073741824L - 1L) / 1073741824L) + 1][];

      for (int i = 0; i < this.array.length - 1; i++) {
         this.array[i] = new byte[size >= 1073741824L ? 1073741824 : (int)size];
         if (read(is, this.array[i]) != this.array[i].length) {
            throw new EOFException();
         }

         size -= this.array[i].length;
      }

      this.current = this.array[0];
   }

   private static long read(InputStream is, byte[] a) throws IOException {
      if (a.length == 0) {
         return 0L;
      } else {
         int read = 0;

         do {
            int result = is.read(a, read, Math.min(a.length - read, 1048576));
            if (result < 0) {
               return read;
            }

            read += result;
         } while (read < a.length);

         return read;
      }
   }

   public FastMultiByteArrayInputStream(FastMultiByteArrayInputStream is) {
      this.array = is.array;
      this.length = is.length;
      this.current = this.array[0];
   }

   public FastMultiByteArrayInputStream(byte[] array) {
      if (array.length == 0) {
         this.array = new byte[1][];
      } else {
         this.array = new byte[2][];
         this.array[0] = array;
         this.length = array.length;
         this.current = array;
      }
   }

   @Override
   public int available() {
      return (int)Math.min(2147483647L, this.length - this.position);
   }

   @Override
   public long skip(long n) {
      if (n > this.length - this.position) {
         n = this.length - this.position;
      }

      this.position += n;
      this.updateCurrent();
      return n;
   }

   @Override
   public int read() {
      if (this.length == this.position) {
         return -1;
      } else {
         int disp = (int)(this.position++ & 1073741823L);
         if (disp == 0) {
            this.updateCurrent();
         }

         return this.current[disp] & 0xFF;
      }
   }

   @Override
   public int read(byte[] b, int offset, int length) {
      long remaining = this.length - this.position;
      if (remaining == 0L) {
         return length == 0 ? 0 : -1;
      } else {
         int n = (int)Math.min((long)length, remaining);
         int m = n;

         while (true) {
            int disp = (int)(this.position & 1073741823L);
            if (disp == 0) {
               this.updateCurrent();
            }

            int res = Math.min(n, this.current.length - disp);
            System.arraycopy(this.current, disp, b, offset, res);
            n -= res;
            this.position += res;
            if (n == 0) {
               return m;
            }

            offset += res;
         }
      }
   }

   private void updateCurrent() {
      this.current = this.array[(int)(this.position >>> 30)];
   }

   @Override
   public long position() {
      return this.position;
   }

   @Override
   public void position(long newPosition) {
      this.position = Math.min(newPosition, this.length);
      this.updateCurrent();
   }

   @Override
   public long length() throws IOException {
      return this.length;
   }

   @Override
   public void close() {
   }

   @Override
   public boolean markSupported() {
      return false;
   }

   @Override
   public void mark(int dummy) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void reset() {
      throw new UnsupportedOperationException();
   }
}
