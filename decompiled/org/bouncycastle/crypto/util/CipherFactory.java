package org.bouncycastle.crypto.util;

import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Null;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.RC2CBCParameter;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.StreamCipher;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.engines.CAST5Engine;
import org.bouncycastle.crypto.engines.DESEngine;
import org.bouncycastle.crypto.engines.DESedeEngine;
import org.bouncycastle.crypto.engines.RC2Engine;
import org.bouncycastle.crypto.engines.RC4Engine;
import org.bouncycastle.crypto.io.CipherOutputStream;
import org.bouncycastle.crypto.modes.AEADBlockCipher;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.modes.CBCModeCipher;
import org.bouncycastle.crypto.modes.CCMBlockCipher;
import org.bouncycastle.crypto.modes.GCMBlockCipher;
import org.bouncycastle.crypto.paddings.PKCS7Padding;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.params.RC2Parameters;
import org.bouncycastle.internal.asn1.cms.CCMParameters;
import org.bouncycastle.internal.asn1.cms.GCMParameters;
import org.bouncycastle.internal.asn1.kisa.KISAObjectIdentifiers;
import org.bouncycastle.internal.asn1.misc.CAST5CBCParameters;
import org.bouncycastle.internal.asn1.misc.MiscObjectIdentifiers;
import org.bouncycastle.internal.asn1.ntt.NTTObjectIdentifiers;
import org.bouncycastle.internal.asn1.oiw.OIWObjectIdentifiers;

public class CipherFactory {
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

   private static int getRC2EffectiveKeyBits(RC2CBCParameter var0) {
      ASN1Integer var1 = var0.getRC2ParameterVersionData();
      if (var1 == null) {
         return 32;
      } else {
         int var2 = var1.intPositiveValueExact();
         return var2 >= 256 ? var2 : rc2Ekb[var2] & 65535;
      }
   }

   public static Object createContentCipher(boolean var0, CipherParameters var1, AlgorithmIdentifier var2) throws IllegalArgumentException {
      ASN1ObjectIdentifier var3 = var2.getAlgorithm();
      if (var3.equals(PKCSObjectIdentifiers.rc4)) {
         RC4Engine var10 = new RC4Engine();
         var10.init(var0, var1);
         return var10;
      } else if (var3.equals(NISTObjectIdentifiers.id_aes128_GCM)
         || var3.equals(NISTObjectIdentifiers.id_aes192_GCM)
         || var3.equals(NISTObjectIdentifiers.id_aes256_GCM)) {
         AEADBlockCipher var9 = createAEADCipher(var2.getAlgorithm());
         GCMParameters var12 = GCMParameters.getInstance(var2.getParameters());
         if (!(var1 instanceof KeyParameter)) {
            throw new IllegalArgumentException("key data must be accessible for GCM operation");
         } else {
            AEADParameters var15 = new AEADParameters((KeyParameter)var1, var12.getIcvLen() * 8, var12.getNonce());
            var9.init(var0, var15);
            return var9;
         }
      } else if (!var3.equals(NISTObjectIdentifiers.id_aes128_CCM)
         && !var3.equals(NISTObjectIdentifiers.id_aes192_CCM)
         && !var3.equals(NISTObjectIdentifiers.id_aes256_CCM)) {
         BufferedBlockCipher var8 = createCipher(var2.getAlgorithm());
         ASN1Primitive var11 = var2.getParameters().toASN1Primitive();
         if (var11 != null && !(var11 instanceof ASN1Null)) {
            if (var3.equals(PKCSObjectIdentifiers.des_EDE3_CBC)
               || var3.equals(AlgorithmIdentifierFactory.IDEA_CBC)
               || var3.equals(NISTObjectIdentifiers.id_aes128_CBC)
               || var3.equals(NISTObjectIdentifiers.id_aes192_CBC)
               || var3.equals(NISTObjectIdentifiers.id_aes256_CBC)
               || var3.equals(NTTObjectIdentifiers.id_camellia128_cbc)
               || var3.equals(NTTObjectIdentifiers.id_camellia192_cbc)
               || var3.equals(NTTObjectIdentifiers.id_camellia256_cbc)
               || var3.equals(KISAObjectIdentifiers.id_seedCBC)
               || var3.equals(OIWObjectIdentifiers.desCBC)) {
               var8.init(var0, new ParametersWithIV(var1, ASN1OctetString.getInstance(var11).getOctets()));
            } else if (var3.equals(AlgorithmIdentifierFactory.CAST5_CBC)) {
               CAST5CBCParameters var13 = CAST5CBCParameters.getInstance(var11);
               var8.init(var0, new ParametersWithIV(var1, var13.getIV()));
            } else {
               if (!var3.equals(PKCSObjectIdentifiers.RC2_CBC)) {
                  throw new IllegalArgumentException("cannot match parameters");
               }

               RC2CBCParameter var14 = RC2CBCParameter.getInstance(var11);
               RC2Parameters var7 = new RC2Parameters(((KeyParameter)var1).getKey(), getRC2EffectiveKeyBits(var14));
               var8.init(var0, new ParametersWithIV(var7, var14.getIV()));
            }
         } else if (!var3.equals(PKCSObjectIdentifiers.des_EDE3_CBC)
            && !var3.equals(AlgorithmIdentifierFactory.IDEA_CBC)
            && !var3.equals(AlgorithmIdentifierFactory.CAST5_CBC)) {
            var8.init(var0, var1);
         } else {
            var8.init(var0, new ParametersWithIV(var1, new byte[8]));
         }

         return var8;
      } else {
         AEADBlockCipher var4 = createAEADCipher(var2.getAlgorithm());
         CCMParameters var5 = CCMParameters.getInstance(var2.getParameters());
         if (!(var1 instanceof KeyParameter)) {
            throw new IllegalArgumentException("key data must be accessible for CCM operation");
         } else {
            AEADParameters var6 = new AEADParameters((KeyParameter)var1, var5.getIcvLen() * 8, var5.getNonce());
            var4.init(var0, var6);
            return var4;
         }
      }
   }

   private static AEADBlockCipher createAEADCipher(ASN1ObjectIdentifier var0) {
      if (NISTObjectIdentifiers.id_aes128_GCM.equals(var0)
         || NISTObjectIdentifiers.id_aes192_GCM.equals(var0)
         || NISTObjectIdentifiers.id_aes256_GCM.equals(var0)) {
         return GCMBlockCipher.newInstance(AESEngine.newInstance());
      } else if (!NISTObjectIdentifiers.id_aes128_CCM.equals(var0)
         && !NISTObjectIdentifiers.id_aes192_CCM.equals(var0)
         && !NISTObjectIdentifiers.id_aes256_CCM.equals(var0)) {
         throw new IllegalArgumentException("cannot recognise cipher: " + var0);
      } else {
         return CCMBlockCipher.newInstance(AESEngine.newInstance());
      }
   }

   private static BufferedBlockCipher createCipher(ASN1ObjectIdentifier var0) throws IllegalArgumentException {
      CBCModeCipher var1;
      if (NISTObjectIdentifiers.id_aes128_CBC.equals(var0)
         || NISTObjectIdentifiers.id_aes192_CBC.equals(var0)
         || NISTObjectIdentifiers.id_aes256_CBC.equals(var0)) {
         var1 = CBCBlockCipher.newInstance(AESEngine.newInstance());
      } else if (PKCSObjectIdentifiers.des_EDE3_CBC.equals(var0)) {
         var1 = CBCBlockCipher.newInstance(new DESedeEngine());
      } else if (OIWObjectIdentifiers.desCBC.equals(var0)) {
         var1 = CBCBlockCipher.newInstance(new DESEngine());
      } else if (PKCSObjectIdentifiers.RC2_CBC.equals(var0)) {
         var1 = CBCBlockCipher.newInstance(new RC2Engine());
      } else {
         if (!MiscObjectIdentifiers.cast5CBC.equals(var0)) {
            throw new IllegalArgumentException("cannot recognise cipher: " + var0);
         }

         var1 = CBCBlockCipher.newInstance(new CAST5Engine());
      }

      return new PaddedBufferedBlockCipher(var1, new PKCS7Padding());
   }

   public static CipherOutputStream createOutputStream(OutputStream var0, Object var1) {
      if (var1 instanceof BufferedBlockCipher) {
         return new CipherOutputStream(var0, (BufferedBlockCipher)var1);
      } else if (var1 instanceof StreamCipher) {
         return new CipherOutputStream(var0, (StreamCipher)var1);
      } else if (var1 instanceof AEADBlockCipher) {
         return new CipherOutputStream(var0, (AEADBlockCipher)var1);
      } else {
         throw new IllegalArgumentException("unknown cipher object: " + var1);
      }
   }
}
