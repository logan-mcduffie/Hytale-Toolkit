package org.bouncycastle.crypto.prng;

public class ThreadedSeedGenerator {
   public byte[] generateSeed(int var1, boolean var2) {
      ThreadedSeedGenerator.SeedGenerator var3 = new ThreadedSeedGenerator.SeedGenerator();
      return var3.generateSeed(var1, var2);
   }

   private static class SeedGenerator implements Runnable {
      private volatile int counter = 0;
      private volatile boolean stop = false;

      private SeedGenerator() {
      }

      @Override
      public void run() {
         while (!this.stop) {
            this.counter++;
         }
      }

      public byte[] generateSeed(int var1, boolean var2) {
         Thread var3 = new Thread(this);
         byte[] var4 = new byte[var1];
         this.counter = 0;
         this.stop = false;
         int var5 = 0;
         var3.start();
         int var6;
         if (var2) {
            var6 = var1;
         } else {
            var6 = var1 * 8;
         }

         for (int var7 = 0; var7 < var6; var7++) {
            while (this.counter == var5) {
               try {
                  Thread.sleep(1L);
               } catch (InterruptedException var9) {
               }
            }

            var5 = this.counter;
            if (var2) {
               var4[var7] = (byte)(var5 & 0xFF);
            } else {
               int var8 = var7 / 8;
               var4[var8] = (byte)(var4[var8] << 1 | var5 & 1);
            }
         }

         this.stop = true;
         return var4;
      }
   }
}
