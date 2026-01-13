package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.params.KeyParameter;

public class Shacal2Engine implements BlockCipher {
   private static final int[] K = new int[]{
      1116352408,
      1899447441,
      -1245643825,
      -373957723,
      961987163,
      1508970993,
      -1841331548,
      -1424204075,
      -670586216,
      310598401,
      607225278,
      1426881987,
      1925078388,
      -2132889090,
      -1680079193,
      -1046744716,
      -459576895,
      -272742522,
      264347078,
      604807628,
      770255983,
      1249150122,
      1555081692,
      1996064986,
      -1740746414,
      -1473132947,
      -1341970488,
      -1084653625,
      -958395405,
      -710438585,
      113926993,
      338241895,
      666307205,
      773529912,
      1294757372,
      1396182291,
      1695183700,
      1986661051,
      -2117940946,
      -1838011259,
      -1564481375,
      -1474664885,
      -1035236496,
      -949202525,
      -778901479,
      -694614492,
      -200395387,
      275423344,
      430227734,
      506948616,
      659060556,
      883997877,
      958139571,
      1322822218,
      1537002063,
      1747873779,
      1955562222,
      2024104815,
      -2067236844,
      -1933114872,
      -1866530822,
      -1538233109,
      -1090935817,
      -965641998
   };
   private static final int BLOCK_SIZE = 32;
   private boolean forEncryption = false;
   private static final int ROUNDS = 64;
   private int[] workingKey = null;

   @Override
   public void reset() {
   }

   @Override
   public String getAlgorithmName() {
      return "Shacal2";
   }

   @Override
   public int getBlockSize() {
      return 32;
   }

   @Override
   public void init(boolean var1, CipherParameters var2) throws IllegalArgumentException {
      if (!(var2 instanceof KeyParameter)) {
         throw new IllegalArgumentException("only simple KeyParameter expected.");
      } else {
         this.forEncryption = var1;
         this.workingKey = new int[64];
         byte[] var3 = ((KeyParameter)var2).getKey();
         this.setKey(var3);
         int var4 = var3.length * 8;
         CryptoServicesRegistrar.checkConstraints(
            new DefaultServiceProperties(this.getAlgorithmName(), var4 < 256 ? var4 : 256, var2, Utils.getPurpose(this.forEncryption))
         );
      }
   }

   public void setKey(byte[] var1) {
      if (var1.length != 0 && var1.length <= 64 && var1.length >= 16 && var1.length % 8 == 0) {
         this.bytes2ints(var1, this.workingKey, 0, 0);

         for (int var2 = 16; var2 < 64; var2++) {
            this.workingKey[var2] = (
                  (this.workingKey[var2 - 2] >>> 17 | this.workingKey[var2 - 2] << -17)
                     ^ (this.workingKey[var2 - 2] >>> 19 | this.workingKey[var2 - 2] << -19)
                     ^ this.workingKey[var2 - 2] >>> 10
               )
               + this.workingKey[var2 - 7]
               + (
                  (this.workingKey[var2 - 15] >>> 7 | this.workingKey[var2 - 15] << -7)
                     ^ (this.workingKey[var2 - 15] >>> 18 | this.workingKey[var2 - 15] << -18)
                     ^ this.workingKey[var2 - 15] >>> 3
               )
               + this.workingKey[var2 - 16];
         }
      } else {
         throw new IllegalArgumentException("Shacal2-key must be 16 - 64 bytes and multiple of 8");
      }
   }

   private void encryptBlock(byte[] var1, int var2, byte[] var3, int var4) {
      int[] var5 = new int[8];
      this.byteBlockToInts(var1, var5, var2, 0);

      for (int var6 = 0; var6 < 64; var6++) {
         int var7 = ((var5[4] >>> 6 | var5[4] << -6) ^ (var5[4] >>> 11 | var5[4] << -11) ^ (var5[4] >>> 25 | var5[4] << -25))
            + (var5[4] & var5[5] ^ ~var5[4] & var5[6])
            + var5[7]
            + K[var6]
            + this.workingKey[var6];
         var5[7] = var5[6];
         var5[6] = var5[5];
         var5[5] = var5[4];
         var5[4] = var5[3] + var7;
         var5[3] = var5[2];
         var5[2] = var5[1];
         var5[1] = var5[0];
         var5[0] = var7
            + ((var5[0] >>> 2 | var5[0] << -2) ^ (var5[0] >>> 13 | var5[0] << -13) ^ (var5[0] >>> 22 | var5[0] << -22))
            + (var5[0] & var5[2] ^ var5[0] & var5[3] ^ var5[2] & var5[3]);
      }

      this.ints2bytes(var5, var3, var4);
   }

   private void decryptBlock(byte[] var1, int var2, byte[] var3, int var4) {
      int[] var5 = new int[8];
      this.byteBlockToInts(var1, var5, var2, 0);

      for (int var6 = 63; var6 > -1; var6--) {
         int var7 = var5[0]
            - ((var5[1] >>> 2 | var5[1] << -2) ^ (var5[1] >>> 13 | var5[1] << -13) ^ (var5[1] >>> 22 | var5[1] << -22))
            - (var5[1] & var5[2] ^ var5[1] & var5[3] ^ var5[2] & var5[3]);
         var5[0] = var5[1];
         var5[1] = var5[2];
         var5[2] = var5[3];
         var5[3] = var5[4] - var7;
         var5[4] = var5[5];
         var5[5] = var5[6];
         var5[6] = var5[7];
         var5[7] = var7
            - K[var6]
            - this.workingKey[var6]
            - ((var5[4] >>> 6 | var5[4] << -6) ^ (var5[4] >>> 11 | var5[4] << -11) ^ (var5[4] >>> 25 | var5[4] << -25))
            - (var5[4] & var5[5] ^ ~var5[4] & var5[6]);
      }

      this.ints2bytes(var5, var3, var4);
   }

   @Override
   public int processBlock(byte[] var1, int var2, byte[] var3, int var4) throws DataLengthException, IllegalStateException {
      if (this.workingKey == null) {
         throw new IllegalStateException("Shacal2 not initialised");
      } else if (var2 + 32 > var1.length) {
         throw new DataLengthException("input buffer too short");
      } else if (var4 + 32 > var3.length) {
         throw new OutputLengthException("output buffer too short");
      } else {
         if (this.forEncryption) {
            this.encryptBlock(var1, var2, var3, var4);
         } else {
            this.decryptBlock(var1, var2, var3, var4);
         }

         return 32;
      }
   }

   private void byteBlockToInts(byte[] var1, int[] var2, int var3, int var4) {
      for (int var5 = var4; var5 < 8; var5++) {
         var2[var5] = (var1[var3++] & 255) << 24 | (var1[var3++] & 255) << 16 | (var1[var3++] & 255) << 8 | var1[var3++] & 255;
      }
   }

   private void bytes2ints(byte[] var1, int[] var2, int var3, int var4) {
      for (int var5 = var4; var5 < var1.length / 4; var5++) {
         var2[var5] = (var1[var3++] & 255) << 24 | (var1[var3++] & 255) << 16 | (var1[var3++] & 255) << 8 | var1[var3++] & 255;
      }
   }

   private void ints2bytes(int[] var1, byte[] var2, int var3) {
      for (int var4 = 0; var4 < var1.length; var4++) {
         var2[var3++] = (byte)(var1[var4] >>> 24);
         var2[var3++] = (byte)(var1[var4] >>> 16);
         var2[var3++] = (byte)(var1[var4] >>> 8);
         var2[var3++] = (byte)var1[var4];
      }
   }
}
