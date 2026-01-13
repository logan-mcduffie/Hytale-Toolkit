package org.bouncycastle.math.ec.custom.sec;

import java.math.BigInteger;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.raw.Nat160;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.encoders.Hex;

public class SecP160R1FieldElement extends ECFieldElement.AbstractFp {
   public static final BigInteger Q = new BigInteger(1, Hex.decodeStrict("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF7FFFFFFF"));
   protected int[] x;

   public SecP160R1FieldElement(BigInteger var1) {
      if (var1 != null && var1.signum() >= 0 && var1.compareTo(Q) < 0) {
         this.x = SecP160R1Field.fromBigInteger(var1);
      } else {
         throw new IllegalArgumentException("x value invalid for SecP160R1FieldElement");
      }
   }

   public SecP160R1FieldElement() {
      this.x = Nat160.create();
   }

   protected SecP160R1FieldElement(int[] var1) {
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
      return "SecP160R1Field";
   }

   @Override
   public int getFieldSize() {
      return Q.bitLength();
   }

   @Override
   public ECFieldElement add(ECFieldElement var1) {
      int[] var2 = Nat160.create();
      SecP160R1Field.add(this.x, ((SecP160R1FieldElement)var1).x, var2);
      return new SecP160R1FieldElement(var2);
   }

   @Override
   public ECFieldElement addOne() {
      int[] var1 = Nat160.create();
      SecP160R1Field.addOne(this.x, var1);
      return new SecP160R1FieldElement(var1);
   }

   @Override
   public ECFieldElement subtract(ECFieldElement var1) {
      int[] var2 = Nat160.create();
      SecP160R1Field.subtract(this.x, ((SecP160R1FieldElement)var1).x, var2);
      return new SecP160R1FieldElement(var2);
   }

   @Override
   public ECFieldElement multiply(ECFieldElement var1) {
      int[] var2 = Nat160.create();
      SecP160R1Field.multiply(this.x, ((SecP160R1FieldElement)var1).x, var2);
      return new SecP160R1FieldElement(var2);
   }

   @Override
   public ECFieldElement divide(ECFieldElement var1) {
      int[] var2 = Nat160.create();
      SecP160R1Field.inv(((SecP160R1FieldElement)var1).x, var2);
      SecP160R1Field.multiply(var2, this.x, var2);
      return new SecP160R1FieldElement(var2);
   }

   @Override
   public ECFieldElement negate() {
      int[] var1 = Nat160.create();
      SecP160R1Field.negate(this.x, var1);
      return new SecP160R1FieldElement(var1);
   }

   @Override
   public ECFieldElement square() {
      int[] var1 = Nat160.create();
      SecP160R1Field.square(this.x, var1);
      return new SecP160R1FieldElement(var1);
   }

   @Override
   public ECFieldElement invert() {
      int[] var1 = Nat160.create();
      SecP160R1Field.inv(this.x, var1);
      return new SecP160R1FieldElement(var1);
   }

   @Override
   public ECFieldElement sqrt() {
      int[] var1 = this.x;
      if (!Nat160.isZero(var1) && !Nat160.isOne(var1)) {
         int[] var2 = Nat160.create();
         SecP160R1Field.square(var1, var2);
         SecP160R1Field.multiply(var2, var1, var2);
         int[] var3 = Nat160.create();
         SecP160R1Field.squareN(var2, 2, var3);
         SecP160R1Field.multiply(var3, var2, var3);
         SecP160R1Field.squareN(var3, 4, var2);
         SecP160R1Field.multiply(var2, var3, var2);
         SecP160R1Field.squareN(var2, 8, var3);
         SecP160R1Field.multiply(var3, var2, var3);
         SecP160R1Field.squareN(var3, 16, var2);
         SecP160R1Field.multiply(var2, var3, var2);
         SecP160R1Field.squareN(var2, 32, var3);
         SecP160R1Field.multiply(var3, var2, var3);
         SecP160R1Field.squareN(var3, 64, var2);
         SecP160R1Field.multiply(var2, var3, var2);
         SecP160R1Field.square(var2, var3);
         SecP160R1Field.multiply(var3, var1, var3);
         SecP160R1Field.squareN(var3, 29, var3);
         SecP160R1Field.square(var3, var2);
         return Nat160.eq(var1, var2) ? new SecP160R1FieldElement(var3) : null;
      } else {
         return this;
      }
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof SecP160R1FieldElement)) {
         return false;
      } else {
         SecP160R1FieldElement var2 = (SecP160R1FieldElement)var1;
         return Nat160.eq(this.x, var2.x);
      }
   }

   @Override
   public int hashCode() {
      return Q.hashCode() ^ Arrays.hashCode(this.x, 0, 5);
   }
}
