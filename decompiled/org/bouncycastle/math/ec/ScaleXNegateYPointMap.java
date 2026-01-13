package org.bouncycastle.math.ec;

public class ScaleXNegateYPointMap implements ECPointMap {
   protected final ECFieldElement scale;

   public ScaleXNegateYPointMap(ECFieldElement var1) {
      this.scale = var1;
   }

   @Override
   public ECPoint map(ECPoint var1) {
      return var1.scaleXNegateY(this.scale);
   }
}
