package org.bouncycastle.crypto.agreement.jpake;

import java.math.BigInteger;

public class JPAKEPrimeOrderGroup {
   private final BigInteger p;
   private final BigInteger q;
   private final BigInteger g;

   public JPAKEPrimeOrderGroup(BigInteger var1, BigInteger var2, BigInteger var3) {
      this(var1, var2, var3, false);
   }

   JPAKEPrimeOrderGroup(BigInteger var1, BigInteger var2, BigInteger var3, boolean var4) {
      JPAKEUtil.validateNotNull(var1, "p");
      JPAKEUtil.validateNotNull(var2, "q");
      JPAKEUtil.validateNotNull(var3, "g");
      if (!var4) {
         if (!var1.subtract(JPAKEUtil.ONE).mod(var2).equals(JPAKEUtil.ZERO)) {
            throw new IllegalArgumentException("p-1 must be evenly divisible by q");
         }

         if (var3.compareTo(BigInteger.valueOf(2L)) < 0 || var3.compareTo(var1) >= 0) {
            throw new IllegalArgumentException("g must be in [2, p-1]");
         }

         if (!var3.modPow(var2, var1).equals(JPAKEUtil.ONE)) {
            throw new IllegalArgumentException("g^q mod p must equal 1");
         }

         if (!var1.isProbablePrime(20)) {
            throw new IllegalArgumentException("p must be prime");
         }

         if (!var2.isProbablePrime(20)) {
            throw new IllegalArgumentException("q must be prime");
         }
      }

      this.p = var1;
      this.q = var2;
      this.g = var3;
   }

   public BigInteger getP() {
      return this.p;
   }

   public BigInteger getQ() {
      return this.q;
   }

   public BigInteger getG() {
      return this.g;
   }
}
