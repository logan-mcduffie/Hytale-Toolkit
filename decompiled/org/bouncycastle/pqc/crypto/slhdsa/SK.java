package org.bouncycastle.pqc.crypto.slhdsa;

class SK {
   final byte[] seed;
   final byte[] prf;

   SK(byte[] var1, byte[] var2) {
      this.seed = var1;
      this.prf = var2;
   }
}
