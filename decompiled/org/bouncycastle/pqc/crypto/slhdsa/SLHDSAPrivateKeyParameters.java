package org.bouncycastle.pqc.crypto.slhdsa;

import org.bouncycastle.util.Arrays;

public class SLHDSAPrivateKeyParameters extends SLHDSAKeyParameters {
   final SK sk;
   final PK pk;

   public SLHDSAPrivateKeyParameters(SLHDSAParameters var1, byte[] var2) {
      super(true, var1);
      int var3 = var1.getN();
      if (var2.length != 4 * var3) {
         throw new IllegalArgumentException("private key encoding does not match parameters");
      } else {
         this.sk = new SK(Arrays.copyOfRange(var2, 0, var3), Arrays.copyOfRange(var2, var3, 2 * var3));
         this.pk = new PK(Arrays.copyOfRange(var2, 2 * var3, 3 * var3), Arrays.copyOfRange(var2, 3 * var3, 4 * var3));
      }
   }

   public SLHDSAPrivateKeyParameters(SLHDSAParameters var1, byte[] var2, byte[] var3, byte[] var4, byte[] var5) {
      super(true, var1);
      this.sk = new SK(var2, var3);
      this.pk = new PK(var4, var5);
   }

   SLHDSAPrivateKeyParameters(SLHDSAParameters var1, SK var2, PK var3) {
      super(true, var1);
      this.sk = var2;
      this.pk = var3;
   }

   public byte[] getSeed() {
      return Arrays.clone(this.sk.seed);
   }

   public byte[] getPrf() {
      return Arrays.clone(this.sk.prf);
   }

   public byte[] getPublicSeed() {
      return Arrays.clone(this.pk.seed);
   }

   public byte[] getRoot() {
      return Arrays.clone(this.pk.root);
   }

   public byte[] getPublicKey() {
      return Arrays.concatenate(this.pk.seed, this.pk.root);
   }

   public byte[] getEncoded() {
      return Arrays.concatenate(new byte[][]{this.sk.seed, this.sk.prf, this.pk.seed, this.pk.root});
   }

   public byte[] getEncodedPublicKey() {
      return Arrays.concatenate(this.pk.seed, this.pk.root);
   }
}
