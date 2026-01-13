package org.bouncycastle.math.ec.custom.sec;

import java.math.BigInteger;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.raw.Mod;
import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.math.raw.Nat224;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.encoders.Hex;

public class SecP224R1FieldElement extends ECFieldElement.AbstractFp {
   public static final BigInteger Q = new BigInteger(1, Hex.decodeStrict("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF000000000000000000000001"));
   protected int[] x;

   public SecP224R1FieldElement(BigInteger var1) {
      if (var1 != null && var1.signum() >= 0 && var1.compareTo(Q) < 0) {
         this.x = SecP224R1Field.fromBigInteger(var1);
      } else {
         throw new IllegalArgumentException("x value invalid for SecP224R1FieldElement");
      }
   }

   public SecP224R1FieldElement() {
      this.x = Nat224.create();
   }

   protected SecP224R1FieldElement(int[] var1) {
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
      return "SecP224R1Field";
   }

   @Override
   public int getFieldSize() {
      return Q.bitLength();
   }

   @Override
   public ECFieldElement add(ECFieldElement var1) {
      int[] var2 = Nat224.create();
      SecP224R1Field.add(this.x, ((SecP224R1FieldElement)var1).x, var2);
      return new SecP224R1FieldElement(var2);
   }

   @Override
   public ECFieldElement addOne() {
      int[] var1 = Nat224.create();
      SecP224R1Field.addOne(this.x, var1);
      return new SecP224R1FieldElement(var1);
   }

   @Override
   public ECFieldElement subtract(ECFieldElement var1) {
      int[] var2 = Nat224.create();
      SecP224R1Field.subtract(this.x, ((SecP224R1FieldElement)var1).x, var2);
      return new SecP224R1FieldElement(var2);
   }

   @Override
   public ECFieldElement multiply(ECFieldElement var1) {
      int[] var2 = Nat224.create();
      SecP224R1Field.multiply(this.x, ((SecP224R1FieldElement)var1).x, var2);
      return new SecP224R1FieldElement(var2);
   }

   @Override
   public ECFieldElement divide(ECFieldElement var1) {
      int[] var2 = Nat224.create();
      SecP224R1Field.inv(((SecP224R1FieldElement)var1).x, var2);
      SecP224R1Field.multiply(var2, this.x, var2);
      return new SecP224R1FieldElement(var2);
   }

   @Override
   public ECFieldElement negate() {
      int[] var1 = Nat224.create();
      SecP224R1Field.negate(this.x, var1);
      return new SecP224R1FieldElement(var1);
   }

   @Override
   public ECFieldElement square() {
      int[] var1 = Nat224.create();
      SecP224R1Field.square(this.x, var1);
      return new SecP224R1FieldElement(var1);
   }

   @Override
   public ECFieldElement invert() {
      int[] var1 = Nat224.create();
      SecP224R1Field.inv(this.x, var1);
      return new SecP224R1FieldElement(var1);
   }

   @Override
   public ECFieldElement sqrt() {
      int[] var1 = this.x;
      if (!Nat224.isZero(var1) && !Nat224.isOne(var1)) {
         int[] var2 = Nat224.create();
         SecP224R1Field.negate(var1, var2);
         int[] var3 = Mod.random(SecP224R1Field.P);
         int[] var4 = Nat224.create();
         if (!isSquare(var1)) {
            return null;
         } else {
            while (!trySqrt(var2, var3, var4)) {
               SecP224R1Field.addOne(var3, var3);
            }

            SecP224R1Field.square(var4, var3);
            return Nat224.eq(var1, var3) ? new SecP224R1FieldElement(var4) : null;
         }
      } else {
         return this;
      }
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof SecP224R1FieldElement)) {
         return false;
      } else {
         SecP224R1FieldElement var2 = (SecP224R1FieldElement)var1;
         return Nat224.eq(this.x, var2.x);
      }
   }

   @Override
   public int hashCode() {
      return Q.hashCode() ^ Arrays.hashCode(this.x, 0, 7);
   }

   private static boolean isSquare(int[] var0) {
      int[] var1 = Nat224.create();
      int[] var2 = Nat224.create();
      Nat224.copy(var0, var1);

      for (int var3 = 0; var3 < 7; var3++) {
         Nat224.copy(var1, var2);
         SecP224R1Field.squareN(var1, 1 << var3, var1);
         SecP224R1Field.multiply(var1, var2, var1);
      }

      SecP224R1Field.squareN(var1, 95, var1);
      return Nat224.isOne(var1);
   }

   private static void RM(int[] var0, int[] var1, int[] var2, int[] var3, int[] var4, int[] var5, int[] var6) {
      SecP224R1Field.multiply(var4, var2, var6);
      SecP224R1Field.multiply(var6, var0, var6);
      SecP224R1Field.multiply(var3, var1, var5);
      SecP224R1Field.add(var5, var6, var5);
      SecP224R1Field.multiply(var3, var2, var6);
      Nat224.copy(var5, var3);
      SecP224R1Field.multiply(var4, var1, var4);
      SecP224R1Field.add(var4, var6, var4);
      SecP224R1Field.square(var4, var5);
      SecP224R1Field.multiply(var5, var0, var5);
   }

   private static void RP(int[] var0, int[] var1, int[] var2, int[] var3, int[] var4) {
      Nat224.copy(var0, var3);
      int[] var5 = Nat224.create();
      int[] var6 = Nat224.create();

      for (int var7 = 0; var7 < 7; var7++) {
         Nat224.copy(var1, var5);
         Nat224.copy(var2, var6);
         int var8 = 1 << var7;

         while (--var8 >= 0) {
            RS(var1, var2, var3, var4);
         }

         RM(var0, var5, var6, var1, var2, var3, var4);
      }
   }

   private static void RS(int[] var0, int[] var1, int[] var2, int[] var3) {
      SecP224R1Field.multiply(var1, var0, var1);
      SecP224R1Field.twice(var1, var1);
      SecP224R1Field.square(var0, var3);
      SecP224R1Field.add(var2, var3, var0);
      SecP224R1Field.multiply(var2, var3, var2);
      int var4 = Nat.shiftUpBits(7, var2, 2, 0);
      SecP224R1Field.reduce32(var4, var2);
   }

   private static boolean trySqrt(int[] var0, int[] var1, int[] var2) {
      int[] var3 = Nat224.create();
      Nat224.copy(var1, var3);
      int[] var4 = Nat224.create();
      var4[0] = 1;
      int[] var5 = Nat224.create();
      RP(var0, var3, var4, var5, var2);
      int[] var6 = Nat224.create();
      int[] var7 = Nat224.create();

      for (int var8 = 1; var8 < 96; var8++) {
         Nat224.copy(var3, var6);
         Nat224.copy(var4, var7);
         RS(var3, var4, var5, var2);
         if (Nat224.isZero(var3)) {
            SecP224R1Field.inv(var7, var2);
            SecP224R1Field.multiply(var2, var6, var2);
            return true;
         }
      }

      return false;
   }
}
