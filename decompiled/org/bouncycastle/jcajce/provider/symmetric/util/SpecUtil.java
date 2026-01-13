package org.bouncycastle.jcajce.provider.symmetric.util;

import java.security.AlgorithmParameters;
import java.security.spec.AlgorithmParameterSpec;

class SpecUtil {
   static AlgorithmParameterSpec extractSpec(AlgorithmParameters var0, Class[] var1) {
      try {
         return var0.getParameterSpec(AlgorithmParameterSpec.class);
      } catch (Exception var6) {
         for (int var3 = 0; var3 != var1.length; var3++) {
            if (var1[var3] != null) {
               try {
                  return var0.getParameterSpec(var1[var3]);
               } catch (Exception var5) {
               }
            }
         }

         return null;
      }
   }
}
