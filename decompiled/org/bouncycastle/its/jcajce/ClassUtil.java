package org.bouncycastle.its.jcajce;

import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.spec.GCMParameterSpec;

class ClassUtil {
   public static AlgorithmParameterSpec getGCMSpec(byte[] var0, int var1) {
      return new GCMParameterSpec(var1, var0);
   }
}
