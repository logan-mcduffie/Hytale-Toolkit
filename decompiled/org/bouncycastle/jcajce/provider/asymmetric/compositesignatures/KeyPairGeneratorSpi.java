package org.bouncycastle.jcajce.provider.asymmetric.compositesignatures;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.internal.asn1.iana.IANAObjectIdentifiers;
import org.bouncycastle.jcajce.CompositePrivateKey;
import org.bouncycastle.jcajce.CompositePublicKey;

public class KeyPairGeneratorSpi extends java.security.KeyPairGeneratorSpi {
   private final ASN1ObjectIdentifier algorithm;
   private final KeyPairGenerator[] generators;
   private SecureRandom secureRandom;
   private boolean parametersInitialized = false;

   KeyPairGeneratorSpi(ASN1ObjectIdentifier var1) {
      this.algorithm = var1;
      String[] var2 = CompositeIndex.getPairing(var1);
      AlgorithmParameterSpec[] var3 = CompositeIndex.getKeyPairSpecs(var1);
      this.generators = new KeyPairGenerator[var2.length];

      for (int var4 = 0; var4 != var2.length; var4++) {
         try {
            this.generators[var4] = KeyPairGenerator.getInstance(CompositeIndex.getBaseName(var2[var4]), "BC");
            AlgorithmParameterSpec var5 = var3[var4];
            if (var5 != null) {
               this.generators[var4].initialize(var5);
            }
         } catch (Exception var6) {
            throw new IllegalStateException("unable to create base generator: " + var6.getMessage());
         }
      }
   }

   @Override
   public void initialize(int var1, SecureRandom var2) {
      throw new IllegalArgumentException("use AlgorithmParameterSpec");
   }

   @Override
   public void initialize(AlgorithmParameterSpec var1, SecureRandom var2) throws InvalidAlgorithmParameterException {
      if (var1 != null) {
         throw new IllegalArgumentException(
            "Use initialize only for custom SecureRandom. AlgorithmParameterSpec must be null because it is determined by algorithm name."
         );
      } else {
         AlgorithmParameterSpec[] var3 = CompositeIndex.getKeyPairSpecs(this.algorithm);

         for (int var4 = 0; var4 != var3.length; var4++) {
            AlgorithmParameterSpec var5 = var3[var4];
            if (var5 != null) {
               this.generators[var4].initialize(var5, var2);
            }
         }
      }
   }

   @Override
   public KeyPair generateKeyPair() {
      return this.getCompositeKeyPair();
   }

   private KeyPair getCompositeKeyPair() {
      PublicKey[] var1 = new PublicKey[this.generators.length];
      PrivateKey[] var2 = new PrivateKey[this.generators.length];

      for (int var3 = 0; var3 < this.generators.length; var3++) {
         KeyPair var4 = this.generators[var3].generateKeyPair();
         var1[var3] = var4.getPublic();
         var2[var3] = var4.getPrivate();
      }

      CompositePublicKey var5 = new CompositePublicKey(this.algorithm, var1);
      CompositePrivateKey var6 = new CompositePrivateKey(this.algorithm, var2);
      return new KeyPair(var5, var6);
   }

   public static final class MLDSA44_ECDSA_P256_SHA256 extends KeyPairGeneratorSpi {
      public MLDSA44_ECDSA_P256_SHA256() {
         super(IANAObjectIdentifiers.id_MLDSA44_ECDSA_P256_SHA256);
      }
   }

   public static final class MLDSA44_Ed25519_SHA512 extends KeyPairGeneratorSpi {
      public MLDSA44_Ed25519_SHA512() {
         super(IANAObjectIdentifiers.id_MLDSA44_Ed25519_SHA512);
      }
   }

   public static final class MLDSA44_RSA2048_PKCS15_SHA256 extends KeyPairGeneratorSpi {
      public MLDSA44_RSA2048_PKCS15_SHA256() {
         super(IANAObjectIdentifiers.id_MLDSA44_RSA2048_PKCS15_SHA256);
      }
   }

   public static final class MLDSA44_RSA2048_PSS_SHA256 extends KeyPairGeneratorSpi {
      public MLDSA44_RSA2048_PSS_SHA256() {
         super(IANAObjectIdentifiers.id_MLDSA44_RSA2048_PSS_SHA256);
      }
   }

   public static final class MLDSA65_ECDSA_P256_SHA512 extends KeyPairGeneratorSpi {
      public MLDSA65_ECDSA_P256_SHA512() {
         super(IANAObjectIdentifiers.id_MLDSA65_ECDSA_P256_SHA512);
      }
   }

   public static final class MLDSA65_ECDSA_P384_SHA512 extends KeyPairGeneratorSpi {
      public MLDSA65_ECDSA_P384_SHA512() {
         super(IANAObjectIdentifiers.id_MLDSA65_ECDSA_P384_SHA512);
      }
   }

   public static final class MLDSA65_ECDSA_brainpoolP256r1_SHA512 extends KeyPairGeneratorSpi {
      public MLDSA65_ECDSA_brainpoolP256r1_SHA512() {
         super(IANAObjectIdentifiers.id_MLDSA65_ECDSA_brainpoolP256r1_SHA512);
      }
   }

   public static final class MLDSA65_Ed25519_SHA512 extends KeyPairGeneratorSpi {
      public MLDSA65_Ed25519_SHA512() {
         super(IANAObjectIdentifiers.id_MLDSA65_Ed25519_SHA512);
      }
   }

   public static final class MLDSA65_RSA3072_PKCS15_SHA512 extends KeyPairGeneratorSpi {
      public MLDSA65_RSA3072_PKCS15_SHA512() {
         super(IANAObjectIdentifiers.id_MLDSA65_RSA3072_PKCS15_SHA512);
      }
   }

   public static final class MLDSA65_RSA3072_PSS_SHA512 extends KeyPairGeneratorSpi {
      public MLDSA65_RSA3072_PSS_SHA512() {
         super(IANAObjectIdentifiers.id_MLDSA65_RSA3072_PSS_SHA512);
      }
   }

   public static final class MLDSA65_RSA4096_PKCS15_SHA512 extends KeyPairGeneratorSpi {
      public MLDSA65_RSA4096_PKCS15_SHA512() {
         super(IANAObjectIdentifiers.id_MLDSA65_RSA4096_PKCS15_SHA512);
      }
   }

   public static final class MLDSA65_RSA4096_PSS_SHA512 extends KeyPairGeneratorSpi {
      public MLDSA65_RSA4096_PSS_SHA512() {
         super(IANAObjectIdentifiers.id_MLDSA65_RSA4096_PSS_SHA512);
      }
   }

   public static final class MLDSA87_ECDSA_P384_SHA512 extends KeyPairGeneratorSpi {
      public MLDSA87_ECDSA_P384_SHA512() {
         super(IANAObjectIdentifiers.id_MLDSA87_ECDSA_P384_SHA512);
      }
   }

   public static final class MLDSA87_ECDSA_P521_SHA512 extends KeyPairGeneratorSpi {
      public MLDSA87_ECDSA_P521_SHA512() {
         super(IANAObjectIdentifiers.id_MLDSA87_ECDSA_P521_SHA512);
      }
   }

   public static final class MLDSA87_ECDSA_brainpoolP384r1_SHA512 extends KeyPairGeneratorSpi {
      public MLDSA87_ECDSA_brainpoolP384r1_SHA512() {
         super(IANAObjectIdentifiers.id_MLDSA87_ECDSA_brainpoolP384r1_SHA512);
      }
   }

   public static final class MLDSA87_Ed448_SHAKE256 extends KeyPairGeneratorSpi {
      public MLDSA87_Ed448_SHAKE256() {
         super(IANAObjectIdentifiers.id_MLDSA87_Ed448_SHAKE256);
      }
   }

   public static final class MLDSA87_RSA3072_PSS_SHA512 extends KeyPairGeneratorSpi {
      public MLDSA87_RSA3072_PSS_SHA512() {
         super(IANAObjectIdentifiers.id_MLDSA87_RSA3072_PSS_SHA512);
      }
   }

   public static final class MLDSA87_RSA4096_PSS_SHA512 extends KeyPairGeneratorSpi {
      public MLDSA87_RSA4096_PSS_SHA512() {
         super(IANAObjectIdentifiers.id_MLDSA87_RSA4096_PSS_SHA512);
      }
   }
}
