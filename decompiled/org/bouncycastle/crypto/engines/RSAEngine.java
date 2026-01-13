package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.CipherParameters;

public class RSAEngine implements AsymmetricBlockCipher {
   private RSACoreEngine core;

   @Override
   public void init(boolean var1, CipherParameters var2) {
      if (this.core == null) {
         this.core = new RSACoreEngine();
      }

      this.core.init(var1, var2);
   }

   @Override
   public int getInputBlockSize() {
      return this.core.getInputBlockSize();
   }

   @Override
   public int getOutputBlockSize() {
      return this.core.getOutputBlockSize();
   }

   @Override
   public byte[] processBlock(byte[] var1, int var2, int var3) {
      if (this.core == null) {
         throw new IllegalStateException("RSA engine not initialised");
      } else {
         return this.core.convertOutput(this.core.processBlock(this.core.convertInput(var1, var2, var3)));
      }
   }
}
