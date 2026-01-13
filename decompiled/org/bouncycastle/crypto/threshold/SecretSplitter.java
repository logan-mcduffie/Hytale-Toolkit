package org.bouncycastle.crypto.threshold;

import java.io.IOException;

public interface SecretSplitter {
   SplitSecret split(int var1, int var2);

   SplitSecret splitAround(SecretShare var1, int var2, int var3) throws IOException;

   SplitSecret resplit(byte[] var1, int var2, int var3);
}
