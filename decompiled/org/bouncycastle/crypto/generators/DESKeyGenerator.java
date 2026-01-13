package org.bouncycastle.crypto.generators;

import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.params.DESParameters;

public class DESKeyGenerator extends CipherKeyGenerator {
   @Override
   public void init(KeyGenerationParameters var1) {
      super.init(var1);
      if (this.strength != 0 && this.strength != 7) {
         if (this.strength != 8) {
            throw new IllegalArgumentException("DES key must be 64 bits long.");
         }
      } else {
         this.strength = 8;
      }

      CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties("DESKeyGen", 56, null, CryptoServicePurpose.KEYGEN));
   }

   @Override
   public byte[] generateKey() {
      byte[] var1 = new byte[8];

      do {
         this.random.nextBytes(var1);
         DESParameters.setOddParity(var1);
      } while (DESParameters.isWeakKey(var1, 0));

      return var1;
   }
}
