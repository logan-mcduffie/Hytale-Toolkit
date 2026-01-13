package org.bouncycastle.tsp.ers;

import org.bouncycastle.operator.DigestCalculator;

public class ERSByteData extends ERSCachingData {
   private final byte[] content;

   public ERSByteData(byte[] var1) {
      this.content = var1;
   }

   @Override
   protected byte[] calculateHash(DigestCalculator var1, byte[] var2) {
      byte[] var3 = ERSUtil.calculateDigest(var1, this.content);
      return var2 != null ? ERSUtil.concatPreviousHashes(var1, var2, var3) : var3;
   }
}
