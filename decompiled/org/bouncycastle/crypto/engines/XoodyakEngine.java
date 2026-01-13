package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.digests.XoodyakDigest;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Bytes;
import org.bouncycastle.util.Integers;
import org.bouncycastle.util.Pack;

public class XoodyakEngine extends AEADBaseEngine {
   private final byte[] state;
   private int phase;
   private int mode;
   private static final int f_bPrime_1 = 47;
   private byte[] K;
   private byte[] iv;
   private static final int PhaseUp = 2;
   private static final int PhaseDown = 1;
   private static final int[] RC = new int[]{88, 56, 960, 208, 288, 20, 96, 44, 896, 240, 416, 18};
   private boolean encrypted;
   private byte aadcd;
   private static final int ModeKeyed = 0;
   private static final int ModeHash = 1;

   public XoodyakEngine() {
      this.algorithmName = "Xoodyak AEAD";
      this.KEY_SIZE = this.IV_SIZE = this.MAC_SIZE = 16;
      this.BlockSize = 24;
      this.AADBufferSize = 44;
      this.state = new byte[48];
      this.setInnerMembers(AEADBaseEngine.ProcessingBufferType.Immediate, AEADBaseEngine.AADOperatorType.Default, AEADBaseEngine.DataOperatorType.Counter);
   }

   @Override
   protected void init(byte[] var1, byte[] var2) throws IllegalArgumentException {
      this.K = var1;
      this.iv = var2;
   }

   @Override
   protected void processBufferAAD(byte[] var1, int var2) {
      this.AbsorbAny(var1, var2, this.AADBufferSize, this.aadcd);
      this.aadcd = 0;
   }

   @Override
   protected void processFinalAAD() {
      this.AbsorbAny(this.m_aad, 0, this.m_aadPos, this.aadcd);
   }

   @Override
   protected void finishAAD(AEADBaseEngine.State var1, boolean var2) {
      this.finishAAD3(var1, var2);
   }

   @Override
   protected void processBufferEncrypt(byte[] var1, int var2, byte[] var3, int var4) {
      up(this.mode, this.state, this.encrypted ? 0 : 128);
      Bytes.xor(this.BlockSize, this.state, var1, var2, var3, var4);
      down(this.mode, this.state, var1, var2, this.BlockSize, 0);
      this.phase = 1;
      this.encrypted = true;
   }

   @Override
   protected void processBufferDecrypt(byte[] var1, int var2, byte[] var3, int var4) {
      up(this.mode, this.state, this.encrypted ? 0 : 128);
      Bytes.xor(this.BlockSize, this.state, var1, var2, var3, var4);
      down(this.mode, this.state, var3, var4, this.BlockSize, 0);
      this.phase = 1;
      this.encrypted = true;
   }

   @Override
   protected void processFinalBlock(byte[] var1, int var2) {
      if (this.m_bufPos != 0 || !this.encrypted) {
         up(this.mode, this.state, this.encrypted ? 0 : 128);
         Bytes.xor(this.m_bufPos, this.state, this.m_buf, 0, var1, var2);
         if (this.forEncryption) {
            down(this.mode, this.state, this.m_buf, 0, this.m_bufPos, 0);
         } else {
            down(this.mode, this.state, var1, var2, this.m_bufPos, 0);
         }

         this.phase = 1;
      }

      up(this.mode, this.state, 64);
      System.arraycopy(this.state, 0, this.mac, 0, this.MAC_SIZE);
      this.phase = 2;
   }

   @Override
   protected void reset(boolean var1) {
      super.reset(var1);
      Arrays.fill(this.state, (byte)0);
      this.encrypted = false;
      this.phase = 2;
      this.aadcd = 3;
      int var2 = this.K.length;
      int var3 = this.iv.length;
      byte[] var4 = new byte[this.AADBufferSize];
      this.mode = 0;
      System.arraycopy(this.K, 0, var4, 0, var2);
      System.arraycopy(this.iv, 0, var4, var2, var3);
      var4[var2 + var3] = (byte)var3;
      this.AbsorbAny(var4, 0, var2 + var3 + 1, 2);
   }

   private void AbsorbAny(byte[] var1, int var2, int var3, int var4) {
      if (this.phase != 2) {
         up(this.mode, this.state, 0);
      }

      do {
         int var5 = Math.min(var3, this.AADBufferSize);
         down(this.mode, this.state, var1, var2, var5, var4);
         this.phase = 1;
         var4 = 0;
         var2 += var5;
         var3 -= var5;
      } while (var3 != 0);
   }

   public static void up(XoodyakDigest.Friend var0, int var1, byte[] var2, int var3) {
      if (null == var0) {
         throw new NullPointerException("This method is only for use by XoodyakDigest");
      } else {
         up(var1, var2, var3);
      }
   }

   private static void up(int var0, byte[] var1, int var2) {
      if (var0 != 1) {
         var1[47] = (byte)(var1[47] ^ var2);
      }

      int var3 = Pack.littleEndianToInt(var1, 0);
      int var4 = Pack.littleEndianToInt(var1, 4);
      int var5 = Pack.littleEndianToInt(var1, 8);
      int var6 = Pack.littleEndianToInt(var1, 12);
      int var7 = Pack.littleEndianToInt(var1, 16);
      int var8 = Pack.littleEndianToInt(var1, 20);
      int var9 = Pack.littleEndianToInt(var1, 24);
      int var10 = Pack.littleEndianToInt(var1, 28);
      int var11 = Pack.littleEndianToInt(var1, 32);
      int var12 = Pack.littleEndianToInt(var1, 36);
      int var13 = Pack.littleEndianToInt(var1, 40);
      int var14 = Pack.littleEndianToInt(var1, 44);

      for (int var15 = 0; var15 < 12; var15++) {
         int var16 = var3 ^ var7 ^ var11;
         int var17 = var4 ^ var8 ^ var12;
         int var18 = var5 ^ var9 ^ var13;
         int var19 = var6 ^ var10 ^ var14;
         int var20 = Integers.rotateLeft(var19, 5) ^ Integers.rotateLeft(var19, 14);
         int var21 = Integers.rotateLeft(var16, 5) ^ Integers.rotateLeft(var16, 14);
         int var22 = Integers.rotateLeft(var17, 5) ^ Integers.rotateLeft(var17, 14);
         int var23 = Integers.rotateLeft(var18, 5) ^ Integers.rotateLeft(var18, 14);
         var3 ^= var20;
         var7 ^= var20;
         var11 ^= var20;
         var4 ^= var21;
         var8 ^= var21;
         var12 ^= var21;
         var5 ^= var22;
         var9 ^= var22;
         var13 ^= var22;
         var6 ^= var23;
         var10 ^= var23;
         var14 ^= var23;
         int var32 = Integers.rotateLeft(var11, 11);
         int var33 = Integers.rotateLeft(var12, 11);
         int var34 = Integers.rotateLeft(var13, 11);
         int var35 = Integers.rotateLeft(var14, 11);
         int var24 = var3 ^ RC[var15];
         var3 = var24 ^ ~var10 & var32;
         var4 ^= ~var7 & var33;
         var5 ^= ~var8 & var34;
         var6 ^= ~var9 & var35;
         int var41 = var10 ^ ~var32 & var24;
         int var43 = var7 ^ ~var33 & var4;
         int var45 = var8 ^ ~var34 & var5;
         int var47 = var9 ^ ~var35 & var6;
         var32 ^= ~var24 & var10;
         var33 ^= ~var4 & var7;
         var34 ^= ~var5 & var8;
         var35 ^= ~var6 & var9;
         var7 = Integers.rotateLeft(var41, 1);
         var8 = Integers.rotateLeft(var43, 1);
         var9 = Integers.rotateLeft(var45, 1);
         var10 = Integers.rotateLeft(var47, 1);
         var11 = Integers.rotateLeft(var34, 8);
         var12 = Integers.rotateLeft(var35, 8);
         var13 = Integers.rotateLeft(var32, 8);
         var14 = Integers.rotateLeft(var33, 8);
      }

      Pack.intToLittleEndian(var3, var1, 0);
      Pack.intToLittleEndian(var4, var1, 4);
      Pack.intToLittleEndian(var5, var1, 8);
      Pack.intToLittleEndian(var6, var1, 12);
      Pack.intToLittleEndian(var7, var1, 16);
      Pack.intToLittleEndian(var8, var1, 20);
      Pack.intToLittleEndian(var9, var1, 24);
      Pack.intToLittleEndian(var10, var1, 28);
      Pack.intToLittleEndian(var11, var1, 32);
      Pack.intToLittleEndian(var12, var1, 36);
      Pack.intToLittleEndian(var13, var1, 40);
      Pack.intToLittleEndian(var14, var1, 44);
   }

   public static void down(XoodyakDigest.Friend var0, int var1, byte[] var2, byte[] var3, int var4, int var5, int var6) {
      if (null == var0) {
         throw new NullPointerException("This method is only for use by XoodyakDigest");
      } else {
         down(var1, var2, var3, var4, var5, var6);
      }
   }

   private static void down(int var0, byte[] var1, byte[] var2, int var3, int var4, int var5) {
      Bytes.xorTo(var4, var2, var3, var1);
      var1[var4] = (byte)(var1[var4] ^ 1);
      var1[47] = (byte)(var1[47] ^ (var0 == 1 ? var5 & 1 : var5));
   }
}
