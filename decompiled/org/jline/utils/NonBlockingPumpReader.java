package org.jline.utils;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.Writer;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class NonBlockingPumpReader extends NonBlockingReader {
   private static final int DEFAULT_BUFFER_SIZE = 4096;
   private final char[] buffer;
   private int read;
   private int write;
   private int count;
   final ReentrantLock lock;
   private final Condition notEmpty;
   private final Condition notFull;
   private final Writer writer;
   private boolean closed;

   public NonBlockingPumpReader() {
      this(4096);
   }

   public NonBlockingPumpReader(int bufferSize) {
      this.buffer = new char[bufferSize];
      this.writer = new NonBlockingPumpReader.NbpWriter();
      this.lock = new ReentrantLock();
      this.notEmpty = this.lock.newCondition();
      this.notFull = this.lock.newCondition();
   }

   public Writer getWriter() {
      return this.writer;
   }

   @Override
   public boolean ready() {
      return this.available() > 0;
   }

   @Override
   public int available() {
      ReentrantLock lock = this.lock;
      lock.lock();

      int var2;
      try {
         var2 = this.count;
      } finally {
         lock.unlock();
      }

      return var2;
   }

   @Override
   protected int read(long timeout, boolean isPeek) throws IOException {
      ReentrantLock lock = this.lock;
      lock.lock();

      int res;
      try {
         if (!this.closed && this.count == 0) {
            try {
               if (timeout > 0L) {
                  this.notEmpty.await(timeout, TimeUnit.MILLISECONDS);
               } else {
                  this.notEmpty.await();
               }
            } catch (InterruptedException var10) {
               throw (IOException)new InterruptedIOException().initCause(var10);
            }
         }

         if (this.closed) {
            return -1;
         }

         if (this.count == 0) {
            return -2;
         }

         if (!isPeek) {
            res = this.buffer[this.read];
            if (++this.read == this.buffer.length) {
               this.read = 0;
            }

            this.count--;
            this.notFull.signal();
            return res;
         }

         res = this.buffer[this.read];
      } finally {
         lock.unlock();
      }

      return res;
   }

   @Override
   public int readBuffered(char[] b, int off, int len, long timeout) throws IOException {
      if (b == null) {
         throw new NullPointerException();
      } else if (off < 0 || len < 0 || off + len < b.length) {
         throw new IllegalArgumentException();
      } else if (len == 0) {
         return 0;
      } else {
         ReentrantLock lock = this.lock;
         lock.lock();

         int r;
         try {
            if (!this.closed && this.count == 0) {
               try {
                  if (timeout > 0L) {
                     if (!this.notEmpty.await(timeout, TimeUnit.MILLISECONDS)) {
                        throw new IOException("Timeout reading");
                     }
                  } else {
                     this.notEmpty.await();
                  }
               } catch (InterruptedException var12) {
                  throw (IOException)new InterruptedIOException().initCause(var12);
               }
            }

            if (this.closed) {
               return -1;
            }

            if (this.count != 0) {
               r = Math.min(len, this.count);

               for (int i = 0; i < r; i++) {
                  b[off + i] = this.buffer[this.read++];
                  if (this.read == this.buffer.length) {
                     this.read = 0;
                  }
               }

               this.count -= r;
               this.notFull.signal();
               return r;
            }

            r = -2;
         } finally {
            lock.unlock();
         }

         return r;
      }
   }

   void write(char[] cbuf, int off, int len) throws IOException {
      if (len > 0) {
         ReentrantLock lock = this.lock;
         lock.lock();

         try {
            while (len > 0) {
               if (!this.closed && this.count == this.buffer.length) {
                  try {
                     this.notFull.await();
                  } catch (InterruptedException var9) {
                     throw (IOException)new InterruptedIOException().initCause(var9);
                  }
               }

               if (this.closed) {
                  throw new IOException("Closed");
               }

               while (len > 0 && this.count < this.buffer.length) {
                  this.buffer[this.write++] = cbuf[off++];
                  this.count++;
                  len--;
                  if (this.write == this.buffer.length) {
                     this.write = 0;
                  }
               }

               this.notEmpty.signal();
            }
         } finally {
            lock.unlock();
         }
      }
   }

   @Override
   public void close() throws IOException {
      ReentrantLock lock = this.lock;
      lock.lock();

      try {
         this.closed = true;
         this.notEmpty.signalAll();
         this.notFull.signalAll();
      } finally {
         lock.unlock();
      }
   }

   private class NbpWriter extends Writer {
      private NbpWriter() {
      }

      @Override
      public void write(char[] cbuf, int off, int len) throws IOException {
         NonBlockingPumpReader.this.write(cbuf, off, len);
      }

      @Override
      public void flush() throws IOException {
      }

      @Override
      public void close() throws IOException {
         NonBlockingPumpReader.this.close();
      }
   }
}
