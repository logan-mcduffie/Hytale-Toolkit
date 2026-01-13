package org.bouncycastle.crypto;

public interface SkippingCipher {
   long skip(long var1);

   long seekTo(long var1);

   long getPosition();
}
