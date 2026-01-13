package org.bouncycastle.crypto.params;

import org.bouncycastle.crypto.DerivationParameters;
import org.bouncycastle.util.Arrays;

public final class KDFDoublePipelineIterationParameters implements DerivationParameters {
   private static final int UNUSED_R = 32;
   private final byte[] ki;
   private final boolean useCounter;
   private final int r;
   private final byte[] fixedInputData;

   private KDFDoublePipelineIterationParameters(byte[] var1, byte[] var2, int var3, boolean var4) {
      if (var1 == null) {
         throw new IllegalArgumentException("A KDF requires Ki (a seed) as input");
      } else {
         this.ki = Arrays.clone(var1);
         if (var2 == null) {
            this.fixedInputData = new byte[0];
         } else {
            this.fixedInputData = Arrays.clone(var2);
         }

         if (var3 != 8 && var3 != 16 && var3 != 24 && var3 != 32) {
            throw new IllegalArgumentException("Length of counter should be 8, 16, 24 or 32");
         } else {
            this.r = var3;
            this.useCounter = var4;
         }
      }
   }

   public static KDFDoublePipelineIterationParameters createWithCounter(byte[] var0, byte[] var1, int var2) {
      return new KDFDoublePipelineIterationParameters(var0, var1, var2, true);
   }

   public static KDFDoublePipelineIterationParameters createWithoutCounter(byte[] var0, byte[] var1) {
      return new KDFDoublePipelineIterationParameters(var0, var1, 32, false);
   }

   public byte[] getKI() {
      return this.ki;
   }

   public boolean useCounter() {
      return this.useCounter;
   }

   public int getR() {
      return this.r;
   }

   public byte[] getFixedInputData() {
      return Arrays.clone(this.fixedInputData);
   }
}
