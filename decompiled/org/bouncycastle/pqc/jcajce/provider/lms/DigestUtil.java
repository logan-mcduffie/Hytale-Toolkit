package org.bouncycastle.pqc.jcajce.provider.lms;

import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.Xof;

class DigestUtil {
   public static byte[] getDigestResult(Digest var0) {
      byte[] var1 = new byte[var0.getDigestSize()];
      if (var0 instanceof Xof) {
         ((Xof)var0).doFinal(var1, 0, var1.length);
      } else {
         var0.doFinal(var1, 0);
      }

      return var1;
   }
}
