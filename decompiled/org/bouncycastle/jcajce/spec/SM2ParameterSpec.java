package org.bouncycastle.jcajce.spec;

import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.util.Arrays;

public class SM2ParameterSpec implements AlgorithmParameterSpec {
   private byte[] id;

   public SM2ParameterSpec(byte[] var1) {
      if (var1 == null) {
         throw new NullPointerException("id string cannot be null");
      } else {
         this.id = Arrays.clone(var1);
      }
   }

   public byte[] getID() {
      return Arrays.clone(this.id);
   }
}
