package org.bouncycastle.math.ec.custom.sec;

import org.bouncycastle.math.ec.ECConstants;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.math.raw.Nat576;

public class SecT571R1Point extends ECPoint.AbstractF2m {
   SecT571R1Point(ECCurve var1, ECFieldElement var2, ECFieldElement var3) {
      super(var1, var2, var3);
   }

   SecT571R1Point(ECCurve var1, ECFieldElement var2, ECFieldElement var3, ECFieldElement[] var4) {
      super(var1, var2, var3, var4);
   }

   @Override
   protected ECPoint detach() {
      return new SecT571R1Point(null, this.getAffineXCoord(), this.getAffineYCoord());
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
         SecT571FieldElement var3 = (SecT571FieldElement)this.x;
         SecT571FieldElement var4 = (SecT571FieldElement)var1.getRawXCoord();
         if (var3.isZero()) {
            return var4.isZero() ? var2.getInfinity() : var1.add(this);
         } else {
            SecT571FieldElement var5 = (SecT571FieldElement)this.y;
            SecT571FieldElement var6 = (SecT571FieldElement)this.zs[0];
            SecT571FieldElement var7 = (SecT571FieldElement)var1.getRawYCoord();
            SecT571FieldElement var8 = (SecT571FieldElement)var1.getZCoord(0);
            long[] var9 = Nat576.create64();
            long[] var10 = Nat576.create64();
            long[] var11 = Nat576.create64();
            long[] var12 = Nat576.create64();
            long[] var13 = var6.isOne() ? null : SecT571Field.precompMultiplicand(var6.x);
            long[] var14;
            long[] var15;
            if (var13 == null) {
               var14 = var4.x;
               var15 = var7.x;
            } else {
               var14 = var10;
               SecT571Field.multiplyPrecomp(var4.x, var13, var10);
               var15 = var12;
               SecT571Field.multiplyPrecomp(var7.x, var13, var12);
            }

            long[] var16 = var8.isOne() ? null : SecT571Field.precompMultiplicand(var8.x);
            long[] var17;
            long[] var18;
            if (var16 == null) {
               var17 = var3.x;
               var18 = var5.x;
            } else {
               var17 = var9;
               SecT571Field.multiplyPrecomp(var3.x, var16, var9);
               var18 = var11;
               SecT571Field.multiplyPrecomp(var5.x, var16, var11);
            }

            SecT571Field.add(var18, var15, var11);
            SecT571Field.add(var17, var14, var12);
            if (Nat576.isZero64(var12)) {
               return Nat576.isZero64(var11) ? this.twice() : var2.getInfinity();
            } else {
               SecT571FieldElement var21;
               SecT571FieldElement var22;
               SecT571FieldElement var23;
               if (var4.isZero()) {
                  ECPoint var24 = this.normalize();
                  var3 = (SecT571FieldElement)var24.getXCoord();
                  ECFieldElement var25 = var24.getYCoord();
                  ECFieldElement var27 = var25.add(var7).divide(var3);
                  var21 = (SecT571FieldElement)var27.square().add(var27).add(var3).addOne();
                  if (var21.isZero()) {
                     return new SecT571R1Point(var2, var21, SecT571R1Curve.SecT571R1_B_SQRT);
                  }

                  ECFieldElement var28 = var27.multiply(var3.add(var21)).add(var21).add(var25);
                  var22 = (SecT571FieldElement)var28.divide(var21).add(var21);
                  var23 = (SecT571FieldElement)var2.fromBigInteger(ECConstants.ONE);
               } else {
                  SecT571Field.square(var12, var12);
                  long[] var30 = SecT571Field.precompMultiplicand(var11);
                  SecT571Field.multiplyPrecomp(var17, var30, var9);
                  SecT571Field.multiplyPrecomp(var14, var30, var10);
                  var21 = new SecT571FieldElement(var9);
                  SecT571Field.multiply(var9, var10, var21.x);
                  if (var21.isZero()) {
                     return new SecT571R1Point(var2, var21, SecT571R1Curve.SecT571R1_B_SQRT);
                  }

                  var23 = new SecT571FieldElement(var11);
                  SecT571Field.multiplyPrecomp(var12, var30, var23.x);
                  if (var16 != null) {
                     SecT571Field.multiplyPrecomp(var23.x, var16, var23.x);
                  }

                  long[] var31 = Nat576.createExt64();
                  SecT571Field.add(var10, var12, var12);
                  SecT571Field.squareAddToExt(var12, var31);
                  SecT571Field.add(var5.x, var6.x, var12);
                  SecT571Field.multiplyAddToExt(var12, var23.x, var31);
                  var22 = new SecT571FieldElement(var12);
                  SecT571Field.reduce(var31, var22.x);
                  if (var13 != null) {
                     SecT571Field.multiplyPrecomp(var23.x, var13, var23.x);
                  }
               }

               return new SecT571R1Point(var2, var21, var22, new ECFieldElement[]{var23});
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
         SecT571FieldElement var2 = (SecT571FieldElement)this.x;
         if (var2.isZero()) {
            return var1.getInfinity();
         } else {
            SecT571FieldElement var3 = (SecT571FieldElement)this.y;
            SecT571FieldElement var4 = (SecT571FieldElement)this.zs[0];
            long[] var5 = Nat576.create64();
            long[] var6 = Nat576.create64();
            long[] var7 = var4.isOne() ? null : SecT571Field.precompMultiplicand(var4.x);
            long[] var8;
            long[] var9;
            if (var7 == null) {
               var8 = var3.x;
               var9 = var4.x;
            } else {
               var8 = var5;
               SecT571Field.multiplyPrecomp(var3.x, var7, var5);
               var9 = var6;
               SecT571Field.square(var4.x, var6);
            }

            long[] var10 = Nat576.create64();
            SecT571Field.square(var3.x, var10);
            SecT571Field.addBothTo(var8, var9, var10);
            if (Nat576.isZero64(var10)) {
               return new SecT571R1Point(var1, new SecT571FieldElement(var10), SecT571R1Curve.SecT571R1_B_SQRT);
            } else {
               long[] var11 = Nat576.createExt64();
               SecT571Field.multiplyAddToExt(var10, var8, var11);
               SecT571FieldElement var12 = new SecT571FieldElement(var5);
               SecT571Field.square(var10, var12.x);
               SecT571FieldElement var13 = new SecT571FieldElement(var10);
               if (var7 != null) {
                  SecT571Field.multiply(var13.x, var9, var13.x);
               }

               long[] var14;
               if (var7 == null) {
                  var14 = var2.x;
               } else {
                  var14 = var6;
                  SecT571Field.multiplyPrecomp(var2.x, var7, var6);
               }

               SecT571Field.squareAddToExt(var14, var11);
               SecT571Field.reduce(var11, var6);
               SecT571Field.addBothTo(var12.x, var13.x, var6);
               SecT571FieldElement var15 = new SecT571FieldElement(var6);
               return new SecT571R1Point(var1, var12, var15, new ECFieldElement[]{var13});
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
         SecT571FieldElement var3 = (SecT571FieldElement)this.x;
         if (var3.isZero()) {
            return var1;
         } else {
            SecT571FieldElement var4 = (SecT571FieldElement)var1.getRawXCoord();
            SecT571FieldElement var5 = (SecT571FieldElement)var1.getZCoord(0);
            if (!var4.isZero() && var5.isOne()) {
               SecT571FieldElement var6 = (SecT571FieldElement)this.y;
               SecT571FieldElement var7 = (SecT571FieldElement)this.zs[0];
               SecT571FieldElement var8 = (SecT571FieldElement)var1.getRawYCoord();
               long[] var9 = Nat576.create64();
               long[] var10 = Nat576.create64();
               long[] var11 = Nat576.create64();
               long[] var12 = Nat576.create64();
               SecT571Field.square(var3.x, var9);
               SecT571Field.square(var6.x, var10);
               SecT571Field.square(var7.x, var11);
               SecT571Field.multiply(var6.x, var7.x, var12);
               SecT571Field.addBothTo(var11, var10, var12);
               long[] var18 = SecT571Field.precompMultiplicand(var11);
               SecT571Field.multiplyPrecomp(var8.x, var18, var11);
               SecT571Field.add(var11, var10, var11);
               long[] var20 = Nat576.createExt64();
               SecT571Field.multiplyAddToExt(var11, var12, var20);
               SecT571Field.multiplyPrecompAddToExt(var9, var18, var20);
               SecT571Field.reduce(var20, var11);
               SecT571Field.multiplyPrecomp(var4.x, var18, var9);
               SecT571Field.add(var9, var12, var10);
               SecT571Field.square(var10, var10);
               if (Nat576.isZero64(var10)) {
                  return Nat576.isZero64(var11) ? var1.twice() : var2.getInfinity();
               } else if (Nat576.isZero64(var11)) {
                  return new SecT571R1Point(var2, new SecT571FieldElement(var11), SecT571R1Curve.SecT571R1_B_SQRT);
               } else {
                  SecT571FieldElement var23 = new SecT571FieldElement();
                  SecT571Field.square(var11, var23.x);
                  SecT571Field.multiply(var23.x, var9, var23.x);
                  SecT571FieldElement var24 = new SecT571FieldElement(var9);
                  SecT571Field.multiply(var11, var10, var24.x);
                  SecT571Field.multiplyPrecomp(var24.x, var18, var24.x);
                  SecT571FieldElement var25 = new SecT571FieldElement(var10);
                  SecT571Field.add(var11, var10, var25.x);
                  SecT571Field.square(var25.x, var25.x);
                  Nat.zero64(18, var20);
                  SecT571Field.multiplyAddToExt(var25.x, var12, var20);
                  SecT571Field.addOne(var8.x, var12);
                  SecT571Field.multiplyAddToExt(var12, var24.x, var20);
                  SecT571Field.reduce(var20, var25.x);
                  return new SecT571R1Point(var2, var23, var25, new ECFieldElement[]{var24});
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
            return new SecT571R1Point(this.curve, var1, var2.add(var3), new ECFieldElement[]{var3});
         }
      }
   }
}
