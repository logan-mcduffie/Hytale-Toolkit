package org.bouncycastle.jcajce.spec;

import java.security.spec.KeySpec;
import org.bouncycastle.util.Arrays;

public class MLKEMPublicKeySpec implements KeySpec {
   private final MLKEMParameterSpec params;
   private final byte[] publicData;

   public MLKEMPublicKeySpec(MLKEMParameterSpec var1, byte[] var2) {
      this.params = var1;
      this.publicData = Arrays.clone(var2);
   }

   public MLKEMParameterSpec getParameterSpec() {
      return this.params;
   }

   public byte[] getPublicData() {
      return Arrays.clone(this.publicData);
   }
}
