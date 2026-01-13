package org.jline.terminal.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import org.jline.terminal.Attributes;
import org.jline.terminal.Size;
import org.jline.terminal.Terminal;
import org.jline.terminal.spi.SystemStream;
import org.jline.terminal.spi.TerminalProvider;
import org.jline.utils.NonBlocking;
import org.jline.utils.NonBlockingInputStream;
import org.jline.utils.NonBlockingReader;

public class DumbTerminal extends AbstractTerminal {
   private final TerminalProvider provider;
   private final SystemStream systemStream;
   private final NonBlockingInputStream input;
   private final OutputStream output;
   private final NonBlockingReader reader;
   private final PrintWriter writer;
   private final Attributes attributes;
   private final Size size;
   private boolean skipNextLf;

   public DumbTerminal(InputStream in, OutputStream out) throws IOException {
      this("dumb", "dumb", in, out, null);
   }

   public DumbTerminal(String name, String type, InputStream in, OutputStream out, Charset encoding) throws IOException {
      this(null, null, name, type, in, out, encoding, Terminal.SignalHandler.SIG_DFL);
   }

   public DumbTerminal(
      TerminalProvider provider,
      SystemStream systemStream,
      String name,
      String type,
      InputStream in,
      OutputStream out,
      Charset encoding,
      Terminal.SignalHandler signalHandler
   ) throws IOException {
      this(provider, systemStream, name, type, in, out, encoding, encoding, encoding, signalHandler);
   }

   public DumbTerminal(
      TerminalProvider provider,
      SystemStream systemStream,
      String name,
      String type,
      InputStream in,
      OutputStream out,
      Charset encoding,
      Charset inputEncoding,
      Charset outputEncoding,
      Terminal.SignalHandler signalHandler
   ) throws IOException {
      super(name, type, encoding, inputEncoding, outputEncoding, signalHandler);
      this.provider = provider;
      this.systemStream = systemStream;
      final NonBlockingInputStream nbis = NonBlocking.nonBlocking(this.getName(), in);
      this.input = new NonBlockingInputStream() {
         @Override
         public int read(long timeout, boolean isPeek) throws IOException {
            while (true) {
               int c = nbis.read(timeout, isPeek);
               if (DumbTerminal.this.attributes.getLocalFlag(Attributes.LocalFlag.ISIG)) {
                  if (c == DumbTerminal.this.attributes.getControlChar(Attributes.ControlChar.VINTR)) {
                     DumbTerminal.this.raise(Terminal.Signal.INT);
                     continue;
                  }

                  if (c == DumbTerminal.this.attributes.getControlChar(Attributes.ControlChar.VQUIT)) {
                     DumbTerminal.this.raise(Terminal.Signal.QUIT);
                     continue;
                  }

                  if (c == DumbTerminal.this.attributes.getControlChar(Attributes.ControlChar.VSUSP)) {
                     DumbTerminal.this.raise(Terminal.Signal.TSTP);
                     continue;
                  }

                  if (c == DumbTerminal.this.attributes.getControlChar(Attributes.ControlChar.VSTATUS)) {
                     DumbTerminal.this.raise(Terminal.Signal.INFO);
                     continue;
                  }
               }

               if (DumbTerminal.this.attributes.getInputFlag(Attributes.InputFlag.INORMEOL)) {
                  if (c == 13) {
                     DumbTerminal.this.skipNextLf = true;
                     c = 10;
                  } else if (c == 10) {
                     if (DumbTerminal.this.skipNextLf) {
                        DumbTerminal.this.skipNextLf = false;
                        continue;
                     }
                  } else {
                     DumbTerminal.this.skipNextLf = false;
                  }
               } else if (c == 13) {
                  if (DumbTerminal.this.attributes.getInputFlag(Attributes.InputFlag.IGNCR)) {
                     continue;
                  }

                  if (DumbTerminal.this.attributes.getInputFlag(Attributes.InputFlag.ICRNL)) {
                     c = 10;
                  }
               } else if (c == 10 && DumbTerminal.this.attributes.getInputFlag(Attributes.InputFlag.INLCR)) {
                  c = 13;
               }

               return c;
            }
         }
      };
      this.output = out;
      this.reader = NonBlocking.nonBlocking(this.getName(), this.input, this.inputEncoding());
      this.writer = new PrintWriter(new OutputStreamWriter(this.output, this.outputEncoding()));
      this.attributes = new Attributes();
      this.attributes.setControlChar(Attributes.ControlChar.VERASE, 127);
      this.attributes.setControlChar(Attributes.ControlChar.VWERASE, 23);
      this.attributes.setControlChar(Attributes.ControlChar.VKILL, 21);
      this.attributes.setControlChar(Attributes.ControlChar.VLNEXT, 22);
      this.size = new Size();
      this.parseInfoCmp();
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
   public Attributes getAttributes() {
      return new Attributes(this.attributes);
   }

   @Override
   public void setAttributes(Attributes attr) {
      this.attributes.copy(attr);
   }

   @Override
   public Size getSize() {
      Size sz = new Size();
      sz.copy(this.size);
      return sz;
   }

   @Override
   public void setSize(Size sz) {
      this.size.copy(sz);
   }

   @Override
   public TerminalProvider getProvider() {
      return this.provider;
   }

   @Override
   public SystemStream getSystemStream() {
      return this.systemStream;
   }
}
