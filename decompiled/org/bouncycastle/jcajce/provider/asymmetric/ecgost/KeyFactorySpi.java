package org.bouncycastle.jcajce.provider.asymmetric.ecgost;

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
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.jcajce.provider.asymmetric.util.BaseKeyFactorySpi;
import org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECParameterSpec;

public class KeyFactorySpi extends BaseKeyFactorySpi {
   @Override
   protected KeySpec engineGetKeySpec(Key var1, Class var2) throws InvalidKeySpecException {
      if (var2.isAssignableFrom(ECPublicKeySpec.class) && var1 instanceof ECPublicKey) {
         ECPublicKey var7 = (ECPublicKey)var1;
         if (var7.getParams() != null) {
            return new ECPublicKeySpec(var7.getW(), var7.getParams());
         } else {
            ECParameterSpec var10 = BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa();
            return new ECPublicKeySpec(var7.getW(), EC5Util.convertSpec(EC5Util.convertCurve(var10.getCurve(), var10.getSeed()), var10));
         }
      } else if (var2.isAssignableFrom(ECPrivateKeySpec.class) && var1 instanceof ECPrivateKey) {
         ECPrivateKey var6 = (ECPrivateKey)var1;
         if (var6.getParams() != null) {
            return new ECPrivateKeySpec(var6.getS(), var6.getParams());
         } else {
            ECParameterSpec var9 = BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa();
            return new ECPrivateKeySpec(var6.getS(), EC5Util.convertSpec(EC5Util.convertCurve(var9.getCurve(), var9.getSeed()), var9));
         }
      } else if (var2.isAssignableFrom(org.bouncycastle.jce.spec.ECPublicKeySpec.class) && var1 instanceof ECPublicKey) {
         ECPublicKey var5 = (ECPublicKey)var1;
         if (var5.getParams() != null) {
            return new org.bouncycastle.jce.spec.ECPublicKeySpec(EC5Util.convertPoint(var5.getParams(), var5.getW()), EC5Util.convertSpec(var5.getParams()));
         } else {
            ECParameterSpec var8 = BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa();
            return new org.bouncycastle.jce.spec.ECPublicKeySpec(EC5Util.convertPoint(var5.getParams(), var5.getW()), var8);
         }
      } else if (var2.isAssignableFrom(org.bouncycastle.jce.spec.ECPrivateKeySpec.class) && var1 instanceof ECPrivateKey) {
         ECPrivateKey var3 = (ECPrivateKey)var1;
         if (var3.getParams() != null) {
            return new org.bouncycastle.jce.spec.ECPrivateKeySpec(var3.getS(), EC5Util.convertSpec(var3.getParams()));
         } else {
            ECParameterSpec var4 = BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa();
            return new org.bouncycastle.jce.spec.ECPrivateKeySpec(var3.getS(), var4);
         }
      } else {
         return super.engineGetKeySpec(var1, var2);
      }
   }

   @Override
   protected Key engineTranslateKey(Key var1) throws InvalidKeyException {
      throw new InvalidKeyException("key type unknown");
   }

   @Override
   protected PrivateKey engineGeneratePrivate(KeySpec var1) throws InvalidKeySpecException {
      if (var1 instanceof org.bouncycastle.jce.spec.ECPrivateKeySpec) {
         return new BCECGOST3410PrivateKey((org.bouncycastle.jce.spec.ECPrivateKeySpec)var1);
      } else {
         return (PrivateKey)(var1 instanceof ECPrivateKeySpec ? new BCECGOST3410PrivateKey((ECPrivateKeySpec)var1) : super.engineGeneratePrivate(var1));
      }
   }

   @Override
   protected PublicKey engineGeneratePublic(KeySpec var1) throws InvalidKeySpecException {
      if (var1 instanceof org.bouncycastle.jce.spec.ECPublicKeySpec) {
         return new BCECGOST3410PublicKey((org.bouncycastle.jce.spec.ECPublicKeySpec)var1, BouncyCastleProvider.CONFIGURATION);
      } else {
         return (PublicKey)(var1 instanceof ECPublicKeySpec ? new BCECGOST3410PublicKey((ECPublicKeySpec)var1) : super.engineGeneratePublic(var1));
      }
   }

   @Override
   public PrivateKey generatePrivate(PrivateKeyInfo var1) throws IOException {
      ASN1ObjectIdentifier var2 = var1.getPrivateKeyAlgorithm().getAlgorithm();
      if (var2.equals(CryptoProObjectIdentifiers.gostR3410_2001)) {
         return new BCECGOST3410PrivateKey(var1);
      } else if (var2.equals(CryptoProObjectIdentifiers.gostR3410_2001DH)) {
         return new BCECGOST3410PrivateKey(var1);
      } else if (var2.equals(CryptoProObjectIdentifiers.gostR3410_2001_CryptoPro_ESDH)) {
         return new BCECGOST3410PrivateKey(var1);
      } else {
         throw new IOException("algorithm identifier " + var2 + " in key not recognised");
      }
   }

   @Override
   public PublicKey generatePublic(SubjectPublicKeyInfo var1) throws IOException {
      ASN1ObjectIdentifier var2 = var1.getAlgorithm().getAlgorithm();
      if (var2.equals(CryptoProObjectIdentifiers.gostR3410_2001)) {
         return new BCECGOST3410PublicKey(var1);
      } else if (var2.equals(CryptoProObjectIdentifiers.gostR3410_2001DH)) {
         return new BCECGOST3410PublicKey(var1);
      } else if (var2.equals(CryptoProObjectIdentifiers.gostR3410_2001_CryptoPro_ESDH)) {
         return new BCECGOST3410PublicKey(var1);
      } else {
         throw new IOException("algorithm identifier " + var2 + " in key not recognised");
      }
   }
}
