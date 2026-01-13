package org.bouncycastle.pqc.crypto.slhdsa;

import org.bouncycastle.util.Arrays;

public class SLHDSAPublicKeyParameters extends SLHDSAKeyParameters {
   private final PK pk;

   public SLHDSAPublicKeyParameters(SLHDSAParameters var1, byte[] var2) {
      super(false, var1);
      int var3 = var1.getN();
      if (var2.length != 2 * var3) {
         throw new IllegalArgumentException("public key encoding does not match parameters");
      } else {
         this.pk = new PK(Arrays.copyOfRange(var2, 0, var3), Arrays.copyOfRange(var2, var3, 2 * var3));
      }
   }

   SLHDSAPublicKeyParameters(SLHDSAParameters var1, PK var2) {
      super(false, var1);
      this.pk = var2;
   }

   public byte[] getSeed() {
      return Arrays.clone(this.pk.seed);
   }

   public byte[] getRoot() {
      return Arrays.clone(this.pk.root);
   }

   public byte[] getEncoded() {
      return Arrays.concatenate(this.pk.seed, this.pk.root);
   }
}
