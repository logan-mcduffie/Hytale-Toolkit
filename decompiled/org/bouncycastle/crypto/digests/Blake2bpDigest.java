package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Pack;

public class Blake2bpDigest implements ExtendedDigest {
   private int bufferPos = 0;
   private int keyLength = 0;
   private int digestLength;
   private int fanout;
   private int depth;
   private int nodeOffset = 0;
   private long innerHashLength;
   private Blake2bDigest[] S = new Blake2bDigest[4];
   private Blake2bDigest root;
   private byte[] buffer = null;
   private byte[] salt = null;
   private byte[] param = null;
   private byte[] key = null;
   private final int BLAKE2B_BLOCKBYTES = 128;
   private final int BLAKE2B_KEYBYTES = 64;
   private final int BLAKE2B_OUTBYTES = 64;
   private final int PARALLELISM_DEGREE = 4;
   private final byte[] singleByte = new byte[1];

   public Blake2bpDigest(byte[] var1) {
      this.param = new byte[64];
      this.buffer = new byte[512];
      this.init(var1);
   }

   @Override
   public String getAlgorithmName() {
      return "BLAKE2bp";
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
      int var5 = 1024 - var4;
      if (var4 != 0 && var3 >= var5) {
         System.arraycopy(var1, var2, this.buffer, var4, var5);

         for (int var6 = 0; var6 < 4; var6++) {
            this.S[var6].update(this.buffer, var6 * 128, 128);
         }

         var2 += var5;
         var3 -= var5;
         var4 = 0;
      }

      for (int var11 = 0; var11 < 4; var11++) {
         int var7 = var3;

         for (int var8 = var2 + var11 * 128; var7 >= 512; var7 -= 512) {
            this.S[var11].update(var1, var8, 128);
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
      byte[][] var3 = new byte[4][64];
      int var4 = 0;

      for (int var5 = 0; var5 < 4; var5++) {
         if (this.bufferPos > var5 * 128) {
            var4 = this.bufferPos - var5 * 128;
            if (var4 > 128) {
               var4 = 128;
            }

            this.S[var5].update(this.buffer, var5 * 128, var4);
         }

         this.S[var5].doFinal(var3[var5], 0);
      }

      for (int var7 = 0; var7 < 4; var7++) {
         this.root.update(var3[var7], 0, 64);
      }

      int var8 = this.root.doFinal(var1, var2);
      this.reset();
      return var8;
   }

   @Override
   public void reset() {
      this.bufferPos = 0;
      this.digestLength = 64;
      this.root.reset();

      for (int var1 = 0; var1 < 4; var1++) {
         this.S[var1].reset();
      }

      this.root.setAsLastNode();
      this.S[3].setAsLastNode();
      if (this.key != null) {
         byte[] var3 = new byte[128];
         System.arraycopy(this.key, 0, var3, 0, this.keyLength);

         for (int var2 = 0; var2 < 4; var2++) {
            this.S[var2].update(var3, 0, 128);
         }
      }
   }

   @Override
   public int getByteLength() {
      return 0;
   }

   private void init(byte[] var1) {
      if (var1 != null && var1.length > 0) {
         this.keyLength = var1.length;
         if (this.keyLength > 64) {
            throw new IllegalArgumentException("Keys > 64 bytes are not supported");
         }

         this.key = Arrays.clone(var1);
      }

      this.bufferPos = 0;
      this.digestLength = 64;
      this.fanout = 4;
      this.depth = 2;
      this.innerHashLength = 64L;
      this.param[0] = (byte)this.digestLength;
      this.param[1] = (byte)this.keyLength;
      this.param[2] = (byte)this.fanout;
      this.param[3] = (byte)this.depth;
      this.param[16] = 1;
      this.param[17] = (byte)this.innerHashLength;
      this.root = new Blake2bDigest(null, this.param);
      Pack.intToLittleEndian(this.nodeOffset, this.param, 8);
      this.param[16] = 0;

      for (int var2 = 0; var2 < 4; var2++) {
         Pack.intToLittleEndian(var2, this.param, 8);
         this.S[var2] = new Blake2bDigest(null, this.param);
      }

      this.root.setAsLastNode();
      this.S[3].setAsLastNode();
      if (var1 != null && this.keyLength > 0) {
         byte[] var4 = new byte[128];
         System.arraycopy(var1, 0, var4, 0, this.keyLength);

         for (int var3 = 0; var3 < 4; var3++) {
            this.S[var3].update(var4, 0, 128);
         }
      }
   }
}
