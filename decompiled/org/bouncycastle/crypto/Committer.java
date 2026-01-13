package org.bouncycastle.crypto;

public interface Committer {
   Commitment commit(byte[] var1);

   boolean isRevealed(Commitment var1, byte[] var2);
}
