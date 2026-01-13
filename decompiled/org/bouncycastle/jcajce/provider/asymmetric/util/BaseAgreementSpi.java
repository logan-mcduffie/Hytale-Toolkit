package org.bouncycastle.jcajce.provider.asymmetric.util;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import javax.crypto.KeyAgreementSpi;
import javax.crypto.SecretKey;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.crypto.DerivationFunction;
import org.bouncycastle.crypto.agreement.kdf.DHKDFParameters;
import org.bouncycastle.crypto.agreement.kdf.DHKEKGenerator;
import org.bouncycastle.crypto.generators.HKDFBytesGenerator;
import org.bouncycastle.crypto.params.DESParameters;
import org.bouncycastle.crypto.params.HKDFParameters;
import org.bouncycastle.crypto.params.KDFParameters;
import org.bouncycastle.internal.asn1.gnu.GNUObjectIdentifiers;
import org.bouncycastle.internal.asn1.kisa.KISAObjectIdentifiers;
import org.bouncycastle.internal.asn1.misc.MiscObjectIdentifiers;
import org.bouncycastle.internal.asn1.ntt.NTTObjectIdentifiers;
import org.bouncycastle.internal.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.jcajce.spec.HybridValueParameterSpec;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Integers;
import org.bouncycastle.util.Strings;

public abstract class BaseAgreementSpi extends KeyAgreementSpi {
   private static final Map<String, ASN1ObjectIdentifier> defaultOids = new HashMap<>();
   private static final Map<String, Integer> keySizes = new HashMap<>();
   private static final Map<String, String> nameTable = new HashMap<>();
   private static final Hashtable oids = new Hashtable();
   private static final Hashtable des = new Hashtable();
   protected final String kaAlgorithm;
   protected final DerivationFunction kdf;
   protected byte[] ukmParameters;
   protected byte[] ukmParametersSalt;
   private HybridValueParameterSpec hybridSpec;

   public BaseAgreementSpi(String var1, DerivationFunction var2) {
      this.kaAlgorithm = var1;
      this.kdf = var2;
   }

   protected static String getAlgorithm(String var0) {
      if (var0.indexOf(91) > 0) {
         return var0.substring(0, var0.indexOf(91));
      } else if (var0.startsWith(NISTObjectIdentifiers.aes.getId())) {
         return "AES";
      } else if (var0.startsWith(GNUObjectIdentifiers.Serpent.getId())) {
         return "Serpent";
      } else {
         String var1 = nameTable.get(Strings.toUpperCase(var0));
         return var1 != null ? var1 : var0;
      }
   }

   protected static int getKeySize(String var0) {
      if (var0.indexOf(91) > 0) {
         return Integer.parseInt(var0.substring(var0.indexOf(91) + 1, var0.indexOf(93)));
      } else {
         String var1 = Strings.toUpperCase(var0);
         return !keySizes.containsKey(var1) ? -1 : keySizes.get(var1);
      }
   }

   protected static byte[] trimZeroes(byte[] var0) {
      if (var0[0] != 0) {
         return var0;
      } else {
         int var1 = 0;

         while (var1 < var0.length && var0[var1] == 0) {
            var1++;
         }

         byte[] var2 = new byte[var0.length - var1];
         System.arraycopy(var0, var1, var2, 0, var2.length);
         return var2;
      }
   }

   @Override
   protected void engineInit(Key var1, SecureRandom var2) throws InvalidKeyException {
      try {
         this.doInitFromKey(var1, null, var2);
      } catch (InvalidAlgorithmParameterException var4) {
         throw new InvalidKeyException(var4.getMessage());
      }
   }

   @Override
   protected void engineInit(Key var1, AlgorithmParameterSpec var2, SecureRandom var3) throws InvalidKeyException, InvalidAlgorithmParameterException {
      if (var2 instanceof HybridValueParameterSpec) {
         this.hybridSpec = (HybridValueParameterSpec)var2;
         this.doInitFromKey(var1, this.hybridSpec.getBaseParameterSpec(), var3);
      } else {
         this.hybridSpec = null;
         this.doInitFromKey(var1, var2, var3);
      }
   }

   @Override
   protected byte[] engineGenerateSecret() throws IllegalStateException {
      if (this.kdf != null) {
         byte[] var1 = this.calcSecret();

         try {
            return this.getSharedSecretBytes(var1, null, var1.length * 8);
         } catch (NoSuchAlgorithmException var3) {
            throw new IllegalStateException(var3.getMessage());
         }
      } else {
         return this.calcSecret();
      }
   }

   @Override
   protected int engineGenerateSecret(byte[] var1, int var2) throws IllegalStateException, ShortBufferException {
      byte[] var3 = this.engineGenerateSecret();
      if (var1.length - var2 < var3.length) {
         throw new ShortBufferException(this.kaAlgorithm + " key agreement: need " + var3.length + " bytes");
      } else {
         System.arraycopy(var3, 0, var1, var2, var3.length);
         return var3.length;
      }
   }

   @Override
   protected SecretKey engineGenerateSecret(String var1) throws NoSuchAlgorithmException {
      String var2 = Strings.toUpperCase(var1);
      String var3 = var1;
      if (oids.containsKey(var2)) {
         var3 = ((ASN1ObjectIdentifier)oids.get(var2)).getId();
      }

      int var4 = getKeySize(var3);
      byte[] var5 = this.getSharedSecretBytes(this.calcSecret(), var3, var4);
      String var6 = getAlgorithm(var1);
      if (des.containsKey(var6)) {
         DESParameters.setOddParity(var5);
      }

      return new SecretKeySpec(var5, var6);
   }

   private byte[] getSharedSecretBytes(byte[] var1, String var2, int var3) throws NoSuchAlgorithmException {
      if (this.kdf != null) {
         if (var3 < 0) {
            throw new NoSuchAlgorithmException("unknown algorithm encountered: " + var2);
         } else {
            byte[] var8 = new byte[var3 / 8];
            if (this.kdf instanceof DHKEKGenerator) {
               if (var2 == null) {
                  throw new NoSuchAlgorithmException("algorithm OID is null");
               }

               ASN1ObjectIdentifier var5;
               try {
                  var5 = new ASN1ObjectIdentifier(var2);
               } catch (IllegalArgumentException var7) {
                  throw new NoSuchAlgorithmException("no OID for algorithm: " + var2);
               }

               DHKDFParameters var6 = new DHKDFParameters(var5, var3, var1, this.ukmParameters);
               this.kdf.init(var6);
            } else if (this.kdf instanceof HKDFBytesGenerator) {
               this.kdf.init(new HKDFParameters(var1, this.ukmParametersSalt, this.ukmParameters));
            } else {
               KDFParameters var9 = new KDFParameters(var1, this.ukmParameters);
               this.kdf.init(var9);
            }

            this.kdf.generateBytes(var8, 0, var8.length);
            Arrays.clear(var1);
            return var8;
         }
      } else if (var3 > 0) {
         byte[] var4 = new byte[var3 / 8];
         System.arraycopy(var1, 0, var4, 0, var4.length);
         Arrays.clear(var1);
         return var4;
      } else {
         return var1;
      }
   }

   private byte[] calcSecret() {
      if (this.hybridSpec != null) {
         byte[] var1 = this.doCalcSecret();
         byte[] var2;
         if (this.hybridSpec.isPrependedT()) {
            var2 = Arrays.concatenate(this.hybridSpec.getT(), var1);
         } else {
            var2 = Arrays.concatenate(var1, this.hybridSpec.getT());
         }

         Arrays.clear(var1);
         return var2;
      } else {
         return this.doCalcSecret();
      }
   }

   protected abstract byte[] doCalcSecret();

   protected abstract void doInitFromKey(Key var1, AlgorithmParameterSpec var2, SecureRandom var3) throws InvalidKeyException, InvalidAlgorithmParameterException;

   static {
      Integer var0 = Integers.valueOf(64);
      Integer var1 = Integers.valueOf(128);
      Integer var2 = Integers.valueOf(192);
      Integer var3 = Integers.valueOf(256);
      keySizes.put("DES", var0);
      keySizes.put("DESEDE", var2);
      keySizes.put("BLOWFISH", var1);
      keySizes.put("AES", var3);
      keySizes.put(NISTObjectIdentifiers.id_aes128_ECB.getId(), var1);
      keySizes.put(NISTObjectIdentifiers.id_aes192_ECB.getId(), var2);
      keySizes.put(NISTObjectIdentifiers.id_aes256_ECB.getId(), var3);
      keySizes.put(NISTObjectIdentifiers.id_aes128_CBC.getId(), var1);
      keySizes.put(NISTObjectIdentifiers.id_aes192_CBC.getId(), var2);
      keySizes.put(NISTObjectIdentifiers.id_aes256_CBC.getId(), var3);
      keySizes.put(NISTObjectIdentifiers.id_aes128_CFB.getId(), var1);
      keySizes.put(NISTObjectIdentifiers.id_aes192_CFB.getId(), var2);
      keySizes.put(NISTObjectIdentifiers.id_aes256_CFB.getId(), var3);
      keySizes.put(NISTObjectIdentifiers.id_aes128_OFB.getId(), var1);
      keySizes.put(NISTObjectIdentifiers.id_aes192_OFB.getId(), var2);
      keySizes.put(NISTObjectIdentifiers.id_aes256_OFB.getId(), var3);
      keySizes.put(NISTObjectIdentifiers.id_aes128_wrap.getId(), var1);
      keySizes.put(NISTObjectIdentifiers.id_aes192_wrap.getId(), var2);
      keySizes.put(NISTObjectIdentifiers.id_aes256_wrap.getId(), var3);
      keySizes.put(NISTObjectIdentifiers.id_aes128_CCM.getId(), var1);
      keySizes.put(NISTObjectIdentifiers.id_aes192_CCM.getId(), var2);
      keySizes.put(NISTObjectIdentifiers.id_aes256_CCM.getId(), var3);
      keySizes.put(NISTObjectIdentifiers.id_aes128_GCM.getId(), var1);
      keySizes.put(NISTObjectIdentifiers.id_aes192_GCM.getId(), var2);
      keySizes.put(NISTObjectIdentifiers.id_aes256_GCM.getId(), var3);
      keySizes.put(NTTObjectIdentifiers.id_camellia128_wrap.getId(), var1);
      keySizes.put(NTTObjectIdentifiers.id_camellia192_wrap.getId(), var2);
      keySizes.put(NTTObjectIdentifiers.id_camellia256_wrap.getId(), var3);
      keySizes.put(KISAObjectIdentifiers.id_npki_app_cmsSeed_wrap.getId(), var1);
      keySizes.put(PKCSObjectIdentifiers.id_alg_CMS3DESwrap.getId(), var2);
      keySizes.put(PKCSObjectIdentifiers.des_EDE3_CBC.getId(), var2);
      keySizes.put(OIWObjectIdentifiers.desCBC.getId(), var0);
      keySizes.put(CryptoProObjectIdentifiers.gostR28147_gcfb.getId(), var3);
      keySizes.put(CryptoProObjectIdentifiers.id_Gost28147_89_None_KeyWrap.getId(), var3);
      keySizes.put(CryptoProObjectIdentifiers.id_Gost28147_89_CryptoPro_KeyWrap.getId(), var3);
      keySizes.put(PKCSObjectIdentifiers.id_hmacWithSHA1.getId(), Integers.valueOf(160));
      keySizes.put(PKCSObjectIdentifiers.id_hmacWithSHA256.getId(), var3);
      keySizes.put(PKCSObjectIdentifiers.id_hmacWithSHA384.getId(), Integers.valueOf(384));
      keySizes.put(PKCSObjectIdentifiers.id_hmacWithSHA512.getId(), Integers.valueOf(512));
      defaultOids.put("DESEDE", PKCSObjectIdentifiers.des_EDE3_CBC);
      defaultOids.put("AES", NISTObjectIdentifiers.id_aes256_CBC);
      defaultOids.put("CAMELLIA", NTTObjectIdentifiers.id_camellia256_cbc);
      defaultOids.put("SEED", KISAObjectIdentifiers.id_seedCBC);
      defaultOids.put("DES", OIWObjectIdentifiers.desCBC);
      nameTable.put(MiscObjectIdentifiers.cast5CBC.getId(), "CAST5");
      nameTable.put(MiscObjectIdentifiers.as_sys_sec_alg_ideaCBC.getId(), "IDEA");
      nameTable.put(MiscObjectIdentifiers.cryptlib_algorithm_blowfish_ECB.getId(), "Blowfish");
      nameTable.put(MiscObjectIdentifiers.cryptlib_algorithm_blowfish_CBC.getId(), "Blowfish");
      nameTable.put(MiscObjectIdentifiers.cryptlib_algorithm_blowfish_CFB.getId(), "Blowfish");
      nameTable.put(MiscObjectIdentifiers.cryptlib_algorithm_blowfish_OFB.getId(), "Blowfish");
      nameTable.put(OIWObjectIdentifiers.desECB.getId(), "DES");
      nameTable.put(OIWObjectIdentifiers.desCBC.getId(), "DES");
      nameTable.put(OIWObjectIdentifiers.desCFB.getId(), "DES");
      nameTable.put(OIWObjectIdentifiers.desOFB.getId(), "DES");
      nameTable.put(OIWObjectIdentifiers.desEDE.getId(), "DESede");
      nameTable.put(PKCSObjectIdentifiers.des_EDE3_CBC.getId(), "DESede");
      nameTable.put(PKCSObjectIdentifiers.id_alg_CMS3DESwrap.getId(), "DESede");
      nameTable.put(PKCSObjectIdentifiers.id_alg_CMSRC2wrap.getId(), "RC2");
      nameTable.put(PKCSObjectIdentifiers.id_hmacWithSHA1.getId(), "HmacSHA1");
      nameTable.put(PKCSObjectIdentifiers.id_hmacWithSHA224.getId(), "HmacSHA224");
      nameTable.put(PKCSObjectIdentifiers.id_hmacWithSHA256.getId(), "HmacSHA256");
      nameTable.put(PKCSObjectIdentifiers.id_hmacWithSHA384.getId(), "HmacSHA384");
      nameTable.put(PKCSObjectIdentifiers.id_hmacWithSHA512.getId(), "HmacSHA512");
      nameTable.put(NTTObjectIdentifiers.id_camellia128_cbc.getId(), "Camellia");
      nameTable.put(NTTObjectIdentifiers.id_camellia192_cbc.getId(), "Camellia");
      nameTable.put(NTTObjectIdentifiers.id_camellia256_cbc.getId(), "Camellia");
      nameTable.put(NTTObjectIdentifiers.id_camellia128_wrap.getId(), "Camellia");
      nameTable.put(NTTObjectIdentifiers.id_camellia192_wrap.getId(), "Camellia");
      nameTable.put(NTTObjectIdentifiers.id_camellia256_wrap.getId(), "Camellia");
      nameTable.put(KISAObjectIdentifiers.id_npki_app_cmsSeed_wrap.getId(), "SEED");
      nameTable.put(KISAObjectIdentifiers.id_seedCBC.getId(), "SEED");
      nameTable.put(KISAObjectIdentifiers.id_seedMAC.getId(), "SEED");
      nameTable.put(CryptoProObjectIdentifiers.gostR28147_gcfb.getId(), "GOST28147");
      nameTable.put(NISTObjectIdentifiers.id_aes128_wrap.getId(), "AES");
      nameTable.put(NISTObjectIdentifiers.id_aes128_CCM.getId(), "AES");
      nameTable.put(NISTObjectIdentifiers.id_aes128_CCM.getId(), "AES");
      oids.put("DESEDE", PKCSObjectIdentifiers.des_EDE3_CBC);
      oids.put("AES", NISTObjectIdentifiers.id_aes256_CBC);
      oids.put("DES", OIWObjectIdentifiers.desCBC);
      des.put("DES", "DES");
      des.put("DESEDE", "DES");
      des.put(OIWObjectIdentifiers.desCBC.getId(), "DES");
      des.put(PKCSObjectIdentifiers.des_EDE3_CBC.getId(), "DES");
      des.put(PKCSObjectIdentifiers.id_alg_CMS3DESwrap.getId(), "DES");
   }
}
