package org.bouncycastle.jcajce.io;

import java.io.IOException;
import java.io.OutputStream;
import javax.crypto.Mac;

public final class MacOutputStream extends OutputStream {
   private Mac mac;

   public MacOutputStream(Mac var1) {
      this.mac = var1;
   }

   @Override
   public void write(int var1) throws IOException {
      this.mac.update((byte)var1);
   }

   @Override
   public void write(byte[] var1, int var2, int var3) throws IOException {
      this.mac.update(var1, var2, var3);
   }

   public byte[] getMac() {
      return this.mac.doFinal();
   }
}
