package org.bouncycastle.cms;

import java.io.IOException;
import java.io.OutputStream;

class NullOutputStream extends OutputStream {
   @Override
   public void write(byte[] var1) throws IOException {
   }

   @Override
   public void write(byte[] var1, int var2, int var3) throws IOException {
   }

   @Override
   public void write(int var1) throws IOException {
   }
}
