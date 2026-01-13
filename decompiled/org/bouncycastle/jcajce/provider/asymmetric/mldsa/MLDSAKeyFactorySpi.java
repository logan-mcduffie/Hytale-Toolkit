package org.bouncycastle.jcajce.provider.asymmetric.mldsa;

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
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.jcajce.spec.MLDSAPrivateKeySpec;
import org.bouncycastle.jcajce.spec.MLDSAPublicKeySpec;
import org.bouncycastle.pqc.crypto.mldsa.MLDSAParameters;
import org.bouncycastle.pqc.crypto.mldsa.MLDSAPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.mldsa.MLDSAPublicKeyParameters;
import org.bouncycastle.pqc.jcajce.provider.util.BaseKeyFactorySpi;
import org.bouncycastle.util.Arrays;

public class MLDSAKeyFactorySpi extends BaseKeyFactorySpi {
   private static final Set<ASN1ObjectIdentifier> pureKeyOids = new HashSet<>();
   private static final Set<ASN1ObjectIdentifier> hashKeyOids = new HashSet<>();
   private final boolean isHashOnly;

   public MLDSAKeyFactorySpi(Set<ASN1ObjectIdentifier> var1) {
      super(var1);
      this.isHashOnly = false;
   }

   public MLDSAKeyFactorySpi(ASN1ObjectIdentifier var1) {
      super(var1);
      this.isHashOnly = var1.equals(NISTObjectIdentifiers.id_hash_ml_dsa_44_with_sha512)
         || var1.equals(NISTObjectIdentifiers.id_hash_ml_dsa_65_with_sha512)
         || var1.equals(NISTObjectIdentifiers.id_hash_ml_dsa_87_with_sha512);
   }

   @Override
   public final KeySpec engineGetKeySpec(Key var1, Class var2) throws InvalidKeySpecException {
      if (var1 instanceof BCMLDSAPrivateKey) {
         if (PKCS8EncodedKeySpec.class.isAssignableFrom(var2)) {
            return new PKCS8EncodedKeySpec(var1.getEncoded());
         }

         if (MLDSAPrivateKeySpec.class.isAssignableFrom(var2)) {
            BCMLDSAPrivateKey var5 = (BCMLDSAPrivateKey)var1;
            byte[] var4 = var5.getSeed();
            if (var4 != null) {
               return new MLDSAPrivateKeySpec(var5.getParameterSpec(), var4);
            }

            return new MLDSAPrivateKeySpec(var5.getParameterSpec(), var5.getPrivateData(), var5.getPublicKey().getPublicData());
         }

         if (MLDSAPublicKeySpec.class.isAssignableFrom(var2)) {
            BCMLDSAPrivateKey var3 = (BCMLDSAPrivateKey)var1;
            return new MLDSAPublicKeySpec(var3.getParameterSpec(), var3.getPublicKey().getPublicData());
         }
      } else {
         if (!(var1 instanceof BCMLDSAPublicKey)) {
            throw new InvalidKeySpecException("unsupported key type: " + var1.getClass() + ".");
         }

         if (X509EncodedKeySpec.class.isAssignableFrom(var2)) {
            return new X509EncodedKeySpec(var1.getEncoded());
         }

         if (MLDSAPublicKeySpec.class.isAssignableFrom(var2)) {
            BCMLDSAPublicKey var6 = (BCMLDSAPublicKey)var1;
            return new MLDSAPublicKeySpec(var6.getParameterSpec(), var6.getPublicData());
         }
      }

      throw new InvalidKeySpecException("unknown key specification: " + var2 + ".");
   }

   @Override
   public final Key engineTranslateKey(Key var1) throws InvalidKeyException {
      if (!(var1 instanceof BCMLDSAPrivateKey) && !(var1 instanceof BCMLDSAPublicKey)) {
         throw new InvalidKeyException("unsupported key type");
      } else {
         return var1;
      }
   }

   @Override
   public PrivateKey engineGeneratePrivate(KeySpec var1) throws InvalidKeySpecException {
      if (var1 instanceof MLDSAPrivateKeySpec) {
         MLDSAPrivateKeySpec var2 = (MLDSAPrivateKeySpec)var1;
         MLDSAParameters var4 = Utils.getParameters(var2.getParameterSpec().getName());
         MLDSAPrivateKeyParameters var3;
         if (var2.isSeed()) {
            var3 = new MLDSAPrivateKeyParameters(var4, var2.getSeed());
         } else {
            var3 = new MLDSAPrivateKeyParameters(var4, var2.getPrivateData(), null);
            byte[] var5 = var2.getPublicData();
            if (var5 != null && !Arrays.constantTimeAreEqual(var5, var3.getPublicKey())) {
               throw new InvalidKeySpecException("public key data does not match private key data");
            }
         }

         return new BCMLDSAPrivateKey(var3);
      } else {
         return super.engineGeneratePrivate(var1);
      }
   }

   @Override
   public PublicKey engineGeneratePublic(KeySpec var1) throws InvalidKeySpecException {
      if (var1 instanceof MLDSAPublicKeySpec) {
         MLDSAPublicKeySpec var2 = (MLDSAPublicKeySpec)var1;
         return new BCMLDSAPublicKey(new MLDSAPublicKeyParameters(Utils.getParameters(var2.getParameterSpec().getName()), var2.getPublicData()));
      } else {
         return super.engineGeneratePublic(var1);
      }
   }

   @Override
   public PrivateKey generatePrivate(PrivateKeyInfo var1) throws IOException {
      BCMLDSAPrivateKey var2 = new BCMLDSAPrivateKey(var1);
      if (this.isHashOnly && var2.getAlgorithm().indexOf("WITH") <= 0) {
         MLDSAPrivateKeyParameters var3 = var2.getKeyParams();
         Object var4 = null;
         if (var3.getParameters().equals(MLDSAParameters.ml_dsa_44)) {
            var4 = MLDSAParameters.ml_dsa_44_with_sha512;
         } else if (var3.getParameters().equals(MLDSAParameters.ml_dsa_65)) {
            var4 = MLDSAParameters.ml_dsa_65_with_sha512;
         } else {
            if (!var3.getParameters().equals(MLDSAParameters.ml_dsa_87)) {
               throw new IllegalStateException("unknown ML-DSA parameters");
            }

            var4 = MLDSAParameters.ml_dsa_87_with_sha512;
         }

         MLDSAPrivateKeyParameters var5 = new MLDSAPrivateKeyParameters(
            (MLDSAParameters)var4, var3.getRho(), var3.getK(), var3.getTr(), var3.getS1(), var3.getS2(), var3.getT0(), var3.getT1(), var3.getSeed()
         );
         return new BCMLDSAPrivateKey(var5);
      } else {
         return var2;
      }
   }

   @Override
   public PublicKey generatePublic(SubjectPublicKeyInfo var1) throws IOException {
      return new BCMLDSAPublicKey(var1);
   }

   static {
      pureKeyOids.add(NISTObjectIdentifiers.id_ml_dsa_44);
      pureKeyOids.add(NISTObjectIdentifiers.id_ml_dsa_65);
      pureKeyOids.add(NISTObjectIdentifiers.id_ml_dsa_87);
      hashKeyOids.add(NISTObjectIdentifiers.id_ml_dsa_44);
      hashKeyOids.add(NISTObjectIdentifiers.id_ml_dsa_65);
      hashKeyOids.add(NISTObjectIdentifiers.id_ml_dsa_87);
      hashKeyOids.add(NISTObjectIdentifiers.id_hash_ml_dsa_44_with_sha512);
      hashKeyOids.add(NISTObjectIdentifiers.id_hash_ml_dsa_65_with_sha512);
      hashKeyOids.add(NISTObjectIdentifiers.id_hash_ml_dsa_87_with_sha512);
   }

   public static class Hash extends MLDSAKeyFactorySpi {
      public Hash() {
         super(MLDSAKeyFactorySpi.hashKeyOids);
      }
   }

   public static class HashMLDSA44 extends MLDSAKeyFactorySpi {
      public HashMLDSA44() {
         super(NISTObjectIdentifiers.id_hash_ml_dsa_44_with_sha512);
      }
   }

   public static class HashMLDSA65 extends MLDSAKeyFactorySpi {
      public HashMLDSA65() {
         super(NISTObjectIdentifiers.id_hash_ml_dsa_65_with_sha512);
      }
   }

   public static class HashMLDSA87 extends MLDSAKeyFactorySpi {
      public HashMLDSA87() {
         super(NISTObjectIdentifiers.id_hash_ml_dsa_87_with_sha512);
      }
   }

   public static class MLDSA44 extends MLDSAKeyFactorySpi {
      public MLDSA44() {
         super(NISTObjectIdentifiers.id_ml_dsa_44);
      }
   }

   public static class MLDSA65 extends MLDSAKeyFactorySpi {
      public MLDSA65() {
         super(NISTObjectIdentifiers.id_ml_dsa_65);
      }
   }

   public static class MLDSA87 extends MLDSAKeyFactorySpi {
      public MLDSA87() {
         super(NISTObjectIdentifiers.id_ml_dsa_87);
      }
   }

   public static class Pure extends MLDSAKeyFactorySpi {
      public Pure() {
         super(MLDSAKeyFactorySpi.pureKeyOids);
      }
   }
}
