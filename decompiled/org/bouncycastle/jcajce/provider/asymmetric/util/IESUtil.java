package org.bouncycastle.jcajce.provider.asymmetric.util;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.jce.spec.IESParameterSpec;

public class IESUtil {
   public static IESParameterSpec guessParameterSpec(BufferedBlockCipher var0, byte[] var1) {
      if (var0 == null) {
         return new IESParameterSpec(null, null, 128);
      } else {
         BlockCipher var2 = var0.getUnderlyingCipher();
         if (var2.getAlgorithmName().equals("DES")
            || var2.getAlgorithmName().equals("RC2")
            || var2.getAlgorithmName().equals("RC5-32")
            || var2.getAlgorithmName().equals("RC5-64")) {
            return new IESParameterSpec(null, null, 64, 64, var1);
         } else if (var2.getAlgorithmName().equals("SKIPJACK")) {
            return new IESParameterSpec(null, null, 80, 80, var1);
         } else {
            return var2.getAlgorithmName().equals("GOST28147")
               ? new IESParameterSpec(null, null, 256, 256, var1)
               : new IESParameterSpec(null, null, 128, 128, var1);
         }
      }
   }
}
