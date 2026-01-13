package org.bouncycastle.pqc.crypto.slhdsa;

class PK {
   final byte[] seed;
   final byte[] root;

   PK(byte[] var1, byte[] var2) {
      this.seed = var1;
      this.root = var2;
   }
}
