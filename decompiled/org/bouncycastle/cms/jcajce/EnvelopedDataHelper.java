package org.bouncycastle.cms.jcajce;

import java.io.IOException;
import java.security.AlgorithmParameterGenerator;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidParameterSpecException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.RC2ParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Null;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PBKDF2Params;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.RC2CBCParameter;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.CMSAlgorithm;
import org.bouncycastle.cms.CMSEnvelopedDataGenerator;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.PasswordRecipient;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.generators.HKDFBytesGenerator;
import org.bouncycastle.crypto.params.HKDFParameters;
import org.bouncycastle.operator.AsymmetricKeyUnwrapper;
import org.bouncycastle.operator.DefaultSecretKeySizeProvider;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.SecretKeySizeProvider;
import org.bouncycastle.operator.SymmetricKeyUnwrapper;
import org.bouncycastle.operator.jcajce.JceAsymmetricKeyUnwrapper;
import org.bouncycastle.operator.jcajce.JceKTSKeyUnwrapper;
import org.bouncycastle.util.Strings;

public class EnvelopedDataHelper {
   protected static final SecretKeySizeProvider KEY_SIZE_PROVIDER = DefaultSecretKeySizeProvider.INSTANCE;
   private static final byte[] hkdfSalt = Strings.toByteArray("The Cryptographic Message Syntax");
   private static final Set authEnvelopedAlgorithms = new HashSet();
   protected static final Map BASE_CIPHER_NAMES = new HashMap();
   protected static final Map CIPHER_ALG_NAMES = new HashMap();
   protected static final Map MAC_ALG_NAMES = new HashMap();
   private static final Map PBKDF2_ALG_NAMES = new HashMap();
   private static final short[] rc2Table = new short[]{
      189,
      86,
      234,
      242,
      162,
      241,
      172,
      42,
      176,
      147,
      209,
      156,
      27,
      51,
      253,
      208,
      48,
      4,
      182,
      220,
      125,
      223,
      50,
      75,
      247,
      203,
      69,
      155,
      49,
      187,
      33,
      90,
      65,
      159,
      225,
      217,
      74,
      77,
      158,
      218,
      160,
      104,
      44,
      195,
      39,
      95,
      128,
      54,
      62,
      238,
      251,
      149,
      26,
      254,
      206,
      168,
      52,
      169,
      19,
      240,
      166,
      63,
      216,
      12,
      120,
      36,
      175,
      35,
      82,
      193,
      103,
      23,
      245,
      102,
      144,
      231,
      232,
      7,
      184,
      96,
      72,
      230,
      30,
      83,
      243,
      146,
      164,
      114,
      140,
      8,
      21,
      110,
      134,
      0,
      132,
      250,
      244,
      127,
      138,
      66,
      25,
      246,
      219,
      205,
      20,
      141,
      80,
      18,
      186,
      60,
      6,
      78,
      236,
      179,
      53,
      17,
      161,
      136,
      142,
      43,
      148,
      153,
      183,
      113,
      116,
      211,
      228,
      191,
      58,
      222,
      150,
      14,
      188,
      10,
      237,
      119,
      252,
      55,
      107,
      3,
      121,
      137,
      98,
      198,
      215,
      192,
      210,
      124,
      106,
      139,
      34,
      163,
      91,
      5,
      93,
      2,
      117,
      213,
      97,
      227,
      24,
      143,
      85,
      81,
      173,
      31,
      11,
      94,
      133,
      229,
      194,
      87,
      99,
      202,
      61,
      108,
      180,
      197,
      204,
      112,
      178,
      145,
      89,
      13,
      71,
      32,
      200,
      79,
      88,
      224,
      1,
      226,
      22,
      56,
      196,
      111,
      59,
      15,
      101,
      70,
      190,
      126,
      45,
      123,
      130,
      249,
      64,
      181,
      29,
      115,
      248,
      235,
      38,
      199,
      135,
      151,
      37,
      84,
      177,
      40,
      170,
      152,
      157,
      165,
      100,
      109,
      122,
      212,
      16,
      129,
      68,
      239,
      73,
      214,
      174,
      46,
      221,
      118,
      92,
      47,
      167,
      28,
      201,
      9,
      105,
      154,
      131,
      207,
      41,
      57,
      185,
      233,
      76,
      255,
      67,
      171
   };
   private static final short[] rc2Ekb = new short[]{
      93,
      190,
      155,
      139,
      17,
      153,
      110,
      77,
      89,
      243,
      133,
      166,
      63,
      183,
      131,
      197,
      228,
      115,
      107,
      58,
      104,
      90,
      192,
      71,
      160,
      100,
      52,
      12,
      241,
      208,
      82,
      165,
      185,
      30,
      150,
      67,
      65,
      216,
      212,
      44,
      219,
      248,
      7,
      119,
      42,
      202,
      235,
      239,
      16,
      28,
      22,
      13,
      56,
      114,
      47,
      137,
      193,
      249,
      128,
      196,
      109,
      174,
      48,
      61,
      206,
      32,
      99,
      254,
      230,
      26,
      199,
      184,
      80,
      232,
      36,
      23,
      252,
      37,
      111,
      187,
      106,
      163,
      68,
      83,
      217,
      162,
      1,
      171,
      188,
      182,
      31,
      152,
      238,
      154,
      167,
      45,
      79,
      158,
      142,
      172,
      224,
      198,
      73,
      70,
      41,
      244,
      148,
      138,
      175,
      225,
      91,
      195,
      179,
      123,
      87,
      209,
      124,
      156,
      237,
      135,
      64,
      140,
      226,
      203,
      147,
      20,
      201,
      97,
      46,
      229,
      204,
      246,
      94,
      168,
      92,
      214,
      117,
      141,
      98,
      149,
      88,
      105,
      118,
      161,
      74,
      181,
      85,
      9,
      120,
      51,
      130,
      215,
      221,
      121,
      245,
      27,
      11,
      222,
      38,
      33,
      40,
      116,
      4,
      151,
      86,
      223,
      60,
      240,
      55,
      57,
      220,
      255,
      6,
      164,
      234,
      66,
      8,
      218,
      180,
      113,
      176,
      207,
      18,
      122,
      78,
      250,
      108,
      29,
      132,
      0,
      200,
      127,
      145,
      69,
      170,
      43,
      194,
      177,
      143,
      213,
      186,
      242,
      173,
      25,
      178,
      103,
      54,
      247,
      15,
      10,
      146,
      125,
      227,
      157,
      233,
      144,
      62,
      35,
      39,
      102,
      19,
      236,
      129,
      21,
      189,
      34,
      191,
      159,
      126,
      169,
      81,
      75,
      76,
      251,
      2,
      211,
      112,
      134,
      49,
      231,
      59,
      5,
      3,
      84,
      96,
      72,
      101,
      24,
      210,
      205,
      95,
      50,
      136,
      14,
      53,
      253
   };
   private JcaJceExtHelper helper;

   EnvelopedDataHelper(JcaJceExtHelper var1) {
      this.helper = var1;
   }

   String getBaseCipherName(ASN1ObjectIdentifier var1) {
      String var2 = (String)BASE_CIPHER_NAMES.get(var1);
      return var2 == null ? var1.getId() : var2;
   }

   Key getJceKey(GenericKey var1) {
      if (var1.getRepresentation() instanceof Key) {
         return (Key)var1.getRepresentation();
      } else if (var1.getRepresentation() instanceof byte[]) {
         return new SecretKeySpec((byte[])var1.getRepresentation(), "ENC");
      } else {
         throw new IllegalArgumentException("unknown generic key type");
      }
   }

   public Key getJceKey(ASN1ObjectIdentifier var1, GenericKey var2) {
      if (var2.getRepresentation() instanceof Key) {
         return (Key)var2.getRepresentation();
      } else if (var2.getRepresentation() instanceof byte[]) {
         return new SecretKeySpec((byte[])var2.getRepresentation(), this.getBaseCipherName(var1));
      } else {
         throw new IllegalArgumentException("unknown generic key type");
      }
   }

   public Key getJceKey(AlgorithmIdentifier var1, GenericKey var2) throws CMSException {
      if (var1.getAlgorithm().equals(CMSObjectIdentifiers.id_alg_cek_hkdf_sha256)) {
         byte[] var3 = null;
         if (var2.getRepresentation() instanceof Key) {
            var3 = ((Key)var2.getRepresentation()).getEncoded();
         }

         if (var2.getRepresentation() instanceof byte[]) {
            var3 = (byte[])var2.getRepresentation();
         }

         AlgorithmIdentifier var4 = AlgorithmIdentifier.getInstance(var1.getParameters());
         HKDFBytesGenerator var5 = new HKDFBytesGenerator(new SHA256Digest());

         try {
            var5.init(new HKDFParameters(var3, hkdfSalt, var4.getEncoded("DER")));
         } catch (IOException var7) {
            throw new CMSException("unable to encode enc algorithm parameters", var7);
         }

         var5.generateBytes(var3, 0, var3.length);
         return new SecretKeySpec(var3, this.getBaseCipherName(var4.getAlgorithm()));
      } else {
         return this.getJceKey(var1.getAlgorithm(), var2);
      }
   }

   public void keySizeCheck(AlgorithmIdentifier var1, Key var2) throws CMSException {
      int var3 = KEY_SIZE_PROVIDER.getKeySize(var1);
      if (var3 > 0) {
         byte[] var4 = null;

         try {
            var4 = var2.getEncoded();
         } catch (Exception var6) {
         }

         if (var4 != null && var4.length * 8 != var3) {
            throw new CMSException("Expected key size for algorithm OID not found in recipient.");
         }
      }
   }

   Cipher createCipher(ASN1ObjectIdentifier var1) throws CMSException {
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
         throw new CMSException("cannot create cipher: " + var5.getMessage(), var5);
      }
   }

   Mac createMac(ASN1ObjectIdentifier var1) throws CMSException {
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
         throw new CMSException("cannot create mac: " + var5.getMessage(), var5);
      }
   }

   Cipher createRFC3211Wrapper(ASN1ObjectIdentifier var1) throws CMSException {
      String var2 = (String)BASE_CIPHER_NAMES.get(var1);
      if (var2 == null) {
         throw new CMSException("no name for " + var1);
      } else {
         var2 = var2 + "RFC3211Wrap";

         try {
            return this.helper.createCipher(var2);
         } catch (GeneralSecurityException var4) {
            throw new CMSException("cannot create cipher: " + var4.getMessage(), var4);
         }
      }
   }

   KeyAgreement createKeyAgreement(ASN1ObjectIdentifier var1) throws CMSException {
      try {
         String var2 = (String)BASE_CIPHER_NAMES.get(var1);
         if (var2 != null) {
            try {
               return this.helper.createKeyAgreement(var2);
            } catch (NoSuchAlgorithmException var4) {
            }
         }

         return this.helper.createKeyAgreement(var1.getId());
      } catch (GeneralSecurityException var5) {
         throw new CMSException("cannot create key agreement: " + var5.getMessage(), var5);
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

   public Cipher createContentCipher(final Key var1, final AlgorithmIdentifier var2) throws CMSException {
      return (Cipher)execute(
         new EnvelopedDataHelper.JCECallback() {
            @Override
            public Object doInJCE() throws CMSException, InvalidAlgorithmParameterException, InvalidKeyException, InvalidParameterSpecException, NoSuchAlgorithmException, NoSuchPaddingException, NoSuchProviderException {
               AlgorithmIdentifier var1x;
               if (var2.getAlgorithm().equals(CMSObjectIdentifiers.id_alg_cek_hkdf_sha256)) {
                  var1x = AlgorithmIdentifier.getInstance(var2.getParameters());
               } else {
                  var1x = var2;
               }

               Cipher var2x = EnvelopedDataHelper.this.createCipher(var1x.getAlgorithm());
               ASN1Encodable var3 = var1x.getParameters();
               String var4 = var1x.getAlgorithm().getId();
               if (var3 != null && !(var3 instanceof ASN1Null)) {
                  try {
                     AlgorithmParameters var5 = EnvelopedDataHelper.this.createAlgorithmParameters(var1x.getAlgorithm());
                     CMSUtils.loadParameters(var5, var3);
                     var2x.init(2, var1, var5);
                  } catch (NoSuchAlgorithmException var6) {
                     if (!var4.equals(CMSAlgorithm.DES_CBC.getId())
                        && !var4.equals(CMSEnvelopedDataGenerator.DES_EDE3_CBC)
                        && !var4.equals(CMSEnvelopedDataGenerator.IDEA_CBC)
                        && !var4.equals(CMSEnvelopedDataGenerator.AES128_CBC)
                        && !var4.equals(CMSEnvelopedDataGenerator.AES192_CBC)
                        && !var4.equals(CMSEnvelopedDataGenerator.AES256_CBC)) {
                        throw var6;
                     }

                     var2x.init(2, var1, new IvParameterSpec(ASN1OctetString.getInstance(var3).getOctets()));
                  }
               } else if (!var4.equals(CMSAlgorithm.DES_CBC.getId())
                  && !var4.equals(CMSEnvelopedDataGenerator.DES_EDE3_CBC)
                  && !var4.equals(CMSEnvelopedDataGenerator.IDEA_CBC)
                  && !var4.equals(CMSEnvelopedDataGenerator.CAST5_CBC)) {
                  var2x.init(2, var1);
               } else {
                  var2x.init(2, var1, new IvParameterSpec(new byte[8]));
               }

               return var2x;
            }
         }
      );
   }

   Mac createContentMac(final Key var1, final AlgorithmIdentifier var2) throws CMSException {
      return (Mac)execute(
         new EnvelopedDataHelper.JCECallback() {
            @Override
            public Object doInJCE() throws CMSException, InvalidAlgorithmParameterException, InvalidKeyException, InvalidParameterSpecException, NoSuchAlgorithmException, NoSuchPaddingException, NoSuchProviderException {
               Mac var1x = EnvelopedDataHelper.this.createMac(var2.getAlgorithm());
               ASN1Encodable var2x = var2.getParameters();
               if (var2x != null && !(var2x instanceof ASN1Null)) {
                  try {
                     AlgorithmParameters var3 = EnvelopedDataHelper.this.createAlgorithmParameters(var2.getAlgorithm());
                     CMSUtils.loadParameters(var3, var2x);
                     var1x.init(var1, var3.getParameterSpec(AlgorithmParameterSpec.class));
                  } catch (NoSuchAlgorithmException var4) {
                     throw var4;
                  }
               } else {
                  var1x.init(var1);
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

   KeyPairGenerator createKeyPairGenerator(ASN1ObjectIdentifier var1) throws CMSException {
      try {
         String var2 = (String)BASE_CIPHER_NAMES.get(var1);
         if (var2 != null) {
            try {
               return this.helper.createKeyPairGenerator(var2);
            } catch (NoSuchAlgorithmException var4) {
            }
         }

         return this.helper.createKeyPairGenerator(var1.getId());
      } catch (GeneralSecurityException var5) {
         throw new CMSException("cannot create key pair generator: " + var5.getMessage(), var5);
      }
   }

   public KeyGenerator createKeyGenerator(ASN1ObjectIdentifier var1) throws CMSException {
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
         throw new CMSException("cannot create key generator: " + var5.getMessage(), var5);
      }
   }

   AlgorithmParameters generateParameters(ASN1ObjectIdentifier var1, SecretKey var2, SecureRandom var3) throws CMSException {
      try {
         AlgorithmParameterGenerator var4 = this.createAlgorithmParameterGenerator(var1);
         if (var1.equals(CMSAlgorithm.RC2_CBC)) {
            byte[] var5 = new byte[8];
            var3.nextBytes(var5);

            try {
               var4.init(new RC2ParameterSpec(var2.getEncoded().length * 8, var5), var3);
            } catch (InvalidAlgorithmParameterException var7) {
               throw new CMSException("parameters generation error: " + var7, var7);
            }
         }

         return var4.generateParameters();
      } catch (NoSuchAlgorithmException var8) {
         return null;
      } catch (GeneralSecurityException var9) {
         throw new CMSException("exception creating algorithm parameter generator: " + var9, var9);
      }
   }

   AlgorithmIdentifier getAlgorithmIdentifier(ASN1ObjectIdentifier var1, AlgorithmParameters var2) throws CMSException {
      Object var3;
      if (var2 != null) {
         var3 = CMSUtils.extractParameters(var2);
      } else {
         var3 = DERNull.INSTANCE;
      }

      return new AlgorithmIdentifier(var1, (ASN1Encodable)var3);
   }

   static Object execute(EnvelopedDataHelper.JCECallback var0) throws CMSException {
      try {
         return var0.doInJCE();
      } catch (NoSuchAlgorithmException var2) {
         throw new CMSException("can't find algorithm.", var2);
      } catch (InvalidKeyException var3) {
         throw new CMSException("key invalid in message.", var3);
      } catch (NoSuchProviderException var4) {
         throw new CMSException("can't find provider.", var4);
      } catch (NoSuchPaddingException var5) {
         throw new CMSException("required padding not supported.", var5);
      } catch (InvalidAlgorithmParameterException var6) {
         throw new CMSException("algorithm parameters invalid.", var6);
      } catch (InvalidParameterSpecException var7) {
         throw new CMSException("MAC algorithm parameter spec invalid.", var7);
      }
   }

   public KeyFactory createKeyFactory(ASN1ObjectIdentifier var1) throws CMSException {
      try {
         String var2 = (String)BASE_CIPHER_NAMES.get(var1);
         if (var2 != null) {
            try {
               return this.helper.createKeyFactory(var2);
            } catch (NoSuchAlgorithmException var4) {
            }
         }

         return this.helper.createKeyFactory(var1.getId());
      } catch (GeneralSecurityException var5) {
         throw new CMSException("cannot create key factory: " + var5.getMessage(), var5);
      }
   }

   public JceAsymmetricKeyUnwrapper createAsymmetricUnwrapper(AlgorithmIdentifier var1, PrivateKey var2) {
      var2 = CMSUtils.cleanPrivateKey(var2);
      return this.helper.createAsymmetricUnwrapper(var1, var2);
   }

   public JceKTSKeyUnwrapper createAsymmetricUnwrapper(AlgorithmIdentifier var1, PrivateKey var2, byte[] var3, byte[] var4) {
      var2 = CMSUtils.cleanPrivateKey(var2);
      return this.helper.createAsymmetricUnwrapper(var1, var2, var3, var4);
   }

   public SymmetricKeyUnwrapper createSymmetricUnwrapper(AlgorithmIdentifier var1, SecretKey var2) {
      return this.helper.createSymmetricUnwrapper(var1, var2);
   }

   public AsymmetricKeyUnwrapper createKEMUnwrapper(AlgorithmIdentifier var1, PrivateKey var2) {
      var2 = CMSUtils.cleanPrivateKey(var2);
      return this.helper.createKEMUnwrapper(var1, var2);
   }

   public AlgorithmIdentifier getAlgorithmIdentifier(ASN1ObjectIdentifier var1, AlgorithmParameterSpec var2) {
      if (var2 instanceof IvParameterSpec) {
         return new AlgorithmIdentifier(var1, new DEROctetString(((IvParameterSpec)var2).getIV()));
      } else if (var2 instanceof RC2ParameterSpec) {
         RC2ParameterSpec var3 = (RC2ParameterSpec)var2;
         int var4 = ((RC2ParameterSpec)var2).getEffectiveKeyBits();
         if (var4 != -1) {
            int var5;
            if (var4 < 256) {
               var5 = rc2Table[var4];
            } else {
               var5 = var4;
            }

            return new AlgorithmIdentifier(var1, new RC2CBCParameter(var5, var3.getIV()));
         } else {
            return new AlgorithmIdentifier(var1, new RC2CBCParameter(var3.getIV()));
         }
      } else {
         throw new IllegalStateException("unknown parameter spec: " + var2);
      }
   }

   SecretKeyFactory createSecretKeyFactory(String var1) throws NoSuchProviderException, NoSuchAlgorithmException {
      return this.helper.createSecretKeyFactory(var1);
   }

   byte[] calculateDerivedKey(int var1, char[] var2, AlgorithmIdentifier var3, int var4) throws CMSException {
      PBKDF2Params var5 = PBKDF2Params.getInstance(var3.getParameters());

      try {
         SecretKeyFactory var6;
         if (var1 == 0) {
            var6 = this.helper.createSecretKeyFactory("PBKDF2with8BIT");
         } else {
            var6 = this.helper.createSecretKeyFactory((String)PBKDF2_ALG_NAMES.get(var5.getPrf()));
         }

         SecretKey var7 = var6.generateSecret(new PBEKeySpec(var2, var5.getSalt(), var5.getIterationCount().intValue(), var4));
         return var7.getEncoded();
      } catch (GeneralSecurityException var8) {
         throw new CMSException("Unable to calculate derived key from password: " + var8.getMessage(), var8);
      }
   }

   boolean isAuthEnveloped(ASN1ObjectIdentifier var1) {
      return authEnvelopedAlgorithms.contains(var1);
   }

   static {
      BASE_CIPHER_NAMES.put(CMSAlgorithm.DES_CBC, "DES");
      BASE_CIPHER_NAMES.put(CMSAlgorithm.DES_EDE3_CBC, "DESEDE");
      BASE_CIPHER_NAMES.put(CMSAlgorithm.AES128_CBC, "AES");
      BASE_CIPHER_NAMES.put(CMSAlgorithm.AES192_CBC, "AES");
      BASE_CIPHER_NAMES.put(CMSAlgorithm.AES256_CBC, "AES");
      BASE_CIPHER_NAMES.put(CMSAlgorithm.RC2_CBC, "RC2");
      BASE_CIPHER_NAMES.put(CMSAlgorithm.CAST5_CBC, "CAST5");
      BASE_CIPHER_NAMES.put(CMSAlgorithm.CAMELLIA128_CBC, "Camellia");
      BASE_CIPHER_NAMES.put(CMSAlgorithm.CAMELLIA192_CBC, "Camellia");
      BASE_CIPHER_NAMES.put(CMSAlgorithm.CAMELLIA256_CBC, "Camellia");
      BASE_CIPHER_NAMES.put(CMSAlgorithm.SEED_CBC, "SEED");
      BASE_CIPHER_NAMES.put(PKCSObjectIdentifiers.rc4, "RC4");
      BASE_CIPHER_NAMES.put(CryptoProObjectIdentifiers.gostR28147_gcfb, "GOST28147");
      CIPHER_ALG_NAMES.put(CMSAlgorithm.DES_CBC, "DES/CBC/PKCS5Padding");
      CIPHER_ALG_NAMES.put(CMSAlgorithm.RC2_CBC, "RC2/CBC/PKCS5Padding");
      CIPHER_ALG_NAMES.put(CMSAlgorithm.DES_EDE3_CBC, "DESEDE/CBC/PKCS5Padding");
      CIPHER_ALG_NAMES.put(CMSAlgorithm.AES128_CBC, "AES/CBC/PKCS5Padding");
      CIPHER_ALG_NAMES.put(CMSAlgorithm.AES192_CBC, "AES/CBC/PKCS5Padding");
      CIPHER_ALG_NAMES.put(CMSAlgorithm.AES256_CBC, "AES/CBC/PKCS5Padding");
      CIPHER_ALG_NAMES.put(PKCSObjectIdentifiers.rsaEncryption, "RSA/ECB/PKCS1Padding");
      CIPHER_ALG_NAMES.put(CMSAlgorithm.CAST5_CBC, "CAST5/CBC/PKCS5Padding");
      CIPHER_ALG_NAMES.put(CMSAlgorithm.CAMELLIA128_CBC, "Camellia/CBC/PKCS5Padding");
      CIPHER_ALG_NAMES.put(CMSAlgorithm.CAMELLIA192_CBC, "Camellia/CBC/PKCS5Padding");
      CIPHER_ALG_NAMES.put(CMSAlgorithm.CAMELLIA256_CBC, "Camellia/CBC/PKCS5Padding");
      CIPHER_ALG_NAMES.put(CMSAlgorithm.SEED_CBC, "SEED/CBC/PKCS5Padding");
      CIPHER_ALG_NAMES.put(PKCSObjectIdentifiers.rc4, "RC4");
      MAC_ALG_NAMES.put(CMSAlgorithm.DES_EDE3_CBC, "DESEDEMac");
      MAC_ALG_NAMES.put(CMSAlgorithm.AES128_CBC, "AESMac");
      MAC_ALG_NAMES.put(CMSAlgorithm.AES192_CBC, "AESMac");
      MAC_ALG_NAMES.put(CMSAlgorithm.AES256_CBC, "AESMac");
      MAC_ALG_NAMES.put(CMSAlgorithm.RC2_CBC, "RC2Mac");
      MAC_ALG_NAMES.put(CMSAlgorithm.ChaCha20Poly1305, "ChaCha20Poly1305Mac");
      PBKDF2_ALG_NAMES.put(PasswordRecipient.PRF.HMacSHA1.getAlgorithmID(), "PBKDF2WITHHMACSHA1");
      PBKDF2_ALG_NAMES.put(PasswordRecipient.PRF.HMacSHA224.getAlgorithmID(), "PBKDF2WITHHMACSHA224");
      PBKDF2_ALG_NAMES.put(PasswordRecipient.PRF.HMacSHA256.getAlgorithmID(), "PBKDF2WITHHMACSHA256");
      PBKDF2_ALG_NAMES.put(PasswordRecipient.PRF.HMacSHA384.getAlgorithmID(), "PBKDF2WITHHMACSHA384");
      PBKDF2_ALG_NAMES.put(PasswordRecipient.PRF.HMacSHA512.getAlgorithmID(), "PBKDF2WITHHMACSHA512");
      authEnvelopedAlgorithms.add(CMSAlgorithm.AES128_GCM);
      authEnvelopedAlgorithms.add(CMSAlgorithm.AES192_GCM);
      authEnvelopedAlgorithms.add(CMSAlgorithm.AES256_GCM);
      authEnvelopedAlgorithms.add(CMSAlgorithm.AES128_CCM);
      authEnvelopedAlgorithms.add(CMSAlgorithm.AES192_CCM);
      authEnvelopedAlgorithms.add(CMSAlgorithm.AES256_CCM);
      authEnvelopedAlgorithms.add(CMSAlgorithm.ChaCha20Poly1305);
   }

   interface JCECallback {
      Object doInJCE() throws CMSException, InvalidAlgorithmParameterException, InvalidKeyException, InvalidParameterSpecException, NoSuchAlgorithmException, NoSuchPaddingException, NoSuchProviderException;
   }
}
