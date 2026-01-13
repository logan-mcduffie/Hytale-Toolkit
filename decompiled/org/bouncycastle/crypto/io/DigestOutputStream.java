package org.bouncycastle.crypto.io;

import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.crypto.Digest;

public class DigestOutputStream extends OutputStream {
   protected Digest digest;

   public DigestOutputStream(Digest var1) {
      this.digest = var1;
   }

   @Override
   public void write(int var1) throws IOException {
      this.digest.update((byte)var1);
   }

   @Override
   public void write(byte[] var1, int var2, int var3) throws IOException {
      this.digest.update(var1, var2, var3);
   }

   public byte[] getDigest() {
      byte[] var1 = new byte[this.digest.getDigestSize()];
      this.digest.doFinal(var1, 0);
      return var1;
   }
}
