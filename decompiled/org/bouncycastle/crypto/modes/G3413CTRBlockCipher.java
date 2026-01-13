package org.bouncycastle.crypto.modes;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.StreamBlockCipher;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.Arrays;

public class G3413CTRBlockCipher extends StreamBlockCipher {
   private final int s;
   private byte[] CTR;
   private byte[] IV;
   private byte[] buf;
   private final int blockSize;
   private final BlockCipher cipher;
   private int byteCount = 0;
   private boolean initialized;

   public G3413CTRBlockCipher(BlockCipher var1) {
      this(var1, var1.getBlockSize() * 8);
   }

   public G3413CTRBlockCipher(BlockCipher var1, int var2) {
      super(var1);
      if (var2 >= 0 && var2 <= var1.getBlockSize() * 8) {
         this.cipher = var1;
         this.blockSize = var1.getBlockSize();
         this.s = var2 / 8;
         this.CTR = new byte[this.blockSize];
      } else {
         throw new IllegalArgumentException("Parameter bitBlockSize must be in range 0 < bitBlockSize <= " + var1.getBlockSize() * 8);
      }
   }

   @Override
   public void init(boolean var1, CipherParameters var2) throws IllegalArgumentException {
      if (var2 instanceof ParametersWithIV) {
         ParametersWithIV var3 = (ParametersWithIV)var2;
         this.initArrays();
         this.IV = Arrays.clone(var3.getIV());
         if (this.IV.length != this.blockSize / 2) {
            throw new IllegalArgumentException("Parameter IV length must be == blockSize/2");
         }

         System.arraycopy(this.IV, 0, this.CTR, 0, this.IV.length);

         for (int var4 = this.IV.length; var4 < this.blockSize; var4++) {
            this.CTR[var4] = 0;
         }

         if (var3.getParameters() != null) {
            this.cipher.init(true, var3.getParameters());
         }
      } else {
         this.initArrays();
         if (var2 != null) {
            this.cipher.init(true, var2);
         }
      }

      this.initialized = true;
   }

   private void initArrays() {
      this.IV = new byte[this.blockSize / 2];
      this.CTR = new byte[this.blockSize];
      this.buf = new byte[this.s];
   }

   @Override
   public String getAlgorithmName() {
      return this.cipher.getAlgorithmName() + "/GCTR";
   }

   @Override
   public int getBlockSize() {
      return this.s;
   }

   @Override
   public int processBlock(byte[] var1, int var2, byte[] var3, int var4) throws DataLengthException, IllegalStateException {
      this.processBytes(var1, var2, this.s, var3, var4);
      return this.s;
   }

   @Override
   protected byte calculateByte(byte var1) {
      if (this.byteCount == 0) {
         this.buf = this.generateBuf();
      }

      byte var2 = (byte)(this.buf[this.byteCount] ^ var1);
      this.byteCount++;
      if (this.byteCount == this.s) {
         this.byteCount = 0;
         this.generateCRT();
      }

      return var2;
   }

   private void generateCRT() {
      this.CTR[this.CTR.length - 1]++;
   }

   private byte[] generateBuf() {
      byte[] var1 = new byte[this.CTR.length];
      this.cipher.processBlock(this.CTR, 0, var1, 0);
      return GOST3413CipherUtil.MSB(var1, this.s);
   }

   @Override
   public void reset() {
      if (this.initialized) {
         System.arraycopy(this.IV, 0, this.CTR, 0, this.IV.length);

         for (int var1 = this.IV.length; var1 < this.blockSize; var1++) {
            this.CTR[var1] = 0;
         }

         this.byteCount = 0;
         this.cipher.reset();
      }
   }
}
