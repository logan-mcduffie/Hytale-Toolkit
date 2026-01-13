package org.bouncycastle.crypto.generators;

import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.Blake2bDigest;
import org.bouncycastle.crypto.params.Argon2Parameters;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Longs;
import org.bouncycastle.util.Pack;

public class Argon2BytesGenerator {
   private static final int ARGON2_BLOCK_SIZE = 1024;
   private static final int ARGON2_QWORDS_IN_BLOCK = 128;
   private static final int ARGON2_ADDRESSES_IN_BLOCK = 128;
   private static final int ARGON2_PREHASH_DIGEST_LENGTH = 64;
   private static final int ARGON2_PREHASH_SEED_LENGTH = 72;
   private static final int ARGON2_SYNC_POINTS = 4;
   private static final int MIN_PARALLELISM = 1;
   private static final int MAX_PARALLELISM = 16777215;
   private static final int MIN_OUTLEN = 4;
   private static final int MIN_ITERATIONS = 1;
   private static final long M32L = 4294967295L;
   private static final byte[] ZERO_BYTES = new byte[4];
   private Argon2Parameters parameters;
   private Argon2BytesGenerator.Block[] memory;
   private int segmentLength;
   private int laneLength;

   public void init(Argon2Parameters var1) {
      if (var1.getVersion() != 16 && var1.getVersion() != 19) {
         throw new UnsupportedOperationException("unknown Argon2 version");
      } else if (var1.getType() != 0 && var1.getType() != 1 && var1.getType() != 2) {
         throw new UnsupportedOperationException("unknown Argon2 type");
      } else if (var1.getLanes() < 1) {
         throw new IllegalStateException("lanes must be at least 1");
      } else if (var1.getLanes() > 16777215) {
         throw new IllegalStateException("lanes must be at most 16777215");
      } else if (var1.getIterations() < 1) {
         throw new IllegalStateException("iterations is less than: 1");
      } else {
         this.parameters = var1;
         int var2 = Math.max(var1.getMemory(), 8 * var1.getLanes());
         this.segmentLength = var2 / (4 * var1.getLanes());
         this.laneLength = this.segmentLength * 4;
         var2 = var1.getLanes() * this.laneLength;
         this.memory = new Argon2BytesGenerator.Block[var2];

         for (int var3 = 0; var3 < this.memory.length; var3++) {
            this.memory[var3] = new Argon2BytesGenerator.Block();
         }
      }
   }

   public int generateBytes(char[] var1, byte[] var2) {
      return this.generateBytes(this.parameters.getCharToByteConverter().convert(var1), var2);
   }

   public int generateBytes(char[] var1, byte[] var2, int var3, int var4) {
      return this.generateBytes(this.parameters.getCharToByteConverter().convert(var1), var2, var3, var4);
   }

   public int generateBytes(byte[] var1, byte[] var2) {
      return this.generateBytes(var1, var2, 0, var2.length);
   }

   public int generateBytes(byte[] var1, byte[] var2, int var3, int var4) {
      if (var4 < 4) {
         throw new IllegalStateException("output length less than 4");
      } else {
         byte[] var5 = new byte[1024];
         this.initialize(var5, var1, var4);
         this.fillMemoryBlocks();
         this.digest(var5, var2, var3, var4);
         this.reset();
         return var4;
      }
   }

   private void reset() {
      if (null != this.memory) {
         for (int var1 = 0; var1 < this.memory.length; var1++) {
            Argon2BytesGenerator.Block var2 = this.memory[var1];
            if (null != var2) {
               var2.clear();
            }
         }
      }
   }

   private void fillMemoryBlocks() {
      Argon2BytesGenerator.FillBlock var1 = new Argon2BytesGenerator.FillBlock();
      Argon2BytesGenerator.Position var2 = new Argon2BytesGenerator.Position();

      for (int var3 = 0; var3 < this.parameters.getIterations(); var3++) {
         var2.pass = var3;

         for (int var4 = 0; var4 < 4; var4++) {
            var2.slice = var4;

            for (int var5 = 0; var5 < this.parameters.getLanes(); var5++) {
               var2.lane = var5;
               this.fillSegment(var1, var2);
            }
         }
      }
   }

   private void fillSegment(Argon2BytesGenerator.FillBlock var1, Argon2BytesGenerator.Position var2) {
      Argon2BytesGenerator.Block var3 = null;
      Argon2BytesGenerator.Block var4 = null;
      boolean var5 = this.isDataIndependentAddressing(var2);
      int var6 = getStartingIndex(var2);
      int var7 = var2.lane * this.laneLength + var2.slice * this.segmentLength + var6;
      int var8 = this.getPrevOffset(var7);
      if (var5) {
         var3 = var1.addressBlock.clear();
         var4 = var1.inputBlock.clear();
         this.initAddressBlocks(var1, var2, var4, var3);
      }

      boolean var9 = this.isWithXor(var2);

      for (int var10 = var6; var10 < this.segmentLength; var10++) {
         long var11 = this.getPseudoRandom(var1, var10, var3, var4, var8, var5);
         int var13 = this.getRefLane(var2, var11);
         int var14 = this.getRefColumn(var2, var10, var11, var13 == var2.lane);
         Argon2BytesGenerator.Block var15 = this.memory[var8];
         Argon2BytesGenerator.Block var16 = this.memory[this.laneLength * var13 + var14];
         Argon2BytesGenerator.Block var17 = this.memory[var7];
         if (var9) {
            var1.fillBlockWithXor(var15, var16, var17);
         } else {
            var1.fillBlock(var15, var16, var17);
         }

         var8 = var7++;
      }
   }

   private boolean isDataIndependentAddressing(Argon2BytesGenerator.Position var1) {
      return this.parameters.getType() == 1 || this.parameters.getType() == 2 && var1.pass == 0 && var1.slice < 2;
   }

   private void initAddressBlocks(
      Argon2BytesGenerator.FillBlock var1, Argon2BytesGenerator.Position var2, Argon2BytesGenerator.Block var3, Argon2BytesGenerator.Block var4
   ) {
      var3.v[0] = this.intToLong(var2.pass);
      var3.v[1] = this.intToLong(var2.lane);
      var3.v[2] = this.intToLong(var2.slice);
      var3.v[3] = this.intToLong(this.memory.length);
      var3.v[4] = this.intToLong(this.parameters.getIterations());
      var3.v[5] = this.intToLong(this.parameters.getType());
      if (var2.pass == 0 && var2.slice == 0) {
         this.nextAddresses(var1, var3, var4);
      }
   }

   private boolean isWithXor(Argon2BytesGenerator.Position var1) {
      return var1.pass != 0 && this.parameters.getVersion() != 16;
   }

   private int getPrevOffset(int var1) {
      return var1 % this.laneLength == 0 ? var1 + this.laneLength - 1 : var1 - 1;
   }

   private static int getStartingIndex(Argon2BytesGenerator.Position var0) {
      return var0.pass == 0 && var0.slice == 0 ? 2 : 0;
   }

   private void nextAddresses(Argon2BytesGenerator.FillBlock var1, Argon2BytesGenerator.Block var2, Argon2BytesGenerator.Block var3) {
      var2.v[6]++;
      var1.fillBlock(var2, var3);
      var1.fillBlock(var3, var3);
   }

   private long getPseudoRandom(
      Argon2BytesGenerator.FillBlock var1, int var2, Argon2BytesGenerator.Block var3, Argon2BytesGenerator.Block var4, int var5, boolean var6
   ) {
      if (var6) {
         int var7 = var2 % 128;
         if (var7 == 0) {
            this.nextAddresses(var1, var4, var3);
         }

         return var3.v[var7];
      } else {
         return this.memory[var5].v[0];
      }
   }

   private int getRefLane(Argon2BytesGenerator.Position var1, long var2) {
      int var4 = (int)((var2 >>> 32) % this.parameters.getLanes());
      if (var1.pass == 0 && var1.slice == 0) {
         var4 = var1.lane;
      }

      return var4;
   }

   private int getRefColumn(Argon2BytesGenerator.Position var1, int var2, long var3, boolean var5) {
      int var6;
      int var7;
      if (var1.pass == 0) {
         var7 = 0;
         if (var5) {
            var6 = var1.slice * this.segmentLength + var2 - 1;
         } else {
            var6 = var1.slice * this.segmentLength + (var2 == 0 ? -1 : 0);
         }
      } else {
         var7 = (var1.slice + 1) * this.segmentLength % this.laneLength;
         if (var5) {
            var6 = this.laneLength - this.segmentLength + var2 - 1;
         } else {
            var6 = this.laneLength - this.segmentLength + (var2 == 0 ? -1 : 0);
         }
      }

      long var8 = var3 & 4294967295L;
      var8 = var8 * var8 >>> 32;
      var8 = var6 - 1 - (var6 * var8 >>> 32);
      return (int)(var7 + var8) % this.laneLength;
   }

   private void digest(byte[] var1, byte[] var2, int var3, int var4) {
      Argon2BytesGenerator.Block var5 = this.memory[this.laneLength - 1];

      for (int var6 = 1; var6 < this.parameters.getLanes(); var6++) {
         int var7 = var6 * this.laneLength + (this.laneLength - 1);
         var5.xorWith(this.memory[var7]);
      }

      var5.toBytes(var1);
      this.hash(var1, var2, var3, var4);
   }

   private void hash(byte[] var1, byte[] var2, int var3, int var4) {
      byte[] var5 = new byte[4];
      Pack.intToLittleEndian(var4, var5, 0);
      byte var6 = 64;
      if (var4 <= var6) {
         Blake2bDigest var7 = new Blake2bDigest(var4 * 8);
         var7.update(var5, 0, var5.length);
         var7.update(var1, 0, var1.length);
         var7.doFinal(var2, var3);
      } else {
         Blake2bDigest var13 = new Blake2bDigest(var6 * 8);
         byte[] var8 = new byte[var6];
         var13.update(var5, 0, var5.length);
         var13.update(var1, 0, var1.length);
         var13.doFinal(var8, 0);
         int var9 = var6 / 2;
         System.arraycopy(var8, 0, var2, var3, var9);
         int var10 = var3 + var9;
         int var11 = (var4 + 31) / 32 - 2;

         for (int var12 = 2; var12 <= var11; var10 += var9) {
            var13.update(var8, 0, var8.length);
            var13.doFinal(var8, 0);
            System.arraycopy(var8, 0, var2, var10, var9);
            var12++;
         }

         int var15 = var4 - 32 * var11;
         var13 = new Blake2bDigest(var15 * 8);
         var13.update(var8, 0, var8.length);
         var13.doFinal(var2, var10);
      }
   }

   private static void roundFunction(
      Argon2BytesGenerator.Block var0,
      int var1,
      int var2,
      int var3,
      int var4,
      int var5,
      int var6,
      int var7,
      int var8,
      int var9,
      int var10,
      int var11,
      int var12,
      int var13,
      int var14,
      int var15,
      int var16
   ) {
      long[] var17 = var0.v;
      F(var17, var1, var5, var9, var13);
      F(var17, var2, var6, var10, var14);
      F(var17, var3, var7, var11, var15);
      F(var17, var4, var8, var12, var16);
      F(var17, var1, var6, var11, var16);
      F(var17, var2, var7, var12, var13);
      F(var17, var3, var8, var9, var14);
      F(var17, var4, var5, var10, var15);
   }

   private static void F(long[] var0, int var1, int var2, int var3, int var4) {
      quarterRound(var0, var1, var2, var4, 32);
      quarterRound(var0, var3, var4, var2, 24);
      quarterRound(var0, var1, var2, var4, 16);
      quarterRound(var0, var3, var4, var2, 63);
   }

   private static void quarterRound(long[] var0, int var1, int var2, int var3, int var4) {
      long var5 = var0[var1];
      long var7 = var0[var2];
      long var9 = var0[var3];
      var5 += var7 + 2L * (var5 & 4294967295L) * (var7 & 4294967295L);
      var9 = Longs.rotateRight(var9 ^ var5, var4);
      var0[var1] = var5;
      var0[var3] = var9;
   }

   private void initialize(byte[] var1, byte[] var2, int var3) {
      Blake2bDigest var4 = new Blake2bDigest(512);
      int[] var5 = new int[]{
         this.parameters.getLanes(),
         var3,
         this.parameters.getMemory(),
         this.parameters.getIterations(),
         this.parameters.getVersion(),
         this.parameters.getType()
      };
      Pack.intToLittleEndian(var5, var1, 0);
      var4.update(var1, 0, var5.length * 4);
      addByteString(var1, var4, var2);
      addByteString(var1, var4, this.parameters.getSalt());
      addByteString(var1, var4, this.parameters.getSecret());
      addByteString(var1, var4, this.parameters.getAdditional());
      byte[] var6 = new byte[72];
      var4.doFinal(var6, 0);
      this.fillFirstBlocks(var1, var6);
   }

   private static void addByteString(byte[] var0, Digest var1, byte[] var2) {
      if (null == var2) {
         var1.update(ZERO_BYTES, 0, 4);
      } else {
         Pack.intToLittleEndian(var2.length, var0, 0);
         var1.update(var0, 0, 4);
         var1.update(var2, 0, var2.length);
      }
   }

   private void fillFirstBlocks(byte[] var1, byte[] var2) {
      byte[] var3 = new byte[72];
      System.arraycopy(var2, 0, var3, 0, 64);
      var3[64] = 1;

      for (int var4 = 0; var4 < this.parameters.getLanes(); var4++) {
         Pack.intToLittleEndian(var4, var2, 68);
         Pack.intToLittleEndian(var4, var3, 68);
         this.hash(var2, var1, 0, 1024);
         this.memory[var4 * this.laneLength + 0].fromBytes(var1);
         this.hash(var3, var1, 0, 1024);
         this.memory[var4 * this.laneLength + 1].fromBytes(var1);
      }
   }

   private long intToLong(int var1) {
      return var1 & 4294967295L;
   }

   private static class Block {
      private static final int SIZE = 128;
      private final long[] v = new long[128];

      private Block() {
      }

      void fromBytes(byte[] var1) {
         if (var1.length < 1024) {
            throw new IllegalArgumentException("input shorter than blocksize");
         } else {
            Pack.littleEndianToLong(var1, 0, this.v);
         }
      }

      void toBytes(byte[] var1) {
         if (var1.length < 1024) {
            throw new IllegalArgumentException("output shorter than blocksize");
         } else {
            Pack.longToLittleEndian(this.v, var1, 0);
         }
      }

      private void copyBlock(Argon2BytesGenerator.Block var1) {
         System.arraycopy(var1.v, 0, this.v, 0, 128);
      }

      private void xor(Argon2BytesGenerator.Block var1, Argon2BytesGenerator.Block var2) {
         long[] var3 = this.v;
         long[] var4 = var1.v;
         long[] var5 = var2.v;

         for (int var6 = 0; var6 < 128; var6++) {
            var3[var6] = var4[var6] ^ var5[var6];
         }
      }

      private void xorWith(Argon2BytesGenerator.Block var1) {
         long[] var2 = this.v;
         long[] var3 = var1.v;

         for (int var4 = 0; var4 < 128; var4++) {
            var2[var4] ^= var3[var4];
         }
      }

      private void xorWith(Argon2BytesGenerator.Block var1, Argon2BytesGenerator.Block var2) {
         long[] var3 = this.v;
         long[] var4 = var1.v;
         long[] var5 = var2.v;

         for (int var6 = 0; var6 < 128; var6++) {
            var3[var6] ^= var4[var6] ^ var5[var6];
         }
      }

      public Argon2BytesGenerator.Block clear() {
         Arrays.fill(this.v, 0L);
         return this;
      }
   }

   private static class FillBlock {
      Argon2BytesGenerator.Block R = new Argon2BytesGenerator.Block();
      Argon2BytesGenerator.Block Z = new Argon2BytesGenerator.Block();
      Argon2BytesGenerator.Block addressBlock = new Argon2BytesGenerator.Block();
      Argon2BytesGenerator.Block inputBlock = new Argon2BytesGenerator.Block();

      private FillBlock() {
      }

      private void applyBlake() {
         for (int var1 = 0; var1 < 8; var1++) {
            int var2 = 16 * var1;
            Argon2BytesGenerator.roundFunction(
               this.Z,
               var2,
               var2 + 1,
               var2 + 2,
               var2 + 3,
               var2 + 4,
               var2 + 5,
               var2 + 6,
               var2 + 7,
               var2 + 8,
               var2 + 9,
               var2 + 10,
               var2 + 11,
               var2 + 12,
               var2 + 13,
               var2 + 14,
               var2 + 15
            );
         }

         for (int var3 = 0; var3 < 8; var3++) {
            int var4 = 2 * var3;
            Argon2BytesGenerator.roundFunction(
               this.Z,
               var4,
               var4 + 1,
               var4 + 16,
               var4 + 17,
               var4 + 32,
               var4 + 33,
               var4 + 48,
               var4 + 49,
               var4 + 64,
               var4 + 65,
               var4 + 80,
               var4 + 81,
               var4 + 96,
               var4 + 97,
               var4 + 112,
               var4 + 113
            );
         }
      }

      private void fillBlock(Argon2BytesGenerator.Block var1, Argon2BytesGenerator.Block var2) {
         this.Z.copyBlock(var1);
         this.applyBlake();
         var2.xor(var1, this.Z);
      }

      private void fillBlock(Argon2BytesGenerator.Block var1, Argon2BytesGenerator.Block var2, Argon2BytesGenerator.Block var3) {
         this.R.xor(var1, var2);
         this.Z.copyBlock(this.R);
         this.applyBlake();
         var3.xor(this.R, this.Z);
      }

      private void fillBlockWithXor(Argon2BytesGenerator.Block var1, Argon2BytesGenerator.Block var2, Argon2BytesGenerator.Block var3) {
         this.R.xor(var1, var2);
         this.Z.copyBlock(this.R);
         this.applyBlake();
         var3.xorWith(this.R, this.Z);
      }
   }

   private static class Position {
      int pass;
      int lane;
      int slice;

      Position() {
      }
   }
}
