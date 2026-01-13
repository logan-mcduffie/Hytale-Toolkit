package org.bouncycastle.pqc.crypto.sphincsplus;

import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.Xof;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.digests.SHAKEDigest;
import org.bouncycastle.crypto.generators.MGF1BytesGenerator;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.MGFParameters;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Bytes;
import org.bouncycastle.util.Memoable;
import org.bouncycastle.util.Pack;

abstract class SPHINCSPlusEngine {
   @Deprecated
   final boolean robust;
   final int N;
   final int WOTS_W;
   final int WOTS_LOGW;
   final int WOTS_LEN;
   final int WOTS_LEN1;
   final int WOTS_LEN2;
   final int D;
   final int A;
   final int K;
   final int H;
   final int H_PRIME;
   final int T;

   public SPHINCSPlusEngine(boolean var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      this.N = var2;
      if (var3 == 16) {
         this.WOTS_LOGW = 4;
         this.WOTS_LEN1 = 8 * this.N / this.WOTS_LOGW;
         if (this.N <= 8) {
            this.WOTS_LEN2 = 2;
         } else if (this.N <= 136) {
            this.WOTS_LEN2 = 3;
         } else {
            if (this.N > 256) {
               throw new IllegalArgumentException("cannot precompute SPX_WOTS_LEN2 for n outside {2, .., 256}");
            }

            this.WOTS_LEN2 = 4;
         }
      } else {
         if (var3 != 256) {
            throw new IllegalArgumentException("wots_w assumed 16 or 256");
         }

         this.WOTS_LOGW = 8;
         this.WOTS_LEN1 = 8 * this.N / this.WOTS_LOGW;
         if (this.N <= 1) {
            this.WOTS_LEN2 = 1;
         } else {
            if (this.N > 256) {
               throw new IllegalArgumentException("cannot precompute SPX_WOTS_LEN2 for n outside {2, .., 256}");
            }

            this.WOTS_LEN2 = 2;
         }
      }

      this.WOTS_W = var3;
      this.WOTS_LEN = this.WOTS_LEN1 + this.WOTS_LEN2;
      this.robust = var1;
      this.D = var4;
      this.A = var5;
      this.K = var6;
      this.H = var7;
      this.H_PRIME = var7 / var4;
      this.T = 1 << var5;
   }

   abstract void init(byte[] var1);

   abstract byte[] F(byte[] var1, ADRS var2, byte[] var3);

   abstract byte[] H(byte[] var1, ADRS var2, byte[] var3, byte[] var4);

   abstract IndexedDigest H_msg(byte[] var1, byte[] var2, byte[] var3, byte[] var4);

   abstract byte[] T_l(byte[] var1, ADRS var2, byte[] var3);

   abstract byte[] PRF(byte[] var1, byte[] var2, ADRS var3);

   abstract byte[] PRF_msg(byte[] var1, byte[] var2, byte[] var3);

   static class HarakaSEngine extends SPHINCSPlusEngine {
      private HarakaSXof harakaSXof;
      private HarakaS256Digest harakaS256Digest;
      private HarakaS512Digest harakaS512Digest;

      public HarakaSEngine(boolean var1, int var2, int var3, int var4, int var5, int var6, int var7) {
         super(var1, var2, var3, var4, var5, var6, var7);
      }

      @Override
      void init(byte[] var1) {
         this.harakaSXof = new HarakaSXof(var1);
         this.harakaS256Digest = new HarakaS256Digest(this.harakaSXof);
         this.harakaS512Digest = new HarakaS512Digest(this.harakaSXof);
      }

      @Override
      public byte[] F(byte[] var1, ADRS var2, byte[] var3) {
         byte[] var4 = new byte[32];
         this.harakaS512Digest.update(var2.value, 0, var2.value.length);
         if (this.robust) {
            this.harakaS256Digest.update(var2.value, 0, var2.value.length);
            this.harakaS256Digest.doFinal(var4, 0);
            Bytes.xorTo(var3.length, var3, var4);
            this.harakaS512Digest.update(var4, 0, var3.length);
         } else {
            this.harakaS512Digest.update(var3, 0, var3.length);
         }

         this.harakaS512Digest.doFinal(var4, 0);
         return Arrays.copyOf(var4, this.N);
      }

      @Override
      public byte[] H(byte[] var1, ADRS var2, byte[] var3, byte[] var4) {
         byte[] var5 = new byte[this.N];
         byte[] var6 = new byte[var3.length + var4.length];
         System.arraycopy(var3, 0, var6, 0, var3.length);
         System.arraycopy(var4, 0, var6, var3.length, var4.length);
         var6 = this.bitmask(var2, var6);
         this.harakaSXof.update(var2.value, 0, var2.value.length);
         this.harakaSXof.update(var6, 0, var6.length);
         this.harakaSXof.doFinal(var5, 0, var5.length);
         return var5;
      }

      @Override
      IndexedDigest H_msg(byte[] var1, byte[] var2, byte[] var3, byte[] var4) {
         int var5 = this.A * this.K + 7 >> 3;
         int var6 = this.H / this.D;
         int var7 = this.H - var6;
         int var8 = var6 + 7 >> 3;
         int var9 = var7 + 7 >> 3;
         byte[] var10 = new byte[var5 + var8 + var9];
         this.harakaSXof.update(var1, 0, var1.length);
         this.harakaSXof.update(var3, 0, var3.length);
         this.harakaSXof.update(var4, 0, var4.length);
         this.harakaSXof.doFinal(var10, 0, var10.length);
         byte[] var11 = new byte[8];
         System.arraycopy(var10, var5, var11, 8 - var9, var9);
         long var12 = Pack.bigEndianToLong(var11, 0);
         var12 &= -1L >>> 64 - var7;
         byte[] var14 = new byte[4];
         System.arraycopy(var10, var5 + var9, var14, 4 - var8, var8);
         int var15 = Pack.bigEndianToInt(var14, 0);
         var15 &= -1 >>> 32 - var6;
         return new IndexedDigest(var12, var15, Arrays.copyOfRange(var10, 0, var5));
      }

      @Override
      public byte[] T_l(byte[] var1, ADRS var2, byte[] var3) {
         byte[] var4 = new byte[this.N];
         var3 = this.bitmask(var2, var3);
         this.harakaSXof.update(var2.value, 0, var2.value.length);
         this.harakaSXof.update(var3, 0, var3.length);
         this.harakaSXof.doFinal(var4, 0, var4.length);
         return var4;
      }

      @Override
      byte[] PRF(byte[] var1, byte[] var2, ADRS var3) {
         byte[] var4 = new byte[32];
         this.harakaS512Digest.update(var3.value, 0, var3.value.length);
         this.harakaS512Digest.update(var2, 0, var2.length);
         this.harakaS512Digest.doFinal(var4, 0);
         return Arrays.copyOf(var4, this.N);
      }

      @Override
      public byte[] PRF_msg(byte[] var1, byte[] var2, byte[] var3) {
         byte[] var4 = new byte[this.N];
         this.harakaSXof.update(var1, 0, var1.length);
         this.harakaSXof.update(var2, 0, var2.length);
         this.harakaSXof.update(var3, 0, var3.length);
         this.harakaSXof.doFinal(var4, 0, var4.length);
         return var4;
      }

      protected byte[] bitmask(ADRS var1, byte[] var2) {
         if (this.robust) {
            byte[] var3 = new byte[var2.length];
            this.harakaSXof.update(var1.value, 0, var1.value.length);
            this.harakaSXof.doFinal(var3, 0, var3.length);
            Bytes.xorTo(var2.length, var3, var2);
         }

         return var2;
      }
   }

   static class Sha2Engine extends SPHINCSPlusEngine {
      private final HMac treeHMac;
      private final MGF1BytesGenerator mgf1;
      private final byte[] hmacBuf;
      private final Digest msgDigest;
      private final byte[] msgDigestBuf;
      private final int bl;
      private final Digest sha256 = new SHA256Digest();
      private final byte[] sha256Buf = new byte[this.sha256.getDigestSize()];
      private Memoable msgMemo;
      private Memoable sha256Memo;

      public Sha2Engine(boolean var1, int var2, int var3, int var4, int var5, int var6, int var7) {
         super(var1, var2, var3, var4, var5, var6, var7);
         if (var2 == 16) {
            this.msgDigest = new SHA256Digest();
            this.treeHMac = new HMac(new SHA256Digest());
            this.mgf1 = new MGF1BytesGenerator(new SHA256Digest());
            this.bl = 64;
         } else {
            this.msgDigest = new SHA512Digest();
            this.treeHMac = new HMac(new SHA512Digest());
            this.mgf1 = new MGF1BytesGenerator(new SHA512Digest());
            this.bl = 128;
         }

         this.hmacBuf = new byte[this.treeHMac.getMacSize()];
         this.msgDigestBuf = new byte[this.msgDigest.getDigestSize()];
      }

      @Override
      void init(byte[] var1) {
         byte[] var2 = new byte[this.bl];
         this.msgDigest.update(var1, 0, var1.length);
         this.msgDigest.update(var2, 0, this.bl - this.N);
         this.msgMemo = ((Memoable)this.msgDigest).copy();
         this.msgDigest.reset();
         this.sha256.update(var1, 0, var1.length);
         this.sha256.update(var2, 0, 64 - var1.length);
         this.sha256Memo = ((Memoable)this.sha256).copy();
         this.sha256.reset();
      }

      @Override
      public byte[] F(byte[] var1, ADRS var2, byte[] var3) {
         byte[] var4 = this.compressedADRS(var2);
         if (this.robust) {
            var3 = this.bitmask256(Arrays.concatenate(var1, var4), var3);
         }

         ((Memoable)this.sha256).reset(this.sha256Memo);
         this.sha256.update(var4, 0, var4.length);
         this.sha256.update(var3, 0, var3.length);
         this.sha256.doFinal(this.sha256Buf, 0);
         return Arrays.copyOfRange(this.sha256Buf, 0, this.N);
      }

      @Override
      public byte[] H(byte[] var1, ADRS var2, byte[] var3, byte[] var4) {
         byte[] var5 = this.compressedADRS(var2);
         ((Memoable)this.msgDigest).reset(this.msgMemo);
         this.msgDigest.update(var5, 0, var5.length);
         if (this.robust) {
            byte[] var6 = this.bitmask(Arrays.concatenate(var1, var5), var3, var4);
            this.msgDigest.update(var6, 0, var6.length);
         } else {
            this.msgDigest.update(var3, 0, var3.length);
            this.msgDigest.update(var4, 0, var4.length);
         }

         this.msgDigest.doFinal(this.msgDigestBuf, 0);
         return Arrays.copyOfRange(this.msgDigestBuf, 0, this.N);
      }

      @Override
      IndexedDigest H_msg(byte[] var1, byte[] var2, byte[] var3, byte[] var4) {
         int var5 = (this.A * this.K + 7) / 8;
         int var6 = this.H / this.D;
         int var7 = this.H - var6;
         int var8 = (var6 + 7) / 8;
         int var9 = (var7 + 7) / 8;
         int var10 = var5 + var8 + var9;
         byte[] var11 = new byte[var10];
         byte[] var12 = new byte[this.msgDigest.getDigestSize()];
         this.msgDigest.update(var1, 0, var1.length);
         this.msgDigest.update(var2, 0, var2.length);
         this.msgDigest.update(var3, 0, var3.length);
         this.msgDigest.update(var4, 0, var4.length);
         this.msgDigest.doFinal(var12, 0);
         var11 = this.bitmask(Arrays.concatenate(var1, var2, var12), var11);
         byte[] var13 = new byte[8];
         System.arraycopy(var11, var5, var13, 8 - var9, var9);
         long var14 = Pack.bigEndianToLong(var13, 0);
         var14 &= -1L >>> 64 - var7;
         byte[] var16 = new byte[4];
         System.arraycopy(var11, var5 + var9, var16, 4 - var8, var8);
         int var17 = Pack.bigEndianToInt(var16, 0);
         var17 &= -1 >>> 32 - var6;
         return new IndexedDigest(var14, var17, Arrays.copyOfRange(var11, 0, var5));
      }

      @Override
      public byte[] T_l(byte[] var1, ADRS var2, byte[] var3) {
         byte[] var4 = this.compressedADRS(var2);
         if (this.robust) {
            var3 = this.bitmask(Arrays.concatenate(var1, var4), var3);
         }

         ((Memoable)this.msgDigest).reset(this.msgMemo);
         this.msgDigest.update(var4, 0, var4.length);
         this.msgDigest.update(var3, 0, var3.length);
         this.msgDigest.doFinal(this.msgDigestBuf, 0);
         return Arrays.copyOfRange(this.msgDigestBuf, 0, this.N);
      }

      @Override
      byte[] PRF(byte[] var1, byte[] var2, ADRS var3) {
         int var4 = var2.length;
         ((Memoable)this.sha256).reset(this.sha256Memo);
         byte[] var5 = this.compressedADRS(var3);
         this.sha256.update(var5, 0, var5.length);
         this.sha256.update(var2, 0, var2.length);
         this.sha256.doFinal(this.sha256Buf, 0);
         return Arrays.copyOfRange(this.sha256Buf, 0, var4);
      }

      @Override
      public byte[] PRF_msg(byte[] var1, byte[] var2, byte[] var3) {
         this.treeHMac.init(new KeyParameter(var1));
         this.treeHMac.update(var2, 0, var2.length);
         this.treeHMac.update(var3, 0, var3.length);
         this.treeHMac.doFinal(this.hmacBuf, 0);
         return Arrays.copyOfRange(this.hmacBuf, 0, this.N);
      }

      private byte[] compressedADRS(ADRS var1) {
         byte[] var2 = new byte[22];
         System.arraycopy(var1.value, 3, var2, 0, 1);
         System.arraycopy(var1.value, 8, var2, 1, 8);
         System.arraycopy(var1.value, 19, var2, 9, 1);
         System.arraycopy(var1.value, 20, var2, 10, 12);
         return var2;
      }

      protected byte[] bitmask(byte[] var1, byte[] var2) {
         byte[] var3 = new byte[var2.length];
         this.mgf1.init(new MGFParameters(var1));
         this.mgf1.generateBytes(var3, 0, var3.length);
         Bytes.xorTo(var2.length, var2, var3);
         return var3;
      }

      protected byte[] bitmask(byte[] var1, byte[] var2, byte[] var3) {
         byte[] var4 = new byte[var2.length + var3.length];
         this.mgf1.init(new MGFParameters(var1));
         this.mgf1.generateBytes(var4, 0, var4.length);
         Bytes.xorTo(var2.length, var2, var4);
         Bytes.xorTo(var3.length, var3, 0, var4, var2.length);
         return var4;
      }

      protected byte[] bitmask256(byte[] var1, byte[] var2) {
         byte[] var3 = new byte[var2.length];
         MGF1BytesGenerator var4 = new MGF1BytesGenerator(new SHA256Digest());
         var4.init(new MGFParameters(var1));
         var4.generateBytes(var3, 0, var3.length);
         Bytes.xorTo(var2.length, var2, var3);
         return var3;
      }
   }

   static class Shake256Engine extends SPHINCSPlusEngine {
      private final Xof treeDigest = new SHAKEDigest(256);
      private final Xof maskDigest = new SHAKEDigest(256);

      public Shake256Engine(boolean var1, int var2, int var3, int var4, int var5, int var6, int var7) {
         super(var1, var2, var3, var4, var5, var6, var7);
      }

      @Override
      void init(byte[] var1) {
      }

      @Override
      byte[] F(byte[] var1, ADRS var2, byte[] var3) {
         byte[] var4 = var3;
         if (this.robust) {
            var4 = this.bitmask(var1, var2, var3);
         }

         byte[] var5 = new byte[this.N];
         this.treeDigest.update(var1, 0, var1.length);
         this.treeDigest.update(var2.value, 0, var2.value.length);
         this.treeDigest.update(var4, 0, var4.length);
         this.treeDigest.doFinal(var5, 0, var5.length);
         return var5;
      }

      @Override
      byte[] H(byte[] var1, ADRS var2, byte[] var3, byte[] var4) {
         byte[] var5 = new byte[this.N];
         this.treeDigest.update(var1, 0, var1.length);
         this.treeDigest.update(var2.value, 0, var2.value.length);
         if (this.robust) {
            byte[] var6 = this.bitmask(var1, var2, var3, var4);
            this.treeDigest.update(var6, 0, var6.length);
         } else {
            this.treeDigest.update(var3, 0, var3.length);
            this.treeDigest.update(var4, 0, var4.length);
         }

         this.treeDigest.doFinal(var5, 0, var5.length);
         return var5;
      }

      @Override
      IndexedDigest H_msg(byte[] var1, byte[] var2, byte[] var3, byte[] var4) {
         int var5 = (this.A * this.K + 7) / 8;
         int var6 = this.H / this.D;
         int var7 = this.H - var6;
         int var8 = (var6 + 7) / 8;
         int var9 = (var7 + 7) / 8;
         int var10 = var5 + var8 + var9;
         byte[] var11 = new byte[var10];
         this.treeDigest.update(var1, 0, var1.length);
         this.treeDigest.update(var2, 0, var2.length);
         this.treeDigest.update(var3, 0, var3.length);
         this.treeDigest.update(var4, 0, var4.length);
         this.treeDigest.doFinal(var11, 0, var11.length);
         byte[] var12 = new byte[8];
         System.arraycopy(var11, var5, var12, 8 - var9, var9);
         long var13 = Pack.bigEndianToLong(var12, 0);
         var13 &= -1L >>> 64 - var7;
         byte[] var15 = new byte[4];
         System.arraycopy(var11, var5 + var9, var15, 4 - var8, var8);
         int var16 = Pack.bigEndianToInt(var15, 0);
         var16 &= -1 >>> 32 - var6;
         return new IndexedDigest(var13, var16, Arrays.copyOfRange(var11, 0, var5));
      }

      @Override
      byte[] T_l(byte[] var1, ADRS var2, byte[] var3) {
         byte[] var4 = var3;
         if (this.robust) {
            var4 = this.bitmask(var1, var2, var3);
         }

         byte[] var5 = new byte[this.N];
         this.treeDigest.update(var1, 0, var1.length);
         this.treeDigest.update(var2.value, 0, var2.value.length);
         this.treeDigest.update(var4, 0, var4.length);
         this.treeDigest.doFinal(var5, 0, var5.length);
         return var5;
      }

      @Override
      byte[] PRF(byte[] var1, byte[] var2, ADRS var3) {
         this.treeDigest.update(var1, 0, var1.length);
         this.treeDigest.update(var3.value, 0, var3.value.length);
         this.treeDigest.update(var2, 0, var2.length);
         byte[] var4 = new byte[this.N];
         this.treeDigest.doFinal(var4, 0, this.N);
         return var4;
      }

      @Override
      public byte[] PRF_msg(byte[] var1, byte[] var2, byte[] var3) {
         this.treeDigest.update(var1, 0, var1.length);
         this.treeDigest.update(var2, 0, var2.length);
         this.treeDigest.update(var3, 0, var3.length);
         byte[] var4 = new byte[this.N];
         this.treeDigest.doFinal(var4, 0, var4.length);
         return var4;
      }

      protected byte[] bitmask(byte[] var1, ADRS var2, byte[] var3) {
         byte[] var4 = new byte[var3.length];
         this.maskDigest.update(var1, 0, var1.length);
         this.maskDigest.update(var2.value, 0, var2.value.length);
         this.maskDigest.doFinal(var4, 0, var4.length);
         Bytes.xorTo(var3.length, var3, var4);
         return var4;
      }

      protected byte[] bitmask(byte[] var1, ADRS var2, byte[] var3, byte[] var4) {
         byte[] var5 = new byte[var3.length + var4.length];
         this.maskDigest.update(var1, 0, var1.length);
         this.maskDigest.update(var2.value, 0, var2.value.length);
         this.maskDigest.doFinal(var5, 0, var5.length);
         Bytes.xorTo(var3.length, var3, var5);
         Bytes.xorTo(var4.length, var4, 0, var5, var3.length);
         return var5;
      }
   }
}
