package org.bouncycastle.crypto.modes;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.StreamBlockCipher;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.Arrays;

public class KCTRBlockCipher extends StreamBlockCipher {
   private byte[] iv;
   private byte[] ofbV;
   private byte[] ofbOutV;
   private int byteCount;
   private boolean initialised;
   private BlockCipher engine;

   public KCTRBlockCipher(BlockCipher var1) {
      super(var1);
      this.engine = var1;
      this.iv = new byte[var1.getBlockSize()];
      this.ofbV = new byte[var1.getBlockSize()];
      this.ofbOutV = new byte[var1.getBlockSize()];
   }

   @Override
   public void init(boolean var1, CipherParameters var2) throws IllegalArgumentException {
      this.initialised = true;
      if (var2 instanceof ParametersWithIV) {
         ParametersWithIV var3 = (ParametersWithIV)var2;
         byte[] var4 = var3.getIV();
         int var5 = this.iv.length - var4.length;
         Arrays.fill(this.iv, (byte)0);
         System.arraycopy(var4, 0, this.iv, var5, var4.length);
         var2 = var3.getParameters();
         if (var2 != null) {
            this.engine.init(true, var2);
         }

         this.reset();
      } else {
         throw new IllegalArgumentException("invalid parameter passed");
      }
   }

   @Override
   public String getAlgorithmName() {
      return this.engine.getAlgorithmName() + "/KCTR";
   }

   @Override
   public int getBlockSize() {
      return this.engine.getBlockSize();
   }

   @Override
   protected byte calculateByte(byte var1) {
      if (this.byteCount == 0) {
         this.incrementCounterAt(0);
         this.checkCounter();
         this.engine.processBlock(this.ofbV, 0, this.ofbOutV, 0);
         return (byte)(this.ofbOutV[this.byteCount++] ^ var1);
      } else {
         byte var2 = (byte)(this.ofbOutV[this.byteCount++] ^ var1);
         if (this.byteCount == this.ofbV.length) {
            this.byteCount = 0;
         }

         return var2;
      }
   }

   @Override
   public int processBlock(byte[] var1, int var2, byte[] var3, int var4) throws DataLengthException, IllegalStateException {
      if (var1.length - var2 < this.getBlockSize()) {
         throw new DataLengthException("input buffer too short");
      } else if (var3.length - var4 < this.getBlockSize()) {
         throw new OutputLengthException("output buffer too short");
      } else {
         this.processBytes(var1, var2, this.getBlockSize(), var3, var4);
         return this.getBlockSize();
      }
   }

   @Override
   public void reset() {
      if (this.initialised) {
         this.engine.processBlock(this.iv, 0, this.ofbV, 0);
      }

      this.engine.reset();
      this.byteCount = 0;
   }

   private void incrementCounterAt(int var1) {
      int var2 = var1;

      while (var2 < this.ofbV.length && ++this.ofbV[var2++] == 0) {
      }
   }

   private void checkCounter() {
   }
}
