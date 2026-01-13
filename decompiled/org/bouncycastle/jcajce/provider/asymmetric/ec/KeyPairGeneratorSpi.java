package org.bouncycastle.jcajce.provider.asymmetric.ec;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.ECGenParameterSpec;
import java.util.Hashtable;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECKeyGenerationParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util;
import org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;
import org.bouncycastle.jcajce.provider.config.ProviderConfiguration;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveGenParameterSpec;
import org.bouncycastle.jce.spec.ECNamedCurveSpec;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.Integers;

public abstract class KeyPairGeneratorSpi extends KeyPairGenerator {
   public KeyPairGeneratorSpi(String var1) {
      super(var1);
   }

   public static class EC extends KeyPairGeneratorSpi {
      ECKeyGenerationParameters param;
      ECKeyPairGenerator engine = new ECKeyPairGenerator();
      Object ecParams = null;
      int strength = 239;
      SecureRandom random = CryptoServicesRegistrar.getSecureRandom();
      boolean initialised = false;
      String algorithm;
      ProviderConfiguration configuration;
      private static Hashtable ecParameters = new Hashtable();

      public EC() {
         super("EC");
         this.algorithm = "EC";
         this.configuration = BouncyCastleProvider.CONFIGURATION;
      }

      public EC(String var1, ProviderConfiguration var2) {
         super(var1);
         this.algorithm = var1;
         this.configuration = var2;
      }

      @Override
      public void initialize(int var1, SecureRandom var2) {
         this.strength = var1;
         this.random = var2;
         ECGenParameterSpec var3 = (ECGenParameterSpec)ecParameters.get(Integers.valueOf(var1));
         if (var3 == null) {
            throw new InvalidParameterException("unknown key size.");
         } else {
            try {
               this.initialize(var3, var2);
            } catch (InvalidAlgorithmParameterException var5) {
               throw new InvalidParameterException("key size not configurable.");
            }
         }
      }

      @Override
      public void initialize(AlgorithmParameterSpec var1, SecureRandom var2) throws InvalidAlgorithmParameterException {
         if (var1 == null) {
            ECParameterSpec var3 = this.configuration.getEcImplicitlyCa();
            if (var3 == null) {
               throw new InvalidAlgorithmParameterException("null parameter passed but no implicitCA set");
            }

            this.ecParams = null;
            this.param = this.createKeyGenParamsBC(var3, var2);
         } else if (var1 instanceof ECParameterSpec) {
            this.ecParams = var1;
            this.param = this.createKeyGenParamsBC((ECParameterSpec)var1, var2);
         } else if (var1 instanceof java.security.spec.ECParameterSpec) {
            this.ecParams = var1;
            this.param = this.createKeyGenParamsJCE((java.security.spec.ECParameterSpec)var1, var2);
         } else if (var1 instanceof ECGenParameterSpec) {
            this.initializeNamedCurve(((ECGenParameterSpec)var1).getName(), var2);
         } else if (var1 instanceof ECNamedCurveGenParameterSpec) {
            this.initializeNamedCurve(((ECNamedCurveGenParameterSpec)var1).getName(), var2);
         } else {
            String var4 = ECUtil.getNameFrom(var1);
            if (var4 == null) {
               throw new InvalidAlgorithmParameterException("invalid parameterSpec: " + var1);
            }

            this.initializeNamedCurve(var4, var2);
         }

         this.engine.init(this.param);
         this.initialised = true;
      }

      @Override
      public KeyPair generateKeyPair() {
         if (!this.initialised) {
            this.initialize(this.strength, new SecureRandom());
         }

         AsymmetricCipherKeyPair var1 = this.engine.generateKeyPair();
         ECPublicKeyParameters var2 = (ECPublicKeyParameters)var1.getPublic();
         ECPrivateKeyParameters var3 = (ECPrivateKeyParameters)var1.getPrivate();
         if (this.ecParams instanceof ECParameterSpec) {
            ECParameterSpec var6 = (ECParameterSpec)this.ecParams;
            BCECPublicKey var7 = new BCECPublicKey(this.algorithm, var2, var6, this.configuration);
            return new KeyPair(var7, new BCECPrivateKey(this.algorithm, var3, var7, var6, this.configuration));
         } else if (this.ecParams == null) {
            return new KeyPair(new BCECPublicKey(this.algorithm, var2, this.configuration), new BCECPrivateKey(this.algorithm, var3, this.configuration));
         } else {
            java.security.spec.ECParameterSpec var4 = (java.security.spec.ECParameterSpec)this.ecParams;
            BCECPublicKey var5 = new BCECPublicKey(this.algorithm, var2, var4, this.configuration);
            return new KeyPair(var5, new BCECPrivateKey(this.algorithm, var3, var5, var4, this.configuration));
         }
      }

      protected ECKeyGenerationParameters createKeyGenParamsBC(ECParameterSpec var1, SecureRandom var2) {
         return new ECKeyGenerationParameters(new ECDomainParameters(var1.getCurve(), var1.getG(), var1.getN(), var1.getH()), var2);
      }

      protected ECKeyGenerationParameters createKeyGenParamsJCE(java.security.spec.ECParameterSpec var1, SecureRandom var2) {
         if (var1 instanceof ECNamedCurveSpec) {
            String var3 = ((ECNamedCurveSpec)var1).getName();
            X9ECParameters var4 = ECUtils.getDomainParametersFromName(var3, this.configuration);
            if (null != var4) {
               return this.createKeyGenParamsJCE(var4, var2);
            }
         }

         ECCurve var8 = EC5Util.convertCurve(var1.getCurve());
         ECPoint var9 = EC5Util.convertPoint(var8, var1.getGenerator());
         BigInteger var5 = var1.getOrder();
         BigInteger var6 = BigInteger.valueOf(var1.getCofactor());
         ECDomainParameters var7 = new ECDomainParameters(var8, var9, var5, var6);
         return new ECKeyGenerationParameters(var7, var2);
      }

      protected ECKeyGenerationParameters createKeyGenParamsJCE(X9ECParameters var1, SecureRandom var2) {
         ECDomainParameters var3 = new ECDomainParameters(var1.getCurve(), var1.getG(), var1.getN(), var1.getH());
         return new ECKeyGenerationParameters(var3, var2);
      }

      protected void initializeNamedCurve(String var1, SecureRandom var2) throws InvalidAlgorithmParameterException {
         X9ECParameters var3 = ECUtils.getDomainParametersFromName(var1, this.configuration);
         if (null == var3) {
            throw new InvalidAlgorithmParameterException("unknown curve name: " + var1);
         } else {
            Object var4 = null;
            this.ecParams = new ECNamedCurveSpec(var1, var3.getCurve(), var3.getG(), var3.getN(), var3.getH(), (byte[])var4);
            this.param = this.createKeyGenParamsJCE(var3, var2);
         }
      }

      static {
         ecParameters.put(Integers.valueOf(192), new ECGenParameterSpec("prime192v1"));
         ecParameters.put(Integers.valueOf(239), new ECGenParameterSpec("prime239v1"));
         ecParameters.put(Integers.valueOf(256), new ECGenParameterSpec("prime256v1"));
         ecParameters.put(Integers.valueOf(224), new ECGenParameterSpec("P-224"));
         ecParameters.put(Integers.valueOf(384), new ECGenParameterSpec("P-384"));
         ecParameters.put(Integers.valueOf(521), new ECGenParameterSpec("P-521"));
      }
   }

   public static class ECDH extends KeyPairGeneratorSpi.EC {
      public ECDH() {
         super("ECDH", BouncyCastleProvider.CONFIGURATION);
      }
   }

   public static class ECDHC extends KeyPairGeneratorSpi.EC {
      public ECDHC() {
         super("ECDHC", BouncyCastleProvider.CONFIGURATION);
      }
   }

   public static class ECDSA extends KeyPairGeneratorSpi.EC {
      public ECDSA() {
         super("ECDSA", BouncyCastleProvider.CONFIGURATION);
      }
   }

   public static class ECMQV extends KeyPairGeneratorSpi.EC {
      public ECMQV() {
         super("ECMQV", BouncyCastleProvider.CONFIGURATION);
      }
   }
}
