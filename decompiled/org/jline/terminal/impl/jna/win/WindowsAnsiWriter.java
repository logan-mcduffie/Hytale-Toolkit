package org.jline.terminal.impl.jna.win;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import java.io.IOException;
import java.io.Writer;
import org.jline.utils.AnsiWriter;
import org.jline.utils.Colors;

public final class WindowsAnsiWriter extends AnsiWriter {
   private static final short FOREGROUND_BLACK = 0;
   private static final short FOREGROUND_YELLOW = 6;
   private static final short FOREGROUND_MAGENTA = 5;
   private static final short FOREGROUND_CYAN = 3;
   private static final short FOREGROUND_WHITE = 7;
   private static final short BACKGROUND_BLACK = 0;
   private static final short BACKGROUND_YELLOW = 96;
   private static final short BACKGROUND_MAGENTA = 80;
   private static final short BACKGROUND_CYAN = 48;
   private static final short BACKGROUND_WHITE = 112;
   private static final short[] ANSI_FOREGROUND_COLOR_MAP = new short[]{0, 4, 2, 6, 1, 5, 3, 7};
   private static final short[] ANSI_BACKGROUND_COLOR_MAP = new short[]{0, 64, 32, 96, 16, 80, 48, 112};
   private static final int MAX_ESCAPE_SEQUENCE_LENGTH = 100;
   private final Pointer console;
   private final Kernel32.CONSOLE_SCREEN_BUFFER_INFO info = new Kernel32.CONSOLE_SCREEN_BUFFER_INFO();
   private final short originalColors;
   private boolean negative;
   private boolean bold;
   private boolean underline;
   private short savedX = -1;
   private short savedY = -1;

   public WindowsAnsiWriter(Writer out, Pointer console) throws IOException {
      super(out);
      this.console = console;
      this.getConsoleInfo();
      this.originalColors = this.info.wAttributes;
   }

   private void getConsoleInfo() throws IOException {
      this.out.flush();
      Kernel32.INSTANCE.GetConsoleScreenBufferInfo(this.console, this.info);
      if (this.negative) {
         this.info.wAttributes = this.invertAttributeColors(this.info.wAttributes);
      }
   }

   private void applyAttribute() throws IOException {
      this.out.flush();
      short attributes = this.info.wAttributes;
      if (this.bold) {
         attributes = (short)(attributes | 8);
      }

      if (this.underline) {
         attributes = (short)(attributes | 128);
      }

      if (this.negative) {
         attributes = this.invertAttributeColors(attributes);
      }

      Kernel32.INSTANCE.SetConsoleTextAttribute(this.console, attributes);
   }

   private short invertAttributeColors(short attributes) {
      int fg = 15 & attributes;
      fg <<= 4;
      int bg = 240 & attributes;
      bg >>= 4;
      return (short)(attributes & '\uff00' | fg | bg);
   }

   private void applyCursorPosition() throws IOException {
      this.info.dwCursorPosition.X = (short)Math.max(0, Math.min(this.info.dwSize.X - 1, this.info.dwCursorPosition.X));
      this.info.dwCursorPosition.Y = (short)Math.max(0, Math.min(this.info.dwSize.Y - 1, this.info.dwCursorPosition.Y));
      Kernel32.INSTANCE.SetConsoleCursorPosition(this.console, this.info.dwCursorPosition);
   }

   @Override
   protected void processEraseScreen(int eraseOption) throws IOException {
      this.getConsoleInfo();
      IntByReference written = new IntByReference();
      switch (eraseOption) {
         case 0:
            int lengthToEnd = (this.info.srWindow.Bottom - this.info.dwCursorPosition.Y) * this.info.dwSize.X
               + (this.info.dwSize.X - this.info.dwCursorPosition.X);
            Kernel32.INSTANCE.FillConsoleOutputCharacter(this.console, ' ', lengthToEnd, this.info.dwCursorPosition, written);
            Kernel32.INSTANCE.FillConsoleOutputAttribute(this.console, this.info.wAttributes, lengthToEnd, this.info.dwCursorPosition, written);
            break;
         case 1:
            Kernel32.COORD topLeft2 = new Kernel32.COORD();
            topLeft2.X = 0;
            topLeft2.Y = this.info.srWindow.Top;
            int lengthToCursor = (this.info.dwCursorPosition.Y - this.info.srWindow.Top) * this.info.dwSize.X + this.info.dwCursorPosition.X;
            Kernel32.INSTANCE.FillConsoleOutputCharacter(this.console, ' ', lengthToCursor, topLeft2, written);
            Kernel32.INSTANCE.FillConsoleOutputAttribute(this.console, this.info.wAttributes, lengthToCursor, topLeft2, written);
            break;
         case 2:
            Kernel32.COORD topLeft = new Kernel32.COORD();
            topLeft.X = 0;
            topLeft.Y = this.info.srWindow.Top;
            int screenLength = this.info.srWindow.height() * this.info.dwSize.X;
            Kernel32.INSTANCE.FillConsoleOutputCharacter(this.console, ' ', screenLength, topLeft, written);
            Kernel32.INSTANCE.FillConsoleOutputAttribute(this.console, this.info.wAttributes, screenLength, topLeft, written);
      }
   }

   @Override
   protected void processEraseLine(int eraseOption) throws IOException {
      this.getConsoleInfo();
      IntByReference written = new IntByReference();
      switch (eraseOption) {
         case 0:
            int lengthToLastCol = this.info.dwSize.X - this.info.dwCursorPosition.X;
            Kernel32.INSTANCE.FillConsoleOutputCharacter(this.console, ' ', lengthToLastCol, this.info.dwCursorPosition, written);
            Kernel32.INSTANCE.FillConsoleOutputAttribute(this.console, this.info.wAttributes, lengthToLastCol, this.info.dwCursorPosition, written);
            break;
         case 1:
            Kernel32.COORD leftColCurrRow2 = new Kernel32.COORD((short)0, this.info.dwCursorPosition.Y);
            Kernel32.INSTANCE.FillConsoleOutputCharacter(this.console, ' ', this.info.dwCursorPosition.X, leftColCurrRow2, written);
            Kernel32.INSTANCE.FillConsoleOutputAttribute(this.console, this.info.wAttributes, this.info.dwCursorPosition.X, leftColCurrRow2, written);
            break;
         case 2:
            Kernel32.COORD leftColCurrRow = new Kernel32.COORD((short)0, this.info.dwCursorPosition.Y);
            Kernel32.INSTANCE.FillConsoleOutputCharacter(this.console, ' ', this.info.dwSize.X, leftColCurrRow, written);
            Kernel32.INSTANCE.FillConsoleOutputAttribute(this.console, this.info.wAttributes, this.info.dwSize.X, leftColCurrRow, written);
      }
   }

   @Override
   protected void processCursorUpLine(int count) throws IOException {
      this.getConsoleInfo();
      this.info.dwCursorPosition.X = 0;
      this.info.dwCursorPosition.Y -= (short)count;
      this.applyCursorPosition();
   }

   @Override
   protected void processCursorDownLine(int count) throws IOException {
      this.getConsoleInfo();
      this.info.dwCursorPosition.X = 0;
      this.info.dwCursorPosition.Y += (short)count;
      this.applyCursorPosition();
   }

   @Override
   protected void processCursorLeft(int count) throws IOException {
      this.getConsoleInfo();
      this.info.dwCursorPosition.X -= (short)count;
      this.applyCursorPosition();
   }

   @Override
   protected void processCursorRight(int count) throws IOException {
      this.getConsoleInfo();
      this.info.dwCursorPosition.X += (short)count;
      this.applyCursorPosition();
   }

   @Override
   protected void processCursorDown(int count) throws IOException {
      this.getConsoleInfo();
      int nb = Math.max(0, this.info.dwCursorPosition.Y + count - this.info.dwSize.Y + 1);
      if (nb != count) {
         this.info.dwCursorPosition.Y += (short)count;
         this.applyCursorPosition();
      }

      if (nb > 0) {
         Kernel32.SMALL_RECT scroll = new Kernel32.SMALL_RECT(this.info.srWindow);
         scroll.Top = 0;
         Kernel32.COORD org = new Kernel32.COORD();
         org.X = 0;
         org.Y = (short)(-nb);
         Kernel32.CHAR_INFO info = new Kernel32.CHAR_INFO(' ', this.originalColors);
         Kernel32.INSTANCE.ScrollConsoleScreenBuffer(this.console, scroll, scroll, org, info);
      }
   }

   @Override
   protected void processCursorUp(int count) throws IOException {
      this.getConsoleInfo();
      this.info.dwCursorPosition.Y -= (short)count;
      this.applyCursorPosition();
   }

   @Override
   protected void processCursorTo(int row, int col) throws IOException {
      this.getConsoleInfo();
      this.info.dwCursorPosition.Y = (short)(this.info.srWindow.Top + row - 1);
      this.info.dwCursorPosition.X = (short)(col - 1);
      this.applyCursorPosition();
   }

   @Override
   protected void processCursorToColumn(int x) throws IOException {
      this.getConsoleInfo();
      this.info.dwCursorPosition.X = (short)(x - 1);
      this.applyCursorPosition();
   }

   @Override
   protected void processSetForegroundColorExt(int paletteIndex) throws IOException {
      int color = Colors.roundColor(paletteIndex, 16);
      this.info.wAttributes = (short)(this.info.wAttributes & -8 | ANSI_FOREGROUND_COLOR_MAP[color & 7]);
      this.info.wAttributes = (short)(this.info.wAttributes & -9 | (color >= 8 ? 8 : 0));
      this.applyAttribute();
   }

   @Override
   protected void processSetBackgroundColorExt(int paletteIndex) throws IOException {
      int color = Colors.roundColor(paletteIndex, 16);
      this.info.wAttributes = (short)(this.info.wAttributes & -113 | ANSI_BACKGROUND_COLOR_MAP[color & 7]);
      this.info.wAttributes = (short)(this.info.wAttributes & -129 | (color >= 8 ? 128 : 0));
      this.applyAttribute();
   }

   @Override
   protected void processDefaultTextColor() throws IOException {
      this.info.wAttributes = (short)(this.info.wAttributes & -16 | this.originalColors & 15);
      this.applyAttribute();
   }

   @Override
   protected void processDefaultBackgroundColor() throws IOException {
      this.info.wAttributes = (short)(this.info.wAttributes & -241 | this.originalColors & 240);
      this.applyAttribute();
   }

   @Override
   protected void processAttributeRest() throws IOException {
      this.info.wAttributes = (short)(this.info.wAttributes & -256 | this.originalColors);
      this.negative = false;
      this.bold = false;
      this.underline = false;
      this.applyAttribute();
   }

   @Override
   protected void processSetAttribute(int attribute) throws IOException {
      switch (attribute) {
         case 1:
            this.bold = true;
            this.applyAttribute();
            break;
         case 4:
            this.underline = true;
            this.applyAttribute();
            break;
         case 7:
            this.negative = true;
            this.applyAttribute();
            break;
         case 22:
            this.bold = false;
            this.applyAttribute();
            break;
         case 24:
            this.underline = false;
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
      this.savedX = this.info.dwCursorPosition.X;
      this.savedY = this.info.dwCursorPosition.Y;
   }

   @Override
   protected void processRestoreCursorPosition() throws IOException {
      if (this.savedX != -1 && this.savedY != -1) {
         this.out.flush();
         this.info.dwCursorPosition.X = this.savedX;
         this.info.dwCursorPosition.Y = this.savedY;
         this.applyCursorPosition();
      }
   }

   @Override
   protected void processInsertLine(int optionInt) throws IOException {
      this.getConsoleInfo();
      Kernel32.SMALL_RECT scroll = new Kernel32.SMALL_RECT(this.info.srWindow);
      scroll.Top = this.info.dwCursorPosition.Y;
      Kernel32.COORD org = new Kernel32.COORD();
      org.X = 0;
      org.Y = (short)(this.info.dwCursorPosition.Y + optionInt);
      Kernel32.CHAR_INFO info = new Kernel32.CHAR_INFO(' ', this.originalColors);
      Kernel32.INSTANCE.ScrollConsoleScreenBuffer(this.console, scroll, scroll, org, info);
   }

   @Override
   protected void processDeleteLine(int optionInt) throws IOException {
      this.getConsoleInfo();
      Kernel32.SMALL_RECT scroll = new Kernel32.SMALL_RECT(this.info.srWindow);
      scroll.Top = this.info.dwCursorPosition.Y;
      Kernel32.COORD org = new Kernel32.COORD();
      org.X = 0;
      org.Y = (short)(this.info.dwCursorPosition.Y - optionInt);
      Kernel32.CHAR_INFO info = new Kernel32.CHAR_INFO(' ', this.originalColors);
      Kernel32.INSTANCE.ScrollConsoleScreenBuffer(this.console, scroll, scroll, org, info);
   }

   @Override
   protected void processChangeWindowTitle(String label) {
      Kernel32.INSTANCE.SetConsoleTitle(label);
   }
}
