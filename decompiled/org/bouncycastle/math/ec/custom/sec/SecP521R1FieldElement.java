package org.bouncycastle.math.ec.custom.sec;

import java.math.BigInteger;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.encoders.Hex;

public class SecP521R1FieldElement extends ECFieldElement.AbstractFp {
   public static final BigInteger Q = new BigInteger(
      1,
      Hex.decodeStrict("01FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF")
   );
   protected int[] x;

   public SecP521R1FieldElement(BigInteger var1) {
      if (var1 != null && var1.signum() >= 0 && var1.compareTo(Q) < 0) {
         this.x = SecP521R1Field.fromBigInteger(var1);
      } else {
         throw new IllegalArgumentException("x value invalid for SecP521R1FieldElement");
      }
   }

   public SecP521R1FieldElement() {
      this.x = Nat.create(17);
   }

   protected SecP521R1FieldElement(int[] var1) {
      this.x = var1;
   }

   @Override
   public boolean isZero() {
      return Nat.isZero(17, this.x);
   }

   @Override
   public boolean isOne() {
      return Nat.isOne(17, this.x);
   }

   @Override
   public boolean testBitZero() {
      return Nat.getBit(this.x, 0) == 1;
   }

   @Override
   public BigInteger toBigInteger() {
      return Nat.toBigInteger(17, this.x);
   }

   @Override
   public String getFieldName() {
      return "SecP521R1Field";
   }

   @Override
   public int getFieldSize() {
      return Q.bitLength();
   }

   @Override
   public ECFieldElement add(ECFieldElement var1) {
      int[] var2 = Nat.create(17);
      SecP521R1Field.add(this.x, ((SecP521R1FieldElement)var1).x, var2);
      return new SecP521R1FieldElement(var2);
   }

   @Override
   public ECFieldElement addOne() {
      int[] var1 = Nat.create(17);
      SecP521R1Field.addOne(this.x, var1);
      return new SecP521R1FieldElement(var1);
   }

   @Override
   public ECFieldElement subtract(ECFieldElement var1) {
      int[] var2 = Nat.create(17);
      SecP521R1Field.subtract(this.x, ((SecP521R1FieldElement)var1).x, var2);
      return new SecP521R1FieldElement(var2);
   }

   @Override
   public ECFieldElement multiply(ECFieldElement var1) {
      int[] var2 = Nat.create(17);
      SecP521R1Field.multiply(this.x, ((SecP521R1FieldElement)var1).x, var2);
      return new SecP521R1FieldElement(var2);
   }

   @Override
   public ECFieldElement divide(ECFieldElement var1) {
      int[] var2 = Nat.create(17);
      SecP521R1Field.inv(((SecP521R1FieldElement)var1).x, var2);
      SecP521R1Field.multiply(var2, this.x, var2);
      return new SecP521R1FieldElement(var2);
   }

   @Override
   public ECFieldElement negate() {
      int[] var1 = Nat.create(17);
      SecP521R1Field.negate(this.x, var1);
      return new SecP521R1FieldElement(var1);
   }

   @Override
   public ECFieldElement square() {
      int[] var1 = Nat.create(17);
      SecP521R1Field.square(this.x, var1);
      return new SecP521R1FieldElement(var1);
   }

   @Override
   public ECFieldElement invert() {
      int[] var1 = Nat.create(17);
      SecP521R1Field.inv(this.x, var1);
      return new SecP521R1FieldElement(var1);
   }

   @Override
   public ECFieldElement sqrt() {
      int[] var1 = this.x;
      if (!Nat.isZero(17, var1) && !Nat.isOne(17, var1)) {
         int[] var2 = Nat.create(33);
         int[] var3 = Nat.create(17);
         int[] var4 = Nat.create(17);
         SecP521R1Field.squareN(var1, 519, var3, var2);
         SecP521R1Field.square(var3, var4, var2);
         return Nat.eq(17, var1, var4) ? new SecP521R1FieldElement(var3) : null;
      } else {
         return this;
      }
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof SecP521R1FieldElement)) {
         return false;
      } else {
         SecP521R1FieldElement var2 = (SecP521R1FieldElement)var1;
         return Nat.eq(17, this.x, var2.x);
      }
   }

   @Override
   public int hashCode() {
      return Q.hashCode() ^ Arrays.hashCode(this.x, 0, 17);
   }
}
