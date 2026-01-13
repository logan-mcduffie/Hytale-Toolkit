package org.bouncycastle.crypto.prng;

public interface RandomGenerator {
   void addSeedMaterial(byte[] var1);

   void addSeedMaterial(long var1);

   void nextBytes(byte[] var1);

   void nextBytes(byte[] var1, int var2, int var3);
}
