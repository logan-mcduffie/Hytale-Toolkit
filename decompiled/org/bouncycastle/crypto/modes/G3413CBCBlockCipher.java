package org.bouncycastle.crypto.modes;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.Arrays;

public class G3413CBCBlockCipher implements BlockCipher {
   private int m;
   private int blockSize;
   private byte[] R;
   private byte[] R_init;
   private BlockCipher cipher;
   private boolean initialized = false;
   private boolean forEncryption;

   public G3413CBCBlockCipher(BlockCipher var1) {
      this.blockSize = var1.getBlockSize();
      this.cipher = var1;
   }

   @Override
   public void init(boolean var1, CipherParameters var2) throws IllegalArgumentException {
      this.forEncryption = var1;
      if (var2 instanceof ParametersWithIV) {
         ParametersWithIV var3 = (ParametersWithIV)var2;
         byte[] var4 = var3.getIV();
         if (var4.length < this.blockSize) {
            throw new IllegalArgumentException("Parameter m must blockSize <= m");
         }

         this.m = var4.length;
         this.initArrays();
         this.R_init = Arrays.clone(var4);
         System.arraycopy(this.R_init, 0, this.R, 0, this.R_init.length);
         if (var3.getParameters() != null) {
            this.cipher.init(var1, var3.getParameters());
         }
      } else {
         this.setupDefaultParams();
         this.initArrays();
         System.arraycopy(this.R_init, 0, this.R, 0, this.R_init.length);
         if (var2 != null) {
            this.cipher.init(var1, var2);
         }
      }

      this.initialized = true;
   }

   private void initArrays() {
      this.R = new byte[this.m];
      this.R_init = new byte[this.m];
   }

   private void setupDefaultParams() {
      this.m = this.blockSize;
   }

   @Override
   public String getAlgorithmName() {
      return this.cipher.getAlgorithmName() + "/CBC";
   }

   @Override
   public int getBlockSize() {
      return this.blockSize;
   }

   @Override
   public int processBlock(byte[] var1, int var2, byte[] var3, int var4) throws DataLengthException, IllegalStateException {
      return this.forEncryption ? this.encrypt(var1, var2, var3, var4) : this.decrypt(var1, var2, var3, var4);
   }

   private int encrypt(byte[] var1, int var2, byte[] var3, int var4) {
      byte[] var5 = GOST3413CipherUtil.MSB(this.R, this.blockSize);
      byte[] var6 = GOST3413CipherUtil.copyFromInput(var1, this.blockSize, var2);
      byte[] var7 = GOST3413CipherUtil.sum(var6, var5);
      byte[] var8 = new byte[var7.length];
      this.cipher.processBlock(var7, 0, var8, 0);
      System.arraycopy(var8, 0, var3, var4, var8.length);
      if (var3.length > var4 + var7.length) {
         this.generateR(var8);
      }

      return var8.length;
   }

   private int decrypt(byte[] var1, int var2, byte[] var3, int var4) {
      byte[] var5 = GOST3413CipherUtil.MSB(this.R, this.blockSize);
      byte[] var6 = GOST3413CipherUtil.copyFromInput(var1, this.blockSize, var2);
      byte[] var7 = new byte[var6.length];
      this.cipher.processBlock(var6, 0, var7, 0);
      byte[] var8 = GOST3413CipherUtil.sum(var7, var5);
      System.arraycopy(var8, 0, var3, var4, var8.length);
      if (var3.length > var4 + var8.length) {
         this.generateR(var6);
      }

      return var8.length;
   }

   private void generateR(byte[] var1) {
      byte[] var2 = GOST3413CipherUtil.LSB(this.R, this.m - this.blockSize);
      System.arraycopy(var2, 0, this.R, 0, var2.length);
      System.arraycopy(var1, 0, this.R, var2.length, this.m - var2.length);
   }

   @Override
   public void reset() {
      if (this.initialized) {
         System.arraycopy(this.R_init, 0, this.R, 0, this.R_init.length);
         this.cipher.reset();
      }
   }
}
