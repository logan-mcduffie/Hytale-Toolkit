package org.bouncycastle.crypto.params;

import org.bouncycastle.crypto.DerivationParameters;
import org.bouncycastle.util.Arrays;

public final class KDFFeedbackParameters implements DerivationParameters {
   private static final int UNUSED_R = -1;
   private final byte[] ki;
   private final byte[] iv;
   private final boolean useCounter;
   private final int r;
   private final byte[] fixedInputData;

   private KDFFeedbackParameters(byte[] var1, byte[] var2, byte[] var3, int var4, boolean var5) {
      if (var1 == null) {
         throw new IllegalArgumentException("A KDF requires Ki (a seed) as input");
      } else {
         this.ki = Arrays.clone(var1);
         if (var3 == null) {
            this.fixedInputData = new byte[0];
         } else {
            this.fixedInputData = Arrays.clone(var3);
         }

         this.r = var4;
         if (var2 == null) {
            this.iv = new byte[0];
         } else {
            this.iv = Arrays.clone(var2);
         }

         this.useCounter = var5;
      }
   }

   public static KDFFeedbackParameters createWithCounter(byte[] var0, byte[] var1, byte[] var2, int var3) {
      if (var3 != 8 && var3 != 16 && var3 != 24 && var3 != 32) {
         throw new IllegalArgumentException("Length of counter should be 8, 16, 24 or 32");
      } else {
         return new KDFFeedbackParameters(var0, var1, var2, var3, true);
      }
   }

   public static KDFFeedbackParameters createWithoutCounter(byte[] var0, byte[] var1, byte[] var2) {
      return new KDFFeedbackParameters(var0, var1, var2, -1, false);
   }

   public byte[] getKI() {
      return this.ki;
   }

   public byte[] getIV() {
      return this.iv;
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
