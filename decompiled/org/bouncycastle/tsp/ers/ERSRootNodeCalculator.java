package org.bouncycastle.tsp.ers;

import org.bouncycastle.asn1.tsp.PartialHashtree;
import org.bouncycastle.operator.DigestCalculator;

public interface ERSRootNodeCalculator {
   byte[] computeRootHash(DigestCalculator var1, PartialHashtree[] var2);

   PartialHashtree[] computePathToRoot(DigestCalculator var1, PartialHashtree var2, int var3);

   byte[] recoverRootHash(DigestCalculator var1, PartialHashtree[] var2);
}
