package org.bouncycastle.pqc.crypto.ntruprime;

import org.bouncycastle.util.Arrays;

public class SNTRUPrimePrivateKeyParameters extends SNTRUPrimeKeyParameters {
   private final byte[] f;
   private final byte[] ginv;
   private final byte[] pk;
   private final byte[] rho;
   private final byte[] hash;

   public SNTRUPrimePrivateKeyParameters(SNTRUPrimeParameters var1, byte[] var2, byte[] var3, byte[] var4, byte[] var5, byte[] var6) {
      super(true, var1);
      this.f = Arrays.clone(var2);
      this.ginv = Arrays.clone(var3);
      this.pk = Arrays.clone(var4);
      this.rho = Arrays.clone(var5);
      this.hash = Arrays.clone(var6);
   }

   public byte[] getF() {
      return Arrays.clone(this.f);
   }

   public byte[] getGinv() {
      return Arrays.clone(this.ginv);
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
      System.arraycopy(this.f, 0, var1, 0, this.f.length);
      System.arraycopy(this.ginv, 0, var1, this.f.length, this.ginv.length);
      System.arraycopy(this.pk, 0, var1, this.f.length + this.ginv.length, this.pk.length);
      System.arraycopy(this.rho, 0, var1, this.f.length + this.ginv.length + this.pk.length, this.rho.length);
      System.arraycopy(this.hash, 0, var1, this.f.length + this.ginv.length + this.pk.length + this.rho.length, this.hash.length);
      return var1;
   }
}
