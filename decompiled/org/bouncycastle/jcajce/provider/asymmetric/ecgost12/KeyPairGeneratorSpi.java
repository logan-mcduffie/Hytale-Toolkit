package org.bouncycastle.jcajce.provider.asymmetric.ecgost12;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.ECGenParameterSpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.cryptopro.ECGOST3410NamedCurves;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECGOST3410Parameters;
import org.bouncycastle.crypto.params.ECKeyGenerationParameters;
import org.bouncycastle.crypto.params.ECNamedDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.internal.asn1.rosstandart.RosstandartObjectIdentifiers;
import org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util;
import org.bouncycastle.jcajce.spec.GOST3410ParameterSpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveGenParameterSpec;
import org.bouncycastle.jce.spec.ECNamedCurveSpec;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;

public class KeyPairGeneratorSpi extends KeyPairGenerator {
   Object ecParams = null;
   ECKeyPairGenerator engine = new ECKeyPairGenerator();
   String algorithm = "ECGOST3410-2012";
   ECKeyGenerationParameters param;
   int strength = 239;
   SecureRandom random = null;
   boolean initialised = false;

   public KeyPairGeneratorSpi() {
      super("ECGOST3410-2012");
   }

   @Override
   public void initialize(int var1, SecureRandom var2) {
      this.strength = var1;
      this.random = var2;
      if (this.ecParams != null) {
         try {
            this.initialize((ECGenParameterSpec)this.ecParams, var2);
         } catch (InvalidAlgorithmParameterException var4) {
            throw new InvalidParameterException("key size not configurable.");
         }
      } else {
         throw new InvalidParameterException("unknown key size.");
      }
   }

   @Override
   public void initialize(AlgorithmParameterSpec var1, SecureRandom var2) throws InvalidAlgorithmParameterException {
      if (var1 instanceof GOST3410ParameterSpec) {
         GOST3410ParameterSpec var9 = (GOST3410ParameterSpec)var1;
         this.init(var9, var2);
      } else if (var1 instanceof ECParameterSpec) {
         ECParameterSpec var8 = (ECParameterSpec)var1;
         this.ecParams = var1;
         this.param = new ECKeyGenerationParameters(new ECDomainParameters(var8.getCurve(), var8.getG(), var8.getN(), var8.getH()), var2);
         this.engine.init(this.param);
         this.initialised = true;
      } else if (var1 instanceof java.security.spec.ECParameterSpec) {
         java.security.spec.ECParameterSpec var7 = (java.security.spec.ECParameterSpec)var1;
         this.ecParams = var1;
         ECCurve var10 = EC5Util.convertCurve(var7.getCurve());
         ECPoint var5 = EC5Util.convertPoint(var10, var7.getGenerator());
         this.param = new ECKeyGenerationParameters(new ECDomainParameters(var10, var5, var7.getOrder(), BigInteger.valueOf(var7.getCofactor())), var2);
         this.engine.init(this.param);
         this.initialised = true;
      } else if (!(var1 instanceof ECGenParameterSpec) && !(var1 instanceof ECNamedCurveGenParameterSpec)) {
         if (var1 != null || BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa() == null) {
            if (var1 == null && BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa() == null) {
               throw new InvalidAlgorithmParameterException("null parameter passed but no implicitCA set");
            }

            throw new InvalidAlgorithmParameterException("parameter object not a ECParameterSpec: " + var1.getClass().getName());
         }

         ECParameterSpec var6 = BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa();
         this.ecParams = var1;
         this.param = new ECKeyGenerationParameters(new ECDomainParameters(var6.getCurve(), var6.getG(), var6.getN(), var6.getH()), var2);
         this.engine.init(this.param);
         this.initialised = true;
      } else {
         String var3;
         if (var1 instanceof ECGenParameterSpec) {
            var3 = ((ECGenParameterSpec)var1).getName();
         } else {
            var3 = ((ECNamedCurveGenParameterSpec)var1).getName();
         }

         ASN1ObjectIdentifier var4 = ECGOST3410NamedCurves.getOID(var3);
         if (!var4.equals(RosstandartObjectIdentifiers.id_tc26_gost_3410_12_256_paramSetB)
            && !var4.equals(RosstandartObjectIdentifiers.id_tc26_gost_3410_12_256_paramSetC)
            && !var4.equals(RosstandartObjectIdentifiers.id_tc26_gost_3410_12_256_paramSetD)) {
            this.init(new GOST3410ParameterSpec(var3), var2);
         } else {
            this.init(new GOST3410ParameterSpec(ECGOST3410NamedCurves.getOID(var3), null), var2);
         }
      }
   }

   private void init(GOST3410ParameterSpec var1, SecureRandom var2) throws InvalidAlgorithmParameterException {
      X9ECParameters var3 = ECGOST3410NamedCurves.getByOIDX9(var1.getPublicKeyParamSet());
      if (var3 == null) {
         throw new InvalidAlgorithmParameterException("unknown curve: " + var1.getPublicKeyParamSet());
      } else {
         this.ecParams = new ECNamedCurveSpec(
            ECGOST3410NamedCurves.getName(var1.getPublicKeyParamSet()), var3.getCurve(), var3.getG(), var3.getN(), var3.getH(), var3.getSeed()
         );
         this.param = new ECKeyGenerationParameters(
            new ECGOST3410Parameters(
               new ECNamedDomainParameters(var1.getPublicKeyParamSet(), var3),
               var1.getPublicKeyParamSet(),
               var1.getDigestParamSet(),
               var1.getEncryptionParamSet()
            ),
            var2
         );
         this.engine.init(this.param);
         this.initialised = true;
      }
   }

   @Override
   public KeyPair generateKeyPair() {
      if (!this.initialised) {
         throw new IllegalStateException("EC Key Pair Generator not initialised");
      } else {
         AsymmetricCipherKeyPair var1 = this.engine.generateKeyPair();
         ECPublicKeyParameters var2 = (ECPublicKeyParameters)var1.getPublic();
         ECPrivateKeyParameters var3 = (ECPrivateKeyParameters)var1.getPrivate();
         if (this.ecParams instanceof ECParameterSpec) {
            ECParameterSpec var6 = (ECParameterSpec)this.ecParams;
            BCECGOST3410_2012PublicKey var7 = new BCECGOST3410_2012PublicKey(this.algorithm, var2, var6);
            return new KeyPair(var7, new BCECGOST3410_2012PrivateKey(this.algorithm, var3, var7, var6));
         } else if (this.ecParams == null) {
            return new KeyPair(new BCECGOST3410_2012PublicKey(this.algorithm, var2), new BCECGOST3410_2012PrivateKey(this.algorithm, var3));
         } else {
            java.security.spec.ECParameterSpec var4 = (java.security.spec.ECParameterSpec)this.ecParams;
            BCECGOST3410_2012PublicKey var5 = new BCECGOST3410_2012PublicKey(this.algorithm, var2, var4);
            return new KeyPair(var5, new BCECGOST3410_2012PrivateKey(this.algorithm, var3, var5, var4));
         }
      }
   }
}
