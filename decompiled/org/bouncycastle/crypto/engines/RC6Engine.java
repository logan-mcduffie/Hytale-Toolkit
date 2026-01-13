package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.params.KeyParameter;

public class RC6Engine implements BlockCipher {
   private static final int wordSize = 32;
   private static final int bytesPerWord = 4;
   private static final int _noRounds = 20;
   private int[] _S = null;
   private static final int P32 = -1209970333;
   private static final int Q32 = -1640531527;
   private static final int LGW = 5;
   private boolean forEncryption;

   @Override
   public String getAlgorithmName() {
      return "RC6";
   }

   @Override
   public int getBlockSize() {
      return 16;
   }

   @Override
   public void init(boolean var1, CipherParameters var2) {
      if (!(var2 instanceof KeyParameter)) {
         throw new IllegalArgumentException("invalid parameter passed to RC6 init - " + var2.getClass().getName());
      } else {
         KeyParameter var3 = (KeyParameter)var2;
         this.forEncryption = var1;
         byte[] var4 = var3.getKey();
         this.setKey(var4);
         CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties(this.getAlgorithmName(), var4.length * 8, var2, Utils.getPurpose(var1)));
      }
   }

   @Override
   public int processBlock(byte[] var1, int var2, byte[] var3, int var4) {
      int var5 = this.getBlockSize();
      if (this._S == null) {
         throw new IllegalStateException("RC6 engine not initialised");
      } else if (var2 + var5 > var1.length) {
         throw new DataLengthException("input buffer too short");
      } else if (var4 + var5 > var3.length) {
         throw new OutputLengthException("output buffer too short");
      } else {
         return this.forEncryption ? this.encryptBlock(var1, var2, var3, var4) : this.decryptBlock(var1, var2, var3, var4);
      }
   }

   @Override
   public void reset() {
   }

   private void setKey(byte[] var1) {
      int var2 = (var1.length + 3) / 4;
      if (var2 == 0) {
         boolean var10 = true;
      }

      int[] var3 = new int[(var1.length + 4 - 1) / 4];

      for (int var4 = var1.length - 1; var4 >= 0; var4--) {
         var3[var4 / 4] = (var3[var4 / 4] << 8) + (var1[var4] & 255);
      }

      this._S = new int[44];
      this._S[0] = -1209970333;

      for (int var11 = 1; var11 < this._S.length; var11++) {
         this._S[var11] = this._S[var11 - 1] + -1640531527;
      }

      int var12;
      if (var3.length > this._S.length) {
         var12 = 3 * var3.length;
      } else {
         var12 = 3 * this._S.length;
      }

      int var5 = 0;
      int var6 = 0;
      int var7 = 0;
      int var8 = 0;

      for (int var9 = 0; var9 < var12; var9++) {
         var5 = this._S[var7] = this.rotateLeft(this._S[var7] + var5 + var6, 3);
         var6 = var3[var8] = this.rotateLeft(var3[var8] + var5 + var6, var5 + var6);
         var7 = (var7 + 1) % this._S.length;
         var8 = (var8 + 1) % var3.length;
      }
   }

   private int encryptBlock(byte[] var1, int var2, byte[] var3, int var4) {
      int var5 = this.bytesToWord(var1, var2);
      int var6 = this.bytesToWord(var1, var2 + 4);
      int var7 = this.bytesToWord(var1, var2 + 8);
      int var8 = this.bytesToWord(var1, var2 + 12);
      var6 += this._S[0];
      var8 += this._S[1];

      for (int var9 = 1; var9 <= 20; var9++) {
         int var10 = 0;
         int var11 = 0;
         var10 = var6 * (2 * var6 + 1);
         var10 = this.rotateLeft(var10, 5);
         var11 = var8 * (2 * var8 + 1);
         var11 = this.rotateLeft(var11, 5);
         var5 ^= var10;
         var5 = this.rotateLeft(var5, var11);
         var5 += this._S[2 * var9];
         var7 ^= var11;
         var7 = this.rotateLeft(var7, var10);
         var7 += this._S[2 * var9 + 1];
         var5 = var6;
         var6 = var7;
         var7 = var8;
         var8 = var5;
      }

      var5 += this._S[42];
      var7 += this._S[43];
      this.wordToBytes(var5, var3, var4);
      this.wordToBytes(var6, var3, var4 + 4);
      this.wordToBytes(var7, var3, var4 + 8);
      this.wordToBytes(var8, var3, var4 + 12);
      return 16;
   }

   private int decryptBlock(byte[] var1, int var2, byte[] var3, int var4) {
      int var5 = this.bytesToWord(var1, var2);
      int var6 = this.bytesToWord(var1, var2 + 4);
      int var7 = this.bytesToWord(var1, var2 + 8);
      int var8 = this.bytesToWord(var1, var2 + 12);
      var7 -= this._S[43];
      var5 -= this._S[42];

      for (int var9 = 20; var9 >= 1; var9--) {
         int var10 = 0;
         int var11 = 0;
         int var12 = var8;
         var8 = var7;
         int var18 = var6;
         var6 = var5;
         var10 = var5 * (2 * var5 + 1);
         var10 = this.rotateLeft(var10, 5);
         var11 = var7 * (2 * var7 + 1);
         var11 = this.rotateLeft(var11, 5);
         var7 = var18 - this._S[2 * var9 + 1];
         var7 = this.rotateRight(var7, var10);
         var7 ^= var11;
         var5 = var12 - this._S[2 * var9];
         var5 = this.rotateRight(var5, var11);
         var5 ^= var10;
      }

      var8 -= this._S[1];
      var6 -= this._S[0];
      this.wordToBytes(var5, var3, var4);
      this.wordToBytes(var6, var3, var4 + 4);
      this.wordToBytes(var7, var3, var4 + 8);
      this.wordToBytes(var8, var3, var4 + 12);
      return 16;
   }

   private int rotateLeft(int var1, int var2) {
      return var1 << var2 | var1 >>> -var2;
   }

   private int rotateRight(int var1, int var2) {
      return var1 >>> var2 | var1 << -var2;
   }

   private int bytesToWord(byte[] var1, int var2) {
      int var3 = 0;

      for (int var4 = 3; var4 >= 0; var4--) {
         var3 = (var3 << 8) + (var1[var4 + var2] & 255);
      }

      return var3;
   }

   private void wordToBytes(int var1, byte[] var2, int var3) {
      for (int var4 = 0; var4 < 4; var4++) {
         var2[var4 + var3] = (byte)var1;
         var1 >>>= 8;
      }
   }
}
