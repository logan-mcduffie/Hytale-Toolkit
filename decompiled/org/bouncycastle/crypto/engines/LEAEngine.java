package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.Pack;

public class LEAEngine implements BlockCipher {
   private static final int BASEROUNDS = 16;
   private static final int NUMWORDS = 4;
   private static final int NUMWORDS128 = 4;
   private static final int MASK128 = 3;
   private static final int NUMWORDS192 = 6;
   private static final int NUMWORDS256 = 8;
   private static final int MASK256 = 7;
   private static final int BLOCKSIZE = 16;
   private static final int KEY0 = 0;
   private static final int KEY1 = 1;
   private static final int KEY2 = 2;
   private static final int KEY3 = 3;
   private static final int KEY4 = 4;
   private static final int KEY5 = 5;
   private static final int ROT1 = 1;
   private static final int ROT3 = 3;
   private static final int ROT5 = 5;
   private static final int ROT6 = 6;
   private static final int ROT9 = 9;
   private static final int ROT11 = 11;
   private static final int ROT13 = 13;
   private static final int ROT17 = 17;
   private static final int[] DELTA = new int[]{-1007687205, 1147300610, 2044886154, 2027892972, 1902027934, -947529206, -531697110, -440137385};
   private final int[] theBlock = new int[4];
   private int theRounds;
   private int[][] theRoundKeys;
   private boolean forEncryption;

   @Override
   public void init(boolean var1, CipherParameters var2) {
      if (!(var2 instanceof KeyParameter)) {
         throw new IllegalArgumentException("Invalid parameter passed to LEA init - " + var2.getClass().getName());
      } else {
         byte[] var3 = ((KeyParameter)var2).getKey();
         int var4 = var3.length;
         if ((var4 << 1) % 16 == 0 && var4 >= 16 && var4 <= 32) {
            this.forEncryption = var1;
            CryptoServicesRegistrar.checkConstraints(
               new DefaultServiceProperties(this.getAlgorithmName(), var4 * 8, var2, Utils.getPurpose(this.forEncryption))
            );
            this.generateRoundKeys(var3);
         } else {
            throw new IllegalArgumentException("KeyBitSize must be 128, 192 or 256");
         }
      }
   }

   @Override
   public void reset() {
   }

   @Override
   public String getAlgorithmName() {
      return "LEA";
   }

   @Override
   public int getBlockSize() {
      return 16;
   }

   @Override
   public int processBlock(byte[] var1, int var2, byte[] var3, int var4) {
      checkBuffer(var1, var2, false);
      checkBuffer(var3, var4, true);
      return this.forEncryption ? this.encryptBlock(var1, var2, var3, var4) : this.decryptBlock(var1, var2, var3, var4);
   }

   private static int bufLength(byte[] var0) {
      return var0 == null ? 0 : var0.length;
   }

   private static void checkBuffer(byte[] var0, int var1, boolean var2) {
      int var3 = bufLength(var0);
      int var4 = var1 + 16;
      boolean var5 = var1 < 0 || var4 < 0;
      if (var5 || var4 > var3) {
         throw var2 ? new OutputLengthException("Output buffer too short.") : new DataLengthException("Input buffer too short.");
      }
   }

   private int encryptBlock(byte[] var1, int var2, byte[] var3, int var4) {
      Pack.littleEndianToInt(var1, var2, this.theBlock, 0, 4);

      for (int var5 = 0; var5 < this.theRounds; var5++) {
         this.encryptRound(var5);
      }

      Pack.intToLittleEndian(this.theBlock, var3, var4);
      return 16;
   }

   private void encryptRound(int var1) {
      int[] var2 = this.theRoundKeys[var1];
      int var3 = (3 + var1) % 4;
      int var4 = leftIndex(var3);
      this.theBlock[var3] = ror32((this.theBlock[var4] ^ var2[4]) + (this.theBlock[var3] ^ var2[5]), 3);
      int var7 = leftIndex(var4);
      this.theBlock[var4] = ror32((this.theBlock[var7] ^ var2[2]) + (this.theBlock[var4] ^ var2[3]), 5);
      var4 = leftIndex(var7);
      this.theBlock[var7] = rol32((this.theBlock[var4] ^ var2[0]) + (this.theBlock[var7] ^ var2[1]), 9);
   }

   private static int leftIndex(int var0) {
      return var0 == 0 ? 3 : var0 - 1;
   }

   private int decryptBlock(byte[] var1, int var2, byte[] var3, int var4) {
      Pack.littleEndianToInt(var1, var2, this.theBlock, 0, 4);

      for (int var5 = this.theRounds - 1; var5 >= 0; var5--) {
         this.decryptRound(var5);
      }

      Pack.intToLittleEndian(this.theBlock, var3, var4);
      return 16;
   }

   private void decryptRound(int var1) {
      int[] var2 = this.theRoundKeys[var1];
      int var3 = var1 % 4;
      int var4 = rightIndex(var3);
      this.theBlock[var4] = ror32(this.theBlock[var4], 9) - (this.theBlock[var3] ^ var2[0]) ^ var2[1];
      int var7 = rightIndex(var4);
      this.theBlock[var7] = rol32(this.theBlock[var7], 5) - (this.theBlock[var4] ^ var2[2]) ^ var2[3];
      var4 = rightIndex(var7);
      this.theBlock[var4] = rol32(this.theBlock[var4], 3) - (this.theBlock[var7] ^ var2[4]) ^ var2[5];
   }

   private static int rightIndex(int var0) {
      return var0 == 3 ? 0 : var0 + 1;
   }

   private void generateRoundKeys(byte[] var1) {
      this.theRounds = (var1.length >> 1) + 16;
      this.theRoundKeys = new int[this.theRounds][6];
      int var2 = var1.length / 4;
      int[] var3 = new int[var2];
      Pack.littleEndianToInt(var1, 0, var3, 0, var2);
      switch (var2) {
         case 4:
            this.generate128RoundKeys(var3);
            break;
         case 5:
         case 7:
         case 8:
         default:
            this.generate256RoundKeys(var3);
            break;
         case 6:
            this.generate192RoundKeys(var3);
      }
   }

   private void generate128RoundKeys(int[] var1) {
      for (int var2 = 0; var2 < this.theRounds; var2++) {
         int var3 = rol32(DELTA[var2 & 3], var2);
         int var4 = 0;
         var1[var4] = rol32(var1[var4++] + var3, 1);
         var1[var4] = rol32(var1[var4] + rol32(var3, var4++), 3);
         var1[var4] = rol32(var1[var4] + rol32(var3, var4++), 6);
         var1[var4] = rol32(var1[var4] + rol32(var3, var4), 11);
         int[] var5 = this.theRoundKeys[var2];
         var5[0] = var1[0];
         var5[1] = var1[1];
         var5[2] = var1[2];
         var5[3] = var1[1];
         var5[4] = var1[3];
         var5[5] = var1[1];
      }
   }

   private void generate192RoundKeys(int[] var1) {
      for (int var2 = 0; var2 < this.theRounds; var2++) {
         int var3 = rol32(DELTA[var2 % 6], var2);
         int var4 = 0;
         var1[var4] = rol32(var1[var4] + rol32(var3, var4++), 1);
         var1[var4] = rol32(var1[var4] + rol32(var3, var4++), 3);
         var1[var4] = rol32(var1[var4] + rol32(var3, var4++), 6);
         var1[var4] = rol32(var1[var4] + rol32(var3, var4++), 11);
         var1[var4] = rol32(var1[var4] + rol32(var3, var4++), 13);
         var1[var4] = rol32(var1[var4] + rol32(var3, var4++), 17);
         System.arraycopy(var1, 0, this.theRoundKeys[var2], 0, var4);
      }
   }

   private void generate256RoundKeys(int[] var1) {
      int var2 = 0;

      for (int var3 = 0; var3 < this.theRounds; var3++) {
         int var4 = rol32(DELTA[var3 & 7], var3);
         int[] var5 = this.theRoundKeys[var3];
         int var6 = 0;
         var5[var6] = rol32(var1[var2 & 7] + var4, 1);
         var1[var2++ & 7] = var5[var6++];
         var5[var6] = rol32(var1[var2 & 7] + rol32(var4, var6), 3);
         var1[var2++ & 7] = var5[var6++];
         var5[var6] = rol32(var1[var2 & 7] + rol32(var4, var6), 6);
         var1[var2++ & 7] = var5[var6++];
         var5[var6] = rol32(var1[var2 & 7] + rol32(var4, var6), 11);
         var1[var2++ & 7] = var5[var6++];
         var5[var6] = rol32(var1[var2 & 7] + rol32(var4, var6), 13);
         var1[var2++ & 7] = var5[var6++];
         var5[var6] = rol32(var1[var2 & 7] + rol32(var4, var6), 17);
         var1[var2++ & 7] = var5[var6];
      }
   }

   private static int rol32(int var0, int var1) {
      return var0 << var1 | var0 >>> 32 - var1;
   }

   private static int ror32(int var0, int var1) {
      return var0 >>> var1 | var0 << 32 - var1;
   }
}
