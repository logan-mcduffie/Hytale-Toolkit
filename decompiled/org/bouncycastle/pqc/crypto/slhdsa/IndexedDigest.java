package org.bouncycastle.pqc.crypto.slhdsa;

class IndexedDigest {
   final long idx_tree;
   final int idx_leaf;
   final byte[] digest;

   IndexedDigest(long var1, int var3, byte[] var4) {
      this.idx_tree = var1;
      this.idx_leaf = var3;
      this.digest = var4;
   }
}
