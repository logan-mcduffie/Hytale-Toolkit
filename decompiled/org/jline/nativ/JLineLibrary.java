package org.jline.nativ;

import java.io.FileDescriptor;
import java.lang.ProcessBuilder.Redirect;

public class JLineLibrary {
   public static native FileDescriptor newFileDescriptor(int var0);

   public static native Redirect newRedirectPipe(FileDescriptor var0);

   static {
      JLineNativeLoader.initialize();
   }
}
