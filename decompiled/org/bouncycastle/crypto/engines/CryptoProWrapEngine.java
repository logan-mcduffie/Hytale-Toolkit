package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.modes.GCFBBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.params.ParametersWithSBox;
import org.bouncycastle.crypto.params.ParametersWithUKM;
import org.bouncycastle.util.Pack;

public class CryptoProWrapEngine extends GOST28147WrapEngine {
   @Override
   public void init(boolean var1, CipherParameters var2) {
      if (var2 instanceof ParametersWithRandom) {
         ParametersWithRandom var3 = (ParametersWithRandom)var2;
         var2 = var3.getParameters();
      }

      ParametersWithUKM var6 = (ParametersWithUKM)var2;
      byte[] var4 = null;
      KeyParameter var5;
      if (var6.getParameters() instanceof ParametersWithSBox) {
         var5 = (KeyParameter)((ParametersWithSBox)var6.getParameters()).getParameters();
         var4 = ((ParametersWithSBox)var6.getParameters()).getSBox();
      } else {
         var5 = (KeyParameter)var6.getParameters();
      }

      var5 = new KeyParameter(cryptoProDiversify(var5.getKey(), var6.getUKM(), var4));
      if (var4 != null) {
         super.init(var1, new ParametersWithUKM(new ParametersWithSBox(var5, var4), var6.getUKM()));
      } else {
         super.init(var1, new ParametersWithUKM(var5, var6.getUKM()));
      }
   }

   private static byte[] cryptoProDiversify(byte[] var0, byte[] var1, byte[] var2) {
      for (int var3 = 0; var3 != 8; var3++) {
         int var4 = 0;
         int var5 = 0;

         for (int var6 = 0; var6 != 8; var6++) {
            int var7 = Pack.littleEndianToInt(var0, var6 * 4);
            if (bitSet(var1[var3], var6)) {
               var4 += var7;
            } else {
               var5 += var7;
            }
         }

         byte[] var8 = new byte[8];
         Pack.intToLittleEndian(var4, var8, 0);
         Pack.intToLittleEndian(var5, var8, 4);
         GCFBBlockCipher var9 = new GCFBBlockCipher(new GOST28147Engine());
         var9.init(true, new ParametersWithIV(new ParametersWithSBox(new KeyParameter(var0), var2), var8));
         var9.processBlock(var0, 0, var0, 0);
         var9.processBlock(var0, 8, var0, 8);
         var9.processBlock(var0, 16, var0, 16);
         var9.processBlock(var0, 24, var0, 24);
      }

      return var0;
   }

   private static boolean bitSet(byte var0, int var1) {
      return (var0 & 1 << var1) != 0;
   }
}
