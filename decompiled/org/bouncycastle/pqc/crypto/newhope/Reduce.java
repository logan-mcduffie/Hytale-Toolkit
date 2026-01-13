package org.bouncycastle.pqc.crypto.newhope;

class Reduce {
   static final int QInv = 12287;
   static final int RLog = 18;
   static final int RMask = 262143;

   static short montgomery(int var0) {
      int var1 = var0 * 12287;
      var1 &= 262143;
      var1 *= 12289;
      var1 += var0;
      return (short)(var1 >>> 18);
   }

   static short barrett(short var0) {
      int var1 = var0 & '\uffff';
      int var2 = var1 * 5 >>> 16;
      var2 *= 12289;
      return (short)(var1 - var2);
   }
}
