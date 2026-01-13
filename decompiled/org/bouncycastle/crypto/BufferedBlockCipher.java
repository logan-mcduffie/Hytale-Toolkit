package org.bouncycastle.crypto;

public class BufferedBlockCipher {
   protected byte[] buf;
   protected int bufOff;
   protected boolean forEncryption;
   protected BlockCipher cipher;
   protected MultiBlockCipher mbCipher;
   protected boolean partialBlockOkay;
   protected boolean pgpCFB;

   BufferedBlockCipher() {
   }

   /** @deprecated */
   public BufferedBlockCipher(BlockCipher var1) {
      this.cipher = var1;
      if (var1 instanceof MultiBlockCipher) {
         this.mbCipher = (MultiBlockCipher)var1;
         this.buf = new byte[this.mbCipher.getMultiBlockSize()];
      } else {
         this.mbCipher = null;
         this.buf = new byte[var1.getBlockSize()];
      }

      this.bufOff = 0;
      String var2 = var1.getAlgorithmName();
      int var3 = var2.indexOf(47) + 1;
      this.pgpCFB = var3 > 0 && var2.startsWith("PGP", var3);
      if (!this.pgpCFB && !(var1 instanceof StreamCipher)) {
         this.partialBlockOkay = var3 > 0 && var2.startsWith("OpenPGP", var3);
      } else {
         this.partialBlockOkay = true;
      }
   }

   public BlockCipher getUnderlyingCipher() {
      return this.cipher;
   }

   public void init(boolean var1, CipherParameters var2) throws IllegalArgumentException {
      this.forEncryption = var1;
      this.reset();
      this.cipher.init(var1, var2);
   }

   public int getBlockSize() {
      return this.cipher.getBlockSize();
   }

   public int getUpdateOutputSize(int var1) {
      int var2 = var1 + this.bufOff;
      int var3;
      if (this.pgpCFB) {
         if (this.forEncryption) {
            var3 = var2 % this.buf.length - (this.cipher.getBlockSize() + 2);
         } else {
            var3 = var2 % this.buf.length;
         }
      } else {
         var3 = var2 % this.buf.length;
      }

      return var2 - var3;
   }

   public int getOutputSize(int var1) {
      return this.pgpCFB && this.forEncryption ? var1 + this.bufOff + this.cipher.getBlockSize() + 2 : var1 + this.bufOff;
   }

   public int processByte(byte var1, byte[] var2, int var3) throws DataLengthException, IllegalStateException {
      int var4 = 0;
      this.buf[this.bufOff++] = var1;
      if (this.bufOff == this.buf.length) {
         var4 = this.processBuffer(var2, var3);
      }

      return var4;
   }

   public int processBytes(byte[] var1, int var2, int var3, byte[] var4, int var5) throws DataLengthException, IllegalStateException {
      if (var3 < 0) {
         throw new IllegalArgumentException("Can't have a negative input length!");
      } else {
         int var6 = this.getBlockSize();
         int var7 = this.getUpdateOutputSize(var3);
         if (var7 > 0 && var5 + var7 > var4.length) {
            throw new OutputLengthException("output buffer too short");
         } else {
            int var8 = 0;
            int var9 = this.buf.length - this.bufOff;
            if (var3 > var9) {
               if (this.bufOff != 0) {
                  System.arraycopy(var1, var2, this.buf, this.bufOff, var9);
                  var2 += var9;
                  var3 -= var9;
               }

               if (var1 == var4) {
                  var1 = new byte[var3];
                  System.arraycopy(var4, var2, var1, 0, var3);
                  var2 = 0;
               }

               if (this.bufOff != 0) {
                  var8 += this.processBuffer(var4, var5);
               }

               if (this.mbCipher != null) {
                  int var10 = var3 / this.mbCipher.getMultiBlockSize() * (this.mbCipher.getMultiBlockSize() / var6);
                  if (var10 > 0) {
                     var8 += this.mbCipher.processBlocks(var1, var2, var10, var4, var5 + var8);
                     int var11 = var10 * var6;
                     var3 -= var11;
                     var2 += var11;
                  }
               } else {
                  while (var3 > this.buf.length) {
                     var8 += this.cipher.processBlock(var1, var2, var4, var5 + var8);
                     var3 -= var6;
                     var2 += var6;
                  }
               }
            }

            System.arraycopy(var1, var2, this.buf, this.bufOff, var3);
            this.bufOff += var3;
            if (this.bufOff == this.buf.length) {
               var8 += this.processBuffer(var4, var5 + var8);
            }

            return var8;
         }
      }
   }

   private int processBuffer(byte[] var1, int var2) {
      this.bufOff = 0;
      return this.mbCipher != null
         ? this.mbCipher.processBlocks(this.buf, 0, this.buf.length / this.mbCipher.getBlockSize(), var1, var2)
         : this.cipher.processBlock(this.buf, 0, var1, var2);
   }

   public int doFinal(byte[] var1, int var2) throws DataLengthException, IllegalStateException, InvalidCipherTextException {
      int var9;
      try {
         int var3 = 0;
         if (var2 + this.bufOff > var1.length) {
            throw new OutputLengthException("output buffer too short for doFinal()");
         }

         if (this.bufOff != 0) {
            var9 = 0;
            if (this.mbCipher != null) {
               int var5 = this.bufOff / this.mbCipher.getBlockSize();
               var3 += this.mbCipher.processBlocks(this.buf, 0, var5, var1, var2);
               var9 = var5 * this.mbCipher.getBlockSize();
            }

            if (this.bufOff != var9) {
               if (!this.partialBlockOkay) {
                  throw new DataLengthException("data not block size aligned");
               }

               this.cipher.processBlock(this.buf, var9, this.buf, var9);
               System.arraycopy(this.buf, var9, var1, var2 + var3, this.bufOff - var9);
               var3 += this.bufOff - var9;
               this.bufOff = 0;
            }
         }

         var9 = var3;
      } finally {
         this.reset();
      }

      return var9;
   }

   public void reset() {
      for (int var1 = 0; var1 < this.buf.length; var1++) {
         this.buf[var1] = 0;
      }

      this.bufOff = 0;
      this.cipher.reset();
   }
}
