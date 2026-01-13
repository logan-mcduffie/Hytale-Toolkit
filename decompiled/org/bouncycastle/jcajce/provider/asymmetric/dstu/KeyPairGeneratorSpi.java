package org.bouncycastle.jcajce.provider.asymmetric.dstu;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.ECGenParameterSpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ua.DSTU4145NamedCurves;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.generators.DSTU4145KeyPairGenerator;
import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import org.bouncycastle.crypto.params.DSTU4145Parameters;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECKeyGenerationParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util;
import org.bouncycastle.jcajce.spec.DSTU4145ParameterSpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveGenParameterSpec;
import org.bouncycastle.jce.spec.ECNamedCurveSpec;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;

public class KeyPairGeneratorSpi extends KeyPairGenerator {
   Object ecParams = null;
   ECKeyPairGenerator engine = new DSTU4145KeyPairGenerator();
   String algorithm = "DSTU4145";
   ECKeyGenerationParameters param;
   SecureRandom random = null;
   boolean initialised = false;

   public KeyPairGeneratorSpi() {
      super("DSTU4145");
   }

   @Override
   public void initialize(int var1, SecureRandom var2) {
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
      if (var1 instanceof ECParameterSpec) {
         ECParameterSpec var10 = (ECParameterSpec)var1;
         this.ecParams = var1;
         this.param = new ECKeyGenerationParameters(new ECDomainParameters(var10.getCurve(), var10.getG(), var10.getN(), var10.getH()), var2);
         this.engine.init(this.param);
         this.initialised = true;
      } else if (var1 instanceof java.security.spec.ECParameterSpec) {
         java.security.spec.ECParameterSpec var9 = (java.security.spec.ECParameterSpec)var1;
         this.ecParams = var1;
         ECCurve var11 = EC5Util.convertCurve(var9.getCurve());
         ECPoint var12 = EC5Util.convertPoint(var11, var9.getGenerator());
         if (var9 instanceof DSTU4145ParameterSpec) {
            DSTU4145ParameterSpec var13 = (DSTU4145ParameterSpec)var9;
            this.param = new ECKeyGenerationParameters(
               new DSTU4145Parameters(new ECDomainParameters(var11, var12, var9.getOrder(), BigInteger.valueOf(var9.getCofactor())), var13.getDKE()), var2
            );
         } else {
            this.param = new ECKeyGenerationParameters(new ECDomainParameters(var11, var12, var9.getOrder(), BigInteger.valueOf(var9.getCofactor())), var2);
         }

         this.engine.init(this.param);
         this.initialised = true;
      } else if (!(var1 instanceof ECGenParameterSpec) && !(var1 instanceof ECNamedCurveGenParameterSpec)) {
         if (var1 != null || BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa() == null) {
            if (var1 == null && BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa() == null) {
               throw new InvalidAlgorithmParameterException("null parameter passed but no implicitCA set");
            }

            throw new InvalidAlgorithmParameterException("parameter object not a ECParameterSpec: " + var1.getClass().getName());
         }

         ECParameterSpec var8 = BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa();
         this.ecParams = var1;
         this.param = new ECKeyGenerationParameters(new ECDomainParameters(var8.getCurve(), var8.getG(), var8.getN(), var8.getH()), var2);
         this.engine.init(this.param);
         this.initialised = true;
      } else {
         String var3;
         if (var1 instanceof ECGenParameterSpec) {
            var3 = ((ECGenParameterSpec)var1).getName();
         } else {
            var3 = ((ECNamedCurveGenParameterSpec)var1).getName();
         }

         ECDomainParameters var4 = DSTU4145NamedCurves.getByOID(new ASN1ObjectIdentifier(var3));
         if (var4 == null) {
            throw new InvalidAlgorithmParameterException("unknown curve name: " + var3);
         }

         this.ecParams = new ECNamedCurveSpec(var3, var4.getCurve(), var4.getG(), var4.getN(), var4.getH(), var4.getSeed());
         java.security.spec.ECParameterSpec var5 = (java.security.spec.ECParameterSpec)this.ecParams;
         ECCurve var6 = EC5Util.convertCurve(var5.getCurve());
         ECPoint var7 = EC5Util.convertPoint(var6, var5.getGenerator());
         this.param = new ECKeyGenerationParameters(new ECDomainParameters(var6, var7, var5.getOrder(), BigInteger.valueOf(var5.getCofactor())), var2);
         this.engine.init(this.param);
         this.initialised = true;
      }
   }

   @Override
   public KeyPair generateKeyPair() {
      if (!this.initialised) {
         throw new IllegalStateException("DSTU Key Pair Generator not initialised");
      } else {
         AsymmetricCipherKeyPair var1 = this.engine.generateKeyPair();
         ECPublicKeyParameters var2 = (ECPublicKeyParameters)var1.getPublic();
         ECPrivateKeyParameters var3 = (ECPrivateKeyParameters)var1.getPrivate();
         if (this.ecParams instanceof ECParameterSpec) {
            ECParameterSpec var6 = (ECParameterSpec)this.ecParams;
            BCDSTU4145PublicKey var7 = new BCDSTU4145PublicKey(this.algorithm, var2, var6);
            return new KeyPair(var7, new BCDSTU4145PrivateKey(this.algorithm, var3, var7, var6));
         } else if (this.ecParams == null) {
            return new KeyPair(new BCDSTU4145PublicKey(this.algorithm, var2), new BCDSTU4145PrivateKey(this.algorithm, var3));
         } else {
            java.security.spec.ECParameterSpec var4 = (java.security.spec.ECParameterSpec)this.ecParams;
            BCDSTU4145PublicKey var5 = new BCDSTU4145PublicKey(this.algorithm, var2, var4);
            return new KeyPair(var5, new BCDSTU4145PrivateKey(this.algorithm, var3, var5, var4));
         }
      }
   }
}
