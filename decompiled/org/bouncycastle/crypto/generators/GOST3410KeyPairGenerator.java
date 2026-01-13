package org.bouncycastle.crypto.generators;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.constraints.ConstraintUtils;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.GOST3410KeyGenerationParameters;
import org.bouncycastle.crypto.params.GOST3410Parameters;
import org.bouncycastle.crypto.params.GOST3410PrivateKeyParameters;
import org.bouncycastle.crypto.params.GOST3410PublicKeyParameters;
import org.bouncycastle.math.ec.WNafUtil;
import org.bouncycastle.util.BigIntegers;

public class GOST3410KeyPairGenerator implements AsymmetricCipherKeyPairGenerator {
   private GOST3410KeyGenerationParameters param;

   @Override
   public void init(KeyGenerationParameters var1) {
      this.param = (GOST3410KeyGenerationParameters)var1;
      CryptoServicesRegistrar.checkConstraints(
         new DefaultServiceProperties(
            "GOST3410KeyGen", ConstraintUtils.bitsOfSecurityFor(this.param.getParameters().getP()), this.param.getParameters(), CryptoServicePurpose.KEYGEN
         )
      );
   }

   @Override
   public AsymmetricCipherKeyPair generateKeyPair() {
      GOST3410Parameters var6 = this.param.getParameters();
      SecureRandom var7 = this.param.getRandom();
      BigInteger var2 = var6.getQ();
      BigInteger var1 = var6.getP();
      BigInteger var3 = var6.getA();
      byte var8 = 64;

      BigInteger var4;
      do {
         var4 = BigIntegers.createRandomBigInteger(256, var7);
      } while (var4.signum() < 1 || var4.compareTo(var2) >= 0 || WNafUtil.getNafWeight(var4) < var8);

      BigInteger var5 = var3.modPow(var4, var1);
      return new AsymmetricCipherKeyPair(
         (AsymmetricKeyParameter)(new GOST3410PublicKeyParameters(var5, var6)), (AsymmetricKeyParameter)(new GOST3410PrivateKeyParameters(var4, var6))
      );
   }
}
