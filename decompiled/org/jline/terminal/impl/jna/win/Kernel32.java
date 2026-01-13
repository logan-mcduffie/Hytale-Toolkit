package org.jline.terminal.impl.jna.win;

import com.sun.jna.LastErrorException;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.Union;
import com.sun.jna.Structure.ByValue;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;
import java.util.Arrays;
import java.util.List;

interface Kernel32 extends StdCallLibrary {
   Kernel32 INSTANCE = (Kernel32)Native.load("kernel32", Kernel32.class, W32APIOptions.UNICODE_OPTIONS);
   Pointer INVALID_HANDLE_VALUE = Pointer.createConstant(-1L);
   int STD_INPUT_HANDLE = -10;
   int STD_OUTPUT_HANDLE = -11;
   int STD_ERROR_HANDLE = -12;
   int ENABLE_PROCESSED_INPUT = 1;
   int ENABLE_LINE_INPUT = 2;
   int ENABLE_ECHO_INPUT = 4;
   int ENABLE_WINDOW_INPUT = 8;
   int ENABLE_MOUSE_INPUT = 16;
   int ENABLE_INSERT_MODE = 32;
   int ENABLE_QUICK_EDIT_MODE = 64;
   int ENABLE_EXTENDED_FLAGS = 128;
   int RIGHT_ALT_PRESSED = 1;
   int LEFT_ALT_PRESSED = 2;
   int RIGHT_CTRL_PRESSED = 4;
   int LEFT_CTRL_PRESSED = 8;
   int SHIFT_PRESSED = 16;
   int FOREGROUND_BLUE = 1;
   int FOREGROUND_GREEN = 2;
   int FOREGROUND_RED = 4;
   int FOREGROUND_INTENSITY = 8;
   int BACKGROUND_BLUE = 16;
   int BACKGROUND_GREEN = 32;
   int BACKGROUND_RED = 64;
   int BACKGROUND_INTENSITY = 128;
   int FROM_LEFT_1ST_BUTTON_PRESSED = 1;
   int RIGHTMOST_BUTTON_PRESSED = 2;
   int FROM_LEFT_2ND_BUTTON_PRESSED = 4;
   int FROM_LEFT_3RD_BUTTON_PRESSED = 8;
   int FROM_LEFT_4TH_BUTTON_PRESSED = 16;
   int MOUSE_MOVED = 1;
   int DOUBLE_CLICK = 2;
   int MOUSE_WHEELED = 4;
   int MOUSE_HWHEELED = 8;

   int WaitForSingleObject(Pointer var1, int var2);

   Pointer GetStdHandle(int var1);

   void AllocConsole() throws LastErrorException;

   void FreeConsole() throws LastErrorException;

   Pointer GetConsoleWindow();

   int GetConsoleCP();

   void FillConsoleOutputCharacter(Pointer var1, char var2, int var3, Kernel32.COORD var4, IntByReference var5) throws LastErrorException;

   void FillConsoleOutputAttribute(Pointer var1, short var2, int var3, Kernel32.COORD var4, IntByReference var5) throws LastErrorException;

   void GetConsoleCursorInfo(Pointer var1, Kernel32.CONSOLE_CURSOR_INFO.ByReference var2) throws LastErrorException;

   void GetConsoleMode(Pointer var1, IntByReference var2) throws LastErrorException;

   void GetConsoleScreenBufferInfo(Pointer var1, Kernel32.CONSOLE_SCREEN_BUFFER_INFO var2) throws LastErrorException;

   void GetNumberOfConsoleInputEvents(Pointer var1, IntByReference var2) throws LastErrorException;

   void ReadConsoleInput(Pointer var1, Kernel32.INPUT_RECORD[] var2, int var3, IntByReference var4) throws LastErrorException;

   void SetConsoleCtrlHandler(Pointer var1, boolean var2) throws LastErrorException;

   void ReadConsoleOutput(Pointer var1, Kernel32.CHAR_INFO[] var2, Kernel32.COORD var3, Kernel32.COORD var4, Kernel32.SMALL_RECT var5) throws LastErrorException;

   void ReadConsoleOutputA(Pointer var1, Kernel32.CHAR_INFO[] var2, Kernel32.COORD var3, Kernel32.COORD var4, Kernel32.SMALL_RECT var5) throws LastErrorException;

   void ReadConsoleOutputCharacter(Pointer var1, char[] var2, int var3, Kernel32.COORD var4, IntByReference var5) throws LastErrorException;

   void ReadConsoleOutputCharacterA(Pointer var1, byte[] var2, int var3, Kernel32.COORD var4, IntByReference var5) throws LastErrorException;

   void SetConsoleCursorInfo(Pointer var1, Kernel32.CONSOLE_CURSOR_INFO var2) throws LastErrorException;

   void SetConsoleCP(int var1) throws LastErrorException;

   void SetConsoleOutputCP(int var1) throws LastErrorException;

   void SetConsoleCursorPosition(Pointer var1, Kernel32.COORD var2) throws LastErrorException;

   void SetConsoleMode(Pointer var1, int var2) throws LastErrorException;

   void SetConsoleScreenBufferSize(Pointer var1, Kernel32.COORD var2) throws LastErrorException;

   void SetConsoleTextAttribute(Pointer var1, short var2) throws LastErrorException;

   void SetConsoleTitle(String var1) throws LastErrorException;

   void SetConsoleWindowInfo(Pointer var1, boolean var2, Kernel32.SMALL_RECT var3) throws LastErrorException;

   void WriteConsoleW(Pointer var1, char[] var2, int var3, IntByReference var4, Pointer var5) throws LastErrorException;

   void WriteConsoleOutput(Pointer var1, Kernel32.CHAR_INFO[] var2, Kernel32.COORD var3, Kernel32.COORD var4, Kernel32.SMALL_RECT var5) throws LastErrorException;

   void WriteConsoleOutputA(Pointer var1, Kernel32.CHAR_INFO[] var2, Kernel32.COORD var3, Kernel32.COORD var4, Kernel32.SMALL_RECT var5) throws LastErrorException;

   void WriteConsoleOutputCharacter(Pointer var1, char[] var2, int var3, Kernel32.COORD var4, IntByReference var5) throws LastErrorException;

   void WriteConsoleOutputCharacterA(Pointer var1, byte[] var2, int var3, Kernel32.COORD var4, IntByReference var5) throws LastErrorException;

   void ScrollConsoleScreenBuffer(Pointer var1, Kernel32.SMALL_RECT var2, Kernel32.SMALL_RECT var3, Kernel32.COORD var4, Kernel32.CHAR_INFO var5) throws LastErrorException;

   public static class CHAR_INFO extends Structure {
      public Kernel32.UnionChar uChar;
      public short Attributes;
      private static String[] fieldOrder = new String[]{"uChar", "Attributes"};

      public CHAR_INFO() {
      }

      public CHAR_INFO(char c, short attr) {
         this.uChar = new Kernel32.UnionChar(c);
         this.Attributes = attr;
      }

      public CHAR_INFO(byte c, short attr) {
         this.uChar = new Kernel32.UnionChar(c);
         this.Attributes = attr;
      }

      public static Kernel32.CHAR_INFO[] createArray(int size) {
         return (Kernel32.CHAR_INFO[])new Kernel32.CHAR_INFO().toArray(size);
      }

      protected List<String> getFieldOrder() {
         return Arrays.asList(fieldOrder);
      }
   }

   public static class CONSOLE_CURSOR_INFO extends Structure {
      public int dwSize;
      public boolean bVisible;
      private static String[] fieldOrder = new String[]{"dwSize", "bVisible"};

      protected List<String> getFieldOrder() {
         return Arrays.asList(fieldOrder);
      }

      public static class ByReference extends Kernel32.CONSOLE_CURSOR_INFO implements com.sun.jna.Structure.ByReference {
      }
   }

   public static class CONSOLE_SCREEN_BUFFER_INFO extends Structure {
      public Kernel32.COORD dwSize;
      public Kernel32.COORD dwCursorPosition;
      public short wAttributes;
      public Kernel32.SMALL_RECT srWindow;
      public Kernel32.COORD dwMaximumWindowSize;
      private static String[] fieldOrder = new String[]{"dwSize", "dwCursorPosition", "wAttributes", "srWindow", "dwMaximumWindowSize"};

      protected List<String> getFieldOrder() {
         return Arrays.asList(fieldOrder);
      }

      public int windowWidth() {
         return this.srWindow.width() + 1;
      }

      public int windowHeight() {
         return this.srWindow.height() + 1;
      }
   }

   public static class COORD extends Structure implements ByValue {
      public short X;
      public short Y;
      private static String[] fieldOrder = new String[]{"X", "Y"};

      public COORD() {
      }

      public COORD(short X, short Y) {
         this.X = X;
         this.Y = Y;
      }

      protected List<String> getFieldOrder() {
         return Arrays.asList(fieldOrder);
      }
   }

   public static class FOCUS_EVENT_RECORD extends Structure {
      public boolean bSetFocus;
      private static String[] fieldOrder = new String[]{"bSetFocus"};

      protected List<String> getFieldOrder() {
         return Arrays.asList(fieldOrder);
      }
   }

   public static class INPUT_RECORD extends Structure {
      public static final short KEY_EVENT = 1;
      public static final short MOUSE_EVENT = 2;
      public static final short WINDOW_BUFFER_SIZE_EVENT = 4;
      public static final short MENU_EVENT = 8;
      public static final short FOCUS_EVENT = 16;
      public short EventType;
      public Kernel32.INPUT_RECORD.EventUnion Event;
      private static String[] fieldOrder = new String[]{"EventType", "Event"};

      public void read() {
         this.readField("EventType");
         switch (this.EventType) {
            case 1:
               this.Event.setType(Kernel32.KEY_EVENT_RECORD.class);
               break;
            case 2:
               this.Event.setType(Kernel32.MOUSE_EVENT_RECORD.class);
               break;
            case 4:
               this.Event.setType(Kernel32.WINDOW_BUFFER_SIZE_RECORD.class);
               break;
            case 8:
               this.Event.setType(Kernel32.MENU_EVENT_RECORD.class);
               break;
            case 16:
               this.Event.setType(Kernel32.MENU_EVENT_RECORD.class);
         }

         super.read();
      }

      protected List<String> getFieldOrder() {
         return Arrays.asList(fieldOrder);
      }

      public static class EventUnion extends Union {
         public Kernel32.KEY_EVENT_RECORD KeyEvent;
         public Kernel32.MOUSE_EVENT_RECORD MouseEvent;
         public Kernel32.WINDOW_BUFFER_SIZE_RECORD WindowBufferSizeEvent;
         public Kernel32.MENU_EVENT_RECORD MenuEvent;
         public Kernel32.FOCUS_EVENT_RECORD FocusEvent;
      }
   }

   public static class KEY_EVENT_RECORD extends Structure {
      public boolean bKeyDown;
      public short wRepeatCount;
      public short wVirtualKeyCode;
      public short wVirtualScanCode;
      public Kernel32.UnionChar uChar;
      public int dwControlKeyState;
      private static String[] fieldOrder = new String[]{"bKeyDown", "wRepeatCount", "wVirtualKeyCode", "wVirtualScanCode", "uChar", "dwControlKeyState"};

      protected List<String> getFieldOrder() {
         return Arrays.asList(fieldOrder);
      }
   }

   public static class MENU_EVENT_RECORD extends Structure {
      public int dwCommandId;
      private static String[] fieldOrder = new String[]{"dwCommandId"};

      protected List<String> getFieldOrder() {
         return Arrays.asList(fieldOrder);
      }
   }

   public static class MOUSE_EVENT_RECORD extends Structure {
      public Kernel32.COORD dwMousePosition;
      public int dwButtonState;
      public int dwControlKeyState;
      public int dwEventFlags;
      private static String[] fieldOrder = new String[]{"dwMousePosition", "dwButtonState", "dwControlKeyState", "dwEventFlags"};

      protected List<String> getFieldOrder() {
         return Arrays.asList(fieldOrder);
      }
   }

   public static class SMALL_RECT extends Structure {
      public short Left;
      public short Top;
      public short Right;
      public short Bottom;
      private static String[] fieldOrder = new String[]{"Left", "Top", "Right", "Bottom"};

      public SMALL_RECT() {
      }

      public SMALL_RECT(Kernel32.SMALL_RECT org) {
         this(org.Top, org.Left, org.Bottom, org.Right);
      }

      public SMALL_RECT(short Top, short Left, short Bottom, short Right) {
         this.Top = Top;
         this.Left = Left;
         this.Bottom = Bottom;
         this.Right = Right;
      }

      protected List<String> getFieldOrder() {
         return Arrays.asList(fieldOrder);
      }

      public short width() {
         return (short)(this.Right - this.Left);
      }

      public short height() {
         return (short)(this.Bottom - this.Top);
      }
   }

   public static class UnionChar extends Union {
      public char UnicodeChar;
      public byte AsciiChar;

      public UnionChar() {
      }

      public UnionChar(char c) {
         this.setType(char.class);
         this.UnicodeChar = c;
      }

      public UnionChar(byte c) {
         this.setType(byte.class);
         this.AsciiChar = c;
      }

      public void set(char c) {
         this.setType(char.class);
         this.UnicodeChar = c;
      }

      public void set(byte c) {
         this.setType(byte.class);
         this.AsciiChar = c;
      }
   }

   public static class WINDOW_BUFFER_SIZE_RECORD extends Structure {
      public Kernel32.COORD dwSize;
      private static String[] fieldOrder = new String[]{"dwSize"};

      protected List<String> getFieldOrder() {
         return Arrays.asList(fieldOrder);
      }
   }
}
