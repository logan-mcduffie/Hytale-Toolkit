package org.fusesource.jansi;

import org.fusesource.jansi.internal.Kernel32;

@Deprecated
public class WindowsSupport {
   @Deprecated
   public static String getLastErrorMessage() {
      return Kernel32.getLastErrorMessage();
   }

   @Deprecated
   public static String getErrorMessage(int errorCode) {
      return Kernel32.getErrorMessage(errorCode);
   }
}
