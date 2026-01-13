package org.bouncycastle.math.ec.custom.sec;

import java.math.BigInteger;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.raw.Nat192;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.encoders.Hex;

public class SecP192K1FieldElement extends ECFieldElement.AbstractFp {
   public static final BigInteger Q = new BigInteger(1, Hex.decodeStrict("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEFFFFEE37"));
   protected int[] x;

   public SecP192K1FieldElement(BigInteger var1) {
      if (var1 != null && var1.signum() >= 0 && var1.compareTo(Q) < 0) {
         this.x = SecP192K1Field.fromBigInteger(var1);
      } else {
         throw new IllegalArgumentException("x value invalid for SecP192K1FieldElement");
      }
   }

   public SecP192K1FieldElement() {
      this.x = Nat192.create();
   }

   protected SecP192K1FieldElement(int[] var1) {
      this.x = var1;
   }

   @Override
   public boolean isZero() {
      return Nat192.isZero(this.x);
   }

   @Override
   public boolean isOne() {
      return Nat192.isOne(this.x);
   }

   @Override
   public boolean testBitZero() {
      return Nat192.getBit(this.x, 0) == 1;
   }

   @Override
   public BigInteger toBigInteger() {
      return Nat192.toBigInteger(this.x);
   }

   @Override
   public String getFieldName() {
      return "SecP192K1Field";
   }

   @Override
   public int getFieldSize() {
      return Q.bitLength();
   }

   @Override
   public ECFieldElement add(ECFieldElement var1) {
      int[] var2 = Nat192.create();
      SecP192K1Field.add(this.x, ((SecP192K1FieldElement)var1).x, var2);
      return new SecP192K1FieldElement(var2);
   }

   @Override
   public ECFieldElement addOne() {
      int[] var1 = Nat192.create();
      SecP192K1Field.addOne(this.x, var1);
      return new SecP192K1FieldElement(var1);
   }

   @Override
   public ECFieldElement subtract(ECFieldElement var1) {
      int[] var2 = Nat192.create();
      SecP192K1Field.subtract(this.x, ((SecP192K1FieldElement)var1).x, var2);
      return new SecP192K1FieldElement(var2);
   }

   @Override
   public ECFieldElement multiply(ECFieldElement var1) {
      int[] var2 = Nat192.create();
      SecP192K1Field.multiply(this.x, ((SecP192K1FieldElement)var1).x, var2);
      return new SecP192K1FieldElement(var2);
   }

   @Override
   public ECFieldElement divide(ECFieldElement var1) {
      int[] var2 = Nat192.create();
      SecP192K1Field.inv(((SecP192K1FieldElement)var1).x, var2);
      SecP192K1Field.multiply(var2, this.x, var2);
      return new SecP192K1FieldElement(var2);
   }

   @Override
   public ECFieldElement negate() {
      int[] var1 = Nat192.create();
      SecP192K1Field.negate(this.x, var1);
      return new SecP192K1FieldElement(var1);
   }

   @Override
   public ECFieldElement square() {
      int[] var1 = Nat192.create();
      SecP192K1Field.square(this.x, var1);
      return new SecP192K1FieldElement(var1);
   }

   @Override
   public ECFieldElement invert() {
      int[] var1 = Nat192.create();
      SecP192K1Field.inv(this.x, var1);
      return new SecP192K1FieldElement(var1);
   }

   @Override
   public ECFieldElement sqrt() {
      int[] var1 = this.x;
      if (!Nat192.isZero(var1) && !Nat192.isOne(var1)) {
         int[] var2 = Nat192.create();
         SecP192K1Field.square(var1, var2);
         SecP192K1Field.multiply(var2, var1, var2);
         int[] var3 = Nat192.create();
         SecP192K1Field.square(var2, var3);
         SecP192K1Field.multiply(var3, var1, var3);
         int[] var4 = Nat192.create();
         SecP192K1Field.squareN(var3, 3, var4);
         SecP192K1Field.multiply(var4, var3, var4);
         SecP192K1Field.squareN(var4, 2, var4);
         SecP192K1Field.multiply(var4, var2, var4);
         SecP192K1Field.squareN(var4, 8, var2);
         SecP192K1Field.multiply(var2, var4, var2);
         SecP192K1Field.squareN(var2, 3, var4);
         SecP192K1Field.multiply(var4, var3, var4);
         int[] var8 = Nat192.create();
         SecP192K1Field.squareN(var4, 16, var8);
         SecP192K1Field.multiply(var8, var2, var8);
         SecP192K1Field.squareN(var8, 35, var2);
         SecP192K1Field.multiply(var2, var8, var2);
         SecP192K1Field.squareN(var2, 70, var8);
         SecP192K1Field.multiply(var8, var2, var8);
         SecP192K1Field.squareN(var8, 19, var2);
         SecP192K1Field.multiply(var2, var4, var2);
         SecP192K1Field.squareN(var2, 20, var2);
         SecP192K1Field.multiply(var2, var4, var2);
         SecP192K1Field.squareN(var2, 4, var2);
         SecP192K1Field.multiply(var2, var3, var2);
         SecP192K1Field.squareN(var2, 6, var2);
         SecP192K1Field.multiply(var2, var3, var2);
         SecP192K1Field.square(var2, var2);
         SecP192K1Field.square(var2, var3);
         return Nat192.eq(var1, var3) ? new SecP192K1FieldElement(var2) : null;
      } else {
         return this;
      }
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof SecP192K1FieldElement)) {
         return false;
      } else {
         SecP192K1FieldElement var2 = (SecP192K1FieldElement)var1;
         return Nat192.eq(this.x, var2.x);
      }
   }

   @Override
   public int hashCode() {
      return Q.hashCode() ^ Arrays.hashCode(this.x, 0, 6);
   }
}
