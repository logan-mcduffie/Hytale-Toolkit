package org.bouncycastle.crypto.generators;

import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.KeyGenerationParameters;

public class Poly1305KeyGenerator extends CipherKeyGenerator {
   private static final byte R_MASK_LOW_2 = -4;
   private static final byte R_MASK_HIGH_4 = 15;

   @Override
   public void init(KeyGenerationParameters var1) {
      super.init(new KeyGenerationParameters(var1.getRandom(), 256));
   }

   @Override
   public byte[] generateKey() {
      byte[] var1 = super.generateKey();
      clamp(var1);
      return var1;
   }

   public static void clamp(byte[] var0) {
      if (var0.length != 32) {
         throw new IllegalArgumentException("Poly1305 key must be 256 bits.");
      } else {
         var0[3] = (byte)(var0[3] & 15);
         var0[7] = (byte)(var0[7] & 15);
         var0[11] = (byte)(var0[11] & 15);
         var0[15] = (byte)(var0[15] & 15);
         var0[4] &= -4;
         var0[8] &= -4;
         var0[12] &= -4;
      }
   }

   public static void checkKey(byte[] var0) {
      if (var0.length != 32) {
         throw new IllegalArgumentException("Poly1305 key must be 256 bits.");
      } else {
         checkMask(var0[3], (byte)15);
         checkMask(var0[7], (byte)15);
         checkMask(var0[11], (byte)15);
         checkMask(var0[15], (byte)15);
         checkMask(var0[4], (byte)-4);
         checkMask(var0[8], (byte)-4);
         checkMask(var0[12], (byte)-4);
      }
   }

   private static void checkMask(byte var0, byte var1) {
      if ((var0 & ~var1) != 0) {
         throw new IllegalArgumentException("Invalid format for r portion of Poly1305 key.");
      }
   }
}
