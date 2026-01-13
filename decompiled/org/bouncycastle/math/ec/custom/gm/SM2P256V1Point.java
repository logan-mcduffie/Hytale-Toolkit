package org.bouncycastle.math.ec.custom.gm;

import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.math.raw.Nat256;

public class SM2P256V1Point extends ECPoint.AbstractFp {
   SM2P256V1Point(ECCurve var1, ECFieldElement var2, ECFieldElement var3) {
      super(var1, var2, var3);
   }

   SM2P256V1Point(ECCurve var1, ECFieldElement var2, ECFieldElement var3, ECFieldElement[] var4) {
      super(var1, var2, var3, var4);
   }

   @Override
   protected ECPoint detach() {
      return new SM2P256V1Point(null, this.getAffineXCoord(), this.getAffineYCoord());
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
         SM2P256V1FieldElement var3 = (SM2P256V1FieldElement)this.x;
         SM2P256V1FieldElement var4 = (SM2P256V1FieldElement)this.y;
         SM2P256V1FieldElement var5 = (SM2P256V1FieldElement)var1.getXCoord();
         SM2P256V1FieldElement var6 = (SM2P256V1FieldElement)var1.getYCoord();
         SM2P256V1FieldElement var7 = (SM2P256V1FieldElement)this.zs[0];
         SM2P256V1FieldElement var8 = (SM2P256V1FieldElement)var1.getZCoord(0);
         int[] var10 = Nat256.createExt();
         int[] var11 = Nat256.create();
         int[] var12 = Nat256.create();
         int[] var13 = Nat256.create();
         boolean var14 = var7.isOne();
         int[] var15;
         int[] var16;
         if (var14) {
            var15 = var5.x;
            var16 = var6.x;
         } else {
            var16 = var12;
            SM2P256V1Field.square(var7.x, var12);
            var15 = var11;
            SM2P256V1Field.multiply(var12, var5.x, var11);
            SM2P256V1Field.multiply(var12, var7.x, var12);
            SM2P256V1Field.multiply(var12, var6.x, var12);
         }

         boolean var17 = var8.isOne();
         int[] var18;
         int[] var19;
         if (var17) {
            var18 = var3.x;
            var19 = var4.x;
         } else {
            var19 = var13;
            SM2P256V1Field.square(var8.x, var13);
            var18 = var10;
            SM2P256V1Field.multiply(var13, var3.x, var10);
            SM2P256V1Field.multiply(var13, var8.x, var13);
            SM2P256V1Field.multiply(var13, var4.x, var13);
         }

         int[] var20 = Nat256.create();
         SM2P256V1Field.subtract(var18, var15, var20);
         SM2P256V1Field.subtract(var19, var16, var11);
         if (Nat256.isZero(var20)) {
            return Nat256.isZero(var11) ? this.twice() : var2.getInfinity();
         } else {
            SM2P256V1Field.square(var20, var12);
            int[] var23 = Nat256.create();
            SM2P256V1Field.multiply(var12, var20, var23);
            SM2P256V1Field.multiply(var12, var18, var12);
            SM2P256V1Field.negate(var23, var23);
            Nat256.mul(var19, var23, var10);
            int var9 = Nat256.addBothTo(var12, var12, var23);
            SM2P256V1Field.reduce32(var9, var23);
            SM2P256V1FieldElement var25 = new SM2P256V1FieldElement(var13);
            SM2P256V1Field.square(var11, var25.x);
            SM2P256V1Field.subtract(var25.x, var23, var25.x);
            SM2P256V1FieldElement var26 = new SM2P256V1FieldElement(var23);
            SM2P256V1Field.subtract(var12, var25.x, var26.x);
            SM2P256V1Field.multiplyAddToExt(var26.x, var11, var10);
            SM2P256V1Field.reduce(var10, var26.x);
            SM2P256V1FieldElement var27 = new SM2P256V1FieldElement(var20);
            if (!var14) {
               SM2P256V1Field.multiply(var27.x, var7.x, var27.x);
            }

            if (!var17) {
               SM2P256V1Field.multiply(var27.x, var8.x, var27.x);
            }

            ECFieldElement[] var28 = new ECFieldElement[]{var27};
            return new SM2P256V1Point(var2, var25, var26, var28);
         }
      }
   }

   @Override
   public ECPoint twice() {
      if (this.isInfinity()) {
         return this;
      } else {
         ECCurve var1 = this.getCurve();
         SM2P256V1FieldElement var2 = (SM2P256V1FieldElement)this.y;
         if (var2.isZero()) {
            return var1.getInfinity();
         } else {
            SM2P256V1FieldElement var3 = (SM2P256V1FieldElement)this.x;
            SM2P256V1FieldElement var4 = (SM2P256V1FieldElement)this.zs[0];
            int[] var6 = Nat256.create();
            int[] var7 = Nat256.create();
            int[] var8 = Nat256.create();
            SM2P256V1Field.square(var2.x, var8);
            int[] var9 = Nat256.create();
            SM2P256V1Field.square(var8, var9);
            boolean var10 = var4.isOne();
            int[] var11 = var4.x;
            if (!var10) {
               var11 = var7;
               SM2P256V1Field.square(var4.x, var7);
            }

            SM2P256V1Field.subtract(var3.x, var11, var6);
            SM2P256V1Field.add(var3.x, var11, var7);
            SM2P256V1Field.multiply(var7, var6, var7);
            int var5 = Nat256.addBothTo(var7, var7, var7);
            SM2P256V1Field.reduce32(var5, var7);
            SM2P256V1Field.multiply(var8, var3.x, var8);
            var5 = Nat.shiftUpBits(8, var8, 2, 0);
            SM2P256V1Field.reduce32(var5, var8);
            var5 = Nat.shiftUpBits(8, var9, 3, 0, var6);
            SM2P256V1Field.reduce32(var5, var6);
            SM2P256V1FieldElement var14 = new SM2P256V1FieldElement(var9);
            SM2P256V1Field.square(var7, var14.x);
            SM2P256V1Field.subtract(var14.x, var8, var14.x);
            SM2P256V1Field.subtract(var14.x, var8, var14.x);
            SM2P256V1FieldElement var15 = new SM2P256V1FieldElement(var8);
            SM2P256V1Field.subtract(var8, var14.x, var15.x);
            SM2P256V1Field.multiply(var15.x, var7, var15.x);
            SM2P256V1Field.subtract(var15.x, var6, var15.x);
            SM2P256V1FieldElement var16 = new SM2P256V1FieldElement(var7);
            SM2P256V1Field.twice(var2.x, var16.x);
            if (!var10) {
               SM2P256V1Field.multiply(var16.x, var4.x, var16.x);
            }

            return new SM2P256V1Point(var1, var14, var15, new ECFieldElement[]{var16});
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
      return this.isInfinity() ? this : new SM2P256V1Point(this.curve, this.x, this.y.negate(), this.zs);
   }
}
