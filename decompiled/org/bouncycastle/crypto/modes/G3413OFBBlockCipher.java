package org.bouncycastle.crypto.modes;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.StreamBlockCipher;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.Arrays;

public class G3413OFBBlockCipher extends StreamBlockCipher {
   private int m;
   private int blockSize;
   private byte[] R;
   private byte[] R_init;
   private byte[] Y;
   private BlockCipher cipher;
   private int byteCount;
   private boolean initialized = false;

   public G3413OFBBlockCipher(BlockCipher var1) {
      super(var1);
      this.blockSize = var1.getBlockSize();
      this.cipher = var1;
      this.Y = new byte[this.blockSize];
   }

   @Override
   public void init(boolean var1, CipherParameters var2) throws IllegalArgumentException {
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
            this.cipher.init(true, var3.getParameters());
         }
      } else {
         this.setupDefaultParams();
         this.initArrays();
         System.arraycopy(this.R_init, 0, this.R, 0, this.R_init.length);
         if (var2 != null) {
            this.cipher.init(true, var2);
         }
      }

      this.initialized = true;
   }

   private void initArrays() {
      this.R = new byte[this.m];
      this.R_init = new byte[this.m];
   }

   private void setupDefaultParams() {
      this.m = 2 * this.blockSize;
   }

   @Override
   public String getAlgorithmName() {
      return this.cipher.getAlgorithmName() + "/OFB";
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
   protected byte calculateByte(byte var1) {
      if (this.byteCount == 0) {
         this.generateY();
      }

      byte var2 = (byte)(this.Y[this.byteCount] ^ var1);
      this.byteCount++;
      if (this.byteCount == this.getBlockSize()) {
         this.byteCount = 0;
         this.generateR();
      }

      return var2;
   }

   private void generateY() {
      byte[] var1 = GOST3413CipherUtil.MSB(this.R, this.blockSize);
      this.cipher.processBlock(var1, 0, this.Y, 0);
   }

   private void generateR() {
      byte[] var1 = GOST3413CipherUtil.LSB(this.R, this.m - this.blockSize);
      System.arraycopy(var1, 0, this.R, 0, var1.length);
      System.arraycopy(this.Y, 0, this.R, var1.length, this.m - var1.length);
   }

   @Override
   public void reset() {
      if (this.initialized) {
         System.arraycopy(this.R_init, 0, this.R, 0, this.R_init.length);
         Arrays.clear(this.Y);
         this.byteCount = 0;
         this.cipher.reset();
      }
   }
}
