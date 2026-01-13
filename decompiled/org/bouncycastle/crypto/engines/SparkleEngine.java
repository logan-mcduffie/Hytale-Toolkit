package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.digests.SparkleDigest;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Integers;
import org.bouncycastle.util.Pack;

public class SparkleEngine extends AEADBaseEngine {
   private static final int[] RCON = new int[]{-1209970334, -1083090816, 951376470, 844003128, -1156479509, 1333558103, -809524792, -1028445891};
   private final int[] state;
   private final int[] k;
   private final int[] npub;
   private boolean encrypted;
   private final int SPARKLE_STEPS_SLIM;
   private final int SPARKLE_STEPS_BIG;
   private final int KEY_WORDS;
   private final int TAG_WORDS;
   private final int STATE_WORDS;
   private final int RATE_WORDS;
   private final int CAP_MASK;
   private final int _A0;
   private final int _A1;
   private final int _M2;
   private final int _M3;

   public SparkleEngine(SparkleEngine.SparkleParameters var1) {
      short var2;
      short var3;
      short var4;
      short var5;
      short var6;
      switch (var1) {
         case SCHWAEMM128_128:
            var5 = 128;
            var6 = 128;
            var3 = 128;
            var2 = 256;
            var4 = 128;
            this.SPARKLE_STEPS_SLIM = 7;
            this.SPARKLE_STEPS_BIG = 10;
            this.algorithmName = "SCHWAEMM128-128";
            break;
         case SCHWAEMM256_128:
            var5 = 128;
            var6 = 256;
            var3 = 128;
            var2 = 384;
            var4 = 128;
            this.SPARKLE_STEPS_SLIM = 7;
            this.SPARKLE_STEPS_BIG = 11;
            this.algorithmName = "SCHWAEMM256-128";
            break;
         case SCHWAEMM192_192:
            var5 = 192;
            var6 = 192;
            var3 = 192;
            var2 = 384;
            var4 = 192;
            this.SPARKLE_STEPS_SLIM = 7;
            this.SPARKLE_STEPS_BIG = 11;
            this.algorithmName = "SCHWAEMM192-192";
            break;
         case SCHWAEMM256_256:
            var5 = 256;
            var6 = 256;
            var3 = 256;
            var2 = 512;
            var4 = 256;
            this.SPARKLE_STEPS_SLIM = 8;
            this.SPARKLE_STEPS_BIG = 12;
            this.algorithmName = "SCHWAEMM256-256";
            break;
         default:
            throw new IllegalArgumentException("Invalid definition of SCHWAEMM instance");
      }

      this.KEY_WORDS = var5 >>> 5;
      this.KEY_SIZE = var5 >>> 3;
      this.TAG_WORDS = var3 >>> 5;
      this.MAC_SIZE = var3 >>> 3;
      this.STATE_WORDS = var2 >>> 5;
      this.RATE_WORDS = var6 >>> 5;
      this.IV_SIZE = var6 >>> 3;
      int var7 = var4 >>> 6;
      int var8 = var4 >>> 5;
      this.CAP_MASK = this.RATE_WORDS > var8 ? var8 - 1 : -1;
      this._A0 = 1 << var7 << 24;
      this._A1 = (1 ^ 1 << var7) << 24;
      this._M2 = (2 ^ 1 << var7) << 24;
      this._M3 = (3 ^ 1 << var7) << 24;
      this.state = new int[this.STATE_WORDS];
      this.k = new int[this.KEY_WORDS];
      this.npub = new int[this.RATE_WORDS];
      this.AADBufferSize = this.BlockSize = this.IV_SIZE;
      this.setInnerMembers(AEADBaseEngine.ProcessingBufferType.Buffered, AEADBaseEngine.AADOperatorType.Default, AEADBaseEngine.DataOperatorType.Default);
   }

   @Override
   protected void init(byte[] var1, byte[] var2) throws IllegalArgumentException {
      Pack.littleEndianToInt(var1, 0, this.k);
      Pack.littleEndianToInt(var2, 0, this.npub);
   }

   @Override
   protected void finishAAD(AEADBaseEngine.State var1, boolean var2) {
      this.finishAAD2(var1);
   }

   @Override
   protected void processFinalBlock(byte[] var1, int var2) {
      if (this.encrypted || this.m_bufPos > 0) {
         this.state[this.STATE_WORDS - 1] = this.state[this.STATE_WORDS - 1] ^ (this.m_bufPos < this.IV_SIZE ? this._M2 : this._M3);
         int[] var3 = new int[this.RATE_WORDS];

         for (int var4 = 0; var4 < this.m_bufPos; var4++) {
            var3[var4 >>> 2] = var3[var4 >>> 2] | (this.m_buf[var4] & 255) << ((var4 & 3) << 3);
         }

         if (this.m_bufPos < this.IV_SIZE) {
            if (!this.forEncryption) {
               int var9 = (this.m_bufPos & 3) << 3;
               var3[this.m_bufPos >>> 2] = var3[this.m_bufPos >>> 2] | this.state[this.m_bufPos >>> 2] >>> var9 << var9;
               var9 = (this.m_bufPos >>> 2) + 1;
               System.arraycopy(this.state, var9, var3, var9, this.RATE_WORDS - var9);
            }

            var3[this.m_bufPos >>> 2] = var3[this.m_bufPos >>> 2] ^ 128 << ((this.m_bufPos & 3) << 3);
         }

         for (int var11 = 0; var11 < this.RATE_WORDS / 2; var11++) {
            int var5 = var11 + this.RATE_WORDS / 2;
            int var6 = this.state[var11];
            int var7 = this.state[var5];
            if (this.forEncryption) {
               this.state[var11] = var7 ^ var3[var11] ^ this.state[this.RATE_WORDS + var11];
               this.state[var5] = var6 ^ var7 ^ var3[var5] ^ this.state[this.RATE_WORDS + (var5 & this.CAP_MASK)];
            } else {
               this.state[var11] = var6 ^ var7 ^ var3[var11] ^ this.state[this.RATE_WORDS + var11];
               this.state[var5] = var6 ^ var3[var5] ^ this.state[this.RATE_WORDS + (var5 & this.CAP_MASK)];
            }

            var3[var11] ^= var6;
            var3[var5] ^= var7;
         }

         for (int var12 = 0; var12 < this.m_bufPos; var12++) {
            var1[var2++] = (byte)(var3[var12 >>> 2] >>> ((var12 & 3) << 3));
         }

         sparkle_opt(this.state, this.SPARKLE_STEPS_BIG);
      }

      for (int var8 = 0; var8 < this.KEY_WORDS; var8++) {
         this.state[this.RATE_WORDS + var8] = this.state[this.RATE_WORDS + var8] ^ this.k[var8];
      }

      Pack.intToLittleEndian(this.state, this.RATE_WORDS, this.TAG_WORDS, this.mac, 0);
   }

   @Override
   protected void processBufferAAD(byte[] var1, int var2) {
      for (int var3 = 0; var3 < this.RATE_WORDS / 2; var3++) {
         int var4 = var3 + this.RATE_WORDS / 2;
         int var5 = this.state[var3];
         int var6 = this.state[var4];
         int var7 = Pack.littleEndianToInt(var1, var2 + var3 * 4);
         int var8 = Pack.littleEndianToInt(var1, var2 + var4 * 4);
         this.state[var3] = var6 ^ var7 ^ this.state[this.RATE_WORDS + var3];
         this.state[var4] = var5 ^ var6 ^ var8 ^ this.state[this.RATE_WORDS + (var4 & this.CAP_MASK)];
      }

      sparkle_opt(this.state, this.SPARKLE_STEPS_SLIM);
   }

   @Override
   protected void processBufferDecrypt(byte[] var1, int var2, byte[] var3, int var4) {
      for (int var5 = 0; var5 < this.RATE_WORDS / 2; var5++) {
         int var6 = var5 + this.RATE_WORDS / 2;
         int var7 = this.state[var5];
         int var8 = this.state[var6];
         int var9 = Pack.littleEndianToInt(var1, var2 + var5 * 4);
         int var10 = Pack.littleEndianToInt(var1, var2 + var6 * 4);
         this.state[var5] = var7 ^ var8 ^ var9 ^ this.state[this.RATE_WORDS + var5];
         this.state[var6] = var7 ^ var10 ^ this.state[this.RATE_WORDS + (var6 & this.CAP_MASK)];
         Pack.intToLittleEndian(var9 ^ var7, var3, var4 + var5 * 4);
         Pack.intToLittleEndian(var10 ^ var8, var3, var4 + var6 * 4);
      }

      sparkle_opt(this.state, this.SPARKLE_STEPS_SLIM);
      this.encrypted = true;
   }

   @Override
   protected void processBufferEncrypt(byte[] var1, int var2, byte[] var3, int var4) {
      for (int var5 = 0; var5 < this.RATE_WORDS / 2; var5++) {
         int var6 = var5 + this.RATE_WORDS / 2;
         int var7 = this.state[var5];
         int var8 = this.state[var6];
         int var9 = Pack.littleEndianToInt(var1, var2 + var5 * 4);
         int var10 = Pack.littleEndianToInt(var1, var2 + var6 * 4);
         this.state[var5] = var8 ^ var9 ^ this.state[this.RATE_WORDS + var5];
         this.state[var6] = var7 ^ var8 ^ var10 ^ this.state[this.RATE_WORDS + (var6 & this.CAP_MASK)];
         Pack.intToLittleEndian(var9 ^ var7, var3, var4 + var5 * 4);
         Pack.intToLittleEndian(var10 ^ var8, var3, var4 + var6 * 4);
      }

      sparkle_opt(this.state, this.SPARKLE_STEPS_SLIM);
      this.encrypted = true;
   }

   @Override
   protected void processFinalAAD() {
      if (this.m_aadPos < this.BlockSize) {
         this.state[this.STATE_WORDS - 1] = this.state[this.STATE_WORDS - 1] ^ this._A0;
         this.m_aad[this.m_aadPos++] = -128;
         Arrays.fill(this.m_aad, this.m_aadPos, this.BlockSize, (byte)0);
      } else {
         this.state[this.STATE_WORDS - 1] = this.state[this.STATE_WORDS - 1] ^ this._A1;
      }

      for (int var1 = 0; var1 < this.RATE_WORDS / 2; var1++) {
         int var2 = var1 + this.RATE_WORDS / 2;
         int var3 = this.state[var1];
         int var4 = this.state[var2];
         int var5 = Pack.littleEndianToInt(this.m_aad, var1 * 4);
         int var6 = Pack.littleEndianToInt(this.m_aad, var2 * 4);
         this.state[var1] = var4 ^ var5 ^ this.state[this.RATE_WORDS + var1];
         this.state[var2] = var3 ^ var4 ^ var6 ^ this.state[this.RATE_WORDS + (var2 & this.CAP_MASK)];
      }

      sparkle_opt(this.state, this.SPARKLE_STEPS_BIG);
   }

   @Override
   protected void reset(boolean var1) {
      this.encrypted = false;
      System.arraycopy(this.npub, 0, this.state, 0, this.RATE_WORDS);
      System.arraycopy(this.k, 0, this.state, this.RATE_WORDS, this.KEY_WORDS);
      sparkle_opt(this.state, this.SPARKLE_STEPS_BIG);
      super.reset(var1);
   }

   private static int ELL(int var0) {
      return Integers.rotateRight(var0, 16) ^ var0 & 65535;
   }

   private static void sparkle_opt(int[] var0, int var1) {
      switch (var0.length) {
         case 8:
            sparkle_opt8(var0, var1);
            break;
         case 12:
            sparkle_opt12(var0, var1);
            break;
         case 16:
            sparkle_opt16(var0, var1);
            break;
         default:
            throw new IllegalStateException();
      }
   }

   static void sparkle_opt8(int[] var0, int var1) {
      int var2 = var0[0];
      int var3 = var0[1];
      int var4 = var0[2];
      int var5 = var0[3];
      int var6 = var0[4];
      int var7 = var0[5];
      int var8 = var0[6];
      int var9 = var0[7];

      for (int var10 = 0; var10 < var1; var10++) {
         var3 ^= RCON[var10 & 7];
         var5 ^= var10;
         int var11 = RCON[0];
         var2 += Integers.rotateRight(var3, 31);
         var3 ^= Integers.rotateRight(var2, 24);
         var2 ^= var11;
         var2 += Integers.rotateRight(var3, 17);
         var3 ^= Integers.rotateRight(var2, 17);
         var2 ^= var11;
         var2 += var3;
         var3 ^= Integers.rotateRight(var2, 31);
         var2 ^= var11;
         var2 += Integers.rotateRight(var3, 24);
         var3 ^= Integers.rotateRight(var2, 16);
         var2 ^= var11;
         var11 = RCON[1];
         var4 += Integers.rotateRight(var5, 31);
         var5 ^= Integers.rotateRight(var4, 24);
         var4 ^= var11;
         var4 += Integers.rotateRight(var5, 17);
         var5 ^= Integers.rotateRight(var4, 17);
         var4 ^= var11;
         var4 += var5;
         var5 ^= Integers.rotateRight(var4, 31);
         var4 ^= var11;
         var4 += Integers.rotateRight(var5, 24);
         var5 ^= Integers.rotateRight(var4, 16);
         var4 ^= var11;
         var11 = RCON[2];
         var6 += Integers.rotateRight(var7, 31);
         var7 ^= Integers.rotateRight(var6, 24);
         var6 ^= var11;
         var6 += Integers.rotateRight(var7, 17);
         var7 ^= Integers.rotateRight(var6, 17);
         var6 ^= var11;
         var6 += var7;
         var7 ^= Integers.rotateRight(var6, 31);
         var6 ^= var11;
         var6 += Integers.rotateRight(var7, 24);
         var7 ^= Integers.rotateRight(var6, 16);
         var6 ^= var11;
         var11 = RCON[3];
         var8 += Integers.rotateRight(var9, 31);
         var9 ^= Integers.rotateRight(var8, 24);
         var8 ^= var11;
         var8 += Integers.rotateRight(var9, 17);
         var9 ^= Integers.rotateRight(var8, 17);
         var8 ^= var11;
         var8 += var9;
         var9 ^= Integers.rotateRight(var8, 31);
         var8 ^= var11;
         var8 += Integers.rotateRight(var9, 24);
         var9 ^= Integers.rotateRight(var8, 16);
         var8 ^= var11;
         var11 = ELL(var2 ^ var4);
         int var12 = ELL(var3 ^ var5);
         int var13 = var2 ^ var6;
         int var14 = var3 ^ var7;
         int var15 = var4 ^ var8;
         int var16 = var5 ^ var9;
         var6 = var2;
         var7 = var3;
         var8 = var4;
         var9 = var5;
         var2 = var15 ^ var12;
         var3 = var16 ^ var11;
         var4 = var13 ^ var12;
         var5 = var14 ^ var11;
      }

      var0[0] = var2;
      var0[1] = var3;
      var0[2] = var4;
      var0[3] = var5;
      var0[4] = var6;
      var0[5] = var7;
      var0[6] = var8;
      var0[7] = var9;
   }

   static void sparkle_opt12(int[] var0, int var1) {
      int var2 = var0[0];
      int var3 = var0[1];
      int var4 = var0[2];
      int var5 = var0[3];
      int var6 = var0[4];
      int var7 = var0[5];
      int var8 = var0[6];
      int var9 = var0[7];
      int var10 = var0[8];
      int var11 = var0[9];
      int var12 = var0[10];
      int var13 = var0[11];

      for (int var14 = 0; var14 < var1; var14++) {
         var3 ^= RCON[var14 & 7];
         var5 ^= var14;
         int var15 = RCON[0];
         var2 += Integers.rotateRight(var3, 31);
         var3 ^= Integers.rotateRight(var2, 24);
         var2 ^= var15;
         var2 += Integers.rotateRight(var3, 17);
         var3 ^= Integers.rotateRight(var2, 17);
         var2 ^= var15;
         var2 += var3;
         var3 ^= Integers.rotateRight(var2, 31);
         var2 ^= var15;
         var2 += Integers.rotateRight(var3, 24);
         var3 ^= Integers.rotateRight(var2, 16);
         var2 ^= var15;
         var15 = RCON[1];
         var4 += Integers.rotateRight(var5, 31);
         var5 ^= Integers.rotateRight(var4, 24);
         var4 ^= var15;
         var4 += Integers.rotateRight(var5, 17);
         var5 ^= Integers.rotateRight(var4, 17);
         var4 ^= var15;
         var4 += var5;
         var5 ^= Integers.rotateRight(var4, 31);
         var4 ^= var15;
         var4 += Integers.rotateRight(var5, 24);
         var5 ^= Integers.rotateRight(var4, 16);
         var4 ^= var15;
         var15 = RCON[2];
         var6 += Integers.rotateRight(var7, 31);
         var7 ^= Integers.rotateRight(var6, 24);
         var6 ^= var15;
         var6 += Integers.rotateRight(var7, 17);
         var7 ^= Integers.rotateRight(var6, 17);
         var6 ^= var15;
         var6 += var7;
         var7 ^= Integers.rotateRight(var6, 31);
         var6 ^= var15;
         var6 += Integers.rotateRight(var7, 24);
         var7 ^= Integers.rotateRight(var6, 16);
         var6 ^= var15;
         var15 = RCON[3];
         var8 += Integers.rotateRight(var9, 31);
         var9 ^= Integers.rotateRight(var8, 24);
         var8 ^= var15;
         var8 += Integers.rotateRight(var9, 17);
         var9 ^= Integers.rotateRight(var8, 17);
         var8 ^= var15;
         var8 += var9;
         var9 ^= Integers.rotateRight(var8, 31);
         var8 ^= var15;
         var8 += Integers.rotateRight(var9, 24);
         var9 ^= Integers.rotateRight(var8, 16);
         var8 ^= var15;
         var15 = RCON[4];
         var10 += Integers.rotateRight(var11, 31);
         var11 ^= Integers.rotateRight(var10, 24);
         var10 ^= var15;
         var10 += Integers.rotateRight(var11, 17);
         var11 ^= Integers.rotateRight(var10, 17);
         var10 ^= var15;
         var10 += var11;
         var11 ^= Integers.rotateRight(var10, 31);
         var10 ^= var15;
         var10 += Integers.rotateRight(var11, 24);
         var11 ^= Integers.rotateRight(var10, 16);
         var10 ^= var15;
         var15 = RCON[5];
         var12 += Integers.rotateRight(var13, 31);
         var13 ^= Integers.rotateRight(var12, 24);
         var12 ^= var15;
         var12 += Integers.rotateRight(var13, 17);
         var13 ^= Integers.rotateRight(var12, 17);
         var12 ^= var15;
         var12 += var13;
         var13 ^= Integers.rotateRight(var12, 31);
         var12 ^= var15;
         var12 += Integers.rotateRight(var13, 24);
         var13 ^= Integers.rotateRight(var12, 16);
         var12 ^= var15;
         var15 = ELL(var2 ^ var4 ^ var6);
         int var16 = ELL(var3 ^ var5 ^ var7);
         int var17 = var2 ^ var8;
         int var18 = var3 ^ var9;
         int var19 = var4 ^ var10;
         int var20 = var5 ^ var11;
         int var21 = var6 ^ var12;
         int var22 = var7 ^ var13;
         var8 = var2;
         var9 = var3;
         var10 = var4;
         var11 = var5;
         var12 = var6;
         var13 = var7;
         var2 = var19 ^ var16;
         var3 = var20 ^ var15;
         var4 = var21 ^ var16;
         var5 = var22 ^ var15;
         var6 = var17 ^ var16;
         var7 = var18 ^ var15;
      }

      var0[0] = var2;
      var0[1] = var3;
      var0[2] = var4;
      var0[3] = var5;
      var0[4] = var6;
      var0[5] = var7;
      var0[6] = var8;
      var0[7] = var9;
      var0[8] = var10;
      var0[9] = var11;
      var0[10] = var12;
      var0[11] = var13;
   }

   public static void sparkle_opt12(SparkleDigest.Friend var0, int[] var1, int var2) {
      if (null == var0) {
         throw new NullPointerException("This method is only for use by SparkleDigest");
      } else {
         sparkle_opt12(var1, var2);
      }
   }

   static void sparkle_opt16(int[] var0, int var1) {
      int var2 = var0[0];
      int var3 = var0[1];
      int var4 = var0[2];
      int var5 = var0[3];
      int var6 = var0[4];
      int var7 = var0[5];
      int var8 = var0[6];
      int var9 = var0[7];
      int var10 = var0[8];
      int var11 = var0[9];
      int var12 = var0[10];
      int var13 = var0[11];
      int var14 = var0[12];
      int var15 = var0[13];
      int var16 = var0[14];
      int var17 = var0[15];

      for (int var18 = 0; var18 < var1; var18++) {
         var3 ^= RCON[var18 & 7];
         var5 ^= var18;
         int var19 = RCON[0];
         var2 += Integers.rotateRight(var3, 31);
         var3 ^= Integers.rotateRight(var2, 24);
         var2 ^= var19;
         var2 += Integers.rotateRight(var3, 17);
         var3 ^= Integers.rotateRight(var2, 17);
         var2 ^= var19;
         var2 += var3;
         var3 ^= Integers.rotateRight(var2, 31);
         var2 ^= var19;
         var2 += Integers.rotateRight(var3, 24);
         var3 ^= Integers.rotateRight(var2, 16);
         var2 ^= var19;
         var19 = RCON[1];
         var4 += Integers.rotateRight(var5, 31);
         var5 ^= Integers.rotateRight(var4, 24);
         var4 ^= var19;
         var4 += Integers.rotateRight(var5, 17);
         var5 ^= Integers.rotateRight(var4, 17);
         var4 ^= var19;
         var4 += var5;
         var5 ^= Integers.rotateRight(var4, 31);
         var4 ^= var19;
         var4 += Integers.rotateRight(var5, 24);
         var5 ^= Integers.rotateRight(var4, 16);
         var4 ^= var19;
         var19 = RCON[2];
         var6 += Integers.rotateRight(var7, 31);
         var7 ^= Integers.rotateRight(var6, 24);
         var6 ^= var19;
         var6 += Integers.rotateRight(var7, 17);
         var7 ^= Integers.rotateRight(var6, 17);
         var6 ^= var19;
         var6 += var7;
         var7 ^= Integers.rotateRight(var6, 31);
         var6 ^= var19;
         var6 += Integers.rotateRight(var7, 24);
         var7 ^= Integers.rotateRight(var6, 16);
         var6 ^= var19;
         var19 = RCON[3];
         var8 += Integers.rotateRight(var9, 31);
         var9 ^= Integers.rotateRight(var8, 24);
         var8 ^= var19;
         var8 += Integers.rotateRight(var9, 17);
         var9 ^= Integers.rotateRight(var8, 17);
         var8 ^= var19;
         var8 += var9;
         var9 ^= Integers.rotateRight(var8, 31);
         var8 ^= var19;
         var8 += Integers.rotateRight(var9, 24);
         var9 ^= Integers.rotateRight(var8, 16);
         var8 ^= var19;
         var19 = RCON[4];
         var10 += Integers.rotateRight(var11, 31);
         var11 ^= Integers.rotateRight(var10, 24);
         var10 ^= var19;
         var10 += Integers.rotateRight(var11, 17);
         var11 ^= Integers.rotateRight(var10, 17);
         var10 ^= var19;
         var10 += var11;
         var11 ^= Integers.rotateRight(var10, 31);
         var10 ^= var19;
         var10 += Integers.rotateRight(var11, 24);
         var11 ^= Integers.rotateRight(var10, 16);
         var10 ^= var19;
         var19 = RCON[5];
         var12 += Integers.rotateRight(var13, 31);
         var13 ^= Integers.rotateRight(var12, 24);
         var12 ^= var19;
         var12 += Integers.rotateRight(var13, 17);
         var13 ^= Integers.rotateRight(var12, 17);
         var12 ^= var19;
         var12 += var13;
         var13 ^= Integers.rotateRight(var12, 31);
         var12 ^= var19;
         var12 += Integers.rotateRight(var13, 24);
         var13 ^= Integers.rotateRight(var12, 16);
         var12 ^= var19;
         var19 = RCON[6];
         var14 += Integers.rotateRight(var15, 31);
         var15 ^= Integers.rotateRight(var14, 24);
         var14 ^= var19;
         var14 += Integers.rotateRight(var15, 17);
         var15 ^= Integers.rotateRight(var14, 17);
         var14 ^= var19;
         var14 += var15;
         var15 ^= Integers.rotateRight(var14, 31);
         var14 ^= var19;
         var14 += Integers.rotateRight(var15, 24);
         var15 ^= Integers.rotateRight(var14, 16);
         var14 ^= var19;
         var19 = RCON[7];
         var16 += Integers.rotateRight(var17, 31);
         var17 ^= Integers.rotateRight(var16, 24);
         var16 ^= var19;
         var16 += Integers.rotateRight(var17, 17);
         var17 ^= Integers.rotateRight(var16, 17);
         var16 ^= var19;
         var16 += var17;
         var17 ^= Integers.rotateRight(var16, 31);
         var16 ^= var19;
         var16 += Integers.rotateRight(var17, 24);
         var17 ^= Integers.rotateRight(var16, 16);
         var16 ^= var19;
         var19 = ELL(var2 ^ var4 ^ var6 ^ var8);
         int var20 = ELL(var3 ^ var5 ^ var7 ^ var9);
         int var21 = var2 ^ var10;
         int var22 = var3 ^ var11;
         int var23 = var4 ^ var12;
         int var24 = var5 ^ var13;
         int var25 = var6 ^ var14;
         int var26 = var7 ^ var15;
         int var27 = var8 ^ var16;
         int var28 = var9 ^ var17;
         var10 = var2;
         var11 = var3;
         var12 = var4;
         var13 = var5;
         var14 = var6;
         var15 = var7;
         var16 = var8;
         var17 = var9;
         var2 = var23 ^ var20;
         var3 = var24 ^ var19;
         var4 = var25 ^ var20;
         var5 = var26 ^ var19;
         var6 = var27 ^ var20;
         var7 = var28 ^ var19;
         var8 = var21 ^ var20;
         var9 = var22 ^ var19;
      }

      var0[0] = var2;
      var0[1] = var3;
      var0[2] = var4;
      var0[3] = var5;
      var0[4] = var6;
      var0[5] = var7;
      var0[6] = var8;
      var0[7] = var9;
      var0[8] = var10;
      var0[9] = var11;
      var0[10] = var12;
      var0[11] = var13;
      var0[12] = var14;
      var0[13] = var15;
      var0[14] = var16;
      var0[15] = var17;
   }

   public static void sparkle_opt16(SparkleDigest.Friend var0, int[] var1, int var2) {
      if (null == var0) {
         throw new NullPointerException("This method is only for use by SparkleDigest");
      } else {
         sparkle_opt16(var1, var2);
      }
   }

   public static enum SparkleParameters {
      SCHWAEMM128_128,
      SCHWAEMM256_128,
      SCHWAEMM192_192,
      SCHWAEMM256_256;
   }
}
