package org.bouncycastle.crypto.paddings;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.DefaultBufferedBlockCipher;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.util.Arrays;

public class PaddedBufferedBlockCipher extends DefaultBufferedBlockCipher {
   BlockCipherPadding padding;

   public PaddedBufferedBlockCipher(BlockCipher var1, BlockCipherPadding var2) {
      this.cipher = var1;
      this.padding = var2;
      this.buf = new byte[var1.getBlockSize()];
      this.bufOff = 0;
   }

   public PaddedBufferedBlockCipher(BlockCipher var1) {
      this(var1, new PKCS7Padding());
   }

   @Override
   public void init(boolean var1, CipherParameters var2) throws IllegalArgumentException {
      this.forEncryption = var1;
      this.reset();
      if (var2 instanceof ParametersWithRandom) {
         ParametersWithRandom var3 = (ParametersWithRandom)var2;
         this.padding.init(var3.getRandom());
         this.cipher.init(var1, var3.getParameters());
      } else {
         this.padding.init(null);
         this.cipher.init(var1, var2);
      }
   }

   @Override
   public int getOutputSize(int var1) {
      int var2 = var1 + this.bufOff;
      int var3 = var2 % this.buf.length;
      if (var3 == 0) {
         return this.forEncryption ? var2 + this.buf.length : var2;
      } else {
         return var2 - var3 + this.buf.length;
      }
   }

   @Override
   public int getUpdateOutputSize(int var1) {
      int var2 = var1 + this.bufOff;
      int var3 = var2 % this.buf.length;
      return var3 == 0 ? Math.max(0, var2 - this.buf.length) : var2 - var3;
   }

   @Override
   public int processByte(byte var1, byte[] var2, int var3) throws DataLengthException, IllegalStateException {
      int var4 = 0;
      if (this.bufOff == this.buf.length) {
         var4 = this.cipher.processBlock(this.buf, 0, var2, var3);
         this.bufOff = 0;
      }

      this.buf[this.bufOff++] = var1;
      return var4;
   }

   @Override
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
               System.arraycopy(var1, var2, this.buf, this.bufOff, var9);
               var2 += var9;
               var3 -= var9;
               if (var1 == var4 && Arrays.segmentsOverlap(var2, var3, var5, var7)) {
                  var1 = new byte[var3];
                  System.arraycopy(var4, var2, var1, 0, var3);
                  var2 = 0;
               }

               var8 += this.cipher.processBlock(this.buf, 0, var4, var5);

               for (this.bufOff = 0; var3 > this.buf.length; var2 += var6) {
                  var8 += this.cipher.processBlock(var1, var2, var4, var5 + var8);
                  var3 -= var6;
               }
            }

            System.arraycopy(var1, var2, this.buf, this.bufOff, var3);
            this.bufOff += var3;
            return var8;
         }
      }
   }

   @Override
   public int doFinal(byte[] var1, int var2) throws DataLengthException, IllegalStateException, InvalidCipherTextException {
      int var3 = this.cipher.getBlockSize();
      int var4 = 0;
      if (this.forEncryption) {
         if (this.bufOff == var3) {
            if (var2 + 2 * var3 > var1.length) {
               this.reset();
               throw new OutputLengthException("output buffer too short");
            }

            var4 = this.cipher.processBlock(this.buf, 0, var1, var2);
            this.bufOff = 0;
         }

         this.padding.addPadding(this.buf, this.bufOff);
         var4 += this.cipher.processBlock(this.buf, 0, var1, var2 + var4);
         this.reset();
      } else {
         if (this.bufOff != var3) {
            this.reset();
            throw new DataLengthException("last block incomplete in decryption");
         }

         var4 = this.cipher.processBlock(this.buf, 0, this.buf, 0);
         this.bufOff = 0;

         try {
            var4 -= this.padding.padCount(this.buf);
            System.arraycopy(this.buf, 0, var1, var2, var4);
         } finally {
            this.reset();
         }
      }

      return var4;
   }
}
