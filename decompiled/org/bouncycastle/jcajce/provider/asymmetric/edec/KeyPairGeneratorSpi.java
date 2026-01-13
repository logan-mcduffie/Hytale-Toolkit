package org.bouncycastle.jcajce.provider.asymmetric.edec;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidParameterException;
import java.security.KeyPair;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.ECGenParameterSpec;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.generators.Ed25519KeyPairGenerator;
import org.bouncycastle.crypto.generators.Ed448KeyPairGenerator;
import org.bouncycastle.crypto.generators.X25519KeyPairGenerator;
import org.bouncycastle.crypto.generators.X448KeyPairGenerator;
import org.bouncycastle.crypto.params.Ed25519KeyGenerationParameters;
import org.bouncycastle.crypto.params.Ed448KeyGenerationParameters;
import org.bouncycastle.crypto.params.X25519KeyGenerationParameters;
import org.bouncycastle.crypto.params.X448KeyGenerationParameters;
import org.bouncycastle.internal.asn1.edec.EdECObjectIdentifiers;
import org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;
import org.bouncycastle.jcajce.spec.EdDSAParameterSpec;
import org.bouncycastle.jcajce.spec.XDHParameterSpec;
import org.bouncycastle.jce.spec.ECNamedCurveGenParameterSpec;

public class KeyPairGeneratorSpi extends java.security.KeyPairGeneratorSpi {
   private static final int EdDSA = -1;
   private static final int XDH = -2;
   private static final int Ed25519 = 1;
   private static final int Ed448 = 2;
   private static final int X25519 = 3;
   private static final int X448 = 4;
   private final int algorithmDeclared;
   private int algorithmInitialized;
   private SecureRandom secureRandom;
   private AsymmetricCipherKeyPairGenerator generator;

   KeyPairGeneratorSpi(int var1) {
      this.algorithmDeclared = var1;
      if (getAlgorithmFamily(var1) != var1) {
         this.algorithmInitialized = var1;
      }
   }

   @Override
   public void initialize(int var1, SecureRandom var2) {
      int var3 = this.getAlgorithmForStrength(var1);
      this.algorithmInitialized = var3;
      this.secureRandom = var2;
      this.generator = null;
   }

   @Override
   public void initialize(AlgorithmParameterSpec var1, SecureRandom var2) throws InvalidAlgorithmParameterException {
      String var3 = getNameFromParams(var1);
      if (null == var3) {
         throw new InvalidAlgorithmParameterException("invalid parameterSpec: " + var1);
      } else {
         int var4 = getAlgorithmForName(var3);
         if (this.algorithmDeclared != var4 && this.algorithmDeclared != getAlgorithmFamily(var4)) {
            throw new InvalidAlgorithmParameterException("parameterSpec for wrong curve type");
         } else {
            this.algorithmInitialized = var4;
            this.secureRandom = var2;
            this.generator = null;
         }
      }
   }

   @Override
   public KeyPair generateKeyPair() {
      if (this.algorithmInitialized == 0) {
         throw new IllegalStateException("generator not correctly initialized");
      } else {
         if (null == this.generator) {
            this.generator = this.setupGenerator();
         }

         AsymmetricCipherKeyPair var1 = this.generator.generateKeyPair();
         switch (this.algorithmInitialized) {
            case 1:
            case 2:
               return new KeyPair(new BCEdDSAPublicKey(var1.getPublic()), new BCEdDSAPrivateKey(var1.getPrivate()));
            case 3:
            case 4:
               return new KeyPair(new BCXDHPublicKey(var1.getPublic()), new BCXDHPrivateKey(var1.getPrivate()));
            default:
               throw new IllegalStateException("generator not correctly initialized");
         }
      }
   }

   private int getAlgorithmForStrength(int var1) {
      switch (var1) {
         case 255:
         case 256:
            switch (this.algorithmDeclared) {
               case -2:
               case 3:
                  return 3;
               case -1:
               case 1:
                  return 1;
               case 0:
               case 2:
               default:
                  throw new InvalidParameterException("key size not configurable");
            }
         case 448:
            switch (this.algorithmDeclared) {
               case -2:
               case 4:
                  return 4;
               case -1:
               case 2:
                  return 2;
               case 0:
               case 1:
               case 3:
               default:
                  throw new InvalidParameterException("key size not configurable");
            }
         default:
            throw new InvalidParameterException("unknown key size");
      }
   }

   private AsymmetricCipherKeyPairGenerator setupGenerator() {
      if (null == this.secureRandom) {
         this.secureRandom = CryptoServicesRegistrar.getSecureRandom();
      }

      switch (this.algorithmInitialized) {
         case 1:
            Ed25519KeyPairGenerator var4 = new Ed25519KeyPairGenerator();
            var4.init(new Ed25519KeyGenerationParameters(this.secureRandom));
            return var4;
         case 2:
            Ed448KeyPairGenerator var3 = new Ed448KeyPairGenerator();
            var3.init(new Ed448KeyGenerationParameters(this.secureRandom));
            return var3;
         case 3:
            X25519KeyPairGenerator var2 = new X25519KeyPairGenerator();
            var2.init(new X25519KeyGenerationParameters(this.secureRandom));
            return var2;
         case 4:
            X448KeyPairGenerator var1 = new X448KeyPairGenerator();
            var1.init(new X448KeyGenerationParameters(this.secureRandom));
            return var1;
         default:
            throw new IllegalStateException("generator not correctly initialized");
      }
   }

   private static int getAlgorithmFamily(int var0) {
      switch (var0) {
         case 1:
         case 2:
            return -1;
         case 3:
         case 4:
            return -2;
         default:
            return var0;
      }
   }

   private static int getAlgorithmForName(String var0) throws InvalidAlgorithmParameterException {
      if (var0.equalsIgnoreCase("X25519") || var0.equals(EdECObjectIdentifiers.id_X25519.getId())) {
         return 3;
      } else if (var0.equalsIgnoreCase("Ed25519") || var0.equals(EdECObjectIdentifiers.id_Ed25519.getId())) {
         return 1;
      } else if (var0.equalsIgnoreCase("X448") || var0.equals(EdECObjectIdentifiers.id_X448.getId())) {
         return 4;
      } else if (!var0.equalsIgnoreCase("Ed448") && !var0.equals(EdECObjectIdentifiers.id_Ed448.getId())) {
         throw new InvalidAlgorithmParameterException("invalid parameterSpec name: " + var0);
      } else {
         return 2;
      }
   }

   private static String getNameFromParams(AlgorithmParameterSpec var0) throws InvalidAlgorithmParameterException {
      if (var0 instanceof ECGenParameterSpec) {
         return ((ECGenParameterSpec)var0).getName();
      } else if (var0 instanceof ECNamedCurveGenParameterSpec) {
         return ((ECNamedCurveGenParameterSpec)var0).getName();
      } else if (var0 instanceof EdDSAParameterSpec) {
         return ((EdDSAParameterSpec)var0).getCurveName();
      } else {
         return var0 instanceof XDHParameterSpec ? ((XDHParameterSpec)var0).getCurveName() : ECUtil.getNameFrom(var0);
      }
   }

   public static final class Ed25519 extends KeyPairGeneratorSpi {
      public Ed25519() {
         super(1);
      }
   }

   public static final class Ed448 extends KeyPairGeneratorSpi {
      public Ed448() {
         super(2);
      }
   }

   public static final class EdDSA extends KeyPairGeneratorSpi {
      public EdDSA() {
         super(-1);
      }
   }

   public static final class X25519 extends KeyPairGeneratorSpi {
      public X25519() {
         super(3);
      }
   }

   public static final class X448 extends KeyPairGeneratorSpi {
      public X448() {
         super(4);
      }
   }

   public static final class XDH extends KeyPairGeneratorSpi {
      public XDH() {
         super(-2);
      }
   }
}
