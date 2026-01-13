package org.bouncycastle.crypto.engines;

import org.bouncycastle.util.Pack;

public class ChaCha7539Engine extends Salsa20Engine {
   @Override
   public String getAlgorithmName() {
      return "ChaCha7539";
   }

   @Override
   protected int getNonceSize() {
      return 12;
   }

   @Override
   protected void advanceCounter(long var1) {
      int var3 = (int)(var1 >>> 32);
      int var4 = (int)var1;
      if (var3 > 0) {
         throw new IllegalStateException("attempt to increase counter past 2^32.");
      } else {
         int var5 = this.engineState[12];
         this.engineState[12] = this.engineState[12] + var4;
         if (var5 != 0 && this.engineState[12] < var5) {
            throw new IllegalStateException("attempt to increase counter past 2^32.");
         }
      }
   }

   @Override
   protected void advanceCounter() {
      if (++this.engineState[12] == 0) {
         throw new IllegalStateException("attempt to increase counter past 2^32.");
      }
   }

   @Override
   protected void retreatCounter(long var1) {
      int var3 = (int)(var1 >>> 32);
      int var4 = (int)var1;
      if (var3 != 0) {
         throw new IllegalStateException("attempt to reduce counter past zero.");
      } else if ((this.engineState[12] & 4294967295L) >= (var4 & 4294967295L)) {
         this.engineState[12] = this.engineState[12] - var4;
      } else {
         throw new IllegalStateException("attempt to reduce counter past zero.");
      }
   }

   @Override
   protected void retreatCounter() {
      if (this.engineState[12] == 0) {
         throw new IllegalStateException("attempt to reduce counter past zero.");
      } else {
         this.engineState[12]--;
      }
   }

   @Override
   protected long getCounter() {
      return this.engineState[12] & 4294967295L;
   }

   @Override
   protected void resetCounter() {
      this.engineState[12] = 0;
   }

   @Override
   protected void setKey(byte[] var1, byte[] var2) {
      if (var1 != null) {
         if (var1.length != 32) {
            throw new IllegalArgumentException(this.getAlgorithmName() + " requires 256 bit key");
         }

         this.packTauOrSigma(var1.length, this.engineState, 0);
         Pack.littleEndianToInt(var1, 0, this.engineState, 4, 8);
      }

      Pack.littleEndianToInt(var2, 0, this.engineState, 13, 3);
   }

   @Override
   protected void generateKeyStream(byte[] var1) {
      ChaChaEngine.chachaCore(this.rounds, this.engineState, this.x);
      Pack.intToLittleEndian(this.x, var1, 0);
   }
}
