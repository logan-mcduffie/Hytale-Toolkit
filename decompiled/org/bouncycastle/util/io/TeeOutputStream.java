package org.bouncycastle.util.io;

import java.io.IOException;
import java.io.OutputStream;

public class TeeOutputStream extends OutputStream {
   private OutputStream output1;
   private OutputStream output2;

   public TeeOutputStream(OutputStream var1, OutputStream var2) {
      this.output1 = var1;
      this.output2 = var2;
   }

   @Override
   public void write(byte[] var1) throws IOException {
      this.output1.write(var1);
      this.output2.write(var1);
   }

   @Override
   public void write(byte[] var1, int var2, int var3) throws IOException {
      this.output1.write(var1, var2, var3);
      this.output2.write(var1, var2, var3);
   }

   @Override
   public void write(int var1) throws IOException {
      this.output1.write(var1);
      this.output2.write(var1);
   }

   @Override
   public void flush() throws IOException {
      this.output1.flush();
      this.output2.flush();
   }

   @Override
   public void close() throws IOException {
      this.output1.close();
      this.output2.close();
   }
}
