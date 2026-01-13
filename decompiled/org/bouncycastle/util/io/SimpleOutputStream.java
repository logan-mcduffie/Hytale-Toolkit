package org.bouncycastle.util.io;

import java.io.IOException;
import java.io.OutputStream;

public abstract class SimpleOutputStream extends OutputStream {
   @Override
   public void close() {
   }

   @Override
   public void flush() {
   }

   @Override
   public void write(int var1) throws IOException {
      byte[] var2 = new byte[]{(byte)var1};
      this.write(var2, 0, 1);
   }
}
