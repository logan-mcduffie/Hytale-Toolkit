package org.bouncycastle.crypto.params;

import java.security.SecureRandom;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;

public class ParametersWithRandom implements CipherParameters {
   private SecureRandom random;
   private CipherParameters parameters;

   public ParametersWithRandom(CipherParameters var1, SecureRandom var2) {
      this.random = CryptoServicesRegistrar.getSecureRandom(var2);
      this.parameters = var1;
   }

   public ParametersWithRandom(CipherParameters var1) {
      this(var1, null);
   }

   public SecureRandom getRandom() {
      return this.random;
   }

   public CipherParameters getParameters() {
      return this.parameters;
   }
}
