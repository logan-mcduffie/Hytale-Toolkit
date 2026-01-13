package org.bouncycastle.crypto.macs;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;

public class CMacWithIV extends CMac {
   public CMacWithIV(BlockCipher var1) {
      super(var1);
   }

   public CMacWithIV(BlockCipher var1, int var2) {
      super(var1, var2);
   }

   @Override
   void validate(CipherParameters var1) {
   }
}
