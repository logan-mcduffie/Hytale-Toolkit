package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.RC5Parameters;

public class RC532Engine implements BlockCipher {
   private int _noRounds = 12;
   private int[] _S = null;
   private static final int P32 = -1209970333;
   private static final int Q32 = -1640531527;
   private boolean forEncryption;

   @Override
   public String getAlgorithmName() {
      return "RC5-32";
   }

   @Override
   public int getBlockSize() {
      return 8;
   }

   @Override
   public void init(boolean var1, CipherParameters var2) {
      byte[] var3;
      if (var2 instanceof RC5Parameters) {
         RC5Parameters var4 = (RC5Parameters)var2;
         this._noRounds = var4.getRounds();
         var3 = var4.getKey();
         this.setKey(var3);
      } else {
         if (!(var2 instanceof KeyParameter)) {
            throw new IllegalArgumentException("invalid parameter passed to RC532 init - " + var2.getClass().getName());
         }

         KeyParameter var5 = (KeyParameter)var2;
         var3 = var5.getKey();
         this.setKey(var3);
      }

      this.forEncryption = var1;
      CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties(this.getAlgorithmName(), var3.length * 8, var2, Utils.getPurpose(var1)));
   }

   @Override
   public int processBlock(byte[] var1, int var2, byte[] var3, int var4) {
      return this.forEncryption ? this.encryptBlock(var1, var2, var3, var4) : this.decryptBlock(var1, var2, var3, var4);
   }

   @Override
   public void reset() {
   }

   private void setKey(byte[] var1) {
      int[] var2 = new int[(var1.length + 3) / 4];

      for (int var3 = 0; var3 != var1.length; var3++) {
         var2[var3 / 4] = var2[var3 / 4] + ((var1[var3] & 255) << 8 * (var3 % 4));
      }

      this._S = new int[2 * (this._noRounds + 1)];
      this._S[0] = -1209970333;

      for (int var9 = 1; var9 < this._S.length; var9++) {
         this._S[var9] = this._S[var9 - 1] + -1640531527;
      }

      int var10;
      if (var2.length > this._S.length) {
         var10 = 3 * var2.length;
      } else {
         var10 = 3 * this._S.length;
      }

      int var4 = 0;
      int var5 = 0;
      int var6 = 0;
      int var7 = 0;

      for (int var8 = 0; var8 < var10; var8++) {
         var4 = this._S[var6] = this.rotateLeft(this._S[var6] + var4 + var5, 3);
         var5 = var2[var7] = this.rotateLeft(var2[var7] + var4 + var5, var4 + var5);
         var6 = (var6 + 1) % this._S.length;
         var7 = (var7 + 1) % var2.length;
      }
   }

   private int encryptBlock(byte[] var1, int var2, byte[] var3, int var4) {
      int var5 = this.bytesToWord(var1, var2) + this._S[0];
      int var6 = this.bytesToWord(var1, var2 + 4) + this._S[1];

      for (int var7 = 1; var7 <= this._noRounds; var7++) {
         var5 = this.rotateLeft(var5 ^ var6, var6) + this._S[2 * var7];
         var6 = this.rotateLeft(var6 ^ var5, var5) + this._S[2 * var7 + 1];
      }

      this.wordToBytes(var5, var3, var4);
      this.wordToBytes(var6, var3, var4 + 4);
      return 8;
   }

   private int decryptBlock(byte[] var1, int var2, byte[] var3, int var4) {
      int var5 = this.bytesToWord(var1, var2);
      int var6 = this.bytesToWord(var1, var2 + 4);

      for (int var7 = this._noRounds; var7 >= 1; var7--) {
         var6 = this.rotateRight(var6 - this._S[2 * var7 + 1], var5) ^ var5;
         var5 = this.rotateRight(var5 - this._S[2 * var7], var6) ^ var6;
      }

      this.wordToBytes(var5 - this._S[0], var3, var4);
      this.wordToBytes(var6 - this._S[1], var3, var4 + 4);
      return 8;
   }

   private int rotateLeft(int var1, int var2) {
      return var1 << (var2 & 31) | var1 >>> 32 - (var2 & 31);
   }

   private int rotateRight(int var1, int var2) {
      return var1 >>> (var2 & 31) | var1 << 32 - (var2 & 31);
   }

   private int bytesToWord(byte[] var1, int var2) {
      return var1[var2] & 0xFF | (var1[var2 + 1] & 0xFF) << 8 | (var1[var2 + 2] & 0xFF) << 16 | (var1[var2 + 3] & 0xFF) << 24;
   }

   private void wordToBytes(int var1, byte[] var2, int var3) {
      var2[var3] = (byte)var1;
      var2[var3 + 1] = (byte)(var1 >> 8);
      var2[var3 + 2] = (byte)(var1 >> 16);
      var2[var3 + 3] = (byte)(var1 >> 24);
   }
}
