package org.bouncycastle.crypto.macs;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.BlockCipherPadding;

public class CBCBlockCipherMac implements Mac {
   private byte[] mac;
   private byte[] buf;
   private int bufOff;
   private BlockCipher cipher;
   private BlockCipherPadding padding;
   private int macSize;

   public CBCBlockCipherMac(BlockCipher var1) {
      this(var1, var1.getBlockSize() * 8 / 2, null);
   }

   public CBCBlockCipherMac(BlockCipher var1, BlockCipherPadding var2) {
      this(var1, var1.getBlockSize() * 8 / 2, var2);
   }

   public CBCBlockCipherMac(BlockCipher var1, int var2) {
      this(var1, var2, null);
   }

   public CBCBlockCipherMac(BlockCipher var1, int var2, BlockCipherPadding var3) {
      if (var2 % 8 != 0) {
         throw new IllegalArgumentException("MAC size must be multiple of 8");
      } else {
         this.cipher = CBCBlockCipher.newInstance(var1);
         this.padding = var3;
         this.macSize = var2 / 8;
         this.mac = new byte[var1.getBlockSize()];
         this.buf = new byte[var1.getBlockSize()];
         this.bufOff = 0;
      }
   }

   @Override
   public String getAlgorithmName() {
      return this.cipher.getAlgorithmName();
   }

   @Override
   public void init(CipherParameters var1) {
      this.reset();
      this.cipher.init(true, var1);
   }

   @Override
   public int getMacSize() {
      return this.macSize;
   }

   @Override
   public void update(byte var1) {
      if (this.bufOff == this.buf.length) {
         this.cipher.processBlock(this.buf, 0, this.mac, 0);
         this.bufOff = 0;
      }

      this.buf[this.bufOff++] = var1;
   }

   @Override
   public void update(byte[] var1, int var2, int var3) {
      if (var3 < 0) {
         throw new IllegalArgumentException("Can't have a negative input length!");
      } else {
         int var4 = this.cipher.getBlockSize();
         int var5 = var4 - this.bufOff;
         if (var3 > var5) {
            System.arraycopy(var1, var2, this.buf, this.bufOff, var5);
            this.cipher.processBlock(this.buf, 0, this.mac, 0);
            this.bufOff = 0;
            var3 -= var5;

            for (var2 += var5; var3 > var4; var2 += var4) {
               this.cipher.processBlock(var1, var2, this.mac, 0);
               var3 -= var4;
            }
         }

         System.arraycopy(var1, var2, this.buf, this.bufOff, var3);
         this.bufOff += var3;
      }
   }

   @Override
   public int doFinal(byte[] var1, int var2) {
      int var3 = this.cipher.getBlockSize();
      if (this.padding == null) {
         while (this.bufOff < var3) {
            this.buf[this.bufOff] = 0;
            this.bufOff++;
         }
      } else {
         if (this.bufOff == var3) {
            this.cipher.processBlock(this.buf, 0, this.mac, 0);
            this.bufOff = 0;
         }

         this.padding.addPadding(this.buf, this.bufOff);
      }

      this.cipher.processBlock(this.buf, 0, this.mac, 0);
      System.arraycopy(this.mac, 0, var1, var2, this.macSize);
      this.reset();
      return this.macSize;
   }

   @Override
   public void reset() {
      for (int var1 = 0; var1 < this.buf.length; var1++) {
         this.buf[var1] = 0;
      }

      this.bufOff = 0;
      this.cipher.reset();
   }
}
