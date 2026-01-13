package org.bouncycastle.crypto.prng;

public class ReversedWindowGenerator implements RandomGenerator {
   private final RandomGenerator generator;
   private byte[] window;
   private int windowCount;

   public ReversedWindowGenerator(RandomGenerator var1, int var2) {
      if (var1 == null) {
         throw new IllegalArgumentException("generator cannot be null");
      } else if (var2 < 2) {
         throw new IllegalArgumentException("windowSize must be at least 2");
      } else {
         this.generator = var1;
         this.window = new byte[var2];
      }
   }

   @Override
   public void addSeedMaterial(byte[] var1) {
      synchronized (this) {
         this.windowCount = 0;
         this.generator.addSeedMaterial(var1);
      }
   }

   @Override
   public void addSeedMaterial(long var1) {
      synchronized (this) {
         this.windowCount = 0;
         this.generator.addSeedMaterial(var1);
      }
   }

   @Override
   public void nextBytes(byte[] var1) {
      this.doNextBytes(var1, 0, var1.length);
   }

   @Override
   public void nextBytes(byte[] var1, int var2, int var3) {
      this.doNextBytes(var1, var2, var3);
   }

   private void doNextBytes(byte[] var1, int var2, int var3) {
      synchronized (this) {
         int var5 = 0;

         while (var5 < var3) {
            if (this.windowCount < 1) {
               this.generator.nextBytes(this.window, 0, this.window.length);
               this.windowCount = this.window.length;
            }

            var1[var2 + var5++] = this.window[--this.windowCount];
         }
      }
   }
}
