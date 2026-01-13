package org.bouncycastle.crypto.engines;

import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Bytes;
import org.bouncycastle.util.Pack;

public class ISAPEngine extends AEADBaseEngine {
   private static final int ISAP_STATE_SZ = 40;
   private byte[] k;
   private byte[] npub;
   private int ISAP_rH;
   private final ISAPEngine.ISAP_AEAD ISAPAEAD;

   public ISAPEngine(ISAPEngine.IsapType var1) {
      this.KEY_SIZE = this.IV_SIZE = this.MAC_SIZE = 16;
      switch (var1) {
         case ISAP_A_128A:
            this.ISAPAEAD = new ISAPEngine.ISAPAEAD_A_128A();
            this.algorithmName = "ISAP-A-128A AEAD";
            break;
         case ISAP_K_128A:
            this.ISAPAEAD = new ISAPEngine.ISAPAEAD_K_128A();
            this.algorithmName = "ISAP-K-128A AEAD";
            break;
         case ISAP_A_128:
            this.ISAPAEAD = new ISAPEngine.ISAPAEAD_A_128();
            this.algorithmName = "ISAP-A-128 AEAD";
            break;
         case ISAP_K_128:
            this.ISAPAEAD = new ISAPEngine.ISAPAEAD_K_128();
            this.algorithmName = "ISAP-K-128 AEAD";
            break;
         default:
            throw new IllegalArgumentException("Incorrect ISAP parameter");
      }

      this.AADBufferSize = this.BlockSize;
      this.setInnerMembers(AEADBaseEngine.ProcessingBufferType.Immediate, AEADBaseEngine.AADOperatorType.Default, AEADBaseEngine.DataOperatorType.Counter);
   }

   @Override
   protected void init(byte[] var1, byte[] var2) throws IllegalArgumentException {
      this.npub = var2;
      this.k = var1;
      this.ISAPAEAD.init();
   }

   @Override
   protected void processBufferAAD(byte[] var1, int var2) {
      this.ISAPAEAD.absorbMacBlock(var1, var2);
   }

   @Override
   protected void processFinalAAD() {
      this.ISAPAEAD.absorbFinalAADBlock();
   }

   @Override
   protected void finishAAD(AEADBaseEngine.State var1, boolean var2) {
      this.finishAAD3(var1, var2);
   }

   @Override
   protected void processBufferEncrypt(byte[] var1, int var2, byte[] var3, int var4) {
      this.ISAPAEAD.processEncBlock(var1, var2, var3, var4);
      this.ISAPAEAD.absorbMacBlock(var3, var4);
   }

   @Override
   protected void processBufferDecrypt(byte[] var1, int var2, byte[] var3, int var4) {
      this.ISAPAEAD.processEncBlock(var1, var2, var3, var4);
      this.ISAPAEAD.absorbMacBlock(var1, var2);
   }

   @Override
   protected void processFinalBlock(byte[] var1, int var2) {
      this.ISAPAEAD.processEncFinalBlock(var1, var2);
      if (this.forEncryption) {
         this.ISAPAEAD.processMACFinal(var1, var2, this.m_bufPos, this.mac);
      } else {
         this.ISAPAEAD.processMACFinal(this.m_buf, 0, this.m_bufPos, this.mac);
      }
   }

   @Override
   protected void reset(boolean var1) {
      super.reset(var1);
      this.ISAPAEAD.reset();
   }

   private abstract class ISAPAEAD_A implements ISAPEngine.ISAP_AEAD {
      protected long[] k64;
      protected long[] npub64;
      protected long ISAP_IV1_64;
      protected long ISAP_IV2_64;
      protected long ISAP_IV3_64;
      AsconPermutationFriend.AsconPermutation p;
      AsconPermutationFriend.AsconPermutation mac;

      public ISAPAEAD_A() {
         ISAPEngine.this.ISAP_rH = 64;
         ISAPEngine.this.BlockSize = ISAPEngine.this.ISAP_rH + 7 >> 3;
         this.p = new AsconPermutationFriend.AsconPermutation();
         this.mac = new AsconPermutationFriend.AsconPermutation();
      }

      @Override
      public void init() {
         this.npub64 = new long[this.getLongSize(ISAPEngine.this.npub.length)];
         this.k64 = new long[this.getLongSize(ISAPEngine.this.k.length)];
         Pack.bigEndianToLong(ISAPEngine.this.npub, 0, this.npub64);
         Pack.bigEndianToLong(ISAPEngine.this.k, 0, this.k64);
      }

      protected abstract void PX1(AsconPermutationFriend.AsconPermutation var1);

      protected abstract void PX2(AsconPermutationFriend.AsconPermutation var1);

      @Override
      public void absorbMacBlock(byte[] var1, int var2) {
         this.mac.x0 = this.mac.x0 ^ Pack.bigEndianToLong(var1, var2);
         this.mac.p(12);
      }

      @Override
      public void absorbFinalAADBlock() {
         for (int var1 = 0; var1 < ISAPEngine.this.m_aadPos; var1++) {
            this.mac.x0 = this.mac.x0 ^ (ISAPEngine.this.m_aad[var1] & 255L) << (7 - var1 << 3);
         }

         this.mac.x0 = this.mac.x0 ^ 128L << (7 - ISAPEngine.this.m_aadPos << 3);
         this.mac.p(12);
         this.mac.x4 ^= 1L;
      }

      @Override
      public void processMACFinal(byte[] var1, int var2, int var3, byte[] var4) {
         for (int var5 = 0; var5 < var3; var5++) {
            this.mac.x0 = this.mac.x0 ^ (var1[var2++] & 255L) << (7 - var5 << 3);
         }

         this.mac.x0 ^= 128L << (7 - var3 << 3);
         this.mac.p(12);
         Pack.longToBigEndian(this.mac.x0, var4, 0);
         Pack.longToBigEndian(this.mac.x1, var4, 8);
         long var11 = this.mac.x2;
         long var7 = this.mac.x3;
         long var9 = this.mac.x4;
         this.isap_rk(this.mac, this.ISAP_IV2_64, var4, ISAPEngine.this.KEY_SIZE);
         this.mac.x2 = var11;
         this.mac.x3 = var7;
         this.mac.x4 = var9;
         this.mac.p(12);
         Pack.longToBigEndian(this.mac.x0, var4, 0);
         Pack.longToBigEndian(this.mac.x1, var4, 8);
      }

      private void isap_rk(AsconPermutationFriend.AsconPermutation var1, long var2, byte[] var4, int var5) {
         var1.set(this.k64[0], this.k64[1], var2, 0L, 0L);
         var1.p(12);

         for (int var6 = 0; var6 < (var5 << 3) - 1; var6++) {
            var1.x0 = var1.x0 ^ ((var4[var6 >>> 3] >>> 7 - (var6 & 7) & 1) << 7 & 255L) << 56;
            this.PX2(var1);
         }

         var1.x0 = var1.x0 ^ (var4[var5 - 1] & 1L) << 7 << 56;
         var1.p(12);
      }

      @Override
      public void processEncBlock(byte[] var1, int var2, byte[] var3, int var4) {
         Pack.longToBigEndian(Pack.bigEndianToLong(var1, var2) ^ this.p.x0, var3, var4);
         this.PX1(this.p);
      }

      @Override
      public void processEncFinalBlock(byte[] var1, int var2) {
         byte[] var3 = Pack.longToLittleEndian(this.p.x0);
         Bytes.xor(ISAPEngine.this.m_bufPos, var3, ISAPEngine.this.BlockSize - ISAPEngine.this.m_bufPos, ISAPEngine.this.m_buf, 0, var1, var2);
      }

      @Override
      public void reset() {
         this.isap_rk(this.p, this.ISAP_IV3_64, ISAPEngine.this.npub, ISAPEngine.this.IV_SIZE);
         this.p.x3 = this.npub64[0];
         this.p.x4 = this.npub64[1];
         this.PX1(this.p);
         this.mac.set(this.npub64[0], this.npub64[1], this.ISAP_IV1_64, 0L, 0L);
         this.mac.p(12);
      }

      private int getLongSize(int var1) {
         return var1 + 7 >>> 3;
      }
   }

   private class ISAPAEAD_A_128 extends ISAPEngine.ISAPAEAD_A {
      public ISAPAEAD_A_128() {
         this.ISAP_IV1_64 = 108156764298152972L;
         this.ISAP_IV2_64 = 180214358336080908L;
         this.ISAP_IV3_64 = 252271952374008844L;
      }

      @Override
      protected void PX1(AsconPermutationFriend.AsconPermutation var1) {
         var1.p(12);
      }

      @Override
      protected void PX2(AsconPermutationFriend.AsconPermutation var1) {
         var1.p(12);
      }
   }

   private class ISAPAEAD_A_128A extends ISAPEngine.ISAPAEAD_A {
      public ISAPAEAD_A_128A() {
         this.ISAP_IV1_64 = 108156764297430540L;
         this.ISAP_IV2_64 = 180214358335358476L;
         this.ISAP_IV3_64 = 252271952373286412L;
      }

      @Override
      protected void PX1(AsconPermutationFriend.AsconPermutation var1) {
         var1.p(6);
      }

      @Override
      protected void PX2(AsconPermutationFriend.AsconPermutation var1) {
         var1.round(75L);
      }
   }

   private abstract class ISAPAEAD_K implements ISAPEngine.ISAP_AEAD {
      protected final int ISAP_STATE_SZ_CRYPTO_NPUBBYTES = 40 - ISAPEngine.this.IV_SIZE;
      protected short[] ISAP_IV1_16;
      protected short[] ISAP_IV2_16;
      protected short[] ISAP_IV3_16;
      protected short[] k16;
      protected short[] iv16;
      private final int[] KeccakF400RoundConstants = new int[]{
         1, 32898, 32906, 32768, 32907, 1, 32897, 32777, 138, 136, 32777, 10, 32907, 139, 32905, 32771, 32770, 128, 32778, 10
      };
      protected short[] SX = new short[25];
      protected short[] macSX = new short[25];
      protected short[] E = new short[25];
      protected short[] C = new short[5];
      protected short[] macE = new short[25];
      protected short[] macC = new short[5];

      public ISAPAEAD_K() {
         ISAPEngine.this.ISAP_rH = 144;
         ISAPEngine.this.BlockSize = ISAPEngine.this.ISAP_rH + 7 >> 3;
      }

      @Override
      public void init() {
         this.k16 = new short[ISAPEngine.this.k.length >> 1];
         Pack.littleEndianToShort(ISAPEngine.this.k, 0, this.k16, 0, this.k16.length);
         this.iv16 = new short[ISAPEngine.this.npub.length >> 1];
         Pack.littleEndianToShort(ISAPEngine.this.npub, 0, this.iv16, 0, this.iv16.length);
      }

      @Override
      public void reset() {
         Arrays.fill(this.SX, (short)0);
         this.isap_rk(this.ISAP_IV3_16, ISAPEngine.this.npub, ISAPEngine.this.IV_SIZE, this.SX, this.ISAP_STATE_SZ_CRYPTO_NPUBBYTES, this.C);
         System.arraycopy(this.iv16, 0, this.SX, 17, 8);
         this.PermuteRoundsKX(this.SX, this.E, this.C);
         Arrays.fill(this.macSX, 12, 25, (short)0);
         System.arraycopy(this.iv16, 0, this.macSX, 0, 8);
         System.arraycopy(this.ISAP_IV1_16, 0, this.macSX, 8, 4);
         this.PermuteRoundsHX(this.macSX, this.macE, this.macC);
      }

      protected abstract void PermuteRoundsHX(short[] var1, short[] var2, short[] var3);

      protected abstract void PermuteRoundsKX(short[] var1, short[] var2, short[] var3);

      protected abstract void PermuteRoundsBX(short[] var1, short[] var2, short[] var3);

      @Override
      public void absorbMacBlock(byte[] var1, int var2) {
         this.byteToShortXor(var1, var2, this.macSX, ISAPEngine.this.BlockSize >> 1);
         this.PermuteRoundsHX(this.macSX, this.macE, this.macC);
      }

      @Override
      public void absorbFinalAADBlock() {
         for (int var1 = 0; var1 < ISAPEngine.this.m_aadPos; var1++) {
            this.macSX[var1 >> 1] = (short)(this.macSX[var1 >> 1] ^ (ISAPEngine.this.m_aad[var1] & 255) << ((var1 & 1) << 3));
         }

         this.macSX[ISAPEngine.this.m_aadPos >> 1] = (short)(this.macSX[ISAPEngine.this.m_aadPos >> 1] ^ 128 << ((ISAPEngine.this.m_aadPos & 1) << 3));
         this.PermuteRoundsHX(this.macSX, this.macE, this.macC);
         this.macSX[24] = (short)(this.macSX[24] ^ 256);
      }

      public void isap_rk(short[] var1, byte[] var2, int var3, short[] var4, int var5, short[] var6) {
         short[] var7 = new short[25];
         short[] var8 = new short[25];
         System.arraycopy(this.k16, 0, var7, 0, 8);
         System.arraycopy(var1, 0, var7, 8, 4);
         this.PermuteRoundsKX(var7, var8, var6);

         for (int var9 = 0; var9 < (var3 << 3) - 1; var9++) {
            var7[0] = (short)(var7[0] ^ (var2[var9 >> 3] >>> 7 - (var9 & 7) & 1) << 7);
            this.PermuteRoundsBX(var7, var8, var6);
         }

         var7[0] = (short)(var7[0] ^ (var2[var3 - 1] & 1) << 7);
         this.PermuteRoundsKX(var7, var8, var6);
         System.arraycopy(var7, 0, var4, 0, var5 == this.ISAP_STATE_SZ_CRYPTO_NPUBBYTES ? 17 : 8);
      }

      @Override
      public void processMACFinal(byte[] var1, int var2, int var3, byte[] var4) {
         for (int var5 = 0; var5 < var3; var5++) {
            this.macSX[var5 >> 1] = (short)(this.macSX[var5 >> 1] ^ (var1[var2++] & 255) << ((var5 & 1) << 3));
         }

         this.macSX[var3 >> 1] = (short)(this.macSX[var3 >> 1] ^ 128 << ((var3 & 1) << 3));
         this.PermuteRoundsHX(this.macSX, this.macE, this.macC);
         Pack.shortToLittleEndian(this.macSX, 0, 8, var4, 0);
         this.isap_rk(this.ISAP_IV2_16, var4, ISAPEngine.this.KEY_SIZE, this.macSX, ISAPEngine.this.KEY_SIZE, this.macC);
         this.PermuteRoundsHX(this.macSX, this.macE, this.macC);
         Pack.shortToLittleEndian(this.macSX, 0, 8, var4, 0);
      }

      @Override
      public void processEncBlock(byte[] var1, int var2, byte[] var3, int var4) {
         for (int var5 = 0; var5 < ISAPEngine.this.BlockSize; var5++) {
            var3[var4++] = (byte)(this.SX[var5 >> 1] >>> ((var5 & 1) << 3) ^ var1[var2++]);
         }

         this.PermuteRoundsKX(this.SX, this.E, this.C);
      }

      @Override
      public void processEncFinalBlock(byte[] var1, int var2) {
         for (int var3 = 0; var3 < ISAPEngine.this.m_bufPos; var3++) {
            var1[var2++] = (byte)(this.SX[var3 >> 1] >>> ((var3 & 1) << 3) ^ ISAPEngine.this.m_buf[var3]);
         }
      }

      private void byteToShortXor(byte[] var1, int var2, short[] var3, int var4) {
         for (int var5 = 0; var5 < var4; var5++) {
            var3[var5] ^= Pack.littleEndianToShort(var1, var2 + (var5 << 1));
         }
      }

      protected void rounds12X(short[] var1, short[] var2, short[] var3) {
         this.prepareThetaX(var1, var3);
         this.rounds_8_18(var1, var2, var3);
      }

      protected void rounds_4_18(short[] var1, short[] var2, short[] var3) {
         this.thetaRhoPiChiIotaPrepareTheta(4, var1, var2, var3);
         this.thetaRhoPiChiIotaPrepareTheta(5, var2, var1, var3);
         this.thetaRhoPiChiIotaPrepareTheta(6, var1, var2, var3);
         this.thetaRhoPiChiIotaPrepareTheta(7, var2, var1, var3);
         this.rounds_8_18(var1, var2, var3);
      }

      protected void rounds_8_18(short[] var1, short[] var2, short[] var3) {
         this.thetaRhoPiChiIotaPrepareTheta(8, var1, var2, var3);
         this.thetaRhoPiChiIotaPrepareTheta(9, var2, var1, var3);
         this.thetaRhoPiChiIotaPrepareTheta(10, var1, var2, var3);
         this.thetaRhoPiChiIotaPrepareTheta(11, var2, var1, var3);
         this.rounds_12_18(var1, var2, var3);
      }

      protected void rounds_12_18(short[] var1, short[] var2, short[] var3) {
         this.thetaRhoPiChiIotaPrepareTheta(12, var1, var2, var3);
         this.thetaRhoPiChiIotaPrepareTheta(13, var2, var1, var3);
         this.thetaRhoPiChiIotaPrepareTheta(14, var1, var2, var3);
         this.thetaRhoPiChiIotaPrepareTheta(15, var2, var1, var3);
         this.thetaRhoPiChiIotaPrepareTheta(16, var1, var2, var3);
         this.thetaRhoPiChiIotaPrepareTheta(17, var2, var1, var3);
         this.thetaRhoPiChiIotaPrepareTheta(18, var1, var2, var3);
         this.thetaRhoPiChiIota(var2, var1, var3);
      }

      protected void prepareThetaX(short[] var1, short[] var2) {
         var2[0] = (short)(var1[0] ^ var1[5] ^ var1[10] ^ var1[15] ^ var1[20]);
         var2[1] = (short)(var1[1] ^ var1[6] ^ var1[11] ^ var1[16] ^ var1[21]);
         var2[2] = (short)(var1[2] ^ var1[7] ^ var1[12] ^ var1[17] ^ var1[22]);
         var2[3] = (short)(var1[3] ^ var1[8] ^ var1[13] ^ var1[18] ^ var1[23]);
         var2[4] = (short)(var1[4] ^ var1[9] ^ var1[14] ^ var1[19] ^ var1[24]);
      }

      private short ROL16(short var1, int var2) {
         return (short)((var1 & '\uffff') << var2 ^ (var1 & '\uffff') >>> 16 - var2);
      }

      protected void thetaRhoPiChiIotaPrepareTheta(int var1, short[] var2, short[] var3, short[] var4) {
         short var5 = (short)(var4[4] ^ this.ROL16(var4[1], 1));
         short var6 = (short)(var4[0] ^ this.ROL16(var4[2], 1));
         short var7 = (short)(var4[1] ^ this.ROL16(var4[3], 1));
         short var8 = (short)(var4[2] ^ this.ROL16(var4[4], 1));
         short var9 = (short)(var4[3] ^ this.ROL16(var4[0], 1));
         short var10 = var2[0] ^= var5;
         var2[6] ^= var6;
         short var11 = this.ROL16(var2[6], 12);
         var2[12] ^= var7;
         short var12 = this.ROL16(var2[12], 11);
         var2[18] ^= var8;
         short var13 = this.ROL16(var2[18], 5);
         var2[24] ^= var9;
         short var14 = this.ROL16(var2[24], 14);
         var4[0] = var3[0] = (short)(var10 ^ ~var11 & var12 ^ this.KeccakF400RoundConstants[var1]);
         var4[1] = var3[1] = (short)(var11 ^ ~var12 & var13);
         var4[2] = var3[2] = (short)(var12 ^ ~var13 & var14);
         var4[3] = var3[3] = (short)(var13 ^ ~var14 & var10);
         var4[4] = var3[4] = (short)(var14 ^ ~var10 & var11);
         var2[3] ^= var8;
         var10 = this.ROL16(var2[3], 12);
         var2[9] ^= var9;
         var11 = this.ROL16(var2[9], 4);
         var2[10] ^= var5;
         var12 = this.ROL16(var2[10], 3);
         var2[16] ^= var6;
         var13 = this.ROL16(var2[16], 13);
         var2[22] ^= var7;
         var14 = this.ROL16(var2[22], 13);
         var3[5] = (short)(var10 ^ ~var11 & var12);
         var4[0] ^= var3[5];
         var3[6] = (short)(var11 ^ ~var12 & var13);
         var4[1] ^= var3[6];
         var3[7] = (short)(var12 ^ ~var13 & var14);
         var4[2] ^= var3[7];
         var3[8] = (short)(var13 ^ ~var14 & var10);
         var4[3] ^= var3[8];
         var3[9] = (short)(var14 ^ ~var10 & var11);
         var4[4] ^= var3[9];
         var2[1] ^= var6;
         var10 = this.ROL16(var2[1], 1);
         var2[7] ^= var7;
         var11 = this.ROL16(var2[7], 6);
         var2[13] ^= var8;
         var12 = this.ROL16(var2[13], 9);
         var2[19] ^= var9;
         var13 = this.ROL16(var2[19], 8);
         var2[20] ^= var5;
         var14 = this.ROL16(var2[20], 2);
         var3[10] = (short)(var10 ^ ~var11 & var12);
         var4[0] ^= var3[10];
         var3[11] = (short)(var11 ^ ~var12 & var13);
         var4[1] ^= var3[11];
         var3[12] = (short)(var12 ^ ~var13 & var14);
         var4[2] ^= var3[12];
         var3[13] = (short)(var13 ^ ~var14 & var10);
         var4[3] ^= var3[13];
         var3[14] = (short)(var14 ^ ~var10 & var11);
         var4[4] ^= var3[14];
         var2[4] ^= var9;
         var10 = this.ROL16(var2[4], 11);
         var2[5] ^= var5;
         var11 = this.ROL16(var2[5], 4);
         var2[11] ^= var6;
         var12 = this.ROL16(var2[11], 10);
         var2[17] ^= var7;
         var13 = this.ROL16(var2[17], 15);
         var2[23] ^= var8;
         var14 = this.ROL16(var2[23], 8);
         var3[15] = (short)(var10 ^ ~var11 & var12);
         var4[0] ^= var3[15];
         var3[16] = (short)(var11 ^ ~var12 & var13);
         var4[1] ^= var3[16];
         var3[17] = (short)(var12 ^ ~var13 & var14);
         var4[2] ^= var3[17];
         var3[18] = (short)(var13 ^ ~var14 & var10);
         var4[3] ^= var3[18];
         var3[19] = (short)(var14 ^ ~var10 & var11);
         var4[4] ^= var3[19];
         var2[2] ^= var7;
         var10 = this.ROL16(var2[2], 14);
         var2[8] ^= var8;
         var11 = this.ROL16(var2[8], 7);
         var2[14] ^= var9;
         var12 = this.ROL16(var2[14], 7);
         var2[15] ^= var5;
         var13 = this.ROL16(var2[15], 9);
         var2[21] ^= var6;
         var14 = this.ROL16(var2[21], 2);
         var3[20] = (short)(var10 ^ ~var11 & var12);
         var4[0] ^= var3[20];
         var3[21] = (short)(var11 ^ ~var12 & var13);
         var4[1] ^= var3[21];
         var3[22] = (short)(var12 ^ ~var13 & var14);
         var4[2] ^= var3[22];
         var3[23] = (short)(var13 ^ ~var14 & var10);
         var4[3] ^= var3[23];
         var3[24] = (short)(var14 ^ ~var10 & var11);
         var4[4] ^= var3[24];
      }

      protected void thetaRhoPiChiIota(short[] var1, short[] var2, short[] var3) {
         short var4 = (short)(var3[4] ^ this.ROL16(var3[1], 1));
         short var5 = (short)(var3[0] ^ this.ROL16(var3[2], 1));
         short var6 = (short)(var3[1] ^ this.ROL16(var3[3], 1));
         short var7 = (short)(var3[2] ^ this.ROL16(var3[4], 1));
         short var8 = (short)(var3[3] ^ this.ROL16(var3[0], 1));
         short var9 = var1[0] ^= var4;
         var1[6] ^= var5;
         short var10 = this.ROL16(var1[6], 12);
         var1[12] ^= var6;
         short var11 = this.ROL16(var1[12], 11);
         var1[18] ^= var7;
         short var12 = this.ROL16(var1[18], 5);
         var1[24] ^= var8;
         short var13 = this.ROL16(var1[24], 14);
         var2[0] = (short)(var9 ^ ~var10 & var11 ^ this.KeccakF400RoundConstants[19]);
         var2[1] = (short)(var10 ^ ~var11 & var12);
         var2[2] = (short)(var11 ^ ~var12 & var13);
         var2[3] = (short)(var12 ^ ~var13 & var9);
         var2[4] = (short)(var13 ^ ~var9 & var10);
         var1[3] ^= var7;
         var9 = this.ROL16(var1[3], 12);
         var1[9] ^= var8;
         var10 = this.ROL16(var1[9], 4);
         var1[10] ^= var4;
         var11 = this.ROL16(var1[10], 3);
         var1[16] ^= var5;
         var12 = this.ROL16(var1[16], 13);
         var1[22] ^= var6;
         var13 = this.ROL16(var1[22], 13);
         var2[5] = (short)(var9 ^ ~var10 & var11);
         var2[6] = (short)(var10 ^ ~var11 & var12);
         var2[7] = (short)(var11 ^ ~var12 & var13);
         var2[8] = (short)(var12 ^ ~var13 & var9);
         var2[9] = (short)(var13 ^ ~var9 & var10);
         var1[1] ^= var5;
         var9 = this.ROL16(var1[1], 1);
         var1[7] ^= var6;
         var10 = this.ROL16(var1[7], 6);
         var1[13] ^= var7;
         var11 = this.ROL16(var1[13], 9);
         var1[19] ^= var8;
         var12 = this.ROL16(var1[19], 8);
         var1[20] ^= var4;
         var13 = this.ROL16(var1[20], 2);
         var2[10] = (short)(var9 ^ ~var10 & var11);
         var2[11] = (short)(var10 ^ ~var11 & var12);
         var2[12] = (short)(var11 ^ ~var12 & var13);
         var2[13] = (short)(var12 ^ ~var13 & var9);
         var2[14] = (short)(var13 ^ ~var9 & var10);
         var1[4] ^= var8;
         var9 = this.ROL16(var1[4], 11);
         var1[5] ^= var4;
         var10 = this.ROL16(var1[5], 4);
         var1[11] ^= var5;
         var11 = this.ROL16(var1[11], 10);
         var1[17] ^= var6;
         var12 = this.ROL16(var1[17], 15);
         var1[23] ^= var7;
         var13 = this.ROL16(var1[23], 8);
         var2[15] = (short)(var9 ^ ~var10 & var11);
         var2[16] = (short)(var10 ^ ~var11 & var12);
         var2[17] = (short)(var11 ^ ~var12 & var13);
         var2[18] = (short)(var12 ^ ~var13 & var9);
         var2[19] = (short)(var13 ^ ~var9 & var10);
         var1[2] ^= var6;
         var9 = this.ROL16(var1[2], 14);
         var1[8] ^= var7;
         var10 = this.ROL16(var1[8], 7);
         var1[14] ^= var8;
         var11 = this.ROL16(var1[14], 7);
         var1[15] ^= var4;
         var12 = this.ROL16(var1[15], 9);
         var1[21] ^= var5;
         var13 = this.ROL16(var1[21], 2);
         var2[20] = (short)(var9 ^ ~var10 & var11);
         var2[21] = (short)(var10 ^ ~var11 & var12);
         var2[22] = (short)(var11 ^ ~var12 & var13);
         var2[23] = (short)(var12 ^ ~var13 & var9);
         var2[24] = (short)(var13 ^ ~var9 & var10);
      }
   }

   private class ISAPAEAD_K_128 extends ISAPEngine.ISAPAEAD_K {
      public ISAPAEAD_K_128() {
         this.ISAP_IV1_16 = new short[]{-32767, 400, 3092, 3084};
         this.ISAP_IV2_16 = new short[]{-32766, 400, 3092, 3084};
         this.ISAP_IV3_16 = new short[]{-32765, 400, 3092, 3084};
      }

      @Override
      protected void PermuteRoundsHX(short[] var1, short[] var2, short[] var3) {
         this.prepareThetaX(var1, var3);
         this.thetaRhoPiChiIotaPrepareTheta(0, var1, var2, var3);
         this.thetaRhoPiChiIotaPrepareTheta(1, var2, var1, var3);
         this.thetaRhoPiChiIotaPrepareTheta(2, var1, var2, var3);
         this.thetaRhoPiChiIotaPrepareTheta(3, var2, var1, var3);
         this.rounds_4_18(var1, var2, var3);
      }

      @Override
      protected void PermuteRoundsKX(short[] var1, short[] var2, short[] var3) {
         this.rounds12X(var1, var2, var3);
      }

      @Override
      protected void PermuteRoundsBX(short[] var1, short[] var2, short[] var3) {
         this.rounds12X(var1, var2, var3);
      }
   }

   private class ISAPAEAD_K_128A extends ISAPEngine.ISAPAEAD_K {
      public ISAPAEAD_K_128A() {
         this.ISAP_IV1_16 = new short[]{-32767, 400, 272, 2056};
         this.ISAP_IV2_16 = new short[]{-32766, 400, 272, 2056};
         this.ISAP_IV3_16 = new short[]{-32765, 400, 272, 2056};
      }

      @Override
      protected void PermuteRoundsHX(short[] var1, short[] var2, short[] var3) {
         this.prepareThetaX(var1, var3);
         this.rounds_4_18(var1, var2, var3);
      }

      @Override
      protected void PermuteRoundsKX(short[] var1, short[] var2, short[] var3) {
         this.prepareThetaX(var1, var3);
         this.rounds_12_18(var1, var2, var3);
      }

      @Override
      protected void PermuteRoundsBX(short[] var1, short[] var2, short[] var3) {
         this.prepareThetaX(var1, var3);
         this.thetaRhoPiChiIotaPrepareTheta(19, var1, var2, var3);
         System.arraycopy(var2, 0, var1, 0, var2.length);
      }
   }

   private interface ISAP_AEAD {
      void init();

      void reset();

      void absorbMacBlock(byte[] var1, int var2);

      void absorbFinalAADBlock();

      void processEncBlock(byte[] var1, int var2, byte[] var3, int var4);

      void processEncFinalBlock(byte[] var1, int var2);

      void processMACFinal(byte[] var1, int var2, int var3, byte[] var4);
   }

   public static enum IsapType {
      ISAP_A_128A,
      ISAP_K_128A,
      ISAP_A_128,
      ISAP_K_128;
   }
}
