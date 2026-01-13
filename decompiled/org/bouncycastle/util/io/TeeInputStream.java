package org.bouncycastle.util.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class TeeInputStream extends InputStream {
   private final InputStream input;
   private final OutputStream output;

   public TeeInputStream(InputStream var1, OutputStream var2) {
      this.input = var1;
      this.output = var2;
   }

   @Override
   public int available() throws IOException {
      return this.input.available();
   }

   @Override
   public int read(byte[] var1) throws IOException {
      return this.read(var1, 0, var1.length);
   }

   @Override
   public int read(byte[] var1, int var2, int var3) throws IOException {
      int var4 = this.input.read(var1, var2, var3);
      if (var4 > 0) {
         this.output.write(var1, var2, var4);
      }

      return var4;
   }

   @Override
   public int read() throws IOException {
      int var1 = this.input.read();
      if (var1 >= 0) {
         this.output.write(var1);
      }

      return var1;
   }

   @Override
   public void close() throws IOException {
      this.input.close();
      this.output.close();
   }

   public OutputStream getOutputStream() {
      return this.output;
   }
}
