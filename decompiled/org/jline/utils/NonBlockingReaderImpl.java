package org.jline.utils;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.Reader;

public class NonBlockingReaderImpl extends NonBlockingReader {
   public static final int READ_EXPIRED = -2;
   private Reader in;
   private int ch = -2;
   private String name;
   private boolean threadIsReading = false;
   private IOException exception = null;
   private long threadDelay = 60000L;
   private Thread thread;

   public NonBlockingReaderImpl(String name, Reader in) {
      this.in = in;
      this.name = name;
   }

   private synchronized void startReadingThreadIfNeeded() {
      if (this.thread == null) {
         this.thread = new Thread(this::run);
         this.thread.setName(this.name + " non blocking reader thread");
         this.thread.setDaemon(true);
         this.thread.start();
      }
   }

   @Override
   public synchronized void shutdown() {
      if (this.thread != null) {
         this.notify();
      }
   }

   @Override
   public void close() throws IOException {
      this.in.close();
      this.shutdown();
   }

   @Override
   public synchronized boolean ready() throws IOException {
      return this.ch >= 0 || this.in.ready();
   }

   @Override
   public int readBuffered(char[] b, int off, int len, long timeout) throws IOException {
      if (b == null) {
         throw new NullPointerException();
      } else if (off < 0 || len < 0 || off + len < b.length) {
         throw new IllegalArgumentException();
      } else if (len == 0) {
         return 0;
      } else if (this.exception != null) {
         assert this.ch == -2;

         IOException toBeThrown = this.exception;
         this.exception = null;
         throw toBeThrown;
      } else if (this.ch >= -1) {
         b[0] = (char)this.ch;
         this.ch = -2;
         return 1;
      } else if (!this.threadIsReading && timeout <= 0L) {
         return this.in.read(b, off, len);
      } else {
         int c = this.read(timeout, false);
         if (c >= 0) {
            b[off] = (char)c;
            return 1;
         } else {
            return c;
         }
      }
   }

   @Override
   protected synchronized int read(long timeout, boolean isPeek) throws IOException {
      if (this.exception != null) {
         assert this.ch == -2;

         IOException toBeThrown = this.exception;
         if (!isPeek) {
            this.exception = null;
         }

         throw toBeThrown;
      } else {
         if (this.ch >= -1) {
            assert this.exception == null;
         } else if (!isPeek && timeout <= 0L && !this.threadIsReading) {
            this.ch = this.in.read();
         } else {
            if (!this.threadIsReading) {
               this.threadIsReading = true;
               this.startReadingThreadIfNeeded();
               this.notifyAll();
            }

            Timeout t = new Timeout(timeout);

            while (!t.elapsed()) {
               try {
                  if (Thread.interrupted()) {
                     throw new InterruptedException();
                  }

                  this.wait(t.timeout());
               } catch (InterruptedException var6) {
                  this.exception = (IOException)new InterruptedIOException().initCause(var6);
               }

               if (this.exception != null) {
                  assert this.ch == -2;

                  IOException toBeThrown = this.exception;
                  if (!isPeek) {
                     this.exception = null;
                  }

                  throw toBeThrown;
               }

               if (this.ch >= -1) {
                  assert this.exception == null;
                  break;
               }
            }
         }

         int ret = this.ch;
         if (!isPeek) {
            this.ch = -2;
         }

         return ret;
      }
   }

   private void run() {
      Log.debug("NonBlockingReader start");

      try {
         while (true) {
            synchronized (this) {
               boolean needToRead = this.threadIsReading;

               try {
                  if (!needToRead) {
                     this.wait(this.threadDelay);
                  }
               } catch (InterruptedException var21) {
               }

               needToRead = this.threadIsReading;
               if (!needToRead) {
                  return;
               }
            }

            int charRead = -2;
            IOException failure = null;

            try {
               charRead = this.in.read();
            } catch (IOException var23) {
               failure = var23;
            }

            synchronized (this) {
               this.exception = failure;
               this.ch = charRead;
               this.threadIsReading = false;
               this.notify();
            }
         }
      } catch (Throwable var25) {
         Log.warn("Error in NonBlockingReader thread", var25);
      } finally {
         Log.debug("NonBlockingReader shutdown");
         synchronized (this) {
            this.thread = null;
            this.threadIsReading = false;
         }
      }
   }

   public synchronized void clear() throws IOException {
      while (this.ready()) {
         this.read();
      }
   }
}
