package org.bouncycastle.crypto.params;

import java.math.BigInteger;
import org.bouncycastle.crypto.CipherParameters;

public class RSABlindingParameters implements CipherParameters {
   private RSAKeyParameters publicKey;
   private BigInteger blindingFactor;

   public RSABlindingParameters(RSAKeyParameters var1, BigInteger var2) {
      if (var1 instanceof RSAPrivateCrtKeyParameters) {
         throw new IllegalArgumentException("RSA parameters should be for a public key");
      } else {
         this.publicKey = var1;
         this.blindingFactor = var2;
      }
   }

   public RSAKeyParameters getPublicKey() {
      return this.publicKey;
   }

   public BigInteger getBlindingFactor() {
      return this.blindingFactor;
   }
}
