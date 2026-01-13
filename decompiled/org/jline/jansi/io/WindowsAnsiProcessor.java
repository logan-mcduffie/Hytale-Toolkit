package org.jline.jansi.io;

import java.io.IOException;
import java.io.OutputStream;

public final class WindowsAnsiProcessor extends AnsiProcessor {
   public WindowsAnsiProcessor(OutputStream ps, long console) throws IOException {
      super(ps);
   }

   public WindowsAnsiProcessor(OutputStream ps, boolean stdout) throws IOException {
      super(ps);
   }

   public WindowsAnsiProcessor(OutputStream ps) throws IOException {
      super(ps);
   }
}
