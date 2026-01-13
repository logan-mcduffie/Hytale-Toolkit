package org.bouncycastle.crypto.params;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.FixedPointCombMultiplier;

public class ECDHUPrivateParameters implements CipherParameters {
   private ECPrivateKeyParameters staticPrivateKey;
   private ECPrivateKeyParameters ephemeralPrivateKey;
   private ECPublicKeyParameters ephemeralPublicKey;

   public ECDHUPrivateParameters(ECPrivateKeyParameters var1, ECPrivateKeyParameters var2) {
      this(var1, var2, null);
   }

   public ECDHUPrivateParameters(ECPrivateKeyParameters var1, ECPrivateKeyParameters var2, ECPublicKeyParameters var3) {
      if (var1 == null) {
         throw new NullPointerException("staticPrivateKey cannot be null");
      } else if (var2 == null) {
         throw new NullPointerException("ephemeralPrivateKey cannot be null");
      } else {
         ECDomainParameters var4 = var1.getParameters();
         if (!var4.equals(var2.getParameters())) {
            throw new IllegalArgumentException("static and ephemeral private keys have different domain parameters");
         } else {
            if (var3 == null) {
               ECPoint var5 = new FixedPointCombMultiplier().multiply(var4.getG(), var2.getD());
               var3 = new ECPublicKeyParameters(var5, var4);
            } else if (!var4.equals(var3.getParameters())) {
               throw new IllegalArgumentException("ephemeral public key has different domain parameters");
            }

            this.staticPrivateKey = var1;
            this.ephemeralPrivateKey = var2;
            this.ephemeralPublicKey = var3;
         }
      }
   }

   public ECPrivateKeyParameters getStaticPrivateKey() {
      return this.staticPrivateKey;
   }

   public ECPrivateKeyParameters getEphemeralPrivateKey() {
      return this.ephemeralPrivateKey;
   }

   public ECPublicKeyParameters getEphemeralPublicKey() {
      return this.ephemeralPublicKey;
   }
}
