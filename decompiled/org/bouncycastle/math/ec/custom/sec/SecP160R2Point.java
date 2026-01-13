package org.bouncycastle.math.ec.custom.sec;

import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.math.raw.Nat160;

public class SecP160R2Point extends ECPoint.AbstractFp {
   SecP160R2Point(ECCurve var1, ECFieldElement var2, ECFieldElement var3) {
      super(var1, var2, var3);
   }

   SecP160R2Point(ECCurve var1, ECFieldElement var2, ECFieldElement var3, ECFieldElement[] var4) {
      super(var1, var2, var3, var4);
   }

   @Override
   protected ECPoint detach() {
      return new SecP160R2Point(null, this.getAffineXCoord(), this.getAffineYCoord());
   }

   @Override
   public ECPoint add(ECPoint var1) {
      if (this.isInfinity()) {
         return var1;
      } else if (var1.isInfinity()) {
         return this;
      } else if (this == var1) {
         return this.twice();
      } else {
         ECCurve var2 = this.getCurve();
         SecP160R2FieldElement var3 = (SecP160R2FieldElement)this.x;
         SecP160R2FieldElement var4 = (SecP160R2FieldElement)this.y;
         SecP160R2FieldElement var5 = (SecP160R2FieldElement)var1.getXCoord();
         SecP160R2FieldElement var6 = (SecP160R2FieldElement)var1.getYCoord();
         SecP160R2FieldElement var7 = (SecP160R2FieldElement)this.zs[0];
         SecP160R2FieldElement var8 = (SecP160R2FieldElement)var1.getZCoord(0);
         int[] var10 = Nat160.createExt();
         int[] var11 = Nat160.create();
         int[] var12 = Nat160.create();
         int[] var13 = Nat160.create();
         boolean var14 = var7.isOne();
         int[] var15;
         int[] var16;
         if (var14) {
            var15 = var5.x;
            var16 = var6.x;
         } else {
            var16 = var12;
            SecP160R2Field.square(var7.x, var12);
            var15 = var11;
            SecP160R2Field.multiply(var12, var5.x, var11);
            SecP160R2Field.multiply(var12, var7.x, var12);
            SecP160R2Field.multiply(var12, var6.x, var12);
         }

         boolean var17 = var8.isOne();
         int[] var18;
         int[] var19;
         if (var17) {
            var18 = var3.x;
            var19 = var4.x;
         } else {
            var19 = var13;
            SecP160R2Field.square(var8.x, var13);
            var18 = var10;
            SecP160R2Field.multiply(var13, var3.x, var10);
            SecP160R2Field.multiply(var13, var8.x, var13);
            SecP160R2Field.multiply(var13, var4.x, var13);
         }

         int[] var20 = Nat160.create();
         SecP160R2Field.subtract(var18, var15, var20);
         SecP160R2Field.subtract(var19, var16, var11);
         if (Nat160.isZero(var20)) {
            return Nat160.isZero(var11) ? this.twice() : var2.getInfinity();
         } else {
            SecP160R2Field.square(var20, var12);
            int[] var23 = Nat160.create();
            SecP160R2Field.multiply(var12, var20, var23);
            SecP160R2Field.multiply(var12, var18, var12);
            SecP160R2Field.negate(var23, var23);
            Nat160.mul(var19, var23, var10);
            int var9 = Nat160.addBothTo(var12, var12, var23);
            SecP160R2Field.reduce32(var9, var23);
            SecP160R2FieldElement var25 = new SecP160R2FieldElement(var13);
            SecP160R2Field.square(var11, var25.x);
            SecP160R2Field.subtract(var25.x, var23, var25.x);
            SecP160R2FieldElement var26 = new SecP160R2FieldElement(var23);
            SecP160R2Field.subtract(var12, var25.x, var26.x);
            SecP160R2Field.multiplyAddToExt(var26.x, var11, var10);
            SecP160R2Field.reduce(var10, var26.x);
            SecP160R2FieldElement var27 = new SecP160R2FieldElement(var20);
            if (!var14) {
               SecP160R2Field.multiply(var27.x, var7.x, var27.x);
            }

            if (!var17) {
               SecP160R2Field.multiply(var27.x, var8.x, var27.x);
            }

            ECFieldElement[] var28 = new ECFieldElement[]{var27};
            return new SecP160R2Point(var2, var25, var26, var28);
         }
      }
   }

   @Override
   public ECPoint twice() {
      if (this.isInfinity()) {
         return this;
      } else {
         ECCurve var1 = this.getCurve();
         SecP160R2FieldElement var2 = (SecP160R2FieldElement)this.y;
         if (var2.isZero()) {
            return var1.getInfinity();
         } else {
            SecP160R2FieldElement var3 = (SecP160R2FieldElement)this.x;
            SecP160R2FieldElement var4 = (SecP160R2FieldElement)this.zs[0];
            int[] var6 = Nat160.create();
            int[] var7 = Nat160.create();
            int[] var8 = Nat160.create();
            SecP160R2Field.square(var2.x, var8);
            int[] var9 = Nat160.create();
            SecP160R2Field.square(var8, var9);
            boolean var10 = var4.isOne();
            int[] var11 = var4.x;
            if (!var10) {
               var11 = var7;
               SecP160R2Field.square(var4.x, var7);
            }

            SecP160R2Field.subtract(var3.x, var11, var6);
            SecP160R2Field.add(var3.x, var11, var7);
            SecP160R2Field.multiply(var7, var6, var7);
            int var5 = Nat160.addBothTo(var7, var7, var7);
            SecP160R2Field.reduce32(var5, var7);
            SecP160R2Field.multiply(var8, var3.x, var8);
            var5 = Nat.shiftUpBits(5, var8, 2, 0);
            SecP160R2Field.reduce32(var5, var8);
            var5 = Nat.shiftUpBits(5, var9, 3, 0, var6);
            SecP160R2Field.reduce32(var5, var6);
            SecP160R2FieldElement var14 = new SecP160R2FieldElement(var9);
            SecP160R2Field.square(var7, var14.x);
            SecP160R2Field.subtract(var14.x, var8, var14.x);
            SecP160R2Field.subtract(var14.x, var8, var14.x);
            SecP160R2FieldElement var15 = new SecP160R2FieldElement(var8);
            SecP160R2Field.subtract(var8, var14.x, var15.x);
            SecP160R2Field.multiply(var15.x, var7, var15.x);
            SecP160R2Field.subtract(var15.x, var6, var15.x);
            SecP160R2FieldElement var16 = new SecP160R2FieldElement(var7);
            SecP160R2Field.twice(var2.x, var16.x);
            if (!var10) {
               SecP160R2Field.multiply(var16.x, var4.x, var16.x);
            }

            return new SecP160R2Point(var1, var14, var15, new ECFieldElement[]{var16});
         }
      }
   }

   @Override
   public ECPoint twicePlus(ECPoint var1) {
      if (this == var1) {
         return this.threeTimes();
      } else if (this.isInfinity()) {
         return var1;
      } else if (var1.isInfinity()) {
         return this.twice();
      } else {
         ECFieldElement var2 = this.y;
         return var2.isZero() ? var1 : this.twice().add(var1);
      }
   }

   @Override
   public ECPoint threeTimes() {
      return (ECPoint)(!this.isInfinity() && !this.y.isZero() ? this.twice().add(this) : this);
   }

   @Override
   public ECPoint negate() {
      return this.isInfinity() ? this : new SecP160R2Point(this.curve, this.x, this.y.negate(), this.zs);
   }
}
