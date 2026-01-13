package org.bouncycastle.crypto.params;

import java.math.BigInteger;
import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.util.Integers;

public class DHPublicKeyParameters extends DHKeyParameters {
   private static final BigInteger ONE = BigInteger.valueOf(1L);
   private static final BigInteger TWO = BigInteger.valueOf(2L);
   private BigInteger y;

   public DHPublicKeyParameters(BigInteger var1, DHParameters var2) {
      super(false, var2);
      this.y = this.validate(var1, var2);
   }

   private BigInteger validate(BigInteger var1, DHParameters var2) {
      if (var1 == null) {
         throw new NullPointerException("y value cannot be null");
      } else {
         BigInteger var3 = var2.getP();
         if (var1.compareTo(TWO) >= 0 && var1.compareTo(var3.subtract(TWO)) <= 0) {
            BigInteger var4 = var2.getQ();
            if (var4 == null) {
               return var1;
            } else {
               if (var3.testBit(0) && var3.bitLength() - 1 == var4.bitLength() && var3.shiftRight(1).equals(var4)) {
                  if (1 == legendre(var1, var3)) {
                     return var1;
                  }
               } else if (ONE.equals(var1.modPow(var4, var3))) {
                  return var1;
               }

               throw new IllegalArgumentException("Y value does not appear to be in correct group");
            }
         } else {
            throw new IllegalArgumentException("invalid DH public key");
         }
      }
   }

   public BigInteger getY() {
      return this.y;
   }

   @Override
   public int hashCode() {
      return this.y.hashCode() ^ super.hashCode();
   }

   @Override
   public boolean equals(Object var1) {
      if (!(var1 instanceof DHPublicKeyParameters)) {
         return false;
      } else {
         DHPublicKeyParameters var2 = (DHPublicKeyParameters)var1;
         return var2.getY().equals(this.y) && super.equals(var1);
      }
   }

   private static int legendre(BigInteger var0, BigInteger var1) {
      int var2 = var1.bitLength();
      int[] var3 = Nat.fromBigInteger(var2, var0);
      int[] var4 = Nat.fromBigInteger(var2, var1);
      int var5 = 0;
      int var6 = var4.length;

      while (true) {
         while (var3[0] != 0) {
            int var7 = Integers.numberOfTrailingZeros(var3[0]);
            if (var7 > 0) {
               Nat.shiftDownBits(var6, var3, var7, 0);
               int var8 = var4[0];
               var5 ^= (var8 ^ var8 >>> 1) & var7 << 1;
            }

            int var10 = Nat.compare(var6, var3, var4);
            if (var10 == 0) {
               return Nat.isOne(var6, var4) ? 1 - (var5 & 2) : 0;
            }

            if (var10 < 0) {
               var5 ^= var3[0] & var4[0];
               int[] var9 = var3;
               var3 = var4;
               var4 = var9;
            }

            while (var3[var6 - 1] == 0) {
               var6--;
            }

            Nat.sub(var6, var3, var4, var3);
         }

         Nat.shiftDownWord(var6, var3, 0);
      }
   }
}
