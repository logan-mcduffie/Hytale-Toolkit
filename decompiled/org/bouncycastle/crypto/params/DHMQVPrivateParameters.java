package org.bouncycastle.crypto.params;

import org.bouncycastle.crypto.CipherParameters;

public class DHMQVPrivateParameters implements CipherParameters {
   private DHPrivateKeyParameters staticPrivateKey;
   private DHPrivateKeyParameters ephemeralPrivateKey;
   private DHPublicKeyParameters ephemeralPublicKey;

   public DHMQVPrivateParameters(DHPrivateKeyParameters var1, DHPrivateKeyParameters var2) {
      this(var1, var2, null);
   }

   public DHMQVPrivateParameters(DHPrivateKeyParameters var1, DHPrivateKeyParameters var2, DHPublicKeyParameters var3) {
      if (var1 == null) {
         throw new NullPointerException("staticPrivateKey cannot be null");
      } else if (var2 == null) {
         throw new NullPointerException("ephemeralPrivateKey cannot be null");
      } else {
         DHParameters var4 = var1.getParameters();
         if (!var4.equals(var2.getParameters())) {
            throw new IllegalArgumentException("Static and ephemeral private keys have different domain parameters");
         } else {
            if (var3 == null) {
               var3 = new DHPublicKeyParameters(var4.getG().modPow(var2.getX(), var4.getP()), var4);
            } else if (!var4.equals(var3.getParameters())) {
               throw new IllegalArgumentException("Ephemeral public key has different domain parameters");
            }

            this.staticPrivateKey = var1;
            this.ephemeralPrivateKey = var2;
            this.ephemeralPublicKey = var3;
         }
      }
   }

   public DHPrivateKeyParameters getStaticPrivateKey() {
      return this.staticPrivateKey;
   }

   public DHPrivateKeyParameters getEphemeralPrivateKey() {
      return this.ephemeralPrivateKey;
   }

   public DHPublicKeyParameters getEphemeralPublicKey() {
      return this.ephemeralPublicKey;
   }
}
