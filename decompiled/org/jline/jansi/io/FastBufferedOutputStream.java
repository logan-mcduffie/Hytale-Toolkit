package org.jline.jansi.io;

import java.io.OutputStream;

public class FastBufferedOutputStream extends org.jline.utils.FastBufferedOutputStream {
   public FastBufferedOutputStream(OutputStream out) {
      super(out);
   }
}
