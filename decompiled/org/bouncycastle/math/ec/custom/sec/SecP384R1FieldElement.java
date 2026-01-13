package org.bouncycastle.math.ec.custom.sec;

import java.math.BigInteger;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.encoders.Hex;

public class SecP384R1FieldElement extends ECFieldElement.AbstractFp {
   public static final BigInteger Q = new BigInteger(
      1, Hex.decodeStrict("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEFFFFFFFF0000000000000000FFFFFFFF")
   );
   protected int[] x;

   public SecP384R1FieldElement(BigInteger var1) {
      if (var1 != null && var1.signum() >= 0 && var1.compareTo(Q) < 0) {
         this.x = SecP384R1Field.fromBigInteger(var1);
      } else {
         throw new IllegalArgumentException("x value invalid for SecP384R1FieldElement");
      }
   }

   public SecP384R1FieldElement() {
      this.x = Nat.create(12);
   }

   protected SecP384R1FieldElement(int[] var1) {
      this.x = var1;
   }

   @Override
   public boolean isZero() {
      return Nat.isZero(12, this.x);
   }

   @Override
   public boolean isOne() {
      return Nat.isOne(12, this.x);
   }

   @Override
   public boolean testBitZero() {
      return Nat.getBit(this.x, 0) == 1;
   }

   @Override
   public BigInteger toBigInteger() {
      return Nat.toBigInteger(12, this.x);
   }

   @Override
   public String getFieldName() {
      return "SecP384R1Field";
   }

   @Override
   public int getFieldSize() {
      return Q.bitLength();
   }

   @Override
   public ECFieldElement add(ECFieldElement var1) {
      int[] var2 = Nat.create(12);
      SecP384R1Field.add(this.x, ((SecP384R1FieldElement)var1).x, var2);
      return new SecP384R1FieldElement(var2);
   }

   @Override
   public ECFieldElement addOne() {
      int[] var1 = Nat.create(12);
      SecP384R1Field.addOne(this.x, var1);
      return new SecP384R1FieldElement(var1);
   }

   @Override
   public ECFieldElement subtract(ECFieldElement var1) {
      int[] var2 = Nat.create(12);
      SecP384R1Field.subtract(this.x, ((SecP384R1FieldElement)var1).x, var2);
      return new SecP384R1FieldElement(var2);
   }

   @Override
   public ECFieldElement multiply(ECFieldElement var1) {
      int[] var2 = Nat.create(12);
      SecP384R1Field.multiply(this.x, ((SecP384R1FieldElement)var1).x, var2);
      return new SecP384R1FieldElement(var2);
   }

   @Override
   public ECFieldElement divide(ECFieldElement var1) {
      int[] var2 = Nat.create(12);
      SecP384R1Field.inv(((SecP384R1FieldElement)var1).x, var2);
      SecP384R1Field.multiply(var2, this.x, var2);
      return new SecP384R1FieldElement(var2);
   }

   @Override
   public ECFieldElement negate() {
      int[] var1 = Nat.create(12);
      SecP384R1Field.negate(this.x, var1);
      return new SecP384R1FieldElement(var1);
   }

   @Override
   public ECFieldElement square() {
      int[] var1 = Nat.create(12);
      SecP384R1Field.square(this.x, var1);
      return new SecP384R1FieldElement(var1);
   }

   @Override
   public ECFieldElement invert() {
      int[] var1 = Nat.create(12);
      SecP384R1Field.inv(this.x, var1);
      return new SecP384R1FieldElement(var1);
   }

   @Override
   public ECFieldElement sqrt() {
      int[] var1 = this.x;
      if (!Nat.isZero(12, var1) && !Nat.isOne(12, var1)) {
         int[] var2 = Nat.create(24);
         int[] var3 = Nat.create(12);
         int[] var4 = Nat.create(12);
         int[] var5 = Nat.create(12);
         int[] var6 = Nat.create(12);
         SecP384R1Field.square(var1, var3, var2);
         SecP384R1Field.multiply(var3, var1, var3, var2);
         SecP384R1Field.squareN(var3, 2, var4, var2);
         SecP384R1Field.multiply(var4, var3, var4, var2);
         SecP384R1Field.square(var4, var4, var2);
         SecP384R1Field.multiply(var4, var1, var4, var2);
         SecP384R1Field.squareN(var4, 5, var5, var2);
         SecP384R1Field.multiply(var5, var4, var5, var2);
         SecP384R1Field.squareN(var5, 5, var6, var2);
         SecP384R1Field.multiply(var6, var4, var6, var2);
         SecP384R1Field.squareN(var6, 15, var4, var2);
         SecP384R1Field.multiply(var4, var6, var4, var2);
         SecP384R1Field.squareN(var4, 2, var5, var2);
         SecP384R1Field.multiply(var3, var5, var3, var2);
         SecP384R1Field.squareN(var5, 28, var5, var2);
         SecP384R1Field.multiply(var4, var5, var4, var2);
         SecP384R1Field.squareN(var4, 60, var5, var2);
         SecP384R1Field.multiply(var5, var4, var5, var2);
         SecP384R1Field.squareN(var5, 120, var4, var2);
         SecP384R1Field.multiply(var4, var5, var4, var2);
         SecP384R1Field.squareN(var4, 15, var4, var2);
         SecP384R1Field.multiply(var4, var6, var4, var2);
         SecP384R1Field.squareN(var4, 33, var4, var2);
         SecP384R1Field.multiply(var4, var3, var4, var2);
         SecP384R1Field.squareN(var4, 64, var4, var2);
         SecP384R1Field.multiply(var4, var1, var4, var2);
         SecP384R1Field.squareN(var4, 30, var3, var2);
         SecP384R1Field.square(var3, var4, var2);
         return Nat.eq(12, var1, var4) ? new SecP384R1FieldElement(var3) : null;
      } else {
         return this;
      }
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof SecP384R1FieldElement)) {
         return false;
      } else {
         SecP384R1FieldElement var2 = (SecP384R1FieldElement)var1;
         return Nat.eq(12, this.x, var2.x);
      }
   }

   @Override
   public int hashCode() {
      return Q.hashCode() ^ Arrays.hashCode(this.x, 0, 12);
   }
}
