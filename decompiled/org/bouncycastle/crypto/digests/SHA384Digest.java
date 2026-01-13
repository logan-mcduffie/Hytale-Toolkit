package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.CryptoServiceProperties;
import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.util.Memoable;
import org.bouncycastle.util.Pack;

public class SHA384Digest extends LongDigest {
   private static final int DIGEST_LENGTH = 48;

   public SHA384Digest() {
      this(CryptoServicePurpose.ANY);
   }

   public SHA384Digest(CryptoServicePurpose var1) {
      super(var1);
      CryptoServicesRegistrar.checkConstraints(this.cryptoServiceProperties());
      this.reset();
   }

   public SHA384Digest(SHA384Digest var1) {
      super(var1);
      CryptoServicesRegistrar.checkConstraints(this.cryptoServiceProperties());
   }

   public SHA384Digest(byte[] var1) {
      super(CryptoServicePurpose.values()[var1[var1.length - 1]]);
      this.restoreState(var1);
      CryptoServicesRegistrar.checkConstraints(this.cryptoServiceProperties());
   }

   @Override
   public String getAlgorithmName() {
      return "SHA-384";
   }

   @Override
   public int getDigestSize() {
      return 48;
   }

   @Override
   public int doFinal(byte[] var1, int var2) {
      this.finish();
      Pack.longToBigEndian(this.H1, var1, var2);
      Pack.longToBigEndian(this.H2, var1, var2 + 8);
      Pack.longToBigEndian(this.H3, var1, var2 + 16);
      Pack.longToBigEndian(this.H4, var1, var2 + 24);
      Pack.longToBigEndian(this.H5, var1, var2 + 32);
      Pack.longToBigEndian(this.H6, var1, var2 + 40);
      this.reset();
      return 48;
   }

   @Override
   public void reset() {
      super.reset();
      this.H1 = -3766243637369397544L;
      this.H2 = 7105036623409894663L;
      this.H3 = -7973340178411365097L;
      this.H4 = 1526699215303891257L;
      this.H5 = 7436329637833083697L;
      this.H6 = -8163818279084223215L;
      this.H7 = -2662702644619276377L;
      this.H8 = 5167115440072839076L;
   }

   @Override
   public Memoable copy() {
      return new SHA384Digest(this);
   }

   @Override
   public void reset(Memoable var1) {
      SHA384Digest var2 = (SHA384Digest)var1;
      super.copyIn(var2);
   }

   @Override
   public byte[] getEncodedState() {
      byte[] var1 = new byte[this.getEncodedStateSize() + 1];
      super.populateState(var1);
      var1[var1.length - 1] = (byte)this.purpose.ordinal();
      return var1;
   }

   @Override
   protected CryptoServiceProperties cryptoServiceProperties() {
      return Utils.getDefaultProperties(this, 256, this.purpose);
   }
}
