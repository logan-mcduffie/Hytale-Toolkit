package org.bouncycastle.jce.provider;

import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.spec.PBEParameterSpec;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.PBEParametersGenerator;
import org.bouncycastle.crypto.digests.MD5Digest;
import org.bouncycastle.crypto.digests.RIPEMD160Digest;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.generators.PKCS12ParametersGenerator;
import org.bouncycastle.crypto.generators.PKCS5S1ParametersGenerator;
import org.bouncycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.jcajce.provider.symmetric.util.BCPBEKey;

public interface BrokenPBE {
   int MD5 = 0;
   int SHA1 = 1;
   int RIPEMD160 = 2;
   int PKCS5S1 = 0;
   int PKCS5S2 = 1;
   int PKCS12 = 2;
   int OLD_PKCS12 = 3;

   public static class Util {
      private static void setOddParity(byte[] var0) {
         for (int var1 = 0; var1 < var0.length; var1++) {
            byte var2 = var0[var1];
            var0[var1] = (byte)(var2 & 254 | var2 >> 1 ^ var2 >> 2 ^ var2 >> 3 ^ var2 >> 4 ^ var2 >> 5 ^ var2 >> 6 ^ var2 >> 7 ^ 1);
         }
      }

      private static PBEParametersGenerator makePBEGenerator(int var0, int var1) {
         Object var2;
         if (var0 == 0) {
            switch (var1) {
               case 0:
                  var2 = new PKCS5S1ParametersGenerator(new MD5Digest());
                  break;
               case 1:
                  var2 = new PKCS5S1ParametersGenerator(new SHA1Digest());
                  break;
               default:
                  throw new IllegalStateException("PKCS5 scheme 1 only supports only MD5 and SHA1.");
            }
         } else if (var0 == 1) {
            var2 = new PKCS5S2ParametersGenerator();
         } else if (var0 == 3) {
            switch (var1) {
               case 0:
                  var2 = new OldPKCS12ParametersGenerator(new MD5Digest());
                  break;
               case 1:
                  var2 = new OldPKCS12ParametersGenerator(new SHA1Digest());
                  break;
               case 2:
                  var2 = new OldPKCS12ParametersGenerator(new RIPEMD160Digest());
                  break;
               default:
                  throw new IllegalStateException("unknown digest scheme for PBE encryption.");
            }
         } else {
            switch (var1) {
               case 0:
                  var2 = new PKCS12ParametersGenerator(new MD5Digest());
                  break;
               case 1:
                  var2 = new PKCS12ParametersGenerator(new SHA1Digest());
                  break;
               case 2:
                  var2 = new PKCS12ParametersGenerator(new RIPEMD160Digest());
                  break;
               default:
                  throw new IllegalStateException("unknown digest scheme for PBE encryption.");
            }
         }

         return (PBEParametersGenerator)var2;
      }

      static CipherParameters makePBEParameters(BCPBEKey var0, AlgorithmParameterSpec var1, int var2, int var3, String var4, int var5, int var6) {
         if (var1 != null && var1 instanceof PBEParameterSpec) {
            PBEParameterSpec var7 = (PBEParameterSpec)var1;
            PBEParametersGenerator var8 = makePBEGenerator(var2, var3);
            byte[] var9 = var0.getEncoded();
            var8.init(var9, var7.getSalt(), var7.getIterationCount());
            CipherParameters var10;
            if (var6 != 0) {
               var10 = var8.generateDerivedParameters(var5, var6);
            } else {
               var10 = var8.generateDerivedParameters(var5);
            }

            if (var4.startsWith("DES")) {
               if (var10 instanceof ParametersWithIV) {
                  KeyParameter var11 = (KeyParameter)((ParametersWithIV)var10).getParameters();
                  setOddParity(var11.getKey());
               } else {
                  KeyParameter var12 = (KeyParameter)var10;
                  setOddParity(var12.getKey());
               }
            }

            for (int var13 = 0; var13 != var9.length; var13++) {
               var9[var13] = 0;
            }

            return var10;
         } else {
            throw new IllegalArgumentException("Need a PBEParameter spec with a PBE key.");
         }
      }

      static CipherParameters makePBEMacParameters(BCPBEKey var0, AlgorithmParameterSpec var1, int var2, int var3, int var4) {
         if (var1 != null && var1 instanceof PBEParameterSpec) {
            PBEParameterSpec var5 = (PBEParameterSpec)var1;
            PBEParametersGenerator var6 = makePBEGenerator(var2, var3);
            byte[] var7 = var0.getEncoded();
            var6.init(var7, var5.getSalt(), var5.getIterationCount());
            CipherParameters var8 = var6.generateDerivedMacParameters(var4);

            for (int var9 = 0; var9 != var7.length; var9++) {
               var7[var9] = 0;
            }

            return var8;
         } else {
            throw new IllegalArgumentException("Need a PBEParameter spec with a PBE key.");
         }
      }
   }
}
