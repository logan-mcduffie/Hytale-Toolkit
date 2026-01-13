package org.bouncycastle.crypto.ec;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.math.ec.ECAlgorithms;
import org.bouncycastle.math.ec.ECMultiplier;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.FixedPointCombMultiplier;

public class ECElGamalEncryptor implements ECEncryptor {
   private ECPublicKeyParameters key;
   private SecureRandom random;

   @Override
   public void init(CipherParameters var1) {
      if (var1 instanceof ParametersWithRandom) {
         ParametersWithRandom var2 = (ParametersWithRandom)var1;
         if (!(var2.getParameters() instanceof ECPublicKeyParameters)) {
            throw new IllegalArgumentException("ECPublicKeyParameters are required for encryption.");
         }

         this.key = (ECPublicKeyParameters)var2.getParameters();
         this.random = var2.getRandom();
      } else {
         if (!(var1 instanceof ECPublicKeyParameters)) {
            throw new IllegalArgumentException("ECPublicKeyParameters are required for encryption.");
         }

         this.key = (ECPublicKeyParameters)var1;
         this.random = CryptoServicesRegistrar.getSecureRandom();
      }
   }

   @Override
   public ECPair encrypt(ECPoint var1) {
      if (this.key == null) {
         throw new IllegalStateException("ECElGamalEncryptor not initialised");
      } else {
         ECDomainParameters var2 = this.key.getParameters();
         BigInteger var3 = ECUtil.generateK(var2.getN(), this.random);
         ECMultiplier var4 = this.createBasePointMultiplier();
         ECPoint[] var5 = new ECPoint[]{var4.multiply(var2.getG(), var3), this.key.getQ().multiply(var3).add(ECAlgorithms.cleanPoint(var2.getCurve(), var1))};
         var2.getCurve().normalizeAll(var5);
         return new ECPair(var5[0], var5[1]);
      }
   }

   protected ECMultiplier createBasePointMultiplier() {
      return new FixedPointCombMultiplier();
   }
}
