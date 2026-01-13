package org.bouncycastle.crypto.fpe;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.params.FPEParameters;
import org.bouncycastle.util.Pack;

public abstract class FPEEngine {
   protected final BlockCipher baseCipher;
   protected boolean forEncryption;
   protected FPEParameters fpeParameters;

   protected FPEEngine(BlockCipher var1) {
      this.baseCipher = var1;
   }

   public int processBlock(byte[] var1, int var2, int var3, byte[] var4, int var5) {
      if (this.fpeParameters == null) {
         throw new IllegalStateException("FPE engine not initialized");
      } else if (var3 < 0) {
         throw new IllegalArgumentException("input length cannot be negative");
      } else if (var1 != null && var4 != null) {
         if (var1.length < var2 + var3) {
            throw new DataLengthException("input buffer too short");
         } else if (var4.length < var5 + var3) {
            throw new OutputLengthException("output buffer too short");
         } else {
            return this.forEncryption ? this.encryptBlock(var1, var2, var3, var4, var5) : this.decryptBlock(var1, var2, var3, var4, var5);
         }
      } else {
         throw new NullPointerException("buffer value is null");
      }
   }

   protected static short[] toShortArray(byte[] var0) {
      if ((var0.length & 1) != 0) {
         throw new IllegalArgumentException("data must be an even number of bytes for a wide radix");
      } else {
         short[] var1 = new short[var0.length / 2];

         for (int var2 = 0; var2 != var1.length; var2++) {
            var1[var2] = Pack.bigEndianToShort(var0, var2 * 2);
         }

         return var1;
      }
   }

   protected static byte[] toByteArray(short[] var0) {
      byte[] var1 = new byte[var0.length * 2];

      for (int var2 = 0; var2 != var0.length; var2++) {
         Pack.shortToBigEndian(var0[var2], var1, var2 * 2);
      }

      return var1;
   }

   public abstract void init(boolean var1, CipherParameters var2);

   public abstract String getAlgorithmName();

   protected abstract int encryptBlock(byte[] var1, int var2, int var3, byte[] var4, int var5);

   protected abstract int decryptBlock(byte[] var1, int var2, int var3, byte[] var4, int var5);
}
