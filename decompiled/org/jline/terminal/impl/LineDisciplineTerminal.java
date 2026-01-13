package org.jline.terminal.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.EnumSet;
import java.util.Objects;
import org.jline.terminal.Attributes;
import org.jline.terminal.Size;
import org.jline.terminal.Terminal;
import org.jline.terminal.spi.SystemStream;
import org.jline.terminal.spi.TerminalProvider;
import org.jline.utils.NonBlocking;
import org.jline.utils.NonBlockingPumpInputStream;
import org.jline.utils.NonBlockingReader;

public class LineDisciplineTerminal extends AbstractTerminal {
   private static final int PIPE_SIZE = 1024;
   protected final OutputStream masterOutput;
   protected final OutputStream slaveInputPipe;
   protected final NonBlockingPumpInputStream slaveInput;
   protected final NonBlockingReader slaveReader;
   protected final PrintWriter slaveWriter;
   protected final OutputStream slaveOutput;
   protected final Attributes attributes;
   protected final Size size;
   protected boolean skipNextLf;

   public LineDisciplineTerminal(String name, String type, OutputStream masterOutput, Charset encoding) throws IOException {
      this(name, type, masterOutput, encoding, Terminal.SignalHandler.SIG_DFL);
   }

   public LineDisciplineTerminal(String name, String type, OutputStream masterOutput, Charset encoding, Terminal.SignalHandler signalHandler) throws IOException {
      this(name, type, masterOutput, encoding, encoding, encoding, signalHandler);
   }

   public LineDisciplineTerminal(
      String name,
      String type,
      OutputStream masterOutput,
      Charset encoding,
      Charset inputEncoding,
      Charset outputEncoding,
      Terminal.SignalHandler signalHandler
   ) throws IOException {
      super(name, type, encoding, inputEncoding, outputEncoding, signalHandler);
      NonBlockingPumpInputStream input = NonBlocking.nonBlockingPumpInputStream(1024);
      this.slaveInputPipe = input.getOutputStream();
      this.slaveInput = input;
      this.slaveReader = NonBlocking.nonBlocking(this.getName(), this.slaveInput, this.inputEncoding());
      this.slaveOutput = new LineDisciplineTerminal.FilteringOutputStream();
      this.slaveWriter = new PrintWriter(new OutputStreamWriter(this.slaveOutput, this.outputEncoding()));
      this.masterOutput = masterOutput;
      this.attributes = getDefaultTerminalAttributes();
      this.size = new Size(160, 50);
      this.parseInfoCmp();
   }

   private static Attributes getDefaultTerminalAttributes() {
      Attributes attr = new Attributes();
      attr.setLocalFlags(
         EnumSet.of(
            Attributes.LocalFlag.ICANON,
            Attributes.LocalFlag.ISIG,
            Attributes.LocalFlag.IEXTEN,
            Attributes.LocalFlag.ECHO,
            Attributes.LocalFlag.ECHOE,
            Attributes.LocalFlag.ECHOKE,
            Attributes.LocalFlag.ECHOCTL,
            Attributes.LocalFlag.PENDIN
         )
      );
      attr.setInputFlags(
         EnumSet.of(
            Attributes.InputFlag.ICRNL,
            Attributes.InputFlag.IXON,
            Attributes.InputFlag.IXANY,
            Attributes.InputFlag.IMAXBEL,
            Attributes.InputFlag.IUTF8,
            Attributes.InputFlag.BRKINT
         )
      );
      attr.setOutputFlags(EnumSet.of(Attributes.OutputFlag.OPOST, Attributes.OutputFlag.ONLCR));
      attr.setControlChar(Attributes.ControlChar.VDISCARD, ctrl('O'));
      attr.setControlChar(Attributes.ControlChar.VDSUSP, ctrl('Y'));
      attr.setControlChar(Attributes.ControlChar.VEOF, ctrl('D'));
      attr.setControlChar(Attributes.ControlChar.VERASE, ctrl('?'));
      attr.setControlChar(Attributes.ControlChar.VINTR, ctrl('C'));
      attr.setControlChar(Attributes.ControlChar.VKILL, ctrl('U'));
      attr.setControlChar(Attributes.ControlChar.VLNEXT, ctrl('V'));
      attr.setControlChar(Attributes.ControlChar.VMIN, 1);
      attr.setControlChar(Attributes.ControlChar.VQUIT, ctrl('\\'));
      attr.setControlChar(Attributes.ControlChar.VREPRINT, ctrl('R'));
      attr.setControlChar(Attributes.ControlChar.VSTART, ctrl('Q'));
      attr.setControlChar(Attributes.ControlChar.VSTATUS, ctrl('T'));
      attr.setControlChar(Attributes.ControlChar.VSTOP, ctrl('S'));
      attr.setControlChar(Attributes.ControlChar.VSUSP, ctrl('Z'));
      attr.setControlChar(Attributes.ControlChar.VTIME, 0);
      attr.setControlChar(Attributes.ControlChar.VWERASE, ctrl('W'));
      return attr;
   }

   private static int ctrl(char c) {
      return c == 63 ? 177 : c - 64;
   }

   @Override
   public NonBlockingReader reader() {
      return this.slaveReader;
   }

   @Override
   public PrintWriter writer() {
      return this.slaveWriter;
   }

   @Override
   public InputStream input() {
      return this.slaveInput;
   }

   @Override
   public OutputStream output() {
      return this.slaveOutput;
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
   public void raise(Terminal.Signal signal) {
      Objects.requireNonNull(signal);
      this.echoSignal(signal);
      super.raise(signal);
   }

   public void processInputByte(int c) throws IOException {
      boolean flushOut = this.doProcessInputByte(c);
      this.slaveInputPipe.flush();
      if (flushOut) {
         this.masterOutput.flush();
      }
   }

   public void processInputBytes(byte[] input) throws IOException {
      this.processInputBytes(input, 0, input.length);
   }

   public void processInputBytes(byte[] input, int offset, int length) throws IOException {
      boolean flushOut = false;

      for (int i = 0; i < length; i++) {
         flushOut |= this.doProcessInputByte(input[offset + i]);
      }

      this.slaveInputPipe.flush();
      if (flushOut) {
         this.masterOutput.flush();
      }
   }

   protected boolean doProcessInputByte(int c) throws IOException {
      if (this.attributes.getLocalFlag(Attributes.LocalFlag.ISIG)) {
         if (c == this.attributes.getControlChar(Attributes.ControlChar.VINTR)) {
            this.raise(Terminal.Signal.INT);
            return false;
         }

         if (c == this.attributes.getControlChar(Attributes.ControlChar.VQUIT)) {
            this.raise(Terminal.Signal.QUIT);
            return false;
         }

         if (c == this.attributes.getControlChar(Attributes.ControlChar.VSUSP)) {
            this.raise(Terminal.Signal.TSTP);
            return false;
         }

         if (c == this.attributes.getControlChar(Attributes.ControlChar.VSTATUS)) {
            this.raise(Terminal.Signal.INFO);
         }
      }

      if (this.attributes.getInputFlag(Attributes.InputFlag.INORMEOL)) {
         if (c == 13) {
            this.skipNextLf = true;
            c = 10;
         } else if (c == 10) {
            if (this.skipNextLf) {
               this.skipNextLf = false;
               return false;
            }
         } else {
            this.skipNextLf = false;
         }
      } else if (c == 13) {
         if (this.attributes.getInputFlag(Attributes.InputFlag.IGNCR)) {
            return false;
         }

         if (this.attributes.getInputFlag(Attributes.InputFlag.ICRNL)) {
            c = 10;
         }
      } else if (c == 10 && this.attributes.getInputFlag(Attributes.InputFlag.INLCR)) {
         c = 13;
      }

      boolean flushOut = false;
      if (this.attributes.getLocalFlag(Attributes.LocalFlag.ECHO)) {
         this.processOutputByte(c);
         flushOut = true;
      }

      this.slaveInputPipe.write(c);
      return flushOut;
   }

   protected void processOutputByte(int c) throws IOException {
      if (this.attributes.getOutputFlag(Attributes.OutputFlag.OPOST) && c == 10 && this.attributes.getOutputFlag(Attributes.OutputFlag.ONLCR)) {
         this.masterOutput.write(13);
         this.masterOutput.write(10);
      } else {
         this.masterOutput.write(c);
      }
   }

   protected void processIOException(IOException ioException) {
      this.slaveInput.setIoException(ioException);
   }

   @Override
   protected void doClose() throws IOException {
      super.doClose();

      try {
         this.slaveReader.close();
      } finally {
         try {
            this.slaveInputPipe.close();
         } finally {
            this.slaveWriter.close();
         }
      }
   }

   @Override
   public TerminalProvider getProvider() {
      return null;
   }

   @Override
   public SystemStream getSystemStream() {
      return null;
   }

   private class FilteringOutputStream extends OutputStream {
      private FilteringOutputStream() {
      }

      @Override
      public void write(int b) throws IOException {
         LineDisciplineTerminal.this.processOutputByte(b);
         this.flush();
      }

      @Override
      public void write(byte[] b, int off, int len) throws IOException {
         if (b == null) {
            throw new NullPointerException();
         } else if (off < 0 || off > b.length || len < 0 || off + len > b.length || off + len < 0) {
            throw new IndexOutOfBoundsException();
         } else if (len != 0) {
            for (int i = 0; i < len; i++) {
               LineDisciplineTerminal.this.processOutputByte(b[off + i]);
            }

            this.flush();
         }
      }

      @Override
      public void flush() throws IOException {
         LineDisciplineTerminal.this.masterOutput.flush();
      }

      @Override
      public void close() throws IOException {
         LineDisciplineTerminal.this.masterOutput.close();
      }
   }
}
