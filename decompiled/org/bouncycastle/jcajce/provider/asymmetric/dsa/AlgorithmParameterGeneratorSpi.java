package org.bouncycastle.jcajce.provider.asymmetric.dsa;

import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidParameterException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.DSAParameterSpec;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.generators.DSAParametersGenerator;
import org.bouncycastle.crypto.params.DSAParameterGenerationParameters;
import org.bouncycastle.crypto.params.DSAParameters;
import org.bouncycastle.jcajce.provider.asymmetric.util.BaseAlgorithmParameterGeneratorSpi;
import org.bouncycastle.jcajce.provider.asymmetric.util.PrimeCertaintyCalculator;

public class AlgorithmParameterGeneratorSpi extends BaseAlgorithmParameterGeneratorSpi {
   protected SecureRandom random;
   protected int strength = 2048;
   protected DSAParameterGenerationParameters params;

   @Override
   protected void engineInit(int var1, SecureRandom var2) {
      if (var1 < 512 || var1 > 3072) {
         throw new InvalidParameterException("strength must be from 512 - 3072");
      } else if (var1 <= 1024 && var1 % 64 != 0) {
         throw new InvalidParameterException("strength must be a multiple of 64 below 1024 bits.");
      } else if (var1 > 1024 && var1 % 1024 != 0) {
         throw new InvalidParameterException("strength must be a multiple of 1024 above 1024 bits.");
      } else {
         this.strength = var1;
         this.random = var2;
      }
   }

   @Override
   protected void engineInit(AlgorithmParameterSpec var1, SecureRandom var2) throws InvalidAlgorithmParameterException {
      throw new InvalidAlgorithmParameterException("No supported AlgorithmParameterSpec for DSA parameter generation.");
   }

   @Override
   protected AlgorithmParameters engineGenerateParameters() {
      DSAParametersGenerator var1;
      if (this.strength <= 1024) {
         var1 = new DSAParametersGenerator();
      } else {
         var1 = new DSAParametersGenerator(SHA256Digest.newInstance());
      }

      if (this.random == null) {
         this.random = CryptoServicesRegistrar.getSecureRandom();
      }

      int var2 = PrimeCertaintyCalculator.getDefaultCertainty(this.strength);
      if (this.strength == 1024) {
         this.params = new DSAParameterGenerationParameters(1024, 160, var2, this.random);
         var1.init(this.params);
      } else if (this.strength > 1024) {
         this.params = new DSAParameterGenerationParameters(this.strength, 256, var2, this.random);
         var1.init(this.params);
      } else {
         var1.init(this.strength, var2, this.random);
      }

      DSAParameters var3 = var1.generateParameters();

      try {
         AlgorithmParameters var4 = this.createParametersInstance("DSA");
         var4.init(new DSAParameterSpec(var3.getP(), var3.getQ(), var3.getG()));
         return var4;
      } catch (Exception var6) {
         throw new RuntimeException(var6.getMessage());
      }
   }
}
