package org.bouncycastle.crypto.macs;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.engines.DSTU7624Engine;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.Arrays;

public class DSTU7624Mac implements Mac {
   private static final int BITS_IN_BYTE = 8;
   private byte[] buf;
   private int bufOff;
   private int macSize;
   private int blockSize;
   private DSTU7624Engine engine;
   private byte[] c;
   private byte[] cTemp;
   private byte[] kDelta;
   private boolean initCalled = false;

   public DSTU7624Mac(int var1, int var2) {
      this.engine = new DSTU7624Engine(var1);
      this.blockSize = var1 / 8;
      this.macSize = var2 / 8;
      this.c = new byte[this.blockSize];
      this.kDelta = new byte[this.blockSize];
      this.cTemp = new byte[this.blockSize];
      this.buf = new byte[this.blockSize];
   }

   @Override
   public void init(CipherParameters var1) throws IllegalArgumentException {
      if (var1 instanceof KeyParameter) {
         this.engine.init(true, var1);
         this.initCalled = true;
         this.reset();
      } else {
         throw new IllegalArgumentException("Invalid parameter passed to DSTU7624Mac");
      }
   }

   @Override
   public String getAlgorithmName() {
      return "DSTU7624Mac";
   }

   @Override
   public int getMacSize() {
      return this.macSize;
   }

   @Override
   public void update(byte var1) {
      if (this.bufOff == this.buf.length) {
         this.processBlock(this.buf, 0);
         this.bufOff = 0;
      }

      this.buf[this.bufOff++] = var1;
   }

   @Override
   public void update(byte[] var1, int var2, int var3) {
      if (var3 < 0) {
         throw new IllegalArgumentException("can't have a negative input length!");
      } else {
         int var4 = this.engine.getBlockSize();
         int var5 = var4 - this.bufOff;
         if (var3 > var5) {
            System.arraycopy(var1, var2, this.buf, this.bufOff, var5);
            this.processBlock(this.buf, 0);
            this.bufOff = 0;
            var3 -= var5;

            for (var2 += var5; var3 > var4; var2 += var4) {
               this.processBlock(var1, var2);
               var3 -= var4;
            }
         }

         System.arraycopy(var1, var2, this.buf, this.bufOff, var3);
         this.bufOff += var3;
      }
   }

   private void processBlock(byte[] var1, int var2) {
      this.xor(this.c, 0, var1, var2, this.cTemp);
      this.engine.processBlock(this.cTemp, 0, this.c, 0);
   }

   @Override
   public int doFinal(byte[] var1, int var2) throws DataLengthException, IllegalStateException {
      if (this.bufOff % this.buf.length != 0) {
         throw new DataLengthException("input must be a multiple of blocksize");
      } else {
         this.xor(this.c, 0, this.buf, 0, this.cTemp);
         this.xor(this.cTemp, 0, this.kDelta, 0, this.c);
         this.engine.processBlock(this.c, 0, this.c, 0);
         if (this.macSize + var2 > var1.length) {
            throw new OutputLengthException("output buffer too short");
         } else {
            System.arraycopy(this.c, 0, var1, var2, this.macSize);
            this.reset();
            return this.macSize;
         }
      }
   }

   @Override
   public void reset() {
      Arrays.fill(this.c, (byte)0);
      Arrays.fill(this.cTemp, (byte)0);
      Arrays.fill(this.kDelta, (byte)0);
      Arrays.fill(this.buf, (byte)0);
      this.engine.reset();
      if (this.initCalled) {
         this.engine.processBlock(this.kDelta, 0, this.kDelta, 0);
      }

      this.bufOff = 0;
   }

   private void xor(byte[] var1, int var2, byte[] var3, int var4, byte[] var5) {
      if (var1.length - var2 >= this.blockSize && var3.length - var4 >= this.blockSize && var5.length >= this.blockSize) {
         for (int var6 = 0; var6 < this.blockSize; var6++) {
            var5[var6] = (byte)(var1[var6 + var2] ^ var3[var6 + var4]);
         }
      } else {
         throw new IllegalArgumentException("some of input buffers too short");
      }
   }
}
