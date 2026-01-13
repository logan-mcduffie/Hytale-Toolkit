package com.nimbusds.jose.util;

import java.io.IOException;
import java.io.InputStream;

public class BoundedInputStream extends InputStream {
   private final InputStream in;
   private final long max;
   private long pos = 0L;
   private long mark = -1L;
   private boolean propagateClose = true;

   public BoundedInputStream(InputStream in, long size) {
      this.max = size;
      this.in = in;
   }

   public BoundedInputStream(InputStream in) {
      this(in, -1L);
   }

   public long getLimitBytes() {
      return this.max;
   }

   @Override
   public int read() throws IOException {
      if (this.max >= 0L && this.pos >= this.max) {
         throw new IOException("Exceeded configured input limit of " + this.max + " bytes");
      } else {
         int result = this.in.read();
         this.pos++;
         return result;
      }
   }

   @Override
   public int read(byte[] b) throws IOException {
      return this.read(b, 0, b.length);
   }

   @Override
   public int read(byte[] b, int off, int len) throws IOException {
      if (this.max >= 0L && this.pos >= this.max) {
         throw new IOException("Exceeded configured input limit of " + this.max + " bytes");
      } else {
         int bytesRead = this.in.read(b, off, len);
         if (bytesRead == -1) {
            return -1;
         } else {
            this.pos += bytesRead;
            if (this.max >= 0L && this.pos >= this.max) {
               throw new IOException("Exceeded configured input limit of " + this.max + " bytes");
            } else {
               return bytesRead;
            }
         }
      }
   }

   @Override
   public long skip(long n) throws IOException {
      long toSkip = this.max >= 0L ? Math.min(n, this.max - this.pos) : n;
      long skippedBytes = this.in.skip(toSkip);
      this.pos += skippedBytes;
      return skippedBytes;
   }

   @Override
   public int available() throws IOException {
      return this.max >= 0L && this.pos >= this.max ? 0 : this.in.available();
   }

   @Override
   public String toString() {
      return this.in.toString();
   }

   @Override
   public void close() throws IOException {
      if (this.propagateClose) {
         this.in.close();
      }
   }

   @Override
   public synchronized void reset() throws IOException {
      this.in.reset();
      this.pos = this.mark;
   }

   @Override
   public synchronized void mark(int readlimit) {
      this.in.mark(readlimit);
      this.mark = this.pos;
   }

   @Override
   public boolean markSupported() {
      return this.in.markSupported();
   }

   public boolean isPropagateClose() {
      return this.propagateClose;
   }

   public void setPropagateClose(boolean propagateClose) {
      this.propagateClose = propagateClose;
   }
}
