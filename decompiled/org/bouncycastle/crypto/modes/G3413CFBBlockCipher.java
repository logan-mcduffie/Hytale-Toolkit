package org.bouncycastle.crypto.modes;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.StreamBlockCipher;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.Arrays;

public class G3413CFBBlockCipher extends StreamBlockCipher {
   private final int s;
   private int m;
   private int blockSize;
   private byte[] R;
   private byte[] R_init;
   private BlockCipher cipher;
   private boolean forEncryption;
   private boolean initialized = false;
   private byte[] gamma;
   private byte[] inBuf;
   private int byteCount;

   public G3413CFBBlockCipher(BlockCipher var1) {
      this(var1, var1.getBlockSize() * 8);
   }

   public G3413CFBBlockCipher(BlockCipher var1, int var2) {
      super(var1);
      if (var2 >= 0 && var2 <= var1.getBlockSize() * 8) {
         this.blockSize = var1.getBlockSize();
         this.cipher = var1;
         this.s = var2 / 8;
         this.inBuf = new byte[this.getBlockSize()];
      } else {
         throw new IllegalArgumentException("Parameter bitBlockSize must be in range 0 < bitBlockSize <= " + var1.getBlockSize() * 8);
      }
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
      return this.cipher.getAlgorithmName() + "/CFB" + this.blockSize * 8;
   }

   @Override
   public int getBlockSize() {
      return this.s;
   }

   @Override
   public int processBlock(byte[] var1, int var2, byte[] var3, int var4) throws DataLengthException, IllegalStateException {
      this.processBytes(var1, var2, this.getBlockSize(), var3, var4);
      return this.getBlockSize();
   }

   @Override
   protected byte calculateByte(byte var1) {
      if (this.byteCount == 0) {
         this.gamma = this.createGamma();
      }

      byte var2 = (byte)(this.gamma[this.byteCount] ^ var1);
      this.inBuf[this.byteCount++] = this.forEncryption ? var2 : var1;
      if (this.byteCount == this.getBlockSize()) {
         this.byteCount = 0;
         this.generateR(this.inBuf);
      }

      return var2;
   }

   byte[] createGamma() {
      byte[] var1 = GOST3413CipherUtil.MSB(this.R, this.blockSize);
      byte[] var2 = new byte[var1.length];
      this.cipher.processBlock(var1, 0, var2, 0);
      return GOST3413CipherUtil.MSB(var2, this.s);
   }

   void generateR(byte[] var1) {
      byte[] var2 = GOST3413CipherUtil.LSB(this.R, this.m - this.s);
      System.arraycopy(var2, 0, this.R, 0, var2.length);
      System.arraycopy(var1, 0, this.R, var2.length, this.m - var2.length);
   }

   @Override
   public void reset() {
      this.byteCount = 0;
      Arrays.clear(this.inBuf);
      Arrays.clear(this.gamma);
      if (this.initialized) {
         System.arraycopy(this.R_init, 0, this.R, 0, this.R_init.length);
         this.cipher.reset();
      }
   }
}
