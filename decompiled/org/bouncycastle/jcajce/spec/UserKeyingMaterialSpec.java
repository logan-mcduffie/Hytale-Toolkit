package org.bouncycastle.jcajce.spec;

import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.util.Arrays;

public class UserKeyingMaterialSpec implements AlgorithmParameterSpec {
   private final byte[] userKeyingMaterial;
   private final byte[] salt;

   public UserKeyingMaterialSpec(byte[] var1) {
      this(var1, null);
   }

   public UserKeyingMaterialSpec(byte[] var1, byte[] var2) {
      this.userKeyingMaterial = Arrays.clone(var1);
      this.salt = Arrays.clone(var2);
   }

   public byte[] getUserKeyingMaterial() {
      return Arrays.clone(this.userKeyingMaterial);
   }

   public byte[] getSalt() {
      return Arrays.clone(this.salt);
   }
}
