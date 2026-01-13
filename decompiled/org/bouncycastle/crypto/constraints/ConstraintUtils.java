package org.bouncycastle.crypto.constraints;

import java.math.BigInteger;
import org.bouncycastle.math.ec.ECCurve;

public class ConstraintUtils {
   public static int bitsOfSecurityFor(BigInteger var0) {
      return bitsOfSecurityForFF(var0.bitLength());
   }

   public static int bitsOfSecurityFor(ECCurve var0) {
      int var1 = (var0.getFieldSize() + 1) / 2;
      return var1 > 256 ? 256 : var1;
   }

   public static int bitsOfSecurityForFF(int var0) {
      if (var0 >= 2048) {
         return var0 >= 3072 ? (var0 >= 7680 ? (var0 >= 15360 ? 256 : 192) : 128) : 112;
      } else {
         return var0 >= 1024 ? 80 : 20;
      }
   }
}
