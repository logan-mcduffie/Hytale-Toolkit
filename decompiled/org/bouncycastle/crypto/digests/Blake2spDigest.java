package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Pack;

public class Blake2spDigest implements ExtendedDigest {
   private int bufferPos = 0;
   private int keyLength = 0;
   private int digestLength;
   private int fanout;
   private int depth;
   private int nodeOffset = 0;
   private long innerHashLength;
   private Blake2sDigest[] S = new Blake2sDigest[8];
   private Blake2sDigest root;
   private byte[] buffer = null;
   private byte[] salt = null;
   private byte[] param = null;
   private byte[] key = null;
   private final int BLAKE2S_BLOCKBYTES = 64;
   private final int BLAKE2S_KEYBYTES = 32;
   private final int BLAKE2S_OUTBYTES = 32;
   private final int PARALLELISM_DEGREE = 8;
   private final byte[] singleByte = new byte[1];

   public Blake2spDigest(byte[] var1) {
      this.param = new byte[32];
      this.buffer = new byte[512];
      this.init(var1);
   }

   @Override
   public String getAlgorithmName() {
      return "BLAKE2sp";
   }

   @Override
   public int getDigestSize() {
      return this.digestLength;
   }

   @Override
   public void update(byte var1) {
      this.singleByte[0] = var1;
      this.update(this.singleByte, 0, 1);
   }

   @Override
   public void update(byte[] var1, int var2, int var3) {
      int var4 = this.bufferPos;
      int var5 = 512 - var4;
      if (var4 != 0 && var3 >= var5) {
         System.arraycopy(var1, var2, this.buffer, var4, var5);

         for (int var6 = 0; var6 < 8; var6++) {
            this.S[var6].update(this.buffer, var6 * 64, 64);
         }

         var2 += var5;
         var3 -= var5;
         var4 = 0;
      }

      for (int var11 = 0; var11 < 8; var11++) {
         int var7 = var3;

         for (int var8 = var2 + var11 * 64; var7 >= 512; var7 -= 512) {
            this.S[var11].update(var1, var8, 64);
            var8 += 512;
         }
      }

      var2 += var3 - var3 % 512;
      var3 %= 512;
      if (var3 > 0) {
         System.arraycopy(var1, var2, this.buffer, var4, var3);
      }

      this.bufferPos = var4 + var3;
   }

   @Override
   public int doFinal(byte[] var1, int var2) {
      byte[][] var3 = new byte[8][32];
      int var4 = 0;

      for (int var5 = 0; var5 < 8; var5++) {
         if (this.bufferPos > var5 * 64) {
            var4 = this.bufferPos - var5 * 64;
            if (var4 > 64) {
               var4 = 64;
            }

            this.S[var5].update(this.buffer, var5 * 64, var4);
         }

         this.S[var5].doFinal(var3[var5], 0);
      }

      for (int var7 = 0; var7 < 8; var7++) {
         this.root.update(var3[var7], 0, 32);
      }

      int var8 = this.root.doFinal(var1, var2);
      this.reset();
      return var8;
   }

   @Override
   public void reset() {
      this.bufferPos = 0;
      this.digestLength = 32;
      this.root.reset();

      for (int var1 = 0; var1 < 8; var1++) {
         this.S[var1].reset();
      }

      this.root.setAsLastNode();
      this.S[7].setAsLastNode();
      if (this.key != null) {
         byte[] var3 = new byte[64];
         System.arraycopy(this.key, 0, var3, 0, this.keyLength);

         for (int var2 = 0; var2 < 8; var2++) {
            this.S[var2].update(var3, 0, 64);
         }
      }
   }

   @Override
   public int getByteLength() {
      return 64;
   }

   private void init(byte[] var1) {
      if (var1 != null && var1.length > 0) {
         this.keyLength = var1.length;
         if (this.keyLength > 32) {
            throw new IllegalArgumentException("Keys > 32 bytes are not supported");
         }

         this.key = Arrays.clone(var1);
      }

      this.bufferPos = 0;
      this.digestLength = 32;
      this.fanout = 8;
      this.depth = 2;
      this.innerHashLength = 32L;
      this.param[0] = (byte)this.digestLength;
      this.param[1] = (byte)this.keyLength;
      this.param[2] = (byte)this.fanout;
      this.param[3] = (byte)this.depth;
      Pack.intToLittleEndian(0, this.param, 8);
      this.param[14] = 1;
      this.param[15] = (byte)this.innerHashLength;
      this.root = new Blake2sDigest(null, this.param);
      Pack.intToLittleEndian(this.nodeOffset, this.param, 8);
      this.param[14] = 0;

      for (int var2 = 0; var2 < 8; var2++) {
         Pack.intToLittleEndian(var2, this.param, 8);
         this.S[var2] = new Blake2sDigest(null, this.param);
      }

      this.root.setAsLastNode();
      this.S[7].setAsLastNode();
      if (var1 != null && this.keyLength > 0) {
         byte[] var4 = new byte[64];
         System.arraycopy(var1, 0, var4, 0, this.keyLength);

         for (int var3 = 0; var3 < 8; var3++) {
            this.S[var3].update(var4, 0, 64);
         }
      }
   }
}
