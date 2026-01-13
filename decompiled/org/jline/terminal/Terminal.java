package org.jline.terminal;

import java.io.Closeable;
import java.io.Flushable;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import org.jline.terminal.impl.NativeSignalHandler;
import org.jline.utils.ColorPalette;
import org.jline.utils.InfoCmp;
import org.jline.utils.NonBlockingReader;

public interface Terminal extends Closeable, Flushable {
   String TYPE_DUMB = "dumb";
   String TYPE_DUMB_COLOR = "dumb-color";

   String getName();

   Terminal.SignalHandler handle(Terminal.Signal var1, Terminal.SignalHandler var2);

   void raise(Terminal.Signal var1);

   NonBlockingReader reader();

   PrintWriter writer();

   Charset encoding();

   default Charset inputEncoding() {
      return this.encoding();
   }

   default Charset outputEncoding() {
      return this.encoding();
   }

   @Deprecated
   default Charset stdinEncoding() {
      return this.inputEncoding();
   }

   @Deprecated
   default Charset stdoutEncoding() {
      return this.outputEncoding();
   }

   @Deprecated
   default Charset stderrEncoding() {
      return this.outputEncoding();
   }

   InputStream input();

   OutputStream output();

   boolean canPauseResume();

   void pause();

   void pause(boolean var1) throws InterruptedException;

   void resume();

   boolean paused();

   Attributes enterRawMode();

   boolean echo();

   boolean echo(boolean var1);

   Attributes getAttributes();

   void setAttributes(Attributes var1);

   Size getSize();

   void setSize(Size var1);

   default int getWidth() {
      return this.getSize().getColumns();
   }

   default int getHeight() {
      return this.getSize().getRows();
   }

   default Size getBufferSize() {
      return this.getSize();
   }

   @Override
   void flush();

   String getType();

   boolean puts(InfoCmp.Capability var1, Object... var2);

   boolean getBooleanCapability(InfoCmp.Capability var1);

   Integer getNumericCapability(InfoCmp.Capability var1);

   String getStringCapability(InfoCmp.Capability var1);

   Cursor getCursorPosition(IntConsumer var1);

   boolean hasMouseSupport();

   boolean trackMouse(Terminal.MouseTracking var1);

   Terminal.MouseTracking getCurrentMouseTracking();

   MouseEvent readMouseEvent();

   MouseEvent readMouseEvent(IntSupplier var1);

   MouseEvent readMouseEvent(String var1);

   MouseEvent readMouseEvent(IntSupplier var1, String var2);

   boolean hasFocusSupport();

   boolean trackFocus(boolean var1);

   ColorPalette getPalette();

   default int getDefaultForegroundColor() {
      return this.getPalette().getDefaultForeground();
   }

   default int getDefaultBackgroundColor() {
      return this.getPalette().getDefaultBackground();
   }

   public static enum MouseTracking {
      Off,
      Normal,
      Button,
      Any;
   }

   public static enum Signal {
      INT,
      QUIT,
      TSTP,
      CONT,
      INFO,
      WINCH;
   }

   public interface SignalHandler {
      Terminal.SignalHandler SIG_DFL = NativeSignalHandler.SIG_DFL;
      Terminal.SignalHandler SIG_IGN = NativeSignalHandler.SIG_IGN;

      void handle(Terminal.Signal var1);
   }
}
