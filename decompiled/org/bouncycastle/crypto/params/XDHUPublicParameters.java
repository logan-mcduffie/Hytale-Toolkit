package org.bouncycastle.crypto.params;

import org.bouncycastle.crypto.CipherParameters;

public class XDHUPublicParameters implements CipherParameters {
   private AsymmetricKeyParameter staticPublicKey;
   private AsymmetricKeyParameter ephemeralPublicKey;

   public XDHUPublicParameters(AsymmetricKeyParameter var1, AsymmetricKeyParameter var2) {
      if (var1 == null) {
         throw new NullPointerException("staticPublicKey cannot be null");
      } else if (!(var1 instanceof X448PublicKeyParameters) && !(var1 instanceof X25519PublicKeyParameters)) {
         throw new IllegalArgumentException("only X25519 and X448 paramaters can be used");
      } else if (var2 == null) {
         throw new NullPointerException("ephemeralPublicKey cannot be null");
      } else if (!var1.getClass().isAssignableFrom(var2.getClass())) {
         throw new IllegalArgumentException("static and ephemeral public keys have different domain parameters");
      } else {
         this.staticPublicKey = var1;
         this.ephemeralPublicKey = var2;
      }
   }

   public AsymmetricKeyParameter getStaticPublicKey() {
      return this.staticPublicKey;
   }

   public AsymmetricKeyParameter getEphemeralPublicKey() {
      return this.ephemeralPublicKey;
   }
}
