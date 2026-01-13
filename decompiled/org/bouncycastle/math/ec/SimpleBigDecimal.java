package org.bouncycastle.math.ec;

import java.math.BigInteger;

class SimpleBigDecimal {
   private static final long serialVersionUID = 1L;
   private final BigInteger bigInt;
   private final int scale;

   public static SimpleBigDecimal getInstance(BigInteger var0, int var1) {
      return new SimpleBigDecimal(var0.shiftLeft(var1), var1);
   }

   public SimpleBigDecimal(BigInteger var1, int var2) {
      if (var2 < 0) {
         throw new IllegalArgumentException("scale may not be negative");
      } else {
         this.bigInt = var1;
         this.scale = var2;
      }
   }

   private void checkScale(SimpleBigDecimal var1) {
      if (this.scale != var1.scale) {
         throw new IllegalArgumentException("Only SimpleBigDecimal of same scale allowed in arithmetic operations");
      }
   }

   public SimpleBigDecimal adjustScale(int var1) {
      if (var1 < 0) {
         throw new IllegalArgumentException("scale may not be negative");
      } else {
         return var1 == this.scale ? this : new SimpleBigDecimal(this.bigInt.shiftLeft(var1 - this.scale), var1);
      }
   }

   public SimpleBigDecimal add(SimpleBigDecimal var1) {
      this.checkScale(var1);
      return new SimpleBigDecimal(this.bigInt.add(var1.bigInt), this.scale);
   }

   public SimpleBigDecimal add(BigInteger var1) {
      return new SimpleBigDecimal(this.bigInt.add(var1.shiftLeft(this.scale)), this.scale);
   }

   public SimpleBigDecimal negate() {
      return new SimpleBigDecimal(this.bigInt.negate(), this.scale);
   }

   public SimpleBigDecimal subtract(SimpleBigDecimal var1) {
      return this.add(var1.negate());
   }

   public SimpleBigDecimal subtract(BigInteger var1) {
      return new SimpleBigDecimal(this.bigInt.subtract(var1.shiftLeft(this.scale)), this.scale);
   }

   public SimpleBigDecimal multiply(SimpleBigDecimal var1) {
      this.checkScale(var1);
      return new SimpleBigDecimal(this.bigInt.multiply(var1.bigInt), this.scale + this.scale);
   }

   public SimpleBigDecimal multiply(BigInteger var1) {
      return new SimpleBigDecimal(this.bigInt.multiply(var1), this.scale);
   }

   public SimpleBigDecimal divide(SimpleBigDecimal var1) {
      this.checkScale(var1);
      BigInteger var2 = this.bigInt.shiftLeft(this.scale);
      return new SimpleBigDecimal(var2.divide(var1.bigInt), this.scale);
   }

   public SimpleBigDecimal divide(BigInteger var1) {
      return new SimpleBigDecimal(this.bigInt.divide(var1), this.scale);
   }

   public SimpleBigDecimal shiftLeft(int var1) {
      return new SimpleBigDecimal(this.bigInt.shiftLeft(var1), this.scale);
   }

   public int compareTo(SimpleBigDecimal var1) {
      this.checkScale(var1);
      return this.bigInt.compareTo(var1.bigInt);
   }

   public int compareTo(BigInteger var1) {
      return this.bigInt.compareTo(var1.shiftLeft(this.scale));
   }

   public BigInteger floor() {
      return this.bigInt.shiftRight(this.scale);
   }

   public BigInteger round() {
      SimpleBigDecimal var1 = new SimpleBigDecimal(ECConstants.ONE, 1);
      return this.add(var1.adjustScale(this.scale)).floor();
   }

   public int intValue() {
      return this.floor().intValue();
   }

   public long longValue() {
      return this.floor().longValue();
   }

   public int getScale() {
      return this.scale;
   }

   @Override
   public String toString() {
      if (this.scale == 0) {
         return this.bigInt.toString();
      } else {
         BigInteger var1 = this.floor();
         BigInteger var2 = this.bigInt.subtract(var1.shiftLeft(this.scale));
         if (this.bigInt.signum() == -1) {
            var2 = ECConstants.ONE.shiftLeft(this.scale).subtract(var2);
         }

         if (var1.signum() == -1 && !var2.equals(ECConstants.ZERO)) {
            var1 = var1.add(ECConstants.ONE);
         }

         String var3 = var1.toString();
         char[] var4 = new char[this.scale];
         String var5 = var2.toString(2);
         int var6 = var5.length();
         int var7 = this.scale - var6;

         for (int var8 = 0; var8 < var7; var8++) {
            var4[var8] = '0';
         }

         for (int var10 = 0; var10 < var6; var10++) {
            var4[var7 + var10] = var5.charAt(var10);
         }

         String var11 = new String(var4);
         StringBuilder var9 = new StringBuilder(var3);
         var9.append(".");
         var9.append(var11);
         return var9.toString();
      }
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof SimpleBigDecimal)) {
         return false;
      } else {
         SimpleBigDecimal var2 = (SimpleBigDecimal)var1;
         return this.bigInt.equals(var2.bigInt) && this.scale == var2.scale;
      }
   }

   @Override
   public int hashCode() {
      return this.bigInt.hashCode() ^ this.scale;
   }
}
