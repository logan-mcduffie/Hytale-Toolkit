package org.bouncycastle.crypto;

import java.security.SecureRandom;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;

public class CipherKeyGenerator {
   protected SecureRandom random;
   protected int strength;

   public void init(KeyGenerationParameters var1) {
      this.random = var1.getRandom();
      this.strength = (var1.getStrength() + 7) / 8;
      CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties("SymKeyGen", var1.getStrength()));
   }

   public byte[] generateKey() {
      byte[] var1 = new byte[this.strength];
      this.random.nextBytes(var1);
      return var1;
   }
}
