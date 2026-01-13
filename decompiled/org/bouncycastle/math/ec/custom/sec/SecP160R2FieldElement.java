package org.bouncycastle.math.ec.custom.sec;

import java.math.BigInteger;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.raw.Nat160;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.encoders.Hex;

public class SecP160R2FieldElement extends ECFieldElement.AbstractFp {
   public static final BigInteger Q = new BigInteger(1, Hex.decodeStrict("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEFFFFAC73"));
   protected int[] x;

   public SecP160R2FieldElement(BigInteger var1) {
      if (var1 != null && var1.signum() >= 0 && var1.compareTo(Q) < 0) {
         this.x = SecP160R2Field.fromBigInteger(var1);
      } else {
         throw new IllegalArgumentException("x value invalid for SecP160R2FieldElement");
      }
   }

   public SecP160R2FieldElement() {
      this.x = Nat160.create();
   }

   protected SecP160R2FieldElement(int[] var1) {
      this.x = var1;
   }

   @Override
   public boolean isZero() {
      return Nat160.isZero(this.x);
   }

   @Override
   public boolean isOne() {
      return Nat160.isOne(this.x);
   }

   @Override
   public boolean testBitZero() {
      return Nat160.getBit(this.x, 0) == 1;
   }

   @Override
   public BigInteger toBigInteger() {
      return Nat160.toBigInteger(this.x);
   }

   @Override
   public String getFieldName() {
      return "SecP160R2Field";
   }

   @Override
   public int getFieldSize() {
      return Q.bitLength();
   }

   @Override
   public ECFieldElement add(ECFieldElement var1) {
      int[] var2 = Nat160.create();
      SecP160R2Field.add(this.x, ((SecP160R2FieldElement)var1).x, var2);
      return new SecP160R2FieldElement(var2);
   }

   @Override
   public ECFieldElement addOne() {
      int[] var1 = Nat160.create();
      SecP160R2Field.addOne(this.x, var1);
      return new SecP160R2FieldElement(var1);
   }

   @Override
   public ECFieldElement subtract(ECFieldElement var1) {
      int[] var2 = Nat160.create();
      SecP160R2Field.subtract(this.x, ((SecP160R2FieldElement)var1).x, var2);
      return new SecP160R2FieldElement(var2);
   }

   @Override
   public ECFieldElement multiply(ECFieldElement var1) {
      int[] var2 = Nat160.create();
      SecP160R2Field.multiply(this.x, ((SecP160R2FieldElement)var1).x, var2);
      return new SecP160R2FieldElement(var2);
   }

   @Override
   public ECFieldElement divide(ECFieldElement var1) {
      int[] var2 = Nat160.create();
      SecP160R2Field.inv(((SecP160R2FieldElement)var1).x, var2);
      SecP160R2Field.multiply(var2, this.x, var2);
      return new SecP160R2FieldElement(var2);
   }

   @Override
   public ECFieldElement negate() {
      int[] var1 = Nat160.create();
      SecP160R2Field.negate(this.x, var1);
      return new SecP160R2FieldElement(var1);
   }

   @Override
   public ECFieldElement square() {
      int[] var1 = Nat160.create();
      SecP160R2Field.square(this.x, var1);
      return new SecP160R2FieldElement(var1);
   }

   @Override
   public ECFieldElement invert() {
      int[] var1 = Nat160.create();
      SecP160R2Field.inv(this.x, var1);
      return new SecP160R2FieldElement(var1);
   }

   @Override
   public ECFieldElement sqrt() {
      int[] var1 = this.x;
      if (!Nat160.isZero(var1) && !Nat160.isOne(var1)) {
         int[] var2 = Nat160.create();
         SecP160R2Field.square(var1, var2);
         SecP160R2Field.multiply(var2, var1, var2);
         int[] var3 = Nat160.create();
         SecP160R2Field.square(var2, var3);
         SecP160R2Field.multiply(var3, var1, var3);
         int[] var4 = Nat160.create();
         SecP160R2Field.square(var3, var4);
         SecP160R2Field.multiply(var4, var1, var4);
         int[] var5 = Nat160.create();
         SecP160R2Field.squareN(var4, 3, var5);
         SecP160R2Field.multiply(var5, var3, var5);
         SecP160R2Field.squareN(var5, 7, var4);
         SecP160R2Field.multiply(var4, var5, var4);
         SecP160R2Field.squareN(var4, 3, var5);
         SecP160R2Field.multiply(var5, var3, var5);
         int[] var8 = Nat160.create();
         SecP160R2Field.squareN(var5, 14, var8);
         SecP160R2Field.multiply(var8, var4, var8);
         SecP160R2Field.squareN(var8, 31, var4);
         SecP160R2Field.multiply(var4, var8, var4);
         SecP160R2Field.squareN(var4, 62, var8);
         SecP160R2Field.multiply(var8, var4, var8);
         SecP160R2Field.squareN(var8, 3, var4);
         SecP160R2Field.multiply(var4, var3, var4);
         SecP160R2Field.squareN(var4, 18, var4);
         SecP160R2Field.multiply(var4, var5, var4);
         SecP160R2Field.squareN(var4, 2, var4);
         SecP160R2Field.multiply(var4, var1, var4);
         SecP160R2Field.squareN(var4, 3, var4);
         SecP160R2Field.multiply(var4, var2, var4);
         SecP160R2Field.squareN(var4, 6, var4);
         SecP160R2Field.multiply(var4, var3, var4);
         SecP160R2Field.squareN(var4, 2, var4);
         SecP160R2Field.multiply(var4, var1, var4);
         SecP160R2Field.square(var4, var2);
         return Nat160.eq(var1, var2) ? new SecP160R2FieldElement(var4) : null;
      } else {
         return this;
      }
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof SecP160R2FieldElement)) {
         return false;
      } else {
         SecP160R2FieldElement var2 = (SecP160R2FieldElement)var1;
         return Nat160.eq(this.x, var2.x);
      }
   }

   @Override
   public int hashCode() {
      return Q.hashCode() ^ Arrays.hashCode(this.x, 0, 5);
   }
}
