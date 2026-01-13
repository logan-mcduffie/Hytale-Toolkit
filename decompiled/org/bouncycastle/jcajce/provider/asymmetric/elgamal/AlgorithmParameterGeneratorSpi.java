package org.bouncycastle.jcajce.provider.asymmetric.elgamal;

import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.spec.DHGenParameterSpec;
import javax.crypto.spec.DHParameterSpec;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.generators.ElGamalParametersGenerator;
import org.bouncycastle.crypto.params.ElGamalParameters;
import org.bouncycastle.jcajce.provider.asymmetric.util.BaseAlgorithmParameterGeneratorSpi;

public class AlgorithmParameterGeneratorSpi extends BaseAlgorithmParameterGeneratorSpi {
   protected SecureRandom random;
   protected int strength = 1024;
   private int l = 0;

   @Override
   protected void engineInit(int var1, SecureRandom var2) {
      this.strength = var1;
      this.random = var2;
   }

   @Override
   protected void engineInit(AlgorithmParameterSpec var1, SecureRandom var2) throws InvalidAlgorithmParameterException {
      if (!(var1 instanceof DHGenParameterSpec)) {
         throw new InvalidAlgorithmParameterException("DH parameter generator requires a DHGenParameterSpec for initialisation");
      } else {
         DHGenParameterSpec var3 = (DHGenParameterSpec)var1;
         this.strength = var3.getPrimeSize();
         this.l = var3.getExponentSize();
         this.random = var2;
      }
   }

   @Override
   protected AlgorithmParameters engineGenerateParameters() {
      ElGamalParametersGenerator var1 = new ElGamalParametersGenerator();
      if (this.random != null) {
         var1.init(this.strength, 20, this.random);
      } else {
         var1.init(this.strength, 20, CryptoServicesRegistrar.getSecureRandom());
      }

      ElGamalParameters var2 = var1.generateParameters();

      try {
         AlgorithmParameters var3 = this.createParametersInstance("ElGamal");
         var3.init(new DHParameterSpec(var2.getP(), var2.getG(), this.l));
         return var3;
      } catch (Exception var5) {
         throw new RuntimeException(var5.getMessage());
      }
   }
}
