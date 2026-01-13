package org.bouncycastle.cert.crmf.jcajce;

import java.io.IOException;
import java.security.AlgorithmParameterGenerator;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.RC2ParameterSpec;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Null;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.iana.IANAObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.cert.crmf.CRMFException;
import org.bouncycastle.cms.CMSAlgorithm;
import org.bouncycastle.jcajce.util.AlgorithmParametersUtils;
import org.bouncycastle.jcajce.util.JcaJceHelper;

class CRMFHelper {
   protected static final Map BASE_CIPHER_NAMES = new HashMap();
   protected static final Map CIPHER_ALG_NAMES = new HashMap();
   protected static final Map DIGEST_ALG_NAMES = new HashMap();
   protected static final Map KEY_ALG_NAMES = new HashMap();
   protected static final Map MAC_ALG_NAMES = new HashMap();
   private JcaJceHelper helper;

   CRMFHelper(JcaJceHelper var1) {
      this.helper = var1;
   }

   PublicKey toPublicKey(SubjectPublicKeyInfo var1) throws CRMFException {
      try {
         X509EncodedKeySpec var2 = new X509EncodedKeySpec(var1.getEncoded());
         AlgorithmIdentifier var3 = var1.getAlgorithm();
         return this.createKeyFactory(var3.getAlgorithm()).generatePublic(var2);
      } catch (Exception var4) {
         throw new CRMFException("invalid key: " + var4.getMessage(), var4);
      }
   }

   Cipher createCipher(ASN1ObjectIdentifier var1) throws CRMFException {
      try {
         String var2 = (String)CIPHER_ALG_NAMES.get(var1);
         if (var2 != null) {
            try {
               return this.helper.createCipher(var2);
            } catch (NoSuchAlgorithmException var4) {
            }
         }

         return this.helper.createCipher(var1.getId());
      } catch (GeneralSecurityException var5) {
         throw new CRMFException("cannot create cipher: " + var5.getMessage(), var5);
      }
   }

   public KeyGenerator createKeyGenerator(ASN1ObjectIdentifier var1) throws CRMFException {
      try {
         String var2 = (String)BASE_CIPHER_NAMES.get(var1);
         if (var2 != null) {
            try {
               return this.helper.createKeyGenerator(var2);
            } catch (NoSuchAlgorithmException var4) {
            }
         }

         return this.helper.createKeyGenerator(var1.getId());
      } catch (GeneralSecurityException var5) {
         throw new CRMFException("cannot create key generator: " + var5.getMessage(), var5);
      }
   }

   Cipher createContentCipher(final Key var1, final AlgorithmIdentifier var2) throws CRMFException {
      return (Cipher)execute(
         new CRMFHelper.JCECallback() {
            @Override
            public Object doInJCE() throws CRMFException, InvalidAlgorithmParameterException, InvalidKeyException, InvalidParameterSpecException, NoSuchAlgorithmException, NoSuchPaddingException, NoSuchProviderException {
               Cipher var1x = CRMFHelper.this.createCipher(var2.getAlgorithm());
               ASN1Primitive var2x = (ASN1Primitive)var2.getParameters();
               ASN1ObjectIdentifier var3 = var2.getAlgorithm();
               if (var2x != null && !(var2x instanceof ASN1Null)) {
                  try {
                     AlgorithmParameters var4 = CRMFHelper.this.createAlgorithmParameters(var2.getAlgorithm());

                     try {
                        AlgorithmParametersUtils.loadParameters(var4, var2x);
                     } catch (IOException var6) {
                        throw new CRMFException("error decoding algorithm parameters.", var6);
                     }

                     var1x.init(2, var1, var4);
                  } catch (NoSuchAlgorithmException var7) {
                     if (!var3.equals(CMSAlgorithm.DES_EDE3_CBC)
                        && !var3.equals(CMSAlgorithm.IDEA_CBC)
                        && !var3.equals(CMSAlgorithm.AES128_CBC)
                        && !var3.equals(CMSAlgorithm.AES192_CBC)
                        && !var3.equals(CMSAlgorithm.AES256_CBC)) {
                        throw var7;
                     }

                     var1x.init(2, var1, new IvParameterSpec(ASN1OctetString.getInstance(var2x).getOctets()));
                  }
               } else if (!var3.equals(CMSAlgorithm.DES_EDE3_CBC) && !var3.equals(CMSAlgorithm.IDEA_CBC) && !var3.equals(CMSAlgorithm.CAST5_CBC)) {
                  var1x.init(2, var1);
               } else {
                  var1x.init(2, var1, new IvParameterSpec(new byte[8]));
               }

               return var1x;
            }
         }
      );
   }

   AlgorithmParameters createAlgorithmParameters(ASN1ObjectIdentifier var1) throws NoSuchAlgorithmException, NoSuchProviderException {
      String var2 = (String)BASE_CIPHER_NAMES.get(var1);
      if (var2 != null) {
         try {
            return this.helper.createAlgorithmParameters(var2);
         } catch (NoSuchAlgorithmException var4) {
         }
      }

      return this.helper.createAlgorithmParameters(var1.getId());
   }

   KeyFactory createKeyFactory(ASN1ObjectIdentifier var1) throws CRMFException {
      try {
         String var2 = (String)KEY_ALG_NAMES.get(var1);
         if (var2 != null) {
            try {
               return this.helper.createKeyFactory(var2);
            } catch (NoSuchAlgorithmException var4) {
            }
         }

         return this.helper.createKeyFactory(var1.getId());
      } catch (GeneralSecurityException var5) {
         throw new CRMFException("cannot create cipher: " + var5.getMessage(), var5);
      }
   }

   MessageDigest createDigest(ASN1ObjectIdentifier var1) throws CRMFException {
      try {
         String var2 = (String)DIGEST_ALG_NAMES.get(var1);
         if (var2 != null) {
            try {
               return this.helper.createMessageDigest(var2);
            } catch (NoSuchAlgorithmException var4) {
            }
         }

         return this.helper.createMessageDigest(var1.getId());
      } catch (GeneralSecurityException var5) {
         throw new CRMFException("cannot create cipher: " + var5.getMessage(), var5);
      }
   }

   Mac createMac(ASN1ObjectIdentifier var1) throws CRMFException {
      try {
         String var2 = (String)MAC_ALG_NAMES.get(var1);
         if (var2 != null) {
            try {
               return this.helper.createMac(var2);
            } catch (NoSuchAlgorithmException var4) {
            }
         }

         return this.helper.createMac(var1.getId());
      } catch (GeneralSecurityException var5) {
         throw new CRMFException("cannot create mac: " + var5.getMessage(), var5);
      }
   }

   AlgorithmParameterGenerator createAlgorithmParameterGenerator(ASN1ObjectIdentifier var1) throws GeneralSecurityException {
      String var2 = (String)BASE_CIPHER_NAMES.get(var1);
      if (var2 != null) {
         try {
            return this.helper.createAlgorithmParameterGenerator(var2);
         } catch (NoSuchAlgorithmException var4) {
         }
      }

      return this.helper.createAlgorithmParameterGenerator(var1.getId());
   }

   AlgorithmParameters generateParameters(ASN1ObjectIdentifier var1, SecretKey var2, SecureRandom var3) throws CRMFException {
      try {
         AlgorithmParameterGenerator var4 = this.createAlgorithmParameterGenerator(var1);
         if (var1.equals(CMSAlgorithm.RC2_CBC)) {
            byte[] var5 = new byte[8];
            var3.nextBytes(var5);

            try {
               var4.init(new RC2ParameterSpec(var2.getEncoded().length * 8, var5), var3);
            } catch (InvalidAlgorithmParameterException var7) {
               throw new CRMFException("parameters generation error: " + var7, var7);
            }
         }

         return var4.generateParameters();
      } catch (NoSuchAlgorithmException var8) {
         return null;
      } catch (GeneralSecurityException var9) {
         throw new CRMFException("exception creating algorithm parameter generator: " + var9, var9);
      }
   }

   AlgorithmIdentifier getAlgorithmIdentifier(ASN1ObjectIdentifier var1, AlgorithmParameters var2) throws CRMFException {
      Object var3;
      if (var2 != null) {
         try {
            var3 = AlgorithmParametersUtils.extractParameters(var2);
         } catch (IOException var5) {
            throw new CRMFException("cannot encode parameters: " + var5.getMessage(), var5);
         }
      } else {
         var3 = DERNull.INSTANCE;
      }

      return new AlgorithmIdentifier(var1, (ASN1Encodable)var3);
   }

   static Object execute(CRMFHelper.JCECallback var0) throws CRMFException {
      try {
         return var0.doInJCE();
      } catch (NoSuchAlgorithmException var2) {
         throw new CRMFException("can't find algorithm.", var2);
      } catch (InvalidKeyException var3) {
         throw new CRMFException("key invalid in message.", var3);
      } catch (NoSuchProviderException var4) {
         throw new CRMFException("can't find provider.", var4);
      } catch (NoSuchPaddingException var5) {
         throw new CRMFException("required padding not supported.", var5);
      } catch (InvalidAlgorithmParameterException var6) {
         throw new CRMFException("algorithm parameters invalid.", var6);
      } catch (InvalidParameterSpecException var7) {
         throw new CRMFException("MAC algorithm parameter spec invalid.", var7);
      }
   }

   static {
      BASE_CIPHER_NAMES.put(PKCSObjectIdentifiers.des_EDE3_CBC, "DESEDE");
      BASE_CIPHER_NAMES.put(NISTObjectIdentifiers.id_aes128_CBC, "AES");
      BASE_CIPHER_NAMES.put(NISTObjectIdentifiers.id_aes192_CBC, "AES");
      BASE_CIPHER_NAMES.put(NISTObjectIdentifiers.id_aes256_CBC, "AES");
      CIPHER_ALG_NAMES.put(CMSAlgorithm.DES_EDE3_CBC, "DESEDE/CBC/PKCS5Padding");
      CIPHER_ALG_NAMES.put(CMSAlgorithm.AES128_CBC, "AES/CBC/PKCS5Padding");
      CIPHER_ALG_NAMES.put(CMSAlgorithm.AES192_CBC, "AES/CBC/PKCS5Padding");
      CIPHER_ALG_NAMES.put(CMSAlgorithm.AES256_CBC, "AES/CBC/PKCS5Padding");
      CIPHER_ALG_NAMES.put(new ASN1ObjectIdentifier(PKCSObjectIdentifiers.rsaEncryption.getId()), "RSA/ECB/PKCS1Padding");
      DIGEST_ALG_NAMES.put(OIWObjectIdentifiers.idSHA1, "SHA1");
      DIGEST_ALG_NAMES.put(NISTObjectIdentifiers.id_sha224, "SHA224");
      DIGEST_ALG_NAMES.put(NISTObjectIdentifiers.id_sha256, "SHA256");
      DIGEST_ALG_NAMES.put(NISTObjectIdentifiers.id_sha384, "SHA384");
      DIGEST_ALG_NAMES.put(NISTObjectIdentifiers.id_sha512, "SHA512");
      MAC_ALG_NAMES.put(IANAObjectIdentifiers.hmacSHA1, "HMACSHA1");
      MAC_ALG_NAMES.put(PKCSObjectIdentifiers.id_hmacWithSHA1, "HMACSHA1");
      MAC_ALG_NAMES.put(PKCSObjectIdentifiers.id_hmacWithSHA224, "HMACSHA224");
      MAC_ALG_NAMES.put(PKCSObjectIdentifiers.id_hmacWithSHA256, "HMACSHA256");
      MAC_ALG_NAMES.put(PKCSObjectIdentifiers.id_hmacWithSHA384, "HMACSHA384");
      MAC_ALG_NAMES.put(PKCSObjectIdentifiers.id_hmacWithSHA512, "HMACSHA512");
      KEY_ALG_NAMES.put(PKCSObjectIdentifiers.rsaEncryption, "RSA");
      KEY_ALG_NAMES.put(X9ObjectIdentifiers.id_dsa, "DSA");
   }

   interface JCECallback {
      Object doInJCE() throws CRMFException, InvalidAlgorithmParameterException, InvalidKeyException, InvalidParameterSpecException, NoSuchAlgorithmException, NoSuchPaddingException, NoSuchProviderException;
   }
}
