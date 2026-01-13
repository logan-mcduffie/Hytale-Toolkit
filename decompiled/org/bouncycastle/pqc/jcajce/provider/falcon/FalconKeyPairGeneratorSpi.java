package org.bouncycastle.pqc.jcajce.provider.falcon;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.jcajce.util.SpecUtil;
import org.bouncycastle.pqc.crypto.falcon.FalconKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.falcon.FalconKeyPairGenerator;
import org.bouncycastle.pqc.crypto.falcon.FalconParameters;
import org.bouncycastle.pqc.crypto.falcon.FalconPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.falcon.FalconPublicKeyParameters;
import org.bouncycastle.pqc.jcajce.spec.FalconParameterSpec;
import org.bouncycastle.util.Strings;

public class FalconKeyPairGeneratorSpi extends KeyPairGenerator {
   private static Map parameters = new HashMap();
   private final FalconParameters falconParameters;
   FalconKeyGenerationParameters param;
   FalconKeyPairGenerator engine = new FalconKeyPairGenerator();
   SecureRandom random = CryptoServicesRegistrar.getSecureRandom();
   boolean initialised = false;

   public FalconKeyPairGeneratorSpi() {
      super("FALCON");
      this.falconParameters = null;
   }

   protected FalconKeyPairGeneratorSpi(FalconParameters var1) {
      super(var1.getName());
      this.falconParameters = var1;
   }

   @Override
   public void initialize(int var1, SecureRandom var2) {
      throw new IllegalArgumentException("use AlgorithmParameterSpec");
   }

   @Override
   public void initialize(AlgorithmParameterSpec var1, SecureRandom var2) throws InvalidAlgorithmParameterException {
      String var3 = getNameFromParams(var1);
      if (var3 != null && parameters.containsKey(var3)) {
         FalconParameters var4 = (FalconParameters)parameters.get(var3);
         this.param = new FalconKeyGenerationParameters(var2, var4);
         if (this.falconParameters != null && !var4.getName().equals(this.falconParameters.getName())) {
            throw new InvalidAlgorithmParameterException("key pair generator locked to " + Strings.toUpperCase(this.falconParameters.getName()));
         } else {
            this.engine.init(this.param);
            this.initialised = true;
         }
      } else {
         throw new InvalidAlgorithmParameterException("invalid ParameterSpec: " + var1);
      }
   }

   private static String getNameFromParams(AlgorithmParameterSpec var0) {
      if (var0 instanceof FalconParameterSpec) {
         FalconParameterSpec var1 = (FalconParameterSpec)var0;
         return var1.getName();
      } else {
         return Strings.toLowerCase(SpecUtil.getNameFrom(var0));
      }
   }

   @Override
   public KeyPair generateKeyPair() {
      if (!this.initialised) {
         if (this.falconParameters != null) {
            this.param = new FalconKeyGenerationParameters(this.random, this.falconParameters);
         } else {
            this.param = new FalconKeyGenerationParameters(this.random, FalconParameters.falcon_512);
         }

         this.engine.init(this.param);
         this.initialised = true;
      }

      AsymmetricCipherKeyPair var1 = this.engine.generateKeyPair();
      FalconPublicKeyParameters var2 = (FalconPublicKeyParameters)var1.getPublic();
      FalconPrivateKeyParameters var3 = (FalconPrivateKeyParameters)var1.getPrivate();
      return new KeyPair(new BCFalconPublicKey(var2), new BCFalconPrivateKey(var3));
   }

   static {
      parameters.put(FalconParameterSpec.falcon_512.getName(), FalconParameters.falcon_512);
      parameters.put(FalconParameterSpec.falcon_1024.getName(), FalconParameters.falcon_1024);
   }

   public static class Falcon1024 extends FalconKeyPairGeneratorSpi {
      public Falcon1024() {
         super(FalconParameters.falcon_1024);
      }
   }

   public static class Falcon512 extends FalconKeyPairGeneratorSpi {
      public Falcon512() {
         super(FalconParameters.falcon_512);
      }
   }
}
