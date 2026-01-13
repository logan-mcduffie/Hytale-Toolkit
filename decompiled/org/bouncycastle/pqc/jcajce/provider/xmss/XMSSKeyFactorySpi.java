package org.bouncycastle.pqc.jcajce.provider.xmss;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactorySpi;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.jcajce.provider.util.AsymmetricKeyInfoConverter;

public class XMSSKeyFactorySpi extends KeyFactorySpi implements AsymmetricKeyInfoConverter {
   @Override
   public PrivateKey engineGeneratePrivate(KeySpec var1) throws InvalidKeySpecException {
      if (var1 instanceof PKCS8EncodedKeySpec) {
         byte[] var2 = ((PKCS8EncodedKeySpec)var1).getEncoded();

         try {
            return this.generatePrivate(PrivateKeyInfo.getInstance(ASN1Primitive.fromByteArray(var2)));
         } catch (Exception var4) {
            throw new InvalidKeySpecException(var4.toString());
         }
      } else {
         throw new InvalidKeySpecException("unsupported key specification: " + var1.getClass() + ".");
      }
   }

   @Override
   public PublicKey engineGeneratePublic(KeySpec var1) throws InvalidKeySpecException {
      if (var1 instanceof X509EncodedKeySpec) {
         byte[] var2 = ((X509EncodedKeySpec)var1).getEncoded();

         try {
            return this.generatePublic(SubjectPublicKeyInfo.getInstance(var2));
         } catch (Exception var4) {
            throw new InvalidKeySpecException(var4.toString());
         }
      } else {
         throw new InvalidKeySpecException("unknown key specification: " + var1 + ".");
      }
   }

   @Override
   public final KeySpec engineGetKeySpec(Key var1, Class var2) throws InvalidKeySpecException {
      if (var1 instanceof BCXMSSPrivateKey) {
         if (PKCS8EncodedKeySpec.class.isAssignableFrom(var2)) {
            return new PKCS8EncodedKeySpec(var1.getEncoded());
         }
      } else {
         if (!(var1 instanceof BCXMSSPublicKey)) {
            throw new InvalidKeySpecException("unsupported key type: " + var1.getClass() + ".");
         }

         if (X509EncodedKeySpec.class.isAssignableFrom(var2)) {
            return new X509EncodedKeySpec(var1.getEncoded());
         }
      }

      throw new InvalidKeySpecException("unknown key specification: " + var2 + ".");
   }

   @Override
   public final Key engineTranslateKey(Key var1) throws InvalidKeyException {
      if (!(var1 instanceof BCXMSSPrivateKey) && !(var1 instanceof BCXMSSPublicKey)) {
         throw new InvalidKeyException("unsupported key type");
      } else {
         return var1;
      }
   }

   @Override
   public PrivateKey generatePrivate(PrivateKeyInfo var1) throws IOException {
      return new BCXMSSPrivateKey(var1);
   }

   @Override
   public PublicKey generatePublic(SubjectPublicKeyInfo var1) throws IOException {
      return new BCXMSSPublicKey(var1);
   }
}
