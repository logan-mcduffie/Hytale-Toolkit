package org.bouncycastle.jcajce.provider.asymmetric.edec;

import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.PublicKey;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.util.PrivateKeyFactory;
import org.bouncycastle.crypto.util.PublicKeyFactory;

class EdECUtil {
   public static AsymmetricKeyParameter generatePublicKeyParameter(PublicKey var0) throws InvalidKeyException {
      if (var0 instanceof BCXDHPublicKey) {
         return ((BCXDHPublicKey)var0).engineGetKeyParameters();
      } else if (var0 instanceof BCEdDSAPublicKey) {
         return ((BCEdDSAPublicKey)var0).engineGetKeyParameters();
      } else {
         try {
            byte[] var1 = var0.getEncoded();
            if (var1 == null) {
               throw new InvalidKeyException("no encoding for EdEC/XDH public key");
            } else {
               return PublicKeyFactory.createKey(var1);
            }
         } catch (Exception var2) {
            throw new InvalidKeyException("cannot identify EdEC/XDH public key: " + var2.getMessage());
         }
      }
   }

   public static AsymmetricKeyParameter generatePrivateKeyParameter(PrivateKey var0) throws InvalidKeyException {
      if (var0 instanceof BCXDHPrivateKey) {
         return ((BCXDHPrivateKey)var0).engineGetKeyParameters();
      } else if (var0 instanceof BCEdDSAPrivateKey) {
         return ((BCEdDSAPrivateKey)var0).engineGetKeyParameters();
      } else {
         try {
            byte[] var1 = var0.getEncoded();
            if (var1 == null) {
               throw new InvalidKeyException("no encoding for EdEC/XDH private key");
            } else {
               return PrivateKeyFactory.createKey(var1);
            }
         } catch (Exception var2) {
            throw new InvalidKeyException("cannot identify EdEC/XDH private key: " + var2.getMessage());
         }
      }
   }
}
