package org.bouncycastle.crypto.prng;

import org.bouncycastle.crypto.Digest;
import org.bouncycastle.util.Arrays;

public class DigestRandomGenerator implements RandomGenerator {
   private static long CYCLE_COUNT = 10L;
   private long stateCounter;
   private long seedCounter;
   private Digest digest;
   private byte[] state;
   private byte[] seed;

   public DigestRandomGenerator(Digest var1) {
      this.digest = var1;
      this.seed = new byte[var1.getDigestSize()];
      this.seedCounter = 1L;
      this.state = new byte[var1.getDigestSize()];
      this.stateCounter = 1L;
   }

   @Override
   public void addSeedMaterial(byte[] var1) {
      synchronized (this) {
         if (!Arrays.isNullOrEmpty(var1)) {
            this.digestUpdate(var1);
         }

         this.digestUpdate(this.seed);
         this.digestDoFinal(this.seed);
      }
   }

   @Override
   public void addSeedMaterial(long var1) {
      synchronized (this) {
         this.digestAddCounter(var1);
         this.digestUpdate(this.seed);
         this.digestDoFinal(this.seed);
      }
   }

   @Override
   public void nextBytes(byte[] var1) {
      this.nextBytes(var1, 0, var1.length);
   }

   @Override
   public void nextBytes(byte[] var1, int var2, int var3) {
      synchronized (this) {
         int var5 = 0;
         this.generateState();
         int var6 = var2 + var3;

         for (int var7 = var2; var7 != var6; var7++) {
            if (var5 == this.state.length) {
               this.generateState();
               var5 = 0;
            }

            var1[var7] = this.state[var5++];
         }
      }
   }

   private void cycleSeed() {
      this.digestUpdate(this.seed);
      this.digestAddCounter(this.seedCounter++);
      this.digestDoFinal(this.seed);
   }

   private void generateState() {
      this.digestAddCounter(this.stateCounter++);
      this.digestUpdate(this.state);
      this.digestUpdate(this.seed);
      this.digestDoFinal(this.state);
      if (this.stateCounter % CYCLE_COUNT == 0L) {
         this.cycleSeed();
      }
   }

   private void digestAddCounter(long var1) {
      for (int var3 = 0; var3 != 8; var3++) {
         this.digest.update((byte)var1);
         var1 >>>= 8;
      }
   }

   private void digestUpdate(byte[] var1) {
      this.digest.update(var1, 0, var1.length);
   }

   private void digestDoFinal(byte[] var1) {
      this.digest.doFinal(var1, 0);
   }
}
