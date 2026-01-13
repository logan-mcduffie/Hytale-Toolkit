package org.bouncycastle.crypto.params;

import org.bouncycastle.crypto.CipherParameters;

public class MQVPublicParameters implements CipherParameters {
   private ECPublicKeyParameters staticPublicKey;
   private ECPublicKeyParameters ephemeralPublicKey;

   public MQVPublicParameters(ECPublicKeyParameters var1, ECPublicKeyParameters var2) {
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

   public ECPublicKeyParameters getStaticPublicKey() {
      return this.staticPublicKey;
   }

   public ECPublicKeyParameters getEphemeralPublicKey() {
      return this.ephemeralPublicKey;
   }
}
