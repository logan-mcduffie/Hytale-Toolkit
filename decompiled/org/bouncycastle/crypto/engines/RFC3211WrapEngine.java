package org.bouncycastle.crypto.engines;

import java.security.SecureRandom;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.Wrapper;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.util.Arrays;

public class RFC3211WrapEngine implements Wrapper {
   private CBCBlockCipher engine;
   private ParametersWithIV param;
   private boolean forWrapping;
   private SecureRandom rand;

   public RFC3211WrapEngine(BlockCipher var1) {
      this.engine = new CBCBlockCipher(var1);
   }

   @Override
   public void init(boolean var1, CipherParameters var2) {
      this.forWrapping = var1;
      if (var2 instanceof ParametersWithRandom) {
         ParametersWithRandom var3 = (ParametersWithRandom)var2;
         this.rand = var3.getRandom();
         if (!(var3.getParameters() instanceof ParametersWithIV)) {
            throw new IllegalArgumentException("RFC3211Wrap requires an IV");
         }

         this.param = (ParametersWithIV)var3.getParameters();
      } else {
         if (var1) {
            this.rand = CryptoServicesRegistrar.getSecureRandom();
         }

         if (!(var2 instanceof ParametersWithIV)) {
            throw new IllegalArgumentException("RFC3211Wrap requires an IV");
         }

         this.param = (ParametersWithIV)var2;
      }
   }

   @Override
   public String getAlgorithmName() {
      return this.engine.getUnderlyingCipher().getAlgorithmName() + "/RFC3211Wrap";
   }

   @Override
   public byte[] wrap(byte[] var1, int var2, int var3) {
      if (!this.forWrapping) {
         throw new IllegalStateException("not set for wrapping");
      } else if (var3 <= 255 && var3 >= 0) {
         this.engine.init(true, this.param);
         int var4 = this.engine.getBlockSize();
         byte[] var5;
         if (var3 + 4 < var4 * 2) {
            var5 = new byte[var4 * 2];
         } else {
            var5 = new byte[(var3 + 4) % var4 == 0 ? var3 + 4 : ((var3 + 4) / var4 + 1) * var4];
         }

         var5[0] = (byte)var3;
         System.arraycopy(var1, var2, var5, 4, var3);
         byte[] var6 = new byte[var5.length - (var3 + 4)];
         this.rand.nextBytes(var6);
         System.arraycopy(var6, 0, var5, var3 + 4, var6.length);
         var5[1] = (byte)(~var5[4]);
         var5[2] = (byte)(~var5[5]);
         var5[3] = (byte)(~var5[6]);

         for (int var7 = 0; var7 < var5.length; var7 += var4) {
            this.engine.processBlock(var5, var7, var5, var7);
         }

         for (int var8 = 0; var8 < var5.length; var8 += var4) {
            this.engine.processBlock(var5, var8, var5, var8);
         }

         return var5;
      } else {
         throw new IllegalArgumentException("input must be from 0 to 255 bytes");
      }
   }

   @Override
   public byte[] unwrap(byte[] var1, int var2, int var3) throws InvalidCipherTextException {
      if (this.forWrapping) {
         throw new IllegalStateException("not set for unwrapping");
      } else {
         int var4 = this.engine.getBlockSize();
         if (var3 < 2 * var4) {
            throw new InvalidCipherTextException("input too short");
         } else {
            byte[] var5 = new byte[var3];
            byte[] var6 = new byte[var4];
            System.arraycopy(var1, var2, var5, 0, var3);
            System.arraycopy(var1, var2, var6, 0, var6.length);
            this.engine.init(false, new ParametersWithIV(this.param.getParameters(), var6));

            for (int var7 = var4; var7 < var5.length; var7 += var4) {
               this.engine.processBlock(var5, var7, var5, var7);
            }

            System.arraycopy(var5, var5.length - var6.length, var6, 0, var6.length);
            this.engine.init(false, new ParametersWithIV(this.param.getParameters(), var6));
            this.engine.processBlock(var5, 0, var5, 0);
            this.engine.init(false, this.param);

            for (int var12 = 0; var12 < var5.length; var12 += var4) {
               this.engine.processBlock(var5, var12, var5, var12);
            }

            boolean var13 = (var5[0] & 255) > var5.length - 4;
            byte[] var8;
            if (var13) {
               var8 = new byte[var5.length - 4];
            } else {
               var8 = new byte[var5[0] & 255];
            }

            System.arraycopy(var5, 4, var8, 0, var8.length);
            int var9 = 0;

            for (int var10 = 0; var10 != 3; var10++) {
               byte var11 = (byte)(~var5[1 + var10]);
               var9 |= var11 ^ var5[4 + var10];
            }

            Arrays.clear(var5);
            if (var9 != 0 | var13) {
               throw new InvalidCipherTextException("wrapped key corrupted");
            } else {
               return var8;
            }
         }
      }
   }
}
