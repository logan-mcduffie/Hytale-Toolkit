package org.bouncycastle.pqc.crypto.picnic;

class View {
   final int[] inputShare;
   final byte[] communicatedBits;
   final int[] outputShare;

   public View(PicnicEngine var1) {
      this.inputShare = new int[var1.stateSizeWords];
      this.communicatedBits = new byte[var1.andSizeBytes];
      this.outputShare = new int[var1.stateSizeWords];
   }
}
