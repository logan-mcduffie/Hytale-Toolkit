package org.jline.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;

public class NonBlockingInputStreamImpl extends NonBlockingInputStream {
   private InputStream in;
   private int b = -2;
   private String name;
   private boolean threadIsReading = false;
   private IOException exception = null;
   private long threadDelay = 60000L;
   private Thread thread;

   public NonBlockingInputStreamImpl(String name, InputStream in) {
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
   public synchronized int read(long timeout, boolean isPeek) throws IOException {
      if (this.exception != null) {
         assert this.b == -2;

         IOException toBeThrown = this.exception;
         if (!isPeek) {
            this.exception = null;
         }

         throw toBeThrown;
      } else {
         if (this.b >= -1) {
            assert this.exception == null;
         } else if (!isPeek && timeout <= 0L && !this.threadIsReading) {
            this.b = this.in.read();
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
                  assert this.b == -2;

                  IOException toBeThrown = this.exception;
                  if (!isPeek) {
                     this.exception = null;
                  }

                  throw toBeThrown;
               }

               if (this.b >= -1) {
                  assert this.exception == null;
                  break;
               }
            }
         }

         int ret = this.b;
         if (!isPeek) {
            this.b = -2;
         }

         return ret;
      }
   }

   private void run() {
      Log.debug("NonBlockingInputStream start");

      try {
         int byteRead;
         do {
            synchronized (this) {
               boolean needToRead = this.threadIsReading;

               try {
                  if (!needToRead) {
                     this.wait(this.threadDelay);
                  }
               } catch (InterruptedException var25) {
               }

               needToRead = this.threadIsReading;
               if (!needToRead) {
                  return;
               }
            }

            byteRead = -2;
            IOException failure = null;

            try {
               byteRead = this.in.read();
            } catch (IOException var24) {
               failure = var24;
            }

            synchronized (this) {
               this.exception = failure;
               this.b = byteRead;
               this.threadIsReading = false;
               this.notify();
            }
         } while (byteRead >= 0);
      } catch (Throwable var27) {
         Log.warn("Error in NonBlockingInputStream thread", var27);
      } finally {
         Log.debug("NonBlockingInputStream shutdown");
         synchronized (this) {
            this.thread = null;
            this.threadIsReading = false;
         }
      }
   }
}
