package org.bouncycastle.crypto;

public abstract class StreamBlockCipher extends DefaultMultiBlockCipher implements StreamCipher {
   private final BlockCipher cipher;

   protected StreamBlockCipher(BlockCipher var1) {
      this.cipher = var1;
   }

   public BlockCipher getUnderlyingCipher() {
      return this.cipher;
   }

   @Override
   public final byte returnByte(byte var1) {
      return this.calculateByte(var1);
   }

   @Override
   public int processBytes(byte[] var1, int var2, int var3, byte[] var4, int var5) throws DataLengthException {
      if (var2 + var3 > var1.length) {
         throw new DataLengthException("input buffer too small");
      } else if (var5 + var3 > var4.length) {
         throw new OutputLengthException("output buffer too short");
      } else {
         int var6 = var2;
         int var7 = var2 + var3;
         int var8 = var5;

         while (var6 < var7) {
            var4[var8++] = this.calculateByte(var1[var6++]);
         }

         return var3;
      }
   }

   protected abstract byte calculateByte(byte var1);
}
