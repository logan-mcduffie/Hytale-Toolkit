package org.bouncycastle.pqc.jcajce.provider.kyber;

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
import org.bouncycastle.asn1.bc.BCObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.pqc.jcajce.provider.util.BaseKeyFactorySpi;

public class KyberKeyFactorySpi extends BaseKeyFactorySpi {
   private static final Set<ASN1ObjectIdentifier> keyOids = new HashSet<>();

   public KyberKeyFactorySpi() {
      super(keyOids);
   }

   public KyberKeyFactorySpi(ASN1ObjectIdentifier var1) {
      super(var1);
   }

   @Override
   public final KeySpec engineGetKeySpec(Key var1, Class var2) throws InvalidKeySpecException {
      if (var1 instanceof BCKyberPrivateKey) {
         if (PKCS8EncodedKeySpec.class.isAssignableFrom(var2)) {
            return new PKCS8EncodedKeySpec(var1.getEncoded());
         }
      } else {
         if (!(var1 instanceof BCKyberPublicKey)) {
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
      if (!(var1 instanceof BCKyberPrivateKey) && !(var1 instanceof BCKyberPublicKey)) {
         throw new InvalidKeyException("Unsupported key type");
      } else {
         return var1;
      }
   }

   @Override
   public PrivateKey generatePrivate(PrivateKeyInfo var1) throws IOException {
      return new BCKyberPrivateKey(var1);
   }

   @Override
   public PublicKey generatePublic(SubjectPublicKeyInfo var1) throws IOException {
      return new BCKyberPublicKey(var1);
   }

   static {
      keyOids.add(NISTObjectIdentifiers.id_alg_ml_kem_512);
      keyOids.add(NISTObjectIdentifiers.id_alg_ml_kem_768);
      keyOids.add(NISTObjectIdentifiers.id_alg_ml_kem_1024);
      keyOids.add(BCObjectIdentifiers.kyber512_aes);
      keyOids.add(BCObjectIdentifiers.kyber768_aes);
      keyOids.add(BCObjectIdentifiers.kyber1024_aes);
   }

   public static class Kyber1024 extends KyberKeyFactorySpi {
      public Kyber1024() {
         super(NISTObjectIdentifiers.id_alg_ml_kem_1024);
      }
   }

   public static class Kyber1024_AES extends KyberKeyFactorySpi {
      public Kyber1024_AES() {
         super(BCObjectIdentifiers.kyber1024_aes);
      }
   }

   public static class Kyber512 extends KyberKeyFactorySpi {
      public Kyber512() {
         super(NISTObjectIdentifiers.id_alg_ml_kem_512);
      }
   }

   public static class Kyber512_AES extends KyberKeyFactorySpi {
      public Kyber512_AES() {
         super(BCObjectIdentifiers.kyber512_aes);
      }
   }

   public static class Kyber768 extends KyberKeyFactorySpi {
      public Kyber768() {
         super(NISTObjectIdentifiers.id_alg_ml_kem_768);
      }
   }

   public static class Kyber768_AES extends KyberKeyFactorySpi {
      public Kyber768_AES() {
         super(BCObjectIdentifiers.kyber768_aes);
      }
   }
}
