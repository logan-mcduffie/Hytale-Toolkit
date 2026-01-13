package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.CryptoServiceProperties;
import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.util.Memoable;
import org.bouncycastle.util.Pack;

public abstract class LongDigest implements ExtendedDigest, Memoable, EncodableDigest {
   private static final int BYTE_LENGTH = 128;
   protected final CryptoServicePurpose purpose;
   private byte[] xBuf = new byte[8];
   private int xBufOff;
   private long byteCount1;
   private long byteCount2;
   protected long H1;
   protected long H2;
   protected long H3;
   protected long H4;
   protected long H5;
   protected long H6;
   protected long H7;
   protected long H8;
   private long[] W = new long[80];
   private int wOff;
   static final long[] K = new long[]{
      4794697086780616226L,
      8158064640168781261L,
      -5349999486874862801L,
      -1606136188198331460L,
      4131703408338449720L,
      6480981068601479193L,
      -7908458776815382629L,
      -6116909921290321640L,
      -2880145864133508542L,
      1334009975649890238L,
      2608012711638119052L,
      6128411473006802146L,
      8268148722764581231L,
      -9160688886553864527L,
      -7215885187991268811L,
      -4495734319001033068L,
      -1973867731355612462L,
      -1171420211273849373L,
      1135362057144423861L,
      2597628984639134821L,
      3308224258029322869L,
      5365058923640841347L,
      6679025012923562964L,
      8573033837759648693L,
      -7476448914759557205L,
      -6327057829258317296L,
      -5763719355590565569L,
      -4658551843659510044L,
      -4116276920077217854L,
      -3051310485924567259L,
      489312712824947311L,
      1452737877330783856L,
      2861767655752347644L,
      3322285676063803686L,
      5560940570517711597L,
      5996557281743188959L,
      7280758554555802590L,
      8532644243296465576L,
      -9096487096722542874L,
      -7894198246740708037L,
      -6719396339535248540L,
      -6333637450476146687L,
      -4446306890439682159L,
      -4076793802049405392L,
      -3345356375505022440L,
      -2983346525034927856L,
      -860691631967231958L,
      1182934255886127544L,
      1847814050463011016L,
      2177327727835720531L,
      2830643537854262169L,
      3796741975233480872L,
      4115178125766777443L,
      5681478168544905931L,
      6601373596472566643L,
      7507060721942968483L,
      8399075790359081724L,
      8693463985226723168L,
      -8878714635349349518L,
      -8302665154208450068L,
      -8016688836872298968L,
      -6606660893046293015L,
      -4685533653050689259L,
      -4147400797238176981L,
      -3880063495543823972L,
      -3348786107499101689L,
      -1523767162380948706L,
      -757361751448694408L,
      500013540394364858L,
      748580250866718886L,
      1242879168328830382L,
      1977374033974150939L,
      2944078676154940804L,
      3659926193048069267L,
      4368137639120453308L,
      4836135668995329356L,
      5532061633213252278L,
      6448918945643986474L,
      6902733635092675308L,
      7801388544844847127L
   };

   protected LongDigest() {
      this(CryptoServicePurpose.ANY);
   }

   protected LongDigest(CryptoServicePurpose var1) {
      this.purpose = var1;
      this.xBufOff = 0;
      this.reset();
   }

   protected LongDigest(LongDigest var1) {
      this.purpose = var1.purpose;
      this.copyIn(var1);
   }

   protected void copyIn(LongDigest var1) {
      System.arraycopy(var1.xBuf, 0, this.xBuf, 0, var1.xBuf.length);
      this.xBufOff = var1.xBufOff;
      this.byteCount1 = var1.byteCount1;
      this.byteCount2 = var1.byteCount2;
      this.H1 = var1.H1;
      this.H2 = var1.H2;
      this.H3 = var1.H3;
      this.H4 = var1.H4;
      this.H5 = var1.H5;
      this.H6 = var1.H6;
      this.H7 = var1.H7;
      this.H8 = var1.H8;
      System.arraycopy(var1.W, 0, this.W, 0, var1.W.length);
      this.wOff = var1.wOff;
   }

   protected void populateState(byte[] var1) {
      System.arraycopy(this.xBuf, 0, var1, 0, this.xBufOff);
      Pack.intToBigEndian(this.xBufOff, var1, 8);
      Pack.longToBigEndian(this.byteCount1, var1, 12);
      Pack.longToBigEndian(this.byteCount2, var1, 20);
      Pack.longToBigEndian(this.H1, var1, 28);
      Pack.longToBigEndian(this.H2, var1, 36);
      Pack.longToBigEndian(this.H3, var1, 44);
      Pack.longToBigEndian(this.H4, var1, 52);
      Pack.longToBigEndian(this.H5, var1, 60);
      Pack.longToBigEndian(this.H6, var1, 68);
      Pack.longToBigEndian(this.H7, var1, 76);
      Pack.longToBigEndian(this.H8, var1, 84);
      Pack.intToBigEndian(this.wOff, var1, 92);

      for (int var2 = 0; var2 < this.wOff; var2++) {
         Pack.longToBigEndian(this.W[var2], var1, 96 + var2 * 8);
      }
   }

   protected void restoreState(byte[] var1) {
      this.xBufOff = Pack.bigEndianToInt(var1, 8);
      System.arraycopy(var1, 0, this.xBuf, 0, this.xBufOff);
      this.byteCount1 = Pack.bigEndianToLong(var1, 12);
      this.byteCount2 = Pack.bigEndianToLong(var1, 20);
      this.H1 = Pack.bigEndianToLong(var1, 28);
      this.H2 = Pack.bigEndianToLong(var1, 36);
      this.H3 = Pack.bigEndianToLong(var1, 44);
      this.H4 = Pack.bigEndianToLong(var1, 52);
      this.H5 = Pack.bigEndianToLong(var1, 60);
      this.H6 = Pack.bigEndianToLong(var1, 68);
      this.H7 = Pack.bigEndianToLong(var1, 76);
      this.H8 = Pack.bigEndianToLong(var1, 84);
      this.wOff = Pack.bigEndianToInt(var1, 92);

      for (int var2 = 0; var2 < this.wOff; var2++) {
         this.W[var2] = Pack.bigEndianToLong(var1, 96 + var2 * 8);
      }
   }

   protected int getEncodedStateSize() {
      return 96 + this.wOff * 8;
   }

   @Override
   public void update(byte var1) {
      this.xBuf[this.xBufOff++] = var1;
      if (this.xBufOff == this.xBuf.length) {
         this.processWord(this.xBuf, 0);
         this.xBufOff = 0;
      }

      this.byteCount1++;
   }

   @Override
   public void update(byte[] var1, int var2, int var3) {
      while (this.xBufOff != 0 && var3 > 0) {
         this.update(var1[var2]);
         var2++;
         var3--;
      }

      while (var3 >= this.xBuf.length) {
         this.processWord(var1, var2);
         var2 += this.xBuf.length;
         var3 -= this.xBuf.length;
         this.byteCount1 = this.byteCount1 + this.xBuf.length;
      }

      while (var3 > 0) {
         this.update(var1[var2]);
         var2++;
         var3--;
      }
   }

   public void finish() {
      this.adjustByteCounts();
      long var1 = this.byteCount1 << 3;
      long var3 = this.byteCount2;
      this.update((byte)-128);

      while (this.xBufOff != 0) {
         this.update((byte)0);
      }

      this.processLength(var1, var3);
      this.processBlock();
   }

   @Override
   public void reset() {
      this.byteCount1 = 0L;
      this.byteCount2 = 0L;
      this.xBufOff = 0;

      for (int var1 = 0; var1 < this.xBuf.length; var1++) {
         this.xBuf[var1] = 0;
      }

      this.wOff = 0;

      for (int var2 = 0; var2 != this.W.length; var2++) {
         this.W[var2] = 0L;
      }
   }

   @Override
   public int getByteLength() {
      return 128;
   }

   protected void processWord(byte[] var1, int var2) {
      this.W[this.wOff] = Pack.bigEndianToLong(var1, var2);
      if (++this.wOff == 16) {
         this.processBlock();
      }
   }

   private void adjustByteCounts() {
      if (this.byteCount1 > 2305843009213693951L) {
         this.byteCount2 = this.byteCount2 + (this.byteCount1 >>> 61);
         this.byteCount1 &= 2305843009213693951L;
      }
   }

   protected void processLength(long var1, long var3) {
      if (this.wOff > 14) {
         this.processBlock();
      }

      this.W[14] = var3;
      this.W[15] = var1;
   }

   protected void processBlock() {
      this.adjustByteCounts();

      for (int var1 = 16; var1 <= 79; var1++) {
         this.W[var1] = this.Sigma1(this.W[var1 - 2]) + this.W[var1 - 7] + this.Sigma0(this.W[var1 - 15]) + this.W[var1 - 16];
      }

      long var19 = this.H1;
      long var3 = this.H2;
      long var5 = this.H3;
      long var7 = this.H4;
      long var9 = this.H5;
      long var11 = this.H6;
      long var13 = this.H7;
      long var15 = this.H8;
      int var17 = 0;

      for (int var18 = 0; var18 < 10; var18++) {
         long var34 = var15 + this.Sum1(var9) + this.Ch(var9, var11, var13) + K[var17] + this.W[var17++];
         long var26 = var7 + var34;
         long var35 = var34 + this.Sum0(var19) + this.Maj(var19, var3, var5);
         long var32 = var13 + this.Sum1(var26) + this.Ch(var26, var9, var11) + K[var17] + this.W[var17++];
         long var24 = var5 + var32;
         long var33 = var32 + this.Sum0(var35) + this.Maj(var35, var19, var3);
         long var30 = var11 + this.Sum1(var24) + this.Ch(var24, var26, var9) + K[var17] + this.W[var17++];
         long var22 = var3 + var30;
         long var31 = var30 + this.Sum0(var33) + this.Maj(var33, var35, var19);
         var9 += this.Sum1(var22) + this.Ch(var22, var24, var26) + K[var17] + this.W[var17++];
         var19 += var9;
         var9 += this.Sum0(var31) + this.Maj(var31, var33, var35);
         long var27 = var26 + this.Sum1(var19) + this.Ch(var19, var22, var24) + K[var17] + this.W[var17++];
         var15 = var35 + var27;
         var7 = var27 + this.Sum0(var9) + this.Maj(var9, var31, var33);
         long var25 = var24 + this.Sum1(var15) + this.Ch(var15, var19, var22) + K[var17] + this.W[var17++];
         var13 = var33 + var25;
         var5 = var25 + this.Sum0(var7) + this.Maj(var7, var9, var31);
         long var23 = var22 + this.Sum1(var13) + this.Ch(var13, var15, var19) + K[var17] + this.W[var17++];
         var11 = var31 + var23;
         var3 = var23 + this.Sum0(var5) + this.Maj(var5, var7, var9);
         var19 += this.Sum1(var11) + this.Ch(var11, var13, var15) + K[var17] + this.W[var17++];
         var9 += var19;
         var19 += this.Sum0(var3) + this.Maj(var3, var5, var7);
      }

      this.H1 += var19;
      this.H2 += var3;
      this.H3 += var5;
      this.H4 += var7;
      this.H5 += var9;
      this.H6 += var11;
      this.H7 += var13;
      this.H8 += var15;
      this.wOff = 0;

      for (int var43 = 0; var43 < 16; var43++) {
         this.W[var43] = 0L;
      }
   }

   private long Ch(long var1, long var3, long var5) {
      return var1 & var3 ^ ~var1 & var5;
   }

   private long Maj(long var1, long var3, long var5) {
      return var1 & var3 ^ var1 & var5 ^ var3 & var5;
   }

   private long Sum0(long var1) {
      return (var1 << 36 | var1 >>> 28) ^ (var1 << 30 | var1 >>> 34) ^ (var1 << 25 | var1 >>> 39);
   }

   private long Sum1(long var1) {
      return (var1 << 50 | var1 >>> 14) ^ (var1 << 46 | var1 >>> 18) ^ (var1 << 23 | var1 >>> 41);
   }

   private long Sigma0(long var1) {
      return (var1 << 63 | var1 >>> 1) ^ (var1 << 56 | var1 >>> 8) ^ var1 >>> 7;
   }

   private long Sigma1(long var1) {
      return (var1 << 45 | var1 >>> 19) ^ (var1 << 3 | var1 >>> 61) ^ var1 >>> 6;
   }

   protected abstract CryptoServiceProperties cryptoServiceProperties();
}
