package org.bouncycastle.jcajce.io;

import java.io.IOException;
import java.io.OutputStream;
import javax.crypto.Mac;

class MacUpdatingOutputStream extends OutputStream {
   private Mac mac;

   MacUpdatingOutputStream(Mac var1) {
      this.mac = var1;
   }

   @Override
   public void write(byte[] var1, int var2, int var3) throws IOException {
      this.mac.update(var1, var2, var3);
   }

   @Override
   public void write(byte[] var1) throws IOException {
      this.mac.update(var1);
   }

   @Override
   public void write(int var1) throws IOException {
      this.mac.update((byte)var1);
   }
}
