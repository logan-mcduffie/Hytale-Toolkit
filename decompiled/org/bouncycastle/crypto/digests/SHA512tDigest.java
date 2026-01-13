package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.CryptoServiceProperties;
import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.util.Memoable;
import org.bouncycastle.util.MemoableResetException;
import org.bouncycastle.util.Pack;

public class SHA512tDigest extends LongDigest {
   private int digestLength;
   private long H1t;
   private long H2t;
   private long H3t;
   private long H4t;
   private long H5t;
   private long H6t;
   private long H7t;
   private long H8t;

   public SHA512tDigest(int var1) {
      this(var1, CryptoServicePurpose.ANY);
   }

   public SHA512tDigest(int var1, CryptoServicePurpose var2) {
      if (var1 >= 512) {
         throw new IllegalArgumentException("bitLength cannot be >= 512");
      } else if (var1 % 8 != 0) {
         throw new IllegalArgumentException("bitLength needs to be a multiple of 8");
      } else if (var1 == 384) {
         throw new IllegalArgumentException("bitLength cannot be 384 use SHA384 instead");
      } else {
         this.digestLength = var1 / 8;
         CryptoServicesRegistrar.checkConstraints(this.cryptoServiceProperties());
         this.tIvGenerate(this.digestLength * 8);
         this.reset();
      }
   }

   public SHA512tDigest(SHA512tDigest var1) {
      super(var1);
      this.digestLength = var1.digestLength;
      CryptoServicesRegistrar.checkConstraints(this.cryptoServiceProperties());
      this.reset(var1);
   }

   public SHA512tDigest(byte[] var1) {
      this(readDigestLength(var1), CryptoServicePurpose.values()[var1[var1.length - 1]]);
      CryptoServicesRegistrar.checkConstraints(this.cryptoServiceProperties());
      this.restoreState(var1);
   }

   private static int readDigestLength(byte[] var0) {
      return Pack.bigEndianToInt(var0, var0.length - 5);
   }

   @Override
   public String getAlgorithmName() {
      return "SHA-512/" + Integer.toString(this.digestLength * 8);
   }

   @Override
   public int getDigestSize() {
      return this.digestLength;
   }

   @Override
   public int doFinal(byte[] var1, int var2) {
      this.finish();
      longToBigEndian(this.H1, var1, var2, this.digestLength);
      longToBigEndian(this.H2, var1, var2 + 8, this.digestLength - 8);
      longToBigEndian(this.H3, var1, var2 + 16, this.digestLength - 16);
      longToBigEndian(this.H4, var1, var2 + 24, this.digestLength - 24);
      longToBigEndian(this.H5, var1, var2 + 32, this.digestLength - 32);
      longToBigEndian(this.H6, var1, var2 + 40, this.digestLength - 40);
      longToBigEndian(this.H7, var1, var2 + 48, this.digestLength - 48);
      longToBigEndian(this.H8, var1, var2 + 56, this.digestLength - 56);
      this.reset();
      return this.digestLength;
   }

   @Override
   public void reset() {
      super.reset();
      this.H1 = this.H1t;
      this.H2 = this.H2t;
      this.H3 = this.H3t;
      this.H4 = this.H4t;
      this.H5 = this.H5t;
      this.H6 = this.H6t;
      this.H7 = this.H7t;
      this.H8 = this.H8t;
   }

   private void tIvGenerate(int var1) {
      this.H1 = -3482333909917012819L;
      this.H2 = 2216346199247487646L;
      this.H3 = -7364697282686394994L;
      this.H4 = 65953792586715988L;
      this.H5 = -816286391624063116L;
      this.H6 = 4512832404995164602L;
      this.H7 = -5033199132376557362L;
      this.H8 = -124578254951840548L;
      this.update((byte)83);
      this.update((byte)72);
      this.update((byte)65);
      this.update((byte)45);
      this.update((byte)53);
      this.update((byte)49);
      this.update((byte)50);
      this.update((byte)47);
      if (var1 > 100) {
         this.update((byte)(var1 / 100 + 48));
         var1 %= 100;
         this.update((byte)(var1 / 10 + 48));
         var1 %= 10;
         this.update((byte)(var1 + 48));
      } else if (var1 > 10) {
         this.update((byte)(var1 / 10 + 48));
         var1 %= 10;
         this.update((byte)(var1 + 48));
      } else {
         this.update((byte)(var1 + 48));
      }

      this.finish();
      this.H1t = this.H1;
      this.H2t = this.H2;
      this.H3t = this.H3;
      this.H4t = this.H4;
      this.H5t = this.H5;
      this.H6t = this.H6;
      this.H7t = this.H7;
      this.H8t = this.H8;
   }

   private static void longToBigEndian(long var0, byte[] var2, int var3, int var4) {
      if (var4 > 0) {
         intToBigEndian((int)(var0 >>> 32), var2, var3, var4);
         if (var4 > 4) {
            intToBigEndian((int)(var0 & 4294967295L), var2, var3 + 4, var4 - 4);
         }
      }
   }

   private static void intToBigEndian(int var0, byte[] var1, int var2, int var3) {
      int var4 = Math.min(4, var3);

      while (--var4 >= 0) {
         int var5 = 8 * (3 - var4);
         var1[var2 + var4] = (byte)(var0 >>> var5);
      }
   }

   @Override
   public Memoable copy() {
      return new SHA512tDigest(this);
   }

   @Override
   public void reset(Memoable var1) {
      SHA512tDigest var2 = (SHA512tDigest)var1;
      if (this.digestLength != var2.digestLength) {
         throw new MemoableResetException("digestLength inappropriate in other");
      } else {
         super.copyIn(var2);
         this.H1t = var2.H1t;
         this.H2t = var2.H2t;
         this.H3t = var2.H3t;
         this.H4t = var2.H4t;
         this.H5t = var2.H5t;
         this.H6t = var2.H6t;
         this.H7t = var2.H7t;
         this.H8t = var2.H8t;
      }
   }

   @Override
   public byte[] getEncodedState() {
      int var1 = this.getEncodedStateSize();
      byte[] var2 = new byte[var1 + 4 + 1];
      this.populateState(var2);
      Pack.intToBigEndian(this.digestLength * 8, var2, var1);
      var2[var2.length - 1] = (byte)this.purpose.ordinal();
      return var2;
   }

   @Override
   protected CryptoServiceProperties cryptoServiceProperties() {
      return Utils.getDefaultProperties(this, this.getDigestSize() * 8, this.purpose);
   }
}
