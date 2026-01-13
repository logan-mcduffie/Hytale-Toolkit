package org.bouncycastle.cms.jcajce;

import java.io.IOException;
import java.io.OutputStream;
import javax.crypto.Cipher;

class JceAADStream extends OutputStream {
   private final byte[] SINGLE_BYTE = new byte[1];
   private Cipher cipher;

   JceAADStream(Cipher var1) {
      this.cipher = var1;
   }

   @Override
   public void write(byte[] var1, int var2, int var3) throws IOException {
      this.cipher.updateAAD(var1, var2, var3);
   }

   @Override
   public void write(int var1) throws IOException {
      this.SINGLE_BYTE[0] = (byte)var1;
      this.cipher.updateAAD(this.SINGLE_BYTE, 0, 1);
   }
}
