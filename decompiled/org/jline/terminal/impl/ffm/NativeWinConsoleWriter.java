package org.jline.terminal.impl.ffm;

import java.io.IOException;
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import org.jline.terminal.impl.AbstractWindowsConsoleWriter;

class NativeWinConsoleWriter extends AbstractWindowsConsoleWriter {
   private final MemorySegment console = Kernel32.GetStdHandle(-11);

   @Override
   protected void writeConsole(char[] text, int len) throws IOException {
      try (Arena arena = Arena.ofConfined()) {
         MemorySegment txt = arena.allocateFrom(ValueLayout.JAVA_CHAR, text);
         if (Kernel32.WriteConsoleW(this.console, txt, len, MemorySegment.NULL, MemorySegment.NULL) == 0) {
            throw new IOException("Failed to write to console: " + Kernel32.getLastErrorMessage());
         }
      }
   }
}
