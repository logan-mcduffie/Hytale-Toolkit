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
import org.bouncycastle.crypto.params.CramerShoupKeyGenerationParameters;
import org.bouncycastle.crypto.params.CramerShoupParameters;
import org.bouncycastle.crypto.params.CramerShoupPrivateKeyParameters;
import org.bouncycastle.crypto.params.CramerShoupPublicKeyParameters;
import org.bouncycastle.util.BigIntegers;

public class CramerShoupKeyPairGenerator implements AsymmetricCipherKeyPairGenerator {
   private static final BigInteger ONE = BigInteger.valueOf(1L);
   private CramerShoupKeyGenerationParameters param;

   @Override
   public void init(KeyGenerationParameters var1) {
      this.param = (CramerShoupKeyGenerationParameters)var1;
      CryptoServicesRegistrar.checkConstraints(
         new DefaultServiceProperties(
            "CramerShoupKeyGen", ConstraintUtils.bitsOfSecurityFor(this.param.getParameters().getP()), this.param.getParameters(), CryptoServicePurpose.KEYGEN
         )
      );
   }

   @Override
   public AsymmetricCipherKeyPair generateKeyPair() {
      CramerShoupParameters var1 = this.param.getParameters();
      CramerShoupPrivateKeyParameters var2 = this.generatePrivateKey(this.param.getRandom(), var1);
      CramerShoupPublicKeyParameters var3 = this.calculatePublicKey(var1, var2);
      var2.setPk(var3);
      return new AsymmetricCipherKeyPair((AsymmetricKeyParameter)var3, (AsymmetricKeyParameter)var2);
   }

   private BigInteger generateRandomElement(BigInteger var1, SecureRandom var2) {
      return BigIntegers.createRandomInRange(ONE, var1.subtract(ONE), var2);
   }

   private CramerShoupPrivateKeyParameters generatePrivateKey(SecureRandom var1, CramerShoupParameters var2) {
      BigInteger var3 = var2.getP();
      return new CramerShoupPrivateKeyParameters(
         var2,
         this.generateRandomElement(var3, var1),
         this.generateRandomElement(var3, var1),
         this.generateRandomElement(var3, var1),
         this.generateRandomElement(var3, var1),
         this.generateRandomElement(var3, var1)
      );
   }

   private CramerShoupPublicKeyParameters calculatePublicKey(CramerShoupParameters var1, CramerShoupPrivateKeyParameters var2) {
      BigInteger var3 = var1.getG1();
      BigInteger var4 = var1.getG2();
      BigInteger var5 = var1.getP();
      BigInteger var6 = var3.modPow(var2.getX1(), var5).multiply(var4.modPow(var2.getX2(), var5));
      BigInteger var7 = var3.modPow(var2.getY1(), var5).multiply(var4.modPow(var2.getY2(), var5));
      BigInteger var8 = var3.modPow(var2.getZ(), var5);
      return new CramerShoupPublicKeyParameters(var1, var6, var7, var8);
   }
}
