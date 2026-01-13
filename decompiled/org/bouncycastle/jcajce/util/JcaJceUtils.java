package org.bouncycastle.jcajce.util;

import java.io.IOException;
import java.security.AlgorithmParameters;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers;
import org.bouncycastle.internal.asn1.oiw.OIWObjectIdentifiers;

public class JcaJceUtils {
   private JcaJceUtils() {
   }

   /** @deprecated */
   public static ASN1Encodable extractParameters(AlgorithmParameters var0) throws IOException {
      ASN1Primitive var1;
      try {
         var1 = ASN1Primitive.fromByteArray(var0.getEncoded("ASN.1"));
      } catch (Exception var3) {
         var1 = ASN1Primitive.fromByteArray(var0.getEncoded());
      }

      return var1;
   }

   /** @deprecated */
   public static void loadParameters(AlgorithmParameters var0, ASN1Encodable var1) throws IOException {
      try {
         var0.init(var1.toASN1Primitive().getEncoded(), "ASN.1");
      } catch (Exception var3) {
         var0.init(var1.toASN1Primitive().getEncoded());
      }
   }

   /** @deprecated */
   public static String getDigestAlgName(ASN1ObjectIdentifier var0) {
      if (PKCSObjectIdentifiers.md5.equals(var0)) {
         return "MD5";
      } else if (OIWObjectIdentifiers.idSHA1.equals(var0)) {
         return "SHA1";
      } else if (NISTObjectIdentifiers.id_sha224.equals(var0)) {
         return "SHA224";
      } else if (NISTObjectIdentifiers.id_sha256.equals(var0)) {
         return "SHA256";
      } else if (NISTObjectIdentifiers.id_sha384.equals(var0)) {
         return "SHA384";
      } else if (NISTObjectIdentifiers.id_sha512.equals(var0)) {
         return "SHA512";
      } else if (TeleTrusTObjectIdentifiers.ripemd128.equals(var0)) {
         return "RIPEMD128";
      } else if (TeleTrusTObjectIdentifiers.ripemd160.equals(var0)) {
         return "RIPEMD160";
      } else if (TeleTrusTObjectIdentifiers.ripemd256.equals(var0)) {
         return "RIPEMD256";
      } else {
         return CryptoProObjectIdentifiers.gostR3411.equals(var0) ? "GOST3411" : var0.getId();
      }
   }
}
