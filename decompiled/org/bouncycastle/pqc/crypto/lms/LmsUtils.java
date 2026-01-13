package org.bouncycastle.pqc.crypto.lms;

import org.bouncycastle.crypto.Digest;

class LmsUtils {
   static void u32str(int var0, Digest var1) {
      var1.update((byte)(var0 >>> 24));
      var1.update((byte)(var0 >>> 16));
      var1.update((byte)(var0 >>> 8));
      var1.update((byte)var0);
   }

   static void u16str(short var0, Digest var1) {
      var1.update((byte)(var0 >>> 8));
      var1.update((byte)var0);
   }

   static void byteArray(byte[] var0, Digest var1) {
      var1.update(var0, 0, var0.length);
   }

   static void byteArray(byte[] var0, int var1, int var2, Digest var3) {
      var3.update(var0, var1, var2);
   }

   static int calculateStrength(LMSParameters var0) {
      if (var0 == null) {
         throw new NullPointerException("lmsParameters cannot be null");
      } else {
         LMSigParameters var1 = var0.getLMSigParam();
         return (1 << var1.getH()) * var1.getM();
      }
   }
}
