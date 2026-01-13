package org.bouncycastle.crypto.engines;

import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Pack;

public class AsconAEAD128 extends AsconBaseEngine {
   public AsconAEAD128() {
      this.KEY_SIZE = this.IV_SIZE = this.MAC_SIZE = this.AADBufferSize = this.BlockSize = 16;
      this.ASCON_IV = 17594342703105L;
      this.algorithmName = "Ascon-AEAD128";
      this.nr = 8;
      this.dsep = Long.MIN_VALUE;
      this.macSizeLowerBound = 4;
      this.setInnerMembers(AEADBaseEngine.ProcessingBufferType.Immediate, AEADBaseEngine.AADOperatorType.DataLimit, AEADBaseEngine.DataOperatorType.DataLimit);
      this.dataLimitCounter.init(54);
      this.decryptionFailureCounter = new AEADBaseEngine.DecryptionFailureCounter();
   }

   @Override
   protected long pad(int var1) {
      return 1L << (var1 << 3);
   }

   @Override
   protected long loadBytes(byte[] var1, int var2) {
      return Pack.littleEndianToLong(var1, var2);
   }

   @Override
   protected void setBytes(long var1, byte[] var3, int var4) {
      Pack.longToLittleEndian(var1, var3, var4);
   }

   @Override
   protected void ascon_aeadinit() {
      this.p.set(this.ASCON_IV, this.K0, this.K1, this.N0, this.N1);
      this.p.p(12);
      this.p.x3 = this.p.x3 ^ this.K0;
      this.p.x4 = this.p.x4 ^ this.K1;
   }

   @Override
   protected void processFinalAAD() {
      if (this.m_aadPos == this.BlockSize) {
         this.p.x0 = this.p.x0 ^ this.loadBytes(this.m_aad, 0);
         this.p.x1 = this.p.x1 ^ this.loadBytes(this.m_aad, 8);
         this.m_aadPos = this.m_aadPos - this.BlockSize;
         this.p.p(this.nr);
      }

      Arrays.fill(this.m_aad, this.m_aadPos, this.AADBufferSize, (byte)0);
      if (this.m_aadPos >= 8) {
         this.p.x0 = this.p.x0 ^ Pack.littleEndianToLong(this.m_aad, 0);
         this.p.x1 = this.p.x1 ^ Pack.littleEndianToLong(this.m_aad, 8) ^ this.pad(this.m_aadPos);
      } else {
         this.p.x0 = this.p.x0 ^ Pack.littleEndianToLong(this.m_aad, 0) ^ this.pad(this.m_aadPos);
      }
   }

   @Override
   protected void processFinalDecrypt(byte[] var1, int var2, byte[] var3, int var4) {
      if (var2 >= 8) {
         long var5 = Pack.littleEndianToLong(var1, 0);
         var2 -= 8;
         long var7 = Pack.littleEndianToLong(var1, 8, var2);
         Pack.longToLittleEndian(this.p.x0 ^ var5, var3, var4);
         Pack.longToLittleEndian(this.p.x1 ^ var7, var3, var4 + 8, var2);
         this.p.x0 = var5;
         this.p.x1 &= -(1L << (var2 << 3));
         this.p.x1 |= var7;
         this.p.x1 = this.p.x1 ^ this.pad(var2);
      } else {
         if (var2 != 0) {
            long var10 = Pack.littleEndianToLong(var1, 0, var2);
            Pack.longToLittleEndian(this.p.x0 ^ var10, var3, var4, var2);
            this.p.x0 &= -(1L << (var2 << 3));
            this.p.x0 |= var10;
         }

         this.p.x0 = this.p.x0 ^ this.pad(var2);
      }

      this.finishData(AEADBaseEngine.State.DecFinal);
   }

   @Override
   protected void processFinalEncrypt(byte[] var1, int var2, byte[] var3, int var4) {
      if (var2 >= 8) {
         this.p.x0 = this.p.x0 ^ Pack.littleEndianToLong(var1, 0);
         var2 -= 8;
         this.p.x1 = this.p.x1 ^ Pack.littleEndianToLong(var1, 8, var2);
         Pack.longToLittleEndian(this.p.x0, var3, var4);
         Pack.longToLittleEndian(this.p.x1, var3, var4 + 8);
         this.p.x1 = this.p.x1 ^ this.pad(var2);
      } else {
         if (var2 != 0) {
            this.p.x0 = this.p.x0 ^ Pack.littleEndianToLong(var1, 0, var2);
            Pack.longToLittleEndian(this.p.x0, var3, var4, var2);
         }

         this.p.x0 = this.p.x0 ^ this.pad(var2);
      }

      this.finishData(AEADBaseEngine.State.EncFinal);
   }

   private void finishData(AEADBaseEngine.State var1) {
      this.p.x2 = this.p.x2 ^ this.K0;
      this.p.x3 = this.p.x3 ^ this.K1;
      this.p.p(12);
      this.p.x3 = this.p.x3 ^ this.K0;
      this.p.x4 = this.p.x4 ^ this.K1;
      this.m_state = var1;
   }

   @Override
   protected void init(byte[] var1, byte[] var2) throws IllegalArgumentException {
      int var3 = (this.MAC_SIZE << 3) - 32;
      long var4 = Pack.littleEndianToLong(var1, 0);
      long var6 = Pack.littleEndianToLong(var1, 8);
      this.decryptionFailureCounter.init(var3);
      if (this.K0 != var4 || this.K1 != var6) {
         this.dataLimitCounter.reset();
         this.decryptionFailureCounter.reset();
         this.K0 = var4;
         this.K1 = var6;
      }

      this.N0 = Pack.littleEndianToLong(var2, 0);
      this.N1 = Pack.littleEndianToLong(var2, 8);
   }

   @Override
   public String getAlgorithmVersion() {
      return "v1.3";
   }
}
