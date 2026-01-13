package org.bouncycastle.pqc.jcajce.provider.lms;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.pqc.crypto.lms.HSSKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.lms.HSSKeyPairGenerator;
import org.bouncycastle.pqc.crypto.lms.HSSPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.lms.HSSPublicKeyParameters;
import org.bouncycastle.pqc.crypto.lms.LMOtsParameters;
import org.bouncycastle.pqc.crypto.lms.LMSKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.lms.LMSKeyPairGenerator;
import org.bouncycastle.pqc.crypto.lms.LMSParameters;
import org.bouncycastle.pqc.crypto.lms.LMSPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.lms.LMSPublicKeyParameters;
import org.bouncycastle.pqc.crypto.lms.LMSigParameters;
import org.bouncycastle.pqc.jcajce.spec.LMSHSSKeyGenParameterSpec;
import org.bouncycastle.pqc.jcajce.spec.LMSHSSParameterSpec;
import org.bouncycastle.pqc.jcajce.spec.LMSKeyGenParameterSpec;
import org.bouncycastle.pqc.jcajce.spec.LMSParameterSpec;

public class LMSKeyPairGeneratorSpi extends KeyPairGenerator {
   private KeyGenerationParameters param;
   private ASN1ObjectIdentifier treeDigest;
   private AsymmetricCipherKeyPairGenerator engine = new LMSKeyPairGenerator();
   private SecureRandom random = CryptoServicesRegistrar.getSecureRandom();
   private boolean initialised = false;

   public LMSKeyPairGeneratorSpi() {
      super("LMS");
   }

   @Override
   public void initialize(int var1, SecureRandom var2) {
      throw new IllegalArgumentException("use AlgorithmParameterSpec");
   }

   @Override
   public void initialize(AlgorithmParameterSpec var1, SecureRandom var2) throws InvalidAlgorithmParameterException {
      if (var1 instanceof LMSKeyGenParameterSpec) {
         LMSKeyGenParameterSpec var3 = (LMSKeyGenParameterSpec)var1;
         this.param = new LMSKeyGenerationParameters(new LMSParameters(var3.getSigParams(), var3.getOtsParams()), var2);
         this.engine = new LMSKeyPairGenerator();
         this.engine.init(this.param);
      } else if (var1 instanceof LMSHSSKeyGenParameterSpec) {
         LMSKeyGenParameterSpec[] var6 = ((LMSHSSKeyGenParameterSpec)var1).getLMSSpecs();
         LMSParameters[] var4 = new LMSParameters[var6.length];

         for (int var5 = 0; var5 != var6.length; var5++) {
            var4[var5] = new LMSParameters(var6[var5].getSigParams(), var6[var5].getOtsParams());
         }

         this.param = new HSSKeyGenerationParameters(var4, var2);
         this.engine = new HSSKeyPairGenerator();
         this.engine.init(this.param);
      } else if (var1 instanceof LMSParameterSpec) {
         LMSParameterSpec var7 = (LMSParameterSpec)var1;
         this.param = new LMSKeyGenerationParameters(new LMSParameters(var7.getSigParams(), var7.getOtsParams()), var2);
         this.engine = new LMSKeyPairGenerator();
         this.engine.init(this.param);
      } else {
         if (!(var1 instanceof LMSHSSParameterSpec)) {
            throw new InvalidAlgorithmParameterException("parameter object not a LMSParameterSpec/LMSHSSParameterSpec");
         }

         LMSParameterSpec[] var8 = ((LMSHSSParameterSpec)var1).getLMSSpecs();
         LMSParameters[] var9 = new LMSParameters[var8.length];

         for (int var10 = 0; var10 != var8.length; var10++) {
            var9[var10] = new LMSParameters(var8[var10].getSigParams(), var8[var10].getOtsParams());
         }

         this.param = new HSSKeyGenerationParameters(var9, var2);
         this.engine = new HSSKeyPairGenerator();
         this.engine.init(this.param);
      }

      this.initialised = true;
   }

   @Override
   public KeyPair generateKeyPair() {
      if (!this.initialised) {
         this.param = new LMSKeyGenerationParameters(new LMSParameters(LMSigParameters.lms_sha256_n32_h10, LMOtsParameters.sha256_n32_w2), this.random);
         this.engine.init(this.param);
         this.initialised = true;
      }

      AsymmetricCipherKeyPair var1 = this.engine.generateKeyPair();
      if (this.engine instanceof LMSKeyPairGenerator) {
         LMSPublicKeyParameters var4 = (LMSPublicKeyParameters)var1.getPublic();
         LMSPrivateKeyParameters var5 = (LMSPrivateKeyParameters)var1.getPrivate();
         return new KeyPair(new BCLMSPublicKey(var4), new BCLMSPrivateKey(var5));
      } else {
         HSSPublicKeyParameters var2 = (HSSPublicKeyParameters)var1.getPublic();
         HSSPrivateKeyParameters var3 = (HSSPrivateKeyParameters)var1.getPrivate();
         return new KeyPair(new BCLMSPublicKey(var2), new BCLMSPrivateKey(var3));
      }
   }
}
