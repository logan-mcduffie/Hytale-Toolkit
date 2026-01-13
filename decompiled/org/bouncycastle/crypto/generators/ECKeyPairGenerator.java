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
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECKeyGenerationParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.math.ec.ECConstants;
import org.bouncycastle.math.ec.ECMultiplier;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.FixedPointCombMultiplier;
import org.bouncycastle.math.ec.WNafUtil;
import org.bouncycastle.util.BigIntegers;

public class ECKeyPairGenerator implements AsymmetricCipherKeyPairGenerator, ECConstants {
   private final String name;
   ECDomainParameters params;
   SecureRandom random;

   public ECKeyPairGenerator() {
      this("ECKeyGen");
   }

   protected ECKeyPairGenerator(String var1) {
      this.name = var1;
   }

   @Override
   public void init(KeyGenerationParameters var1) {
      ECKeyGenerationParameters var2 = (ECKeyGenerationParameters)var1;
      this.random = var2.getRandom();
      this.params = var2.getDomainParameters();
      CryptoServicesRegistrar.checkConstraints(
         new DefaultServiceProperties(
            this.name, ConstraintUtils.bitsOfSecurityFor(this.params.getCurve()), var2.getDomainParameters(), CryptoServicePurpose.KEYGEN
         )
      );
   }

   @Override
   public AsymmetricCipherKeyPair generateKeyPair() {
      BigInteger var1 = this.params.getN();
      int var2 = var1.bitLength();
      int var3 = var2 >>> 2;

      BigInteger var4;
      do {
         var4 = BigIntegers.createRandomBigInteger(var2, this.random);
      } while (this.isOutOfRangeD(var4, var1) || WNafUtil.getNafWeight(var4) < var3);

      ECPoint var5 = this.createBasePointMultiplier().multiply(this.params.getG(), var4);
      return new AsymmetricCipherKeyPair(
         (AsymmetricKeyParameter)(new ECPublicKeyParameters(var5, this.params)), (AsymmetricKeyParameter)(new ECPrivateKeyParameters(var4, this.params))
      );
   }

   protected boolean isOutOfRangeD(BigInteger var1, BigInteger var2) {
      return var1.compareTo(ONE) < 0 || var1.compareTo(var2) >= 0;
   }

   protected ECMultiplier createBasePointMultiplier() {
      return new FixedPointCombMultiplier();
   }
}
