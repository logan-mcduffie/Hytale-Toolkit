package org.bouncycastle.pqc.crypto.picnic;

class Msg {
   byte[][] msgs;
   int pos;
   int unopened;

   public Msg(PicnicEngine var1) {
      this.msgs = new byte[var1.numMPCParties][var1.andSizeBytes];
      this.pos = 0;
      this.unopened = -1;
   }
}
