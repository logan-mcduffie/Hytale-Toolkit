package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.CryptoServiceProperties;
import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.util.Memoable;
import org.bouncycastle.util.Pack;

public class SHA512Digest extends LongDigest {
   private static final int DIGEST_LENGTH = 64;

   public SHA512Digest() {
      this(CryptoServicePurpose.ANY);
   }

   public SHA512Digest(CryptoServicePurpose var1) {
      super(var1);
      CryptoServicesRegistrar.checkConstraints(this.cryptoServiceProperties());
      this.reset();
   }

   public SHA512Digest(SHA512Digest var1) {
      super(var1);
      CryptoServicesRegistrar.checkConstraints(this.cryptoServiceProperties());
   }

   public SHA512Digest(byte[] var1) {
      super(CryptoServicePurpose.values()[var1[var1.length - 1]]);
      this.restoreState(var1);
      CryptoServicesRegistrar.checkConstraints(this.cryptoServiceProperties());
   }

   @Override
   public String getAlgorithmName() {
      return "SHA-512";
   }

   @Override
   public int getDigestSize() {
      return 64;
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
      Pack.longToBigEndian(this.H7, var1, var2 + 48);
      Pack.longToBigEndian(this.H8, var1, var2 + 56);
      this.reset();
      return 64;
   }

   @Override
   public void reset() {
      super.reset();
      this.H1 = 7640891576956012808L;
      this.H2 = -4942790177534073029L;
      this.H3 = 4354685564936845355L;
      this.H4 = -6534734903238641935L;
      this.H5 = 5840696475078001361L;
      this.H6 = -7276294671716946913L;
      this.H7 = 2270897969802886507L;
      this.H8 = 6620516959819538809L;
   }

   @Override
   public Memoable copy() {
      return new SHA512Digest(this);
   }

   @Override
   public void reset(Memoable var1) {
      SHA512Digest var2 = (SHA512Digest)var1;
      this.copyIn(var2);
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
