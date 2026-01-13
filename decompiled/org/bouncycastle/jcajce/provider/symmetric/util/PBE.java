package org.bouncycastle.jcajce.provider.symmetric.util;

import java.security.InvalidAlgorithmParameterException;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.SecretKey;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.PBEParametersGenerator;
import org.bouncycastle.crypto.digests.GOST3411Digest;
import org.bouncycastle.crypto.digests.MD2Digest;
import org.bouncycastle.crypto.digests.RIPEMD160Digest;
import org.bouncycastle.crypto.digests.SM3Digest;
import org.bouncycastle.crypto.digests.TigerDigest;
import org.bouncycastle.crypto.generators.OpenSSLPBEParametersGenerator;
import org.bouncycastle.crypto.generators.PKCS12ParametersGenerator;
import org.bouncycastle.crypto.generators.PKCS5S1ParametersGenerator;
import org.bouncycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.bouncycastle.crypto.params.DESParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.util.DigestFactory;

public interface PBE {
   int MD5 = 0;
   int SHA1 = 1;
   int RIPEMD160 = 2;
   int TIGER = 3;
   int SHA256 = 4;
   int MD2 = 5;
   int GOST3411 = 6;
   int SHA224 = 7;
   int SHA384 = 8;
   int SHA512 = 9;
   int SHA3_224 = 10;
   int SHA3_256 = 11;
   int SHA3_384 = 12;
   int SHA3_512 = 13;
   int SM3 = 14;
   int SHA512_224 = 15;
   int SHA512_256 = 16;
   int PKCS5S1 = 0;
   int PKCS5S2 = 1;
   int PKCS12 = 2;
   int OPENSSL = 3;
   int PKCS5S1_UTF8 = 4;
   int PKCS5S2_UTF8 = 5;

   public static class Util {
      private static PBEParametersGenerator makePBEGenerator(int var0, int var1) {
         Object var2;
         if (var0 == 0 || var0 == 4) {
            switch (var1) {
               case 0:
                  var2 = new PKCS5S1ParametersGenerator(DigestFactory.createMD5());
                  break;
               case 1:
                  var2 = new PKCS5S1ParametersGenerator(DigestFactory.createSHA1());
                  break;
               case 5:
                  var2 = new PKCS5S1ParametersGenerator(new MD2Digest());
                  break;
               default:
                  throw new IllegalStateException("PKCS5 scheme 1 only supports MD2, MD5 and SHA1.");
            }
         } else if (var0 == 1 || var0 == 5) {
            switch (var1) {
               case 0:
                  var2 = new PKCS5S2ParametersGenerator(DigestFactory.createMD5PRF());
                  break;
               case 1:
                  var2 = new PKCS5S2ParametersGenerator(DigestFactory.createSHA1PRF());
                  break;
               case 2:
                  var2 = new PKCS5S2ParametersGenerator(new RIPEMD160Digest(CryptoServicePurpose.PRF));
                  break;
               case 3:
                  var2 = new PKCS5S2ParametersGenerator(new TigerDigest(CryptoServicePurpose.PRF));
                  break;
               case 4:
                  var2 = new PKCS5S2ParametersGenerator(DigestFactory.createSHA256PRF());
                  break;
               case 5:
                  var2 = new PKCS5S2ParametersGenerator(new MD2Digest(CryptoServicePurpose.PRF));
                  break;
               case 6:
                  var2 = new PKCS5S2ParametersGenerator(new GOST3411Digest(CryptoServicePurpose.PRF));
                  break;
               case 7:
                  var2 = new PKCS5S2ParametersGenerator(DigestFactory.createSHA224PRF());
                  break;
               case 8:
                  var2 = new PKCS5S2ParametersGenerator(DigestFactory.createSHA384PRF());
                  break;
               case 9:
                  var2 = new PKCS5S2ParametersGenerator(DigestFactory.createSHA512PRF());
                  break;
               case 10:
                  var2 = new PKCS5S2ParametersGenerator(DigestFactory.createSHA3_224PRF());
                  break;
               case 11:
                  var2 = new PKCS5S2ParametersGenerator(DigestFactory.createSHA3_256PRF());
                  break;
               case 12:
                  var2 = new PKCS5S2ParametersGenerator(DigestFactory.createSHA3_384PRF());
                  break;
               case 13:
                  var2 = new PKCS5S2ParametersGenerator(DigestFactory.createSHA3_512PRF());
                  break;
               case 14:
                  var2 = new PKCS5S2ParametersGenerator(new SM3Digest(CryptoServicePurpose.PRF));
                  break;
               case 15:
                  var2 = new PKCS5S2ParametersGenerator(DigestFactory.createSHA512_224PRF());
                  break;
               case 16:
                  var2 = new PKCS5S2ParametersGenerator(DigestFactory.createSHA512_256PRF());
                  break;
               default:
                  throw new IllegalStateException("unknown digest scheme for PBE PKCS5S2 encryption.");
            }
         } else if (var0 == 2) {
            switch (var1) {
               case 0:
                  var2 = new PKCS12ParametersGenerator(DigestFactory.createMD5PRF());
                  break;
               case 1:
                  var2 = new PKCS12ParametersGenerator(DigestFactory.createSHA1PRF());
                  break;
               case 2:
                  var2 = new PKCS12ParametersGenerator(new RIPEMD160Digest(CryptoServicePurpose.PRF));
                  break;
               case 3:
                  var2 = new PKCS12ParametersGenerator(new TigerDigest(CryptoServicePurpose.PRF));
                  break;
               case 4:
                  var2 = new PKCS12ParametersGenerator(DigestFactory.createSHA256PRF());
                  break;
               case 5:
                  var2 = new PKCS12ParametersGenerator(new MD2Digest(CryptoServicePurpose.PRF));
                  break;
               case 6:
                  var2 = new PKCS12ParametersGenerator(new GOST3411Digest(CryptoServicePurpose.PRF));
                  break;
               case 7:
                  var2 = new PKCS12ParametersGenerator(DigestFactory.createSHA224PRF());
                  break;
               case 8:
                  var2 = new PKCS12ParametersGenerator(DigestFactory.createSHA384PRF());
                  break;
               case 9:
                  var2 = new PKCS12ParametersGenerator(DigestFactory.createSHA512PRF());
                  break;
               default:
                  throw new IllegalStateException("unknown digest scheme for PBE encryption.");
            }
         } else {
            var2 = new OpenSSLPBEParametersGenerator();
         }

         return (PBEParametersGenerator)var2;
      }

      public static CipherParameters makePBEParameters(byte[] var0, int var1, int var2, int var3, int var4, AlgorithmParameterSpec var5, String var6) throws InvalidAlgorithmParameterException {
         if (var5 != null && var5 instanceof PBEParameterSpec) {
            PBEParameterSpec var7 = (PBEParameterSpec)var5;
            PBEParametersGenerator var8 = makePBEGenerator(var1, var2);
            var8.init(var0, var7.getSalt(), var7.getIterationCount());
            CipherParameters var10;
            if (var4 != 0) {
               var10 = var8.generateDerivedParameters(var3, var4);
            } else {
               var10 = var8.generateDerivedParameters(var3);
            }

            if (var6.startsWith("DES")) {
               if (var10 instanceof ParametersWithIV) {
                  KeyParameter var11 = (KeyParameter)((ParametersWithIV)var10).getParameters();
                  DESParameters.setOddParity(var11.getKey());
               } else {
                  KeyParameter var12 = (KeyParameter)var10;
                  DESParameters.setOddParity(var12.getKey());
               }
            }

            return var10;
         } else {
            throw new InvalidAlgorithmParameterException("Need a PBEParameter spec with a PBE key.");
         }
      }

      public static CipherParameters makePBEParameters(BCPBEKey var0, AlgorithmParameterSpec var1, String var2) {
         if (var1 != null && var1 instanceof PBEParameterSpec) {
            PBEParameterSpec var3 = (PBEParameterSpec)var1;
            PBEParametersGenerator var4 = makePBEGenerator(var0.getType(), var0.getDigest());
            byte[] var5 = var0.getEncoded();
            if (var0.shouldTryWrongPKCS12()) {
               var5 = new byte[2];
            }

            var4.init(var5, var3.getSalt(), var3.getIterationCount());
            CipherParameters var6;
            if (var0.getIvSize() != 0) {
               var6 = var4.generateDerivedParameters(var0.getKeySize(), var0.getIvSize());
            } else {
               var6 = var4.generateDerivedParameters(var0.getKeySize());
            }

            if (var2.startsWith("DES")) {
               if (var6 instanceof ParametersWithIV) {
                  KeyParameter var7 = (KeyParameter)((ParametersWithIV)var6).getParameters();
                  DESParameters.setOddParity(var7.getKey());
               } else {
                  KeyParameter var8 = (KeyParameter)var6;
                  DESParameters.setOddParity(var8.getKey());
               }
            }

            return var6;
         } else {
            throw new IllegalArgumentException("Need a PBEParameter spec with a PBE key.");
         }
      }

      public static CipherParameters makePBEMacParameters(BCPBEKey var0, AlgorithmParameterSpec var1) {
         if (var1 != null && var1 instanceof PBEParameterSpec) {
            PBEParameterSpec var2 = (PBEParameterSpec)var1;
            PBEParametersGenerator var3 = makePBEGenerator(var0.getType(), var0.getDigest());
            byte[] var4 = var0.getEncoded();
            var3.init(var4, var2.getSalt(), var2.getIterationCount());
            return var3.generateDerivedMacParameters(var0.getKeySize());
         } else {
            throw new IllegalArgumentException("Need a PBEParameter spec with a PBE key.");
         }
      }

      public static CipherParameters makePBEMacParameters(PBEKeySpec var0, int var1, int var2, int var3) {
         PBEParametersGenerator var4 = makePBEGenerator(var1, var2);
         byte[] var5 = convertPassword(var1, var0);
         var4.init(var5, var0.getSalt(), var0.getIterationCount());
         CipherParameters var6 = var4.generateDerivedMacParameters(var3);

         for (int var7 = 0; var7 != var5.length; var7++) {
            var5[var7] = 0;
         }

         return var6;
      }

      public static CipherParameters makePBEParameters(PBEKeySpec var0, int var1, int var2, int var3, int var4) {
         PBEParametersGenerator var5 = makePBEGenerator(var1, var2);
         byte[] var6 = convertPassword(var1, var0);
         var5.init(var6, var0.getSalt(), var0.getIterationCount());
         CipherParameters var7;
         if (var4 != 0) {
            var7 = var5.generateDerivedParameters(var3, var4);
         } else {
            var7 = var5.generateDerivedParameters(var3);
         }

         for (int var8 = 0; var8 != var6.length; var8++) {
            var6[var8] = 0;
         }

         return var7;
      }

      public static CipherParameters makePBEMacParameters(SecretKey var0, int var1, int var2, int var3, PBEParameterSpec var4) {
         PBEParametersGenerator var5 = makePBEGenerator(var1, var2);
         byte[] var7 = var0.getEncoded();
         var5.init(var0.getEncoded(), var4.getSalt(), var4.getIterationCount());
         CipherParameters var6 = var5.generateDerivedMacParameters(var3);

         for (int var8 = 0; var8 != var7.length; var8++) {
            var7[var8] = 0;
         }

         return var6;
      }

      private static byte[] convertPassword(int var0, PBEKeySpec var1) {
         byte[] var2;
         if (var0 == 2) {
            var2 = PBEParametersGenerator.PKCS12PasswordToBytes(var1.getPassword());
         } else if (var0 != 5 && var0 != 4) {
            var2 = PBEParametersGenerator.PKCS5PasswordToBytes(var1.getPassword());
         } else {
            var2 = PBEParametersGenerator.PKCS5PasswordToUTF8Bytes(var1.getPassword());
         }

         return var2;
      }
   }
}
