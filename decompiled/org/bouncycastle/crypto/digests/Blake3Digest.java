package org.bouncycastle.crypto.digests;

import java.util.Iterator;
import java.util.Stack;
import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.Xof;
import org.bouncycastle.crypto.params.Blake3Parameters;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Integers;
import org.bouncycastle.util.Memoable;
import org.bouncycastle.util.Pack;

public class Blake3Digest implements ExtendedDigest, Memoable, Xof {
   private static final String ERR_OUTPUTTING = "Already outputting";
   private static final int NUMWORDS = 8;
   private static final int ROUNDS = 7;
   private static final int BLOCKLEN = 64;
   private static final int CHUNKLEN = 1024;
   private static final int CHUNKSTART = 1;
   private static final int CHUNKEND = 2;
   private static final int PARENT = 4;
   private static final int ROOT = 8;
   private static final int KEYEDHASH = 16;
   private static final int DERIVECONTEXT = 32;
   private static final int DERIVEKEY = 64;
   private static final int CHAINING0 = 0;
   private static final int CHAINING1 = 1;
   private static final int CHAINING2 = 2;
   private static final int CHAINING3 = 3;
   private static final int CHAINING4 = 4;
   private static final int CHAINING5 = 5;
   private static final int CHAINING6 = 6;
   private static final int CHAINING7 = 7;
   private static final int IV0 = 8;
   private static final int IV1 = 9;
   private static final int IV2 = 10;
   private static final int IV3 = 11;
   private static final int COUNT0 = 12;
   private static final int COUNT1 = 13;
   private static final int DATALEN = 14;
   private static final int FLAGS = 15;
   private static final byte[] SIGMA = new byte[]{2, 6, 3, 10, 7, 0, 4, 13, 1, 11, 12, 5, 9, 14, 15, 8};
   private static final int[] IV = new int[]{1779033703, -1150833019, 1013904242, -1521486534, 1359893119, -1694144372, 528734635, 1541459225};
   private final byte[] theBuffer = new byte[64];
   private final int[] theK = new int[8];
   private final int[] theChaining = new int[8];
   private final int[] theV = new int[16];
   private final int[] theM = new int[16];
   private final byte[] theIndices = new byte[16];
   private final Stack theStack = new Stack();
   private final int theDigestLen;
   private boolean outputting;
   private long outputAvailable;
   private int theMode;
   private int theOutputMode;
   private int theOutputDataLen;
   private long theCounter;
   private int theCurrBytes;
   private int thePos;
   private final CryptoServicePurpose purpose;

   public Blake3Digest() {
      this(256);
   }

   public Blake3Digest(int var1) {
      this(var1 > 100 ? var1 : var1 * 8, CryptoServicePurpose.ANY);
   }

   public Blake3Digest(int var1, CryptoServicePurpose var2) {
      this.purpose = var2;
      this.theDigestLen = var1 / 8;
      CryptoServicesRegistrar.checkConstraints(Utils.getDefaultProperties(this, this.getDigestSize() * 8, var2));
      this.init(null);
   }

   public Blake3Digest(Blake3Digest var1) {
      this.theDigestLen = var1.theDigestLen;
      this.purpose = var1.purpose;
      this.reset(var1);
   }

   @Override
   public int getByteLength() {
      return 64;
   }

   @Override
   public String getAlgorithmName() {
      return "BLAKE3";
   }

   @Override
   public int getDigestSize() {
      return this.theDigestLen;
   }

   public void init(Blake3Parameters var1) {
      byte[] var2 = var1 == null ? null : var1.getKey();
      byte[] var3 = var1 == null ? null : var1.getContext();
      this.reset();
      if (var2 != null) {
         this.initKey(var2);
         Arrays.fill(var2, (byte)0);
      } else if (var3 != null) {
         this.initNullKey();
         this.theMode = 32;
         this.update(var3, 0, var3.length);
         this.doFinal(this.theBuffer, 0);
         this.initKeyFromContext();
         this.reset();
      } else {
         this.initNullKey();
         this.theMode = 0;
      }
   }

   @Override
   public void update(byte var1) {
      if (this.outputting) {
         throw new IllegalStateException("Already outputting");
      } else {
         int var2 = this.theBuffer.length;
         int var3 = var2 - this.thePos;
         if (var3 == 0) {
            this.compressBlock(this.theBuffer, 0);
            Arrays.fill(this.theBuffer, (byte)0);
            this.thePos = 0;
         }

         this.theBuffer[this.thePos] = var1;
         this.thePos++;
      }
   }

   @Override
   public void update(byte[] var1, int var2, int var3) {
      if (var1 != null && var3 != 0) {
         if (this.outputting) {
            throw new IllegalStateException("Already outputting");
         } else {
            int var4 = 0;
            if (this.thePos != 0) {
               var4 = 64 - this.thePos;
               if (var4 >= var3) {
                  System.arraycopy(var1, var2, this.theBuffer, this.thePos, var3);
                  this.thePos += var3;
                  return;
               }

               System.arraycopy(var1, var2, this.theBuffer, this.thePos, var4);
               this.compressBlock(this.theBuffer, 0);
               this.thePos = 0;
               Arrays.fill(this.theBuffer, (byte)0);
            }

            int var6 = var2 + var3 - 64;

            int var5;
            for (var5 = var2 + var4; var5 < var6; var5 += 64) {
               this.compressBlock(var1, var5);
            }

            int var7 = var3 - var5;
            System.arraycopy(var1, var5, this.theBuffer, 0, var2 + var7);
            this.thePos += var2 + var7;
         }
      }
   }

   @Override
   public int doFinal(byte[] var1, int var2) {
      return this.doFinal(var1, var2, this.getDigestSize());
   }

   @Override
   public int doFinal(byte[] var1, int var2, int var3) {
      int var4 = this.doOutput(var1, var2, var3);
      this.reset();
      return var4;
   }

   @Override
   public int doOutput(byte[] var1, int var2, int var3) {
      if (var2 > var1.length - var3) {
         throw new OutputLengthException("output buffer too short");
      } else {
         if (!this.outputting) {
            this.compressFinalBlock(this.thePos);
         }

         if (var3 >= 0 && (this.outputAvailable < 0L || var3 <= this.outputAvailable)) {
            int var4 = var3;
            int var5 = var2;
            if (this.thePos < 64) {
               int var6 = Math.min(var3, 64 - this.thePos);
               System.arraycopy(this.theBuffer, this.thePos, var1, var2, var6);
               this.thePos += var6;
               var5 = var2 + var6;
               var4 = var3 - var6;
            }

            while (var4 > 0) {
               this.nextOutputBlock();
               int var7 = Math.min(var4, 64);
               System.arraycopy(this.theBuffer, 0, var1, var5, var7);
               this.thePos += var7;
               var5 += var7;
               var4 -= var7;
            }

            this.outputAvailable -= var3;
            return var3;
         } else {
            throw new IllegalArgumentException("Insufficient bytes remaining");
         }
      }
   }

   @Override
   public void reset() {
      this.resetBlockCount();
      this.thePos = 0;
      this.outputting = false;
      Arrays.fill(this.theBuffer, (byte)0);
   }

   @Override
   public void reset(Memoable var1) {
      Blake3Digest var2 = (Blake3Digest)var1;
      this.theCounter = var2.theCounter;
      this.theCurrBytes = var2.theCurrBytes;
      this.theMode = var2.theMode;
      this.outputting = var2.outputting;
      this.outputAvailable = var2.outputAvailable;
      this.theOutputMode = var2.theOutputMode;
      this.theOutputDataLen = var2.theOutputDataLen;
      System.arraycopy(var2.theChaining, 0, this.theChaining, 0, this.theChaining.length);
      System.arraycopy(var2.theK, 0, this.theK, 0, this.theK.length);
      System.arraycopy(var2.theM, 0, this.theM, 0, this.theM.length);
      this.theStack.clear();
      Iterator var3 = var2.theStack.iterator();

      while (var3.hasNext()) {
         this.theStack.push(Arrays.clone((int[])var3.next()));
      }

      System.arraycopy(var2.theBuffer, 0, this.theBuffer, 0, this.theBuffer.length);
      this.thePos = var2.thePos;
   }

   @Override
   public Memoable copy() {
      return new Blake3Digest(this);
   }

   private void compressBlock(byte[] var1, int var2) {
      this.initChunkBlock(64, false);
      this.initM(var1, var2);
      this.compress();
      if (this.theCurrBytes == 0) {
         this.adjustStack();
      }
   }

   private void adjustStack() {
      for (long var1 = this.theCounter; var1 > 0L && (var1 & 1L) != 1L; var1 >>= 1) {
         int[] var3 = (int[])this.theStack.pop();
         System.arraycopy(var3, 0, this.theM, 0, 8);
         System.arraycopy(this.theChaining, 0, this.theM, 8, 8);
         this.initParentBlock();
         this.compress();
      }

      this.theStack.push(Arrays.copyOf(this.theChaining, 8));
   }

   private void compressFinalBlock(int var1) {
      this.initChunkBlock(var1, true);
      this.initM(this.theBuffer, 0);
      this.compress();
      this.processStack();
   }

   private void processStack() {
      while (!this.theStack.isEmpty()) {
         int[] var1 = (int[])this.theStack.pop();
         System.arraycopy(var1, 0, this.theM, 0, 8);
         System.arraycopy(this.theChaining, 0, this.theM, 8, 8);
         this.initParentBlock();
         if (this.theStack.isEmpty()) {
            this.setRoot();
         }

         this.compress();
      }
   }

   private void compress() {
      this.initIndices();

      for (int var1 = 0; var1 < 6; var1++) {
         this.performRound();
         this.permuteIndices();
      }

      this.performRound();
      this.adjustChaining();
   }

   private void performRound() {
      this.mixG(0, 0, 4, 8, 12);
      this.mixG(1, 1, 5, 9, 13);
      this.mixG(2, 2, 6, 10, 14);
      this.mixG(3, 3, 7, 11, 15);
      this.mixG(4, 0, 5, 10, 15);
      this.mixG(5, 1, 6, 11, 12);
      this.mixG(6, 2, 7, 8, 13);
      this.mixG(7, 3, 4, 9, 14);
   }

   private void initM(byte[] var1, int var2) {
      Pack.littleEndianToInt(var1, var2, this.theM);
   }

   private void adjustChaining() {
      if (this.outputting) {
         for (int var1 = 0; var1 < 8; var1++) {
            this.theV[var1] = this.theV[var1] ^ this.theV[var1 + 8];
            this.theV[var1 + 8] = this.theV[var1 + 8] ^ this.theChaining[var1];
         }

         Pack.intToLittleEndian(this.theV, this.theBuffer, 0);
         this.thePos = 0;
      } else {
         for (int var2 = 0; var2 < 8; var2++) {
            this.theChaining[var2] = this.theV[var2] ^ this.theV[var2 + 8];
         }
      }
   }

   private void mixG(int var1, int var2, int var3, int var4, int var5) {
      int var6 = var1 << 1;
      this.theV[var2] = this.theV[var2] + this.theV[var3] + this.theM[this.theIndices[var6++]];
      this.theV[var5] = Integers.rotateRight(this.theV[var5] ^ this.theV[var2], 16);
      this.theV[var4] = this.theV[var4] + this.theV[var5];
      this.theV[var3] = Integers.rotateRight(this.theV[var3] ^ this.theV[var4], 12);
      this.theV[var2] = this.theV[var2] + this.theV[var3] + this.theM[this.theIndices[var6]];
      this.theV[var5] = Integers.rotateRight(this.theV[var5] ^ this.theV[var2], 8);
      this.theV[var4] = this.theV[var4] + this.theV[var5];
      this.theV[var3] = Integers.rotateRight(this.theV[var3] ^ this.theV[var4], 7);
   }

   private void initIndices() {
      byte var1 = 0;

      while (var1 < this.theIndices.length) {
         this.theIndices[var1] = var1++;
      }
   }

   private void permuteIndices() {
      for (byte var1 = 0; var1 < this.theIndices.length; var1++) {
         this.theIndices[var1] = SIGMA[this.theIndices[var1]];
      }
   }

   private void initNullKey() {
      System.arraycopy(IV, 0, this.theK, 0, 8);
   }

   private void initKey(byte[] var1) {
      Pack.littleEndianToInt(var1, 0, this.theK);
      this.theMode = 16;
   }

   private void initKeyFromContext() {
      System.arraycopy(this.theV, 0, this.theK, 0, 8);
      this.theMode = 64;
   }

   private void initChunkBlock(int var1, boolean var2) {
      System.arraycopy(this.theCurrBytes == 0 ? this.theK : this.theChaining, 0, this.theV, 0, 8);
      System.arraycopy(IV, 0, this.theV, 8, 4);
      this.theV[12] = (int)this.theCounter;
      this.theV[13] = (int)(this.theCounter >> 32);
      this.theV[14] = var1;
      this.theV[15] = this.theMode + (this.theCurrBytes == 0 ? 1 : 0) + (var2 ? 2 : 0);
      this.theCurrBytes += var1;
      if (this.theCurrBytes >= 1024) {
         this.incrementBlockCount();
         this.theV[15] = this.theV[15] | 2;
      }

      if (var2 && this.theStack.isEmpty()) {
         this.setRoot();
      }
   }

   private void initParentBlock() {
      System.arraycopy(this.theK, 0, this.theV, 0, 8);
      System.arraycopy(IV, 0, this.theV, 8, 4);
      this.theV[12] = 0;
      this.theV[13] = 0;
      this.theV[14] = 64;
      this.theV[15] = this.theMode | 4;
   }

   private void nextOutputBlock() {
      this.theCounter++;
      System.arraycopy(this.theChaining, 0, this.theV, 0, 8);
      System.arraycopy(IV, 0, this.theV, 8, 4);
      this.theV[12] = (int)this.theCounter;
      this.theV[13] = (int)(this.theCounter >> 32);
      this.theV[14] = this.theOutputDataLen;
      this.theV[15] = this.theOutputMode;
      this.compress();
   }

   private void incrementBlockCount() {
      this.theCounter++;
      this.theCurrBytes = 0;
   }

   private void resetBlockCount() {
      this.theCounter = 0L;
      this.theCurrBytes = 0;
   }

   private void setRoot() {
      this.theV[15] = this.theV[15] | 8;
      this.theOutputMode = this.theV[15];
      this.theOutputDataLen = this.theV[14];
      this.theCounter = 0L;
      this.outputting = true;
      this.outputAvailable = -1L;
      System.arraycopy(this.theV, 0, this.theChaining, 0, 8);
   }
}
