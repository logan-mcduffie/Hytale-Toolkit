package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.Wrapper;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.util.Arrays;

public class RFC3394WrapEngine implements Wrapper {
   private static final byte[] DEFAULT_IV = new byte[]{-90, -90, -90, -90, -90, -90, -90, -90};
   private final BlockCipher engine;
   private final boolean wrapCipherMode;
   private final byte[] iv = new byte[8];
   private KeyParameter param = null;
   private boolean forWrapping = true;

   public RFC3394WrapEngine(BlockCipher var1) {
      this(var1, false);
   }

   public RFC3394WrapEngine(BlockCipher var1, boolean var2) {
      this.engine = var1;
      this.wrapCipherMode = !var2;
   }

   @Override
   public void init(boolean var1, CipherParameters var2) {
      this.forWrapping = var1;
      if (var2 instanceof ParametersWithRandom) {
         var2 = ((ParametersWithRandom)var2).getParameters();
      }

      if (var2 instanceof KeyParameter) {
         this.param = (KeyParameter)var2;
         System.arraycopy(DEFAULT_IV, 0, this.iv, 0, 8);
      } else if (var2 instanceof ParametersWithIV) {
         ParametersWithIV var3 = (ParametersWithIV)var2;
         byte[] var4 = var3.getIV();
         if (var4.length != 8) {
            throw new IllegalArgumentException("IV not equal to 8");
         }

         this.param = (KeyParameter)var3.getParameters();
         System.arraycopy(var4, 0, this.iv, 0, 8);
      }
   }

   @Override
   public String getAlgorithmName() {
      return this.engine.getAlgorithmName();
   }

   @Override
   public byte[] wrap(byte[] var1, int var2, int var3) {
      if (!this.forWrapping) {
         throw new IllegalStateException("not set for wrapping");
      } else if (var3 < 8) {
         throw new DataLengthException("wrap data must be at least 8 bytes");
      } else {
         int var4 = var3 / 8;
         if (var4 * 8 != var3) {
            throw new DataLengthException("wrap data must be a multiple of 8 bytes");
         } else {
            this.engine.init(this.wrapCipherMode, this.param);
            byte[] var5 = new byte[var3 + this.iv.length];
            System.arraycopy(this.iv, 0, var5, 0, this.iv.length);
            System.arraycopy(var1, var2, var5, this.iv.length, var3);
            if (var4 == 1) {
               this.engine.processBlock(var5, 0, var5, 0);
            } else {
               byte[] var6 = new byte[8 + this.iv.length];

               for (int var7 = 0; var7 != 6; var7++) {
                  for (int var8 = 1; var8 <= var4; var8++) {
                     System.arraycopy(var5, 0, var6, 0, this.iv.length);
                     System.arraycopy(var5, 8 * var8, var6, this.iv.length, 8);
                     this.engine.processBlock(var6, 0, var6, 0);
                     int var9 = var4 * var7 + var8;

                     for (int var10 = 1; var9 != 0; var10++) {
                        byte var11 = (byte)var9;
                        var6[this.iv.length - var10] = (byte)(var6[this.iv.length - var10] ^ var11);
                        var9 >>>= 8;
                     }

                     System.arraycopy(var6, 0, var5, 0, 8);
                     System.arraycopy(var6, 8, var5, 8 * var8, 8);
                  }
               }
            }

            return var5;
         }
      }
   }

   @Override
   public byte[] unwrap(byte[] var1, int var2, int var3) throws InvalidCipherTextException {
      if (this.forWrapping) {
         throw new IllegalStateException("not set for unwrapping");
      } else if (var3 < 16) {
         throw new InvalidCipherTextException("unwrap data too short");
      } else {
         int var4 = var3 / 8;
         if (var4 * 8 != var3) {
            throw new InvalidCipherTextException("unwrap data must be a multiple of 8 bytes");
         } else {
            this.engine.init(!this.wrapCipherMode, this.param);
            byte[] var5 = new byte[var3 - this.iv.length];
            byte[] var6 = new byte[this.iv.length];
            byte[] var7 = new byte[8 + this.iv.length];
            if (--var4 == 1) {
               this.engine.processBlock(var1, var2, var7, 0);
               System.arraycopy(var7, 0, var6, 0, this.iv.length);
               System.arraycopy(var7, this.iv.length, var5, 0, 8);
            } else {
               System.arraycopy(var1, var2, var6, 0, this.iv.length);
               System.arraycopy(var1, var2 + this.iv.length, var5, 0, var3 - this.iv.length);

               for (int var8 = 5; var8 >= 0; var8--) {
                  for (int var9 = var4; var9 >= 1; var9--) {
                     System.arraycopy(var6, 0, var7, 0, this.iv.length);
                     System.arraycopy(var5, 8 * (var9 - 1), var7, this.iv.length, 8);
                     int var10 = var4 * var8 + var9;

                     for (int var11 = 1; var10 != 0; var11++) {
                        byte var12 = (byte)var10;
                        var7[this.iv.length - var11] = (byte)(var7[this.iv.length - var11] ^ var12);
                        var10 >>>= 8;
                     }

                     this.engine.processBlock(var7, 0, var7, 0);
                     System.arraycopy(var7, 0, var6, 0, 8);
                     System.arraycopy(var7, 8, var5, 8 * (var9 - 1), 8);
                  }
               }
            }

            if (var4 != 1) {
               if (!Arrays.constantTimeAreEqual(var6, this.iv)) {
                  throw new InvalidCipherTextException("checksum failed");
               }
            } else if (!Arrays.constantTimeAreEqual(var6, this.iv)) {
               System.arraycopy(var1, var2, var6, 0, this.iv.length);
               System.arraycopy(var1, var2 + this.iv.length, var5, 0, var3 - this.iv.length);

               for (int var14 = 5; var14 >= 0; var14--) {
                  System.arraycopy(var6, 0, var7, 0, this.iv.length);
                  System.arraycopy(var5, 0, var7, this.iv.length, 8);
                  int var15 = var4 * var14 + 1;

                  for (int var16 = 1; var15 != 0; var16++) {
                     byte var17 = (byte)var15;
                     var7[this.iv.length - var16] = (byte)(var7[this.iv.length - var16] ^ var17);
                     var15 >>>= 8;
                  }

                  this.engine.processBlock(var7, 0, var7, 0);
                  System.arraycopy(var7, 0, var6, 0, 8);
                  System.arraycopy(var7, 8, var5, 0, 8);
               }

               if (!Arrays.constantTimeAreEqual(var6, this.iv)) {
                  throw new InvalidCipherTextException("checksum failed");
               }
            }

            return var5;
         }
      }
   }
}
