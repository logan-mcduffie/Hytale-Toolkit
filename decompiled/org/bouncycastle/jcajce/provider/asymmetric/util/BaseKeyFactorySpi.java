package org.bouncycastle.jcajce.provider.asymmetric.util;

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
import org.bouncycastle.jcajce.provider.util.AsymmetricKeyInfoConverter;

public abstract class BaseKeyFactorySpi extends KeyFactorySpi implements AsymmetricKeyInfoConverter {
   @Override
   protected PrivateKey engineGeneratePrivate(KeySpec var1) throws InvalidKeySpecException {
      if (var1 instanceof PKCS8EncodedKeySpec) {
         try {
            return this.generatePrivate(PrivateKeyInfo.getInstance(((PKCS8EncodedKeySpec)var1).getEncoded()));
         } catch (Exception var3) {
            throw new InvalidKeySpecException("encoded key spec not recognized: " + var3.getMessage());
         }
      } else {
         throw new InvalidKeySpecException("key spec not recognized");
      }
   }

   @Override
   protected PublicKey engineGeneratePublic(KeySpec var1) throws InvalidKeySpecException {
      if (var1 instanceof X509EncodedKeySpec) {
         try {
            return this.generatePublic(SubjectPublicKeyInfo.getInstance(((X509EncodedKeySpec)var1).getEncoded()));
         } catch (Exception var3) {
            throw new InvalidKeySpecException("encoded key spec not recognized: " + var3.getMessage());
         }
      } else {
         throw new InvalidKeySpecException("key spec not recognized");
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
}
