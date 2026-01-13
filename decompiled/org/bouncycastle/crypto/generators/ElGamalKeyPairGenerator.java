package org.bouncycastle.crypto.generators;

import java.math.BigInteger;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.constraints.ConstraintUtils;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.DHParameters;
import org.bouncycastle.crypto.params.ElGamalKeyGenerationParameters;
import org.bouncycastle.crypto.params.ElGamalParameters;
import org.bouncycastle.crypto.params.ElGamalPrivateKeyParameters;
import org.bouncycastle.crypto.params.ElGamalPublicKeyParameters;

public class ElGamalKeyPairGenerator implements AsymmetricCipherKeyPairGenerator {
   private ElGamalKeyGenerationParameters param;

   @Override
   public void init(KeyGenerationParameters var1) {
      this.param = (ElGamalKeyGenerationParameters)var1;
      CryptoServicesRegistrar.checkConstraints(
         new DefaultServiceProperties(
            "ElGamalKeyGen", ConstraintUtils.bitsOfSecurityFor(this.param.getParameters().getP()), this.param.getParameters(), CryptoServicePurpose.KEYGEN
         )
      );
   }

   @Override
   public AsymmetricCipherKeyPair generateKeyPair() {
      DHKeyGeneratorHelper var1 = DHKeyGeneratorHelper.INSTANCE;
      ElGamalParameters var2 = this.param.getParameters();
      DHParameters var3 = new DHParameters(var2.getP(), var2.getG(), null, var2.getL());
      BigInteger var4 = var1.calculatePrivate(var3, this.param.getRandom());
      BigInteger var5 = var1.calculatePublic(var3, var4);
      return new AsymmetricCipherKeyPair(
         (AsymmetricKeyParameter)(new ElGamalPublicKeyParameters(var5, var2)), (AsymmetricKeyParameter)(new ElGamalPrivateKeyParameters(var4, var2))
      );
   }
}
