package org.bouncycastle.openssl.jcajce;

import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.RC2ParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.openssl.EncryptionException;
import org.bouncycastle.openssl.PEMException;
import org.bouncycastle.util.Integers;

class PEMUtilities {
   private static final Map KEYSIZES = new HashMap();
   private static final Set PKCS5_SCHEME_1 = new HashSet();
   private static final Set PKCS5_SCHEME_2 = new HashSet();
   private static final Map PRFS = new HashMap();
   private static final Map PRFS_SALT = new HashMap();
   private static final Map CIPHER_NAMES = new HashMap();
   private static final Map KEY_NAMES = new HashMap();

   static int getKeySize(String var0) {
      if (!KEYSIZES.containsKey(var0)) {
         throw new IllegalStateException("no key size for algorithm: " + var0);
      } else {
         return (Integer)KEYSIZES.get(var0);
      }
   }

   static int getSaltSize(ASN1ObjectIdentifier var0) {
      if (!PRFS_SALT.containsKey(var0)) {
         throw new IllegalStateException("no salt size for algorithm: " + var0);
      } else {
         return (Integer)PRFS_SALT.get(var0);
      }
   }

   static boolean isHmacSHA1(AlgorithmIdentifier var0) {
      return var0 == null || var0.getAlgorithm().equals(PKCSObjectIdentifiers.id_hmacWithSHA1);
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

   public static SecretKey generateSecretKeyForPKCS5Scheme2(JcaJceHelper var0, String var1, char[] var2, byte[] var3, int var4) throws NoSuchProviderException, NoSuchAlgorithmException, InvalidKeySpecException {
      SecretKeyFactory var5 = var0.createSecretKeyFactory("PBKDF2with8BIT");
      SecretKey var6 = var5.generateSecret(new PBEKeySpec(var2, var3, var4, getKeySize(var1)));
      return new SecretKeySpec(var6.getEncoded(), getAlgorithmName(var1));
   }

   public static SecretKey generateSecretKeyForPKCS5Scheme2(JcaJceHelper var0, String var1, char[] var2, byte[] var3, int var4, AlgorithmIdentifier var5) throws NoSuchProviderException, NoSuchAlgorithmException, InvalidKeySpecException {
      String var6 = (String)PRFS.get(var5.getAlgorithm());
      if (var6 == null) {
         throw new NoSuchAlgorithmException("unknown PRF in PKCS#2: " + var5.getAlgorithm());
      } else {
         SecretKeyFactory var7 = var0.createSecretKeyFactory(var6);
         SecretKey var8 = var7.generateSecret(new PBEKeySpec(var2, var3, var4, getKeySize(var1)));
         return new SecretKeySpec(var8.getEncoded(), var1);
      }
   }

   static byte[] crypt(boolean var0, JcaJceHelper var1, byte[] var2, char[] var3, String var4, byte[] var5) throws PEMException {
      Object var6 = new IvParameterSpec(var5);
      String var8 = "CBC";
      String var9 = "PKCS5Padding";
      if (var4.endsWith("-CFB")) {
         var8 = "CFB";
         var9 = "NoPadding";
      }

      if (var4.endsWith("-ECB") || "DES-EDE".equals(var4) || "DES-EDE3".equals(var4)) {
         var8 = "ECB";
         var6 = null;
      }

      if (var4.endsWith("-OFB")) {
         var8 = "OFB";
         var9 = "NoPadding";
      }

      String var7;
      SecretKey var10;
      if (var4.startsWith("DES-EDE")) {
         var7 = "DESede";
         boolean var11 = !var4.startsWith("DES-EDE3");
         var10 = getKey(var1, var3, var7, 24, var5, var11);
      } else if (var4.startsWith("DES-")) {
         var7 = "DES";
         var10 = getKey(var1, var3, var7, 8, var5);
      } else if (var4.startsWith("BF-")) {
         var7 = "Blowfish";
         var10 = getKey(var1, var3, var7, 16, var5);
      } else if (var4.startsWith("RC2-")) {
         var7 = "RC2";
         short var15 = 128;
         if (var4.startsWith("RC2-40-")) {
            var15 = 40;
         } else if (var4.startsWith("RC2-64-")) {
            var15 = 64;
         }

         var10 = getKey(var1, var3, var7, var15 / 8, var5);
         if (var6 == null) {
            var6 = new RC2ParameterSpec(var15);
         } else {
            var6 = new RC2ParameterSpec(var15, var5);
         }
      } else {
         if (!var4.startsWith("AES-")) {
            throw new EncryptionException("unknown encryption with private key");
         }

         var7 = "AES";
         byte[] var16 = var5;
         if (var5.length > 8) {
            var16 = new byte[8];
            System.arraycopy(var5, 0, var16, 0, 8);
         }

         short var12;
         if (var4.startsWith("AES-128-")) {
            var12 = 128;
         } else if (var4.startsWith("AES-192-")) {
            var12 = 192;
         } else {
            if (!var4.startsWith("AES-256-")) {
               throw new EncryptionException("unknown AES encryption with private key");
            }

            var12 = 256;
         }

         var10 = getKey(var1, var3, "AES", var12 / 8, var16);
      }

      String var17 = var7 + "/" + var8 + "/" + var9;

      try {
         Cipher var18 = var1.createCipher(var17);
         int var13 = var0 ? 1 : 2;
         if (var6 == null) {
            var18.init(var13, var10);
         } else {
            var18.init(var13, var10, (AlgorithmParameterSpec)var6);
         }

         return var18.doFinal(var2);
      } catch (Exception var14) {
         throw new EncryptionException("exception using cipher - please check password and data.", var14);
      }
   }

   private static SecretKey getKey(JcaJceHelper var0, char[] var1, String var2, int var3, byte[] var4) throws PEMException {
      return getKey(var0, var1, var2, var3, var4, false);
   }

   private static SecretKey getKey(JcaJceHelper var0, char[] var1, String var2, int var3, byte[] var4, boolean var5) throws PEMException {
      try {
         PBEKeySpec var6 = new PBEKeySpec(var1, var4, 1, var3 * 8);
         SecretKeyFactory var7 = var0.createSecretKeyFactory("PBKDF-OpenSSL");
         byte[] var8 = var7.generateSecret(var6).getEncoded();
         if (var5 && var8.length >= 24) {
            System.arraycopy(var8, 0, var8, 16, 8);
         }

         return new SecretKeySpec(var8, var2);
      } catch (GeneralSecurityException var9) {
         throw new PEMException("Unable to create OpenSSL PBDKF: " + var9.getMessage(), var9);
      }
   }

   static String getCipherName(ASN1ObjectIdentifier var0) {
      String var1 = (String)CIPHER_NAMES.get(var0);
      return var1 != null ? var1 : var0.getId();
   }

   static String getAlgorithmName(String var0) {
      String var1 = (String)KEY_NAMES.get(var0);
      return var1 != null ? var1 : var0;
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
      PRFS.put(PKCSObjectIdentifiers.id_hmacWithSHA1, "PBKDF2withHMACSHA1");
      PRFS.put(PKCSObjectIdentifiers.id_hmacWithSHA256, "PBKDF2withHMACSHA256");
      PRFS.put(PKCSObjectIdentifiers.id_hmacWithSHA512, "PBKDF2withHMACSHA512");
      PRFS.put(PKCSObjectIdentifiers.id_hmacWithSHA224, "PBKDF2withHMACSHA224");
      PRFS.put(PKCSObjectIdentifiers.id_hmacWithSHA384, "PBKDF2withHMACSHA384");
      PRFS.put(NISTObjectIdentifiers.id_hmacWithSHA3_224, "PBKDF2withHMACSHA3-224");
      PRFS.put(NISTObjectIdentifiers.id_hmacWithSHA3_256, "PBKDF2withHMACSHA3-256");
      PRFS.put(NISTObjectIdentifiers.id_hmacWithSHA3_384, "PBKDF2withHMACSHA3-384");
      PRFS.put(NISTObjectIdentifiers.id_hmacWithSHA3_512, "PBKDF2withHMACSHA3-512");
      PRFS.put(CryptoProObjectIdentifiers.gostR3411Hmac, "PBKDF2withHMACGOST3411");
      PRFS_SALT.put(PKCSObjectIdentifiers.id_hmacWithSHA1, Integers.valueOf(20));
      PRFS_SALT.put(PKCSObjectIdentifiers.id_hmacWithSHA256, Integers.valueOf(32));
      PRFS_SALT.put(PKCSObjectIdentifiers.id_hmacWithSHA512, Integers.valueOf(64));
      PRFS_SALT.put(PKCSObjectIdentifiers.id_hmacWithSHA224, Integers.valueOf(28));
      PRFS_SALT.put(PKCSObjectIdentifiers.id_hmacWithSHA384, Integers.valueOf(48));
      PRFS_SALT.put(NISTObjectIdentifiers.id_hmacWithSHA3_224, Integers.valueOf(28));
      PRFS_SALT.put(NISTObjectIdentifiers.id_hmacWithSHA3_256, Integers.valueOf(32));
      PRFS_SALT.put(NISTObjectIdentifiers.id_hmacWithSHA3_384, Integers.valueOf(48));
      PRFS_SALT.put(NISTObjectIdentifiers.id_hmacWithSHA3_512, Integers.valueOf(64));
      PRFS_SALT.put(CryptoProObjectIdentifiers.gostR3411Hmac, Integers.valueOf(32));
      CIPHER_NAMES.put(PKCSObjectIdentifiers.des_EDE3_CBC, "DESEDE/CBC/PKCS5Padding");
      CIPHER_NAMES.put(NISTObjectIdentifiers.id_aes128_CBC, "AES/CBC/PKCS7Padding");
      CIPHER_NAMES.put(NISTObjectIdentifiers.id_aes192_CBC, "AES/CBC/PKCS7Padding");
      CIPHER_NAMES.put(NISTObjectIdentifiers.id_aes256_CBC, "AES/CBC/PKCS7Padding");
      KEY_NAMES.put(PKCSObjectIdentifiers.des_EDE3_CBC.getId(), "DESEDE");
      KEY_NAMES.put(NISTObjectIdentifiers.id_aes128_CBC.getId(), "AES");
      KEY_NAMES.put(NISTObjectIdentifiers.id_aes192_CBC.getId(), "AES");
      KEY_NAMES.put(NISTObjectIdentifiers.id_aes256_CBC.getId(), "AES");
   }
}
