package org.bouncycastle.crypto.util;

import org.bouncycastle.internal.asn1.misc.MiscObjectIdentifiers;

public class ScryptConfig extends PBKDFConfig {
   private final int costParameter;
   private final int blockSize;
   private final int parallelizationParameter;
   private final int saltLength;

   private ScryptConfig(ScryptConfig.Builder var1) {
      super(MiscObjectIdentifiers.id_scrypt);
      this.costParameter = var1.costParameter;
      this.blockSize = var1.blockSize;
      this.parallelizationParameter = var1.parallelizationParameter;
      this.saltLength = var1.saltLength;
   }

   public int getCostParameter() {
      return this.costParameter;
   }

   public int getBlockSize() {
      return this.blockSize;
   }

   public int getParallelizationParameter() {
      return this.parallelizationParameter;
   }

   public int getSaltLength() {
      return this.saltLength;
   }

   public static class Builder {
      private final int costParameter;
      private final int blockSize;
      private final int parallelizationParameter;
      private int saltLength = 16;

      public Builder(int var1, int var2, int var3) {
         if (var1 > 1 && isPowerOf2(var1)) {
            this.costParameter = var1;
            this.blockSize = var2;
            this.parallelizationParameter = var3;
         } else {
            throw new IllegalArgumentException("Cost parameter N must be > 1 and a power of 2");
         }
      }

      public ScryptConfig.Builder withSaltLength(int var1) {
         this.saltLength = var1;
         return this;
      }

      public ScryptConfig build() {
         return new ScryptConfig(this);
      }

      private static boolean isPowerOf2(int var0) {
         return (var0 & var0 - 1) == 0;
      }
   }
}
