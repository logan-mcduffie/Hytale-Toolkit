package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.crypto.Xof;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Pack;

public final class Kangaroo {
   private static final int DIGESTLEN = 32;

   abstract static class KangarooBase implements ExtendedDigest, Xof {
      private static final int BLKSIZE = 8192;
      private static final byte[] SINGLE = new byte[]{7};
      private static final byte[] INTERMEDIATE = new byte[]{11};
      private static final byte[] FINAL = new byte[]{-1, -1, 6};
      private static final byte[] FIRST = new byte[]{3, 0, 0, 0, 0, 0, 0, 0};
      private final byte[] singleByte = new byte[1];
      private final Kangaroo.KangarooSponge theTree;
      private final Kangaroo.KangarooSponge theLeaf;
      private final int theChainLen;
      private byte[] thePersonal;
      private boolean squeezing;
      private int theCurrNode;
      private int theProcessed;

      KangarooBase(int var1, int var2, int var3, CryptoServicePurpose var4) {
         this.theTree = new Kangaroo.KangarooSponge(var1, var2);
         this.theLeaf = new Kangaroo.KangarooSponge(var1, var2);
         this.theChainLen = var1 >> 2;
         this.buildPersonal(null);
         CryptoServicesRegistrar.checkConstraints(Utils.getDefaultProperties(this, var1, var4));
      }

      private void buildPersonal(byte[] var1) {
         int var2 = var1 == null ? 0 : var1.length;
         byte[] var3 = lengthEncode(var2);
         this.thePersonal = var1 == null ? new byte[var2 + var3.length] : Arrays.copyOf(var1, var2 + var3.length);
         System.arraycopy(var3, 0, this.thePersonal, var2, var3.length);
      }

      @Override
      public int getByteLength() {
         return this.theTree.theRateBytes;
      }

      @Override
      public int getDigestSize() {
         return this.theChainLen >> 1;
      }

      public void init(Kangaroo.KangarooParameters var1) {
         this.buildPersonal(var1.getPersonalisation());
         this.reset();
      }

      @Override
      public void update(byte var1) {
         this.singleByte[0] = var1;
         this.update(this.singleByte, 0, 1);
      }

      @Override
      public void update(byte[] var1, int var2, int var3) {
         this.processData(var1, var2, var3);
      }

      @Override
      public int doFinal(byte[] var1, int var2) {
         return this.doFinal(var1, var2, this.getDigestSize());
      }

      @Override
      public int doFinal(byte[] var1, int var2, int var3) {
         if (this.squeezing) {
            throw new IllegalStateException("Already outputting");
         } else {
            int var4 = this.doOutput(var1, var2, var3);
            this.reset();
            return var4;
         }
      }

      @Override
      public int doOutput(byte[] var1, int var2, int var3) {
         if (!this.squeezing) {
            this.switchToSqueezing();
         }

         if (var3 < 0) {
            throw new IllegalArgumentException("Invalid output length");
         } else {
            this.theTree.squeeze(var1, var2, var3);
            return var3;
         }
      }

      private void processData(byte[] var1, int var2, int var3) {
         if (this.squeezing) {
            throw new IllegalStateException("attempt to absorb while squeezing");
         } else {
            Kangaroo.KangarooSponge var4 = this.theCurrNode == 0 ? this.theTree : this.theLeaf;
            int var5 = 8192 - this.theProcessed;
            if (var5 >= var3) {
               var4.absorb(var1, var2, var3);
               this.theProcessed += var3;
            } else {
               if (var5 > 0) {
                  var4.absorb(var1, var2, var5);
                  this.theProcessed += var5;
               }

               int var6 = var5;

               while (var6 < var3) {
                  if (this.theProcessed == 8192) {
                     this.switchLeaf(true);
                  }

                  int var7 = Math.min(var3 - var6, 8192);
                  this.theLeaf.absorb(var1, var2 + var6, var7);
                  this.theProcessed += var7;
                  var6 += var7;
               }
            }
         }
      }

      @Override
      public void reset() {
         this.theTree.initSponge();
         this.theLeaf.initSponge();
         this.theCurrNode = 0;
         this.theProcessed = 0;
         this.squeezing = false;
      }

      private void switchLeaf(boolean var1) {
         if (this.theCurrNode == 0) {
            this.theTree.absorb(FIRST, 0, FIRST.length);
         } else {
            this.theLeaf.absorb(INTERMEDIATE, 0, INTERMEDIATE.length);
            byte[] var2 = new byte[this.theChainLen];
            this.theLeaf.squeeze(var2, 0, this.theChainLen);
            this.theTree.absorb(var2, 0, this.theChainLen);
            this.theLeaf.initSponge();
         }

         if (var1) {
            this.theCurrNode++;
         }

         this.theProcessed = 0;
      }

      private void switchToSqueezing() {
         this.processData(this.thePersonal, 0, this.thePersonal.length);
         if (this.theCurrNode == 0) {
            this.switchSingle();
         } else {
            this.switchFinal();
         }
      }

      private void switchSingle() {
         this.theTree.absorb(SINGLE, 0, 1);
         this.theTree.padAndSwitchToSqueezingPhase();
      }

      private void switchFinal() {
         this.switchLeaf(false);
         byte[] var1 = lengthEncode(this.theCurrNode);
         this.theTree.absorb(var1, 0, var1.length);
         this.theTree.absorb(FINAL, 0, FINAL.length);
         this.theTree.padAndSwitchToSqueezingPhase();
      }

      private static byte[] lengthEncode(long var0) {
         byte var2 = 0;
         long var3 = var0;
         if (var0 != 0L) {
            var2 = 1;

            while ((var3 >>= 8) != 0L) {
               var2++;
            }
         }

         byte[] var5 = new byte[var2 + 1];
         var5[var2] = var2;

         for (int var6 = 0; var6 < var2; var6++) {
            var5[var6] = (byte)(var0 >> 8 * (var2 - var6 - 1));
         }

         return var5;
      }
   }

   public static class KangarooParameters implements CipherParameters {
      private byte[] thePersonal;

      public byte[] getPersonalisation() {
         return Arrays.clone(this.thePersonal);
      }

      public static class Builder {
         private byte[] thePersonal;

         public Kangaroo.KangarooParameters.Builder setPersonalisation(byte[] var1) {
            this.thePersonal = Arrays.clone(var1);
            return this;
         }

         public Kangaroo.KangarooParameters build() {
            Kangaroo.KangarooParameters var1 = new Kangaroo.KangarooParameters();
            if (this.thePersonal != null) {
               var1.thePersonal = this.thePersonal;
            }

            return var1;
         }
      }
   }

   private static class KangarooSponge {
      private static final long[] KeccakRoundConstants = new long[]{
         1L,
         32898L,
         -9223372036854742902L,
         -9223372034707259392L,
         32907L,
         2147483649L,
         -9223372034707259263L,
         -9223372036854743031L,
         138L,
         136L,
         2147516425L,
         2147483658L,
         2147516555L,
         -9223372036854775669L,
         -9223372036854742903L,
         -9223372036854743037L,
         -9223372036854743038L,
         -9223372036854775680L,
         32778L,
         -9223372034707292150L,
         -9223372034707259263L,
         -9223372036854742912L,
         2147483649L,
         -9223372034707259384L
      };
      private final int theRounds;
      private final int theRateBytes;
      private final long[] theState = new long[25];
      private final byte[] theQueue;
      private int bytesInQueue;
      private boolean squeezing;

      KangarooSponge(int var1, int var2) {
         this.theRateBytes = 1600 - (var1 << 1) >> 3;
         this.theRounds = var2;
         this.theQueue = new byte[this.theRateBytes];
         this.initSponge();
      }

      private void initSponge() {
         Arrays.fill(this.theState, 0L);
         Arrays.fill(this.theQueue, (byte)0);
         this.bytesInQueue = 0;
         this.squeezing = false;
      }

      private void absorb(byte[] var1, int var2, int var3) {
         if (this.squeezing) {
            throw new IllegalStateException("attempt to absorb while squeezing");
         } else {
            int var4 = 0;

            while (var4 < var3) {
               if (this.bytesInQueue == this.theRateBytes) {
                  this.KangarooAbsorb(this.theQueue, 0);
                  this.bytesInQueue = 0;
               }

               if (this.bytesInQueue == 0 && var4 <= var3 - this.theRateBytes) {
                  while (true) {
                     this.KangarooAbsorb(var1, var2 + var4);
                     var4 += this.theRateBytes;
                     if (var4 > var3 - this.theRateBytes) {
                        break;
                     }
                  }
               } else {
                  int var5 = Math.min(this.theRateBytes - this.bytesInQueue, var3 - var4);
                  System.arraycopy(var1, var2 + var4, this.theQueue, this.bytesInQueue, var5);
                  this.bytesInQueue += var5;
                  var4 += var5;
               }
            }
         }
      }

      private void padAndSwitchToSqueezingPhase() {
         for (int var1 = this.bytesInQueue; var1 < this.theRateBytes; var1++) {
            this.theQueue[var1] = 0;
         }

         this.theQueue[this.theRateBytes - 1] = (byte)(this.theQueue[this.theRateBytes - 1] ^ 128);
         this.KangarooAbsorb(this.theQueue, 0);
         this.KangarooExtract();
         this.bytesInQueue = this.theRateBytes;
         this.squeezing = true;
      }

      private void squeeze(byte[] var1, int var2, int var3) {
         if (!this.squeezing) {
            this.padAndSwitchToSqueezingPhase();
         }

         int var4 = 0;

         while (var4 < var3) {
            if (this.bytesInQueue == 0) {
               this.KangarooPermutation();
               this.KangarooExtract();
               this.bytesInQueue = this.theRateBytes;
            }

            int var5 = Math.min(this.bytesInQueue, var3 - var4);
            System.arraycopy(this.theQueue, this.theRateBytes - this.bytesInQueue, var1, var2 + var4, var5);
            this.bytesInQueue -= var5;
            var4 += var5;
         }
      }

      private void KangarooAbsorb(byte[] var1, int var2) {
         int var3 = this.theRateBytes >> 3;
         int var4 = var2;

         for (int var5 = 0; var5 < var3; var5++) {
            this.theState[var5] = this.theState[var5] ^ Pack.littleEndianToLong(var1, var4);
            var4 += 8;
         }

         this.KangarooPermutation();
      }

      private void KangarooExtract() {
         Pack.longToLittleEndian(this.theState, 0, this.theRateBytes >> 3, this.theQueue, 0);
      }

      private void KangarooPermutation() {
         long[] var1 = this.theState;
         long var2 = var1[0];
         long var4 = var1[1];
         long var6 = var1[2];
         long var8 = var1[3];
         long var10 = var1[4];
         long var12 = var1[5];
         long var14 = var1[6];
         long var16 = var1[7];
         long var18 = var1[8];
         long var20 = var1[9];
         long var22 = var1[10];
         long var24 = var1[11];
         long var26 = var1[12];
         long var28 = var1[13];
         long var30 = var1[14];
         long var32 = var1[15];
         long var34 = var1[16];
         long var36 = var1[17];
         long var38 = var1[18];
         long var40 = var1[19];
         long var42 = var1[20];
         long var44 = var1[21];
         long var46 = var1[22];
         long var48 = var1[23];
         long var50 = var1[24];
         int var52 = KeccakRoundConstants.length - this.theRounds;

         for (int var53 = 0; var53 < this.theRounds; var53++) {
            long var54 = var2 ^ var12 ^ var22 ^ var32 ^ var42;
            long var56 = var4 ^ var14 ^ var24 ^ var34 ^ var44;
            long var58 = var6 ^ var16 ^ var26 ^ var36 ^ var46;
            long var60 = var8 ^ var18 ^ var28 ^ var38 ^ var48;
            long var62 = var10 ^ var20 ^ var30 ^ var40 ^ var50;
            long var64 = (var56 << 1 | var56 >>> -1) ^ var62;
            long var66 = (var58 << 1 | var58 >>> -1) ^ var54;
            long var68 = (var60 << 1 | var60 >>> -1) ^ var56;
            long var70 = (var62 << 1 | var62 >>> -1) ^ var58;
            long var72 = (var54 << 1 | var54 >>> -1) ^ var60;
            var2 ^= var64;
            var12 ^= var64;
            var22 ^= var64;
            var32 ^= var64;
            var42 ^= var64;
            var4 ^= var66;
            var14 ^= var66;
            var24 ^= var66;
            var34 ^= var66;
            var44 ^= var66;
            var6 ^= var68;
            var16 ^= var68;
            var26 ^= var68;
            var36 ^= var68;
            var46 ^= var68;
            var8 ^= var70;
            var18 ^= var70;
            var28 ^= var70;
            var38 ^= var70;
            var48 ^= var70;
            var10 ^= var72;
            var20 ^= var72;
            var30 ^= var72;
            var40 ^= var72;
            var50 ^= var72;
            var56 = var4 << 1 | var4 >>> 63;
            var4 = var14 << 44 | var14 >>> 20;
            var14 = var20 << 20 | var20 >>> 44;
            var20 = var46 << 61 | var46 >>> 3;
            var46 = var30 << 39 | var30 >>> 25;
            var30 = var42 << 18 | var42 >>> 46;
            var42 = var6 << 62 | var6 >>> 2;
            var6 = var26 << 43 | var26 >>> 21;
            var26 = var28 << 25 | var28 >>> 39;
            var28 = var40 << 8 | var40 >>> 56;
            var40 = var48 << 56 | var48 >>> 8;
            var48 = var32 << 41 | var32 >>> 23;
            var32 = var10 << 27 | var10 >>> 37;
            var10 = var50 << 14 | var50 >>> 50;
            var50 = var44 << 2 | var44 >>> 62;
            var44 = var18 << 55 | var18 >>> 9;
            var18 = var34 << 45 | var34 >>> 19;
            var34 = var12 << 36 | var12 >>> 28;
            var12 = var8 << 28 | var8 >>> 36;
            var8 = var38 << 21 | var38 >>> 43;
            var38 = var36 << 15 | var36 >>> 49;
            var36 = var24 << 10 | var24 >>> 54;
            var24 = var16 << 6 | var16 >>> 58;
            var16 = var22 << 3 | var22 >>> 61;
            var54 = var2 ^ ~var4 & var6;
            long var130 = var4 ^ ~var6 & var8;
            var6 ^= ~var8 & var10;
            var8 ^= ~var10 & var2;
            var10 ^= ~var2 & var4;
            var4 = var130;
            long var125 = var12 ^ ~var14 & var16;
            long var131 = var14 ^ ~var16 & var18;
            var16 ^= ~var18 & var20;
            var18 ^= ~var20 & var12;
            var20 ^= ~var12 & var14;
            var12 = var125;
            var14 = var131;
            long var126 = var56 ^ ~var24 & var26;
            long var132 = var24 ^ ~var26 & var28;
            var26 ^= ~var28 & var30;
            var28 ^= ~var30 & var56;
            var30 ^= ~var56 & var24;
            var22 = var126;
            var24 = var132;
            long var127 = var32 ^ ~var34 & var36;
            var56 = var34 ^ ~var36 & var38;
            var36 ^= ~var38 & var40;
            var38 ^= ~var40 & var32;
            var40 ^= ~var32 & var34;
            var32 = var127;
            var34 = var56;
            long var128 = var42 ^ ~var44 & var46;
            var56 = var44 ^ ~var46 & var48;
            var46 ^= ~var48 & var50;
            var48 ^= ~var50 & var42;
            var50 ^= ~var42 & var44;
            var42 = var128;
            var44 = var56;
            var2 = var54 ^ KeccakRoundConstants[var52 + var53];
         }

         var1[0] = var2;
         var1[1] = var4;
         var1[2] = var6;
         var1[3] = var8;
         var1[4] = var10;
         var1[5] = var12;
         var1[6] = var14;
         var1[7] = var16;
         var1[8] = var18;
         var1[9] = var20;
         var1[10] = var22;
         var1[11] = var24;
         var1[12] = var26;
         var1[13] = var28;
         var1[14] = var30;
         var1[15] = var32;
         var1[16] = var34;
         var1[17] = var36;
         var1[18] = var38;
         var1[19] = var40;
         var1[20] = var42;
         var1[21] = var44;
         var1[22] = var46;
         var1[23] = var48;
         var1[24] = var50;
      }
   }

   public static class KangarooTwelve extends Kangaroo.KangarooBase {
      public KangarooTwelve() {
         this(32, CryptoServicePurpose.ANY);
      }

      public KangarooTwelve(int var1, CryptoServicePurpose var2) {
         super(128, 12, var1, var2);
      }

      public KangarooTwelve(CryptoServicePurpose var1) {
         this(32, var1);
      }

      @Override
      public String getAlgorithmName() {
         return "KangarooTwelve";
      }
   }

   public static class MarsupilamiFourteen extends Kangaroo.KangarooBase {
      public MarsupilamiFourteen() {
         this(32, CryptoServicePurpose.ANY);
      }

      public MarsupilamiFourteen(int var1, CryptoServicePurpose var2) {
         super(256, 14, var1, var2);
      }

      public MarsupilamiFourteen(CryptoServicePurpose var1) {
         this(32, var1);
      }

      @Override
      public String getAlgorithmName() {
         return "MarsupilamiFourteen";
      }
   }
}
