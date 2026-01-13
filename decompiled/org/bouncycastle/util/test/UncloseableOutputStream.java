package org.bouncycastle.util.test;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class UncloseableOutputStream extends FilterOutputStream {
   public UncloseableOutputStream(OutputStream var1) {
      super(var1);
   }

   @Override
   public void close() {
      throw new RuntimeException("close() called on UncloseableOutputStream");
   }

   @Override
   public void write(byte[] var1, int var2, int var3) throws IOException {
      this.out.write(var1, var2, var3);
   }
}
