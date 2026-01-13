package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.TweakableBlockCipherParameters;
import org.bouncycastle.util.Pack;

public class ThreefishEngine implements BlockCipher {
   public static final int BLOCKSIZE_256 = 256;
   public static final int BLOCKSIZE_512 = 512;
   public static final int BLOCKSIZE_1024 = 1024;
   private static final int TWEAK_SIZE_BYTES = 16;
   private static final int TWEAK_SIZE_WORDS = 2;
   private static final int ROUNDS_256 = 72;
   private static final int ROUNDS_512 = 72;
   private static final int ROUNDS_1024 = 80;
   private static final int MAX_ROUNDS = 80;
   private static final long C_240 = 2004413935125273122L;
   private static int[] MOD9 = new int[80];
   private static int[] MOD17 = new int[MOD9.length];
   private static int[] MOD5 = new int[MOD9.length];
   private static int[] MOD3 = new int[MOD9.length];
   private int blocksizeBytes;
   private int blocksizeWords;
   private long[] currentBlock;
   private long[] t = new long[5];
   private long[] kw;
   private ThreefishEngine.ThreefishCipher cipher;
   private boolean forEncryption;

   public ThreefishEngine(int var1) {
      this.blocksizeBytes = var1 / 8;
      this.blocksizeWords = this.blocksizeBytes / 8;
      this.currentBlock = new long[this.blocksizeWords];
      this.kw = new long[2 * this.blocksizeWords + 1];
      switch (var1) {
         case 256:
            this.cipher = new ThreefishEngine.Threefish256Cipher(this.kw, this.t);
            break;
         case 512:
            this.cipher = new ThreefishEngine.Threefish512Cipher(this.kw, this.t);
            break;
         case 1024:
            this.cipher = new ThreefishEngine.Threefish1024Cipher(this.kw, this.t);
            break;
         default:
            throw new IllegalArgumentException("Invalid blocksize - Threefish is defined with block size of 256, 512, or 1024 bits");
      }
   }

   @Override
   public void init(boolean var1, CipherParameters var2) throws IllegalArgumentException {
      byte[] var3;
      byte[] var4;
      if (var2 instanceof TweakableBlockCipherParameters) {
         TweakableBlockCipherParameters var5 = (TweakableBlockCipherParameters)var2;
         var3 = var5.getKey().getKey();
         var4 = var5.getTweak();
      } else {
         if (!(var2 instanceof KeyParameter)) {
            throw new IllegalArgumentException("Invalid parameter passed to Threefish init - " + var2.getClass().getName());
         }

         var3 = ((KeyParameter)var2).getKey();
         var4 = null;
      }

      long[] var7 = null;
      long[] var6 = null;
      if (var3 != null) {
         if (var3.length != this.blocksizeBytes) {
            throw new IllegalArgumentException("Threefish key must be same size as block (" + this.blocksizeBytes + " bytes)");
         }

         var7 = new long[this.blocksizeWords];
         Pack.littleEndianToLong(var3, 0, var7);
      }

      if (var4 != null) {
         if (var4.length != 16) {
            throw new IllegalArgumentException("Threefish tweak must be 16 bytes");
         }

         var6 = new long[2];
         Pack.littleEndianToLong(var4, 0, var6);
      }

      this.init(var1, var7, var6);
      CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties(this.getAlgorithmName(), 256, var2, Utils.getPurpose(var1)));
   }

   public void init(boolean var1, long[] var2, long[] var3) {
      this.forEncryption = var1;
      if (var2 != null) {
         this.setKey(var2);
      }

      if (var3 != null) {
         this.setTweak(var3);
      }
   }

   private void setKey(long[] var1) {
      if (var1.length != this.blocksizeWords) {
         throw new IllegalArgumentException("Threefish key must be same size as block (" + this.blocksizeWords + " words)");
      } else {
         long var2 = 2004413935125273122L;

         for (int var4 = 0; var4 < this.blocksizeWords; var4++) {
            this.kw[var4] = var1[var4];
            var2 ^= this.kw[var4];
         }

         this.kw[this.blocksizeWords] = var2;
         System.arraycopy(this.kw, 0, this.kw, this.blocksizeWords + 1, this.blocksizeWords);
      }
   }

   private void setTweak(long[] var1) {
      if (var1.length != 2) {
         throw new IllegalArgumentException("Tweak must be 2 words.");
      } else {
         this.t[0] = var1[0];
         this.t[1] = var1[1];
         this.t[2] = this.t[0] ^ this.t[1];
         this.t[3] = this.t[0];
         this.t[4] = this.t[1];
      }
   }

   @Override
   public String getAlgorithmName() {
      return "Threefish-" + this.blocksizeBytes * 8;
   }

   @Override
   public int getBlockSize() {
      return this.blocksizeBytes;
   }

   @Override
   public void reset() {
   }

   @Override
   public int processBlock(byte[] var1, int var2, byte[] var3, int var4) throws DataLengthException, IllegalStateException {
      if (var2 + this.blocksizeBytes > var1.length) {
         throw new DataLengthException("Input buffer too short");
      } else if (var4 + this.blocksizeBytes > var3.length) {
         throw new OutputLengthException("Output buffer too short");
      } else {
         Pack.littleEndianToLong(var1, var2, this.currentBlock);
         this.processBlock(this.currentBlock, this.currentBlock);
         Pack.longToLittleEndian(this.currentBlock, var3, var4);
         return this.blocksizeBytes;
      }
   }

   public int processBlock(long[] var1, long[] var2) throws DataLengthException, IllegalStateException {
      if (this.kw[this.blocksizeWords] == 0L) {
         throw new IllegalStateException("Threefish engine not initialised");
      } else if (var1.length != this.blocksizeWords) {
         throw new DataLengthException("Input buffer too short");
      } else if (var2.length != this.blocksizeWords) {
         throw new OutputLengthException("Output buffer too short");
      } else {
         if (this.forEncryption) {
            this.cipher.encryptBlock(var1, var2);
         } else {
            this.cipher.decryptBlock(var1, var2);
         }

         return this.blocksizeWords;
      }
   }

   /** @deprecated */
   public static long bytesToWord(byte[] var0, int var1) {
      return Pack.littleEndianToLong(var0, var1);
   }

   /** @deprecated */
   public static void wordToBytes(long var0, byte[] var2, int var3) {
      Pack.longToLittleEndian(var0, var2, var3);
   }

   static long rotlXor(long var0, int var2, long var3) {
      return (var0 << var2 | var0 >>> -var2) ^ var3;
   }

   static long xorRotr(long var0, int var2, long var3) {
      long var5 = var0 ^ var3;
      return var5 >>> var2 | var5 << -var2;
   }

   static {
      for (int var0 = 0; var0 < MOD9.length; var0++) {
         MOD17[var0] = var0 % 17;
         MOD9[var0] = var0 % 9;
         MOD5[var0] = var0 % 5;
         MOD3[var0] = var0 % 3;
      }
   }

   private static final class Threefish1024Cipher extends ThreefishEngine.ThreefishCipher {
      private static final int ROTATION_0_0 = 24;
      private static final int ROTATION_0_1 = 13;
      private static final int ROTATION_0_2 = 8;
      private static final int ROTATION_0_3 = 47;
      private static final int ROTATION_0_4 = 8;
      private static final int ROTATION_0_5 = 17;
      private static final int ROTATION_0_6 = 22;
      private static final int ROTATION_0_7 = 37;
      private static final int ROTATION_1_0 = 38;
      private static final int ROTATION_1_1 = 19;
      private static final int ROTATION_1_2 = 10;
      private static final int ROTATION_1_3 = 55;
      private static final int ROTATION_1_4 = 49;
      private static final int ROTATION_1_5 = 18;
      private static final int ROTATION_1_6 = 23;
      private static final int ROTATION_1_7 = 52;
      private static final int ROTATION_2_0 = 33;
      private static final int ROTATION_2_1 = 4;
      private static final int ROTATION_2_2 = 51;
      private static final int ROTATION_2_3 = 13;
      private static final int ROTATION_2_4 = 34;
      private static final int ROTATION_2_5 = 41;
      private static final int ROTATION_2_6 = 59;
      private static final int ROTATION_2_7 = 17;
      private static final int ROTATION_3_0 = 5;
      private static final int ROTATION_3_1 = 20;
      private static final int ROTATION_3_2 = 48;
      private static final int ROTATION_3_3 = 41;
      private static final int ROTATION_3_4 = 47;
      private static final int ROTATION_3_5 = 28;
      private static final int ROTATION_3_6 = 16;
      private static final int ROTATION_3_7 = 25;
      private static final int ROTATION_4_0 = 41;
      private static final int ROTATION_4_1 = 9;
      private static final int ROTATION_4_2 = 37;
      private static final int ROTATION_4_3 = 31;
      private static final int ROTATION_4_4 = 12;
      private static final int ROTATION_4_5 = 47;
      private static final int ROTATION_4_6 = 44;
      private static final int ROTATION_4_7 = 30;
      private static final int ROTATION_5_0 = 16;
      private static final int ROTATION_5_1 = 34;
      private static final int ROTATION_5_2 = 56;
      private static final int ROTATION_5_3 = 51;
      private static final int ROTATION_5_4 = 4;
      private static final int ROTATION_5_5 = 53;
      private static final int ROTATION_5_6 = 42;
      private static final int ROTATION_5_7 = 41;
      private static final int ROTATION_6_0 = 31;
      private static final int ROTATION_6_1 = 44;
      private static final int ROTATION_6_2 = 47;
      private static final int ROTATION_6_3 = 46;
      private static final int ROTATION_6_4 = 19;
      private static final int ROTATION_6_5 = 42;
      private static final int ROTATION_6_6 = 44;
      private static final int ROTATION_6_7 = 25;
      private static final int ROTATION_7_0 = 9;
      private static final int ROTATION_7_1 = 48;
      private static final int ROTATION_7_2 = 35;
      private static final int ROTATION_7_3 = 52;
      private static final int ROTATION_7_4 = 23;
      private static final int ROTATION_7_5 = 31;
      private static final int ROTATION_7_6 = 37;
      private static final int ROTATION_7_7 = 20;

      public Threefish1024Cipher(long[] var1, long[] var2) {
         super(var1, var2);
      }

      @Override
      void encryptBlock(long[] var1, long[] var2) {
         long[] var3 = this.kw;
         long[] var4 = this.t;
         int[] var5 = ThreefishEngine.MOD17;
         int[] var6 = ThreefishEngine.MOD3;
         if (var3.length != 33) {
            throw new IllegalArgumentException();
         } else if (var4.length != 5) {
            throw new IllegalArgumentException();
         } else {
            long var7 = var1[0];
            long var9 = var1[1];
            long var11 = var1[2];
            long var13 = var1[3];
            long var15 = var1[4];
            long var17 = var1[5];
            long var19 = var1[6];
            long var21 = var1[7];
            long var23 = var1[8];
            long var25 = var1[9];
            long var27 = var1[10];
            long var29 = var1[11];
            long var31 = var1[12];
            long var33 = var1[13];
            long var35 = var1[14];
            long var37 = var1[15];
            var7 += var3[0];
            var9 += var3[1];
            var11 += var3[2];
            var13 += var3[3];
            var15 += var3[4];
            var17 += var3[5];
            var19 += var3[6];
            var21 += var3[7];
            var23 += var3[8];
            var25 += var3[9];
            var27 += var3[10];
            var29 += var3[11];
            var31 += var3[12];
            var33 += var3[13] + var4[0];
            var35 += var3[14] + var4[1];
            var37 += var3[15];

            for (byte var39 = 1; var39 < 20; var39 += 2) {
               int var40 = var5[var39];
               int var41 = var6[var39];
               long var43;
               var9 = ThreefishEngine.rotlXor(var9, 24, var43 = var7 + var9);
               long var63;
               var13 = ThreefishEngine.rotlXor(var13, 13, var63 = var11 + var13);
               long var83;
               var17 = ThreefishEngine.rotlXor(var17, 8, var83 = var15 + var17);
               long var103;
               var21 = ThreefishEngine.rotlXor(var21, 47, var103 = var19 + var21);
               long var123;
               var25 = ThreefishEngine.rotlXor(var25, 8, var123 = var23 + var25);
               long var143;
               var29 = ThreefishEngine.rotlXor(var29, 17, var143 = var27 + var29);
               long var163;
               var33 = ThreefishEngine.rotlXor(var33, 22, var163 = var31 + var33);
               long var183;
               var37 = ThreefishEngine.rotlXor(var37, 37, var183 = var35 + var37);
               var25 = ThreefishEngine.rotlXor(var25, 38, var7 = var43 + var25);
               var33 = ThreefishEngine.rotlXor(var33, 19, var11 = var63 + var33);
               var29 = ThreefishEngine.rotlXor(var29, 10, var19 = var103 + var29);
               var37 = ThreefishEngine.rotlXor(var37, 55, var15 = var83 + var37);
               var21 = ThreefishEngine.rotlXor(var21, 49, var27 = var143 + var21);
               var13 = ThreefishEngine.rotlXor(var13, 18, var31 = var163 + var13);
               var17 = ThreefishEngine.rotlXor(var17, 23, var35 = var183 + var17);
               var9 = ThreefishEngine.rotlXor(var9, 52, var23 = var123 + var9);
               long var45;
               var21 = ThreefishEngine.rotlXor(var21, 33, var45 = var7 + var21);
               long var65;
               var17 = ThreefishEngine.rotlXor(var17, 4, var65 = var11 + var17);
               long var85;
               var13 = ThreefishEngine.rotlXor(var13, 51, var85 = var15 + var13);
               long var105;
               var9 = ThreefishEngine.rotlXor(var9, 13, var105 = var19 + var9);
               long var165;
               var37 = ThreefishEngine.rotlXor(var37, 34, var165 = var31 + var37);
               long var185;
               var33 = ThreefishEngine.rotlXor(var33, 41, var185 = var35 + var33);
               long var125;
               var29 = ThreefishEngine.rotlXor(var29, 59, var125 = var23 + var29);
               long var145;
               var25 = ThreefishEngine.rotlXor(var25, 17, var145 = var27 + var25);
               var37 = ThreefishEngine.rotlXor(var37, 5, var7 = var45 + var37);
               var29 = ThreefishEngine.rotlXor(var29, 20, var11 = var65 + var29);
               var33 = ThreefishEngine.rotlXor(var33, 48, var19 = var105 + var33);
               var25 = ThreefishEngine.rotlXor(var25, 41, var15 = var85 + var25);
               var9 = ThreefishEngine.rotlXor(var9, 47, var35 = var185 + var9);
               var17 = ThreefishEngine.rotlXor(var17, 28, var23 = var125 + var17);
               var13 = ThreefishEngine.rotlXor(var13, 16, var27 = var145 + var13);
               var21 = ThreefishEngine.rotlXor(var21, 25, var31 = var165 + var21);
               var7 += var3[var40];
               var9 += var3[var40 + 1];
               var11 += var3[var40 + 2];
               var13 += var3[var40 + 3];
               var15 += var3[var40 + 4];
               var17 += var3[var40 + 5];
               var19 += var3[var40 + 6];
               var21 += var3[var40 + 7];
               var23 += var3[var40 + 8];
               var25 += var3[var40 + 9];
               var27 += var3[var40 + 10];
               var29 += var3[var40 + 11];
               var31 += var3[var40 + 12];
               var33 += var3[var40 + 13] + var4[var41];
               var35 += var3[var40 + 14] + var4[var41 + 1];
               var37 += var3[var40 + 15] + var39;
               long var48;
               var9 = ThreefishEngine.rotlXor(var9, 41, var48 = var7 + var9);
               long var68;
               var13 = ThreefishEngine.rotlXor(var13, 9, var68 = var11 + var13);
               long var88;
               var17 = ThreefishEngine.rotlXor(var17, 37, var88 = var15 + var17);
               long var108;
               var21 = ThreefishEngine.rotlXor(var21, 31, var108 = var19 + var21);
               long var128;
               var25 = ThreefishEngine.rotlXor(var25, 12, var128 = var23 + var25);
               long var148;
               var29 = ThreefishEngine.rotlXor(var29, 47, var148 = var27 + var29);
               long var168;
               var33 = ThreefishEngine.rotlXor(var33, 44, var168 = var31 + var33);
               long var188;
               var37 = ThreefishEngine.rotlXor(var37, 30, var188 = var35 + var37);
               var25 = ThreefishEngine.rotlXor(var25, 16, var7 = var48 + var25);
               var33 = ThreefishEngine.rotlXor(var33, 34, var11 = var68 + var33);
               var29 = ThreefishEngine.rotlXor(var29, 56, var19 = var108 + var29);
               var37 = ThreefishEngine.rotlXor(var37, 51, var15 = var88 + var37);
               var21 = ThreefishEngine.rotlXor(var21, 4, var27 = var148 + var21);
               var13 = ThreefishEngine.rotlXor(var13, 53, var31 = var168 + var13);
               var17 = ThreefishEngine.rotlXor(var17, 42, var35 = var188 + var17);
               var9 = ThreefishEngine.rotlXor(var9, 41, var23 = var128 + var9);
               long var50;
               var21 = ThreefishEngine.rotlXor(var21, 31, var50 = var7 + var21);
               long var70;
               var17 = ThreefishEngine.rotlXor(var17, 44, var70 = var11 + var17);
               long var90;
               var13 = ThreefishEngine.rotlXor(var13, 47, var90 = var15 + var13);
               long var110;
               var9 = ThreefishEngine.rotlXor(var9, 46, var110 = var19 + var9);
               long var170;
               var37 = ThreefishEngine.rotlXor(var37, 19, var170 = var31 + var37);
               long var190;
               var33 = ThreefishEngine.rotlXor(var33, 42, var190 = var35 + var33);
               long var130;
               var29 = ThreefishEngine.rotlXor(var29, 44, var130 = var23 + var29);
               long var150;
               var25 = ThreefishEngine.rotlXor(var25, 25, var150 = var27 + var25);
               var37 = ThreefishEngine.rotlXor(var37, 9, var7 = var50 + var37);
               var29 = ThreefishEngine.rotlXor(var29, 48, var11 = var70 + var29);
               var33 = ThreefishEngine.rotlXor(var33, 35, var19 = var110 + var33);
               var25 = ThreefishEngine.rotlXor(var25, 52, var15 = var90 + var25);
               var9 = ThreefishEngine.rotlXor(var9, 23, var35 = var190 + var9);
               var17 = ThreefishEngine.rotlXor(var17, 31, var23 = var130 + var17);
               var13 = ThreefishEngine.rotlXor(var13, 37, var27 = var150 + var13);
               var21 = ThreefishEngine.rotlXor(var21, 20, var31 = var170 + var21);
               var7 += var3[var40 + 1];
               var9 += var3[var40 + 2];
               var11 += var3[var40 + 3];
               var13 += var3[var40 + 4];
               var15 += var3[var40 + 5];
               var17 += var3[var40 + 6];
               var19 += var3[var40 + 7];
               var21 += var3[var40 + 8];
               var23 += var3[var40 + 9];
               var25 += var3[var40 + 10];
               var27 += var3[var40 + 11];
               var29 += var3[var40 + 12];
               var31 += var3[var40 + 13];
               var33 += var3[var40 + 14] + var4[var41 + 1];
               var35 += var3[var40 + 15] + var4[var41 + 2];
               var37 += var3[var40 + 16] + var39 + 1L;
            }

            var2[0] = var7;
            var2[1] = var9;
            var2[2] = var11;
            var2[3] = var13;
            var2[4] = var15;
            var2[5] = var17;
            var2[6] = var19;
            var2[7] = var21;
            var2[8] = var23;
            var2[9] = var25;
            var2[10] = var27;
            var2[11] = var29;
            var2[12] = var31;
            var2[13] = var33;
            var2[14] = var35;
            var2[15] = var37;
         }
      }

      @Override
      void decryptBlock(long[] var1, long[] var2) {
         long[] var3 = this.kw;
         long[] var4 = this.t;
         int[] var5 = ThreefishEngine.MOD17;
         int[] var6 = ThreefishEngine.MOD3;
         if (var3.length != 33) {
            throw new IllegalArgumentException();
         } else if (var4.length != 5) {
            throw new IllegalArgumentException();
         } else {
            long var7 = var1[0];
            long var9 = var1[1];
            long var11 = var1[2];
            long var13 = var1[3];
            long var15 = var1[4];
            long var17 = var1[5];
            long var19 = var1[6];
            long var21 = var1[7];
            long var23 = var1[8];
            long var25 = var1[9];
            long var27 = var1[10];
            long var29 = var1[11];
            long var31 = var1[12];
            long var33 = var1[13];
            long var35 = var1[14];
            long var37 = var1[15];

            for (byte var39 = 19; var39 >= 1; var39 -= 2) {
               int var40 = var5[var39];
               int var41 = var6[var39];
               var7 -= var3[var40 + 1];
               long var52 = var9 - var3[var40 + 2];
               var11 -= var3[var40 + 3];
               long var72 = var13 - var3[var40 + 4];
               var15 -= var3[var40 + 5];
               long var92 = var17 - var3[var40 + 6];
               var19 -= var3[var40 + 7];
               long var112 = var21 - var3[var40 + 8];
               var23 -= var3[var40 + 9];
               long var132 = var25 - var3[var40 + 10];
               var27 -= var3[var40 + 11];
               long var152 = var29 - var3[var40 + 12];
               var31 -= var3[var40 + 13];
               long var172 = var33 - (var3[var40 + 14] + var4[var41 + 1]);
               var35 -= var3[var40 + 15] + var4[var41 + 2];
               long var192 = var37 - (var3[var40 + 16] + var39 + 1L);
               long var193 = ThreefishEngine.xorRotr(var192, 9, var7);
               var7 -= var193;
               long var153 = ThreefishEngine.xorRotr(var152, 48, var11);
               var11 -= var153;
               long var173 = ThreefishEngine.xorRotr(var172, 35, var19);
               var19 -= var173;
               long var133 = ThreefishEngine.xorRotr(var132, 52, var15);
               var15 -= var133;
               long var53 = ThreefishEngine.xorRotr(var52, 23, var35);
               var35 -= var53;
               long var93 = ThreefishEngine.xorRotr(var92, 31, var23);
               var23 -= var93;
               long var73 = ThreefishEngine.xorRotr(var72, 37, var27);
               var27 -= var73;
               long var113 = ThreefishEngine.xorRotr(var112, 20, var31);
               var31 -= var113;
               long var114 = ThreefishEngine.xorRotr(var113, 31, var7);
               var7 -= var114;
               long var94 = ThreefishEngine.xorRotr(var93, 44, var11);
               var11 -= var94;
               long var74 = ThreefishEngine.xorRotr(var73, 47, var15);
               var15 -= var74;
               long var54 = ThreefishEngine.xorRotr(var53, 46, var19);
               var19 -= var54;
               long var194 = ThreefishEngine.xorRotr(var193, 19, var31);
               var31 -= var194;
               long var174 = ThreefishEngine.xorRotr(var173, 42, var35);
               var35 -= var174;
               long var154 = ThreefishEngine.xorRotr(var153, 44, var23);
               var23 -= var154;
               long var134 = ThreefishEngine.xorRotr(var133, 25, var27);
               var27 -= var134;
               long var135 = ThreefishEngine.xorRotr(var134, 16, var7);
               var7 -= var135;
               long var175 = ThreefishEngine.xorRotr(var174, 34, var11);
               var11 -= var175;
               long var155 = ThreefishEngine.xorRotr(var154, 56, var19);
               var19 -= var155;
               long var195 = ThreefishEngine.xorRotr(var194, 51, var15);
               var15 -= var195;
               long var115 = ThreefishEngine.xorRotr(var114, 4, var27);
               var27 -= var115;
               long var75 = ThreefishEngine.xorRotr(var74, 53, var31);
               var31 -= var75;
               long var95 = ThreefishEngine.xorRotr(var94, 42, var35);
               var35 -= var95;
               long var55 = ThreefishEngine.xorRotr(var54, 41, var23);
               var23 -= var55;
               long var56 = ThreefishEngine.xorRotr(var55, 41, var7);
               var7 -= var56;
               long var76 = ThreefishEngine.xorRotr(var75, 9, var11);
               var11 -= var76;
               long var96 = ThreefishEngine.xorRotr(var95, 37, var15);
               var15 -= var96;
               long var116 = ThreefishEngine.xorRotr(var115, 31, var19);
               var19 -= var116;
               long var136 = ThreefishEngine.xorRotr(var135, 12, var23);
               var23 -= var136;
               long var156 = ThreefishEngine.xorRotr(var155, 47, var27);
               var27 -= var156;
               long var176 = ThreefishEngine.xorRotr(var175, 44, var31);
               var31 -= var176;
               long var196 = ThreefishEngine.xorRotr(var195, 30, var35);
               var35 -= var196;
               var7 -= var3[var40];
               long var57 = var56 - var3[var40 + 1];
               var11 -= var3[var40 + 2];
               long var77 = var76 - var3[var40 + 3];
               var15 -= var3[var40 + 4];
               long var97 = var96 - var3[var40 + 5];
               var19 -= var3[var40 + 6];
               long var117 = var116 - var3[var40 + 7];
               var23 -= var3[var40 + 8];
               long var137 = var136 - var3[var40 + 9];
               var27 -= var3[var40 + 10];
               long var157 = var156 - var3[var40 + 11];
               var31 -= var3[var40 + 12];
               long var177 = var176 - (var3[var40 + 13] + var4[var41]);
               var35 -= var3[var40 + 14] + var4[var41 + 1];
               long var197 = var196 - (var3[var40 + 15] + var39);
               long var198 = ThreefishEngine.xorRotr(var197, 5, var7);
               var7 -= var198;
               long var158 = ThreefishEngine.xorRotr(var157, 20, var11);
               var11 -= var158;
               long var178 = ThreefishEngine.xorRotr(var177, 48, var19);
               var19 -= var178;
               long var138 = ThreefishEngine.xorRotr(var137, 41, var15);
               var15 -= var138;
               long var58 = ThreefishEngine.xorRotr(var57, 47, var35);
               var35 -= var58;
               long var98 = ThreefishEngine.xorRotr(var97, 28, var23);
               var23 -= var98;
               long var78 = ThreefishEngine.xorRotr(var77, 16, var27);
               var27 -= var78;
               long var118 = ThreefishEngine.xorRotr(var117, 25, var31);
               var31 -= var118;
               long var119 = ThreefishEngine.xorRotr(var118, 33, var7);
               var7 -= var119;
               long var99 = ThreefishEngine.xorRotr(var98, 4, var11);
               var11 -= var99;
               long var79 = ThreefishEngine.xorRotr(var78, 51, var15);
               var15 -= var79;
               long var59 = ThreefishEngine.xorRotr(var58, 13, var19);
               var19 -= var59;
               long var199 = ThreefishEngine.xorRotr(var198, 34, var31);
               var31 -= var199;
               long var179 = ThreefishEngine.xorRotr(var178, 41, var35);
               var35 -= var179;
               long var159 = ThreefishEngine.xorRotr(var158, 59, var23);
               var23 -= var159;
               long var139 = ThreefishEngine.xorRotr(var138, 17, var27);
               var27 -= var139;
               long var140 = ThreefishEngine.xorRotr(var139, 38, var7);
               var7 -= var140;
               long var180 = ThreefishEngine.xorRotr(var179, 19, var11);
               var11 -= var180;
               long var160 = ThreefishEngine.xorRotr(var159, 10, var19);
               var19 -= var160;
               long var200 = ThreefishEngine.xorRotr(var199, 55, var15);
               var15 -= var200;
               long var120 = ThreefishEngine.xorRotr(var119, 49, var27);
               var27 -= var120;
               long var80 = ThreefishEngine.xorRotr(var79, 18, var31);
               var31 -= var80;
               long var100 = ThreefishEngine.xorRotr(var99, 23, var35);
               var35 -= var100;
               long var60 = ThreefishEngine.xorRotr(var59, 52, var23);
               var23 -= var60;
               var9 = ThreefishEngine.xorRotr(var60, 24, var7);
               var7 -= var9;
               var13 = ThreefishEngine.xorRotr(var80, 13, var11);
               var11 -= var13;
               var17 = ThreefishEngine.xorRotr(var100, 8, var15);
               var15 -= var17;
               var21 = ThreefishEngine.xorRotr(var120, 47, var19);
               var19 -= var21;
               var25 = ThreefishEngine.xorRotr(var140, 8, var23);
               var23 -= var25;
               var29 = ThreefishEngine.xorRotr(var160, 17, var27);
               var27 -= var29;
               var33 = ThreefishEngine.xorRotr(var180, 22, var31);
               var31 -= var33;
               var37 = ThreefishEngine.xorRotr(var200, 37, var35);
               var35 -= var37;
            }

            var7 -= var3[0];
            var9 -= var3[1];
            var11 -= var3[2];
            var13 -= var3[3];
            var15 -= var3[4];
            var17 -= var3[5];
            var19 -= var3[6];
            var21 -= var3[7];
            var23 -= var3[8];
            var25 -= var3[9];
            var27 -= var3[10];
            var29 -= var3[11];
            var31 -= var3[12];
            var33 -= var3[13] + var4[0];
            var35 -= var3[14] + var4[1];
            var37 -= var3[15];
            var2[0] = var7;
            var2[1] = var9;
            var2[2] = var11;
            var2[3] = var13;
            var2[4] = var15;
            var2[5] = var17;
            var2[6] = var19;
            var2[7] = var21;
            var2[8] = var23;
            var2[9] = var25;
            var2[10] = var27;
            var2[11] = var29;
            var2[12] = var31;
            var2[13] = var33;
            var2[14] = var35;
            var2[15] = var37;
         }
      }
   }

   private static final class Threefish256Cipher extends ThreefishEngine.ThreefishCipher {
      private static final int ROTATION_0_0 = 14;
      private static final int ROTATION_0_1 = 16;
      private static final int ROTATION_1_0 = 52;
      private static final int ROTATION_1_1 = 57;
      private static final int ROTATION_2_0 = 23;
      private static final int ROTATION_2_1 = 40;
      private static final int ROTATION_3_0 = 5;
      private static final int ROTATION_3_1 = 37;
      private static final int ROTATION_4_0 = 25;
      private static final int ROTATION_4_1 = 33;
      private static final int ROTATION_5_0 = 46;
      private static final int ROTATION_5_1 = 12;
      private static final int ROTATION_6_0 = 58;
      private static final int ROTATION_6_1 = 22;
      private static final int ROTATION_7_0 = 32;
      private static final int ROTATION_7_1 = 32;

      public Threefish256Cipher(long[] var1, long[] var2) {
         super(var1, var2);
      }

      @Override
      void encryptBlock(long[] var1, long[] var2) {
         long[] var3 = this.kw;
         long[] var4 = this.t;
         int[] var5 = ThreefishEngine.MOD5;
         int[] var6 = ThreefishEngine.MOD3;
         if (var3.length != 9) {
            throw new IllegalArgumentException();
         } else if (var4.length != 5) {
            throw new IllegalArgumentException();
         } else {
            long var7 = var1[0];
            long var9 = var1[1];
            long var11 = var1[2];
            long var13 = var1[3];
            var7 += var3[0];
            var9 += var3[1] + var4[0];
            var11 += var3[2] + var4[1];
            var13 += var3[3];

            for (byte var15 = 1; var15 < 18; var15 += 2) {
               int var16 = var5[var15];
               int var17 = var6[var15];
               long var19;
               var9 = ThreefishEngine.rotlXor(var9, 14, var19 = var7 + var9);
               long var39;
               var13 = ThreefishEngine.rotlXor(var13, 16, var39 = var11 + var13);
               var13 = ThreefishEngine.rotlXor(var13, 52, var7 = var19 + var13);
               var9 = ThreefishEngine.rotlXor(var9, 57, var11 = var39 + var9);
               long var21;
               var9 = ThreefishEngine.rotlXor(var9, 23, var21 = var7 + var9);
               long var41;
               var13 = ThreefishEngine.rotlXor(var13, 40, var41 = var11 + var13);
               var13 = ThreefishEngine.rotlXor(var13, 5, var7 = var21 + var13);
               var9 = ThreefishEngine.rotlXor(var9, 37, var11 = var41 + var9);
               var7 += var3[var16];
               var9 += var3[var16 + 1] + var4[var17];
               var11 += var3[var16 + 2] + var4[var17 + 1];
               var13 += var3[var16 + 3] + var15;
               long var24;
               var9 = ThreefishEngine.rotlXor(var9, 25, var24 = var7 + var9);
               long var44;
               var13 = ThreefishEngine.rotlXor(var13, 33, var44 = var11 + var13);
               var13 = ThreefishEngine.rotlXor(var13, 46, var7 = var24 + var13);
               var9 = ThreefishEngine.rotlXor(var9, 12, var11 = var44 + var9);
               long var26;
               var9 = ThreefishEngine.rotlXor(var9, 58, var26 = var7 + var9);
               long var46;
               var13 = ThreefishEngine.rotlXor(var13, 22, var46 = var11 + var13);
               var13 = ThreefishEngine.rotlXor(var13, 32, var7 = var26 + var13);
               var9 = ThreefishEngine.rotlXor(var9, 32, var11 = var46 + var9);
               var7 += var3[var16 + 1];
               var9 += var3[var16 + 2] + var4[var17 + 1];
               var11 += var3[var16 + 3] + var4[var17 + 2];
               var13 += var3[var16 + 4] + var15 + 1L;
            }

            var2[0] = var7;
            var2[1] = var9;
            var2[2] = var11;
            var2[3] = var13;
         }
      }

      @Override
      void decryptBlock(long[] var1, long[] var2) {
         long[] var3 = this.kw;
         long[] var4 = this.t;
         int[] var5 = ThreefishEngine.MOD5;
         int[] var6 = ThreefishEngine.MOD3;
         if (var3.length != 9) {
            throw new IllegalArgumentException();
         } else if (var4.length != 5) {
            throw new IllegalArgumentException();
         } else {
            long var7 = var1[0];
            long var9 = var1[1];
            long var11 = var1[2];
            long var13 = var1[3];

            for (byte var15 = 17; var15 >= 1; var15 -= 2) {
               int var16 = var5[var15];
               int var17 = var6[var15];
               var7 -= var3[var16 + 1];
               long var28 = var9 - (var3[var16 + 2] + var4[var17 + 1]);
               var11 -= var3[var16 + 3] + var4[var17 + 2];
               long var48 = var13 - (var3[var16 + 4] + var15 + 1L);
               long var49 = ThreefishEngine.xorRotr(var48, 32, var7);
               var7 -= var49;
               long var29 = ThreefishEngine.xorRotr(var28, 32, var11);
               var11 -= var29;
               long var30 = ThreefishEngine.xorRotr(var29, 58, var7);
               var7 -= var30;
               long var50 = ThreefishEngine.xorRotr(var49, 22, var11);
               var11 -= var50;
               long var51 = ThreefishEngine.xorRotr(var50, 46, var7);
               var7 -= var51;
               long var31 = ThreefishEngine.xorRotr(var30, 12, var11);
               var11 -= var31;
               long var32 = ThreefishEngine.xorRotr(var31, 25, var7);
               var7 -= var32;
               long var52 = ThreefishEngine.xorRotr(var51, 33, var11);
               var11 -= var52;
               var7 -= var3[var16];
               long var33 = var32 - (var3[var16 + 1] + var4[var17]);
               var11 -= var3[var16 + 2] + var4[var17 + 1];
               long var53 = var52 - (var3[var16 + 3] + var15);
               long var54 = ThreefishEngine.xorRotr(var53, 5, var7);
               var7 -= var54;
               long var34 = ThreefishEngine.xorRotr(var33, 37, var11);
               var11 -= var34;
               long var35 = ThreefishEngine.xorRotr(var34, 23, var7);
               var7 -= var35;
               long var55 = ThreefishEngine.xorRotr(var54, 40, var11);
               var11 -= var55;
               long var56 = ThreefishEngine.xorRotr(var55, 52, var7);
               var7 -= var56;
               long var36 = ThreefishEngine.xorRotr(var35, 57, var11);
               var11 -= var36;
               var9 = ThreefishEngine.xorRotr(var36, 14, var7);
               var7 -= var9;
               var13 = ThreefishEngine.xorRotr(var56, 16, var11);
               var11 -= var13;
            }

            var7 -= var3[0];
            var9 -= var3[1] + var4[0];
            var11 -= var3[2] + var4[1];
            var13 -= var3[3];
            var2[0] = var7;
            var2[1] = var9;
            var2[2] = var11;
            var2[3] = var13;
         }
      }
   }

   private static final class Threefish512Cipher extends ThreefishEngine.ThreefishCipher {
      private static final int ROTATION_0_0 = 46;
      private static final int ROTATION_0_1 = 36;
      private static final int ROTATION_0_2 = 19;
      private static final int ROTATION_0_3 = 37;
      private static final int ROTATION_1_0 = 33;
      private static final int ROTATION_1_1 = 27;
      private static final int ROTATION_1_2 = 14;
      private static final int ROTATION_1_3 = 42;
      private static final int ROTATION_2_0 = 17;
      private static final int ROTATION_2_1 = 49;
      private static final int ROTATION_2_2 = 36;
      private static final int ROTATION_2_3 = 39;
      private static final int ROTATION_3_0 = 44;
      private static final int ROTATION_3_1 = 9;
      private static final int ROTATION_3_2 = 54;
      private static final int ROTATION_3_3 = 56;
      private static final int ROTATION_4_0 = 39;
      private static final int ROTATION_4_1 = 30;
      private static final int ROTATION_4_2 = 34;
      private static final int ROTATION_4_3 = 24;
      private static final int ROTATION_5_0 = 13;
      private static final int ROTATION_5_1 = 50;
      private static final int ROTATION_5_2 = 10;
      private static final int ROTATION_5_3 = 17;
      private static final int ROTATION_6_0 = 25;
      private static final int ROTATION_6_1 = 29;
      private static final int ROTATION_6_2 = 39;
      private static final int ROTATION_6_3 = 43;
      private static final int ROTATION_7_0 = 8;
      private static final int ROTATION_7_1 = 35;
      private static final int ROTATION_7_2 = 56;
      private static final int ROTATION_7_3 = 22;

      protected Threefish512Cipher(long[] var1, long[] var2) {
         super(var1, var2);
      }

      @Override
      public void encryptBlock(long[] var1, long[] var2) {
         long[] var3 = this.kw;
         long[] var4 = this.t;
         int[] var5 = ThreefishEngine.MOD9;
         int[] var6 = ThreefishEngine.MOD3;
         if (var3.length != 17) {
            throw new IllegalArgumentException();
         } else if (var4.length != 5) {
            throw new IllegalArgumentException();
         } else {
            long var7 = var1[0];
            long var9 = var1[1];
            long var11 = var1[2];
            long var13 = var1[3];
            long var15 = var1[4];
            long var17 = var1[5];
            long var19 = var1[6];
            long var21 = var1[7];
            var7 += var3[0];
            var9 += var3[1];
            var11 += var3[2];
            var13 += var3[3];
            var15 += var3[4];
            var17 += var3[5] + var4[0];
            var19 += var3[6] + var4[1];
            var21 += var3[7];

            for (byte var23 = 1; var23 < 18; var23 += 2) {
               int var24 = var5[var23];
               int var25 = var6[var23];
               long var27;
               var9 = ThreefishEngine.rotlXor(var9, 46, var27 = var7 + var9);
               long var47;
               var13 = ThreefishEngine.rotlXor(var13, 36, var47 = var11 + var13);
               long var67;
               var17 = ThreefishEngine.rotlXor(var17, 19, var67 = var15 + var17);
               long var87;
               var21 = ThreefishEngine.rotlXor(var21, 37, var87 = var19 + var21);
               var9 = ThreefishEngine.rotlXor(var9, 33, var11 = var47 + var9);
               var21 = ThreefishEngine.rotlXor(var21, 27, var15 = var67 + var21);
               var17 = ThreefishEngine.rotlXor(var17, 14, var19 = var87 + var17);
               var13 = ThreefishEngine.rotlXor(var13, 42, var7 = var27 + var13);
               long var69;
               var9 = ThreefishEngine.rotlXor(var9, 17, var69 = var15 + var9);
               long var89;
               var13 = ThreefishEngine.rotlXor(var13, 49, var89 = var19 + var13);
               long var29;
               var17 = ThreefishEngine.rotlXor(var17, 36, var29 = var7 + var17);
               long var49;
               var21 = ThreefishEngine.rotlXor(var21, 39, var49 = var11 + var21);
               var9 = ThreefishEngine.rotlXor(var9, 44, var19 = var89 + var9);
               var21 = ThreefishEngine.rotlXor(var21, 9, var7 = var29 + var21);
               var17 = ThreefishEngine.rotlXor(var17, 54, var11 = var49 + var17);
               var13 = ThreefishEngine.rotlXor(var13, 56, var15 = var69 + var13);
               var7 += var3[var24];
               var9 += var3[var24 + 1];
               var11 += var3[var24 + 2];
               var13 += var3[var24 + 3];
               var15 += var3[var24 + 4];
               var17 += var3[var24 + 5] + var4[var25];
               var19 += var3[var24 + 6] + var4[var25 + 1];
               var21 += var3[var24 + 7] + var23;
               long var32;
               var9 = ThreefishEngine.rotlXor(var9, 39, var32 = var7 + var9);
               long var52;
               var13 = ThreefishEngine.rotlXor(var13, 30, var52 = var11 + var13);
               long var72;
               var17 = ThreefishEngine.rotlXor(var17, 34, var72 = var15 + var17);
               long var92;
               var21 = ThreefishEngine.rotlXor(var21, 24, var92 = var19 + var21);
               var9 = ThreefishEngine.rotlXor(var9, 13, var11 = var52 + var9);
               var21 = ThreefishEngine.rotlXor(var21, 50, var15 = var72 + var21);
               var17 = ThreefishEngine.rotlXor(var17, 10, var19 = var92 + var17);
               var13 = ThreefishEngine.rotlXor(var13, 17, var7 = var32 + var13);
               long var74;
               var9 = ThreefishEngine.rotlXor(var9, 25, var74 = var15 + var9);
               long var94;
               var13 = ThreefishEngine.rotlXor(var13, 29, var94 = var19 + var13);
               long var34;
               var17 = ThreefishEngine.rotlXor(var17, 39, var34 = var7 + var17);
               long var54;
               var21 = ThreefishEngine.rotlXor(var21, 43, var54 = var11 + var21);
               var9 = ThreefishEngine.rotlXor(var9, 8, var19 = var94 + var9);
               var21 = ThreefishEngine.rotlXor(var21, 35, var7 = var34 + var21);
               var17 = ThreefishEngine.rotlXor(var17, 56, var11 = var54 + var17);
               var13 = ThreefishEngine.rotlXor(var13, 22, var15 = var74 + var13);
               var7 += var3[var24 + 1];
               var9 += var3[var24 + 2];
               var11 += var3[var24 + 3];
               var13 += var3[var24 + 4];
               var15 += var3[var24 + 5];
               var17 += var3[var24 + 6] + var4[var25 + 1];
               var19 += var3[var24 + 7] + var4[var25 + 2];
               var21 += var3[var24 + 8] + var23 + 1L;
            }

            var2[0] = var7;
            var2[1] = var9;
            var2[2] = var11;
            var2[3] = var13;
            var2[4] = var15;
            var2[5] = var17;
            var2[6] = var19;
            var2[7] = var21;
         }
      }

      @Override
      public void decryptBlock(long[] var1, long[] var2) {
         long[] var3 = this.kw;
         long[] var4 = this.t;
         int[] var5 = ThreefishEngine.MOD9;
         int[] var6 = ThreefishEngine.MOD3;
         if (var3.length != 17) {
            throw new IllegalArgumentException();
         } else if (var4.length != 5) {
            throw new IllegalArgumentException();
         } else {
            long var7 = var1[0];
            long var9 = var1[1];
            long var11 = var1[2];
            long var13 = var1[3];
            long var15 = var1[4];
            long var17 = var1[5];
            long var19 = var1[6];
            long var21 = var1[7];

            for (byte var23 = 17; var23 >= 1; var23 -= 2) {
               int var24 = var5[var23];
               int var25 = var6[var23];
               var7 -= var3[var24 + 1];
               long var36 = var9 - var3[var24 + 2];
               var11 -= var3[var24 + 3];
               long var56 = var13 - var3[var24 + 4];
               var15 -= var3[var24 + 5];
               long var76 = var17 - (var3[var24 + 6] + var4[var25 + 1]);
               var19 -= var3[var24 + 7] + var4[var25 + 2];
               long var96 = var21 - (var3[var24 + 8] + var23 + 1L);
               long var37 = ThreefishEngine.xorRotr(var36, 8, var19);
               var19 -= var37;
               long var97 = ThreefishEngine.xorRotr(var96, 35, var7);
               var7 -= var97;
               long var77 = ThreefishEngine.xorRotr(var76, 56, var11);
               var11 -= var77;
               long var57 = ThreefishEngine.xorRotr(var56, 22, var15);
               var15 -= var57;
               long var38 = ThreefishEngine.xorRotr(var37, 25, var15);
               var15 -= var38;
               long var58 = ThreefishEngine.xorRotr(var57, 29, var19);
               var19 -= var58;
               long var78 = ThreefishEngine.xorRotr(var77, 39, var7);
               var7 -= var78;
               long var98 = ThreefishEngine.xorRotr(var97, 43, var11);
               var11 -= var98;
               long var39 = ThreefishEngine.xorRotr(var38, 13, var11);
               var11 -= var39;
               long var99 = ThreefishEngine.xorRotr(var98, 50, var15);
               var15 -= var99;
               long var79 = ThreefishEngine.xorRotr(var78, 10, var19);
               var19 -= var79;
               long var59 = ThreefishEngine.xorRotr(var58, 17, var7);
               var7 -= var59;
               long var40 = ThreefishEngine.xorRotr(var39, 39, var7);
               var7 -= var40;
               long var60 = ThreefishEngine.xorRotr(var59, 30, var11);
               var11 -= var60;
               long var80 = ThreefishEngine.xorRotr(var79, 34, var15);
               var15 -= var80;
               long var100 = ThreefishEngine.xorRotr(var99, 24, var19);
               var19 -= var100;
               var7 -= var3[var24];
               long var41 = var40 - var3[var24 + 1];
               var11 -= var3[var24 + 2];
               long var61 = var60 - var3[var24 + 3];
               var15 -= var3[var24 + 4];
               long var81 = var80 - (var3[var24 + 5] + var4[var25]);
               var19 -= var3[var24 + 6] + var4[var25 + 1];
               long var101 = var100 - (var3[var24 + 7] + var23);
               long var42 = ThreefishEngine.xorRotr(var41, 44, var19);
               var19 -= var42;
               long var102 = ThreefishEngine.xorRotr(var101, 9, var7);
               var7 -= var102;
               long var82 = ThreefishEngine.xorRotr(var81, 54, var11);
               var11 -= var82;
               long var62 = ThreefishEngine.xorRotr(var61, 56, var15);
               var15 -= var62;
               long var43 = ThreefishEngine.xorRotr(var42, 17, var15);
               var15 -= var43;
               long var63 = ThreefishEngine.xorRotr(var62, 49, var19);
               var19 -= var63;
               long var83 = ThreefishEngine.xorRotr(var82, 36, var7);
               var7 -= var83;
               long var103 = ThreefishEngine.xorRotr(var102, 39, var11);
               var11 -= var103;
               long var44 = ThreefishEngine.xorRotr(var43, 33, var11);
               var11 -= var44;
               long var104 = ThreefishEngine.xorRotr(var103, 27, var15);
               var15 -= var104;
               long var84 = ThreefishEngine.xorRotr(var83, 14, var19);
               var19 -= var84;
               long var64 = ThreefishEngine.xorRotr(var63, 42, var7);
               var7 -= var64;
               var9 = ThreefishEngine.xorRotr(var44, 46, var7);
               var7 -= var9;
               var13 = ThreefishEngine.xorRotr(var64, 36, var11);
               var11 -= var13;
               var17 = ThreefishEngine.xorRotr(var84, 19, var15);
               var15 -= var17;
               var21 = ThreefishEngine.xorRotr(var104, 37, var19);
               var19 -= var21;
            }

            var7 -= var3[0];
            var9 -= var3[1];
            var11 -= var3[2];
            var13 -= var3[3];
            var15 -= var3[4];
            var17 -= var3[5] + var4[0];
            var19 -= var3[6] + var4[1];
            var21 -= var3[7];
            var2[0] = var7;
            var2[1] = var9;
            var2[2] = var11;
            var2[3] = var13;
            var2[4] = var15;
            var2[5] = var17;
            var2[6] = var19;
            var2[7] = var21;
         }
      }
   }

   private abstract static class ThreefishCipher {
      protected final long[] t;
      protected final long[] kw;

      protected ThreefishCipher(long[] var1, long[] var2) {
         this.kw = var1;
         this.t = var2;
      }

      abstract void encryptBlock(long[] var1, long[] var2);

      abstract void decryptBlock(long[] var1, long[] var2);
   }
}
