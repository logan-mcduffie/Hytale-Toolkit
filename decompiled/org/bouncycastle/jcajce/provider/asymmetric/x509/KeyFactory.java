package org.bouncycastle.jcajce.provider.asymmetric.x509;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactorySpi;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class KeyFactory extends KeyFactorySpi {
   @Override
   protected PrivateKey engineGeneratePrivate(KeySpec var1) throws InvalidKeySpecException {
      if (var1 instanceof PKCS8EncodedKeySpec) {
         try {
            PrivateKeyInfo var2 = PrivateKeyInfo.getInstance(((PKCS8EncodedKeySpec)var1).getEncoded());
            PrivateKey var3 = BouncyCastleProvider.getPrivateKey(var2);
            if (var3 != null) {
               return var3;
            } else {
               throw new InvalidKeySpecException("no factory found for OID: " + var2.getPrivateKeyAlgorithm().getAlgorithm());
            }
         } catch (Exception var4) {
            throw new InvalidKeySpecException(var4.toString());
         }
      } else {
         throw new InvalidKeySpecException("Unknown KeySpec type: " + var1.getClass().getName());
      }
   }

   @Override
   protected PublicKey engineGeneratePublic(KeySpec var1) throws InvalidKeySpecException {
      if (var1 instanceof X509EncodedKeySpec) {
         try {
            SubjectPublicKeyInfo var2 = SubjectPublicKeyInfo.getInstance(((X509EncodedKeySpec)var1).getEncoded());
            PublicKey var3 = BouncyCastleProvider.getPublicKey(var2);
            if (var3 != null) {
               return var3;
            } else {
               throw new InvalidKeySpecException("no factory found for OID: " + var2.getAlgorithm().getAlgorithm());
            }
         } catch (Exception var4) {
            throw new InvalidKeySpecException(var4.toString());
         }
      } else {
         throw new InvalidKeySpecException("Unknown KeySpec type: " + var1.getClass().getName());
      }
   }

   @Override
   protected KeySpec engineGetKeySpec(Key var1, Class var2) throws InvalidKeySpecException {
      if (var2.isAssignableFrom(PKCS8EncodedKeySpec.class) && var1.getFormat().equals("PKCS#8")) {
         return new PKCS8EncodedKeySpec(var1.getEncoded());
      } else if (var2.isAssignableFrom(X509EncodedKeySpec.class) && var1.getFormat().equals("X.509")) {
         return new X509EncodedKeySpec(var1.getEncoded());
      } else {
         throw new InvalidKeySpecException("not implemented yet " + var1 + " " + var2);
      }
   }

   @Override
   protected Key engineTranslateKey(Key var1) throws InvalidKeyException {
      throw new InvalidKeyException("not implemented yet " + var1);
   }
}
