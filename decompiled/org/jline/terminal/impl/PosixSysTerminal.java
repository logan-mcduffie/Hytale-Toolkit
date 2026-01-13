package org.jline.terminal.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.jline.terminal.Terminal;
import org.jline.terminal.spi.Pty;
import org.jline.utils.FastBufferedOutputStream;
import org.jline.utils.NonBlocking;
import org.jline.utils.NonBlockingInputStream;
import org.jline.utils.NonBlockingReader;
import org.jline.utils.ShutdownHooks;
import org.jline.utils.Signals;

public class PosixSysTerminal extends AbstractPosixTerminal {
   protected final NonBlockingInputStream input;
   protected final OutputStream output;
   protected final NonBlockingReader reader;
   protected final PrintWriter writer;
   protected final Map<Terminal.Signal, Object> nativeHandlers = new HashMap<>();
   protected final ShutdownHooks.Task closer;

   public PosixSysTerminal(String name, String type, Pty pty, Charset encoding, boolean nativeSignals, Terminal.SignalHandler signalHandler) throws IOException {
      this(name, type, pty, encoding, encoding, encoding, nativeSignals, signalHandler);
   }

   public PosixSysTerminal(
      String name,
      String type,
      Pty pty,
      Charset encoding,
      Charset inputEncoding,
      Charset outputEncoding,
      boolean nativeSignals,
      Terminal.SignalHandler signalHandler
   ) throws IOException {
      super(name, type, pty, encoding, inputEncoding, outputEncoding, signalHandler);
      this.input = NonBlocking.nonBlocking(this.getName(), pty.getSlaveInput());
      this.output = new FastBufferedOutputStream(pty.getSlaveOutput());
      this.reader = NonBlocking.nonBlocking(this.getName(), this.input, this.inputEncoding());
      this.writer = new PrintWriter(new OutputStreamWriter(this.output, this.outputEncoding()));
      this.parseInfoCmp();
      if (nativeSignals) {
         for (Terminal.Signal signal : Terminal.Signal.values()) {
            if (signalHandler == Terminal.SignalHandler.SIG_DFL) {
               this.nativeHandlers.put(signal, Signals.registerDefault(signal.name()));
            } else {
               this.nativeHandlers.put(signal, Signals.register(signal.name(), () -> this.raise(signal)));
            }
         }
      }

      this.closer = this::close;
      ShutdownHooks.add(this.closer);
   }

   @Override
   public Terminal.SignalHandler handle(Terminal.Signal signal, Terminal.SignalHandler handler) {
      Terminal.SignalHandler prev = super.handle(signal, handler);
      if (prev != handler) {
         if (handler == Terminal.SignalHandler.SIG_DFL) {
            Signals.registerDefault(signal.name());
         } else {
            Signals.register(signal.name(), () -> this.raise(signal));
         }
      }

      return prev;
   }

   @Override
   public NonBlockingReader reader() {
      return this.reader;
   }

   @Override
   public PrintWriter writer() {
      return this.writer;
   }

   @Override
   public InputStream input() {
      return this.input;
   }

   @Override
   public OutputStream output() {
      return this.output;
   }

   @Override
   protected void doClose() throws IOException {
      this.writer.flush();
      ShutdownHooks.remove(this.closer);

      for (Entry<Terminal.Signal, Object> entry : this.nativeHandlers.entrySet()) {
         Signals.unregister(entry.getKey().name(), entry.getValue());
      }

      super.doClose();
      this.reader.shutdown();
   }
}
