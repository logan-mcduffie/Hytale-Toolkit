package org.bouncycastle.math.ec.custom.sec;

import java.math.BigInteger;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.raw.Nat192;
import org.bouncycastle.util.Arrays;

public class SecT163FieldElement extends ECFieldElement.AbstractF2m {
   protected long[] x;

   public SecT163FieldElement(BigInteger var1) {
      if (var1 != null && var1.signum() >= 0 && var1.bitLength() <= 163) {
         this.x = SecT163Field.fromBigInteger(var1);
      } else {
         throw new IllegalArgumentException("x value invalid for SecT163FieldElement");
      }
   }

   public SecT163FieldElement() {
      this.x = Nat192.create64();
   }

   protected SecT163FieldElement(long[] var1) {
      this.x = var1;
   }

   @Override
   public boolean isOne() {
      return Nat192.isOne64(this.x);
   }

   @Override
   public boolean isZero() {
      return Nat192.isZero64(this.x);
   }

   @Override
   public boolean testBitZero() {
      return (this.x[0] & 1L) != 0L;
   }

   @Override
   public BigInteger toBigInteger() {
      return Nat192.toBigInteger64(this.x);
   }

   @Override
   public String getFieldName() {
      return "SecT163Field";
   }

   @Override
   public int getFieldSize() {
      return 163;
   }

   @Override
   public ECFieldElement add(ECFieldElement var1) {
      long[] var2 = Nat192.create64();
      SecT163Field.add(this.x, ((SecT163FieldElement)var1).x, var2);
      return new SecT163FieldElement(var2);
   }

   @Override
   public ECFieldElement addOne() {
      long[] var1 = Nat192.create64();
      SecT163Field.addOne(this.x, var1);
      return new SecT163FieldElement(var1);
   }

   @Override
   public ECFieldElement subtract(ECFieldElement var1) {
      return this.add(var1);
   }

   @Override
   public ECFieldElement multiply(ECFieldElement var1) {
      long[] var2 = Nat192.create64();
      SecT163Field.multiply(this.x, ((SecT163FieldElement)var1).x, var2);
      return new SecT163FieldElement(var2);
   }

   @Override
   public ECFieldElement multiplyMinusProduct(ECFieldElement var1, ECFieldElement var2, ECFieldElement var3) {
      return this.multiplyPlusProduct(var1, var2, var3);
   }

   @Override
   public ECFieldElement multiplyPlusProduct(ECFieldElement var1, ECFieldElement var2, ECFieldElement var3) {
      long[] var4 = this.x;
      long[] var5 = ((SecT163FieldElement)var1).x;
      long[] var6 = ((SecT163FieldElement)var2).x;
      long[] var7 = ((SecT163FieldElement)var3).x;
      long[] var8 = Nat192.createExt64();
      SecT163Field.multiplyAddToExt(var4, var5, var8);
      SecT163Field.multiplyAddToExt(var6, var7, var8);
      long[] var9 = Nat192.create64();
      SecT163Field.reduce(var8, var9);
      return new SecT163FieldElement(var9);
   }

   @Override
   public ECFieldElement divide(ECFieldElement var1) {
      return this.multiply(var1.invert());
   }

   @Override
   public ECFieldElement negate() {
      return this;
   }

   @Override
   public ECFieldElement square() {
      long[] var1 = Nat192.create64();
      SecT163Field.square(this.x, var1);
      return new SecT163FieldElement(var1);
   }

   @Override
   public ECFieldElement squareMinusProduct(ECFieldElement var1, ECFieldElement var2) {
      return this.squarePlusProduct(var1, var2);
   }

   @Override
   public ECFieldElement squarePlusProduct(ECFieldElement var1, ECFieldElement var2) {
      long[] var3 = this.x;
      long[] var4 = ((SecT163FieldElement)var1).x;
      long[] var5 = ((SecT163FieldElement)var2).x;
      long[] var6 = Nat192.createExt64();
      SecT163Field.squareAddToExt(var3, var6);
      SecT163Field.multiplyAddToExt(var4, var5, var6);
      long[] var7 = Nat192.create64();
      SecT163Field.reduce(var6, var7);
      return new SecT163FieldElement(var7);
   }

   @Override
   public ECFieldElement squarePow(int var1) {
      if (var1 < 1) {
         return this;
      } else {
         long[] var2 = Nat192.create64();
         SecT163Field.squareN(this.x, var1, var2);
         return new SecT163FieldElement(var2);
      }
   }

   @Override
   public ECFieldElement halfTrace() {
      long[] var1 = Nat192.create64();
      SecT163Field.halfTrace(this.x, var1);
      return new SecT163FieldElement(var1);
   }

   @Override
   public boolean hasFastTrace() {
      return true;
   }

   @Override
   public int trace() {
      return SecT163Field.trace(this.x);
   }

   @Override
   public ECFieldElement invert() {
      long[] var1 = Nat192.create64();
      SecT163Field.invert(this.x, var1);
      return new SecT163FieldElement(var1);
   }

   @Override
   public ECFieldElement sqrt() {
      long[] var1 = Nat192.create64();
      SecT163Field.sqrt(this.x, var1);
      return new SecT163FieldElement(var1);
   }

   public int getRepresentation() {
      return 3;
   }

   public int getM() {
      return 163;
   }

   public int getK1() {
      return 3;
   }

   public int getK2() {
      return 6;
   }

   public int getK3() {
      return 7;
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof SecT163FieldElement)) {
         return false;
      } else {
         SecT163FieldElement var2 = (SecT163FieldElement)var1;
         return Nat192.eq64(this.x, var2.x);
      }
   }

   @Override
   public int hashCode() {
      return 163763 ^ Arrays.hashCode(this.x, 0, 3);
   }
}
