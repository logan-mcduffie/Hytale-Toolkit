package org.bouncycastle.crypto.params;

import org.bouncycastle.crypto.CipherParameters;

public class DHUPublicParameters implements CipherParameters {
   private DHPublicKeyParameters staticPublicKey;
   private DHPublicKeyParameters ephemeralPublicKey;

   public DHUPublicParameters(DHPublicKeyParameters var1, DHPublicKeyParameters var2) {
      if (var1 == null) {
         throw new NullPointerException("staticPublicKey cannot be null");
      } else if (var2 == null) {
         throw new NullPointerException("ephemeralPublicKey cannot be null");
      } else if (!var1.getParameters().equals(var2.getParameters())) {
         throw new IllegalArgumentException("Static and ephemeral public keys have different domain parameters");
      } else {
         this.staticPublicKey = var1;
         this.ephemeralPublicKey = var2;
      }
   }

   public DHPublicKeyParameters getStaticPublicKey() {
      return this.staticPublicKey;
   }

   public DHPublicKeyParameters getEphemeralPublicKey() {
      return this.ephemeralPublicKey;
   }
}
