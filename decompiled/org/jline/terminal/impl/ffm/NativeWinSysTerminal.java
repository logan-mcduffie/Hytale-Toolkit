package org.jline.terminal.impl.ffm;

import java.io.BufferedWriter;
import java.io.IOError;
import java.io.IOException;
import java.io.Writer;
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.nio.charset.Charset;
import java.util.function.IntConsumer;
import org.jline.terminal.Cursor;
import org.jline.terminal.Size;
import org.jline.terminal.Terminal;
import org.jline.terminal.impl.AbstractWindowsTerminal;
import org.jline.terminal.spi.SystemStream;
import org.jline.terminal.spi.TerminalProvider;
import org.jline.utils.OSUtils;

public class NativeWinSysTerminal extends AbstractWindowsTerminal<MemorySegment> {
   private final char[] focus = new char[]{'\u001b', '[', ' '};
   private final char[] mouse = new char[]{'\u001b', '[', 'M', ' ', ' ', ' '};

   public static NativeWinSysTerminal createTerminal(
      TerminalProvider provider,
      SystemStream systemStream,
      String name,
      String type,
      boolean ansiPassThrough,
      Charset encoding,
      boolean nativeSignals,
      Terminal.SignalHandler signalHandler,
      boolean paused
   ) throws IOException {
      return createTerminal(provider, systemStream, name, type, ansiPassThrough, encoding, encoding, encoding, nativeSignals, signalHandler, paused);
   }

   public static NativeWinSysTerminal createTerminal(
      TerminalProvider provider,
      SystemStream systemStream,
      String name,
      String type,
      boolean ansiPassThrough,
      Charset encoding,
      Charset inputEncoding,
      Charset outputEncoding,
      boolean nativeSignals,
      Terminal.SignalHandler signalHandler,
      boolean paused
   ) throws IOException {
      NativeWinSysTerminal var18;
      try (Arena arena = Arena.ofConfined()) {
         MemorySegment consoleIn = Kernel32.GetStdHandle(-10);
         MemorySegment inMode = allocateInt(arena);
         if (Kernel32.GetConsoleMode(consoleIn, inMode) == 0) {
            throw new IOException("Failed to get console mode: " + Kernel32.getLastErrorMessage());
         }
         MemorySegment console = switch (systemStream) {
            case Output -> Kernel32.GetStdHandle(-11);
            case Error -> Kernel32.GetStdHandle(-12);
            default -> throw new IllegalArgumentException("Unsupported stream for console: " + systemStream);
         };
         MemorySegment outMode = allocateInt(arena);
         if (Kernel32.GetConsoleMode(console, outMode) == 0) {
            throw new IOException("Failed to get console mode: " + Kernel32.getLastErrorMessage());
         }

         Writer writer;
         if (ansiPassThrough) {
            type = type != null ? type : (OSUtils.IS_CONEMU ? "windows-conemu" : "windows");
            writer = new NativeWinConsoleWriter();
         } else {
            int m = outMode.get(ValueLayout.JAVA_INT, 0L);
            if (enableVtp(console, m)) {
               type = type != null ? type : "windows-vtp";
               writer = new NativeWinConsoleWriter();
            } else if (OSUtils.IS_CONEMU) {
               type = type != null ? type : "windows-conemu";
               writer = new NativeWinConsoleWriter();
            } else {
               type = type != null ? type : "windows";
               writer = new WindowsAnsiWriter(new BufferedWriter(new NativeWinConsoleWriter()));
            }
         }

         NativeWinSysTerminal terminal = new NativeWinSysTerminal(
            provider,
            systemStream,
            writer,
            name,
            type,
            encoding,
            inputEncoding,
            outputEncoding,
            nativeSignals,
            signalHandler,
            consoleIn,
            inMode.get(ValueLayout.JAVA_INT, 0L),
            console,
            outMode.get(ValueLayout.JAVA_INT, 0L)
         );
         if (!paused) {
            terminal.resume();
         }

         var18 = terminal;
      }

      return var18;
   }

   private static boolean enableVtp(MemorySegment console, int m) {
      return Kernel32.SetConsoleMode(console, m | 4) != 0;
   }

   public static boolean isWindowsSystemStream(SystemStream stream) {
      try (Arena arena = Arena.ofConfined()) {
         MemorySegment mode = allocateInt(arena);
         MemorySegment console;
         switch (stream) {
            case Output:
               console = Kernel32.GetStdHandle(-11);
               break;
            case Error:
               console = Kernel32.GetStdHandle(-12);
               break;
            case Input:
               console = Kernel32.GetStdHandle(-10);
               break;
            default:
               return false;
         }

         return Kernel32.GetConsoleMode(console, mode) != 0;
      }
   }

   private static MemorySegment allocateInt(Arena arena) {
      return arena.allocate(ValueLayout.JAVA_INT);
   }

   NativeWinSysTerminal(
      TerminalProvider provider,
      SystemStream systemStream,
      Writer writer,
      String name,
      String type,
      Charset encoding,
      boolean nativeSignals,
      Terminal.SignalHandler signalHandler,
      MemorySegment inConsole,
      int inConsoleMode,
      MemorySegment outConsole,
      int outConsoleMode
   ) throws IOException {
      this(
         provider,
         systemStream,
         writer,
         name,
         type,
         encoding,
         encoding,
         encoding,
         nativeSignals,
         signalHandler,
         inConsole,
         inConsoleMode,
         outConsole,
         outConsoleMode
      );
   }

   NativeWinSysTerminal(
      TerminalProvider provider,
      SystemStream systemStream,
      Writer writer,
      String name,
      String type,
      Charset encoding,
      Charset inputEncoding,
      Charset outputEncoding,
      boolean nativeSignals,
      Terminal.SignalHandler signalHandler,
      MemorySegment inConsole,
      int inConsoleMode,
      MemorySegment outConsole,
      int outConsoleMode
   ) throws IOException {
      super(
         provider,
         systemStream,
         writer,
         name,
         type,
         encoding,
         inputEncoding,
         outputEncoding,
         nativeSignals,
         signalHandler,
         inConsole,
         inConsoleMode,
         outConsole,
         outConsoleMode
      );
   }

   protected int getConsoleMode(MemorySegment console) {
      int var4;
      try (Arena arena = Arena.ofConfined()) {
         MemorySegment mode = arena.allocate(ValueLayout.JAVA_INT);
         if (Kernel32.GetConsoleMode(console, mode) == 0) {
            return -1;
         }

         var4 = mode.get(ValueLayout.JAVA_INT, 0L);
      }

      return var4;
   }

   protected void setConsoleMode(MemorySegment console, int mode) {
      Kernel32.SetConsoleMode(console, mode);
   }

   @Override
   public Size getSize() {
      Size var3;
      try (Arena arena = Arena.ofConfined()) {
         Kernel32.CONSOLE_SCREEN_BUFFER_INFO info = new Kernel32.CONSOLE_SCREEN_BUFFER_INFO(arena);
         Kernel32.GetConsoleScreenBufferInfo(this.outConsole, info);
         var3 = new Size(info.windowWidth(), info.windowHeight());
      }

      return var3;
   }

   @Override
   public Size getBufferSize() {
      Size var3;
      try (Arena arena = Arena.ofConfined()) {
         Kernel32.CONSOLE_SCREEN_BUFFER_INFO info = new Kernel32.CONSOLE_SCREEN_BUFFER_INFO(arena);
         Kernel32.GetConsoleScreenBufferInfo(this.outConsole, info);
         var3 = new Size(info.size().x(), info.size().y());
      }

      return var3;
   }

   @Override
   protected boolean processConsoleInput() throws IOException {
      boolean flush;
      try (Arena arena = Arena.ofConfined()) {
         if (this.inConsole != null && this.inConsole.address() != -1L && Kernel32.WaitForSingleObject(this.inConsole, 100) == 0) {
            Kernel32.INPUT_RECORD[] events = Kernel32.readConsoleInputHelper(arena, this.inConsole, 1, false);
            flush = false;

            for (Kernel32.INPUT_RECORD event : events) {
               int eventType = event.eventType();
               if (eventType == 1) {
                  Kernel32.KEY_EVENT_RECORD keyEvent = event.keyEvent();
                  this.processKeyEvent(keyEvent.keyDown(), keyEvent.keyCode(), keyEvent.uchar(), keyEvent.controlKeyState());
                  flush = true;
               } else if (eventType == 4) {
                  this.raise(Terminal.Signal.WINCH);
               } else if (eventType == 2) {
                  this.processMouseEvent(event.mouseEvent());
                  flush = true;
               } else if (eventType == 16) {
                  this.processFocusEvent(event.focusEvent().setFocus());
               }
            }

            return flush;
         }

         flush = false;
      }

      return flush;
   }

   private void processFocusEvent(boolean hasFocus) throws IOException {
      if (this.focusTracking) {
         this.focus[2] = (char)(hasFocus ? 73 : 79);
         this.slaveInputPipe.write(this.focus);
      }
   }

   private void processMouseEvent(Kernel32.MOUSE_EVENT_RECORD mouseEvent) throws IOException {
      int dwEventFlags = mouseEvent.eventFlags();
      int dwButtonState = mouseEvent.buttonState();
      if (this.tracking != Terminal.MouseTracking.Off
         && (this.tracking != Terminal.MouseTracking.Normal || dwEventFlags != 1)
         && (this.tracking != Terminal.MouseTracking.Button || dwEventFlags != 1 || dwButtonState != 0)) {
         int cb = 0;
         dwEventFlags &= -3;
         if (dwEventFlags == 4) {
            cb |= 64;
            if (dwButtonState >> 16 < 0) {
               cb |= 1;
            }
         } else {
            if (dwEventFlags == 8) {
               return;
            }

            if ((dwButtonState & 1) != 0) {
               cb |= 0;
            } else if ((dwButtonState & 2) != 0) {
               cb |= 1;
            } else if ((dwButtonState & 4) != 0) {
               cb |= 2;
            } else {
               cb |= 3;
            }
         }

         int cx = mouseEvent.mousePosition().x();
         int cy = mouseEvent.mousePosition().y();
         this.mouse[3] = (char)(32 + cb);
         this.mouse[4] = (char)(32 + cx + 1);
         this.mouse[5] = (char)(32 + cy + 1);
         this.slaveInputPipe.write(this.mouse);
      }
   }

   @Override
   public Cursor getCursorPosition(IntConsumer discarded) {
      Cursor var4;
      try (Arena arena = Arena.ofConfined()) {
         Kernel32.CONSOLE_SCREEN_BUFFER_INFO info = new Kernel32.CONSOLE_SCREEN_BUFFER_INFO(arena);
         if (Kernel32.GetConsoleScreenBufferInfo(this.outConsole, info) == 0) {
            throw new IOError(new IOException("Could not get the cursor position: " + Kernel32.getLastErrorMessage()));
         }

         var4 = new Cursor(info.cursorPosition().x(), info.cursorPosition().y());
      }

      return var4;
   }

   @Override
   public int getDefaultForegroundColor() {
      int var3;
      try (Arena arena = Arena.ofConfined()) {
         Kernel32.CONSOLE_SCREEN_BUFFER_INFO info = new Kernel32.CONSOLE_SCREEN_BUFFER_INFO(arena);
         if (Kernel32.GetConsoleScreenBufferInfo(this.outConsole, info) == 0) {
            return -1;
         }

         var3 = this.convertAttributeToRgb(info.attributes() & 15, true);
      }

      return var3;
   }

   @Override
   public int getDefaultBackgroundColor() {
      int var3;
      try (Arena arena = Arena.ofConfined()) {
         Kernel32.CONSOLE_SCREEN_BUFFER_INFO info = new Kernel32.CONSOLE_SCREEN_BUFFER_INFO(arena);
         if (Kernel32.GetConsoleScreenBufferInfo(this.outConsole, info) == 0) {
            return -1;
         }

         var3 = this.convertAttributeToRgb((info.attributes() & 240) >> 4, false);
      }

      return var3;
   }
}
