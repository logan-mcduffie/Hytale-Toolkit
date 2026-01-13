package org.bouncycastle.crypto;

import org.bouncycastle.crypto.digests.EncodableDigest;
import org.bouncycastle.util.Memoable;

public interface SavableDigest extends ExtendedDigest, EncodableDigest, Memoable {
}
