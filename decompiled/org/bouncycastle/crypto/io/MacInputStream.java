package org.bouncycastle.crypto.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.crypto.Mac;

public class MacInputStream extends FilterInputStream {
   protected Mac mac;

   public MacInputStream(InputStream var1, Mac var2) {
      super(var1);
      this.mac = var2;
   }

   @Override
   public int read() throws IOException {
      int var1 = this.in.read();
      if (var1 >= 0) {
         this.mac.update((byte)var1);
      }

      return var1;
   }

   @Override
   public int read(byte[] var1, int var2, int var3) throws IOException {
      int var4 = this.in.read(var1, var2, var3);
      if (var4 >= 0) {
         this.mac.update(var1, var2, var4);
      }

      return var4;
   }

   public Mac getMac() {
      return this.mac;
   }
}
