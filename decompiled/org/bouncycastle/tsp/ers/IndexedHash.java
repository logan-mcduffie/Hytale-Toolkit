package org.bouncycastle.tsp.ers;

class IndexedHash {
   final int order;
   final byte[] digest;

   IndexedHash(int var1, byte[] var2) {
      this.order = var1;
      this.digest = var2;
   }
}
