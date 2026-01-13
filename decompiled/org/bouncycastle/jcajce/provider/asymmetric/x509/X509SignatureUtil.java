package org.bouncycastle.jcajce.provider.asymmetric.x509;

import java.io.IOException;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.PSSParameterSpec;
import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.RSASSAPSSparams;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.internal.asn1.edec.EdECObjectIdentifiers;
import org.bouncycastle.internal.asn1.misc.MiscObjectIdentifiers;
import org.bouncycastle.internal.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.jcajce.util.MessageDigestUtils;
import org.bouncycastle.util.Exceptions;
import org.bouncycastle.util.Objects;
import org.bouncycastle.util.Properties;
import org.bouncycastle.util.encoders.Hex;

class X509SignatureUtil {
   private static final Map<ASN1ObjectIdentifier, String> algNames = new HashMap<>();

   static boolean areEquivalentAlgorithms(AlgorithmIdentifier var0, AlgorithmIdentifier var1) {
      if (!var0.getAlgorithm().equals(var1.getAlgorithm())) {
         return false;
      } else {
         return Properties.isOverrideSet("org.bouncycastle.x509.allow_absent_equiv_NULL")
               && isAbsentOrEmptyParameters(var0.getParameters())
               && isAbsentOrEmptyParameters(var1.getParameters())
            ? true
            : Objects.areEqual(var0.getParameters(), var1.getParameters());
      }
   }

   static byte[] getExtensionValue(Extensions var0, String var1) {
      if (var1 != null) {
         ASN1ObjectIdentifier var2 = ASN1ObjectIdentifier.tryFromID(var1);
         if (var2 != null) {
            ASN1OctetString var3 = Extensions.getExtensionValue(var0, var2);
            if (null != var3) {
               try {
                  return var3.getEncoded();
               } catch (Exception var5) {
                  throw Exceptions.illegalStateException("error parsing " + var5.getMessage(), var5);
               }
            }
         }
      }

      return null;
   }

   private static boolean isAbsentOrEmptyParameters(ASN1Encodable var0) {
      return var0 == null || DERNull.INSTANCE.equals(var0);
   }

   static boolean isCompositeAlgorithm(AlgorithmIdentifier var0) {
      return MiscObjectIdentifiers.id_alg_composite.equals(var0.getAlgorithm());
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
            RSASSAPSSparams var5 = RSASSAPSSparams.getInstance(var2);
            return getDigestAlgName(var5.getHashAlgorithm().getAlgorithm()) + "withRSAandMGF1";
         }

         if (X9ObjectIdentifiers.ecdsa_with_SHA2.equals(var1)) {
            AlgorithmIdentifier var4 = AlgorithmIdentifier.getInstance(var2);
            return getDigestAlgName(var4.getAlgorithm()) + "withECDSA";
         }
      }

      String var3 = algNames.get(var1);
      return var3 != null ? var3 : findAlgName(var1);
   }

   private static String getDigestAlgName(ASN1ObjectIdentifier var0) {
      String var1 = MessageDigestUtils.getDigestName(var0);
      int var2 = var1.indexOf(45);
      return var2 > 0 && !var1.startsWith("SHA3") ? var1.substring(0, var2) + var1.substring(var2 + 1) : var1;
   }

   private static String findAlgName(ASN1ObjectIdentifier var0) {
      Provider var1 = Security.getProvider("BC");
      if (var1 != null) {
         String var2 = lookupAlg(var1, var0);
         if (var2 != null) {
            return var2;
         }
      }

      Provider[] var5 = Security.getProviders();

      for (int var3 = 0; var3 != var5.length; var3++) {
         if (var1 != var5[var3]) {
            String var4 = lookupAlg(var5[var3], var0);
            if (var4 != null) {
               return var4;
            }
         }
      }

      return var0.getId();
   }

   private static String lookupAlg(Provider var0, ASN1ObjectIdentifier var1) {
      String var2 = var0.getProperty("Alg.Alias.Signature." + var1);
      if (var2 != null) {
         return var2;
      } else {
         var2 = var0.getProperty("Alg.Alias.Signature.OID." + var1);
         return var2 != null ? var2 : null;
      }
   }

   static void prettyPrintSignature(byte[] var0, StringBuilder var1, String var2) {
      if (var0.length > 20) {
         var1.append("            Signature: ").append(Hex.toHexString(var0, 0, 20)).append(var2);

         for (byte var3 = 20; var3 < var0.length; var3 += 20) {
            if (var3 < var0.length - 20) {
               var1.append("                       ").append(Hex.toHexString(var0, var3, 20)).append(var2);
            } else {
               var1.append("                       ").append(Hex.toHexString(var0, var3, var0.length - var3)).append(var2);
            }
         }
      } else {
         var1.append("            Signature: ").append(Hex.toHexString(var0)).append(var2);
      }
   }

   static {
      algNames.put(EdECObjectIdentifiers.id_Ed25519, "Ed25519");
      algNames.put(EdECObjectIdentifiers.id_Ed448, "Ed448");
      algNames.put(OIWObjectIdentifiers.dsaWithSHA1, "SHA1withDSA");
      algNames.put(X9ObjectIdentifiers.id_dsa_with_sha1, "SHA1withDSA");
   }
}
