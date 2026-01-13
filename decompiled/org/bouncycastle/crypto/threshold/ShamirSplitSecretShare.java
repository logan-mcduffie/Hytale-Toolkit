package org.bouncycastle.crypto.threshold;

import java.io.IOException;
import org.bouncycastle.util.Arrays;

public class ShamirSplitSecretShare implements SecretShare {
   private final byte[] secretShare;
   final int r;

   public ShamirSplitSecretShare(byte[] var1, int var2) {
      this.secretShare = Arrays.clone(var1);
      this.r = var2;
   }

   public ShamirSplitSecretShare(byte[] var1) {
      this.secretShare = Arrays.clone(var1);
      this.r = 1;
   }

   @Override
   public byte[] getEncoded() throws IOException {
      return Arrays.clone(this.secretShare);
   }
}
