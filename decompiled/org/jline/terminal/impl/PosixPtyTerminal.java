package org.jline.terminal.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.Objects;
import org.jline.terminal.Terminal;
import org.jline.terminal.spi.Pty;
import org.jline.utils.ClosedException;
import org.jline.utils.NonBlocking;
import org.jline.utils.NonBlockingInputStream;
import org.jline.utils.NonBlockingReader;

public class PosixPtyTerminal extends AbstractPosixTerminal {
   private final InputStream in;
   private final OutputStream out;
   private final InputStream masterInput;
   private final OutputStream masterOutput;
   private final NonBlockingInputStream input;
   private final OutputStream output;
   private final NonBlockingReader reader;
   private final PrintWriter writer;
   private final Object lock = new Object();
   private Thread inputPumpThread;
   private Thread outputPumpThread;
   private boolean paused = true;

   public PosixPtyTerminal(String name, String type, Pty pty, InputStream in, OutputStream out, Charset encoding) throws IOException {
      this(name, type, pty, in, out, encoding, Terminal.SignalHandler.SIG_DFL);
   }

   public PosixPtyTerminal(String name, String type, Pty pty, InputStream in, OutputStream out, Charset encoding, Terminal.SignalHandler signalHandler) throws IOException {
      this(name, type, pty, in, out, encoding, signalHandler, false);
   }

   public PosixPtyTerminal(
      String name, String type, Pty pty, InputStream in, OutputStream out, Charset encoding, Terminal.SignalHandler signalHandler, boolean paused
   ) throws IOException {
      this(name, type, pty, in, out, encoding, encoding, encoding, signalHandler, paused);
   }

   public PosixPtyTerminal(
      String name,
      String type,
      Pty pty,
      InputStream in,
      OutputStream out,
      Charset encoding,
      Charset inputEncoding,
      Charset outputEncoding,
      Terminal.SignalHandler signalHandler,
      boolean paused
   ) throws IOException {
      super(name, type, pty, encoding, inputEncoding, outputEncoding, signalHandler);
      this.in = Objects.requireNonNull(in);
      this.out = Objects.requireNonNull(out);
      this.masterInput = pty.getMasterInput();
      this.masterOutput = pty.getMasterOutput();
      this.input = new PosixPtyTerminal.InputStreamWrapper(NonBlocking.nonBlocking(name, pty.getSlaveInput()));
      this.output = pty.getSlaveOutput();
      this.reader = NonBlocking.nonBlocking(name, this.input, this.inputEncoding());
      this.writer = new PrintWriter(new OutputStreamWriter(this.output, this.outputEncoding()));
      this.parseInfoCmp();
      if (!paused) {
         this.resume();
      }
   }

   @Override
   public InputStream input() {
      return this.input;
   }

   @Override
   public NonBlockingReader reader() {
      return this.reader;
   }

   @Override
   public OutputStream output() {
      return this.output;
   }

   @Override
   public PrintWriter writer() {
      return this.writer;
   }

   @Override
   protected void doClose() throws IOException {
      super.doClose();
      this.reader.close();
   }

   @Override
   public boolean canPauseResume() {
      return true;
   }

   @Override
   public void pause() {
      try {
         this.pause(false);
      } catch (InterruptedException var2) {
      }
   }

   @Override
   public void pause(boolean wait) throws InterruptedException {
      Thread p1;
      Thread p2;
      synchronized (this.lock) {
         this.paused = true;
         p1 = this.inputPumpThread;
         p2 = this.outputPumpThread;
      }

      if (p1 != null) {
         p1.interrupt();
      }

      if (p2 != null) {
         p2.interrupt();
      }

      if (wait) {
         if (p1 != null) {
            p1.join();
         }

         if (p2 != null) {
            p2.join();
         }
      }
   }

   @Override
   public void resume() {
      synchronized (this.lock) {
         this.paused = false;
         if (this.inputPumpThread == null) {
            this.inputPumpThread = new Thread(this::pumpIn, this.toString() + " input pump thread");
            this.inputPumpThread.setDaemon(true);
            this.inputPumpThread.start();
         }

         if (this.outputPumpThread == null) {
            this.outputPumpThread = new Thread(this::pumpOut, this.toString() + " output pump thread");
            this.outputPumpThread.setDaemon(true);
            this.outputPumpThread.start();
         }
      }
   }

   @Override
   public boolean paused() {
      synchronized (this.lock) {
         return this.paused;
      }
   }

   private void pumpIn() {
      while (true) {
         try {
            synchronized (this.lock) {
               if (this.paused) {
                  this.inputPumpThread = null;
                  return;
               }
            }

            int b = this.in.read();
            if (b < 0) {
               this.input.close();
               break;
            }

            this.masterOutput.write(b);
            this.masterOutput.flush();
         } catch (IOException var19) {
            var19.printStackTrace();
            break;
         } finally {
            synchronized (this.lock) {
               this.inputPumpThread = null;
            }
         }
      }
   }

   private void pumpOut() {
      try {
         while (true) {
            synchronized (this.lock) {
               if (this.paused) {
                  this.outputPumpThread = null;
                  return;
               }
            }

            int b = this.masterInput.read();
            if (b < 0) {
               this.input.close();
               break;
            }

            this.out.write(b);
            this.out.flush();
         }
      } catch (IOException var21) {
         var21.printStackTrace();
      } finally {
         synchronized (this.lock) {
            this.outputPumpThread = null;
         }
      }

      try {
         this.close();
      } catch (Throwable var19) {
      }
   }

   private static class InputStreamWrapper extends NonBlockingInputStream {
      private final NonBlockingInputStream in;
      private volatile boolean closed;

      protected InputStreamWrapper(NonBlockingInputStream in) {
         this.in = in;
      }

      @Override
      public int read(long timeout, boolean isPeek) throws IOException {
         if (this.closed) {
            throw new ClosedException();
         } else {
            return this.in.read(timeout, isPeek);
         }
      }

      @Override
      public void close() throws IOException {
         this.closed = true;
      }
   }
}
