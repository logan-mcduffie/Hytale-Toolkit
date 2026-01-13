package org.bouncycastle.crypto.params;

import org.bouncycastle.crypto.DerivationParameters;
import org.bouncycastle.util.Arrays;

public final class KDFCounterParameters implements DerivationParameters {
   private byte[] ki;
   private byte[] fixedInputDataCounterPrefix;
   private byte[] fixedInputDataCounterSuffix;
   private int r;

   public KDFCounterParameters(byte[] var1, byte[] var2, int var3) {
      this(var1, null, var2, var3);
   }

   public KDFCounterParameters(byte[] var1, byte[] var2, byte[] var3, int var4) {
      if (var1 == null) {
         throw new IllegalArgumentException("A KDF requires Ki (a seed) as input");
      } else {
         this.ki = Arrays.clone(var1);
         if (var2 == null) {
            this.fixedInputDataCounterPrefix = new byte[0];
         } else {
            this.fixedInputDataCounterPrefix = Arrays.clone(var2);
         }

         if (var3 == null) {
            this.fixedInputDataCounterSuffix = new byte[0];
         } else {
            this.fixedInputDataCounterSuffix = Arrays.clone(var3);
         }

         if (var4 != 8 && var4 != 16 && var4 != 24 && var4 != 32) {
            throw new IllegalArgumentException("Length of counter should be 8, 16, 24 or 32");
         } else {
            this.r = var4;
         }
      }
   }

   public byte[] getKI() {
      return this.ki;
   }

   public byte[] getFixedInputData() {
      return Arrays.clone(this.fixedInputDataCounterSuffix);
   }

   public byte[] getFixedInputDataCounterPrefix() {
      return Arrays.clone(this.fixedInputDataCounterPrefix);
   }

   public byte[] getFixedInputDataCounterSuffix() {
      return Arrays.clone(this.fixedInputDataCounterSuffix);
   }

   public int getR() {
      return this.r;
   }
}
