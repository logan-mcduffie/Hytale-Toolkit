package org.bouncycastle.jcajce.provider.asymmetric.ec;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECPrivateKeySpec;
import java.security.spec.ECPublicKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.util.OpenSSHPublicKeyUtil;
import org.bouncycastle.jcajce.provider.asymmetric.util.BaseKeyFactorySpi;
import org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util;
import org.bouncycastle.jcajce.provider.config.ProviderConfiguration;
import org.bouncycastle.jcajce.provider.util.AsymmetricKeyInfoConverter;
import org.bouncycastle.jcajce.spec.OpenSSHPrivateKeySpec;
import org.bouncycastle.jcajce.spec.OpenSSHPublicKeySpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECParameterSpec;

public class KeyFactorySpi extends BaseKeyFactorySpi implements AsymmetricKeyInfoConverter {
   String algorithm;
   ProviderConfiguration configuration;

   KeyFactorySpi(String var1, ProviderConfiguration var2) {
      this.algorithm = var1;
      this.configuration = var2;
   }

   @Override
   protected Key engineTranslateKey(Key var1) throws InvalidKeyException {
      if (var1 instanceof ECPublicKey) {
         return new BCECPublicKey((ECPublicKey)var1, this.configuration);
      } else if (var1 instanceof ECPrivateKey) {
         return new BCECPrivateKey((ECPrivateKey)var1, this.configuration);
      } else {
         throw new InvalidKeyException("key type unknown");
      }
   }

   @Override
   protected KeySpec engineGetKeySpec(Key var1, Class var2) throws InvalidKeySpecException {
      if ((var2.isAssignableFrom(KeySpec.class) || var2.isAssignableFrom(ECPublicKeySpec.class)) && var1 instanceof ECPublicKey) {
         ECPublicKey var11 = (ECPublicKey)var1;
         if (var11.getParams() != null) {
            return new ECPublicKeySpec(var11.getW(), var11.getParams());
         } else {
            ECParameterSpec var15 = BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa();
            return new ECPublicKeySpec(var11.getW(), EC5Util.convertSpec(EC5Util.convertCurve(var15.getCurve(), var15.getSeed()), var15));
         }
      } else if ((var2.isAssignableFrom(KeySpec.class) || var2.isAssignableFrom(ECPrivateKeySpec.class)) && var1 instanceof ECPrivateKey) {
         ECPrivateKey var10 = (ECPrivateKey)var1;
         if (var10.getParams() != null) {
            return new ECPrivateKeySpec(var10.getS(), var10.getParams());
         } else {
            ECParameterSpec var14 = BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa();
            return new ECPrivateKeySpec(var10.getS(), EC5Util.convertSpec(EC5Util.convertCurve(var14.getCurve(), var14.getSeed()), var14));
         }
      } else if (var2.isAssignableFrom(org.bouncycastle.jce.spec.ECPublicKeySpec.class) && var1 instanceof ECPublicKey) {
         ECPublicKey var9 = (ECPublicKey)var1;
         if (var9.getParams() != null) {
            return new org.bouncycastle.jce.spec.ECPublicKeySpec(EC5Util.convertPoint(var9.getParams(), var9.getW()), EC5Util.convertSpec(var9.getParams()));
         } else {
            ECParameterSpec var13 = BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa();
            return new org.bouncycastle.jce.spec.ECPublicKeySpec(EC5Util.convertPoint(var9.getParams(), var9.getW()), var13);
         }
      } else if (var2.isAssignableFrom(org.bouncycastle.jce.spec.ECPrivateKeySpec.class) && var1 instanceof ECPrivateKey) {
         ECPrivateKey var8 = (ECPrivateKey)var1;
         if (var8.getParams() != null) {
            return new org.bouncycastle.jce.spec.ECPrivateKeySpec(var8.getS(), EC5Util.convertSpec(var8.getParams()));
         } else {
            ECParameterSpec var12 = BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa();
            return new org.bouncycastle.jce.spec.ECPrivateKeySpec(var8.getS(), var12);
         }
      } else if (var2.isAssignableFrom(OpenSSHPublicKeySpec.class) && var1 instanceof ECPublicKey) {
         if (var1 instanceof BCECPublicKey) {
            BCECPublicKey var3 = (BCECPublicKey)var1;
            ECParameterSpec var4 = var3.getParameters();

            try {
               return new OpenSSHPublicKeySpec(
                  OpenSSHPublicKeyUtil.encodePublicKey(
                     new ECPublicKeyParameters(var3.getQ(), new ECDomainParameters(var4.getCurve(), var4.getG(), var4.getN(), var4.getH(), var4.getSeed()))
                  )
               );
            } catch (IOException var6) {
               throw new IllegalArgumentException("unable to produce encoding: " + var6.getMessage());
            }
         } else {
            throw new IllegalArgumentException("invalid key type: " + var1.getClass().getName());
         }
      } else if (!var2.isAssignableFrom(OpenSSHPrivateKeySpec.class) || !(var1 instanceof ECPrivateKey)) {
         return super.engineGetKeySpec(var1, var2);
      } else if (var1 instanceof BCECPrivateKey) {
         try {
            return new OpenSSHPrivateKeySpec(PrivateKeyInfo.getInstance(var1.getEncoded()).parsePrivateKey().toASN1Primitive().getEncoded());
         } catch (IOException var7) {
            throw new IllegalArgumentException("cannot encoded key: " + var7.getMessage());
         }
      } else {
         throw new IllegalArgumentException("invalid key type: " + var1.getClass().getName());
      }
   }

   @Override
   protected PrivateKey engineGeneratePrivate(KeySpec var1) throws InvalidKeySpecException {
      if (var1 instanceof org.bouncycastle.jce.spec.ECPrivateKeySpec) {
         return new BCECPrivateKey(this.algorithm, (org.bouncycastle.jce.spec.ECPrivateKeySpec)var1, this.configuration);
      } else if (var1 instanceof ECPrivateKeySpec) {
         return new BCECPrivateKey(this.algorithm, (ECPrivateKeySpec)var1, this.configuration);
      } else if (var1 instanceof OpenSSHPrivateKeySpec) {
         org.bouncycastle.asn1.sec.ECPrivateKey var2 = org.bouncycastle.asn1.sec.ECPrivateKey.getInstance(((OpenSSHPrivateKeySpec)var1).getEncoded());

         try {
            return new BCECPrivateKey(
               this.algorithm,
               new PrivateKeyInfo(new AlgorithmIdentifier(X9ObjectIdentifiers.id_ecPublicKey, var2.getParametersObject()), var2),
               this.configuration
            );
         } catch (IOException var4) {
            throw new InvalidKeySpecException("bad encoding: " + var4.getMessage());
         }
      } else {
         return super.engineGeneratePrivate(var1);
      }
   }

   @Override
   protected PublicKey engineGeneratePublic(KeySpec var1) throws InvalidKeySpecException {
      try {
         if (var1 instanceof org.bouncycastle.jce.spec.ECPublicKeySpec) {
            return new BCECPublicKey(this.algorithm, (org.bouncycastle.jce.spec.ECPublicKeySpec)var1, this.configuration);
         }

         if (var1 instanceof ECPublicKeySpec) {
            return new BCECPublicKey(this.algorithm, (ECPublicKeySpec)var1, this.configuration);
         }

         if (var1 instanceof OpenSSHPublicKeySpec) {
            AsymmetricKeyParameter var2 = OpenSSHPublicKeyUtil.parsePublicKey(((OpenSSHPublicKeySpec)var1).getEncoded());
            if (var2 instanceof ECPublicKeyParameters) {
               ECDomainParameters var3 = ((ECPublicKeyParameters)var2).getParameters();
               return this.engineGeneratePublic(
                  new org.bouncycastle.jce.spec.ECPublicKeySpec(
                     ((ECPublicKeyParameters)var2).getQ(), new ECParameterSpec(var3.getCurve(), var3.getG(), var3.getN(), var3.getH(), var3.getSeed())
                  )
               );
            }

            throw new IllegalArgumentException("openssh key is not ec public key");
         }
      } catch (Exception var4) {
         throw new InvalidKeySpecException("invalid KeySpec: " + var4.getMessage(), var4);
      }

      return super.engineGeneratePublic(var1);
   }

   @Override
   public PrivateKey generatePrivate(PrivateKeyInfo var1) throws IOException {
      ASN1ObjectIdentifier var2 = var1.getPrivateKeyAlgorithm().getAlgorithm();
      if (var2.equals(X9ObjectIdentifiers.id_ecPublicKey)) {
         return new BCECPrivateKey(this.algorithm, var1, this.configuration);
      } else {
         throw new IOException("algorithm identifier " + var2 + " in key not recognised");
      }
   }

   @Override
   public PublicKey generatePublic(SubjectPublicKeyInfo var1) throws IOException {
      ASN1ObjectIdentifier var2 = var1.getAlgorithm().getAlgorithm();
      if (var2.equals(X9ObjectIdentifiers.id_ecPublicKey)) {
         return new BCECPublicKey(this.algorithm, var1, this.configuration);
      } else {
         throw new IOException("algorithm identifier " + var2 + " in key not recognised");
      }
   }

   public static class EC extends KeyFactorySpi {
      public EC() {
         super("EC", BouncyCastleProvider.CONFIGURATION);
      }
   }

   public static class ECDH extends KeyFactorySpi {
      public ECDH() {
         super("ECDH", BouncyCastleProvider.CONFIGURATION);
      }
   }

   public static class ECDHC extends KeyFactorySpi {
      public ECDHC() {
         super("ECDHC", BouncyCastleProvider.CONFIGURATION);
      }
   }

   public static class ECDSA extends KeyFactorySpi {
      public ECDSA() {
         super("ECDSA", BouncyCastleProvider.CONFIGURATION);
      }
   }

   public static class ECGOST3410 extends KeyFactorySpi {
      public ECGOST3410() {
         super("ECGOST3410", BouncyCastleProvider.CONFIGURATION);
      }
   }

   public static class ECGOST3410_2012 extends KeyFactorySpi {
      public ECGOST3410_2012() {
         super("ECGOST3410-2012", BouncyCastleProvider.CONFIGURATION);
      }
   }

   public static class ECMQV extends KeyFactorySpi {
      public ECMQV() {
         super("ECMQV", BouncyCastleProvider.CONFIGURATION);
      }
   }
}
