package org.bouncycastle.jcajce.spec;

import java.security.spec.KeySpec;
import org.bouncycastle.util.Arrays;

public class MLDSAPrivateKeySpec implements KeySpec {
   private final byte[] data;
   private final byte[] publicData;
   private final MLDSAParameterSpec params;
   private final boolean isSeed;

   public MLDSAPrivateKeySpec(MLDSAParameterSpec var1, byte[] var2) {
      if (var2.length != 32) {
         throw new IllegalArgumentException("incorrect length for seed");
      } else {
         this.isSeed = true;
         this.params = var1;
         this.data = Arrays.clone(var2);
         this.publicData = null;
      }
   }

   public MLDSAPrivateKeySpec(MLDSAParameterSpec var1, byte[] var2, byte[] var3) {
      this.isSeed = false;
      this.params = var1;
      this.data = Arrays.clone(var2);
      this.publicData = Arrays.clone(var3);
   }

   public boolean isSeed() {
      return this.isSeed;
   }

   public MLDSAParameterSpec getParameterSpec() {
      return this.params;
   }

   public byte[] getSeed() {
      if (this.isSeed()) {
         return Arrays.clone(this.data);
      } else {
         throw new IllegalStateException("KeySpec represents long form");
      }
   }

   public byte[] getPrivateData() {
      if (!this.isSeed()) {
         return Arrays.clone(this.data);
      } else {
         throw new IllegalStateException("KeySpec represents seed");
      }
   }

   public byte[] getPublicData() {
      if (!this.isSeed()) {
         return Arrays.clone(this.publicData);
      } else {
         throw new IllegalStateException("KeySpec represents long form");
      }
   }
}
