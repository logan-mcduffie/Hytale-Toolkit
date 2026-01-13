package org.bouncycastle.crypto.macs;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.ISO7816d4Padding;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.Pack;

public class CMac implements Mac {
   private byte[] poly;
   private byte[] ZEROES;
   private byte[] mac;
   private byte[] buf;
   private int bufOff;
   private BlockCipher cipher;
   private int macSize;
   private byte[] Lu;
   private byte[] Lu2;

   public CMac(BlockCipher var1) {
      this(var1, var1.getBlockSize() * 8);
   }

   public CMac(BlockCipher var1, int var2) {
      if (var2 % 8 != 0) {
         throw new IllegalArgumentException("MAC size must be multiple of 8");
      } else if (var2 > var1.getBlockSize() * 8) {
         throw new IllegalArgumentException("MAC size must be less or equal to " + var1.getBlockSize() * 8);
      } else {
         this.cipher = CBCBlockCipher.newInstance(var1);
         this.macSize = var2 / 8;
         this.poly = lookupPoly(var1.getBlockSize());
         this.mac = new byte[var1.getBlockSize()];
         this.buf = new byte[var1.getBlockSize()];
         this.ZEROES = new byte[var1.getBlockSize()];
         this.bufOff = 0;
      }
   }

   @Override
   public String getAlgorithmName() {
      return this.cipher.getAlgorithmName();
   }

   private static int shiftLeft(byte[] var0, byte[] var1) {
      int var2 = var0.length;
      int var3 = 0;

      while (--var2 >= 0) {
         int var4 = var0[var2] & 255;
         var1[var2] = (byte)(var4 << 1 | var3);
         var3 = var4 >>> 7 & 1;
      }

      return var3;
   }

   private byte[] doubleLu(byte[] var1) {
      byte[] var2 = new byte[var1.length];
      int var3 = shiftLeft(var1, var2);
      int var4 = -var3 & 0xFF;
      var2[var1.length - 3] = (byte)(var2[var1.length - 3] ^ this.poly[1] & var4);
      var2[var1.length - 2] = (byte)(var2[var1.length - 2] ^ this.poly[2] & var4);
      var2[var1.length - 1] = (byte)(var2[var1.length - 1] ^ this.poly[3] & var4);
      return var2;
   }

   private static byte[] lookupPoly(int var0) {
      int var1;
      switch (var0 * 8) {
         case 64:
            var1 = 27;
            break;
         case 128:
            var1 = 135;
            break;
         case 160:
            var1 = 45;
            break;
         case 192:
            var1 = 135;
            break;
         case 224:
            var1 = 777;
            break;
         case 256:
            var1 = 1061;
            break;
         case 320:
            var1 = 27;
            break;
         case 384:
            var1 = 4109;
            break;
         case 448:
            var1 = 2129;
            break;
         case 512:
            var1 = 293;
            break;
         case 768:
            var1 = 655377;
            break;
         case 1024:
            var1 = 524355;
            break;
         case 2048:
            var1 = 548865;
            break;
         default:
            throw new IllegalArgumentException("Unknown block size for CMAC: " + var0 * 8);
      }

      return Pack.intToBigEndian(var1);
   }

   @Override
   public void init(CipherParameters var1) {
      this.validate(var1);
      this.cipher.init(true, var1);
      byte[] var2 = new byte[this.ZEROES.length];
      this.cipher.processBlock(this.ZEROES, 0, var2, 0);
      this.Lu = this.doubleLu(var2);
      this.Lu2 = this.doubleLu(this.Lu);
      this.reset();
   }

   void validate(CipherParameters var1) {
      if (var1 != null && !(var1 instanceof KeyParameter)) {
         throw new IllegalArgumentException("CMac mode only permits key to be set.");
      }
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
      byte[] var4;
      if (this.bufOff == var3) {
         var4 = this.Lu;
      } else {
         new ISO7816d4Padding().addPadding(this.buf, this.bufOff);
         var4 = this.Lu2;
      }

      for (int var5 = 0; var5 < this.mac.length; var5++) {
         this.buf[var5] = (byte)(this.buf[var5] ^ var4[var5]);
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
