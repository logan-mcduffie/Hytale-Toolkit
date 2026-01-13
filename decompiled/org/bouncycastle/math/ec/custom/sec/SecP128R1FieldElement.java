package org.bouncycastle.math.ec.custom.sec;

import java.math.BigInteger;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.raw.Nat128;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.encoders.Hex;

public class SecP128R1FieldElement extends ECFieldElement.AbstractFp {
   public static final BigInteger Q = new BigInteger(1, Hex.decodeStrict("FFFFFFFDFFFFFFFFFFFFFFFFFFFFFFFF"));
   protected int[] x;

   public SecP128R1FieldElement(BigInteger var1) {
      if (var1 != null && var1.signum() >= 0 && var1.compareTo(Q) < 0) {
         this.x = SecP128R1Field.fromBigInteger(var1);
      } else {
         throw new IllegalArgumentException("x value invalid for SecP128R1FieldElement");
      }
   }

   public SecP128R1FieldElement() {
      this.x = Nat128.create();
   }

   protected SecP128R1FieldElement(int[] var1) {
      this.x = var1;
   }

   @Override
   public boolean isZero() {
      return Nat128.isZero(this.x);
   }

   @Override
   public boolean isOne() {
      return Nat128.isOne(this.x);
   }

   @Override
   public boolean testBitZero() {
      return Nat128.getBit(this.x, 0) == 1;
   }

   @Override
   public BigInteger toBigInteger() {
      return Nat128.toBigInteger(this.x);
   }

   @Override
   public String getFieldName() {
      return "SecP128R1Field";
   }

   @Override
   public int getFieldSize() {
      return Q.bitLength();
   }

   @Override
   public ECFieldElement add(ECFieldElement var1) {
      int[] var2 = Nat128.create();
      SecP128R1Field.add(this.x, ((SecP128R1FieldElement)var1).x, var2);
      return new SecP128R1FieldElement(var2);
   }

   @Override
   public ECFieldElement addOne() {
      int[] var1 = Nat128.create();
      SecP128R1Field.addOne(this.x, var1);
      return new SecP128R1FieldElement(var1);
   }

   @Override
   public ECFieldElement subtract(ECFieldElement var1) {
      int[] var2 = Nat128.create();
      SecP128R1Field.subtract(this.x, ((SecP128R1FieldElement)var1).x, var2);
      return new SecP128R1FieldElement(var2);
   }

   @Override
   public ECFieldElement multiply(ECFieldElement var1) {
      int[] var2 = Nat128.create();
      SecP128R1Field.multiply(this.x, ((SecP128R1FieldElement)var1).x, var2);
      return new SecP128R1FieldElement(var2);
   }

   @Override
   public ECFieldElement divide(ECFieldElement var1) {
      int[] var2 = Nat128.create();
      SecP128R1Field.inv(((SecP128R1FieldElement)var1).x, var2);
      SecP128R1Field.multiply(var2, this.x, var2);
      return new SecP128R1FieldElement(var2);
   }

   @Override
   public ECFieldElement negate() {
      int[] var1 = Nat128.create();
      SecP128R1Field.negate(this.x, var1);
      return new SecP128R1FieldElement(var1);
   }

   @Override
   public ECFieldElement square() {
      int[] var1 = Nat128.create();
      SecP128R1Field.square(this.x, var1);
      return new SecP128R1FieldElement(var1);
   }

   @Override
   public ECFieldElement invert() {
      int[] var1 = Nat128.create();
      SecP128R1Field.inv(this.x, var1);
      return new SecP128R1FieldElement(var1);
   }

   @Override
   public ECFieldElement sqrt() {
      int[] var1 = this.x;
      if (!Nat128.isZero(var1) && !Nat128.isOne(var1)) {
         int[] var2 = Nat128.create();
         SecP128R1Field.square(var1, var2);
         SecP128R1Field.multiply(var2, var1, var2);
         int[] var3 = Nat128.create();
         SecP128R1Field.squareN(var2, 2, var3);
         SecP128R1Field.multiply(var3, var2, var3);
         int[] var4 = Nat128.create();
         SecP128R1Field.squareN(var3, 4, var4);
         SecP128R1Field.multiply(var4, var3, var4);
         SecP128R1Field.squareN(var4, 2, var3);
         SecP128R1Field.multiply(var3, var2, var3);
         SecP128R1Field.squareN(var3, 10, var2);
         SecP128R1Field.multiply(var2, var3, var2);
         SecP128R1Field.squareN(var2, 10, var4);
         SecP128R1Field.multiply(var4, var3, var4);
         SecP128R1Field.square(var4, var3);
         SecP128R1Field.multiply(var3, var1, var3);
         SecP128R1Field.squareN(var3, 95, var3);
         SecP128R1Field.square(var3, var4);
         return Nat128.eq(var1, var4) ? new SecP128R1FieldElement(var3) : null;
      } else {
         return this;
      }
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof SecP128R1FieldElement)) {
         return false;
      } else {
         SecP128R1FieldElement var2 = (SecP128R1FieldElement)var1;
         return Nat128.eq(this.x, var2.x);
      }
   }

   @Override
   public int hashCode() {
      return Q.hashCode() ^ Arrays.hashCode(this.x, 0, 4);
   }
}
