package it.unimi.dsi.fastutil.io;

public class FastByteArrayInputStream extends MeasurableInputStream implements RepositionableStream {
   public byte[] array;
   public int offset;
   public int length;
   private int position;
   private int mark;

   public FastByteArrayInputStream(byte[] array, int offset, int length) {
      this.array = array;
      this.offset = offset;
      this.length = length;
   }

   public FastByteArrayInputStream(byte[] array) {
      this(array, 0, array.length);
   }

   @Override
   public boolean markSupported() {
      return true;
   }

   @Override
   public void reset() {
      this.position = this.mark;
   }

   @Override
   public void close() {
   }

   @Override
   public void mark(int dummy) {
      this.mark = this.position;
   }

   @Override
   public int available() {
      return this.length - this.position;
   }

   @Override
   public long skip(long n) {
      if (n <= this.length - this.position) {
         this.position += (int)n;
         return n;
      } else {
         n = this.length - this.position;
         this.position = this.length;
         return n;
      }
   }

   @Override
   public int read() {
      return this.length == this.position ? -1 : this.array[this.offset + this.position++] & 0xFF;
   }

   @Override
   public int read(byte[] b, int offset, int length) {
      if (this.length == this.position) {
         return length == 0 ? 0 : -1;
      } else {
         int n = Math.min(length, this.length - this.position);
         System.arraycopy(this.array, this.offset + this.position, b, offset, n);
         this.position += n;
         return n;
      }
   }

   @Override
   public long position() {
      return this.position;
   }

   @Override
   public void position(long newPosition) {
      this.position = (int)Math.min(newPosition, (long)this.length);
   }

   @Override
   public long length() {
      return this.length;
   }
}
