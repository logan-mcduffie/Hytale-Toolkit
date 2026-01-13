package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.util.Memoable;

public class GOST3411_2012_512Digest extends GOST3411_2012Digest {
   private static final byte[] IV = new byte[]{
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0
   };

   public GOST3411_2012_512Digest(CryptoServicePurpose var1) {
      super(IV, var1);
   }

   public GOST3411_2012_512Digest() {
      super(IV, CryptoServicePurpose.ANY);
   }

   public GOST3411_2012_512Digest(GOST3411_2012_512Digest var1) {
      super(IV, var1.purpose);
      this.reset(var1);
   }

   @Override
   public String getAlgorithmName() {
      return "GOST3411-2012-512";
   }

   @Override
   public int getDigestSize() {
      return 64;
   }

   @Override
   public Memoable copy() {
      return new GOST3411_2012_512Digest(this);
   }
}
