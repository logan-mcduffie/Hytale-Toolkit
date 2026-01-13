package org.bouncycastle.math.ec.custom.sec;

import java.math.BigInteger;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.raw.Nat576;
import org.bouncycastle.util.Arrays;

public class SecT571FieldElement extends ECFieldElement.AbstractF2m {
   protected long[] x;

   public SecT571FieldElement(BigInteger var1) {
      if (var1 != null && var1.signum() >= 0 && var1.bitLength() <= 571) {
         this.x = SecT571Field.fromBigInteger(var1);
      } else {
         throw new IllegalArgumentException("x value invalid for SecT571FieldElement");
      }
   }

   public SecT571FieldElement() {
      this.x = Nat576.create64();
   }

   protected SecT571FieldElement(long[] var1) {
      this.x = var1;
   }

   @Override
   public boolean isOne() {
      return Nat576.isOne64(this.x);
   }

   @Override
   public boolean isZero() {
      return Nat576.isZero64(this.x);
   }

   @Override
   public boolean testBitZero() {
      return (this.x[0] & 1L) != 0L;
   }

   @Override
   public BigInteger toBigInteger() {
      return Nat576.toBigInteger64(this.x);
   }

   @Override
   public String getFieldName() {
      return "SecT571Field";
   }

   @Override
   public int getFieldSize() {
      return 571;
   }

   @Override
   public ECFieldElement add(ECFieldElement var1) {
      long[] var2 = Nat576.create64();
      SecT571Field.add(this.x, ((SecT571FieldElement)var1).x, var2);
      return new SecT571FieldElement(var2);
   }

   @Override
   public ECFieldElement addOne() {
      long[] var1 = Nat576.create64();
      SecT571Field.addOne(this.x, var1);
      return new SecT571FieldElement(var1);
   }

   @Override
   public ECFieldElement subtract(ECFieldElement var1) {
      return this.add(var1);
   }

   @Override
   public ECFieldElement multiply(ECFieldElement var1) {
      long[] var2 = Nat576.create64();
      SecT571Field.multiply(this.x, ((SecT571FieldElement)var1).x, var2);
      return new SecT571FieldElement(var2);
   }

   @Override
   public ECFieldElement multiplyMinusProduct(ECFieldElement var1, ECFieldElement var2, ECFieldElement var3) {
      return this.multiplyPlusProduct(var1, var2, var3);
   }

   @Override
   public ECFieldElement multiplyPlusProduct(ECFieldElement var1, ECFieldElement var2, ECFieldElement var3) {
      long[] var4 = this.x;
      long[] var5 = ((SecT571FieldElement)var1).x;
      long[] var6 = ((SecT571FieldElement)var2).x;
      long[] var7 = ((SecT571FieldElement)var3).x;
      long[] var8 = Nat576.createExt64();
      SecT571Field.multiplyAddToExt(var4, var5, var8);
      SecT571Field.multiplyAddToExt(var6, var7, var8);
      long[] var9 = Nat576.create64();
      SecT571Field.reduce(var8, var9);
      return new SecT571FieldElement(var9);
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
      long[] var1 = Nat576.create64();
      SecT571Field.square(this.x, var1);
      return new SecT571FieldElement(var1);
   }

   @Override
   public ECFieldElement squareMinusProduct(ECFieldElement var1, ECFieldElement var2) {
      return this.squarePlusProduct(var1, var2);
   }

   @Override
   public ECFieldElement squarePlusProduct(ECFieldElement var1, ECFieldElement var2) {
      long[] var3 = this.x;
      long[] var4 = ((SecT571FieldElement)var1).x;
      long[] var5 = ((SecT571FieldElement)var2).x;
      long[] var6 = Nat576.createExt64();
      SecT571Field.squareAddToExt(var3, var6);
      SecT571Field.multiplyAddToExt(var4, var5, var6);
      long[] var7 = Nat576.create64();
      SecT571Field.reduce(var6, var7);
      return new SecT571FieldElement(var7);
   }

   @Override
   public ECFieldElement squarePow(int var1) {
      if (var1 < 1) {
         return this;
      } else {
         long[] var2 = Nat576.create64();
         SecT571Field.squareN(this.x, var1, var2);
         return new SecT571FieldElement(var2);
      }
   }

   @Override
   public ECFieldElement halfTrace() {
      long[] var1 = Nat576.create64();
      SecT571Field.halfTrace(this.x, var1);
      return new SecT571FieldElement(var1);
   }

   @Override
   public boolean hasFastTrace() {
      return true;
   }

   @Override
   public int trace() {
      return SecT571Field.trace(this.x);
   }

   @Override
   public ECFieldElement invert() {
      long[] var1 = Nat576.create64();
      SecT571Field.invert(this.x, var1);
      return new SecT571FieldElement(var1);
   }

   @Override
   public ECFieldElement sqrt() {
      long[] var1 = Nat576.create64();
      SecT571Field.sqrt(this.x, var1);
      return new SecT571FieldElement(var1);
   }

   public int getRepresentation() {
      return 3;
   }

   public int getM() {
      return 571;
   }

   public int getK1() {
      return 2;
   }

   public int getK2() {
      return 5;
   }

   public int getK3() {
      return 10;
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof SecT571FieldElement)) {
         return false;
      } else {
         SecT571FieldElement var2 = (SecT571FieldElement)var1;
         return Nat576.eq64(this.x, var2.x);
      }
   }

   @Override
   public int hashCode() {
      return 5711052 ^ Arrays.hashCode(this.x, 0, 9);
   }
}
