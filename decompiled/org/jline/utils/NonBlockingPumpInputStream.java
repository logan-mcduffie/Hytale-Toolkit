package org.jline.utils;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;

public class NonBlockingPumpInputStream extends NonBlockingInputStream {
   private static final int DEFAULT_BUFFER_SIZE = 4096;
   private final ByteBuffer readBuffer;
   private final ByteBuffer writeBuffer;
   private final OutputStream output;
   private boolean closed;
   private IOException ioException;

   public NonBlockingPumpInputStream() {
      this(4096);
   }

   public NonBlockingPumpInputStream(int bufferSize) {
      byte[] buf = new byte[bufferSize];
      this.readBuffer = ByteBuffer.wrap(buf);
      this.writeBuffer = ByteBuffer.wrap(buf);
      this.output = new NonBlockingPumpInputStream.NbpOutputStream();
      ((Buffer)this.readBuffer).limit(0);
   }

   public OutputStream getOutputStream() {
      return this.output;
   }

   private int wait(ByteBuffer buffer, long timeout) throws IOException {
      Timeout t = new Timeout(timeout);

      while (!this.closed && !buffer.hasRemaining() && !t.elapsed()) {
         this.notifyAll();

         try {
            this.wait(t.timeout());
            this.checkIoException();
         } catch (InterruptedException var6) {
            this.checkIoException();
            throw new InterruptedIOException();
         }
      }

      return buffer.hasRemaining() ? 0 : (this.closed ? -1 : -2);
   }

   private static boolean rewind(ByteBuffer buffer, ByteBuffer other) {
      if (buffer.position() > other.position()) {
         ((Buffer)other).limit(buffer.position());
      }

      if (buffer.position() == buffer.capacity()) {
         ((Buffer)buffer).rewind();
         ((Buffer)buffer).limit(other.position());
         return true;
      } else {
         return false;
      }
   }

   @Override
   public synchronized int available() {
      int count = this.readBuffer.remaining();
      if (this.writeBuffer.position() < this.readBuffer.position()) {
         count += this.writeBuffer.position();
      }

      return count;
   }

   @Override
   public synchronized int read(long timeout, boolean isPeek) throws IOException {
      this.checkIoException();
      int res = this.wait(this.readBuffer, timeout);
      if (res >= 0) {
         res = this.readBuffer.get() & 255;
      }

      rewind(this.readBuffer, this.writeBuffer);
      return res;
   }

   @Override
   public synchronized int readBuffered(byte[] b, int off, int len, long timeout) throws IOException {
      if (b == null) {
         throw new NullPointerException();
      } else if (off < 0 || len < 0 || off + len < b.length) {
         throw new IllegalArgumentException();
      } else if (len == 0) {
         return 0;
      } else {
         this.checkIoException();
         int res = this.wait(this.readBuffer, timeout);
         if (res >= 0) {
            res = 0;

            while (res < len && this.readBuffer.hasRemaining()) {
               b[off + res++] = (byte)(this.readBuffer.get() & 255);
            }
         }

         rewind(this.readBuffer, this.writeBuffer);
         return res;
      }
   }

   public synchronized void setIoException(IOException exception) {
      this.ioException = exception;
      this.notifyAll();
   }

   protected synchronized void checkIoException() throws IOException {
      if (this.ioException != null) {
         throw this.ioException;
      }
   }

   synchronized void write(byte[] cbuf, int off, int len) throws IOException {
      while (len > 0) {
         if (this.wait(this.writeBuffer, 0L) == -1) {
            throw new ClosedException();
         }

         int count = Math.min(len, this.writeBuffer.remaining());
         this.writeBuffer.put(cbuf, off, count);
         off += count;
         len -= count;
         rewind(this.writeBuffer, this.readBuffer);
      }
   }

   synchronized void flush() {
      if (this.readBuffer.hasRemaining()) {
         this.notifyAll();
      }
   }

   @Override
   public synchronized void close() throws IOException {
      this.closed = true;
      this.notifyAll();
   }

   private class NbpOutputStream extends OutputStream {
      private NbpOutputStream() {
      }

      @Override
      public void write(int b) throws IOException {
         NonBlockingPumpInputStream.this.write(new byte[]{(byte)b}, 0, 1);
      }

      @Override
      public void write(byte[] cbuf, int off, int len) throws IOException {
         NonBlockingPumpInputStream.this.write(cbuf, off, len);
      }

      @Override
      public void flush() throws IOException {
         NonBlockingPumpInputStream.this.flush();
      }

      @Override
      public void close() throws IOException {
         NonBlockingPumpInputStream.this.close();
      }
   }
}
