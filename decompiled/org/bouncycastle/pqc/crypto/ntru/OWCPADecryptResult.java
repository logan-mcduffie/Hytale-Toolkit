package org.bouncycastle.pqc.crypto.ntru;

class OWCPADecryptResult {
   final byte[] rm;
   final int fail;

   public OWCPADecryptResult(byte[] var1, int var2) {
      this.rm = var1;
      this.fail = var2;
   }
}
