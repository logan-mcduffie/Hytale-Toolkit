package org.bouncycastle.crypto.macs;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.paddings.BlockCipherPadding;

public class CFBBlockCipherMac implements Mac {
   private byte[] mac;
   private byte[] buf;
   private int bufOff;
   private MacCFBBlockCipher cipher;
   private BlockCipherPadding padding = null;
   private int macSize;

   public CFBBlockCipherMac(BlockCipher var1) {
      this(var1, 8, var1.getBlockSize() * 8 / 2, null);
   }

   public CFBBlockCipherMac(BlockCipher var1, BlockCipherPadding var2) {
      this(var1, 8, var1.getBlockSize() * 8 / 2, var2);
   }

   public CFBBlockCipherMac(BlockCipher var1, int var2, int var3) {
      this(var1, var2, var3, null);
   }

   public CFBBlockCipherMac(BlockCipher var1, int var2, int var3, BlockCipherPadding var4) {
      if (var3 % 8 != 0) {
         throw new IllegalArgumentException("MAC size must be multiple of 8");
      } else {
         this.mac = new byte[var1.getBlockSize()];
         this.cipher = new MacCFBBlockCipher(var1, var2);
         this.padding = var4;
         this.macSize = var3 / 8;
         this.buf = new byte[this.cipher.getBlockSize()];
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
      this.cipher.init(var1);
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
         int var5 = 0;
         int var6 = var4 - this.bufOff;
         if (var3 > var6) {
            System.arraycopy(var1, var2, this.buf, this.bufOff, var6);
            var5 += this.cipher.processBlock(this.buf, 0, this.mac, 0);
            this.bufOff = 0;
            var3 -= var6;

            for (var2 += var6; var3 > var4; var2 += var4) {
               var5 += this.cipher.processBlock(var1, var2, this.mac, 0);
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
         this.padding.addPadding(this.buf, this.bufOff);
      }

      this.cipher.processBlock(this.buf, 0, this.mac, 0);
      this.cipher.getMacBlock(this.mac);
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
