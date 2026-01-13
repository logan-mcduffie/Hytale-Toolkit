package org.bouncycastle.crypto.modes;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.MultiBlockCipher;

public interface CBCModeCipher extends MultiBlockCipher {
   BlockCipher getUnderlyingCipher();
}
