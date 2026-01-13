package org.bouncycastle.pqc.crypto.falcon;

import org.bouncycastle.util.Arrays;

public class FalconPrivateKeyParameters extends FalconKeyParameters {
   private final byte[] pk;
   private final byte[] f;
   private final byte[] g;
   private final byte[] F;

   public FalconPrivateKeyParameters(FalconParameters var1, byte[] var2, byte[] var3, byte[] var4, byte[] var5) {
      super(true, var1);
      this.f = Arrays.clone(var2);
      this.g = Arrays.clone(var3);
      this.F = Arrays.clone(var4);
      this.pk = Arrays.clone(var5);
   }

   public byte[] getEncoded() {
      return Arrays.concatenate(this.f, this.g, this.F);
   }

   public byte[] getPublicKey() {
      return Arrays.clone(this.pk);
   }

   public byte[] getSpolyf() {
      return Arrays.clone(this.f);
   }

   public byte[] getG() {
      return Arrays.clone(this.g);
   }

   public byte[] getSpolyF() {
      return Arrays.clone(this.F);
   }
}
