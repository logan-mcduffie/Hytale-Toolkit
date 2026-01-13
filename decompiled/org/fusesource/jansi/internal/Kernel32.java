package org.fusesource.jansi.internal;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class Kernel32 {
   public static short FOREGROUND_BLUE;
   public static short FOREGROUND_GREEN;
   public static short FOREGROUND_RED;
   public static short FOREGROUND_INTENSITY;
   public static short BACKGROUND_BLUE;
   public static short BACKGROUND_GREEN;
   public static short BACKGROUND_RED;
   public static short BACKGROUND_INTENSITY;
   public static short COMMON_LVB_LEADING_BYTE;
   public static short COMMON_LVB_TRAILING_BYTE;
   public static short COMMON_LVB_GRID_HORIZONTAL;
   public static short COMMON_LVB_GRID_LVERTICAL;
   public static short COMMON_LVB_GRID_RVERTICAL;
   public static short COMMON_LVB_REVERSE_VIDEO;
   public static short COMMON_LVB_UNDERSCORE;
   public static int FORMAT_MESSAGE_FROM_SYSTEM;
   public static int STD_INPUT_HANDLE;
   public static int STD_OUTPUT_HANDLE;
   public static int STD_ERROR_HANDLE;
   public static int INVALID_HANDLE_VALUE;

   private static native void init();

   public static native long malloc(long var0);

   public static native void free(long var0);

   public static native int SetConsoleTextAttribute(long var0, short var2);

   public static native int WaitForSingleObject(long var0, int var2);

   public static native int CloseHandle(long var0);

   public static native int GetLastError();

   public static native int FormatMessageW(int var0, long var1, int var3, int var4, byte[] var5, int var6, long[] var7);

   public static native int GetConsoleScreenBufferInfo(long var0, Kernel32.CONSOLE_SCREEN_BUFFER_INFO var2);

   public static native long GetStdHandle(int var0);

   public static native int SetConsoleCursorPosition(long var0, Kernel32.COORD var2);

   public static native int FillConsoleOutputCharacterW(long var0, char var2, int var3, Kernel32.COORD var4, int[] var5);

   public static native int FillConsoleOutputAttribute(long var0, short var2, int var3, Kernel32.COORD var4, int[] var5);

   public static native int WriteConsoleW(long var0, char[] var2, int var3, int[] var4, long var5);

   public static native int GetConsoleMode(long var0, int[] var2);

   public static native int SetConsoleMode(long var0, int var2);

   public static native int _getch();

   public static native int SetConsoleTitle(String var0);

   public static native int GetConsoleOutputCP();

   public static native int SetConsoleOutputCP(int var0);

   public static native int ScrollConsoleScreenBuffer(
      long var0, Kernel32.SMALL_RECT var2, Kernel32.SMALL_RECT var3, Kernel32.COORD var4, Kernel32.CHAR_INFO var5
   );

   private static native int ReadConsoleInputW(long var0, long var2, int var4, int[] var5);

   private static native int PeekConsoleInputW(long var0, long var2, int var4, int[] var5);

   public static native int GetNumberOfConsoleInputEvents(long var0, int[] var2);

   public static native int FlushConsoleInputBuffer(long var0);

   public static Kernel32.INPUT_RECORD[] readConsoleInputHelper(long handle, int count, boolean peek) throws IOException {
      int[] length = new int[1];
      long inputRecordPtr = 0L;

      Kernel32.INPUT_RECORD[] records;
      try {
         inputRecordPtr = malloc(Kernel32.INPUT_RECORD.SIZEOF * count);
         if (inputRecordPtr == 0L) {
            throw new IOException("cannot allocate memory with JNI");
         }

         int res = peek ? PeekConsoleInputW(handle, inputRecordPtr, count, length) : ReadConsoleInputW(handle, inputRecordPtr, count, length);
         if (res == 0) {
            throw new IOException("ReadConsoleInputW failed: " + getLastErrorMessage());
         }

         if (length[0] > 0) {
            records = new Kernel32.INPUT_RECORD[length[0]];

            for (int i = 0; i < records.length; i++) {
               records[i] = new Kernel32.INPUT_RECORD();
               Kernel32.INPUT_RECORD.memmove(records[i], inputRecordPtr + i * Kernel32.INPUT_RECORD.SIZEOF, Kernel32.INPUT_RECORD.SIZEOF);
            }

            return records;
         }

         records = new Kernel32.INPUT_RECORD[0];
      } finally {
         if (inputRecordPtr != 0L) {
            free(inputRecordPtr);
         }
      }

      return records;
   }

   public static Kernel32.INPUT_RECORD[] readConsoleKeyInput(long handle, int count, boolean peek) throws IOException {
      Kernel32.INPUT_RECORD[] evts;
      int keyEvtCount;
      do {
         evts = readConsoleInputHelper(handle, count, peek);
         keyEvtCount = 0;

         for (Kernel32.INPUT_RECORD evt : evts) {
            if (evt.eventType == Kernel32.INPUT_RECORD.KEY_EVENT) {
               keyEvtCount++;
            }
         }
      } while (keyEvtCount <= 0);

      Kernel32.INPUT_RECORD[] res = new Kernel32.INPUT_RECORD[keyEvtCount];
      int i = 0;

      for (Kernel32.INPUT_RECORD evtx : evts) {
         if (evtx.eventType == Kernel32.INPUT_RECORD.KEY_EVENT) {
            res[i++] = evtx;
         }
      }

      return res;
   }

   public static String getLastErrorMessage() {
      int errorCode = GetLastError();
      return getErrorMessage(errorCode);
   }

   public static String getErrorMessage(int errorCode) {
      int bufferSize = 160;
      byte[] data = new byte[bufferSize];
      FormatMessageW(FORMAT_MESSAGE_FROM_SYSTEM, 0L, errorCode, 0, data, bufferSize, null);

      try {
         return new String(data, "UTF-16LE").trim();
      } catch (UnsupportedEncodingException var4) {
         throw new IllegalStateException(var4);
      }
   }

   static {
      if (JansiLoader.initialize()) {
         init();
      }
   }

   public static class CHAR_INFO {
      public static int SIZEOF;
      public short attributes;
      public char unicodeChar;

      private static native void init();

      static {
         JansiLoader.initialize();
         init();
      }
   }

   public static class CONSOLE_SCREEN_BUFFER_INFO {
      public static int SIZEOF;
      public Kernel32.COORD size = new Kernel32.COORD();
      public Kernel32.COORD cursorPosition = new Kernel32.COORD();
      public short attributes;
      public Kernel32.SMALL_RECT window = new Kernel32.SMALL_RECT();
      public Kernel32.COORD maximumWindowSize = new Kernel32.COORD();

      private static native void init();

      public int windowWidth() {
         return this.window.width() + 1;
      }

      public int windowHeight() {
         return this.window.height() + 1;
      }

      static {
         JansiLoader.initialize();
         init();
      }
   }

   public static class COORD {
      public static int SIZEOF;
      public short x;
      public short y;

      private static native void init();

      public Kernel32.COORD copy() {
         Kernel32.COORD rc = new Kernel32.COORD();
         rc.x = this.x;
         rc.y = this.y;
         return rc;
      }

      static {
         JansiLoader.initialize();
         init();
      }
   }

   public static class FOCUS_EVENT_RECORD {
      public static int SIZEOF;
      public boolean setFocus;

      private static native void init();

      static {
         JansiLoader.initialize();
         init();
      }
   }

   public static class INPUT_RECORD {
      public static int SIZEOF;
      public static short KEY_EVENT;
      public static short MOUSE_EVENT;
      public static short WINDOW_BUFFER_SIZE_EVENT;
      public static short FOCUS_EVENT;
      public static short MENU_EVENT;
      public short eventType;
      public Kernel32.KEY_EVENT_RECORD keyEvent = new Kernel32.KEY_EVENT_RECORD();
      public Kernel32.MOUSE_EVENT_RECORD mouseEvent = new Kernel32.MOUSE_EVENT_RECORD();
      public Kernel32.WINDOW_BUFFER_SIZE_RECORD windowBufferSizeEvent = new Kernel32.WINDOW_BUFFER_SIZE_RECORD();
      public Kernel32.MENU_EVENT_RECORD menuEvent = new Kernel32.MENU_EVENT_RECORD();
      public Kernel32.FOCUS_EVENT_RECORD focusEvent = new Kernel32.FOCUS_EVENT_RECORD();

      private static native void init();

      public static native void memmove(Kernel32.INPUT_RECORD var0, long var1, long var3);

      static {
         JansiLoader.initialize();
         init();
      }
   }

   public static class KEY_EVENT_RECORD {
      public static int SIZEOF;
      public static int CAPSLOCK_ON;
      public static int NUMLOCK_ON;
      public static int SCROLLLOCK_ON;
      public static int ENHANCED_KEY;
      public static int LEFT_ALT_PRESSED;
      public static int LEFT_CTRL_PRESSED;
      public static int RIGHT_ALT_PRESSED;
      public static int RIGHT_CTRL_PRESSED;
      public static int SHIFT_PRESSED;
      public boolean keyDown;
      public short repeatCount;
      public short keyCode;
      public short scanCode;
      public char uchar;
      public int controlKeyState;

      private static native void init();

      @Override
      public String toString() {
         return "KEY_EVENT_RECORD{keyDown="
            + this.keyDown
            + ", repeatCount="
            + this.repeatCount
            + ", keyCode="
            + this.keyCode
            + ", scanCode="
            + this.scanCode
            + ", uchar="
            + this.uchar
            + ", controlKeyState="
            + this.controlKeyState
            + '}';
      }

      static {
         JansiLoader.initialize();
         init();
      }
   }

   public static class MENU_EVENT_RECORD {
      public static int SIZEOF;
      public int commandId;

      private static native void init();

      static {
         JansiLoader.initialize();
         init();
      }
   }

   public static class MOUSE_EVENT_RECORD {
      public static int SIZEOF;
      public static int FROM_LEFT_1ST_BUTTON_PRESSED;
      public static int FROM_LEFT_2ND_BUTTON_PRESSED;
      public static int FROM_LEFT_3RD_BUTTON_PRESSED;
      public static int FROM_LEFT_4TH_BUTTON_PRESSED;
      public static int RIGHTMOST_BUTTON_PRESSED;
      public static int CAPSLOCK_ON;
      public static int NUMLOCK_ON;
      public static int SCROLLLOCK_ON;
      public static int ENHANCED_KEY;
      public static int LEFT_ALT_PRESSED;
      public static int LEFT_CTRL_PRESSED;
      public static int RIGHT_ALT_PRESSED;
      public static int RIGHT_CTRL_PRESSED;
      public static int SHIFT_PRESSED;
      public static int DOUBLE_CLICK;
      public static int MOUSE_HWHEELED;
      public static int MOUSE_MOVED;
      public static int MOUSE_WHEELED;
      public Kernel32.COORD mousePosition = new Kernel32.COORD();
      public int buttonState;
      public int controlKeyState;
      public int eventFlags;

      private static native void init();

      @Override
      public String toString() {
         return "MOUSE_EVENT_RECORD{mousePosition="
            + this.mousePosition
            + ", buttonState="
            + this.buttonState
            + ", controlKeyState="
            + this.controlKeyState
            + ", eventFlags="
            + this.eventFlags
            + '}';
      }

      static {
         JansiLoader.initialize();
         init();
      }
   }

   public static class SMALL_RECT {
      public static int SIZEOF;
      public short left;
      public short top;
      public short right;
      public short bottom;

      private static native void init();

      public short width() {
         return (short)(this.right - this.left);
      }

      public short height() {
         return (short)(this.bottom - this.top);
      }

      public Kernel32.SMALL_RECT copy() {
         Kernel32.SMALL_RECT rc = new Kernel32.SMALL_RECT();
         rc.left = this.left;
         rc.top = this.top;
         rc.right = this.right;
         rc.bottom = this.bottom;
         return rc;
      }

      static {
         JansiLoader.initialize();
         init();
      }
   }

   public static class WINDOW_BUFFER_SIZE_RECORD {
      public static int SIZEOF;
      public Kernel32.COORD size = new Kernel32.COORD();

      private static native void init();

      @Override
      public String toString() {
         return "WINDOW_BUFFER_SIZE_RECORD{size=" + this.size + '}';
      }

      static {
         JansiLoader.initialize();
         init();
      }
   }
}
