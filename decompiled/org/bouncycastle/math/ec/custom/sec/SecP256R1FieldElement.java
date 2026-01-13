package org.bouncycastle.math.ec.custom.sec;

import java.math.BigInteger;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.raw.Nat256;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.encoders.Hex;

public class SecP256R1FieldElement extends ECFieldElement.AbstractFp {
   public static final BigInteger Q = new BigInteger(1, Hex.decodeStrict("FFFFFFFF00000001000000000000000000000000FFFFFFFFFFFFFFFFFFFFFFFF"));
   protected int[] x;

   public SecP256R1FieldElement(BigInteger var1) {
      if (var1 != null && var1.signum() >= 0 && var1.compareTo(Q) < 0) {
         this.x = SecP256R1Field.fromBigInteger(var1);
      } else {
         throw new IllegalArgumentException("x value invalid for SecP256R1FieldElement");
      }
   }

   public SecP256R1FieldElement() {
      this.x = Nat256.create();
   }

   protected SecP256R1FieldElement(int[] var1) {
      this.x = var1;
   }

   @Override
   public boolean isZero() {
      return Nat256.isZero(this.x);
   }

   @Override
   public boolean isOne() {
      return Nat256.isOne(this.x);
   }

   @Override
   public boolean testBitZero() {
      return Nat256.getBit(this.x, 0) == 1;
   }

   @Override
   public BigInteger toBigInteger() {
      return Nat256.toBigInteger(this.x);
   }

   @Override
   public String getFieldName() {
      return "SecP256R1Field";
   }

   @Override
   public int getFieldSize() {
      return Q.bitLength();
   }

   @Override
   public ECFieldElement add(ECFieldElement var1) {
      int[] var2 = Nat256.create();
      SecP256R1Field.add(this.x, ((SecP256R1FieldElement)var1).x, var2);
      return new SecP256R1FieldElement(var2);
   }

   @Override
   public ECFieldElement addOne() {
      int[] var1 = Nat256.create();
      SecP256R1Field.addOne(this.x, var1);
      return new SecP256R1FieldElement(var1);
   }

   @Override
   public ECFieldElement subtract(ECFieldElement var1) {
      int[] var2 = Nat256.create();
      SecP256R1Field.subtract(this.x, ((SecP256R1FieldElement)var1).x, var2);
      return new SecP256R1FieldElement(var2);
   }

   @Override
   public ECFieldElement multiply(ECFieldElement var1) {
      int[] var2 = Nat256.create();
      SecP256R1Field.multiply(this.x, ((SecP256R1FieldElement)var1).x, var2);
      return new SecP256R1FieldElement(var2);
   }

   @Override
   public ECFieldElement divide(ECFieldElement var1) {
      int[] var2 = Nat256.create();
      SecP256R1Field.inv(((SecP256R1FieldElement)var1).x, var2);
      SecP256R1Field.multiply(var2, this.x, var2);
      return new SecP256R1FieldElement(var2);
   }

   @Override
   public ECFieldElement negate() {
      int[] var1 = Nat256.create();
      SecP256R1Field.negate(this.x, var1);
      return new SecP256R1FieldElement(var1);
   }

   @Override
   public ECFieldElement square() {
      int[] var1 = Nat256.create();
      SecP256R1Field.square(this.x, var1);
      return new SecP256R1FieldElement(var1);
   }

   @Override
   public ECFieldElement invert() {
      int[] var1 = Nat256.create();
      SecP256R1Field.inv(this.x, var1);
      return new SecP256R1FieldElement(var1);
   }

   @Override
   public ECFieldElement sqrt() {
      int[] var1 = this.x;
      if (!Nat256.isZero(var1) && !Nat256.isOne(var1)) {
         int[] var2 = Nat256.createExt();
         int[] var3 = Nat256.create();
         int[] var4 = Nat256.create();
         SecP256R1Field.square(var1, var3, var2);
         SecP256R1Field.multiply(var3, var1, var3, var2);
         SecP256R1Field.squareN(var3, 2, var4, var2);
         SecP256R1Field.multiply(var4, var3, var4, var2);
         SecP256R1Field.squareN(var4, 4, var3, var2);
         SecP256R1Field.multiply(var3, var4, var3, var2);
         SecP256R1Field.squareN(var3, 8, var4, var2);
         SecP256R1Field.multiply(var4, var3, var4, var2);
         SecP256R1Field.squareN(var4, 16, var3, var2);
         SecP256R1Field.multiply(var3, var4, var3, var2);
         SecP256R1Field.squareN(var3, 32, var3, var2);
         SecP256R1Field.multiply(var3, var1, var3, var2);
         SecP256R1Field.squareN(var3, 96, var3, var2);
         SecP256R1Field.multiply(var3, var1, var3, var2);
         SecP256R1Field.squareN(var3, 94, var3, var2);
         SecP256R1Field.square(var3, var4, var2);
         return Nat256.eq(var1, var4) ? new SecP256R1FieldElement(var3) : null;
      } else {
         return this;
      }
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof SecP256R1FieldElement)) {
         return false;
      } else {
         SecP256R1FieldElement var2 = (SecP256R1FieldElement)var1;
         return Nat256.eq(this.x, var2.x);
      }
   }

   @Override
   public int hashCode() {
      return Q.hashCode() ^ Arrays.hashCode(this.x, 0, 8);
   }
}
