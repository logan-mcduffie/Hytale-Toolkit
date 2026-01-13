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

public class ECNewPublicKeyTransform implements ECPairTransform {
   private ECPublicKeyParameters key;
   private SecureRandom random;

   @Override
   public void init(CipherParameters var1) {
      if (var1 instanceof ParametersWithRandom) {
         ParametersWithRandom var2 = (ParametersWithRandom)var1;
         if (!(var2.getParameters() instanceof ECPublicKeyParameters)) {
            throw new IllegalArgumentException("ECPublicKeyParameters are required for new public key transform.");
         }

         this.key = (ECPublicKeyParameters)var2.getParameters();
         this.random = var2.getRandom();
      } else {
         if (!(var1 instanceof ECPublicKeyParameters)) {
            throw new IllegalArgumentException("ECPublicKeyParameters are required for new public key transform.");
         }

         this.key = (ECPublicKeyParameters)var1;
         this.random = CryptoServicesRegistrar.getSecureRandom();
      }
   }

   @Override
   public ECPair transform(ECPair var1) {
      if (this.key == null) {
         throw new IllegalStateException("ECNewPublicKeyTransform not initialised");
      } else {
         ECDomainParameters var2 = this.key.getParameters();
         BigInteger var3 = var2.getN();
         ECMultiplier var4 = this.createBasePointMultiplier();
         BigInteger var5 = ECUtil.generateK(var3, this.random);
         ECPoint[] var6 = new ECPoint[]{
            var4.multiply(var2.getG(), var5), this.key.getQ().multiply(var5).add(ECAlgorithms.cleanPoint(var2.getCurve(), var1.getY()))
         };
         var2.getCurve().normalizeAll(var6);
         return new ECPair(var6[0], var6[1]);
      }
   }

   protected ECMultiplier createBasePointMultiplier() {
      return new FixedPointCombMultiplier();
   }
}
