package org.bouncycastle.tsp.ers;

import org.bouncycastle.operator.DigestCalculator;

public interface ERSData {
   byte[] getHash(DigestCalculator var1, byte[] var2);
}
