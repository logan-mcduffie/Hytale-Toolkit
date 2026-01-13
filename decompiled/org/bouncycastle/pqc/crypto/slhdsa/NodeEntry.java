package org.bouncycastle.pqc.crypto.slhdsa;

class NodeEntry {
   final byte[] nodeValue;
   final int nodeHeight;

   NodeEntry(byte[] var1, int var2) {
      this.nodeValue = var1;
      this.nodeHeight = var2;
   }
}
