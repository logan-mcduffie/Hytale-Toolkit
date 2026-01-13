package org.bouncycastle.math.ec;

public class WNafPreCompInfo implements PreCompInfo {
   volatile int promotionCountdown = 4;
   protected int confWidth = -1;
   protected ECPoint[] preComp = null;
   protected ECPoint[] preCompNeg = null;
   protected ECPoint twice = null;
   protected int width = -1;

   int decrementPromotionCountdown() {
      int var1 = this.promotionCountdown;
      if (var1 > 0) {
         this.promotionCountdown = --var1;
      }

      return var1;
   }

   int getPromotionCountdown() {
      return this.promotionCountdown;
   }

   void setPromotionCountdown(int var1) {
      this.promotionCountdown = var1;
   }

   public boolean isPromoted() {
      return this.promotionCountdown <= 0;
   }

   public int getConfWidth() {
      return this.confWidth;
   }

   public void setConfWidth(int var1) {
      this.confWidth = var1;
   }

   public ECPoint[] getPreComp() {
      return this.preComp;
   }

   public void setPreComp(ECPoint[] var1) {
      this.preComp = var1;
   }

   public ECPoint[] getPreCompNeg() {
      return this.preCompNeg;
   }

   public void setPreCompNeg(ECPoint[] var1) {
      this.preCompNeg = var1;
   }

   public ECPoint getTwice() {
      return this.twice;
   }

   public void setTwice(ECPoint var1) {
      this.twice = var1;
   }

   public int getWidth() {
      return this.width;
   }

   public void setWidth(int var1) {
      this.width = var1;
   }
}
