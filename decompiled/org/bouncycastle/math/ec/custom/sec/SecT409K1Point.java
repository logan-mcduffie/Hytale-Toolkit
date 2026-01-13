package org.bouncycastle.math.ec.custom.sec;

import org.bouncycastle.math.ec.ECConstants;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECPoint;

public class SecT409K1Point extends ECPoint.AbstractF2m {
   SecT409K1Point(ECCurve var1, ECFieldElement var2, ECFieldElement var3) {
      super(var1, var2, var3);
   }

   SecT409K1Point(ECCurve var1, ECFieldElement var2, ECFieldElement var3, ECFieldElement[] var4) {
      super(var1, var2, var3, var4);
   }

   @Override
   protected ECPoint detach() {
      return new SecT409K1Point(null, this.getAffineXCoord(), this.getAffineYCoord());
   }

   @Override
   public ECFieldElement getYCoord() {
      ECFieldElement var1 = this.x;
      ECFieldElement var2 = this.y;
      if (!this.isInfinity() && !var1.isZero()) {
         ECFieldElement var3 = var2.add(var1).multiply(var1);
         ECFieldElement var4 = this.zs[0];
         if (!var4.isOne()) {
            var3 = var3.divide(var4);
         }

         return var3;
      } else {
         return var2;
      }
   }

   @Override
   protected boolean getCompressionYTilde() {
      ECFieldElement var1 = this.getRawXCoord();
      if (var1.isZero()) {
         return false;
      } else {
         ECFieldElement var2 = this.getRawYCoord();
         return var2.testBitZero() != var1.testBitZero();
      }
   }

   @Override
   public ECPoint add(ECPoint var1) {
      if (this.isInfinity()) {
         return var1;
      } else if (var1.isInfinity()) {
         return this;
      } else {
         ECCurve var2 = this.getCurve();
         ECFieldElement var3 = this.x;
         ECFieldElement var4 = var1.getRawXCoord();
         if (var3.isZero()) {
            return var4.isZero() ? var2.getInfinity() : var1.add(this);
         } else {
            ECFieldElement var5 = this.y;
            ECFieldElement var6 = this.zs[0];
            ECFieldElement var7 = var1.getRawYCoord();
            ECFieldElement var8 = var1.getZCoord(0);
            boolean var9 = var6.isOne();
            ECFieldElement var10 = var4;
            ECFieldElement var11 = var7;
            if (!var9) {
               var10 = var4.multiply(var6);
               var11 = var7.multiply(var6);
            }

            boolean var12 = var8.isOne();
            ECFieldElement var13 = var3;
            ECFieldElement var14 = var5;
            if (!var12) {
               var13 = var3.multiply(var8);
               var14 = var5.multiply(var8);
            }

            ECFieldElement var15 = var14.add(var11);
            ECFieldElement var16 = var13.add(var10);
            if (var16.isZero()) {
               return var15.isZero() ? this.twice() : var2.getInfinity();
            } else {
               ECFieldElement var17;
               ECFieldElement var18;
               ECFieldElement var19;
               if (var4.isZero()) {
                  ECPoint var20 = this.normalize();
                  var3 = var20.getXCoord();
                  ECFieldElement var21 = var20.getYCoord();
                  ECFieldElement var23 = var21.add(var7).divide(var3);
                  var17 = var23.square().add(var23).add(var3);
                  if (var17.isZero()) {
                     return new SecT409K1Point(var2, var17, var2.getB());
                  }

                  ECFieldElement var24 = var23.multiply(var3.add(var17)).add(var17).add(var21);
                  var18 = var24.divide(var17).add(var17);
                  var19 = var2.fromBigInteger(ECConstants.ONE);
               } else {
                  var16 = var16.square();
                  ECFieldElement var27 = var15.multiply(var13);
                  ECFieldElement var28 = var15.multiply(var10);
                  var17 = var27.multiply(var28);
                  if (var17.isZero()) {
                     return new SecT409K1Point(var2, var17, var2.getB());
                  }

                  ECFieldElement var22 = var15.multiply(var16);
                  if (!var12) {
                     var22 = var22.multiply(var8);
                  }

                  var18 = var28.add(var16).squarePlusProduct(var22, var5.add(var6));
                  var19 = var22;
                  if (!var9) {
                     var19 = var22.multiply(var6);
                  }
               }

               return new SecT409K1Point(var2, var17, var18, new ECFieldElement[]{var19});
            }
         }
      }
   }

   @Override
   public ECPoint twice() {
      if (this.isInfinity()) {
         return this;
      } else {
         ECCurve var1 = this.getCurve();
         ECFieldElement var2 = this.x;
         if (var2.isZero()) {
            return var1.getInfinity();
         } else {
            ECFieldElement var3 = this.y;
            ECFieldElement var4 = this.zs[0];
            boolean var5 = var4.isOne();
            ECFieldElement var6 = var5 ? var4 : var4.square();
            ECFieldElement var7;
            if (var5) {
               var7 = var3.square().add(var3);
            } else {
               var7 = var3.add(var4).multiply(var3);
            }

            if (var7.isZero()) {
               return new SecT409K1Point(var1, var7, var1.getB());
            } else {
               ECFieldElement var8 = var7.square();
               ECFieldElement var9 = var5 ? var7 : var7.multiply(var6);
               ECFieldElement var10 = var3.add(var2).square();
               ECFieldElement var11 = var5 ? var4 : var6.square();
               ECFieldElement var12 = var10.add(var7).add(var6).multiply(var10).add(var11).add(var8).add(var9);
               return new SecT409K1Point(var1, var8, var12, new ECFieldElement[]{var9});
            }
         }
      }
   }

   @Override
   public ECPoint twicePlus(ECPoint var1) {
      if (this.isInfinity()) {
         return var1;
      } else if (var1.isInfinity()) {
         return this.twice();
      } else {
         ECCurve var2 = this.getCurve();
         ECFieldElement var3 = this.x;
         if (var3.isZero()) {
            return var1;
         } else {
            ECFieldElement var4 = var1.getRawXCoord();
            ECFieldElement var5 = var1.getZCoord(0);
            if (!var4.isZero() && var5.isOne()) {
               ECFieldElement var6 = this.y;
               ECFieldElement var7 = this.zs[0];
               ECFieldElement var8 = var1.getRawYCoord();
               ECFieldElement var9 = var3.square();
               ECFieldElement var10 = var6.square();
               ECFieldElement var11 = var7.square();
               ECFieldElement var12 = var6.multiply(var7);
               ECFieldElement var13 = var10.add(var12);
               ECFieldElement var14 = var8.addOne();
               ECFieldElement var15 = var14.multiply(var11).add(var10).multiplyPlusProduct(var13, var9, var11);
               ECFieldElement var16 = var4.multiply(var11);
               ECFieldElement var17 = var16.add(var13).square();
               if (var17.isZero()) {
                  return var15.isZero() ? var1.twice() : var2.getInfinity();
               } else if (var15.isZero()) {
                  return new SecT409K1Point(var2, var15, var2.getB());
               } else {
                  ECFieldElement var18 = var15.square().multiply(var16);
                  ECFieldElement var19 = var15.multiply(var17).multiply(var11);
                  ECFieldElement var20 = var15.add(var17).square().multiplyPlusProduct(var13, var14, var19);
                  return new SecT409K1Point(var2, var18, var20, new ECFieldElement[]{var19});
               }
            } else {
               return this.twice().add(var1);
            }
         }
      }
   }

   @Override
   public ECPoint negate() {
      if (this.isInfinity()) {
         return this;
      } else {
         ECFieldElement var1 = this.x;
         if (var1.isZero()) {
            return this;
         } else {
            ECFieldElement var2 = this.y;
            ECFieldElement var3 = this.zs[0];
            return new SecT409K1Point(this.curve, var1, var2.add(var3), new ECFieldElement[]{var3});
         }
      }
   }
}
