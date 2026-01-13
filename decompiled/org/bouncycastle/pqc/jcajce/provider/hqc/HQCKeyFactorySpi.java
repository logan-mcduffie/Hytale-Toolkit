package org.bouncycastle.pqc.jcajce.provider.hqc;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashSet;
import java.util.Set;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.bc.BCObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.pqc.jcajce.provider.util.BaseKeyFactorySpi;

public class HQCKeyFactorySpi extends BaseKeyFactorySpi {
   private static final Set<ASN1ObjectIdentifier> keyOids = new HashSet<>();

   public HQCKeyFactorySpi() {
      super(keyOids);
   }

   public HQCKeyFactorySpi(ASN1ObjectIdentifier var1) {
      super(var1);
   }

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
         throw new InvalidKeySpecException("Unsupported key specification: " + var1.getClass() + ".");
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
         throw new InvalidKeySpecException("Unknown key specification: " + var1 + ".");
      }
   }

   @Override
   public final KeySpec engineGetKeySpec(Key var1, Class var2) throws InvalidKeySpecException {
      if (var1 instanceof BCHQCPrivateKey) {
         if (PKCS8EncodedKeySpec.class.isAssignableFrom(var2)) {
            return new PKCS8EncodedKeySpec(var1.getEncoded());
         }
      } else {
         if (!(var1 instanceof BCHQCPublicKey)) {
            throw new InvalidKeySpecException("Unsupported key type: " + var1.getClass() + ".");
         }

         if (X509EncodedKeySpec.class.isAssignableFrom(var2)) {
            return new X509EncodedKeySpec(var1.getEncoded());
         }
      }

      throw new InvalidKeySpecException("Unknown key specification: " + var2 + ".");
   }

   @Override
   public final Key engineTranslateKey(Key var1) throws InvalidKeyException {
      if (!(var1 instanceof BCHQCPrivateKey) && !(var1 instanceof BCHQCPublicKey)) {
         throw new InvalidKeyException("Unsupported key type");
      } else {
         return var1;
      }
   }

   @Override
   public PrivateKey generatePrivate(PrivateKeyInfo var1) throws IOException {
      return new BCHQCPrivateKey(var1);
   }

   @Override
   public PublicKey generatePublic(SubjectPublicKeyInfo var1) throws IOException {
      return new BCHQCPublicKey(var1);
   }

   static {
      keyOids.add(BCObjectIdentifiers.hqc128);
      keyOids.add(BCObjectIdentifiers.hqc192);
      keyOids.add(BCObjectIdentifiers.hqc256);
   }

   public static class HQC128 extends HQCKeyFactorySpi {
      public HQC128() {
         super(BCObjectIdentifiers.hqc128);
      }
   }

   public static class HQC192 extends HQCKeyFactorySpi {
      public HQC192() {
         super(BCObjectIdentifiers.hqc192);
      }
   }

   public static class HQC256 extends HQCKeyFactorySpi {
      public HQC256() {
         super(BCObjectIdentifiers.hqc256);
      }
   }
}
