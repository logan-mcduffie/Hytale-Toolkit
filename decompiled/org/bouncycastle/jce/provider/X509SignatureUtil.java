package org.bouncycastle.jce.provider;

import java.io.IOException;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.PSSParameterSpec;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.RSASSAPSSparams;
import org.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.internal.asn1.oiw.OIWObjectIdentifiers;

class X509SignatureUtil {
   static byte[] getExtensionValue(Extensions var0, String var1) {
      if (var1 != null) {
         ASN1ObjectIdentifier var2 = ASN1ObjectIdentifier.tryFromID(var1);
         if (var2 != null) {
            ASN1OctetString var3 = Extensions.getExtensionValue(var0, var2);
            if (null != var3) {
               try {
                  return var3.getEncoded();
               } catch (Exception var5) {
                  throw new IllegalStateException("error parsing " + var5.toString());
               }
            }
         }
      }

      return null;
   }

   private static boolean isAbsentOrEmptyParameters(ASN1Encodable var0) {
      return var0 == null || DERNull.INSTANCE.equals(var0);
   }

   static void setSignatureParameters(Signature var0, ASN1Encodable var1) throws NoSuchAlgorithmException, SignatureException, InvalidKeyException {
      if (!isAbsentOrEmptyParameters(var1)) {
         String var2 = var0.getAlgorithm();
         AlgorithmParameters var3 = AlgorithmParameters.getInstance(var2, var0.getProvider());

         try {
            var3.init(var1.toASN1Primitive().getEncoded());
         } catch (IOException var6) {
            throw new SignatureException("IOException decoding parameters: " + var6.getMessage());
         }

         if (var2.endsWith("MGF1")) {
            try {
               var0.setParameter(var3.getParameterSpec(PSSParameterSpec.class));
            } catch (GeneralSecurityException var5) {
               throw new SignatureException("Exception extracting parameters: " + var5.getMessage());
            }
         }
      }
   }

   static String getSignatureName(AlgorithmIdentifier var0) {
      ASN1ObjectIdentifier var1 = var0.getAlgorithm();
      ASN1Encodable var2 = var0.getParameters();
      if (!isAbsentOrEmptyParameters(var2)) {
         if (PKCSObjectIdentifiers.id_RSASSA_PSS.equals(var1)) {
            RSASSAPSSparams var4 = RSASSAPSSparams.getInstance(var2);
            return getDigestAlgName(var4.getHashAlgorithm().getAlgorithm()) + "withRSAandMGF1";
         }

         if (X9ObjectIdentifiers.ecdsa_with_SHA2.equals(var1)) {
            AlgorithmIdentifier var3 = AlgorithmIdentifier.getInstance(var2);
            return getDigestAlgName(var3.getAlgorithm()) + "withECDSA";
         }
      }

      return var1.getId();
   }

   private static String getDigestAlgName(ASN1ObjectIdentifier var0) {
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
