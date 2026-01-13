package org.bouncycastle.crypto.modes;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.DefaultBufferedBlockCipher;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.util.Arrays;

public class OldCTSBlockCipher extends DefaultBufferedBlockCipher {
   private int blockSize;

   public OldCTSBlockCipher(BlockCipher var1) {
      if (!(var1 instanceof OFBBlockCipher) && !(var1 instanceof CFBBlockCipher)) {
         this.cipher = var1;
         this.blockSize = var1.getBlockSize();
         this.buf = new byte[this.blockSize * 2];
         this.bufOff = 0;
      } else {
         throw new IllegalArgumentException("CTSBlockCipher can only accept ECB, or CBC ciphers");
      }
   }

   @Override
   public int getUpdateOutputSize(int var1) {
      int var2 = var1 + this.bufOff;
      int var3 = var2 % this.buf.length;
      return var3 == 0 ? var2 - this.buf.length : var2 - var3;
   }

   @Override
   public int getOutputSize(int var1) {
      return var1 + this.bufOff;
   }

   @Override
   public int processByte(byte var1, byte[] var2, int var3) throws DataLengthException, IllegalStateException {
      int var4 = 0;
      if (this.bufOff == this.buf.length) {
         var4 = this.cipher.processBlock(this.buf, 0, var2, var3);
         System.arraycopy(this.buf, this.blockSize, this.buf, 0, this.blockSize);
         this.bufOff = this.blockSize;
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
               System.arraycopy(this.buf, var6, this.buf, 0, var6);

               for (this.bufOff = var6; var3 > var6; var2 += var6) {
                  System.arraycopy(var1, var2, this.buf, this.bufOff, var6);
                  var8 += this.cipher.processBlock(this.buf, 0, var4, var5 + var8);
                  System.arraycopy(this.buf, var6, this.buf, 0, var6);
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
      if (this.bufOff + var2 > var1.length) {
         throw new OutputLengthException("output buffer to small in doFinal");
      } else {
         int var3 = this.cipher.getBlockSize();
         int var4 = this.bufOff - var3;
         byte[] var5 = new byte[var3];
         if (this.forEncryption) {
            this.cipher.processBlock(this.buf, 0, var5, 0);
            if (this.bufOff < var3) {
               throw new DataLengthException("need at least one block of input for CTS");
            }

            for (int var6 = this.bufOff; var6 != this.buf.length; var6++) {
               this.buf[var6] = var5[var6 - var3];
            }

            for (int var8 = var3; var8 != this.bufOff; var8++) {
               this.buf[var8] = (byte)(this.buf[var8] ^ var5[var8 - var3]);
            }

            if (this.cipher instanceof CBCBlockCipher) {
               BlockCipher var9 = ((CBCBlockCipher)this.cipher).getUnderlyingCipher();
               var9.processBlock(this.buf, var3, var1, var2);
            } else {
               this.cipher.processBlock(this.buf, var3, var1, var2);
            }

            System.arraycopy(var5, 0, var1, var2 + var3, var4);
         } else {
            byte[] var10 = new byte[var3];
            if (this.cipher instanceof CBCBlockCipher) {
               BlockCipher var7 = ((CBCBlockCipher)this.cipher).getUnderlyingCipher();
               var7.processBlock(this.buf, 0, var5, 0);
            } else {
               this.cipher.processBlock(this.buf, 0, var5, 0);
            }

            for (int var12 = var3; var12 != this.bufOff; var12++) {
               var10[var12 - var3] = (byte)(var5[var12 - var3] ^ this.buf[var12]);
            }

            System.arraycopy(this.buf, var3, var5, 0, var4);
            this.cipher.processBlock(var5, 0, var1, var2);
            System.arraycopy(var10, 0, var1, var2 + var3, var4);
         }

         int var11 = this.bufOff;
         this.reset();
         return var11;
      }
   }
}
