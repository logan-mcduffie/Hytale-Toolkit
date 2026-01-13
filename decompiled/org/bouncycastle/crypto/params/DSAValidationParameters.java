package org.bouncycastle.crypto.params;

import org.bouncycastle.util.Arrays;

public class DSAValidationParameters {
   private int usageIndex;
   private byte[] seed;
   private int counter;

   public DSAValidationParameters(byte[] var1, int var2) {
      this(var1, var2, -1);
   }

   public DSAValidationParameters(byte[] var1, int var2, int var3) {
      this.seed = Arrays.clone(var1);
      this.counter = var2;
      this.usageIndex = var3;
   }

   public int getCounter() {
      return this.counter;
   }

   public byte[] getSeed() {
      return Arrays.clone(this.seed);
   }

   public int getUsageIndex() {
      return this.usageIndex;
   }

   @Override
   public int hashCode() {
      return this.counter ^ Arrays.hashCode(this.seed);
   }

   @Override
   public boolean equals(Object var1) {
      if (!(var1 instanceof DSAValidationParameters)) {
         return false;
      } else {
         DSAValidationParameters var2 = (DSAValidationParameters)var1;
         return var2.counter != this.counter ? false : Arrays.areEqual(this.seed, var2.seed);
      }
   }
}
