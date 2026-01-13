package org.bouncycastle.math.ec.custom.sec;

import java.math.BigInteger;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.raw.Nat256;
import org.bouncycastle.util.Arrays;

public class SecT239FieldElement extends ECFieldElement.AbstractF2m {
   protected long[] x;

   public SecT239FieldElement(BigInteger var1) {
      if (var1 != null && var1.signum() >= 0 && var1.bitLength() <= 239) {
         this.x = SecT239Field.fromBigInteger(var1);
      } else {
         throw new IllegalArgumentException("x value invalid for SecT239FieldElement");
      }
   }

   public SecT239FieldElement() {
      this.x = Nat256.create64();
   }

   protected SecT239FieldElement(long[] var1) {
      this.x = var1;
   }

   @Override
   public boolean isOne() {
      return Nat256.isOne64(this.x);
   }

   @Override
   public boolean isZero() {
      return Nat256.isZero64(this.x);
   }

   @Override
   public boolean testBitZero() {
      return (this.x[0] & 1L) != 0L;
   }

   @Override
   public BigInteger toBigInteger() {
      return Nat256.toBigInteger64(this.x);
   }

   @Override
   public String getFieldName() {
      return "SecT239Field";
   }

   @Override
   public int getFieldSize() {
      return 239;
   }

   @Override
   public ECFieldElement add(ECFieldElement var1) {
      long[] var2 = Nat256.create64();
      SecT239Field.add(this.x, ((SecT239FieldElement)var1).x, var2);
      return new SecT239FieldElement(var2);
   }

   @Override
   public ECFieldElement addOne() {
      long[] var1 = Nat256.create64();
      SecT239Field.addOne(this.x, var1);
      return new SecT239FieldElement(var1);
   }

   @Override
   public ECFieldElement subtract(ECFieldElement var1) {
      return this.add(var1);
   }

   @Override
   public ECFieldElement multiply(ECFieldElement var1) {
      long[] var2 = Nat256.create64();
      SecT239Field.multiply(this.x, ((SecT239FieldElement)var1).x, var2);
      return new SecT239FieldElement(var2);
   }

   @Override
   public ECFieldElement multiplyMinusProduct(ECFieldElement var1, ECFieldElement var2, ECFieldElement var3) {
      return this.multiplyPlusProduct(var1, var2, var3);
   }

   @Override
   public ECFieldElement multiplyPlusProduct(ECFieldElement var1, ECFieldElement var2, ECFieldElement var3) {
      long[] var4 = this.x;
      long[] var5 = ((SecT239FieldElement)var1).x;
      long[] var6 = ((SecT239FieldElement)var2).x;
      long[] var7 = ((SecT239FieldElement)var3).x;
      long[] var8 = Nat256.createExt64();
      SecT239Field.multiplyAddToExt(var4, var5, var8);
      SecT239Field.multiplyAddToExt(var6, var7, var8);
      long[] var9 = Nat256.create64();
      SecT239Field.reduce(var8, var9);
      return new SecT239FieldElement(var9);
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
      long[] var1 = Nat256.create64();
      SecT239Field.square(this.x, var1);
      return new SecT239FieldElement(var1);
   }

   @Override
   public ECFieldElement squareMinusProduct(ECFieldElement var1, ECFieldElement var2) {
      return this.squarePlusProduct(var1, var2);
   }

   @Override
   public ECFieldElement squarePlusProduct(ECFieldElement var1, ECFieldElement var2) {
      long[] var3 = this.x;
      long[] var4 = ((SecT239FieldElement)var1).x;
      long[] var5 = ((SecT239FieldElement)var2).x;
      long[] var6 = Nat256.createExt64();
      SecT239Field.squareAddToExt(var3, var6);
      SecT239Field.multiplyAddToExt(var4, var5, var6);
      long[] var7 = Nat256.create64();
      SecT239Field.reduce(var6, var7);
      return new SecT239FieldElement(var7);
   }

   @Override
   public ECFieldElement squarePow(int var1) {
      if (var1 < 1) {
         return this;
      } else {
         long[] var2 = Nat256.create64();
         SecT239Field.squareN(this.x, var1, var2);
         return new SecT239FieldElement(var2);
      }
   }

   @Override
   public ECFieldElement halfTrace() {
      long[] var1 = Nat256.create64();
      SecT239Field.halfTrace(this.x, var1);
      return new SecT239FieldElement(var1);
   }

   @Override
   public boolean hasFastTrace() {
      return true;
   }

   @Override
   public int trace() {
      return SecT239Field.trace(this.x);
   }

   @Override
   public ECFieldElement invert() {
      long[] var1 = Nat256.create64();
      SecT239Field.invert(this.x, var1);
      return new SecT239FieldElement(var1);
   }

   @Override
   public ECFieldElement sqrt() {
      long[] var1 = Nat256.create64();
      SecT239Field.sqrt(this.x, var1);
      return new SecT239FieldElement(var1);
   }

   public int getRepresentation() {
      return 2;
   }

   public int getM() {
      return 239;
   }

   public int getK1() {
      return 158;
   }

   public int getK2() {
      return 0;
   }

   public int getK3() {
      return 0;
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof SecT239FieldElement)) {
         return false;
      } else {
         SecT239FieldElement var2 = (SecT239FieldElement)var1;
         return Nat256.eq64(this.x, var2.x);
      }
   }

   @Override
   public int hashCode() {
      return 23900158 ^ Arrays.hashCode(this.x, 0, 4);
   }
}
