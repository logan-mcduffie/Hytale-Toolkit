package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.params.RC5Parameters;

public class RC564Engine implements BlockCipher {
   private static final int wordSize = 64;
   private static final int bytesPerWord = 8;
   private int _noRounds = 12;
   private long[] _S = null;
   private static final long P64 = -5196783011329398165L;
   private static final long Q64 = -7046029254386353131L;
   private boolean forEncryption;

   @Override
   public String getAlgorithmName() {
      return "RC5-64";
   }

   @Override
   public int getBlockSize() {
      return 16;
   }

   @Override
   public void init(boolean var1, CipherParameters var2) {
      if (!(var2 instanceof RC5Parameters)) {
         throw new IllegalArgumentException("invalid parameter passed to RC564 init - " + var2.getClass().getName());
      } else {
         RC5Parameters var3 = (RC5Parameters)var2;
         this.forEncryption = var1;
         this._noRounds = var3.getRounds();
         byte[] var4 = var3.getKey();
         this.setKey(var4);
         CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties(this.getAlgorithmName(), var4.length * 8, var2, Utils.getPurpose(var1)));
      }
   }

   @Override
   public int processBlock(byte[] var1, int var2, byte[] var3, int var4) {
      return this.forEncryption ? this.encryptBlock(var1, var2, var3, var4) : this.decryptBlock(var1, var2, var3, var4);
   }

   @Override
   public void reset() {
   }

   private void setKey(byte[] var1) {
      long[] var2 = new long[(var1.length + 7) / 8];

      for (int var3 = 0; var3 != var1.length; var3++) {
         var2[var3 / 8] = var2[var3 / 8] + ((long)(var1[var3] & 255) << 8 * (var3 % 8));
      }

      this._S = new long[2 * (this._noRounds + 1)];
      this._S[0] = -5196783011329398165L;

      for (int var11 = 1; var11 < this._S.length; var11++) {
         this._S[var11] = this._S[var11 - 1] + -7046029254386353131L;
      }

      int var12;
      if (var2.length > this._S.length) {
         var12 = 3 * var2.length;
      } else {
         var12 = 3 * this._S.length;
      }

      long var4 = 0L;
      long var6 = 0L;
      int var8 = 0;
      int var9 = 0;

      for (int var10 = 0; var10 < var12; var10++) {
         var4 = this._S[var8] = this.rotateLeft(this._S[var8] + var4 + var6, 3L);
         var6 = var2[var9] = this.rotateLeft(var2[var9] + var4 + var6, var4 + var6);
         var8 = (var8 + 1) % this._S.length;
         var9 = (var9 + 1) % var2.length;
      }
   }

   private int encryptBlock(byte[] var1, int var2, byte[] var3, int var4) {
      long var5 = this.bytesToWord(var1, var2) + this._S[0];
      long var7 = this.bytesToWord(var1, var2 + 8) + this._S[1];

      for (int var9 = 1; var9 <= this._noRounds; var9++) {
         var5 = this.rotateLeft(var5 ^ var7, var7) + this._S[2 * var9];
         var7 = this.rotateLeft(var7 ^ var5, var5) + this._S[2 * var9 + 1];
      }

      this.wordToBytes(var5, var3, var4);
      this.wordToBytes(var7, var3, var4 + 8);
      return 16;
   }

   private int decryptBlock(byte[] var1, int var2, byte[] var3, int var4) {
      long var5 = this.bytesToWord(var1, var2);
      long var7 = this.bytesToWord(var1, var2 + 8);

      for (int var9 = this._noRounds; var9 >= 1; var9--) {
         var7 = this.rotateRight(var7 - this._S[2 * var9 + 1], var5) ^ var5;
         var5 = this.rotateRight(var5 - this._S[2 * var9], var7) ^ var7;
      }

      this.wordToBytes(var5 - this._S[0], var3, var4);
      this.wordToBytes(var7 - this._S[1], var3, var4 + 8);
      return 16;
   }

   private long rotateLeft(long var1, long var3) {
      return var1 << (int)(var3 & 63L) | var1 >>> (int)(64L - (var3 & 63L));
   }

   private long rotateRight(long var1, long var3) {
      return var1 >>> (int)(var3 & 63L) | var1 << (int)(64L - (var3 & 63L));
   }

   private long bytesToWord(byte[] var1, int var2) {
      long var3 = 0L;

      for (int var5 = 7; var5 >= 0; var5--) {
         var3 = (var3 << 8) + (var1[var5 + var2] & 255);
      }

      return var3;
   }

   private void wordToBytes(long var1, byte[] var3, int var4) {
      for (int var5 = 0; var5 < 8; var5++) {
         var3[var5 + var4] = (byte)var1;
         var1 >>>= 8;
      }
   }
}
