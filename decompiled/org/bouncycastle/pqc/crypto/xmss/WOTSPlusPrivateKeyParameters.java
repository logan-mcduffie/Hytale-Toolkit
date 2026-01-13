package org.bouncycastle.pqc.crypto.xmss;

final class WOTSPlusPrivateKeyParameters {
   private final byte[][] privateKey;

   protected WOTSPlusPrivateKeyParameters(WOTSPlusParameters var1, byte[][] var2) {
      if (var1 == null) {
         throw new NullPointerException("params == null");
      } else if (var2 == null) {
         throw new NullPointerException("privateKey == null");
      } else if (XMSSUtil.hasNullPointer(var2)) {
         throw new NullPointerException("privateKey byte array == null");
      } else if (var2.length != var1.getLen()) {
         throw new IllegalArgumentException("wrong privateKey format");
      } else {
         for (int var3 = 0; var3 < var2.length; var3++) {
            if (var2[var3].length != var1.getTreeDigestSize()) {
               throw new IllegalArgumentException("wrong privateKey format");
            }
         }

         this.privateKey = XMSSUtil.cloneArray(var2);
      }
   }

   protected byte[][] toByteArray() {
      return XMSSUtil.cloneArray(this.privateKey);
   }
}
