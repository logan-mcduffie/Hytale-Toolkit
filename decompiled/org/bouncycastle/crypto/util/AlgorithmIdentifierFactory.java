package org.bouncycastle.crypto.util;

import java.security.SecureRandom;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.RC2CBCParameter;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.internal.asn1.cms.CCMParameters;
import org.bouncycastle.internal.asn1.cms.GCMParameters;
import org.bouncycastle.internal.asn1.kisa.KISAObjectIdentifiers;
import org.bouncycastle.internal.asn1.misc.CAST5CBCParameters;
import org.bouncycastle.internal.asn1.ntt.NTTObjectIdentifiers;
import org.bouncycastle.internal.asn1.oiw.OIWObjectIdentifiers;

public class AlgorithmIdentifierFactory {
   static final ASN1ObjectIdentifier IDEA_CBC = new ASN1ObjectIdentifier("1.3.6.1.4.1.188.7.1.1.2").intern();
   static final ASN1ObjectIdentifier CAST5_CBC = new ASN1ObjectIdentifier("1.2.840.113533.7.66.10").intern();
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

   private AlgorithmIdentifierFactory() {
   }

   public static AlgorithmIdentifier generateEncryptionAlgID(ASN1ObjectIdentifier var0, int var1, SecureRandom var2) throws IllegalArgumentException {
      if (var0.equals(NISTObjectIdentifiers.id_aes128_CBC)
         || var0.equals(NISTObjectIdentifiers.id_aes192_CBC)
         || var0.equals(NISTObjectIdentifiers.id_aes256_CBC)
         || var0.equals(NTTObjectIdentifiers.id_camellia128_cbc)
         || var0.equals(NTTObjectIdentifiers.id_camellia192_cbc)
         || var0.equals(NTTObjectIdentifiers.id_camellia256_cbc)
         || var0.equals(KISAObjectIdentifiers.id_seedCBC)) {
         byte[] var9 = new byte[16];
         var2.nextBytes(var9);
         return new AlgorithmIdentifier(var0, new DEROctetString(var9));
      } else if (var0.equals(NISTObjectIdentifiers.id_aes128_GCM)
         || var0.equals(NISTObjectIdentifiers.id_aes192_GCM)
         || var0.equals(NISTObjectIdentifiers.id_aes256_GCM)) {
         byte[] var8 = new byte[12];
         var2.nextBytes(var8);
         return new AlgorithmIdentifier(var0, new GCMParameters(var8, 16));
      } else if (var0.equals(NISTObjectIdentifiers.id_aes128_CCM)
         || var0.equals(NISTObjectIdentifiers.id_aes192_CCM)
         || var0.equals(NISTObjectIdentifiers.id_aes256_CCM)) {
         byte[] var7 = new byte[8];
         var2.nextBytes(var7);
         return new AlgorithmIdentifier(var0, new CCMParameters(var7, 16));
      } else if (var0.equals(PKCSObjectIdentifiers.des_EDE3_CBC) || var0.equals(IDEA_CBC) || var0.equals(OIWObjectIdentifiers.desCBC)) {
         byte[] var6 = new byte[8];
         var2.nextBytes(var6);
         return new AlgorithmIdentifier(var0, new DEROctetString(var6));
      } else if (var0.equals(CAST5_CBC)) {
         byte[] var5 = new byte[8];
         var2.nextBytes(var5);
         CAST5CBCParameters var10 = new CAST5CBCParameters(var5, var1);
         return new AlgorithmIdentifier(var0, var10);
      } else if (var0.equals(PKCSObjectIdentifiers.rc4)) {
         return new AlgorithmIdentifier(var0, DERNull.INSTANCE);
      } else if (var0.equals(PKCSObjectIdentifiers.RC2_CBC)) {
         byte[] var3 = new byte[8];
         var2.nextBytes(var3);
         RC2CBCParameter var4 = new RC2CBCParameter(rc2Table[128], var3);
         return new AlgorithmIdentifier(var0, var4);
      } else {
         throw new IllegalArgumentException("unable to match algorithm");
      }
   }
}
