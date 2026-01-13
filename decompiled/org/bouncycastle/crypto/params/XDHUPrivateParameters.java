package org.bouncycastle.crypto.params;

import org.bouncycastle.crypto.CipherParameters;

public class XDHUPrivateParameters implements CipherParameters {
   private AsymmetricKeyParameter staticPrivateKey;
   private AsymmetricKeyParameter ephemeralPrivateKey;
   private AsymmetricKeyParameter ephemeralPublicKey;

   public XDHUPrivateParameters(AsymmetricKeyParameter var1, AsymmetricKeyParameter var2) {
      this(var1, var2, null);
   }

   public XDHUPrivateParameters(AsymmetricKeyParameter var1, AsymmetricKeyParameter var2, AsymmetricKeyParameter var3) {
      if (var1 == null) {
         throw new NullPointerException("staticPrivateKey cannot be null");
      } else if (!(var1 instanceof X448PrivateKeyParameters) && !(var1 instanceof X25519PrivateKeyParameters)) {
         throw new IllegalArgumentException("only X25519 and X448 paramaters can be used");
      } else if (var2 == null) {
         throw new NullPointerException("ephemeralPrivateKey cannot be null");
      } else if (!var1.getClass().isAssignableFrom(var2.getClass())) {
         throw new IllegalArgumentException("static and ephemeral private keys have different domain parameters");
      } else {
         if (var3 == null) {
            if (var2 instanceof X448PrivateKeyParameters) {
               var3 = ((X448PrivateKeyParameters)var2).generatePublicKey();
            } else {
               var3 = ((X25519PrivateKeyParameters)var2).generatePublicKey();
            }
         } else {
            if (var3 instanceof X448PublicKeyParameters && !(var1 instanceof X448PrivateKeyParameters)) {
               throw new IllegalArgumentException("ephemeral public key has different domain parameters");
            }

            if (var3 instanceof X25519PublicKeyParameters && !(var1 instanceof X25519PrivateKeyParameters)) {
               throw new IllegalArgumentException("ephemeral public key has different domain parameters");
            }
         }

         this.staticPrivateKey = var1;
         this.ephemeralPrivateKey = var2;
         this.ephemeralPublicKey = (AsymmetricKeyParameter)var3;
      }
   }

   public AsymmetricKeyParameter getStaticPrivateKey() {
      return this.staticPrivateKey;
   }

   public AsymmetricKeyParameter getEphemeralPrivateKey() {
      return this.ephemeralPrivateKey;
   }

   public AsymmetricKeyParameter getEphemeralPublicKey() {
      return this.ephemeralPublicKey;
   }
}
