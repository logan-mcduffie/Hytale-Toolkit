package org.bouncycastle.pqc.crypto.xmss;

final class WOTSPlusSignature {
   private byte[][] signature;

   protected WOTSPlusSignature(WOTSPlusParameters var1, byte[][] var2) {
      if (var1 == null) {
         throw new NullPointerException("params == null");
      } else if (var2 == null) {
         throw new NullPointerException("signature == null");
      } else if (XMSSUtil.hasNullPointer(var2)) {
         throw new NullPointerException("signature byte array == null");
      } else if (var2.length != var1.getLen()) {
         throw new IllegalArgumentException("wrong signature size");
      } else {
         for (int var3 = 0; var3 < var2.length; var3++) {
            if (var2[var3].length != var1.getTreeDigestSize()) {
               throw new IllegalArgumentException("wrong signature format");
            }
         }

         this.signature = XMSSUtil.cloneArray(var2);
      }
   }

   public byte[][] toByteArray() {
      return XMSSUtil.cloneArray(this.signature);
   }
}
