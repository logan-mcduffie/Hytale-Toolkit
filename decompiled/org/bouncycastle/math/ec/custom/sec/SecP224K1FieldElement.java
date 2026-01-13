package org.bouncycastle.math.ec.custom.sec;

import java.math.BigInteger;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.raw.Nat224;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.encoders.Hex;

public class SecP224K1FieldElement extends ECFieldElement.AbstractFp {
   public static final BigInteger Q = new BigInteger(1, Hex.decodeStrict("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEFFFFE56D"));
   private static final int[] PRECOMP_POW2 = new int[]{868209154, -587542221, 579297866, -1014948952, -1470801668, 514782679, -1897982644};
   protected int[] x;

   public SecP224K1FieldElement(BigInteger var1) {
      if (var1 != null && var1.signum() >= 0 && var1.compareTo(Q) < 0) {
         this.x = SecP224K1Field.fromBigInteger(var1);
      } else {
         throw new IllegalArgumentException("x value invalid for SecP224K1FieldElement");
      }
   }

   public SecP224K1FieldElement() {
      this.x = Nat224.create();
   }

   protected SecP224K1FieldElement(int[] var1) {
      this.x = var1;
   }

   @Override
   public boolean isZero() {
      return Nat224.isZero(this.x);
   }

   @Override
   public boolean isOne() {
      return Nat224.isOne(this.x);
   }

   @Override
   public boolean testBitZero() {
      return Nat224.getBit(this.x, 0) == 1;
   }

   @Override
   public BigInteger toBigInteger() {
      return Nat224.toBigInteger(this.x);
   }

   @Override
   public String getFieldName() {
      return "SecP224K1Field";
   }

   @Override
   public int getFieldSize() {
      return Q.bitLength();
   }

   @Override
   public ECFieldElement add(ECFieldElement var1) {
      int[] var2 = Nat224.create();
      SecP224K1Field.add(this.x, ((SecP224K1FieldElement)var1).x, var2);
      return new SecP224K1FieldElement(var2);
   }

   @Override
   public ECFieldElement addOne() {
      int[] var1 = Nat224.create();
      SecP224K1Field.addOne(this.x, var1);
      return new SecP224K1FieldElement(var1);
   }

   @Override
   public ECFieldElement subtract(ECFieldElement var1) {
      int[] var2 = Nat224.create();
      SecP224K1Field.subtract(this.x, ((SecP224K1FieldElement)var1).x, var2);
      return new SecP224K1FieldElement(var2);
   }

   @Override
   public ECFieldElement multiply(ECFieldElement var1) {
      int[] var2 = Nat224.create();
      SecP224K1Field.multiply(this.x, ((SecP224K1FieldElement)var1).x, var2);
      return new SecP224K1FieldElement(var2);
   }

   @Override
   public ECFieldElement divide(ECFieldElement var1) {
      int[] var2 = Nat224.create();
      SecP224K1Field.inv(((SecP224K1FieldElement)var1).x, var2);
      SecP224K1Field.multiply(var2, this.x, var2);
      return new SecP224K1FieldElement(var2);
   }

   @Override
   public ECFieldElement negate() {
      int[] var1 = Nat224.create();
      SecP224K1Field.negate(this.x, var1);
      return new SecP224K1FieldElement(var1);
   }

   @Override
   public ECFieldElement square() {
      int[] var1 = Nat224.create();
      SecP224K1Field.square(this.x, var1);
      return new SecP224K1FieldElement(var1);
   }

   @Override
   public ECFieldElement invert() {
      int[] var1 = Nat224.create();
      SecP224K1Field.inv(this.x, var1);
      return new SecP224K1FieldElement(var1);
   }

   @Override
   public ECFieldElement sqrt() {
      int[] var1 = this.x;
      if (!Nat224.isZero(var1) && !Nat224.isOne(var1)) {
         int[] var2 = Nat224.create();
         SecP224K1Field.square(var1, var2);
         SecP224K1Field.multiply(var2, var1, var2);
         SecP224K1Field.square(var2, var2);
         SecP224K1Field.multiply(var2, var1, var2);
         int[] var4 = Nat224.create();
         SecP224K1Field.square(var2, var4);
         SecP224K1Field.multiply(var4, var1, var4);
         int[] var5 = Nat224.create();
         SecP224K1Field.squareN(var4, 4, var5);
         SecP224K1Field.multiply(var5, var4, var5);
         int[] var6 = Nat224.create();
         SecP224K1Field.squareN(var5, 3, var6);
         SecP224K1Field.multiply(var6, var2, var6);
         SecP224K1Field.squareN(var6, 8, var6);
         SecP224K1Field.multiply(var6, var5, var6);
         SecP224K1Field.squareN(var6, 4, var5);
         SecP224K1Field.multiply(var5, var4, var5);
         SecP224K1Field.squareN(var5, 19, var4);
         SecP224K1Field.multiply(var4, var6, var4);
         int[] var10 = Nat224.create();
         SecP224K1Field.squareN(var4, 42, var10);
         SecP224K1Field.multiply(var10, var4, var10);
         SecP224K1Field.squareN(var10, 23, var4);
         SecP224K1Field.multiply(var4, var5, var4);
         SecP224K1Field.squareN(var4, 84, var5);
         SecP224K1Field.multiply(var5, var10, var5);
         SecP224K1Field.squareN(var5, 20, var5);
         SecP224K1Field.multiply(var5, var6, var5);
         SecP224K1Field.squareN(var5, 3, var5);
         SecP224K1Field.multiply(var5, var1, var5);
         SecP224K1Field.squareN(var5, 2, var5);
         SecP224K1Field.multiply(var5, var1, var5);
         SecP224K1Field.squareN(var5, 4, var5);
         SecP224K1Field.multiply(var5, var2, var5);
         SecP224K1Field.square(var5, var5);
         SecP224K1Field.square(var5, var10);
         if (Nat224.eq(var1, var10)) {
            return new SecP224K1FieldElement(var5);
         } else {
            SecP224K1Field.multiply(var5, PRECOMP_POW2, var5);
            SecP224K1Field.square(var5, var10);
            return Nat224.eq(var1, var10) ? new SecP224K1FieldElement(var5) : null;
         }
      } else {
         return this;
      }
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof SecP224K1FieldElement)) {
         return false;
      } else {
         SecP224K1FieldElement var2 = (SecP224K1FieldElement)var1;
         return Nat224.eq(this.x, var2.x);
      }
   }

   @Override
   public int hashCode() {
      return Q.hashCode() ^ Arrays.hashCode(this.x, 0, 7);
   }
}
