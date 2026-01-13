package org.bouncycastle.openssl.bc;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.PBEParametersGenerator;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.engines.BlowfishEngine;
import org.bouncycastle.crypto.engines.DESEngine;
import org.bouncycastle.crypto.engines.DESedeEngine;
import org.bouncycastle.crypto.engines.RC2Engine;
import org.bouncycastle.crypto.generators.OpenSSLPBEParametersGenerator;
import org.bouncycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.modes.CFBBlockCipher;
import org.bouncycastle.crypto.modes.OFBBlockCipher;
import org.bouncycastle.crypto.paddings.PKCS7Padding;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.params.RC2Parameters;
import org.bouncycastle.openssl.EncryptionException;
import org.bouncycastle.openssl.PEMException;
import org.bouncycastle.util.Integers;

class PEMUtilities {
   private static final Map KEYSIZES = new HashMap();
   private static final Set PKCS5_SCHEME_1 = new HashSet();
   private static final Set PKCS5_SCHEME_2 = new HashSet();

   static int getKeySize(String var0) {
      if (!KEYSIZES.containsKey(var0)) {
         throw new IllegalStateException("no key size for algorithm: " + var0);
      } else {
         return (Integer)KEYSIZES.get(var0);
      }
   }

   static boolean isPKCS5Scheme1(ASN1ObjectIdentifier var0) {
      return PKCS5_SCHEME_1.contains(var0);
   }

   static boolean isPKCS5Scheme2(ASN1ObjectIdentifier var0) {
      return PKCS5_SCHEME_2.contains(var0);
   }

   public static boolean isPKCS12(ASN1ObjectIdentifier var0) {
      return var0.getId().startsWith(PKCSObjectIdentifiers.pkcs_12PbeIds.getId());
   }

   public static KeyParameter generateSecretKeyForPKCS5Scheme2(String var0, char[] var1, byte[] var2, int var3) {
      PKCS5S2ParametersGenerator var4 = new PKCS5S2ParametersGenerator(new SHA1Digest());
      var4.init(PBEParametersGenerator.PKCS5PasswordToBytes(var1), var2, var3);
      return (KeyParameter)var4.generateDerivedParameters(getKeySize(var0));
   }

   static byte[] crypt(boolean var0, byte[] var1, char[] var2, String var3, byte[] var4) throws PEMException {
      byte[] var5 = var4;
      String var6 = "CBC";
      PKCS7Padding var8 = new PKCS7Padding();
      if (var3.endsWith("-CFB")) {
         var6 = "CFB";
         var8 = null;
      }

      if (var3.endsWith("-ECB") || "DES-EDE".equals(var3) || "DES-EDE3".equals(var3)) {
         var6 = "ECB";
         var5 = null;
      }

      if (var3.endsWith("-OFB")) {
         var6 = "OFB";
         var8 = null;
      }

      Object var7;
      Object var9;
      if (var3.startsWith("DES-EDE")) {
         boolean var10 = !var3.startsWith("DES-EDE3");
         var9 = getKey(var2, 24, var4, var10);
         var7 = new DESedeEngine();
      } else if (var3.startsWith("DES-")) {
         var9 = getKey(var2, 8, var4);
         var7 = new DESEngine();
      } else if (var3.startsWith("BF-")) {
         var9 = getKey(var2, 16, var4);
         var7 = new BlowfishEngine();
      } else if (var3.startsWith("RC2-")) {
         short var15 = 128;
         if (var3.startsWith("RC2-40-")) {
            var15 = 40;
         } else if (var3.startsWith("RC2-64-")) {
            var15 = 64;
         }

         var9 = new RC2Parameters(getKey(var2, var15 / 8, var4).getKey(), var15);
         var7 = new RC2Engine();
      } else {
         if (!var3.startsWith("AES-")) {
            throw new EncryptionException("unknown encryption with private key: " + var3);
         }

         byte[] var16 = var4;
         if (var4.length > 8) {
            var16 = new byte[8];
            System.arraycopy(var4, 0, var16, 0, 8);
         }

         short var11;
         if (var3.startsWith("AES-128-")) {
            var11 = 128;
         } else if (var3.startsWith("AES-192-")) {
            var11 = 192;
         } else {
            if (!var3.startsWith("AES-256-")) {
               throw new EncryptionException("unknown AES encryption with private key: " + var3);
            }

            var11 = 256;
         }

         var9 = getKey(var2, var11 / 8, var16);
         var7 = AESEngine.newInstance();
      }

      if (var6.equals("CBC")) {
         var7 = CBCBlockCipher.newInstance((BlockCipher)var7);
      } else if (var6.equals("CFB")) {
         var7 = CFBBlockCipher.newInstance((BlockCipher)var7, ((BlockCipher)var7).getBlockSize() * 8);
      } else if (var6.equals("OFB")) {
         var7 = new OFBBlockCipher((BlockCipher)var7, ((BlockCipher)var7).getBlockSize() * 8);
      }

      try {
         Object var17;
         if (var8 == null) {
            var17 = new BufferedBlockCipher((BlockCipher)var7);
         } else {
            var17 = new PaddedBufferedBlockCipher((BlockCipher)var7, var8);
         }

         if (var5 == null) {
            ((BufferedBlockCipher)var17).init(var0, (CipherParameters)var9);
         } else {
            ((BufferedBlockCipher)var17).init(var0, new ParametersWithIV((CipherParameters)var9, var5));
         }

         byte[] var18 = new byte[((BufferedBlockCipher)var17).getOutputSize(var1.length)];
         int var12 = ((BufferedBlockCipher)var17).processBytes(var1, 0, var1.length, var18, 0);
         var12 += ((BufferedBlockCipher)var17).doFinal(var18, var12);
         if (var12 == var18.length) {
            return var18;
         } else {
            byte[] var13 = new byte[var12];
            System.arraycopy(var18, 0, var13, 0, var12);
            return var13;
         }
      } catch (Exception var14) {
         throw new EncryptionException("exception using cipher - please check password and data.", var14);
      }
   }

   private static KeyParameter getKey(char[] var0, int var1, byte[] var2) throws PEMException {
      return getKey(var0, var1, var2, false);
   }

   private static KeyParameter getKey(char[] var0, int var1, byte[] var2, boolean var3) throws PEMException {
      OpenSSLPBEParametersGenerator var4 = new OpenSSLPBEParametersGenerator();
      var4.init(PBEParametersGenerator.PKCS5PasswordToBytes(var0), var2, 1);
      KeyParameter var5 = (KeyParameter)var4.generateDerivedParameters(var1 * 8);
      if (var3 && var5.getKey().length == 24) {
         byte[] var6 = var5.getKey();
         System.arraycopy(var6, 0, var6, 16, 8);
         return new KeyParameter(var6);
      } else {
         return var5;
      }
   }

   static {
      PKCS5_SCHEME_1.add(PKCSObjectIdentifiers.pbeWithMD2AndDES_CBC);
      PKCS5_SCHEME_1.add(PKCSObjectIdentifiers.pbeWithMD2AndRC2_CBC);
      PKCS5_SCHEME_1.add(PKCSObjectIdentifiers.pbeWithMD5AndDES_CBC);
      PKCS5_SCHEME_1.add(PKCSObjectIdentifiers.pbeWithMD5AndRC2_CBC);
      PKCS5_SCHEME_1.add(PKCSObjectIdentifiers.pbeWithSHA1AndDES_CBC);
      PKCS5_SCHEME_1.add(PKCSObjectIdentifiers.pbeWithSHA1AndRC2_CBC);
      PKCS5_SCHEME_2.add(PKCSObjectIdentifiers.id_PBES2);
      PKCS5_SCHEME_2.add(PKCSObjectIdentifiers.des_EDE3_CBC);
      PKCS5_SCHEME_2.add(NISTObjectIdentifiers.id_aes128_CBC);
      PKCS5_SCHEME_2.add(NISTObjectIdentifiers.id_aes192_CBC);
      PKCS5_SCHEME_2.add(NISTObjectIdentifiers.id_aes256_CBC);
      KEYSIZES.put(PKCSObjectIdentifiers.des_EDE3_CBC.getId(), Integers.valueOf(192));
      KEYSIZES.put(NISTObjectIdentifiers.id_aes128_CBC.getId(), Integers.valueOf(128));
      KEYSIZES.put(NISTObjectIdentifiers.id_aes192_CBC.getId(), Integers.valueOf(192));
      KEYSIZES.put(NISTObjectIdentifiers.id_aes256_CBC.getId(), Integers.valueOf(256));
      KEYSIZES.put(PKCSObjectIdentifiers.pbeWithSHAAnd128BitRC4.getId(), Integers.valueOf(128));
      KEYSIZES.put(PKCSObjectIdentifiers.pbeWithSHAAnd40BitRC4, Integers.valueOf(40));
      KEYSIZES.put(PKCSObjectIdentifiers.pbeWithSHAAnd2_KeyTripleDES_CBC, Integers.valueOf(128));
      KEYSIZES.put(PKCSObjectIdentifiers.pbeWithSHAAnd3_KeyTripleDES_CBC, Integers.valueOf(192));
      KEYSIZES.put(PKCSObjectIdentifiers.pbeWithSHAAnd128BitRC2_CBC, Integers.valueOf(128));
      KEYSIZES.put(PKCSObjectIdentifiers.pbeWithSHAAnd40BitRC2_CBC, Integers.valueOf(40));
   }
}
