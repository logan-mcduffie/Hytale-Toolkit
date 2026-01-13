package org.bouncycastle.pqc.crypto.sphincs;

import org.bouncycastle.util.Arrays;

public class SPHINCSPrivateKeyParameters extends SPHINCSKeyParameters {
   private final byte[] keyData;

   public SPHINCSPrivateKeyParameters(byte[] var1) {
      super(true, null);
      this.keyData = Arrays.clone(var1);
   }

   public SPHINCSPrivateKeyParameters(byte[] var1, String var2) {
      super(true, var2);
      this.keyData = Arrays.clone(var1);
   }

   public byte[] getKeyData() {
      return Arrays.clone(this.keyData);
   }
}
