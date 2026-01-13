package org.bouncycastle.math.ec.custom.sec;

import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.math.raw.Nat384;

public class SecP384R1Point extends ECPoint.AbstractFp {
   SecP384R1Point(ECCurve var1, ECFieldElement var2, ECFieldElement var3) {
      super(var1, var2, var3);
   }

   SecP384R1Point(ECCurve var1, ECFieldElement var2, ECFieldElement var3, ECFieldElement[] var4) {
      super(var1, var2, var3, var4);
   }

   @Override
   protected ECPoint detach() {
      return new SecP384R1Point(null, this.getAffineXCoord(), this.getAffineYCoord());
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
         SecP384R1FieldElement var3 = (SecP384R1FieldElement)this.x;
         SecP384R1FieldElement var4 = (SecP384R1FieldElement)this.y;
         SecP384R1FieldElement var5 = (SecP384R1FieldElement)var1.getXCoord();
         SecP384R1FieldElement var6 = (SecP384R1FieldElement)var1.getYCoord();
         SecP384R1FieldElement var7 = (SecP384R1FieldElement)this.zs[0];
         SecP384R1FieldElement var8 = (SecP384R1FieldElement)var1.getZCoord(0);
         int[] var10 = Nat.create(24);
         int[] var11 = Nat.create(24);
         int[] var12 = Nat.create(24);
         int[] var13 = Nat.create(12);
         int[] var14 = Nat.create(12);
         boolean var15 = var7.isOne();
         int[] var16;
         int[] var17;
         if (var15) {
            var16 = var5.x;
            var17 = var6.x;
         } else {
            var17 = var13;
            SecP384R1Field.square(var7.x, var13, var10);
            var16 = var12;
            SecP384R1Field.multiply(var13, var5.x, var12, var10);
            SecP384R1Field.multiply(var13, var7.x, var13, var10);
            SecP384R1Field.multiply(var13, var6.x, var13, var10);
         }

         boolean var18 = var8.isOne();
         int[] var19;
         int[] var20;
         if (var18) {
            var19 = var3.x;
            var20 = var4.x;
         } else {
            var20 = var14;
            SecP384R1Field.square(var8.x, var14, var10);
            var19 = var11;
            SecP384R1Field.multiply(var14, var3.x, var11, var10);
            SecP384R1Field.multiply(var14, var8.x, var14, var10);
            SecP384R1Field.multiply(var14, var4.x, var14, var10);
         }

         int[] var21 = Nat.create(12);
         SecP384R1Field.subtract(var19, var16, var21);
         int[] var22 = Nat.create(12);
         SecP384R1Field.subtract(var20, var17, var22);
         if (Nat.isZero(12, var21)) {
            return Nat.isZero(12, var22) ? this.twice() : var2.getInfinity();
         } else {
            SecP384R1Field.square(var21, var13, var10);
            int[] var24 = Nat.create(12);
            SecP384R1Field.multiply(var13, var21, var24, var10);
            SecP384R1Field.multiply(var13, var19, var13, var10);
            SecP384R1Field.negate(var24, var24);
            Nat384.mul(var20, var24, var11);
            int var9 = Nat.addBothTo(12, var13, var13, var24);
            SecP384R1Field.reduce32(var9, var24);
            SecP384R1FieldElement var26 = new SecP384R1FieldElement(var14);
            SecP384R1Field.square(var22, var26.x, var10);
            SecP384R1Field.subtract(var26.x, var24, var26.x);
            SecP384R1FieldElement var27 = new SecP384R1FieldElement(var24);
            SecP384R1Field.subtract(var13, var26.x, var27.x);
            Nat384.mul(var27.x, var22, var12);
            SecP384R1Field.addExt(var11, var12, var11);
            SecP384R1Field.reduce(var11, var27.x);
            SecP384R1FieldElement var28 = new SecP384R1FieldElement(var21);
            if (!var15) {
               SecP384R1Field.multiply(var28.x, var7.x, var28.x, var10);
            }

            if (!var18) {
               SecP384R1Field.multiply(var28.x, var8.x, var28.x, var10);
            }

            ECFieldElement[] var29 = new ECFieldElement[]{var28};
            return new SecP384R1Point(var2, var26, var27, var29);
         }
      }
   }

   @Override
   public ECPoint twice() {
      if (this.isInfinity()) {
         return this;
      } else {
         ECCurve var1 = this.getCurve();
         SecP384R1FieldElement var2 = (SecP384R1FieldElement)this.y;
         if (var2.isZero()) {
            return var1.getInfinity();
         } else {
            SecP384R1FieldElement var3 = (SecP384R1FieldElement)this.x;
            SecP384R1FieldElement var4 = (SecP384R1FieldElement)this.zs[0];
            int[] var6 = Nat.create(24);
            int[] var7 = Nat.create(12);
            int[] var8 = Nat.create(12);
            int[] var9 = Nat.create(12);
            SecP384R1Field.square(var2.x, var9, var6);
            int[] var10 = Nat.create(12);
            SecP384R1Field.square(var9, var10, var6);
            boolean var11 = var4.isOne();
            int[] var12 = var4.x;
            if (!var11) {
               var12 = var8;
               SecP384R1Field.square(var4.x, var8, var6);
            }

            SecP384R1Field.subtract(var3.x, var12, var7);
            SecP384R1Field.add(var3.x, var12, var8);
            SecP384R1Field.multiply(var8, var7, var8, var6);
            int var5 = Nat.addBothTo(12, var8, var8, var8);
            SecP384R1Field.reduce32(var5, var8);
            SecP384R1Field.multiply(var9, var3.x, var9, var6);
            var5 = Nat.shiftUpBits(12, var9, 2, 0);
            SecP384R1Field.reduce32(var5, var9);
            var5 = Nat.shiftUpBits(12, var10, 3, 0, var7);
            SecP384R1Field.reduce32(var5, var7);
            SecP384R1FieldElement var15 = new SecP384R1FieldElement(var10);
            SecP384R1Field.square(var8, var15.x, var6);
            SecP384R1Field.subtract(var15.x, var9, var15.x);
            SecP384R1Field.subtract(var15.x, var9, var15.x);
            SecP384R1FieldElement var16 = new SecP384R1FieldElement(var9);
            SecP384R1Field.subtract(var9, var15.x, var16.x);
            SecP384R1Field.multiply(var16.x, var8, var16.x, var6);
            SecP384R1Field.subtract(var16.x, var7, var16.x);
            SecP384R1FieldElement var17 = new SecP384R1FieldElement(var8);
            SecP384R1Field.twice(var2.x, var17.x);
            if (!var11) {
               SecP384R1Field.multiply(var17.x, var4.x, var17.x, var6);
            }

            return new SecP384R1Point(var1, var15, var16, new ECFieldElement[]{var17});
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
      return this.isInfinity() ? this : new SecP384R1Point(this.curve, this.x, this.y.negate(), this.zs);
   }
}
