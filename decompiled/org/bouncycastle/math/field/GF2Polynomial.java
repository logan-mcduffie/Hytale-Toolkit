package org.bouncycastle.math.field;

import org.bouncycastle.util.Arrays;

class GF2Polynomial implements Polynomial {
   protected final int[] exponents;

   GF2Polynomial(int[] var1) {
      this.exponents = Arrays.clone(var1);
   }

   @Override
   public int getDegree() {
      return this.exponents[this.exponents.length - 1];
   }

   @Override
   public int[] getExponentsPresent() {
      return Arrays.clone(this.exponents);
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof GF2Polynomial)) {
         return false;
      } else {
         GF2Polynomial var2 = (GF2Polynomial)var1;
         return Arrays.areEqual(this.exponents, var2.exponents);
      }
   }

   @Override
   public int hashCode() {
      return Arrays.hashCode(this.exponents);
   }
}
