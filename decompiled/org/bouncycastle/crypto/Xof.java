package org.bouncycastle.crypto;

public interface Xof extends ExtendedDigest {
   int doFinal(byte[] var1, int var2, int var3);

   int doOutput(byte[] var1, int var2, int var3);
}
