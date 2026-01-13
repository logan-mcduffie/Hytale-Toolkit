package org.bouncycastle.jcajce.provider.asymmetric.mlkem;

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
import org.bouncycastle.jcajce.spec.MLKEMPrivateKeySpec;
import org.bouncycastle.jcajce.spec.MLKEMPublicKeySpec;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMParameters;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMPublicKeyParameters;
import org.bouncycastle.pqc.jcajce.provider.util.BaseKeyFactorySpi;
import org.bouncycastle.util.Arrays;

public class MLKEMKeyFactorySpi extends BaseKeyFactorySpi {
   private static final Set<ASN1ObjectIdentifier> keyOids = new HashSet<>();

   public MLKEMKeyFactorySpi() {
      super(keyOids);
   }

   public MLKEMKeyFactorySpi(ASN1ObjectIdentifier var1) {
      super(var1);
   }

   @Override
   public final KeySpec engineGetKeySpec(Key var1, Class var2) throws InvalidKeySpecException {
      if (var1 instanceof BCMLKEMPrivateKey) {
         if (PKCS8EncodedKeySpec.class.isAssignableFrom(var2)) {
            return new PKCS8EncodedKeySpec(var1.getEncoded());
         }

         if (MLKEMPrivateKeySpec.class.isAssignableFrom(var2)) {
            BCMLKEMPrivateKey var5 = (BCMLKEMPrivateKey)var1;
            byte[] var4 = var5.getSeed();
            if (var4 != null) {
               return new MLKEMPrivateKeySpec(var5.getParameterSpec(), var4);
            }

            return new MLKEMPrivateKeySpec(var5.getParameterSpec(), var5.getPrivateData(), var5.getPublicKey().getPublicData());
         }

         if (MLKEMPublicKeySpec.class.isAssignableFrom(var2)) {
            BCMLKEMPrivateKey var3 = (BCMLKEMPrivateKey)var1;
            return new MLKEMPublicKeySpec(var3.getParameterSpec(), var3.getPublicKey().getPublicData());
         }
      } else {
         if (!(var1 instanceof BCMLKEMPublicKey)) {
            throw new InvalidKeySpecException("Unsupported key type: " + var1.getClass() + ".");
         }

         if (X509EncodedKeySpec.class.isAssignableFrom(var2)) {
            return new X509EncodedKeySpec(var1.getEncoded());
         }

         if (MLKEMPublicKeySpec.class.isAssignableFrom(var2)) {
            BCMLKEMPublicKey var6 = (BCMLKEMPublicKey)var1;
            return new MLKEMPublicKeySpec(var6.getParameterSpec(), var6.getPublicData());
         }
      }

      throw new InvalidKeySpecException("unknown key specification: " + var2 + ".");
   }

   @Override
   public final Key engineTranslateKey(Key var1) throws InvalidKeyException {
      if (!(var1 instanceof BCMLKEMPrivateKey) && !(var1 instanceof BCMLKEMPublicKey)) {
         throw new InvalidKeyException("unsupported key type");
      } else {
         return var1;
      }
   }

   @Override
   public PrivateKey engineGeneratePrivate(KeySpec var1) throws InvalidKeySpecException {
      if (var1 instanceof MLKEMPrivateKeySpec) {
         MLKEMPrivateKeySpec var2 = (MLKEMPrivateKeySpec)var1;
         MLKEMParameters var4 = Utils.getParameters(var2.getParameterSpec().getName());
         MLKEMPrivateKeyParameters var3;
         if (var2.isSeed()) {
            var3 = new MLKEMPrivateKeyParameters(var4, var2.getSeed());
         } else {
            var3 = new MLKEMPrivateKeyParameters(var4, var2.getPrivateData());
            byte[] var5 = var2.getPublicData();
            if (var5 != null && !Arrays.constantTimeAreEqual(var5, var3.getPublicKey())) {
               throw new InvalidKeySpecException("public key data does not match private key data");
            }
         }

         return new BCMLKEMPrivateKey(var3);
      } else {
         return super.engineGeneratePrivate(var1);
      }
   }

   @Override
   public PublicKey engineGeneratePublic(KeySpec var1) throws InvalidKeySpecException {
      if (var1 instanceof MLKEMPublicKeySpec) {
         MLKEMPublicKeySpec var2 = (MLKEMPublicKeySpec)var1;
         return new BCMLKEMPublicKey(new MLKEMPublicKeyParameters(Utils.getParameters(var2.getParameterSpec().getName()), var2.getPublicData()));
      } else {
         return super.engineGeneratePublic(var1);
      }
   }

   @Override
   public PrivateKey generatePrivate(PrivateKeyInfo var1) throws IOException {
      return new BCMLKEMPrivateKey(var1);
   }

   @Override
   public PublicKey generatePublic(SubjectPublicKeyInfo var1) throws IOException {
      return new BCMLKEMPublicKey(var1);
   }

   static {
      keyOids.add(NISTObjectIdentifiers.id_alg_ml_kem_512);
      keyOids.add(NISTObjectIdentifiers.id_alg_ml_kem_768);
      keyOids.add(NISTObjectIdentifiers.id_alg_ml_kem_1024);
   }

   public static class MLKEM1024 extends MLKEMKeyFactorySpi {
      public MLKEM1024() {
         super(NISTObjectIdentifiers.id_alg_ml_kem_1024);
      }
   }

   public static class MLKEM512 extends MLKEMKeyFactorySpi {
      public MLKEM512() {
         super(NISTObjectIdentifiers.id_alg_ml_kem_512);
      }
   }

   public static class MLKEM768 extends MLKEMKeyFactorySpi {
      public MLKEM768() {
         super(NISTObjectIdentifiers.id_alg_ml_kem_768);
      }
   }
}
