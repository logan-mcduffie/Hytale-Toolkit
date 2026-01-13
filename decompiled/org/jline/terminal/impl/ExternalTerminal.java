package org.jline.terminal.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.IntConsumer;
import org.jline.terminal.Attributes;
import org.jline.terminal.Cursor;
import org.jline.terminal.Size;
import org.jline.terminal.Terminal;
import org.jline.terminal.spi.TerminalProvider;

public class ExternalTerminal extends LineDisciplineTerminal {
   private final TerminalProvider provider;
   protected final AtomicBoolean closed = new AtomicBoolean();
   protected final InputStream masterInput;
   protected final Object lock = new Object();
   protected boolean paused = true;
   protected Thread pumpThread;

   public ExternalTerminal(String name, String type, InputStream masterInput, OutputStream masterOutput, Charset encoding) throws IOException {
      this(null, name, type, masterInput, masterOutput, encoding, encoding, encoding, encoding, Terminal.SignalHandler.SIG_DFL);
   }

   public ExternalTerminal(
      TerminalProvider provider,
      String name,
      String type,
      InputStream masterInput,
      OutputStream masterOutput,
      Charset encoding,
      Terminal.SignalHandler signalHandler
   ) throws IOException {
      this(provider, name, type, masterInput, masterOutput, encoding, encoding, encoding, signalHandler, false);
   }

   public ExternalTerminal(
      TerminalProvider provider,
      String name,
      String type,
      InputStream masterInput,
      OutputStream masterOutput,
      Charset encoding,
      Charset stdinEncoding,
      Charset stdoutEncoding,
      Charset stderrEncoding,
      Terminal.SignalHandler signalHandler
   ) throws IOException {
      this(provider, name, type, masterInput, masterOutput, encoding, stdinEncoding, stdoutEncoding, signalHandler, false);
   }

   public ExternalTerminal(
      TerminalProvider provider,
      String name,
      String type,
      InputStream masterInput,
      OutputStream masterOutput,
      Charset encoding,
      Terminal.SignalHandler signalHandler,
      boolean paused
   ) throws IOException {
      this(provider, name, type, masterInput, masterOutput, encoding, encoding, encoding, signalHandler, paused, null, null);
   }

   public ExternalTerminal(
      TerminalProvider provider,
      String name,
      String type,
      InputStream masterInput,
      OutputStream masterOutput,
      Charset encoding,
      Charset inputEncoding,
      Charset outputEncoding,
      Terminal.SignalHandler signalHandler,
      boolean paused
   ) throws IOException {
      this(provider, name, type, masterInput, masterOutput, encoding, inputEncoding, outputEncoding, signalHandler, paused, null, null);
   }

   public ExternalTerminal(
      TerminalProvider provider,
      String name,
      String type,
      InputStream masterInput,
      OutputStream masterOutput,
      Charset encoding,
      Terminal.SignalHandler signalHandler,
      boolean paused,
      Attributes attributes,
      Size size
   ) throws IOException {
      this(provider, name, type, masterInput, masterOutput, encoding, encoding, encoding, signalHandler, paused, attributes, size);
   }

   public ExternalTerminal(
      TerminalProvider provider,
      String name,
      String type,
      InputStream masterInput,
      OutputStream masterOutput,
      Charset encoding,
      Charset inputEncoding,
      Charset outputEncoding,
      Terminal.SignalHandler signalHandler,
      boolean paused,
      Attributes attributes,
      Size size
   ) throws IOException {
      super(name, type, masterOutput, encoding, inputEncoding, outputEncoding, signalHandler);
      this.provider = provider;
      this.masterInput = masterInput;
      if (attributes != null) {
         this.setAttributes(attributes);
      }

      if (size != null) {
         this.setSize(size);
      }

      if (!paused) {
         this.resume();
      }
   }

   @Override
   protected void doClose() throws IOException {
      if (this.closed.compareAndSet(false, true)) {
         this.pause();
         super.doClose();
      }
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
      Thread p;
      synchronized (this.lock) {
         this.paused = true;
         p = this.pumpThread;
      }

      if (p != null) {
         p.interrupt();
         if (wait) {
            p.join();
         }
      }
   }

   @Override
   public void resume() {
      synchronized (this.lock) {
         this.paused = false;
         if (this.pumpThread == null) {
            this.pumpThread = new Thread(this::pump, this.toString() + " input pump thread");
            this.pumpThread.setDaemon(true);
            this.pumpThread.start();
         }
      }
   }

   @Override
   public boolean paused() {
      synchronized (this.lock) {
         return this.paused;
      }
   }

   public void pump() {
      try {
         byte[] buf = new byte[1024];

         while (true) {
            int c = this.masterInput.read(buf);
            if (c >= 0) {
               this.processInputBytes(buf, 0, c);
            }

            if (c < 0 || this.closed.get()) {
               break;
            }

            synchronized (this.lock) {
               if (this.paused) {
                  this.pumpThread = null;
                  return;
               }
            }
         }
      } catch (IOException var23) {
         this.processIOException(var23);
      } finally {
         synchronized (this.lock) {
            this.pumpThread = null;
         }
      }

      try {
         this.slaveInput.close();
      } catch (IOException var21) {
      }
   }

   @Override
   public Cursor getCursorPosition(IntConsumer discarded) {
      return CursorSupport.getCursorPosition(this, discarded);
   }

   @Override
   public TerminalProvider getProvider() {
      return this.provider;
   }
}
