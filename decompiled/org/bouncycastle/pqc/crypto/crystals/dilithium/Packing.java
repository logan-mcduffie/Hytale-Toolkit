package org.bouncycastle.pqc.crypto.crystals.dilithium;

import org.bouncycastle.util.Arrays;

class Packing {
   static byte[] packPublicKey(PolyVecK var0, DilithiumEngine var1) {
      byte[] var2 = new byte[var1.getCryptoPublicKeyBytes() - 32];

      for (int var3 = 0; var3 < var1.getDilithiumK(); var3++) {
         System.arraycopy(var0.getVectorIndex(var3).polyt1Pack(), 0, var2, var3 * 320, 320);
      }

      return var2;
   }

   static PolyVecK unpackPublicKey(PolyVecK var0, byte[] var1, DilithiumEngine var2) {
      for (int var3 = 0; var3 < var2.getDilithiumK(); var3++) {
         var0.getVectorIndex(var3).polyt1Unpack(Arrays.copyOfRange(var1, var3 * 320, (var3 + 1) * 320));
      }

      return var0;
   }

   static byte[][] packSecretKey(byte[] var0, byte[] var1, byte[] var2, PolyVecK var3, PolyVecL var4, PolyVecK var5, DilithiumEngine var6) {
      byte[][] var7 = new byte[][]{var0, var2, var1, new byte[var6.getDilithiumL() * var6.getDilithiumPolyEtaPackedBytes()], null, null};

      for (int var8 = 0; var8 < var6.getDilithiumL(); var8++) {
         var4.getVectorIndex(var8).polyEtaPack(var7[3], var8 * var6.getDilithiumPolyEtaPackedBytes());
      }

      var7[4] = new byte[var6.getDilithiumK() * var6.getDilithiumPolyEtaPackedBytes()];

      for (int var9 = 0; var9 < var6.getDilithiumK(); var9++) {
         var5.getVectorIndex(var9).polyEtaPack(var7[4], var9 * var6.getDilithiumPolyEtaPackedBytes());
      }

      var7[5] = new byte[var6.getDilithiumK() * 416];

      for (int var10 = 0; var10 < var6.getDilithiumK(); var10++) {
         var3.getVectorIndex(var10).polyt0Pack(var7[5], var10 * 416);
      }

      return var7;
   }

   static void unpackSecretKey(PolyVecK var0, PolyVecL var1, PolyVecK var2, byte[] var3, byte[] var4, byte[] var5, DilithiumEngine var6) {
      for (int var7 = 0; var7 < var6.getDilithiumL(); var7++) {
         var1.getVectorIndex(var7).polyEtaUnpack(var4, var7 * var6.getDilithiumPolyEtaPackedBytes());
      }

      for (int var8 = 0; var8 < var6.getDilithiumK(); var8++) {
         var2.getVectorIndex(var8).polyEtaUnpack(var5, var8 * var6.getDilithiumPolyEtaPackedBytes());
      }

      for (int var9 = 0; var9 < var6.getDilithiumK(); var9++) {
         var0.getVectorIndex(var9).polyt0Unpack(var3, var9 * 416);
      }
   }

   static byte[] packSignature(byte[] var0, PolyVecL var1, PolyVecK var2, DilithiumEngine var3) {
      int var7 = 0;
      byte[] var8 = new byte[var3.getCryptoBytes()];
      System.arraycopy(var0, 0, var8, 0, var3.getDilithiumCTilde());
      var7 += var3.getDilithiumCTilde();

      for (int var4 = 0; var4 < var3.getDilithiumL(); var4++) {
         System.arraycopy(var1.getVectorIndex(var4).zPack(), 0, var8, var7 + var4 * var3.getDilithiumPolyZPackedBytes(), var3.getDilithiumPolyZPackedBytes());
      }

      var7 += var3.getDilithiumL() * var3.getDilithiumPolyZPackedBytes();

      for (int var9 = 0; var9 < var3.getDilithiumOmega() + var3.getDilithiumK(); var9++) {
         var8[var7 + var9] = 0;
      }

      int var6 = 0;

      for (int var10 = 0; var10 < var3.getDilithiumK(); var10++) {
         for (int var5 = 0; var5 < 256; var5++) {
            if (var2.getVectorIndex(var10).getCoeffIndex(var5) != 0) {
               var8[var7 + var6++] = (byte)var5;
            }
         }

         var8[var7 + var3.getDilithiumOmega() + var10] = (byte)var6;
      }

      return var8;
   }

   static boolean unpackSignature(PolyVecL var0, PolyVecK var1, byte[] var2, DilithiumEngine var3) {
      int var7 = var3.getDilithiumCTilde();

      for (int var4 = 0; var4 < var3.getDilithiumL(); var4++) {
         var0.getVectorIndex(var4)
            .zUnpack(Arrays.copyOfRange(var2, var7 + var4 * var3.getDilithiumPolyZPackedBytes(), var7 + (var4 + 1) * var3.getDilithiumPolyZPackedBytes()));
      }

      var7 += var3.getDilithiumL() * var3.getDilithiumPolyZPackedBytes();
      byte var6 = 0;

      for (int var8 = 0; var8 < var3.getDilithiumK(); var8++) {
         for (int var5 = 0; var5 < 256; var5++) {
            var1.getVectorIndex(var8).setCoeffIndex(var5, 0);
         }

         if ((var2[var7 + var3.getDilithiumOmega() + var8] & 255) < var6 || (var2[var7 + var3.getDilithiumOmega() + var8] & 255) > var3.getDilithiumOmega()) {
            return false;
         }

         for (int var9 = var6; var9 < (var2[var7 + var3.getDilithiumOmega() + var8] & 255); var9++) {
            if (var9 > var6 && (var2[var7 + var9] & 255) <= (var2[var7 + var9 - 1] & 255)) {
               return false;
            }

            var1.getVectorIndex(var8).setCoeffIndex(var2[var7 + var9] & 255, 1);
         }

         var6 = var2[var7 + var3.getDilithiumOmega() + var8];
      }

      for (int var10 = var6; var10 < var3.getDilithiumOmega(); var10++) {
         if ((var2[var7 + var10] & 255) != 0) {
            return false;
         }
      }

      return true;
   }
}
