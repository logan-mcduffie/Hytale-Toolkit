package org.bouncycastle.crypto.macs;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.Pack;

public class Poly1305 implements Mac {
   private static final int BLOCK_SIZE = 16;
   private final BlockCipher cipher;
   private final byte[] singleByte = new byte[1];
   private int r0;
   private int r1;
   private int r2;
   private int r3;
   private int r4;
   private int s1;
   private int s2;
   private int s3;
   private int s4;
   private int k0;
   private int k1;
   private int k2;
   private int k3;
   private final byte[] currentBlock = new byte[16];
   private int currentBlockOffset = 0;
   private int h0;
   private int h1;
   private int h2;
   private int h3;
   private int h4;

   public Poly1305() {
      this.cipher = null;
   }

   public Poly1305(BlockCipher var1) {
      if (var1.getBlockSize() != 16) {
         throw new IllegalArgumentException("Poly1305 requires a 128 bit block cipher.");
      } else {
         this.cipher = var1;
      }
   }

   @Override
   public void init(CipherParameters var1) throws IllegalArgumentException {
      byte[] var2 = null;
      if (this.cipher != null) {
         if (!(var1 instanceof ParametersWithIV)) {
            throw new IllegalArgumentException("Poly1305 requires an IV when used with a block cipher.");
         }

         ParametersWithIV var3 = (ParametersWithIV)var1;
         var2 = var3.getIV();
         var1 = var3.getParameters();
      }

      if (!(var1 instanceof KeyParameter)) {
         throw new IllegalArgumentException("Poly1305 requires a key.");
      } else {
         KeyParameter var4 = (KeyParameter)var1;
         this.setKey(var4.getKey(), var2);
         this.reset();
      }
   }

   private void setKey(byte[] var1, byte[] var2) {
      if (var1.length != 32) {
         throw new IllegalArgumentException("Poly1305 key must be 256 bits.");
      } else if (this.cipher == null || var2 != null && var2.length == 16) {
         int var3 = Pack.littleEndianToInt(var1, 0);
         int var4 = Pack.littleEndianToInt(var1, 4);
         int var5 = Pack.littleEndianToInt(var1, 8);
         int var6 = Pack.littleEndianToInt(var1, 12);
         this.r0 = var3 & 67108863;
         this.r1 = (var3 >>> 26 | var4 << 6) & 67108611;
         this.r2 = (var4 >>> 20 | var5 << 12) & 67092735;
         this.r3 = (var5 >>> 14 | var6 << 18) & 66076671;
         this.r4 = var6 >>> 8 & 1048575;
         this.s1 = this.r1 * 5;
         this.s2 = this.r2 * 5;
         this.s3 = this.r3 * 5;
         this.s4 = this.r4 * 5;
         byte[] var7;
         byte var8;
         if (this.cipher == null) {
            var7 = var1;
            var8 = 16;
         } else {
            var7 = new byte[16];
            var8 = 0;
            this.cipher.init(true, new KeyParameter(var1, 16, 16));
            this.cipher.processBlock(var2, 0, var7, 0);
         }

         this.k0 = Pack.littleEndianToInt(var7, var8 + 0);
         this.k1 = Pack.littleEndianToInt(var7, var8 + 4);
         this.k2 = Pack.littleEndianToInt(var7, var8 + 8);
         this.k3 = Pack.littleEndianToInt(var7, var8 + 12);
      } else {
         throw new IllegalArgumentException("Poly1305 requires a 128 bit IV.");
      }
   }

   @Override
   public String getAlgorithmName() {
      return this.cipher == null ? "Poly1305" : "Poly1305-" + this.cipher.getAlgorithmName();
   }

   @Override
   public int getMacSize() {
      return 16;
   }

   @Override
   public void update(byte var1) throws IllegalStateException {
      this.singleByte[0] = var1;
      this.update(this.singleByte, 0, 1);
   }

   @Override
   public void update(byte[] var1, int var2, int var3) throws DataLengthException, IllegalStateException {
      int var4 = 0;

      while (var3 > var4) {
         if (this.currentBlockOffset == 16) {
            this.processBlock();
            this.currentBlockOffset = 0;
         }

         int var5 = Math.min(var3 - var4, 16 - this.currentBlockOffset);
         System.arraycopy(var1, var4 + var2, this.currentBlock, this.currentBlockOffset, var5);
         var4 += var5;
         this.currentBlockOffset += var5;
      }
   }

   private void processBlock() {
      if (this.currentBlockOffset < 16) {
         this.currentBlock[this.currentBlockOffset] = 1;

         for (int var1 = this.currentBlockOffset + 1; var1 < 16; var1++) {
            this.currentBlock[var1] = 0;
         }
      }

      long var19 = 4294967295L & Pack.littleEndianToInt(this.currentBlock, 0);
      long var3 = 4294967295L & Pack.littleEndianToInt(this.currentBlock, 4);
      long var5 = 4294967295L & Pack.littleEndianToInt(this.currentBlock, 8);
      long var7 = 4294967295L & Pack.littleEndianToInt(this.currentBlock, 12);
      this.h0 = (int)(this.h0 + (var19 & 67108863L));
      this.h1 = (int)(this.h1 + ((var3 << 32 | var19) >>> 26 & 67108863L));
      this.h2 = (int)(this.h2 + ((var5 << 32 | var3) >>> 20 & 67108863L));
      this.h3 = (int)(this.h3 + ((var7 << 32 | var5) >>> 14 & 67108863L));
      this.h4 = (int)(this.h4 + (var7 >>> 8));
      if (this.currentBlockOffset == 16) {
         this.h4 += 16777216;
      }

      long var9 = mul32x32_64(this.h0, this.r0)
         + mul32x32_64(this.h1, this.s4)
         + mul32x32_64(this.h2, this.s3)
         + mul32x32_64(this.h3, this.s2)
         + mul32x32_64(this.h4, this.s1);
      long var11 = mul32x32_64(this.h0, this.r1)
         + mul32x32_64(this.h1, this.r0)
         + mul32x32_64(this.h2, this.s4)
         + mul32x32_64(this.h3, this.s3)
         + mul32x32_64(this.h4, this.s2);
      long var13 = mul32x32_64(this.h0, this.r2)
         + mul32x32_64(this.h1, this.r1)
         + mul32x32_64(this.h2, this.r0)
         + mul32x32_64(this.h3, this.s4)
         + mul32x32_64(this.h4, this.s3);
      long var15 = mul32x32_64(this.h0, this.r3)
         + mul32x32_64(this.h1, this.r2)
         + mul32x32_64(this.h2, this.r1)
         + mul32x32_64(this.h3, this.r0)
         + mul32x32_64(this.h4, this.s4);
      long var17 = mul32x32_64(this.h0, this.r4)
         + mul32x32_64(this.h1, this.r3)
         + mul32x32_64(this.h2, this.r2)
         + mul32x32_64(this.h3, this.r1)
         + mul32x32_64(this.h4, this.r0);
      this.h0 = (int)var9 & 67108863;
      var11 += var9 >>> 26;
      this.h1 = (int)var11 & 67108863;
      var13 += var11 >>> 26;
      this.h2 = (int)var13 & 67108863;
      var15 += var13 >>> 26;
      this.h3 = (int)var15 & 67108863;
      var17 += var15 >>> 26;
      this.h4 = (int)var17 & 67108863;
      this.h0 += (int)(var17 >>> 26) * 5;
      this.h1 = this.h1 + (this.h0 >>> 26);
      this.h0 &= 67108863;
   }

   @Override
   public int doFinal(byte[] var1, int var2) throws DataLengthException, IllegalStateException {
      if (var2 + 16 > var1.length) {
         throw new OutputLengthException("Output buffer is too short.");
      } else {
         if (this.currentBlockOffset > 0) {
            this.processBlock();
         }

         this.h1 = this.h1 + (this.h0 >>> 26);
         this.h0 &= 67108863;
         this.h2 = this.h2 + (this.h1 >>> 26);
         this.h1 &= 67108863;
         this.h3 = this.h3 + (this.h2 >>> 26);
         this.h2 &= 67108863;
         this.h4 = this.h4 + (this.h3 >>> 26);
         this.h3 &= 67108863;
         this.h0 = this.h0 + (this.h4 >>> 26) * 5;
         this.h4 &= 67108863;
         this.h1 = this.h1 + (this.h0 >>> 26);
         this.h0 &= 67108863;
         int var3 = this.h0 + 5;
         int var8 = var3 >>> 26;
         var3 &= 67108863;
         int var4 = this.h1 + var8;
         var8 = var4 >>> 26;
         var4 &= 67108863;
         int var5 = this.h2 + var8;
         var8 = var5 >>> 26;
         var5 &= 67108863;
         int var6 = this.h3 + var8;
         var8 = var6 >>> 26;
         var6 &= 67108863;
         int var7 = this.h4 + var8 - 67108864;
         var8 = (var7 >>> 31) - 1;
         int var9 = ~var8;
         this.h0 = this.h0 & var9 | var3 & var8;
         this.h1 = this.h1 & var9 | var4 & var8;
         this.h2 = this.h2 & var9 | var5 & var8;
         this.h3 = this.h3 & var9 | var6 & var8;
         this.h4 = this.h4 & var9 | var7 & var8;
         long var10 = ((this.h0 | this.h1 << 26) & 4294967295L) + (4294967295L & this.k0);
         long var12 = ((this.h1 >>> 6 | this.h2 << 20) & 4294967295L) + (4294967295L & this.k1);
         long var14 = ((this.h2 >>> 12 | this.h3 << 14) & 4294967295L) + (4294967295L & this.k2);
         long var16 = ((this.h3 >>> 18 | this.h4 << 8) & 4294967295L) + (4294967295L & this.k3);
         Pack.intToLittleEndian((int)var10, var1, var2);
         var12 += var10 >>> 32;
         Pack.intToLittleEndian((int)var12, var1, var2 + 4);
         var14 += var12 >>> 32;
         Pack.intToLittleEndian((int)var14, var1, var2 + 8);
         var16 += var14 >>> 32;
         Pack.intToLittleEndian((int)var16, var1, var2 + 12);
         this.reset();
         return 16;
      }
   }

   @Override
   public void reset() {
      this.currentBlockOffset = 0;
      this.h0 = this.h1 = this.h2 = this.h3 = this.h4 = 0;
   }

   private static final long mul32x32_64(int var0, int var1) {
      return (var0 & 4294967295L) * var1;
   }
}
