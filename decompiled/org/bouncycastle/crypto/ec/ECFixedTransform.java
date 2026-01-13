package org.bouncycastle.crypto.ec;

import java.math.BigInteger;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.math.ec.ECAlgorithms;
import org.bouncycastle.math.ec.ECMultiplier;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.FixedPointCombMultiplier;

public class ECFixedTransform implements ECPairFactorTransform {
   private ECPublicKeyParameters key;
   private BigInteger k;

   public ECFixedTransform(BigInteger var1) {
      this.k = var1;
   }

   @Override
   public void init(CipherParameters var1) {
      if (!(var1 instanceof ECPublicKeyParameters)) {
         throw new IllegalArgumentException("ECPublicKeyParameters are required for fixed transform.");
      } else {
         this.key = (ECPublicKeyParameters)var1;
      }
   }

   @Override
   public ECPair transform(ECPair var1) {
      if (this.key == null) {
         throw new IllegalStateException("ECFixedTransform not initialised");
      } else {
         ECDomainParameters var2 = this.key.getParameters();
         BigInteger var3 = var2.getN();
         ECMultiplier var4 = this.createBasePointMultiplier();
         BigInteger var5 = this.k.mod(var3);
         ECPoint[] var6 = new ECPoint[]{
            var4.multiply(var2.getG(), var5).add(ECAlgorithms.cleanPoint(var2.getCurve(), var1.getX())),
            this.key.getQ().multiply(var5).add(ECAlgorithms.cleanPoint(var2.getCurve(), var1.getY()))
         };
         var2.getCurve().normalizeAll(var6);
         return new ECPair(var6[0], var6[1]);
      }
   }

   @Override
   public BigInteger getTransformValue() {
      return this.k;
   }

   protected ECMultiplier createBasePointMultiplier() {
      return new FixedPointCombMultiplier();
   }
}
