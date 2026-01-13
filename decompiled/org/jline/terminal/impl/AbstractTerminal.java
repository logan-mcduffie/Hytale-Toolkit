package org.jline.terminal.impl;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.nio.charset.Charset;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import org.jline.terminal.Attributes;
import org.jline.terminal.Cursor;
import org.jline.terminal.MouseEvent;
import org.jline.terminal.Terminal;
import org.jline.terminal.spi.TerminalExt;
import org.jline.utils.ColorPalette;
import org.jline.utils.Curses;
import org.jline.utils.InfoCmp;
import org.jline.utils.Log;
import org.jline.utils.Status;

public abstract class AbstractTerminal implements TerminalExt {
   protected final String name;
   protected final String type;
   protected final Charset encoding;
   protected final Charset inputEncoding;
   protected final Charset outputEncoding;
   protected final Map<Terminal.Signal, Terminal.SignalHandler> handlers = new ConcurrentHashMap<>();
   protected final Set<InfoCmp.Capability> bools = new HashSet<>();
   protected final Map<InfoCmp.Capability, Integer> ints = new HashMap<>();
   protected final Map<InfoCmp.Capability, String> strings = new HashMap<>();
   protected final ColorPalette palette;
   protected Status status;
   protected Runnable onClose;
   protected Terminal.MouseTracking currentMouseTracking = Terminal.MouseTracking.Off;
   private MouseEvent lastMouseEvent = new MouseEvent(MouseEvent.Type.Moved, MouseEvent.Button.NoButton, EnumSet.noneOf(MouseEvent.Modifier.class), 0, 0);

   public AbstractTerminal(String name, String type) throws IOException {
      this(name, type, null, Terminal.SignalHandler.SIG_DFL);
   }

   public AbstractTerminal(String name, String type, Charset encoding, Terminal.SignalHandler signalHandler) throws IOException {
      this(name, type, encoding, encoding, encoding, signalHandler);
   }

   public AbstractTerminal(String name, String type, Charset encoding, Charset inputEncoding, Charset outputEncoding, Terminal.SignalHandler signalHandler) throws IOException {
      this.name = name;
      this.type = type != null ? type : "ansi";
      this.encoding = encoding != null ? encoding : Charset.defaultCharset();
      this.inputEncoding = inputEncoding != null ? inputEncoding : this.encoding;
      this.outputEncoding = outputEncoding != null ? outputEncoding : this.encoding;
      this.palette = new ColorPalette(this);

      for (Terminal.Signal signal : Terminal.Signal.values()) {
         this.handlers.put(signal, signalHandler);
      }
   }

   public void setOnClose(Runnable onClose) {
      this.onClose = onClose;
   }

   public Status getStatus() {
      return this.getStatus(true);
   }

   public Status getStatus(boolean create) {
      if (this.status == null && create) {
         this.status = new Status(this);
      }

      return this.status;
   }

   @Override
   public Terminal.SignalHandler handle(Terminal.Signal signal, Terminal.SignalHandler handler) {
      Objects.requireNonNull(signal);
      Objects.requireNonNull(handler);
      return this.handlers.put(signal, handler);
   }

   @Override
   public void raise(Terminal.Signal signal) {
      Objects.requireNonNull(signal);
      Terminal.SignalHandler handler = this.handlers.get(signal);
      if (handler == Terminal.SignalHandler.SIG_DFL) {
         if (this.status != null && signal == Terminal.Signal.WINCH) {
            this.status.resize();
         }
      } else if (handler != Terminal.SignalHandler.SIG_IGN) {
         handler.handle(signal);
      }
   }

   @Override
   public final void close() throws IOException {
      try {
         this.doClose();
      } finally {
         if (this.onClose != null) {
            this.onClose.run();
         }
      }
   }

   protected void doClose() throws IOException {
      if (this.status != null) {
         this.status.close();
      }
   }

   protected void echoSignal(Terminal.Signal signal) {
      Attributes.ControlChar cc = null;
      switch (signal) {
         case INT:
            cc = Attributes.ControlChar.VINTR;
            break;
         case QUIT:
            cc = Attributes.ControlChar.VQUIT;
            break;
         case TSTP:
            cc = Attributes.ControlChar.VSUSP;
      }

      if (cc != null) {
         int vcc = this.getAttributes().getControlChar(cc);
         if (vcc > 0 && vcc < 32) {
            this.writer().write(new char[]{'^', (char)(vcc + 64)}, 0, 2);
         }
      }
   }

   @Override
   public Attributes enterRawMode() {
      Attributes prvAttr = this.getAttributes();
      Attributes newAttr = new Attributes(prvAttr);
      newAttr.setLocalFlags(EnumSet.of(Attributes.LocalFlag.ICANON, Attributes.LocalFlag.ECHO, Attributes.LocalFlag.IEXTEN), false);
      newAttr.setInputFlags(EnumSet.of(Attributes.InputFlag.IXON, Attributes.InputFlag.ICRNL, Attributes.InputFlag.INLCR), false);
      newAttr.setControlChar(Attributes.ControlChar.VMIN, 0);
      newAttr.setControlChar(Attributes.ControlChar.VTIME, 1);
      this.setAttributes(newAttr);
      return prvAttr;
   }

   @Override
   public boolean echo() {
      return this.getAttributes().getLocalFlag(Attributes.LocalFlag.ECHO);
   }

   @Override
   public boolean echo(boolean echo) {
      Attributes attr = this.getAttributes();
      boolean prev = attr.getLocalFlag(Attributes.LocalFlag.ECHO);
      if (prev != echo) {
         attr.setLocalFlag(Attributes.LocalFlag.ECHO, echo);
         this.setAttributes(attr);
      }

      return prev;
   }

   @Override
   public String getName() {
      return this.name;
   }

   @Override
   public String getType() {
      return this.type;
   }

   public String getKind() {
      return this.getClass().getSimpleName();
   }

   @Override
   public Charset encoding() {
      return this.encoding;
   }

   @Override
   public Charset inputEncoding() {
      return this.inputEncoding;
   }

   @Override
   public Charset outputEncoding() {
      return this.outputEncoding;
   }

   @Override
   public void flush() {
      this.writer().flush();
   }

   @Override
   public boolean puts(InfoCmp.Capability capability, Object... params) {
      String str = this.getStringCapability(capability);
      if (str == null) {
         return false;
      } else {
         Curses.tputs(this.writer(), str, params);
         return true;
      }
   }

   @Override
   public boolean getBooleanCapability(InfoCmp.Capability capability) {
      return this.bools.contains(capability);
   }

   @Override
   public Integer getNumericCapability(InfoCmp.Capability capability) {
      return this.ints.get(capability);
   }

   @Override
   public String getStringCapability(InfoCmp.Capability capability) {
      return this.strings.get(capability);
   }

   protected void parseInfoCmp() {
      String capabilities = null;

      try {
         capabilities = InfoCmp.getInfoCmp(this.type);
      } catch (Exception var3) {
         Log.warn("Unable to retrieve infocmp for type " + this.type, var3);
      }

      if (capabilities == null) {
         capabilities = InfoCmp.getDefaultInfoCmp("ansi");
      }

      InfoCmp.parseInfoCmp(capabilities, this.bools, this.ints, this.strings);
   }

   @Override
   public Cursor getCursorPosition(IntConsumer discarded) {
      return null;
   }

   @Override
   public boolean hasMouseSupport() {
      return MouseSupport.hasMouseSupport(this);
   }

   @Override
   public Terminal.MouseTracking getCurrentMouseTracking() {
      return this.currentMouseTracking;
   }

   @Override
   public boolean trackMouse(Terminal.MouseTracking tracking) {
      if (MouseSupport.trackMouse(this, tracking)) {
         this.currentMouseTracking = tracking;
         return true;
      } else {
         return false;
      }
   }

   @Override
   public MouseEvent readMouseEvent() {
      return this.readMouseEvent(this.getStringCapability(InfoCmp.Capability.key_mouse));
   }

   @Override
   public MouseEvent readMouseEvent(IntSupplier reader) {
      return this.readMouseEvent(reader, this.getStringCapability(InfoCmp.Capability.key_mouse));
   }

   @Override
   public MouseEvent readMouseEvent(String prefix) {
      return this.lastMouseEvent = MouseSupport.readMouse(this, this.lastMouseEvent, prefix);
   }

   @Override
   public MouseEvent readMouseEvent(IntSupplier reader, String prefix) {
      return this.lastMouseEvent = MouseSupport.readMouse(reader, this.lastMouseEvent, prefix);
   }

   @Override
   public boolean hasFocusSupport() {
      return this.type.startsWith("xterm");
   }

   @Override
   public boolean trackFocus(boolean tracking) {
      if (this.hasFocusSupport()) {
         this.writer().write(tracking ? "\u001b[?1004h" : "\u001b[?1004l");
         this.writer().flush();
         return true;
      } else {
         return false;
      }
   }

   protected void checkInterrupted() throws InterruptedIOException {
      if (Thread.interrupted()) {
         throw new InterruptedIOException();
      }
   }

   @Override
   public boolean canPauseResume() {
      return false;
   }

   @Override
   public void pause() {
   }

   @Override
   public void pause(boolean wait) throws InterruptedException {
   }

   @Override
   public void resume() {
   }

   @Override
   public boolean paused() {
      return false;
   }

   @Override
   public ColorPalette getPalette() {
      return this.palette;
   }

   @Override
   public String toString() {
      return this.getKind() + "[name='" + this.name + '\'' + ", type='" + this.type + '\'' + ", size='" + this.getSize() + '\'' + ']';
   }

   @Override
   public int getDefaultForegroundColor() {
      return -1;
   }

   @Override
   public int getDefaultBackgroundColor() {
      return -1;
   }
}
