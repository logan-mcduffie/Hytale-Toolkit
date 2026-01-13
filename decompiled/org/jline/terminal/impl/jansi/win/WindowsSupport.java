package org.jline.terminal.impl.jansi.win;

import java.nio.charset.StandardCharsets;
import org.fusesource.jansi.internal.Kernel32;

class WindowsSupport {
   public static String getLastErrorMessage() {
      int errorCode = Kernel32.GetLastError();
      int bufferSize = 160;
      byte[] data = new byte[bufferSize];
      Kernel32.FormatMessageW(Kernel32.FORMAT_MESSAGE_FROM_SYSTEM, 0L, errorCode, 0, data, bufferSize, null);
      return new String(data, StandardCharsets.UTF_16LE).trim();
   }
}
