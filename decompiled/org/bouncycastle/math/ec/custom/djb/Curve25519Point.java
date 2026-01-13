package org.bouncycastle.math.ec.custom.djb;

import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.raw.Nat256;

public class Curve25519Point extends ECPoint.AbstractFp {
   Curve25519Point(ECCurve var1, ECFieldElement var2, ECFieldElement var3) {
      super(var1, var2, var3);
   }

   Curve25519Point(ECCurve var1, ECFieldElement var2, ECFieldElement var3, ECFieldElement[] var4) {
      super(var1, var2, var3, var4);
   }

   @Override
   protected ECPoint detach() {
      return new Curve25519Point(null, this.getAffineXCoord(), this.getAffineYCoord());
   }

   @Override
   public ECFieldElement getZCoord(int var1) {
      return (ECFieldElement)(var1 == 1 ? this.getJacobianModifiedW() : super.getZCoord(var1));
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
         Curve25519FieldElement var3 = (Curve25519FieldElement)this.x;
         Curve25519FieldElement var4 = (Curve25519FieldElement)this.y;
         Curve25519FieldElement var5 = (Curve25519FieldElement)this.zs[0];
         Curve25519FieldElement var6 = (Curve25519FieldElement)var1.getXCoord();
         Curve25519FieldElement var7 = (Curve25519FieldElement)var1.getYCoord();
         Curve25519FieldElement var8 = (Curve25519FieldElement)var1.getZCoord(0);
         int[] var10 = Nat256.createExt();
         int[] var11 = Nat256.create();
         int[] var12 = Nat256.create();
         int[] var13 = Nat256.create();
         boolean var14 = var5.isOne();
         int[] var15;
         int[] var16;
         if (var14) {
            var15 = var6.x;
            var16 = var7.x;
         } else {
            var16 = var12;
            Curve25519Field.square(var5.x, var12);
            var15 = var11;
            Curve25519Field.multiply(var12, var6.x, var11);
            Curve25519Field.multiply(var12, var5.x, var12);
            Curve25519Field.multiply(var12, var7.x, var12);
         }

         boolean var17 = var8.isOne();
         int[] var18;
         int[] var19;
         if (var17) {
            var18 = var3.x;
            var19 = var4.x;
         } else {
            var19 = var13;
            Curve25519Field.square(var8.x, var13);
            var18 = var10;
            Curve25519Field.multiply(var13, var3.x, var10);
            Curve25519Field.multiply(var13, var8.x, var13);
            Curve25519Field.multiply(var13, var4.x, var13);
         }

         int[] var20 = Nat256.create();
         Curve25519Field.subtract(var18, var15, var20);
         Curve25519Field.subtract(var19, var16, var11);
         if (Nat256.isZero(var20)) {
            return Nat256.isZero(var11) ? this.twice() : var2.getInfinity();
         } else {
            int[] var22 = Nat256.create();
            Curve25519Field.square(var20, var22);
            int[] var23 = Nat256.create();
            Curve25519Field.multiply(var22, var20, var23);
            Curve25519Field.multiply(var22, var18, var12);
            Curve25519Field.negate(var23, var23);
            Nat256.mul(var19, var23, var10);
            int var9 = Nat256.addBothTo(var12, var12, var23);
            Curve25519Field.reduce27(var9, var23);
            Curve25519FieldElement var25 = new Curve25519FieldElement(var13);
            Curve25519Field.square(var11, var25.x);
            Curve25519Field.subtract(var25.x, var23, var25.x);
            Curve25519FieldElement var26 = new Curve25519FieldElement(var23);
            Curve25519Field.subtract(var12, var25.x, var26.x);
            Curve25519Field.multiplyAddToExt(var26.x, var11, var10);
            Curve25519Field.reduce(var10, var26.x);
            Curve25519FieldElement var27 = new Curve25519FieldElement(var20);
            if (!var14) {
               Curve25519Field.multiply(var27.x, var5.x, var27.x);
            }

            if (!var17) {
               Curve25519Field.multiply(var27.x, var8.x, var27.x);
            }

            int[] var28 = var14 && var17 ? var22 : null;
            Curve25519FieldElement var29 = this.calculateJacobianModifiedW(var27, var28);
            ECFieldElement[] var30 = new ECFieldElement[]{var27, var29};
            return new Curve25519Point(var2, var25, var26, var30);
         }
      }
   }

   @Override
   public ECPoint twice() {
      if (this.isInfinity()) {
         return this;
      } else {
         ECCurve var1 = this.getCurve();
         ECFieldElement var2 = this.y;
         return (ECPoint)(var2.isZero() ? var1.getInfinity() : this.twiceJacobianModified(true));
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
         return var2.isZero() ? var1 : this.twiceJacobianModified(false).add(var1);
      }
   }

   @Override
   public ECPoint threeTimes() {
      if (this.isInfinity()) {
         return this;
      } else {
         ECFieldElement var1 = this.y;
         return (ECPoint)(var1.isZero() ? this : this.twiceJacobianModified(false).add(this));
      }
   }

   @Override
   public ECPoint negate() {
      return this.isInfinity() ? this : new Curve25519Point(this.getCurve(), this.x, this.y.negate(), this.zs);
   }

   protected Curve25519FieldElement calculateJacobianModifiedW(Curve25519FieldElement var1, int[] var2) {
      Curve25519FieldElement var3 = (Curve25519FieldElement)this.getCurve().getA();
      if (var1.isOne()) {
         return var3;
      } else {
         Curve25519FieldElement var4 = new Curve25519FieldElement();
         if (var2 == null) {
            var2 = var4.x;
            Curve25519Field.square(var1.x, var2);
         }

         Curve25519Field.square(var2, var4.x);
         Curve25519Field.multiply(var4.x, var3.x, var4.x);
         return var4;
      }
   }

   protected Curve25519FieldElement getJacobianModifiedW() {
      Curve25519FieldElement var1 = (Curve25519FieldElement)this.zs[1];
      if (var1 == null) {
         this.zs[1] = var1 = this.calculateJacobianModifiedW((Curve25519FieldElement)this.zs[0], null);
      }

      return var1;
   }

   protected Curve25519Point twiceJacobianModified(boolean var1) {
      Curve25519FieldElement var2 = (Curve25519FieldElement)this.x;
      Curve25519FieldElement var3 = (Curve25519FieldElement)this.y;
      Curve25519FieldElement var4 = (Curve25519FieldElement)this.zs[0];
      Curve25519FieldElement var5 = this.getJacobianModifiedW();
      int[] var7 = Nat256.create();
      Curve25519Field.square(var2.x, var7);
      int var6 = Nat256.addBothTo(var7, var7, var7);
      var6 += Nat256.addTo(var5.x, var7);
      Curve25519Field.reduce27(var6, var7);
      int[] var8 = Nat256.create();
      Curve25519Field.twice(var3.x, var8);
      int[] var9 = Nat256.create();
      Curve25519Field.multiply(var8, var3.x, var9);
      int[] var10 = Nat256.create();
      Curve25519Field.multiply(var9, var2.x, var10);
      Curve25519Field.twice(var10, var10);
      int[] var11 = Nat256.create();
      Curve25519Field.square(var9, var11);
      Curve25519Field.twice(var11, var11);
      Curve25519FieldElement var12 = new Curve25519FieldElement(var9);
      Curve25519Field.square(var7, var12.x);
      Curve25519Field.subtract(var12.x, var10, var12.x);
      Curve25519Field.subtract(var12.x, var10, var12.x);
      Curve25519FieldElement var13 = new Curve25519FieldElement(var10);
      Curve25519Field.subtract(var10, var12.x, var13.x);
      Curve25519Field.multiply(var13.x, var7, var13.x);
      Curve25519Field.subtract(var13.x, var11, var13.x);
      Curve25519FieldElement var14 = new Curve25519FieldElement(var8);
      if (!Nat256.isOne(var4.x)) {
         Curve25519Field.multiply(var14.x, var4.x, var14.x);
      }

      Curve25519FieldElement var15 = null;
      if (var1) {
         var15 = new Curve25519FieldElement(var11);
         Curve25519Field.multiply(var15.x, var5.x, var15.x);
         Curve25519Field.twice(var15.x, var15.x);
      }

      return new Curve25519Point(this.getCurve(), var12, var13, new ECFieldElement[]{var14, var15});
   }
}
