package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.Wrapper;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Pack;

public class RFC5649WrapEngine implements Wrapper {
   private static final byte[] DEFAULT_IV = new byte[]{-90, 89, 89, -90};
   private final BlockCipher engine;
   private final byte[] preIV = new byte[4];
   private KeyParameter param = null;
   private boolean forWrapping = true;

   public RFC5649WrapEngine(BlockCipher var1) {
      this.engine = var1;
   }

   @Override
   public void init(boolean var1, CipherParameters var2) {
      this.forWrapping = var1;
      if (var2 instanceof ParametersWithRandom) {
         var2 = ((ParametersWithRandom)var2).getParameters();
      }

      if (var2 instanceof KeyParameter) {
         this.param = (KeyParameter)var2;
         System.arraycopy(DEFAULT_IV, 0, this.preIV, 0, 4);
      } else if (var2 instanceof ParametersWithIV) {
         ParametersWithIV var3 = (ParametersWithIV)var2;
         byte[] var4 = var3.getIV();
         if (var4.length != 4) {
            throw new IllegalArgumentException("IV length not equal to 4");
         }

         this.param = (KeyParameter)var3.getParameters();
         System.arraycopy(var4, 0, this.preIV, 0, 4);
      }
   }

   @Override
   public String getAlgorithmName() {
      return this.engine.getAlgorithmName();
   }

   private byte[] padPlaintext(byte[] var1) {
      int var2 = var1.length;
      int var3 = (8 - var2 % 8) % 8;
      byte[] var4 = new byte[var2 + var3];
      System.arraycopy(var1, 0, var4, 0, var2);
      if (var3 != 0) {
         byte[] var5 = new byte[var3];
         System.arraycopy(var5, 0, var4, var2, var3);
      }

      return var4;
   }

   @Override
   public byte[] wrap(byte[] var1, int var2, int var3) {
      if (!this.forWrapping) {
         throw new IllegalStateException("not set for wrapping");
      } else {
         byte[] var4 = new byte[8];
         System.arraycopy(this.preIV, 0, var4, 0, 4);
         Pack.intToBigEndian(var3, var4, 4);
         byte[] var5 = new byte[var3];
         System.arraycopy(var1, var2, var5, 0, var3);
         byte[] var6 = this.padPlaintext(var5);
         if (var6.length != 8) {
            RFC3394WrapEngine var10 = new RFC3394WrapEngine(this.engine);
            ParametersWithIV var11 = new ParametersWithIV(this.param, var4);
            var10.init(true, var11);
            return var10.wrap(var6, 0, var6.length);
         } else {
            byte[] var7 = new byte[var6.length + var4.length];
            System.arraycopy(var4, 0, var7, 0, var4.length);
            System.arraycopy(var6, 0, var7, var4.length, var6.length);
            this.engine.init(true, this.param);
            int var8 = 0;

            for (int var9 = this.engine.getBlockSize(); var8 < var7.length; var8 += var9) {
               this.engine.processBlock(var7, var8, var7, var8);
            }

            return var7;
         }
      }
   }

   @Override
   public byte[] unwrap(byte[] var1, int var2, int var3) throws InvalidCipherTextException {
      if (this.forWrapping) {
         throw new IllegalStateException("not set for unwrapping");
      } else {
         int var4 = var3 / 8;
         if (var4 * 8 != var3) {
            throw new InvalidCipherTextException("unwrap data must be a multiple of 8 bytes");
         } else if (var4 <= 1) {
            throw new InvalidCipherTextException("unwrap data must be at least 16 bytes");
         } else {
            byte[] var5 = new byte[var3];
            System.arraycopy(var1, var2, var5, 0, var3);
            byte[] var6 = new byte[var3];
            byte[] var8 = new byte[8];
            byte[] var7;
            if (var4 == 2) {
               this.engine.init(false, this.param);
               int var9 = 0;

               for (int var10 = this.engine.getBlockSize(); var9 < var5.length; var9 += var10) {
                  this.engine.processBlock(var5, var9, var6, var9);
               }

               System.arraycopy(var6, 0, var8, 0, var8.length);
               var7 = new byte[var6.length - var8.length];
               System.arraycopy(var6, var8.length, var7, 0, var7.length);
            } else {
               var6 = this.rfc3394UnwrapNoIvCheck(var1, var2, var3, var8);
               var7 = var6;
            }

            byte[] var19 = new byte[4];
            System.arraycopy(var8, 0, var19, 0, 4);
            int var20 = Pack.bigEndianToInt(var8, 4);
            boolean var11 = Arrays.constantTimeAreEqual(var19, this.preIV);
            int var12 = var7.length;
            int var13 = var12 - 8;
            if (var20 <= var13) {
               var11 = false;
            }

            if (var20 > var12) {
               var11 = false;
            }

            int var14 = var12 - var20;
            if (var14 >= 8 || var14 < 0) {
               var11 = false;
               var14 = 4;
            }

            byte[] var15 = new byte[var14];
            byte[] var16 = new byte[var14];
            System.arraycopy(var7, var7.length - var14, var16, 0, var14);
            if (!Arrays.constantTimeAreEqual(var16, var15)) {
               var11 = false;
            }

            if (!var11) {
               throw new InvalidCipherTextException("checksum failed");
            } else {
               byte[] var17 = new byte[var20];
               System.arraycopy(var7, 0, var17, 0, var17.length);
               return var17;
            }
         }
      }
   }

   private byte[] rfc3394UnwrapNoIvCheck(byte[] var1, int var2, int var3, byte[] var4) {
      byte[] var5 = new byte[var3 - 8];
      byte[] var6 = new byte[16];
      System.arraycopy(var1, var2, var6, 0, 8);
      System.arraycopy(var1, var2 + 8, var5, 0, var3 - 8);
      this.engine.init(false, this.param);
      int var7 = var3 / 8;
      var7--;

      for (int var8 = 5; var8 >= 0; var8--) {
         for (int var9 = var7; var9 >= 1; var9--) {
            System.arraycopy(var5, 8 * (var9 - 1), var6, 8, 8);
            int var10 = var7 * var8 + var9;

            for (int var11 = 1; var10 != 0; var11++) {
               var6[8 - var11] = (byte)(var6[8 - var11] ^ (byte)var10);
               var10 >>>= 8;
            }

            this.engine.processBlock(var6, 0, var6, 0);
            System.arraycopy(var6, 8, var5, 8 * (var9 - 1), 8);
         }
      }

      System.arraycopy(var6, 0, var4, 0, 8);
      return var5;
   }
}
