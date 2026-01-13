package org.bouncycastle.crypto.io;

import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.crypto.Signer;

public class SignerOutputStream extends OutputStream {
   protected Signer signer;

   public SignerOutputStream(Signer var1) {
      this.signer = var1;
   }

   @Override
   public void write(int var1) throws IOException {
      this.signer.update((byte)var1);
   }

   @Override
   public void write(byte[] var1, int var2, int var3) throws IOException {
      this.signer.update(var1, var2, var3);
   }

   public Signer getSigner() {
      return this.signer;
   }
}
