package org.fusesource.jansi.io;

import java.io.IOException;
import java.io.OutputStream;
import org.fusesource.jansi.internal.Kernel32;

public final class WindowsAnsiProcessor extends AnsiProcessor {
   private final long console;
   private static final short FOREGROUND_BLACK = 0;
   private static final short FOREGROUND_YELLOW = (short)(Kernel32.FOREGROUND_RED | Kernel32.FOREGROUND_GREEN);
   private static final short FOREGROUND_MAGENTA = (short)(Kernel32.FOREGROUND_BLUE | Kernel32.FOREGROUND_RED);
   private static final short FOREGROUND_CYAN = (short)(Kernel32.FOREGROUND_BLUE | Kernel32.FOREGROUND_GREEN);
   private static final short FOREGROUND_WHITE = (short)(Kernel32.FOREGROUND_RED | Kernel32.FOREGROUND_GREEN | Kernel32.FOREGROUND_BLUE);
   private static final short BACKGROUND_BLACK = 0;
   private static final short BACKGROUND_YELLOW = (short)(Kernel32.BACKGROUND_RED | Kernel32.BACKGROUND_GREEN);
   private static final short BACKGROUND_MAGENTA = (short)(Kernel32.BACKGROUND_BLUE | Kernel32.BACKGROUND_RED);
   private static final short BACKGROUND_CYAN = (short)(Kernel32.BACKGROUND_BLUE | Kernel32.BACKGROUND_GREEN);
   private static final short BACKGROUND_WHITE = (short)(Kernel32.BACKGROUND_RED | Kernel32.BACKGROUND_GREEN | Kernel32.BACKGROUND_BLUE);
   private static final short[] ANSI_FOREGROUND_COLOR_MAP = new short[]{
      0, Kernel32.FOREGROUND_RED, Kernel32.FOREGROUND_GREEN, FOREGROUND_YELLOW, Kernel32.FOREGROUND_BLUE, FOREGROUND_MAGENTA, FOREGROUND_CYAN, FOREGROUND_WHITE
   };
   private static final short[] ANSI_BACKGROUND_COLOR_MAP = new short[]{
      0, Kernel32.BACKGROUND_RED, Kernel32.BACKGROUND_GREEN, BACKGROUND_YELLOW, Kernel32.BACKGROUND_BLUE, BACKGROUND_MAGENTA, BACKGROUND_CYAN, BACKGROUND_WHITE
   };
   private final Kernel32.CONSOLE_SCREEN_BUFFER_INFO info = new Kernel32.CONSOLE_SCREEN_BUFFER_INFO();
   private final short originalColors;
   private boolean negative;
   private short savedX = -1;
   private short savedY = -1;

   public WindowsAnsiProcessor(OutputStream ps, long console) throws IOException {
      super(ps);
      this.console = console;
      this.getConsoleInfo();
      this.originalColors = this.info.attributes;
   }

   public WindowsAnsiProcessor(OutputStream ps, boolean stdout) throws IOException {
      this(ps, Kernel32.GetStdHandle(stdout ? Kernel32.STD_OUTPUT_HANDLE : Kernel32.STD_ERROR_HANDLE));
   }

   public WindowsAnsiProcessor(OutputStream ps) throws IOException {
      this(ps, true);
   }

   private void getConsoleInfo() throws IOException {
      this.os.flush();
      if (Kernel32.GetConsoleScreenBufferInfo(this.console, this.info) == 0) {
         throw new IOException("Could not get the screen info: " + Kernel32.getLastErrorMessage());
      } else {
         if (this.negative) {
            this.info.attributes = this.invertAttributeColors(this.info.attributes);
         }
      }
   }

   private void applyAttribute() throws IOException {
      this.os.flush();
      short attributes = this.info.attributes;
      if (this.negative) {
         attributes = this.invertAttributeColors(attributes);
      }

      if (Kernel32.SetConsoleTextAttribute(this.console, attributes) == 0) {
         throw new IOException(Kernel32.getLastErrorMessage());
      }
   }

   private short invertAttributeColors(short attributes) {
      int fg = 15 & attributes;
      fg <<= 4;
      int bg = 240 & attributes;
      bg >>= 4;
      return (short)(attributes & '\uff00' | fg | bg);
   }

   private void applyCursorPosition() throws IOException {
      if (Kernel32.SetConsoleCursorPosition(this.console, this.info.cursorPosition.copy()) == 0) {
         throw new IOException(Kernel32.getLastErrorMessage());
      }
   }

   @Override
   protected void processEraseScreen(int eraseOption) throws IOException {
      this.getConsoleInfo();
      int[] written = new int[1];
      switch (eraseOption) {
         case 0:
            int lengthToEnd = (this.info.window.bottom - this.info.cursorPosition.y) * this.info.size.x + (this.info.size.x - this.info.cursorPosition.x);
            Kernel32.FillConsoleOutputAttribute(this.console, this.info.attributes, lengthToEnd, this.info.cursorPosition.copy(), written);
            Kernel32.FillConsoleOutputCharacterW(this.console, ' ', lengthToEnd, this.info.cursorPosition.copy(), written);
            break;
         case 1:
            Kernel32.COORD topLeft2 = new Kernel32.COORD();
            topLeft2.x = 0;
            topLeft2.y = this.info.window.top;
            int lengthToCursor = (this.info.cursorPosition.y - this.info.window.top) * this.info.size.x + this.info.cursorPosition.x;
            Kernel32.FillConsoleOutputAttribute(this.console, this.info.attributes, lengthToCursor, topLeft2, written);
            Kernel32.FillConsoleOutputCharacterW(this.console, ' ', lengthToCursor, topLeft2, written);
            break;
         case 2:
            Kernel32.COORD topLeft = new Kernel32.COORD();
            topLeft.x = 0;
            topLeft.y = this.info.window.top;
            int screenLength = this.info.window.height() * this.info.size.x;
            Kernel32.FillConsoleOutputAttribute(this.console, this.info.attributes, screenLength, topLeft, written);
            Kernel32.FillConsoleOutputCharacterW(this.console, ' ', screenLength, topLeft, written);
      }
   }

   @Override
   protected void processEraseLine(int eraseOption) throws IOException {
      this.getConsoleInfo();
      int[] written = new int[1];
      switch (eraseOption) {
         case 0:
            int lengthToLastCol = this.info.size.x - this.info.cursorPosition.x;
            Kernel32.FillConsoleOutputAttribute(this.console, this.info.attributes, lengthToLastCol, this.info.cursorPosition.copy(), written);
            Kernel32.FillConsoleOutputCharacterW(this.console, ' ', lengthToLastCol, this.info.cursorPosition.copy(), written);
            break;
         case 1:
            Kernel32.COORD leftColCurrRow2 = this.info.cursorPosition.copy();
            leftColCurrRow2.x = 0;
            Kernel32.FillConsoleOutputAttribute(this.console, this.info.attributes, this.info.cursorPosition.x, leftColCurrRow2, written);
            Kernel32.FillConsoleOutputCharacterW(this.console, ' ', this.info.cursorPosition.x, leftColCurrRow2, written);
            break;
         case 2:
            Kernel32.COORD leftColCurrRow = this.info.cursorPosition.copy();
            leftColCurrRow.x = 0;
            Kernel32.FillConsoleOutputAttribute(this.console, this.info.attributes, this.info.size.x, leftColCurrRow, written);
            Kernel32.FillConsoleOutputCharacterW(this.console, ' ', this.info.size.x, leftColCurrRow, written);
      }
   }

   @Override
   protected void processCursorLeft(int count) throws IOException {
      this.getConsoleInfo();
      this.info.cursorPosition.x = (short)Math.max(0, this.info.cursorPosition.x - count);
      this.applyCursorPosition();
   }

   @Override
   protected void processCursorRight(int count) throws IOException {
      this.getConsoleInfo();
      this.info.cursorPosition.x = (short)Math.min(this.info.window.width(), this.info.cursorPosition.x + count);
      this.applyCursorPosition();
   }

   @Override
   protected void processCursorDown(int count) throws IOException {
      this.getConsoleInfo();
      this.info.cursorPosition.y = (short)Math.min(Math.max(0, this.info.size.y - 1), this.info.cursorPosition.y + count);
      this.applyCursorPosition();
   }

   @Override
   protected void processCursorUp(int count) throws IOException {
      this.getConsoleInfo();
      this.info.cursorPosition.y = (short)Math.max(this.info.window.top, this.info.cursorPosition.y - count);
      this.applyCursorPosition();
   }

   @Override
   protected void processCursorTo(int row, int col) throws IOException {
      this.getConsoleInfo();
      this.info.cursorPosition.y = (short)Math.max(this.info.window.top, Math.min(this.info.size.y, this.info.window.top + row - 1));
      this.info.cursorPosition.x = (short)Math.max(0, Math.min(this.info.window.width(), col - 1));
      this.applyCursorPosition();
   }

   @Override
   protected void processCursorToColumn(int x) throws IOException {
      this.getConsoleInfo();
      this.info.cursorPosition.x = (short)Math.max(0, Math.min(this.info.window.width(), x - 1));
      this.applyCursorPosition();
   }

   @Override
   protected void processCursorUpLine(int count) throws IOException {
      this.getConsoleInfo();
      this.info.cursorPosition.x = 0;
      this.info.cursorPosition.y = (short)Math.max(this.info.window.top, this.info.cursorPosition.y - count);
      this.applyCursorPosition();
   }

   @Override
   protected void processCursorDownLine(int count) throws IOException {
      this.getConsoleInfo();
      this.info.cursorPosition.x = 0;
      this.info.cursorPosition.y = (short)Math.max(this.info.window.top, this.info.cursorPosition.y + count);
      this.applyCursorPosition();
   }

   @Override
   protected void processSetForegroundColor(int color, boolean bright) throws IOException {
      this.info.attributes = (short)(this.info.attributes & -8 | ANSI_FOREGROUND_COLOR_MAP[color]);
      if (bright) {
         this.info.attributes = (short)(this.info.attributes | Kernel32.FOREGROUND_INTENSITY);
      }

      this.applyAttribute();
   }

   @Override
   protected void processSetForegroundColorExt(int paletteIndex) throws IOException {
      int round = Colors.roundColor(paletteIndex, 16);
      this.processSetForegroundColor(round >= 8 ? round - 8 : round, round >= 8);
   }

   @Override
   protected void processSetForegroundColorExt(int r, int g, int b) throws IOException {
      int round = Colors.roundRgbColor(r, g, b, 16);
      this.processSetForegroundColor(round >= 8 ? round - 8 : round, round >= 8);
   }

   @Override
   protected void processSetBackgroundColor(int color, boolean bright) throws IOException {
      this.info.attributes = (short)(this.info.attributes & -113 | ANSI_BACKGROUND_COLOR_MAP[color]);
      if (bright) {
         this.info.attributes = (short)(this.info.attributes | Kernel32.BACKGROUND_INTENSITY);
      }

      this.applyAttribute();
   }

   @Override
   protected void processSetBackgroundColorExt(int paletteIndex) throws IOException {
      int round = Colors.roundColor(paletteIndex, 16);
      this.processSetBackgroundColor(round >= 8 ? round - 8 : round, round >= 8);
   }

   @Override
   protected void processSetBackgroundColorExt(int r, int g, int b) throws IOException {
      int round = Colors.roundRgbColor(r, g, b, 16);
      this.processSetBackgroundColor(round >= 8 ? round - 8 : round, round >= 8);
   }

   @Override
   protected void processDefaultTextColor() throws IOException {
      this.info.attributes = (short)(this.info.attributes & -16 | this.originalColors & 15);
      this.info.attributes = (short)(this.info.attributes & ~Kernel32.FOREGROUND_INTENSITY);
      this.applyAttribute();
   }

   @Override
   protected void processDefaultBackgroundColor() throws IOException {
      this.info.attributes = (short)(this.info.attributes & -241 | this.originalColors & 240);
      this.info.attributes = (short)(this.info.attributes & ~Kernel32.BACKGROUND_INTENSITY);
      this.applyAttribute();
   }

   @Override
   protected void processAttributeReset() throws IOException {
      this.info.attributes = (short)(this.info.attributes & -256 | this.originalColors);
      this.negative = false;
      this.applyAttribute();
   }

   @Override
   protected void processSetAttribute(int attribute) throws IOException {
      switch (attribute) {
         case 1:
            this.info.attributes = (short)(this.info.attributes | Kernel32.FOREGROUND_INTENSITY);
            this.applyAttribute();
            break;
         case 4:
            this.info.attributes = (short)(this.info.attributes | Kernel32.BACKGROUND_INTENSITY);
            this.applyAttribute();
            break;
         case 7:
            this.negative = true;
            this.applyAttribute();
            break;
         case 22:
            this.info.attributes = (short)(this.info.attributes & ~Kernel32.FOREGROUND_INTENSITY);
            this.applyAttribute();
            break;
         case 24:
            this.info.attributes = (short)(this.info.attributes & ~Kernel32.BACKGROUND_INTENSITY);
            this.applyAttribute();
            break;
         case 27:
            this.negative = false;
            this.applyAttribute();
      }
   }

   @Override
   protected void processSaveCursorPosition() throws IOException {
      this.getConsoleInfo();
      this.savedX = this.info.cursorPosition.x;
      this.savedY = this.info.cursorPosition.y;
   }

   @Override
   protected void processRestoreCursorPosition() throws IOException {
      if (this.savedX != -1 && this.savedY != -1) {
         this.os.flush();
         this.info.cursorPosition.x = this.savedX;
         this.info.cursorPosition.y = this.savedY;
         this.applyCursorPosition();
      }
   }

   @Override
   protected void processInsertLine(int optionInt) throws IOException {
      this.getConsoleInfo();
      Kernel32.SMALL_RECT scroll = this.info.window.copy();
      scroll.top = this.info.cursorPosition.y;
      Kernel32.COORD org = new Kernel32.COORD();
      org.x = 0;
      org.y = (short)(this.info.cursorPosition.y + optionInt);
      Kernel32.CHAR_INFO info = new Kernel32.CHAR_INFO();
      info.attributes = this.originalColors;
      info.unicodeChar = ' ';
      if (Kernel32.ScrollConsoleScreenBuffer(this.console, scroll, scroll, org, info) == 0) {
         throw new IOException(Kernel32.getLastErrorMessage());
      }
   }

   @Override
   protected void processDeleteLine(int optionInt) throws IOException {
      this.getConsoleInfo();
      Kernel32.SMALL_RECT scroll = this.info.window.copy();
      scroll.top = this.info.cursorPosition.y;
      Kernel32.COORD org = new Kernel32.COORD();
      org.x = 0;
      org.y = (short)(this.info.cursorPosition.y - optionInt);
      Kernel32.CHAR_INFO info = new Kernel32.CHAR_INFO();
      info.attributes = this.originalColors;
      info.unicodeChar = ' ';
      if (Kernel32.ScrollConsoleScreenBuffer(this.console, scroll, scroll, org, info) == 0) {
         throw new IOException(Kernel32.getLastErrorMessage());
      }
   }

   @Override
   protected void processChangeWindowTitle(String label) {
      Kernel32.SetConsoleTitle(label);
   }
}
