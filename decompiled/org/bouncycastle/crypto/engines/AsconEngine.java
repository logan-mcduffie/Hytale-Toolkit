package org.bouncycastle.crypto.engines;

import org.bouncycastle.util.Pack;

/** @deprecated */
public class AsconEngine extends AsconBaseEngine {
   private final AsconEngine.AsconParameters asconParameters;
   private long K2;

   public AsconEngine(AsconEngine.AsconParameters var1) {
      this.asconParameters = var1;
      this.IV_SIZE = this.MAC_SIZE = 16;
      switch (var1) {
         case ascon80pq:
            this.KEY_SIZE = 20;
            this.BlockSize = 8;
            this.ASCON_IV = -6899501409222262784L;
            this.algorithmName = "Ascon-80pq AEAD";
            break;
         case ascon128a:
            this.KEY_SIZE = 16;
            this.BlockSize = 16;
            this.ASCON_IV = -9187330011336540160L;
            this.algorithmName = "Ascon-128a AEAD";
            break;
         case ascon128:
            this.KEY_SIZE = 16;
            this.BlockSize = 8;
            this.ASCON_IV = -9205344418435956736L;
            this.algorithmName = "Ascon-128 AEAD";
            break;
         default:
            throw new IllegalArgumentException("invalid parameter setting for ASCON AEAD");
      }

      this.nr = this.BlockSize == 8 ? 6 : 8;
      this.AADBufferSize = this.BlockSize;
      this.dsep = 1L;
      this.setInnerMembers(AEADBaseEngine.ProcessingBufferType.Immediate, AEADBaseEngine.AADOperatorType.Default, AEADBaseEngine.DataOperatorType.Default);
   }

   @Override
   protected long pad(int var1) {
      return 128L << 56 - (var1 << 3);
   }

   @Override
   protected long loadBytes(byte[] var1, int var2) {
      return Pack.bigEndianToLong(var1, var2);
   }

   @Override
   protected void setBytes(long var1, byte[] var3, int var4) {
      Pack.longToBigEndian(var1, var3, var4);
   }

   @Override
   protected void ascon_aeadinit() {
      this.p.set(this.ASCON_IV, this.K1, this.K2, this.N0, this.N1);
      if (this.KEY_SIZE == 20) {
         this.p.x0 = this.p.x0 ^ this.K0;
      }

      this.p.p(12);
      if (this.KEY_SIZE == 20) {
         this.p.x2 = this.p.x2 ^ this.K0;
      }

      this.p.x3 = this.p.x3 ^ this.K1;
      this.p.x4 = this.p.x4 ^ this.K2;
   }

   @Override
   protected void processFinalAAD() {
      this.m_aad[this.m_aadPos] = -128;
      if (this.m_aadPos >= 8) {
         this.p.x0 = this.p.x0 ^ Pack.bigEndianToLong(this.m_aad, 0);
         this.p.x1 = this.p.x1 ^ Pack.bigEndianToLong(this.m_aad, 8) & -1L << 56 - (this.m_aadPos - 8 << 3);
      } else {
         this.p.x0 = this.p.x0 ^ Pack.bigEndianToLong(this.m_aad, 0) & -1L << 56 - (this.m_aadPos << 3);
      }
   }

   @Override
   protected void processFinalDecrypt(byte[] var1, int var2, byte[] var3, int var4) {
      if (var2 >= 8) {
         long var5 = Pack.bigEndianToLong(var1, 0);
         this.p.x0 ^= var5;
         Pack.longToBigEndian(this.p.x0, var3, var4);
         this.p.x0 = var5;
         var4 += 8;
         var2 -= 8;
         this.p.x1 = this.p.x1 ^ this.pad(var2);
         if (var2 != 0) {
            long var7 = Pack.littleEndianToLong_High(var1, 8, var2);
            this.p.x1 ^= var7;
            Pack.longToLittleEndian_High(this.p.x1, var3, var4, var2);
            this.p.x1 &= -1L >>> (var2 << 3);
            this.p.x1 ^= var7;
         }
      } else {
         this.p.x0 = this.p.x0 ^ this.pad(var2);
         if (var2 != 0) {
            long var11 = Pack.littleEndianToLong_High(var1, 0, var2);
            this.p.x0 ^= var11;
            Pack.longToLittleEndian_High(this.p.x0, var3, var4, var2);
            this.p.x0 &= -1L >>> (var2 << 3);
            this.p.x0 ^= var11;
         }
      }

      this.finishData(AEADBaseEngine.State.DecFinal);
   }

   @Override
   protected void processFinalEncrypt(byte[] var1, int var2, byte[] var3, int var4) {
      if (var2 >= 8) {
         this.p.x0 = this.p.x0 ^ Pack.bigEndianToLong(var1, 0);
         Pack.longToBigEndian(this.p.x0, var3, var4);
         var4 += 8;
         var2 -= 8;
         this.p.x1 = this.p.x1 ^ this.pad(var2);
         if (var2 != 0) {
            this.p.x1 = this.p.x1 ^ Pack.littleEndianToLong_High(var1, 8, var2);
            Pack.longToLittleEndian_High(this.p.x1, var3, var4, var2);
         }
      } else {
         this.p.x0 = this.p.x0 ^ this.pad(var2);
         if (var2 != 0) {
            this.p.x0 = this.p.x0 ^ Pack.littleEndianToLong_High(var1, 0, var2);
            Pack.longToLittleEndian_High(this.p.x0, var3, var4, var2);
         }
      }

      this.finishData(AEADBaseEngine.State.EncFinal);
   }

   protected void finishData(AEADBaseEngine.State var1) {
      switch (this.asconParameters) {
         case ascon80pq:
            this.p.x1 = this.p.x1 ^ (this.K0 << 32 | this.K1 >> 32);
            this.p.x2 = this.p.x2 ^ (this.K1 << 32 | this.K2 >> 32);
            this.p.x3 = this.p.x3 ^ this.K2 << 32;
            break;
         case ascon128a:
            this.p.x2 = this.p.x2 ^ this.K1;
            this.p.x3 = this.p.x3 ^ this.K2;
            break;
         case ascon128:
            this.p.x1 = this.p.x1 ^ this.K1;
            this.p.x2 = this.p.x2 ^ this.K2;
            break;
         default:
            throw new IllegalStateException();
      }

      this.p.p(12);
      this.p.x3 = this.p.x3 ^ this.K1;
      this.p.x4 = this.p.x4 ^ this.K2;
      this.m_state = var1;
   }

   @Override
   protected void init(byte[] var1, byte[] var2) throws IllegalArgumentException {
      this.N0 = Pack.bigEndianToLong(var2, 0);
      this.N1 = Pack.bigEndianToLong(var2, 8);
      if (this.KEY_SIZE == 16) {
         this.K1 = Pack.bigEndianToLong(var1, 0);
         this.K2 = Pack.bigEndianToLong(var1, 8);
      } else {
         if (this.KEY_SIZE != 20) {
            throw new IllegalStateException();
         }

         this.K0 = Pack.bigEndianToInt(var1, 0);
         this.K1 = Pack.bigEndianToLong(var1, 4);
         this.K2 = Pack.bigEndianToLong(var1, 12);
      }
   }

   @Override
   public String getAlgorithmVersion() {
      return "v1.2";
   }

   public static enum AsconParameters {
      ascon80pq,
      ascon128a,
      ascon128;
   }
}
