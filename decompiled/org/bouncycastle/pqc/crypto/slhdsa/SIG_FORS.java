package org.bouncycastle.pqc.crypto.slhdsa;

class SIG_FORS {
   final byte[][] authPath;
   final byte[] sk;

   SIG_FORS(byte[] var1, byte[][] var2) {
      this.authPath = var2;
      this.sk = var1;
   }

   byte[] getSK() {
      return this.sk;
   }

   public byte[][] getAuthPath() {
      return this.authPath;
   }
}
