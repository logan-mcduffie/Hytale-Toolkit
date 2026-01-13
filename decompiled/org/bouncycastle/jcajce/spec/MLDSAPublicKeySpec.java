package org.bouncycastle.jcajce.spec;

import java.security.spec.KeySpec;
import org.bouncycastle.util.Arrays;

public class MLDSAPublicKeySpec implements KeySpec {
   private final MLDSAParameterSpec params;
   private final byte[] publicData;

   public MLDSAPublicKeySpec(MLDSAParameterSpec var1, byte[] var2) {
      this.params = var1;
      this.publicData = Arrays.clone(var2);
   }

   public MLDSAParameterSpec getParameterSpec() {
      return this.params;
   }

   public byte[] getPublicData() {
      return Arrays.clone(this.publicData);
   }
}
