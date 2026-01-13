package org.jline.terminal.impl.ffm;

import java.io.IOException;
import java.lang.foreign.AddressLayout;
import java.lang.foreign.Arena;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.GroupLayout;
import java.lang.foreign.Linker;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SymbolLookup;
import java.lang.foreign.ValueLayout;
import java.lang.foreign.MemoryLayout.PathElement;
import java.lang.foreign.ValueLayout.OfBoolean;
import java.lang.foreign.ValueLayout.OfByte;
import java.lang.foreign.ValueLayout.OfChar;
import java.lang.foreign.ValueLayout.OfDouble;
import java.lang.foreign.ValueLayout.OfFloat;
import java.lang.foreign.ValueLayout.OfInt;
import java.lang.foreign.ValueLayout.OfLong;
import java.lang.foreign.ValueLayout.OfShort;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.VarHandle;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

final class Kernel32 {
   public static final int FORMAT_MESSAGE_FROM_SYSTEM = 4096;
   public static final long INVALID_HANDLE_VALUE = -1L;
   public static final int STD_INPUT_HANDLE = -10;
   public static final int STD_OUTPUT_HANDLE = -11;
   public static final int STD_ERROR_HANDLE = -12;
   public static final int ENABLE_PROCESSED_INPUT = 1;
   public static final int ENABLE_LINE_INPUT = 2;
   public static final int ENABLE_ECHO_INPUT = 4;
   public static final int ENABLE_WINDOW_INPUT = 8;
   public static final int ENABLE_MOUSE_INPUT = 16;
   public static final int ENABLE_INSERT_MODE = 32;
   public static final int ENABLE_QUICK_EDIT_MODE = 64;
   public static final int ENABLE_EXTENDED_FLAGS = 128;
   public static final int RIGHT_ALT_PRESSED = 1;
   public static final int LEFT_ALT_PRESSED = 2;
   public static final int RIGHT_CTRL_PRESSED = 4;
   public static final int LEFT_CTRL_PRESSED = 8;
   public static final int SHIFT_PRESSED = 16;
   public static final int FOREGROUND_BLUE = 1;
   public static final int FOREGROUND_GREEN = 2;
   public static final int FOREGROUND_RED = 4;
   public static final int FOREGROUND_INTENSITY = 8;
   public static final int BACKGROUND_BLUE = 16;
   public static final int BACKGROUND_GREEN = 32;
   public static final int BACKGROUND_RED = 64;
   public static final int BACKGROUND_INTENSITY = 128;
   public static final int FROM_LEFT_1ST_BUTTON_PRESSED = 1;
   public static final int RIGHTMOST_BUTTON_PRESSED = 2;
   public static final int FROM_LEFT_2ND_BUTTON_PRESSED = 4;
   public static final int FROM_LEFT_3RD_BUTTON_PRESSED = 8;
   public static final int FROM_LEFT_4TH_BUTTON_PRESSED = 16;
   public static final int MOUSE_MOVED = 1;
   public static final int DOUBLE_CLICK = 2;
   public static final int MOUSE_WHEELED = 4;
   public static final int MOUSE_HWHEELED = 8;
   public static final short KEY_EVENT = 1;
   public static final short MOUSE_EVENT = 2;
   public static final short WINDOW_BUFFER_SIZE_EVENT = 4;
   public static final short MENU_EVENT = 8;
   public static final short FOCUS_EVENT = 16;
   private static final SymbolLookup SYMBOL_LOOKUP = SymbolLookup.loaderLookup();
   static final OfBoolean C_BOOL$LAYOUT = ValueLayout.JAVA_BOOLEAN;
   static final OfByte C_CHAR$LAYOUT = ValueLayout.JAVA_BYTE;
   static final OfChar C_WCHAR$LAYOUT = ValueLayout.JAVA_CHAR;
   static final OfShort C_SHORT$LAYOUT = ValueLayout.JAVA_SHORT;
   static final OfShort C_WORD$LAYOUT = ValueLayout.JAVA_SHORT;
   static final OfInt C_DWORD$LAYOUT = ValueLayout.JAVA_INT;
   static final OfInt C_INT$LAYOUT = ValueLayout.JAVA_INT;
   static final OfLong C_LONG$LAYOUT = ValueLayout.JAVA_LONG;
   static final OfLong C_LONG_LONG$LAYOUT = ValueLayout.JAVA_LONG;
   static final OfFloat C_FLOAT$LAYOUT = ValueLayout.JAVA_FLOAT;
   static final OfDouble C_DOUBLE$LAYOUT = ValueLayout.JAVA_DOUBLE;
   static final AddressLayout C_POINTER$LAYOUT = ValueLayout.ADDRESS;
   static final MethodHandle WaitForSingleObject$MH = downcallHandle("WaitForSingleObject", FunctionDescriptor.of(C_INT$LAYOUT, C_POINTER$LAYOUT, C_INT$LAYOUT));
   static final MethodHandle GetStdHandle$MH = downcallHandle("GetStdHandle", FunctionDescriptor.of(C_POINTER$LAYOUT, C_INT$LAYOUT));
   static final MethodHandle FormatMessageW$MH = downcallHandle(
      "FormatMessageW",
      FunctionDescriptor.of(C_INT$LAYOUT, C_INT$LAYOUT, C_POINTER$LAYOUT, C_INT$LAYOUT, C_INT$LAYOUT, C_POINTER$LAYOUT, C_INT$LAYOUT, C_POINTER$LAYOUT)
   );
   static final MethodHandle SetConsoleTextAttribute$MH = downcallHandle(
      "SetConsoleTextAttribute", FunctionDescriptor.of(C_INT$LAYOUT, C_POINTER$LAYOUT, C_SHORT$LAYOUT)
   );
   static final MethodHandle SetConsoleMode$MH = downcallHandle("SetConsoleMode", FunctionDescriptor.of(C_INT$LAYOUT, C_POINTER$LAYOUT, C_INT$LAYOUT));
   static final MethodHandle GetConsoleMode$MH = downcallHandle("GetConsoleMode", FunctionDescriptor.of(C_INT$LAYOUT, C_POINTER$LAYOUT, C_POINTER$LAYOUT));
   static final MethodHandle SetConsoleTitleW$MH = downcallHandle("SetConsoleTitleW", FunctionDescriptor.of(C_INT$LAYOUT, C_POINTER$LAYOUT));
   static final MethodHandle SetConsoleCursorPosition$MH = downcallHandle(
      "SetConsoleCursorPosition", FunctionDescriptor.of(C_INT$LAYOUT, C_POINTER$LAYOUT, Kernel32.COORD.LAYOUT)
   );
   static final MethodHandle FillConsoleOutputCharacterW$MH = downcallHandle(
      "FillConsoleOutputCharacterW",
      FunctionDescriptor.of(C_INT$LAYOUT, C_POINTER$LAYOUT, C_WCHAR$LAYOUT, C_INT$LAYOUT, Kernel32.COORD.LAYOUT, C_POINTER$LAYOUT)
   );
   static final MethodHandle FillConsoleOutputAttribute$MH = downcallHandle(
      "FillConsoleOutputAttribute",
      FunctionDescriptor.of(C_INT$LAYOUT, C_POINTER$LAYOUT, C_SHORT$LAYOUT, C_INT$LAYOUT, Kernel32.COORD.LAYOUT, C_POINTER$LAYOUT)
   );
   static final MethodHandle WriteConsoleW$MH = downcallHandle(
      "WriteConsoleW", FunctionDescriptor.of(C_INT$LAYOUT, C_POINTER$LAYOUT, C_POINTER$LAYOUT, C_INT$LAYOUT, C_POINTER$LAYOUT, C_POINTER$LAYOUT)
   );
   static final MethodHandle ReadConsoleInputW$MH = downcallHandle(
      "ReadConsoleInputW", FunctionDescriptor.of(C_INT$LAYOUT, C_POINTER$LAYOUT, C_POINTER$LAYOUT, C_INT$LAYOUT, C_POINTER$LAYOUT)
   );
   static final MethodHandle PeekConsoleInputW$MH = downcallHandle(
      "PeekConsoleInputW", FunctionDescriptor.of(C_INT$LAYOUT, C_POINTER$LAYOUT, C_POINTER$LAYOUT, C_INT$LAYOUT, C_POINTER$LAYOUT)
   );
   static final MethodHandle GetConsoleScreenBufferInfo$MH = downcallHandle(
      "GetConsoleScreenBufferInfo", FunctionDescriptor.of(C_INT$LAYOUT, C_POINTER$LAYOUT, C_POINTER$LAYOUT)
   );
   static final MethodHandle ScrollConsoleScreenBufferW$MH = downcallHandle(
      "ScrollConsoleScreenBufferW",
      FunctionDescriptor.of(C_INT$LAYOUT, C_POINTER$LAYOUT, C_POINTER$LAYOUT, C_POINTER$LAYOUT, Kernel32.COORD.LAYOUT, C_POINTER$LAYOUT)
   );
   static final MethodHandle GetLastError$MH = downcallHandle("GetLastError", FunctionDescriptor.of(C_INT$LAYOUT));
   static final MethodHandle GetFileType$MH = downcallHandle("GetFileType", FunctionDescriptor.of(C_INT$LAYOUT, C_POINTER$LAYOUT));
   static final MethodHandle _get_osfhandle$MH = downcallHandle("_get_osfhandle", FunctionDescriptor.of(C_POINTER$LAYOUT, C_INT$LAYOUT));

   public static int WaitForSingleObject(MemorySegment hHandle, int dwMilliseconds) {
      MethodHandle mh$ = requireNonNull(WaitForSingleObject$MH, "WaitForSingleObject");

      try {
         return (int)mh$.invokeExact((MemorySegment)hHandle, (int)dwMilliseconds);
      } catch (Throwable var4) {
         throw new AssertionError("should not reach here", var4);
      }
   }

   public static MemorySegment GetStdHandle(int nStdHandle) {
      MethodHandle mh$ = requireNonNull(GetStdHandle$MH, "GetStdHandle");

      try {
         return (MemorySegment)mh$.invokeExact((int)nStdHandle);
      } catch (Throwable var3) {
         throw new AssertionError("should not reach here", var3);
      }
   }

   public static int FormatMessageW(
      int dwFlags, MemorySegment lpSource, int dwMessageId, int dwLanguageId, MemorySegment lpBuffer, int nSize, MemorySegment Arguments
   ) {
      MethodHandle mh$ = requireNonNull(FormatMessageW$MH, "FormatMessageW");

      try {
         return (int)mh$.invokeExact(
            (int)dwFlags, (MemorySegment)lpSource, (int)dwMessageId, (int)dwLanguageId, (MemorySegment)lpBuffer, (int)nSize, (MemorySegment)Arguments
         );
      } catch (Throwable var9) {
         throw new AssertionError("should not reach here", var9);
      }
   }

   public static int SetConsoleTextAttribute(MemorySegment hConsoleOutput, short wAttributes) {
      MethodHandle mh$ = requireNonNull(SetConsoleTextAttribute$MH, "SetConsoleTextAttribute");

      try {
         return (int)mh$.invokeExact((MemorySegment)hConsoleOutput, (short)wAttributes);
      } catch (Throwable var4) {
         throw new AssertionError("should not reach here", var4);
      }
   }

   public static int SetConsoleMode(MemorySegment hConsoleHandle, int dwMode) {
      MethodHandle mh$ = requireNonNull(SetConsoleMode$MH, "SetConsoleMode");

      try {
         return (int)mh$.invokeExact((MemorySegment)hConsoleHandle, (int)dwMode);
      } catch (Throwable var4) {
         throw new AssertionError("should not reach here", var4);
      }
   }

   public static int GetConsoleMode(MemorySegment hConsoleHandle, MemorySegment lpMode) {
      MethodHandle mh$ = requireNonNull(GetConsoleMode$MH, "GetConsoleMode");

      try {
         return (int)mh$.invokeExact((MemorySegment)hConsoleHandle, (MemorySegment)lpMode);
      } catch (Throwable var4) {
         throw new AssertionError("should not reach here", var4);
      }
   }

   public static int SetConsoleTitleW(MemorySegment lpConsoleTitle) {
      MethodHandle mh$ = requireNonNull(SetConsoleTitleW$MH, "SetConsoleTitleW");

      try {
         return (int)mh$.invokeExact((MemorySegment)lpConsoleTitle);
      } catch (Throwable var3) {
         throw new AssertionError("should not reach here", var3);
      }
   }

   public static int SetConsoleCursorPosition(MemorySegment hConsoleOutput, Kernel32.COORD dwCursorPosition) {
      MethodHandle mh$ = requireNonNull(SetConsoleCursorPosition$MH, "SetConsoleCursorPosition");

      try {
         return (int)mh$.invokeExact((MemorySegment)hConsoleOutput, (MemorySegment)dwCursorPosition.seg);
      } catch (Throwable var4) {
         throw new AssertionError("should not reach here", var4);
      }
   }

   public static int FillConsoleOutputCharacterW(
      MemorySegment hConsoleOutput, char cCharacter, int nLength, Kernel32.COORD dwWriteCoord, MemorySegment lpNumberOfCharsWritten
   ) {
      MethodHandle mh$ = requireNonNull(FillConsoleOutputCharacterW$MH, "FillConsoleOutputCharacterW");

      try {
         return (int)mh$.invokeExact(
            (MemorySegment)hConsoleOutput, (char)cCharacter, (int)nLength, (MemorySegment)dwWriteCoord.seg, (MemorySegment)lpNumberOfCharsWritten
         );
      } catch (Throwable var7) {
         throw new AssertionError("should not reach here", var7);
      }
   }

   public static int FillConsoleOutputAttribute(
      MemorySegment hConsoleOutput, short wAttribute, int nLength, Kernel32.COORD dwWriteCoord, MemorySegment lpNumberOfAttrsWritten
   ) {
      MethodHandle mh$ = requireNonNull(FillConsoleOutputAttribute$MH, "FillConsoleOutputAttribute");

      try {
         return (int)mh$.invokeExact(
            (MemorySegment)hConsoleOutput, (short)wAttribute, (int)nLength, (MemorySegment)dwWriteCoord.seg, (MemorySegment)lpNumberOfAttrsWritten
         );
      } catch (Throwable var7) {
         throw new AssertionError("should not reach here", var7);
      }
   }

   public static int WriteConsoleW(
      MemorySegment hConsoleOutput, MemorySegment lpBuffer, int nNumberOfCharsToWrite, MemorySegment lpNumberOfCharsWritten, MemorySegment lpReserved
   ) {
      MethodHandle mh$ = requireNonNull(WriteConsoleW$MH, "WriteConsoleW");

      try {
         return (int)mh$.invokeExact(
            (MemorySegment)hConsoleOutput,
            (MemorySegment)lpBuffer,
            (int)nNumberOfCharsToWrite,
            (MemorySegment)lpNumberOfCharsWritten,
            (MemorySegment)lpReserved
         );
      } catch (Throwable var7) {
         throw new AssertionError("should not reach here", var7);
      }
   }

   public static int ReadConsoleInputW(MemorySegment hConsoleInput, MemorySegment lpBuffer, int nLength, MemorySegment lpNumberOfEventsRead) {
      MethodHandle mh$ = requireNonNull(ReadConsoleInputW$MH, "ReadConsoleInputW");

      try {
         return (int)mh$.invokeExact((MemorySegment)hConsoleInput, (MemorySegment)lpBuffer, (int)nLength, (MemorySegment)lpNumberOfEventsRead);
      } catch (Throwable var6) {
         throw new AssertionError("should not reach here", var6);
      }
   }

   public static int PeekConsoleInputW(MemorySegment hConsoleInput, MemorySegment lpBuffer, int nLength, MemorySegment lpNumberOfEventsRead) {
      MethodHandle mh$ = requireNonNull(PeekConsoleInputW$MH, "PeekConsoleInputW");

      try {
         return (int)mh$.invokeExact((MemorySegment)hConsoleInput, (MemorySegment)lpBuffer, (int)nLength, (MemorySegment)lpNumberOfEventsRead);
      } catch (Throwable var6) {
         throw new AssertionError("should not reach here", var6);
      }
   }

   public static int GetConsoleScreenBufferInfo(MemorySegment hConsoleOutput, Kernel32.CONSOLE_SCREEN_BUFFER_INFO lpConsoleScreenBufferInfo) {
      MethodHandle mh$ = requireNonNull(GetConsoleScreenBufferInfo$MH, "GetConsoleScreenBufferInfo");

      try {
         return (int)mh$.invokeExact((MemorySegment)hConsoleOutput, (MemorySegment)lpConsoleScreenBufferInfo.seg);
      } catch (Throwable var4) {
         throw new AssertionError("should not reach here", var4);
      }
   }

   public static int ScrollConsoleScreenBuffer(
      MemorySegment hConsoleOutput,
      Kernel32.SMALL_RECT lpScrollRectangle,
      Kernel32.SMALL_RECT lpClipRectangle,
      Kernel32.COORD dwDestinationOrigin,
      Kernel32.CHAR_INFO lpFill
   ) {
      MethodHandle mh$ = requireNonNull(ScrollConsoleScreenBufferW$MH, "ScrollConsoleScreenBuffer");

      try {
         return (int)mh$.invokeExact(
            (MemorySegment)hConsoleOutput,
            (MemorySegment)lpScrollRectangle.seg,
            (MemorySegment)lpClipRectangle.seg,
            (MemorySegment)dwDestinationOrigin.seg,
            (MemorySegment)lpFill.seg
         );
      } catch (Throwable var7) {
         throw new AssertionError("should not reach here", var7);
      }
   }

   public static int GetLastError() {
      MethodHandle mh$ = requireNonNull(GetLastError$MH, "GetLastError");

      try {
         return (int)mh$.invokeExact();
      } catch (Throwable var2) {
         throw new AssertionError("should not reach here", var2);
      }
   }

   public static int GetFileType(MemorySegment hFile) {
      MethodHandle mh$ = requireNonNull(GetFileType$MH, "GetFileType");

      try {
         return (int)mh$.invokeExact((MemorySegment)hFile);
      } catch (Throwable var3) {
         throw new AssertionError("should not reach here", var3);
      }
   }

   public static MemorySegment _get_osfhandle(int fd) {
      MethodHandle mh$ = requireNonNull(_get_osfhandle$MH, "_get_osfhandle");

      try {
         return (MemorySegment)mh$.invokeExact((int)fd);
      } catch (Throwable var3) {
         throw new AssertionError("should not reach here", var3);
      }
   }

   public static Kernel32.INPUT_RECORD[] readConsoleInputHelper(MemorySegment handle, int count, boolean peek) throws IOException {
      return readConsoleInputHelper(Arena.ofAuto(), handle, count, peek);
   }

   public static Kernel32.INPUT_RECORD[] readConsoleInputHelper(Arena arena, MemorySegment handle, int count, boolean peek) throws IOException {
      MemorySegment inputRecordPtr = arena.allocate(Kernel32.INPUT_RECORD.LAYOUT, count);
      MemorySegment length = arena.allocate(ValueLayout.JAVA_INT, 1L);
      int res = peek ? PeekConsoleInputW(handle, inputRecordPtr, count, length) : ReadConsoleInputW(handle, inputRecordPtr, count, length);
      if (res == 0) {
         throw new IOException("ReadConsoleInputW failed: " + getLastErrorMessage());
      } else {
         int len = length.get(ValueLayout.JAVA_INT, 0L);
         return inputRecordPtr.elements(Kernel32.INPUT_RECORD.LAYOUT).map(Kernel32.INPUT_RECORD::new).limit(len).toArray(Kernel32.INPUT_RECORD[]::new);
      }
   }

   public static String getLastErrorMessage() {
      int errorCode = GetLastError();
      return getErrorMessage(errorCode);
   }

   public static String getErrorMessage(int errorCode) {
      int bufferSize = 160;

      String var4;
      try (Arena arena = Arena.ofConfined()) {
         MemorySegment data = arena.allocate(bufferSize);
         FormatMessageW(4096, MemorySegment.NULL, errorCode, 0, data, bufferSize, MemorySegment.NULL);
         var4 = new String(data.toArray(ValueLayout.JAVA_BYTE), StandardCharsets.UTF_16LE).trim();
      }

      return var4;
   }

   static MethodHandle downcallHandle(String name, FunctionDescriptor fdesc) {
      return SYMBOL_LOOKUP.find(name).map(addr -> Linker.nativeLinker().downcallHandle(addr, fdesc)).orElse(null);
   }

   static <T> T requireNonNull(T obj, String symbolName) {
      if (obj == null) {
         throw new UnsatisfiedLinkError("unresolved symbol: " + symbolName);
      } else {
         return obj;
      }
   }

   static VarHandle varHandle(MemoryLayout layout, String name) {
      return FfmTerminalProvider.lookupVarHandle(layout, PathElement.groupElement(name));
   }

   static VarHandle varHandle(MemoryLayout layout, String e1, String name) {
      return FfmTerminalProvider.lookupVarHandle(layout, PathElement.groupElement(e1), PathElement.groupElement(name));
   }

   static long byteOffset(MemoryLayout layout, String name) {
      return layout.byteOffset(PathElement.groupElement(name));
   }

   static {
      System.loadLibrary("msvcrt");
      System.loadLibrary("Kernel32");
   }

   public static final class CHAR_INFO {
      static final GroupLayout LAYOUT = MemoryLayout.structLayout(
         MemoryLayout.unionLayout(Kernel32.C_WCHAR$LAYOUT.withName("UnicodeChar"), Kernel32.C_CHAR$LAYOUT.withName("AsciiChar")).withName("Char"),
         Kernel32.C_WORD$LAYOUT.withName("Attributes")
      );
      static final VarHandle UnicodeChar$VH = Kernel32.varHandle(LAYOUT, "Char", "UnicodeChar");
      static final VarHandle Attributes$VH = Kernel32.varHandle(LAYOUT, "Attributes");
      final MemorySegment seg;

      public CHAR_INFO() {
         this(Arena.ofAuto());
      }

      public CHAR_INFO(Arena arena) {
         this(arena.allocate(LAYOUT));
      }

      public CHAR_INFO(Arena arena, char c, short a) {
         this(arena);
         UnicodeChar$VH.set((MemorySegment)this.seg, (char)c);
         Attributes$VH.set((MemorySegment)this.seg, (short)a);
      }

      public CHAR_INFO(MemorySegment seg) {
         this.seg = seg;
      }

      public char unicodeChar() {
         return (char)UnicodeChar$VH.get((MemorySegment)this.seg);
      }
   }

   public static final class CONSOLE_SCREEN_BUFFER_INFO {
      static final GroupLayout LAYOUT = MemoryLayout.structLayout(
         Kernel32.COORD.LAYOUT.withName("dwSize"),
         Kernel32.COORD.LAYOUT.withName("dwCursorPosition"),
         Kernel32.C_WORD$LAYOUT.withName("wAttributes"),
         Kernel32.SMALL_RECT.LAYOUT.withName("srWindow"),
         Kernel32.COORD.LAYOUT.withName("dwMaximumWindowSize")
      );
      static final long dwSize$OFFSET = Kernel32.byteOffset(LAYOUT, "dwSize");
      static final long dwCursorPosition$OFFSET = Kernel32.byteOffset(LAYOUT, "dwCursorPosition");
      static final VarHandle wAttributes$VH = Kernel32.varHandle(LAYOUT, "wAttributes");
      static final long srWindow$OFFSET = Kernel32.byteOffset(LAYOUT, "srWindow");
      private final MemorySegment seg;

      public CONSOLE_SCREEN_BUFFER_INFO() {
         this(Arena.ofAuto());
      }

      public CONSOLE_SCREEN_BUFFER_INFO(Arena arena) {
         this(arena.allocate(LAYOUT));
      }

      public CONSOLE_SCREEN_BUFFER_INFO(MemorySegment seg) {
         this.seg = seg;
      }

      public Kernel32.COORD size() {
         return new Kernel32.COORD(this.seg, dwSize$OFFSET);
      }

      public Kernel32.COORD cursorPosition() {
         return new Kernel32.COORD(this.seg, dwCursorPosition$OFFSET);
      }

      public short attributes() {
         return (short)wAttributes$VH.get((MemorySegment)this.seg);
      }

      public Kernel32.SMALL_RECT window() {
         return new Kernel32.SMALL_RECT(this.seg, srWindow$OFFSET);
      }

      public int windowWidth() {
         return this.window().width() + 1;
      }

      public int windowHeight() {
         return this.window().height() + 1;
      }

      public void attributes(short attr) {
         wAttributes$VH.set((MemorySegment)this.seg, (short)attr);
      }
   }

   public static final class COORD {
      static final GroupLayout LAYOUT = MemoryLayout.structLayout(Kernel32.C_SHORT$LAYOUT.withName("x"), Kernel32.C_SHORT$LAYOUT.withName("y"));
      static final VarHandle x$VH = Kernel32.varHandle(LAYOUT, "x");
      static final VarHandle y$VH = Kernel32.varHandle(LAYOUT, "y");
      private final MemorySegment seg;

      public COORD() {
         this(Arena.ofAuto());
      }

      public COORD(Arena arena) {
         this(arena.allocate(LAYOUT));
      }

      public COORD(Arena arena, short x, short y) {
         this(arena.allocate(LAYOUT));
         this.x(x);
         this.y(y);
      }

      public COORD(MemorySegment seg) {
         this.seg = seg;
      }

      public COORD(MemorySegment seg, long offset) {
         this.seg = Objects.requireNonNull(seg).asSlice(offset, LAYOUT.byteSize());
      }

      public short x() {
         return (short)x$VH.get((MemorySegment)this.seg);
      }

      public void x(short x) {
         x$VH.set((MemorySegment)this.seg, (short)x);
      }

      public short y() {
         return (short)y$VH.get((MemorySegment)this.seg);
      }

      public void y(short y) {
         y$VH.set((MemorySegment)this.seg, (short)y);
      }

      public Kernel32.COORD copy(Arena arena) {
         return new Kernel32.COORD(arena.allocate(LAYOUT).copyFrom(this.seg));
      }
   }

   public static final class FOCUS_EVENT_RECORD {
      static final GroupLayout LAYOUT = MemoryLayout.structLayout(Kernel32.C_INT$LAYOUT.withName("bSetFocus"));
      static final VarHandle SET_FOCUS = Kernel32.varHandle(LAYOUT, "bSetFocus");
      private final MemorySegment seg;

      public FOCUS_EVENT_RECORD() {
         this(Arena.ofAuto());
      }

      public FOCUS_EVENT_RECORD(Arena arena) {
         this(arena.allocate(LAYOUT));
      }

      public FOCUS_EVENT_RECORD(MemorySegment seg) {
         this.seg = Objects.requireNonNull(seg);
      }

      public FOCUS_EVENT_RECORD(MemorySegment seg, long offset) {
         this.seg = Objects.requireNonNull(seg).asSlice(offset, LAYOUT.byteSize());
      }

      public boolean setFocus() {
         return (int)SET_FOCUS.get((MemorySegment)this.seg) != 0;
      }

      public void setFocus(boolean setFocus) {
         SET_FOCUS.set((MemorySegment)this.seg, (int)(setFocus ? 1 : 0));
      }
   }

   public static final class INPUT_RECORD {
      static final MemoryLayout LAYOUT = MemoryLayout.structLayout(
         ValueLayout.JAVA_SHORT.withName("EventType"),
         ValueLayout.JAVA_SHORT,
         MemoryLayout.unionLayout(
               Kernel32.KEY_EVENT_RECORD.LAYOUT.withName("KeyEvent"),
               Kernel32.MOUSE_EVENT_RECORD.LAYOUT.withName("MouseEvent"),
               Kernel32.WINDOW_BUFFER_SIZE_RECORD.LAYOUT.withName("WindowBufferSizeEvent"),
               Kernel32.MENU_EVENT_RECORD.LAYOUT.withName("MenuEvent"),
               Kernel32.FOCUS_EVENT_RECORD.LAYOUT.withName("FocusEvent")
            )
            .withName("Event")
      );
      static final VarHandle EventType$VH = Kernel32.varHandle(LAYOUT, "EventType");
      static final long Event$OFFSET = Kernel32.byteOffset(LAYOUT, "Event");
      private final MemorySegment seg;

      public INPUT_RECORD() {
         this(Arena.ofAuto());
      }

      public INPUT_RECORD(Arena arena) {
         this(arena.allocate(LAYOUT));
      }

      public INPUT_RECORD(MemorySegment seg) {
         this.seg = seg;
      }

      public short eventType() {
         return (short)EventType$VH.get((MemorySegment)this.seg);
      }

      public Kernel32.KEY_EVENT_RECORD keyEvent() {
         return new Kernel32.KEY_EVENT_RECORD(this.seg, Event$OFFSET);
      }

      public Kernel32.MOUSE_EVENT_RECORD mouseEvent() {
         return new Kernel32.MOUSE_EVENT_RECORD(this.seg, Event$OFFSET);
      }

      public Kernel32.FOCUS_EVENT_RECORD focusEvent() {
         return new Kernel32.FOCUS_EVENT_RECORD(this.seg, Event$OFFSET);
      }
   }

   public static final class KEY_EVENT_RECORD {
      static final MemoryLayout LAYOUT = MemoryLayout.structLayout(
         ValueLayout.JAVA_INT.withName("bKeyDown"),
         ValueLayout.JAVA_SHORT.withName("wRepeatCount"),
         ValueLayout.JAVA_SHORT.withName("wVirtualKeyCode"),
         ValueLayout.JAVA_SHORT.withName("wVirtualScanCode"),
         MemoryLayout.unionLayout(ValueLayout.JAVA_CHAR.withName("UnicodeChar"), ValueLayout.JAVA_BYTE.withName("AsciiChar")).withName("uChar"),
         ValueLayout.JAVA_INT.withName("dwControlKeyState")
      );
      static final VarHandle bKeyDown$VH = Kernel32.varHandle(LAYOUT, "bKeyDown");
      static final VarHandle wRepeatCount$VH = Kernel32.varHandle(LAYOUT, "wRepeatCount");
      static final VarHandle wVirtualKeyCode$VH = Kernel32.varHandle(LAYOUT, "wVirtualKeyCode");
      static final VarHandle wVirtualScanCode$VH = Kernel32.varHandle(LAYOUT, "wVirtualScanCode");
      static final VarHandle UnicodeChar$VH = Kernel32.varHandle(LAYOUT, "uChar", "UnicodeChar");
      static final VarHandle AsciiChar$VH = Kernel32.varHandle(LAYOUT, "uChar", "AsciiChar");
      static final VarHandle dwControlKeyState$VH = Kernel32.varHandle(LAYOUT, "dwControlKeyState");
      final MemorySegment seg;

      public KEY_EVENT_RECORD() {
         this(Arena.ofAuto());
      }

      public KEY_EVENT_RECORD(Arena arena) {
         this(arena.allocate(LAYOUT));
      }

      public KEY_EVENT_RECORD(MemorySegment seg) {
         this.seg = seg;
      }

      public KEY_EVENT_RECORD(MemorySegment seg, long offset) {
         this.seg = Objects.requireNonNull(seg).asSlice(offset, LAYOUT.byteSize());
      }

      public boolean keyDown() {
         return (int)bKeyDown$VH.get((MemorySegment)this.seg) != 0;
      }

      public int repeatCount() {
         return (int)wRepeatCount$VH.get((MemorySegment)this.seg);
      }

      public short keyCode() {
         return (short)wVirtualKeyCode$VH.get((MemorySegment)this.seg);
      }

      public short scanCode() {
         return (short)wVirtualScanCode$VH.get((MemorySegment)this.seg);
      }

      public char uchar() {
         return (char)UnicodeChar$VH.get((MemorySegment)this.seg);
      }

      public int controlKeyState() {
         return (int)dwControlKeyState$VH.get((MemorySegment)this.seg);
      }

      @Override
      public String toString() {
         return "KEY_EVENT_RECORD{keyDown="
            + this.keyDown()
            + ", repeatCount="
            + this.repeatCount()
            + ", keyCode="
            + this.keyCode()
            + ", scanCode="
            + this.scanCode()
            + ", uchar="
            + this.uchar()
            + ", controlKeyState="
            + this.controlKeyState()
            + "}";
      }
   }

   public static final class MENU_EVENT_RECORD {
      static final GroupLayout LAYOUT = MemoryLayout.structLayout(Kernel32.C_DWORD$LAYOUT.withName("dwCommandId"));
      static final VarHandle COMMAND_ID = Kernel32.varHandle(LAYOUT, "dwCommandId");
      private final MemorySegment seg;

      public MENU_EVENT_RECORD() {
         this(Arena.ofAuto());
      }

      public MENU_EVENT_RECORD(Arena arena) {
         this(arena.allocate(LAYOUT));
      }

      public MENU_EVENT_RECORD(MemorySegment seg) {
         this.seg = seg;
      }

      public int commandId() {
         return (int)COMMAND_ID.get((MemorySegment)this.seg);
      }

      public void commandId(int commandId) {
         COMMAND_ID.set((MemorySegment)this.seg, (int)commandId);
      }
   }

   public static final class MOUSE_EVENT_RECORD {
      static final MemoryLayout LAYOUT = MemoryLayout.structLayout(
         Kernel32.COORD.LAYOUT.withName("dwMousePosition"),
         Kernel32.C_DWORD$LAYOUT.withName("dwButtonState"),
         Kernel32.C_DWORD$LAYOUT.withName("dwControlKeyState"),
         Kernel32.C_DWORD$LAYOUT.withName("dwEventFlags")
      );
      static final long MOUSE_POSITION_OFFSET = Kernel32.byteOffset(LAYOUT, "dwMousePosition");
      static final VarHandle BUTTON_STATE = Kernel32.varHandle(LAYOUT, "dwButtonState");
      static final VarHandle CONTROL_KEY_STATE = Kernel32.varHandle(LAYOUT, "dwControlKeyState");
      static final VarHandle EVENT_FLAGS = Kernel32.varHandle(LAYOUT, "dwEventFlags");
      private final MemorySegment seg;

      public MOUSE_EVENT_RECORD() {
         this(Arena.ofAuto());
      }

      public MOUSE_EVENT_RECORD(Arena arena) {
         this(arena.allocate(LAYOUT));
      }

      public MOUSE_EVENT_RECORD(MemorySegment seg) {
         this.seg = Objects.requireNonNull(seg);
      }

      public MOUSE_EVENT_RECORD(MemorySegment seg, long offset) {
         this.seg = Objects.requireNonNull(seg).asSlice(offset, LAYOUT.byteSize());
      }

      public Kernel32.COORD mousePosition() {
         return new Kernel32.COORD(this.seg, MOUSE_POSITION_OFFSET);
      }

      public int buttonState() {
         return (int)BUTTON_STATE.get((MemorySegment)this.seg);
      }

      public int controlKeyState() {
         return (int)CONTROL_KEY_STATE.get((MemorySegment)this.seg);
      }

      public int eventFlags() {
         return (int)EVENT_FLAGS.get((MemorySegment)this.seg);
      }

      @Override
      public String toString() {
         return "MOUSE_EVENT_RECORD{mousePosition="
            + this.mousePosition()
            + ", buttonState="
            + this.buttonState()
            + ", controlKeyState="
            + this.controlKeyState()
            + ", eventFlags="
            + this.eventFlags()
            + "}";
      }
   }

   public static final class SMALL_RECT {
      static final GroupLayout LAYOUT = MemoryLayout.structLayout(
         Kernel32.C_SHORT$LAYOUT.withName("Left"),
         Kernel32.C_SHORT$LAYOUT.withName("Top"),
         Kernel32.C_SHORT$LAYOUT.withName("Right"),
         Kernel32.C_SHORT$LAYOUT.withName("Bottom")
      );
      static final VarHandle Left$VH = Kernel32.varHandle(LAYOUT, "Left");
      static final VarHandle Top$VH = Kernel32.varHandle(LAYOUT, "Top");
      static final VarHandle Right$VH = Kernel32.varHandle(LAYOUT, "Right");
      static final VarHandle Bottom$VH = Kernel32.varHandle(LAYOUT, "Bottom");
      private final MemorySegment seg;

      public SMALL_RECT() {
         this(Arena.ofAuto());
      }

      public SMALL_RECT(Arena arena) {
         this(arena.allocate(LAYOUT));
      }

      public SMALL_RECT(Arena arena, Kernel32.SMALL_RECT rect) {
         this(arena);
         this.left(rect.left());
         this.right(rect.right());
         this.top(rect.top());
         this.bottom(rect.bottom());
      }

      public SMALL_RECT(MemorySegment seg, long offset) {
         this(seg.asSlice(offset, LAYOUT.byteSize()));
      }

      public SMALL_RECT(MemorySegment seg) {
         this.seg = seg;
      }

      public short left() {
         return (short)Left$VH.get((MemorySegment)this.seg);
      }

      public short top() {
         return (short)Top$VH.get((MemorySegment)this.seg);
      }

      public short right() {
         return (short)Right$VH.get((MemorySegment)this.seg);
      }

      public short bottom() {
         return (short)Bottom$VH.get((MemorySegment)this.seg);
      }

      public short width() {
         return (short)(this.right() - this.left());
      }

      public short height() {
         return (short)(this.bottom() - this.top());
      }

      public void left(short l) {
         Left$VH.set((MemorySegment)this.seg, (short)l);
      }

      public void top(short t) {
         Top$VH.set((MemorySegment)this.seg, (short)t);
      }

      public void right(short r) {
         Right$VH.set((MemorySegment)this.seg, (short)r);
      }

      public void bottom(short b) {
         Bottom$VH.set((MemorySegment)this.seg, (short)b);
      }

      public Kernel32.SMALL_RECT copy(Arena arena) {
         return new Kernel32.SMALL_RECT(arena.allocate(LAYOUT).copyFrom(this.seg));
      }
   }

   public static final class WINDOW_BUFFER_SIZE_RECORD {
      static final GroupLayout LAYOUT = MemoryLayout.structLayout(Kernel32.COORD.LAYOUT.withName("size"));
      static final long SIZE_OFFSET = Kernel32.byteOffset(LAYOUT, "size");
      private final MemorySegment seg;

      public WINDOW_BUFFER_SIZE_RECORD() {
         this(Arena.ofAuto());
      }

      public WINDOW_BUFFER_SIZE_RECORD(Arena arena) {
         this(arena.allocate(LAYOUT));
      }

      public WINDOW_BUFFER_SIZE_RECORD(MemorySegment seg) {
         this.seg = seg;
      }

      public Kernel32.COORD size() {
         return new Kernel32.COORD(this.seg, SIZE_OFFSET);
      }

      @Override
      public String toString() {
         return "WINDOW_BUFFER_SIZE_RECORD{size=" + this.size() + "}";
      }
   }
}
