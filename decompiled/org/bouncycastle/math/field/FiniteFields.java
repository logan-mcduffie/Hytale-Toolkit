package org.bouncycastle.math.field;

import java.math.BigInteger;
import org.bouncycastle.util.BigIntegers;

public abstract class FiniteFields {
   static final FiniteField GF_2 = new PrimeField(BigInteger.valueOf(2L));
   static final FiniteField GF_3 = new PrimeField(BigInteger.valueOf(3L));

   public static PolynomialExtensionField getBinaryExtensionField(int[] var0) {
      if (var0[0] != 0) {
         throw new IllegalArgumentException("Irreducible polynomials in GF(2) must have constant term");
      } else {
         for (int var1 = 1; var1 < var0.length; var1++) {
            if (var0[var1] <= var0[var1 - 1]) {
               throw new IllegalArgumentException("Polynomial exponents must be monotonically increasing");
            }
         }

         return new GenericPolynomialExtensionField(GF_2, new GF2Polynomial(var0));
      }
   }

   public static FiniteField getPrimeField(BigInteger var0) {
      int var1 = var0.bitLength();
      if (var0.signum() <= 0 || var1 < 2) {
         throw new IllegalArgumentException("'characteristic' must be >= 2");
      } else {
         if (var1 < 3) {
            switch (BigIntegers.intValueExact(var0)) {
               case 2:
                  return GF_2;
               case 3:
                  return GF_3;
            }
         }

         return new PrimeField(var0);
      }
   }
}
