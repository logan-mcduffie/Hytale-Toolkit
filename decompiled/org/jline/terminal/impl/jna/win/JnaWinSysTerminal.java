package org.jline.terminal.impl.jna.win;

import com.sun.jna.LastErrorException;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.function.IntConsumer;
import org.jline.terminal.Cursor;
import org.jline.terminal.Size;
import org.jline.terminal.Terminal;
import org.jline.terminal.impl.AbstractWindowsTerminal;
import org.jline.terminal.spi.SystemStream;
import org.jline.terminal.spi.TerminalProvider;
import org.jline.utils.InfoCmp;
import org.jline.utils.OSUtils;

public class JnaWinSysTerminal extends AbstractWindowsTerminal<Pointer> {
   private static final Pointer consoleIn = Kernel32.INSTANCE.GetStdHandle(-10);
   private static final Pointer consoleOut = Kernel32.INSTANCE.GetStdHandle(-11);
   private static final Pointer consoleErr = Kernel32.INSTANCE.GetStdHandle(-12);
   private char[] focus = new char[]{'\u001b', '[', ' '};
   private char[] mouse = new char[]{'\u001b', '[', 'M', ' ', ' ', ' '};
   private final Kernel32.INPUT_RECORD[] inputEvents = new Kernel32.INPUT_RECORD[1];
   private final IntByReference eventsRead = new IntByReference();

   public static JnaWinSysTerminal createTerminal(
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
      return createTerminal(provider, systemStream, name, type, ansiPassThrough, encoding, encoding, encoding, encoding, nativeSignals, signalHandler, paused);
   }

   public static JnaWinSysTerminal createTerminal(
      TerminalProvider provider,
      SystemStream systemStream,
      String name,
      String type,
      boolean ansiPassThrough,
      Charset encoding,
      Charset stdinEncoding,
      Charset stdoutEncoding,
      Charset stderrEncoding,
      boolean nativeSignals,
      Terminal.SignalHandler signalHandler,
      boolean paused
   ) throws IOException {
      IntByReference inMode = new IntByReference();
      Kernel32.INSTANCE.GetConsoleMode(consoleIn, inMode);
      Pointer console;
      switch (systemStream) {
         case Output:
            console = consoleOut;
            break;
         case Error:
            console = consoleErr;
            break;
         default:
            throw new IllegalArgumentException("Unsupported stream for console: " + systemStream);
      }

      IntByReference outMode = new IntByReference();
      Kernel32.INSTANCE.GetConsoleMode(console, outMode);
      Writer writer;
      if (ansiPassThrough) {
         type = type != null ? type : (OSUtils.IS_CONEMU ? "windows-conemu" : "windows");
         writer = new JnaWinConsoleWriter(console);
      } else if (enableVtp(console, outMode.getValue())) {
         type = type != null ? type : "windows-vtp";
         writer = new JnaWinConsoleWriter(console);
      } else if (OSUtils.IS_CONEMU) {
         type = type != null ? type : "windows-conemu";
         writer = new JnaWinConsoleWriter(console);
      } else {
         type = type != null ? type : "windows";
         writer = new WindowsAnsiWriter(new BufferedWriter(new JnaWinConsoleWriter(console)), console);
      }

      Charset outputEncoding = systemStream == SystemStream.Error ? stderrEncoding : stdoutEncoding;
      JnaWinSysTerminal terminal = new JnaWinSysTerminal(
         provider,
         systemStream,
         writer,
         name,
         type,
         encoding,
         stdinEncoding,
         outputEncoding,
         nativeSignals,
         signalHandler,
         consoleIn,
         inMode.getValue(),
         console,
         outMode.getValue()
      );
      if (!paused) {
         terminal.resume();
      }

      return terminal;
   }

   private static boolean enableVtp(Pointer console, int outMode) {
      try {
         Kernel32.INSTANCE.SetConsoleMode(console, outMode | 4);
         return true;
      } catch (LastErrorException var3) {
         return false;
      }
   }

   public static boolean isWindowsSystemStream(SystemStream stream) {
      try {
         IntByReference mode = new IntByReference();
         Pointer console;
         switch (stream) {
            case Output:
               console = consoleOut;
               break;
            case Error:
               console = consoleErr;
               break;
            case Input:
               console = consoleIn;
               break;
            default:
               return false;
         }

         Kernel32.INSTANCE.GetConsoleMode(console, mode);
         return true;
      } catch (LastErrorException var3) {
         return false;
      }
   }

   JnaWinSysTerminal(
      TerminalProvider provider,
      SystemStream systemStream,
      Writer writer,
      String name,
      String type,
      Charset encoding,
      boolean nativeSignals,
      Terminal.SignalHandler signalHandler,
      Pointer inConsole,
      int inConsoleMode,
      Pointer outConsole,
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

   JnaWinSysTerminal(
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
      Pointer inConsole,
      int inConsoleMode,
      Pointer outConsole,
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
      this.strings.put(InfoCmp.Capability.key_mouse, "\\E[M");
   }

   protected int getConsoleMode(Pointer console) {
      IntByReference mode = new IntByReference();
      Kernel32.INSTANCE.GetConsoleMode(console, mode);
      return mode.getValue();
   }

   protected void setConsoleMode(Pointer console, int mode) {
      Kernel32.INSTANCE.SetConsoleMode(console, mode);
   }

   @Override
   public Size getSize() {
      Kernel32.CONSOLE_SCREEN_BUFFER_INFO info = new Kernel32.CONSOLE_SCREEN_BUFFER_INFO();
      Kernel32.INSTANCE.GetConsoleScreenBufferInfo(this.outConsole, info);
      return new Size(info.windowWidth(), info.windowHeight());
   }

   @Override
   public Size getBufferSize() {
      Kernel32.CONSOLE_SCREEN_BUFFER_INFO info = new Kernel32.CONSOLE_SCREEN_BUFFER_INFO();
      Kernel32.INSTANCE.GetConsoleScreenBufferInfo(consoleOut, info);
      return new Size(info.dwSize.X, info.dwSize.Y);
   }

   @Override
   protected boolean processConsoleInput() throws IOException {
      Kernel32.INPUT_RECORD event = this.readConsoleInput(100);
      if (event == null) {
         return false;
      } else {
         switch (event.EventType) {
            case 1:
               this.processKeyEvent(event.Event.KeyEvent);
               return true;
            case 2:
               this.processMouseEvent(event.Event.MouseEvent);
               return true;
            case 4:
               this.raise(Terminal.Signal.WINCH);
               return false;
            case 16:
               this.processFocusEvent(event.Event.FocusEvent.bSetFocus);
               return true;
            default:
               return false;
         }
      }
   }

   private void processKeyEvent(Kernel32.KEY_EVENT_RECORD keyEvent) throws IOException {
      this.processKeyEvent(keyEvent.bKeyDown, keyEvent.wVirtualKeyCode, keyEvent.uChar.UnicodeChar, keyEvent.dwControlKeyState);
   }

   private void processFocusEvent(boolean hasFocus) throws IOException {
      if (this.focusTracking) {
         this.focus[2] = (char)(hasFocus ? 73 : 79);
         this.slaveInputPipe.write(this.focus);
      }
   }

   private void processMouseEvent(Kernel32.MOUSE_EVENT_RECORD mouseEvent) throws IOException {
      int dwEventFlags = mouseEvent.dwEventFlags;
      int dwButtonState = mouseEvent.dwButtonState;
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

         int cx = mouseEvent.dwMousePosition.X;
         int cy = mouseEvent.dwMousePosition.Y;
         this.mouse[3] = (char)(32 + cb);
         this.mouse[4] = (char)(32 + cx + 1);
         this.mouse[5] = (char)(32 + cy + 1);
         this.slaveInputPipe.write(this.mouse);
      }
   }

   private Kernel32.INPUT_RECORD readConsoleInput(int dwMilliseconds) throws IOException {
      if (Kernel32.INSTANCE.WaitForSingleObject(consoleIn, dwMilliseconds) != 0) {
         return null;
      } else {
         Kernel32.INSTANCE.ReadConsoleInput(consoleIn, this.inputEvents, 1, this.eventsRead);
         return this.eventsRead.getValue() == 1 ? this.inputEvents[0] : null;
      }
   }

   @Override
   public Cursor getCursorPosition(IntConsumer discarded) {
      Kernel32.CONSOLE_SCREEN_BUFFER_INFO info = new Kernel32.CONSOLE_SCREEN_BUFFER_INFO();
      Kernel32.INSTANCE.GetConsoleScreenBufferInfo(consoleOut, info);
      return new Cursor(info.dwCursorPosition.X, info.dwCursorPosition.Y);
   }

   @Override
   public int getDefaultForegroundColor() {
      Kernel32.CONSOLE_SCREEN_BUFFER_INFO info = new Kernel32.CONSOLE_SCREEN_BUFFER_INFO();
      Kernel32.INSTANCE.GetConsoleScreenBufferInfo(consoleOut, info);
      return this.convertAttributeToRgb(info.wAttributes & 15, true);
   }

   @Override
   public int getDefaultBackgroundColor() {
      Kernel32.CONSOLE_SCREEN_BUFFER_INFO info = new Kernel32.CONSOLE_SCREEN_BUFFER_INFO();
      Kernel32.INSTANCE.GetConsoleScreenBufferInfo(consoleOut, info);
      return this.convertAttributeToRgb((info.wAttributes & 240) >> 4, false);
   }
}
