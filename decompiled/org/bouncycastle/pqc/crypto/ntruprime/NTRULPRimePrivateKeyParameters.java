package org.bouncycastle.pqc.crypto.ntruprime;

import org.bouncycastle.util.Arrays;

public class NTRULPRimePrivateKeyParameters extends NTRULPRimeKeyParameters {
   private final byte[] enca;
   private final byte[] pk;
   private final byte[] rho;
   private final byte[] hash;

   public NTRULPRimePrivateKeyParameters(NTRULPRimeParameters var1, byte[] var2, byte[] var3, byte[] var4, byte[] var5) {
      super(true, var1);
      this.enca = Arrays.clone(var2);
      this.pk = Arrays.clone(var3);
      this.rho = Arrays.clone(var4);
      this.hash = Arrays.clone(var5);
   }

   public byte[] getEnca() {
      return Arrays.clone(this.enca);
   }

   public byte[] getPk() {
      return Arrays.clone(this.pk);
   }

   public byte[] getRho() {
      return Arrays.clone(this.rho);
   }

   public byte[] getHash() {
      return Arrays.clone(this.hash);
   }

   public byte[] getEncoded() {
      byte[] var1 = new byte[this.getParameters().getPrivateKeyBytes()];
      System.arraycopy(this.enca, 0, var1, 0, this.enca.length);
      System.arraycopy(this.pk, 0, var1, this.enca.length, this.pk.length);
      System.arraycopy(this.rho, 0, var1, this.enca.length + this.pk.length, this.rho.length);
      System.arraycopy(this.hash, 0, var1, this.enca.length + this.pk.length + this.rho.length, this.hash.length);
      return var1;
   }
}
