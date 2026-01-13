package org.bouncycastle.crypto.modes;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.StreamBlockCipher;
import org.bouncycastle.crypto.params.ParametersWithIV;

public class GOFBBlockCipher extends StreamBlockCipher {
   private byte[] IV;
   private byte[] ofbV;
   private byte[] ofbOutV;
   private int byteCount;
   private final int blockSize;
   private final BlockCipher cipher;
   boolean firstStep = true;
   int N3;
   int N4;
   static final int C1 = 16843012;
   static final int C2 = 16843009;

   public GOFBBlockCipher(BlockCipher var1) {
      super(var1);
      this.cipher = var1;
      this.blockSize = var1.getBlockSize();
      if (this.blockSize != 8) {
         throw new IllegalArgumentException("GCTR only for 64 bit block ciphers");
      } else {
         this.IV = new byte[var1.getBlockSize()];
         this.ofbV = new byte[var1.getBlockSize()];
         this.ofbOutV = new byte[var1.getBlockSize()];
      }
   }

   @Override
   public void init(boolean var1, CipherParameters var2) throws IllegalArgumentException {
      this.firstStep = true;
      this.N3 = 0;
      this.N4 = 0;
      if (var2 instanceof ParametersWithIV) {
         ParametersWithIV var3 = (ParametersWithIV)var2;
         byte[] var4 = var3.getIV();
         if (var4.length < this.IV.length) {
            System.arraycopy(var4, 0, this.IV, this.IV.length - var4.length, var4.length);

            for (int var5 = 0; var5 < this.IV.length - var4.length; var5++) {
               this.IV[var5] = 0;
            }
         } else {
            System.arraycopy(var4, 0, this.IV, 0, this.IV.length);
         }

         this.reset();
         if (var3.getParameters() != null) {
            this.cipher.init(true, var3.getParameters());
         }
      } else {
         this.reset();
         if (var2 != null) {
            this.cipher.init(true, var2);
         }
      }
   }

   @Override
   public String getAlgorithmName() {
      return this.cipher.getAlgorithmName() + "/GCTR";
   }

   @Override
   public int getBlockSize() {
      return this.blockSize;
   }

   @Override
   public int processBlock(byte[] var1, int var2, byte[] var3, int var4) throws DataLengthException, IllegalStateException {
      this.processBytes(var1, var2, this.blockSize, var3, var4);
      return this.blockSize;
   }

   @Override
   public void reset() {
      this.firstStep = true;
      this.N3 = 0;
      this.N4 = 0;
      System.arraycopy(this.IV, 0, this.ofbV, 0, this.IV.length);
      this.byteCount = 0;
      this.cipher.reset();
   }

   private int bytesToint(byte[] var1, int var2) {
      return (var1[var2 + 3] << 24 & 0xFF000000) + (var1[var2 + 2] << 16 & 0xFF0000) + (var1[var2 + 1] << 8 & 0xFF00) + (var1[var2] & 0xFF);
   }

   private void intTobytes(int var1, byte[] var2, int var3) {
      var2[var3 + 3] = (byte)(var1 >>> 24);
      var2[var3 + 2] = (byte)(var1 >>> 16);
      var2[var3 + 1] = (byte)(var1 >>> 8);
      var2[var3] = (byte)var1;
   }

   @Override
   protected byte calculateByte(byte var1) {
      if (this.byteCount == 0) {
         if (this.firstStep) {
            this.firstStep = false;
            this.cipher.processBlock(this.ofbV, 0, this.ofbOutV, 0);
            this.N3 = this.bytesToint(this.ofbOutV, 0);
            this.N4 = this.bytesToint(this.ofbOutV, 4);
         }

         this.N3 += 16843009;
         this.N4 += 16843012;
         if (this.N4 < 16843012 && this.N4 > 0) {
            this.N4++;
         }

         this.intTobytes(this.N3, this.ofbV, 0);
         this.intTobytes(this.N4, this.ofbV, 4);
         this.cipher.processBlock(this.ofbV, 0, this.ofbOutV, 0);
      }

      byte var2 = (byte)(this.ofbOutV[this.byteCount++] ^ var1);
      if (this.byteCount == this.blockSize) {
         this.byteCount = 0;
         System.arraycopy(this.ofbV, this.blockSize, this.ofbV, 0, this.ofbV.length - this.blockSize);
         System.arraycopy(this.ofbOutV, 0, this.ofbV, this.ofbV.length - this.blockSize, this.blockSize);
      }

      return var2;
   }
}
