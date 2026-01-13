package org.bouncycastle.pqc.crypto.sphincs;

import org.bouncycastle.util.Arrays;

public class SPHINCSPublicKeyParameters extends SPHINCSKeyParameters {
   private final byte[] keyData;

   public SPHINCSPublicKeyParameters(byte[] var1) {
      super(false, null);
      this.keyData = Arrays.clone(var1);
   }

   public SPHINCSPublicKeyParameters(byte[] var1, String var2) {
      super(false, var2);
      this.keyData = Arrays.clone(var1);
   }

   public byte[] getKeyData() {
      return Arrays.clone(this.keyData);
   }
}
