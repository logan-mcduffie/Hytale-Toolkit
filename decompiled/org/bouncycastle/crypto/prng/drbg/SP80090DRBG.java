package org.bouncycastle.crypto.prng.drbg;

public interface SP80090DRBG {
   int getBlockSize();

   int generate(byte[] var1, byte[] var2, boolean var3);

   void reseed(byte[] var1);
}
