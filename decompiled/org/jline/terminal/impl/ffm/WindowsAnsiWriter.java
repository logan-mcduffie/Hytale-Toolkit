package org.jline.terminal.impl.ffm;

import java.io.IOException;
import java.io.Writer;
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import org.jline.utils.AnsiWriter;
import org.jline.utils.Colors;

class WindowsAnsiWriter extends AnsiWriter {
   private static final MemorySegment console = Kernel32.GetStdHandle(-11);
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
   private final Kernel32.CONSOLE_SCREEN_BUFFER_INFO info = new Kernel32.CONSOLE_SCREEN_BUFFER_INFO(Arena.ofAuto());
   private final short originalColors;
   private boolean negative;
   private boolean bold;
   private boolean underline;
   private short savedX = -1;
   private short savedY = -1;

   public WindowsAnsiWriter(Writer out) throws IOException {
      super(out);
      this.getConsoleInfo();
      this.originalColors = this.info.attributes();
   }

   private void getConsoleInfo() throws IOException {
      this.out.flush();
      if (Kernel32.GetConsoleScreenBufferInfo(console, this.info) == 0) {
         throw new IOException("Could not get the screen info: " + Kernel32.getLastErrorMessage());
      } else {
         if (this.negative) {
            this.info.attributes(this.invertAttributeColors(this.info.attributes()));
         }
      }
   }

   private void applyAttribute() throws IOException {
      this.out.flush();
      short attributes = this.info.attributes();
      if (this.bold) {
         attributes = (short)(attributes | 8);
      }

      if (this.underline) {
         attributes = (short)(attributes | 128);
      }

      if (this.negative) {
         attributes = this.invertAttributeColors(attributes);
      }

      if (Kernel32.SetConsoleTextAttribute(console, attributes) == 0) {
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
      this.info.cursorPosition().x((short)Math.max(0, Math.min(this.info.size().x() - 1, this.info.cursorPosition().x())));
      this.info.cursorPosition().y((short)Math.max(0, Math.min(this.info.size().y() - 1, this.info.cursorPosition().y())));
      if (Kernel32.SetConsoleCursorPosition(console, this.info.cursorPosition()) == 0) {
         throw new IOException(Kernel32.getLastErrorMessage());
      }
   }

   @Override
   protected void processEraseScreen(int eraseOption) throws IOException {
      this.getConsoleInfo();

      try (Arena arena = Arena.ofConfined()) {
         MemorySegment written = arena.allocate(ValueLayout.JAVA_INT);
         switch (eraseOption) {
            case 0:
               int lengthToEnd = (this.info.window().bottom() - this.info.cursorPosition().y()) * this.info.size().x()
                  + (this.info.size().x() - this.info.cursorPosition().x());
               Kernel32.FillConsoleOutputAttribute(console, this.originalColors, lengthToEnd, this.info.cursorPosition(), written);
               Kernel32.FillConsoleOutputCharacterW(console, ' ', lengthToEnd, this.info.cursorPosition(), written);
               break;
            case 1:
               Kernel32.COORD topLeft2 = new Kernel32.COORD(arena, (short)0, this.info.window().top());
               int lengthToCursor = (this.info.cursorPosition().y() - this.info.window().top()) * this.info.size().x() + this.info.cursorPosition().x();
               Kernel32.FillConsoleOutputAttribute(console, this.originalColors, lengthToCursor, topLeft2, written);
               Kernel32.FillConsoleOutputCharacterW(console, ' ', lengthToCursor, topLeft2, written);
               break;
            case 2:
               Kernel32.COORD topLeft = new Kernel32.COORD(arena, (short)0, this.info.window().top());
               int screenLength = this.info.window().height() * this.info.size().x();
               Kernel32.FillConsoleOutputAttribute(console, this.originalColors, screenLength, topLeft, written);
               Kernel32.FillConsoleOutputCharacterW(console, ' ', screenLength, topLeft, written);
         }
      }
   }

   @Override
   protected void processEraseLine(int eraseOption) throws IOException {
      this.getConsoleInfo();

      try (Arena arena = Arena.ofConfined()) {
         MemorySegment written = arena.allocate(ValueLayout.JAVA_INT);
         switch (eraseOption) {
            case 0:
               int lengthToLastCol = this.info.size().x() - this.info.cursorPosition().x();
               Kernel32.FillConsoleOutputAttribute(console, this.originalColors, lengthToLastCol, this.info.cursorPosition(), written);
               Kernel32.FillConsoleOutputCharacterW(console, ' ', lengthToLastCol, this.info.cursorPosition(), written);
               break;
            case 1:
               Kernel32.COORD leftColCurrRow2 = new Kernel32.COORD(arena, (short)0, this.info.cursorPosition().y());
               Kernel32.FillConsoleOutputAttribute(console, this.originalColors, this.info.cursorPosition().x(), leftColCurrRow2, written);
               Kernel32.FillConsoleOutputCharacterW(console, ' ', this.info.cursorPosition().x(), leftColCurrRow2, written);
               break;
            case 2:
               Kernel32.COORD leftColCurrRow = new Kernel32.COORD(arena, (short)0, this.info.cursorPosition().y());
               Kernel32.FillConsoleOutputAttribute(console, this.originalColors, this.info.size().x(), leftColCurrRow, written);
               Kernel32.FillConsoleOutputCharacterW(console, ' ', this.info.size().x(), leftColCurrRow, written);
         }
      }
   }

   @Override
   protected void processCursorUpLine(int count) throws IOException {
      this.getConsoleInfo();
      this.info.cursorPosition().x((short)0);
      this.info.cursorPosition().y((short)(this.info.cursorPosition().y() - count));
      this.applyCursorPosition();
   }

   @Override
   protected void processCursorDownLine(int count) throws IOException {
      this.getConsoleInfo();
      this.info.cursorPosition().x((short)0);
      this.info.cursorPosition().y((short)(this.info.cursorPosition().y() + count));
      this.applyCursorPosition();
   }

   @Override
   protected void processCursorLeft(int count) throws IOException {
      this.getConsoleInfo();
      this.info.cursorPosition().x((short)(this.info.cursorPosition().x() - count));
      this.applyCursorPosition();
   }

   @Override
   protected void processCursorRight(int count) throws IOException {
      this.getConsoleInfo();
      this.info.cursorPosition().x((short)(this.info.cursorPosition().x() + count));
      this.applyCursorPosition();
   }

   @Override
   protected void processCursorDown(int count) throws IOException {
      this.getConsoleInfo();
      int nb = Math.max(0, this.info.cursorPosition().y() + count - this.info.size().y() + 1);
      if (nb != count) {
         this.info.cursorPosition().y((short)(this.info.cursorPosition().y() + count));
         this.applyCursorPosition();
      }

      if (nb > 0) {
         try (Arena arena = Arena.ofConfined()) {
            Kernel32.SMALL_RECT scroll = new Kernel32.SMALL_RECT(arena, this.info.window());
            scroll.top((short)0);
            Kernel32.COORD org = new Kernel32.COORD(arena);
            org.x((short)0);
            org.y((short)(-nb));
            Kernel32.CHAR_INFO info = new Kernel32.CHAR_INFO(arena, ' ', this.originalColors);
            Kernel32.ScrollConsoleScreenBuffer(console, scroll, scroll, org, info);
         }
      }
   }

   @Override
   protected void processCursorUp(int count) throws IOException {
      this.getConsoleInfo();
      this.info.cursorPosition().y((short)(this.info.cursorPosition().y() - count));
      this.applyCursorPosition();
   }

   @Override
   protected void processCursorTo(int row, int col) throws IOException {
      this.getConsoleInfo();
      this.info.cursorPosition().y((short)(this.info.window().top() + row - 1));
      this.info.cursorPosition().x((short)(col - 1));
      this.applyCursorPosition();
   }

   @Override
   protected void processCursorToColumn(int x) throws IOException {
      this.getConsoleInfo();
      this.info.cursorPosition().x((short)(x - 1));
      this.applyCursorPosition();
   }

   @Override
   protected void processSetForegroundColorExt(int paletteIndex) throws IOException {
      int color = Colors.roundColor(paletteIndex, 16);
      this.info.attributes((short)(this.info.attributes() & -8 | ANSI_FOREGROUND_COLOR_MAP[color & 7]));
      this.info.attributes((short)(this.info.attributes() & -9 | (color >= 8 ? 8 : 0)));
      this.applyAttribute();
   }

   @Override
   protected void processSetBackgroundColorExt(int paletteIndex) throws IOException {
      int color = Colors.roundColor(paletteIndex, 16);
      this.info.attributes((short)(this.info.attributes() & -113 | ANSI_BACKGROUND_COLOR_MAP[color & 7]));
      this.info.attributes((short)(this.info.attributes() & -129 | (color >= 8 ? 128 : 0)));
      this.applyAttribute();
   }

   @Override
   protected void processDefaultTextColor() throws IOException {
      this.info.attributes((short)(this.info.attributes() & -16 | this.originalColors & 15));
      this.info.attributes((short)(this.info.attributes() & -9));
      this.applyAttribute();
   }

   @Override
   protected void processDefaultBackgroundColor() throws IOException {
      this.info.attributes((short)(this.info.attributes() & -241 | this.originalColors & 240));
      this.info.attributes((short)(this.info.attributes() & -129));
      this.applyAttribute();
   }

   @Override
   protected void processAttributeRest() throws IOException {
      this.info.attributes((short)(this.info.attributes() & -256 | this.originalColors));
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
      this.savedX = this.info.cursorPosition().x();
      this.savedY = this.info.cursorPosition().y();
   }

   @Override
   protected void processRestoreCursorPosition() throws IOException {
      if (this.savedX != -1 && this.savedY != -1) {
         this.out.flush();
         this.info.cursorPosition().x(this.savedX);
         this.info.cursorPosition().y(this.savedY);
         this.applyCursorPosition();
      }
   }

   @Override
   protected void processInsertLine(int optionInt) throws IOException {
      try (Arena arena = Arena.ofConfined()) {
         this.getConsoleInfo();
         Kernel32.SMALL_RECT scroll = this.info.window().copy(arena);
         scroll.top(this.info.cursorPosition().y());
         Kernel32.COORD org = new Kernel32.COORD(arena, (short)0, (short)(this.info.cursorPosition().y() + optionInt));
         Kernel32.CHAR_INFO info = new Kernel32.CHAR_INFO(arena, ' ', this.originalColors);
         if (Kernel32.ScrollConsoleScreenBuffer(console, scroll, scroll, org, info) == 0) {
            throw new IOException(Kernel32.getLastErrorMessage());
         }
      }
   }

   @Override
   protected void processDeleteLine(int optionInt) throws IOException {
      try (Arena arena = Arena.ofConfined()) {
         this.getConsoleInfo();
         Kernel32.SMALL_RECT scroll = this.info.window().copy(arena);
         scroll.top(this.info.cursorPosition().y());
         Kernel32.COORD org = new Kernel32.COORD(arena, (short)0, (short)(this.info.cursorPosition().y() - optionInt));
         Kernel32.CHAR_INFO info = new Kernel32.CHAR_INFO(arena, ' ', this.originalColors);
         if (Kernel32.ScrollConsoleScreenBuffer(console, scroll, scroll, org, info) == 0) {
            throw new IOException(Kernel32.getLastErrorMessage());
         }
      }
   }

   @Override
   protected void processChangeWindowTitle(String title) {
      try (Arena session = Arena.ofConfined()) {
         MemorySegment str = session.allocateFrom(title);
         Kernel32.SetConsoleTitleW(str);
      }
   }
}
