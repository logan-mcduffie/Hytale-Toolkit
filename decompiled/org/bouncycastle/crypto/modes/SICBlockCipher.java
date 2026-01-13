package org.bouncycastle.crypto.modes;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.StreamBlockCipher;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Pack;

public class SICBlockCipher extends StreamBlockCipher implements CTRModeCipher {
   private final BlockCipher cipher;
   private final int blockSize;
   private byte[] IV;
   private byte[] counter;
   private byte[] counterOut;
   private int byteCount;

   public static CTRModeCipher newInstance(BlockCipher var0) {
      return new SICBlockCipher(var0);
   }

   @Deprecated
   public SICBlockCipher(BlockCipher var1) {
      super(var1);
      this.cipher = var1;
      this.blockSize = this.cipher.getBlockSize();
      this.IV = new byte[this.blockSize];
      this.counter = new byte[this.blockSize];
      this.counterOut = new byte[this.blockSize];
      this.byteCount = 0;
   }

   @Override
   public void init(boolean var1, CipherParameters var2) throws IllegalArgumentException {
      if (var2 instanceof ParametersWithIV) {
         ParametersWithIV var3 = (ParametersWithIV)var2;
         this.IV = Arrays.clone(var3.getIV());
         if (this.blockSize < this.IV.length) {
            throw new IllegalArgumentException("CTR/SIC mode requires IV no greater than: " + this.blockSize + " bytes.");
         } else {
            int var4 = 8 > this.blockSize / 2 ? this.blockSize / 2 : 8;
            if (this.blockSize - this.IV.length > var4) {
               throw new IllegalArgumentException("CTR/SIC mode requires IV of at least: " + (this.blockSize - var4) + " bytes.");
            } else {
               if (var3.getParameters() != null) {
                  this.cipher.init(true, var3.getParameters());
               }

               this.reset();
            }
         }
      } else {
         throw new IllegalArgumentException("CTR/SIC mode requires ParametersWithIV");
      }
   }

   @Override
   public String getAlgorithmName() {
      return this.cipher.getAlgorithmName() + "/SIC";
   }

   @Override
   public int getBlockSize() {
      return this.cipher.getBlockSize();
   }

   @Override
   public int processBlock(byte[] var1, int var2, byte[] var3, int var4) throws DataLengthException, IllegalStateException {
      if (this.byteCount != 0) {
         this.processBytes(var1, var2, this.blockSize, var3, var4);
         return this.blockSize;
      } else if (var2 + this.blockSize > var1.length) {
         throw new DataLengthException("input buffer too small");
      } else if (var4 + this.blockSize > var3.length) {
         throw new OutputLengthException("output buffer too short");
      } else {
         this.cipher.processBlock(this.counter, 0, this.counterOut, 0);

         for (int var5 = 0; var5 < this.blockSize; var5++) {
            var3[var4 + var5] = (byte)(var1[var2 + var5] ^ this.counterOut[var5]);
         }

         this.incrementCounter();
         return this.blockSize;
      }
   }

   @Override
   public int processBytes(byte[] var1, int var2, int var3, byte[] var4, int var5) throws DataLengthException {
      if (var2 + var3 > var1.length) {
         throw new DataLengthException("input buffer too small");
      } else if (var5 + var3 > var4.length) {
         throw new OutputLengthException("output buffer too short");
      } else {
         for (int var6 = 0; var6 < var3; var6++) {
            byte var7;
            if (this.byteCount == 0) {
               this.checkLastIncrement();
               this.cipher.processBlock(this.counter, 0, this.counterOut, 0);
               var7 = (byte)(var1[var2 + var6] ^ this.counterOut[this.byteCount++]);
            } else {
               var7 = (byte)(var1[var2 + var6] ^ this.counterOut[this.byteCount++]);
               if (this.byteCount == this.counter.length) {
                  this.byteCount = 0;
                  this.incrementCounter();
               }
            }

            var4[var5 + var6] = var7;
         }

         return var3;
      }
   }

   @Override
   protected byte calculateByte(byte var1) throws DataLengthException, IllegalStateException {
      if (this.byteCount == 0) {
         this.checkLastIncrement();
         this.cipher.processBlock(this.counter, 0, this.counterOut, 0);
         return (byte)(this.counterOut[this.byteCount++] ^ var1);
      } else {
         byte var2 = (byte)(this.counterOut[this.byteCount++] ^ var1);
         if (this.byteCount == this.counter.length) {
            this.byteCount = 0;
            this.incrementCounter();
         }

         return var2;
      }
   }

   private void checkCounter() {
      if (this.IV.length < this.blockSize) {
         for (int var1 = this.IV.length - 1; var1 >= 0; var1--) {
            if (this.counter[var1] != this.IV[var1]) {
               throw new IllegalStateException("Counter in CTR/SIC mode out of range.");
            }
         }
      }
   }

   private void checkLastIncrement() {
      if (this.IV.length < this.blockSize && this.counter[this.IV.length - 1] != this.IV[this.IV.length - 1]) {
         throw new IllegalStateException("Counter in CTR/SIC mode out of range.");
      }
   }

   private void incrementCounter() {
      int var1 = this.counter.length;

      do {
         var1--;
      } while (var1 >= 0 && ++this.counter[var1] == 0);
   }

   private void incrementCounterAt(int var1) {
      int var2 = this.counter.length - var1;

      do {
         var2--;
      } while (var2 >= 0 && ++this.counter[var2] == 0);
   }

   private void incrementCounter(int var1) {
      byte var2 = this.counter[this.counter.length - 1];
      this.counter[this.counter.length - 1] = (byte)(this.counter[this.counter.length - 1] + (byte)var1);
      if ((var2 & 255) + var1 > 255) {
         this.incrementCounterAt(1);
      }
   }

   private void decrementCounterAt(int var1) {
      int var2 = this.counter.length - var1;

      while (--var2 >= 0) {
         if (--this.counter[var2] != -1) {
            return;
         }
      }
   }

   private void adjustCounter(long var1) {
      if (var1 >= 0L) {
         long var3 = (var1 + this.byteCount) / this.blockSize;
         long var5 = var3;
         if (var3 > 255L) {
            for (int var7 = 5; var7 >= 1; var7--) {
               for (long var8 = 1L << 8 * var7; var5 >= var8; var5 -= var8) {
                  this.incrementCounterAt(var7);
               }
            }
         }

         this.incrementCounter((int)var5);
         this.byteCount = (int)(var1 + this.byteCount - this.blockSize * var3);
      } else {
         long var10 = (-var1 - this.byteCount) / this.blockSize;
         long var11 = var10;
         if (var10 > 255L) {
            for (int var12 = 5; var12 >= 1; var12--) {
               for (long var15 = 1L << 8 * var12; var11 > var15; var11 -= var15) {
                  this.decrementCounterAt(var12);
               }
            }
         }

         for (long var13 = 0L; var13 != var11; var13++) {
            this.decrementCounterAt(0);
         }

         int var14 = (int)(this.byteCount + var1 + this.blockSize * var10);
         if (var14 >= 0) {
            this.byteCount = 0;
         } else {
            this.decrementCounterAt(0);
            this.byteCount = this.blockSize + var14;
         }
      }
   }

   @Override
   public void reset() {
      Arrays.fill(this.counter, (byte)0);
      System.arraycopy(this.IV, 0, this.counter, 0, this.IV.length);
      this.cipher.reset();
      this.byteCount = 0;
   }

   @Override
   public long skip(long var1) {
      this.adjustCounter(var1);
      this.checkCounter();
      this.cipher.processBlock(this.counter, 0, this.counterOut, 0);
      return var1;
   }

   @Override
   public long seekTo(long var1) {
      this.reset();
      return this.skip(var1);
   }

   @Override
   public long getPosition() {
      byte[] var1 = new byte[this.counter.length];
      System.arraycopy(this.counter, 0, var1, 0, var1.length);

      for (int var2 = var1.length - 1; var2 >= 1; var2--) {
         int var3;
         if (var2 < this.IV.length) {
            var3 = (var1[var2] & 255) - (this.IV[var2] & 255);
         } else {
            var3 = var1[var2] & 255;
         }

         if (var3 < 0) {
            var1[var2 - 1]--;
            var3 += 256;
         }

         var1[var2] = (byte)var3;
      }

      return Pack.bigEndianToLong(var1, var1.length - 8) * this.blockSize + this.byteCount;
   }
}
