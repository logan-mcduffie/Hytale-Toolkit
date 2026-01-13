package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.OutputLengthException;

public class NullEngine implements BlockCipher {
   private boolean initialised;
   protected static final int DEFAULT_BLOCK_SIZE = 1;
   private final int blockSize;

   public NullEngine() {
      this(1);
   }

   public NullEngine(int var1) {
      this.blockSize = var1;
   }

   @Override
   public void init(boolean var1, CipherParameters var2) throws IllegalArgumentException {
      this.initialised = true;
   }

   @Override
   public String getAlgorithmName() {
      return "Null";
   }

   @Override
   public int getBlockSize() {
      return this.blockSize;
   }

   @Override
   public int processBlock(byte[] var1, int var2, byte[] var3, int var4) throws DataLengthException, IllegalStateException {
      if (!this.initialised) {
         throw new IllegalStateException("Null engine not initialised");
      } else if (var2 + this.blockSize > var1.length) {
         throw new DataLengthException("input buffer too short");
      } else if (var4 + this.blockSize > var3.length) {
         throw new OutputLengthException("output buffer too short");
      } else {
         for (int var5 = 0; var5 < this.blockSize; var5++) {
            var3[var4 + var5] = var1[var2 + var5];
         }

         return this.blockSize;
      }
   }

   @Override
   public void reset() {
   }
}
