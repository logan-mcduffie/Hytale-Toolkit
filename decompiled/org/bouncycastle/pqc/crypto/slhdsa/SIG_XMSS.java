package org.bouncycastle.pqc.crypto.slhdsa;

class SIG_XMSS {
   final byte[] sig;
   final byte[][] auth;

   public SIG_XMSS(byte[] var1, byte[][] var2) {
      this.sig = var1;
      this.auth = var2;
   }

   public byte[] getWOTSSig() {
      return this.sig;
   }

   public byte[][] getXMSSAUTH() {
      return this.auth;
   }
}
