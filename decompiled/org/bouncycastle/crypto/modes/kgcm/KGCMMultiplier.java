package org.bouncycastle.crypto.modes.kgcm;

public interface KGCMMultiplier {
   void init(long[] var1);

   void multiplyH(long[] var1);
}
