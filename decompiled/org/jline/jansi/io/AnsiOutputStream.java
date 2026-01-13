package org.jline.jansi.io;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import org.jline.jansi.AnsiColors;
import org.jline.jansi.AnsiMode;
import org.jline.jansi.AnsiType;

public class AnsiOutputStream extends FilterOutputStream {
   public static final byte[] RESET_CODE = "\u001b[0m".getBytes(StandardCharsets.US_ASCII);
   private static final int LOOKING_FOR_FIRST_ESC_CHAR = 0;
   private static final int LOOKING_FOR_SECOND_ESC_CHAR = 1;
   private static final int LOOKING_FOR_NEXT_ARG = 2;
   private static final int LOOKING_FOR_STR_ARG_END = 3;
   private static final int LOOKING_FOR_INT_ARG_END = 4;
   private static final int LOOKING_FOR_OSC_COMMAND = 5;
   private static final int LOOKING_FOR_OSC_COMMAND_END = 6;
   private static final int LOOKING_FOR_OSC_PARAM = 7;
   private static final int LOOKING_FOR_ST = 8;
   private static final int LOOKING_FOR_CHARSET = 9;
   private static final int FIRST_ESC_CHAR = 27;
   private static final int SECOND_ESC_CHAR = 91;
   private static final int SECOND_OSC_CHAR = 93;
   private static final int BEL = 7;
   private static final int SECOND_ST_CHAR = 92;
   private static final int SECOND_CHARSET0_CHAR = 40;
   private static final int SECOND_CHARSET1_CHAR = 41;
   private AnsiProcessor ap;
   private static final int MAX_ESCAPE_SEQUENCE_LENGTH = 100;
   private final byte[] buffer = new byte[100];
   private int pos = 0;
   private int startOfValue;
   private final ArrayList<Object> options = new ArrayList<>();
   private int state = 0;
   private final Charset cs;
   private final AnsiOutputStream.WidthSupplier width;
   private final AnsiProcessor processor;
   private final AnsiType type;
   private final AnsiColors colors;
   private final AnsiOutputStream.IoRunnable installer;
   private final AnsiOutputStream.IoRunnable uninstaller;
   private AnsiMode mode;
   private boolean resetAtUninstall;

   public AnsiOutputStream(
      OutputStream os,
      AnsiOutputStream.WidthSupplier width,
      AnsiMode mode,
      AnsiProcessor processor,
      AnsiType type,
      AnsiColors colors,
      Charset cs,
      AnsiOutputStream.IoRunnable installer,
      AnsiOutputStream.IoRunnable uninstaller,
      boolean resetAtUninstall
   ) {
      super(os);
      this.width = width;
      this.processor = processor;
      this.type = type;
      this.colors = colors;
      this.installer = installer;
      this.uninstaller = uninstaller;
      this.resetAtUninstall = resetAtUninstall;
      this.cs = cs;
      this.setMode(mode);
   }

   public int getTerminalWidth() {
      return this.width.getTerminalWidth();
   }

   public AnsiType getType() {
      return this.type;
   }

   public AnsiColors getColors() {
      return this.colors;
   }

   public AnsiMode getMode() {
      return this.mode;
   }

   public final void setMode(AnsiMode mode) {
      this.ap = (AnsiProcessor)(mode == AnsiMode.Strip
         ? new AnsiProcessor(this.out)
         : (mode != AnsiMode.Force && this.processor != null ? this.processor : new ColorsAnsiProcessor(this.out, this.colors)));
      this.mode = mode;
   }

   public boolean isResetAtUninstall() {
      return this.resetAtUninstall;
   }

   public void setResetAtUninstall(boolean resetAtUninstall) {
      this.resetAtUninstall = resetAtUninstall;
   }

   @Override
   public void write(int data) throws IOException {
      switch (this.state) {
         case 0:
            if (data == 27) {
               this.buffer[this.pos++] = (byte)data;
               this.state = 1;
            } else {
               this.out.write(data);
            }
            break;
         case 1:
            this.buffer[this.pos++] = (byte)data;
            if (data == 91) {
               this.state = 2;
            } else if (data == 93) {
               this.state = 5;
            } else if (data == 40) {
               this.options.add(0);
               this.state = 9;
            } else if (data == 41) {
               this.options.add(1);
               this.state = 9;
            } else {
               this.reset(false);
            }
            break;
         case 2:
            this.buffer[this.pos++] = (byte)data;
            if (34 == data) {
               this.startOfValue = this.pos - 1;
               this.state = 3;
            } else if (48 <= data && data <= 57) {
               this.startOfValue = this.pos - 1;
               this.state = 4;
            } else if (59 == data) {
               this.options.add(null);
            } else if (63 == data) {
               this.options.add('?');
            } else if (61 == data) {
               this.options.add('=');
            } else {
               this.processEscapeCommand(data);
            }
            break;
         case 3:
            this.buffer[this.pos++] = (byte)data;
            if (34 != data) {
               String value = new String(this.buffer, this.startOfValue, this.pos - 1 - this.startOfValue, this.cs);
               this.options.add(value);
               if (data == 59) {
                  this.state = 2;
               } else {
                  this.processEscapeCommand(data);
               }
            }
            break;
         case 4:
            this.buffer[this.pos++] = (byte)data;
            if (48 > data || data > 57) {
               String strValue = new String(this.buffer, this.startOfValue, this.pos - 1 - this.startOfValue);
               Integer value = Integer.valueOf(strValue);
               this.options.add(value);
               if (data == 59) {
                  this.state = 2;
               } else {
                  this.processEscapeCommand(data);
               }
            }
            break;
         case 5:
            this.buffer[this.pos++] = (byte)data;
            if (48 <= data && data <= 57) {
               this.startOfValue = this.pos - 1;
               this.state = 6;
            } else {
               this.reset(false);
            }
            break;
         case 6:
            this.buffer[this.pos++] = (byte)data;
            if (59 == data) {
               String strValue = new String(this.buffer, this.startOfValue, this.pos - 1 - this.startOfValue);
               Integer value = Integer.valueOf(strValue);
               this.options.add(value);
               this.startOfValue = this.pos;
               this.state = 7;
            } else if (48 > data || data > 57) {
               this.reset(false);
            }
            break;
         case 7:
            this.buffer[this.pos++] = (byte)data;
            if (7 == data) {
               String value = new String(this.buffer, this.startOfValue, this.pos - 1 - this.startOfValue, this.cs);
               this.options.add(value);
               this.processOperatingSystemCommand();
            } else if (27 == data) {
               this.state = 8;
            }
            break;
         case 8:
            this.buffer[this.pos++] = (byte)data;
            if (92 == data) {
               String value = new String(this.buffer, this.startOfValue, this.pos - 2 - this.startOfValue, this.cs);
               this.options.add(value);
               this.processOperatingSystemCommand();
            } else {
               this.state = 7;
            }
            break;
         case 9:
            this.options.add((char)data);
            this.processCharsetSelect();
      }

      if (this.pos >= this.buffer.length) {
         this.reset(false);
      }
   }

   private void processCharsetSelect() throws IOException {
      try {
         this.reset(this.ap != null && this.ap.processCharsetSelect(this.options));
      } catch (RuntimeException var2) {
         this.reset(true);
         throw var2;
      }
   }

   private void processOperatingSystemCommand() throws IOException {
      try {
         this.reset(this.ap != null && this.ap.processOperatingSystemCommand(this.options));
      } catch (RuntimeException var2) {
         this.reset(true);
         throw var2;
      }
   }

   private void processEscapeCommand(int data) throws IOException {
      try {
         this.reset(this.ap != null && this.ap.processEscapeCommand(this.options, data));
      } catch (RuntimeException var3) {
         this.reset(true);
         throw var3;
      }
   }

   private void reset(boolean skipBuffer) throws IOException {
      if (!skipBuffer) {
         this.out.write(this.buffer, 0, this.pos);
      }

      this.pos = 0;
      this.startOfValue = 0;
      this.options.clear();
      this.state = 0;
   }

   public void install() throws IOException {
      if (this.installer != null) {
         this.installer.run();
      }
   }

   public void uninstall() throws IOException {
      if (this.resetAtUninstall && this.type != AnsiType.Redirected && this.type != AnsiType.Unsupported) {
         this.setMode(AnsiMode.Default);
         this.write(RESET_CODE);
         this.flush();
      }

      if (this.uninstaller != null) {
         this.uninstaller.run();
      }
   }

   @Override
   public void close() throws IOException {
      this.uninstall();
      super.close();
   }

   @FunctionalInterface
   public interface IoRunnable {
      void run() throws IOException;
   }

   @FunctionalInterface
   public interface WidthSupplier {
      int getTerminalWidth();
   }

   public static class ZeroWidthSupplier implements AnsiOutputStream.WidthSupplier {
      @Override
      public int getTerminalWidth() {
         return 0;
      }
   }
}
