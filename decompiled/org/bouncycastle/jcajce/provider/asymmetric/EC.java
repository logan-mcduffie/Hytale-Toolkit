package org.bouncycastle.jcajce.provider.asymmetric;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.sec.SECObjectIdentifiers;
import org.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers;
import org.bouncycastle.asn1.x9.ECNamedCurveTable;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.internal.asn1.bsi.BSIObjectIdentifiers;
import org.bouncycastle.internal.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.internal.asn1.eac.EACObjectIdentifiers;
import org.bouncycastle.jcajce.provider.asymmetric.ec.KeyFactorySpi;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AsymmetricAlgorithmProvider;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.util.Properties;

public class EC {
   private static final String PREFIX = "org.bouncycastle.jcajce.provider.asymmetric.ec.";
   private static final Map<String, String> generalEcAttributes = new HashMap<>();
   private static final Map<String, String> ecSupportCurves = new HashMap<>();

   static {
      generalEcAttributes.put("SupportedKeyClasses", "java.security.interfaces.ECPublicKey|java.security.interfaces.ECPrivateKey");
      generalEcAttributes.put("SupportedKeyFormats", "PKCS#8|X.509");
      Enumeration var0 = ECNamedCurveTable.getNames();
      Hashtable var1 = new Hashtable();

      while (var0.hasMoreElements()) {
         String var2 = (String)var0.nextElement();
         ECNamedCurveParameterSpec var3 = org.bouncycastle.jce.ECNamedCurveTable.getParameterSpec(var2);
         if (var3 != null) {
            ASN1ObjectIdentifier var4 = ECNamedCurveTable.getOID(var2);
            if (var4 != null) {
               String var5 = var4.getId();
               Vector var6 = (Vector)var1.get(var5);
               if (var6 == null) {
                  var6 = new Vector();
                  var1.put(var5, var6);
               }

               if (!var6.contains(var2)) {
                  var6.addElement(var2);
               }
            }
         }
      }

      Enumeration var10 = var1.keys();
      Vector var11 = new Vector();

      while (var10.hasMoreElements()) {
         String var12 = (String)var10.nextElement();
         Vector var14 = (Vector)var1.get(var12);
         StringBuffer var16 = new StringBuffer();
         var16.append("[");
         ASN1ObjectIdentifier var7 = new ASN1ObjectIdentifier(var12);
         if (X9ObjectIdentifiers.prime256v1.equals(var7)) {
            var16.append("secp256r1,NIST P-256,X9.62 prime256v1");
         } else if (X9ObjectIdentifiers.prime192v1.equals(var7)) {
            var16.append("secp192r1,NIST P-192,X9.62 prime192v1");
         } else {
            if (var7.on(X9ObjectIdentifiers.primeCurve) || var7.on(X9ObjectIdentifiers.cTwoCurve)) {
               var16.append("X9.62 ");
            }

            for (int var8 = 0; var8 < var14.size(); var8++) {
               if (var8 > 0) {
                  var16.append(",");
               }

               String var9 = (String)var14.elementAt(var8);
               if ((var7.on(SECObjectIdentifiers.ellipticCurve) || X9ObjectIdentifiers.prime256v1.equals(var7) || X9ObjectIdentifiers.prime192v1.equals(var7))
                  && (var9.startsWith("K-") || var9.startsWith("B-") || var9.startsWith("P-"))) {
                  var16.append("NIST ");
               }

               var16.append(var9);
            }
         }

         var16.append(",").append(var12).append("]");
         var11.addElement(var16.toString());
      }

      StringBuffer var13 = new StringBuffer();

      for (int var15 = 0; var15 < var11.size(); var15++) {
         if (var15 > 0) {
            var13.append("|");
         }

         var13.append((String)var11.elementAt(var15));
      }

      ecSupportCurves.put("SupportedCurves", var13.toString());
   }

   public static class Mappings extends AsymmetricAlgorithmProvider {
      @Override
      public void configure(ConfigurableProvider var1) {
         var1.addAlgorithm("AlgorithmParameters.EC", "org.bouncycastle.jcajce.provider.asymmetric.ec.AlgorithmParametersSpi", EC.ecSupportCurves);
         var1.addAlgorithm("KeyAgreement.ECDH", "org.bouncycastle.jcajce.provider.asymmetric.ec.KeyAgreementSpi$DH", EC.generalEcAttributes);
         var1.addAlgorithm("KeyAgreement.ECDHC", "org.bouncycastle.jcajce.provider.asymmetric.ec.KeyAgreementSpi$DHC", EC.generalEcAttributes);
         var1.addAlgorithm("KeyAgreement.ECCDH", "org.bouncycastle.jcajce.provider.asymmetric.ec.KeyAgreementSpi$DHC", EC.generalEcAttributes);
         var1.addAlgorithm("KeyAgreement.ECCDHU", "org.bouncycastle.jcajce.provider.asymmetric.ec.KeyAgreementSpi$DHUC", EC.generalEcAttributes);
         var1.addAlgorithm(
            "KeyAgreement.ECDHWITHSHA1KDF", "org.bouncycastle.jcajce.provider.asymmetric.ec.KeyAgreementSpi$DHwithSHA1KDFAndSharedInfo", EC.generalEcAttributes
         );
         var1.addAlgorithm(
            "KeyAgreement.ECCDHWITHSHA1KDF",
            "org.bouncycastle.jcajce.provider.asymmetric.ec.KeyAgreementSpi$CDHwithSHA1KDFAndSharedInfo",
            EC.generalEcAttributes
         );
         var1.addAlgorithm(
            "KeyAgreement.ECDHWITHSHA224KDF",
            "org.bouncycastle.jcajce.provider.asymmetric.ec.KeyAgreementSpi$DHwithSHA224KDFAndSharedInfo",
            EC.generalEcAttributes
         );
         var1.addAlgorithm(
            "KeyAgreement.ECCDHWITHSHA224KDF",
            "org.bouncycastle.jcajce.provider.asymmetric.ec.KeyAgreementSpi$CDHwithSHA224KDFAndSharedInfo",
            EC.generalEcAttributes
         );
         var1.addAlgorithm(
            "KeyAgreement.ECDHWITHSHA256KDF",
            "org.bouncycastle.jcajce.provider.asymmetric.ec.KeyAgreementSpi$DHwithSHA256KDFAndSharedInfo",
            EC.generalEcAttributes
         );
         var1.addAlgorithm(
            "KeyAgreement.ECCDHWITHSHA256KDF",
            "org.bouncycastle.jcajce.provider.asymmetric.ec.KeyAgreementSpi$CDHwithSHA256KDFAndSharedInfo",
            EC.generalEcAttributes
         );
         var1.addAlgorithm(
            "KeyAgreement.ECDHWITHSHA384KDF",
            "org.bouncycastle.jcajce.provider.asymmetric.ec.KeyAgreementSpi$DHwithSHA384KDFAndSharedInfo",
            EC.generalEcAttributes
         );
         var1.addAlgorithm(
            "KeyAgreement.ECCDHWITHSHA384KDF",
            "org.bouncycastle.jcajce.provider.asymmetric.ec.KeyAgreementSpi$CDHwithSHA384KDFAndSharedInfo",
            EC.generalEcAttributes
         );
         var1.addAlgorithm(
            "KeyAgreement.ECDHWITHSHA512KDF",
            "org.bouncycastle.jcajce.provider.asymmetric.ec.KeyAgreementSpi$DHwithSHA512KDFAndSharedInfo",
            EC.generalEcAttributes
         );
         var1.addAlgorithm(
            "KeyAgreement.ECCDHWITHSHA512KDF",
            "org.bouncycastle.jcajce.provider.asymmetric.ec.KeyAgreementSpi$CDHwithSHA512KDFAndSharedInfo",
            EC.generalEcAttributes
         );
         var1.addAlgorithm(
            "KeyAgreement",
            X9ObjectIdentifiers.dhSinglePass_stdDH_sha1kdf_scheme,
            "org.bouncycastle.jcajce.provider.asymmetric.ec.KeyAgreementSpi$DHwithSHA1KDFAndSharedInfo",
            EC.generalEcAttributes
         );
         var1.addAlgorithm(
            "KeyAgreement",
            X9ObjectIdentifiers.dhSinglePass_cofactorDH_sha1kdf_scheme,
            "org.bouncycastle.jcajce.provider.asymmetric.ec.KeyAgreementSpi$CDHwithSHA1KDFAndSharedInfo",
            EC.generalEcAttributes
         );
         var1.addAlgorithm(
            "KeyAgreement",
            SECObjectIdentifiers.dhSinglePass_stdDH_sha224kdf_scheme,
            "org.bouncycastle.jcajce.provider.asymmetric.ec.KeyAgreementSpi$DHwithSHA224KDFAndSharedInfo",
            EC.generalEcAttributes
         );
         var1.addAlgorithm(
            "KeyAgreement",
            SECObjectIdentifiers.dhSinglePass_cofactorDH_sha224kdf_scheme,
            "org.bouncycastle.jcajce.provider.asymmetric.ec.KeyAgreementSpi$CDHwithSHA224KDFAndSharedInfo",
            EC.generalEcAttributes
         );
         var1.addAlgorithm(
            "KeyAgreement",
            SECObjectIdentifiers.dhSinglePass_stdDH_sha256kdf_scheme,
            "org.bouncycastle.jcajce.provider.asymmetric.ec.KeyAgreementSpi$DHwithSHA256KDFAndSharedInfo",
            EC.generalEcAttributes
         );
         var1.addAlgorithm(
            "KeyAgreement",
            SECObjectIdentifiers.dhSinglePass_cofactorDH_sha256kdf_scheme,
            "org.bouncycastle.jcajce.provider.asymmetric.ec.KeyAgreementSpi$CDHwithSHA256KDFAndSharedInfo",
            EC.generalEcAttributes
         );
         var1.addAlgorithm(
            "KeyAgreement",
            SECObjectIdentifiers.dhSinglePass_stdDH_sha384kdf_scheme,
            "org.bouncycastle.jcajce.provider.asymmetric.ec.KeyAgreementSpi$DHwithSHA384KDFAndSharedInfo",
            EC.generalEcAttributes
         );
         var1.addAlgorithm(
            "KeyAgreement",
            SECObjectIdentifiers.dhSinglePass_cofactorDH_sha384kdf_scheme,
            "org.bouncycastle.jcajce.provider.asymmetric.ec.KeyAgreementSpi$CDHwithSHA384KDFAndSharedInfo",
            EC.generalEcAttributes
         );
         var1.addAlgorithm(
            "KeyAgreement",
            SECObjectIdentifiers.dhSinglePass_stdDH_sha512kdf_scheme,
            "org.bouncycastle.jcajce.provider.asymmetric.ec.KeyAgreementSpi$DHwithSHA512KDFAndSharedInfo",
            EC.generalEcAttributes
         );
         var1.addAlgorithm(
            "KeyAgreement",
            SECObjectIdentifiers.dhSinglePass_cofactorDH_sha512kdf_scheme,
            "org.bouncycastle.jcajce.provider.asymmetric.ec.KeyAgreementSpi$CDHwithSHA512KDFAndSharedInfo",
            EC.generalEcAttributes
         );
         var1.addAlgorithm(
            "KeyAgreement.ECCDHWITHSHA1CKDF", "org.bouncycastle.jcajce.provider.asymmetric.ec.KeyAgreementSpi$DHwithSHA1CKDF", EC.generalEcAttributes
         );
         var1.addAlgorithm(
            "KeyAgreement.ECCDHWITHSHA256CKDF", "org.bouncycastle.jcajce.provider.asymmetric.ec.KeyAgreementSpi$DHwithSHA256CKDF", EC.generalEcAttributes
         );
         var1.addAlgorithm(
            "KeyAgreement.ECCDHWITHSHA384CKDF", "org.bouncycastle.jcajce.provider.asymmetric.ec.KeyAgreementSpi$DHwithSHA384CKDF", EC.generalEcAttributes
         );
         var1.addAlgorithm(
            "KeyAgreement.ECCDHWITHSHA512CKDF", "org.bouncycastle.jcajce.provider.asymmetric.ec.KeyAgreementSpi$DHwithSHA512CKDF", EC.generalEcAttributes
         );
         var1.addAlgorithm(
            "KeyAgreement.ECCDHUWITHSHA1CKDF", "org.bouncycastle.jcajce.provider.asymmetric.ec.KeyAgreementSpi$DHUwithSHA1CKDF", EC.generalEcAttributes
         );
         var1.addAlgorithm(
            "KeyAgreement.ECCDHUWITHSHA224CKDF", "org.bouncycastle.jcajce.provider.asymmetric.ec.KeyAgreementSpi$DHUwithSHA224CKDF", EC.generalEcAttributes
         );
         var1.addAlgorithm(
            "KeyAgreement.ECCDHUWITHSHA256CKDF", "org.bouncycastle.jcajce.provider.asymmetric.ec.KeyAgreementSpi$DHUwithSHA256CKDF", EC.generalEcAttributes
         );
         var1.addAlgorithm(
            "KeyAgreement.ECCDHUWITHSHA384CKDF", "org.bouncycastle.jcajce.provider.asymmetric.ec.KeyAgreementSpi$DHUwithSHA384CKDF", EC.generalEcAttributes
         );
         var1.addAlgorithm(
            "KeyAgreement.ECCDHUWITHSHA512CKDF", "org.bouncycastle.jcajce.provider.asymmetric.ec.KeyAgreementSpi$DHUwithSHA512CKDF", EC.generalEcAttributes
         );
         var1.addAlgorithm(
            "KeyAgreement.ECCDHUWITHSHA1KDF", "org.bouncycastle.jcajce.provider.asymmetric.ec.KeyAgreementSpi$DHUwithSHA1KDF", EC.generalEcAttributes
         );
         var1.addAlgorithm(
            "KeyAgreement.ECCDHUWITHSHA224KDF", "org.bouncycastle.jcajce.provider.asymmetric.ec.KeyAgreementSpi$DHUwithSHA224KDF", EC.generalEcAttributes
         );
         var1.addAlgorithm(
            "KeyAgreement.ECCDHUWITHSHA256KDF", "org.bouncycastle.jcajce.provider.asymmetric.ec.KeyAgreementSpi$DHUwithSHA256KDF", EC.generalEcAttributes
         );
         var1.addAlgorithm(
            "KeyAgreement.ECCDHUWITHSHA384KDF", "org.bouncycastle.jcajce.provider.asymmetric.ec.KeyAgreementSpi$DHUwithSHA384KDF", EC.generalEcAttributes
         );
         var1.addAlgorithm(
            "KeyAgreement.ECCDHUWITHSHA512KDF", "org.bouncycastle.jcajce.provider.asymmetric.ec.KeyAgreementSpi$DHUwithSHA512KDF", EC.generalEcAttributes
         );
         var1.addAlgorithm(
            "KeyAgreement.ECKAEGWITHSHA1KDF", "org.bouncycastle.jcajce.provider.asymmetric.ec.KeyAgreementSpi$ECKAEGwithSHA1KDF", EC.generalEcAttributes
         );
         var1.addAlgorithm(
            "KeyAgreement.ECKAEGWITHSHA224KDF", "org.bouncycastle.jcajce.provider.asymmetric.ec.KeyAgreementSpi$ECKAEGwithSHA224KDF", EC.generalEcAttributes
         );
         var1.addAlgorithm(
            "KeyAgreement.ECKAEGWITHSHA256KDF", "org.bouncycastle.jcajce.provider.asymmetric.ec.KeyAgreementSpi$ECKAEGwithSHA256KDF", EC.generalEcAttributes
         );
         var1.addAlgorithm(
            "KeyAgreement.ECKAEGWITHSHA384KDF", "org.bouncycastle.jcajce.provider.asymmetric.ec.KeyAgreementSpi$ECKAEGwithSHA384KDF", EC.generalEcAttributes
         );
         var1.addAlgorithm(
            "KeyAgreement.ECKAEGWITHSHA512KDF", "org.bouncycastle.jcajce.provider.asymmetric.ec.KeyAgreementSpi$ECKAEGwithSHA512KDF", EC.generalEcAttributes
         );
         var1.addAlgorithm(
            "KeyAgreement",
            BSIObjectIdentifiers.ecka_eg_X963kdf_SHA1,
            "org.bouncycastle.jcajce.provider.asymmetric.ec.KeyAgreementSpi$ECKAEGwithSHA1KDF",
            EC.generalEcAttributes
         );
         var1.addAlgorithm(
            "KeyAgreement",
            BSIObjectIdentifiers.ecka_eg_X963kdf_SHA224,
            "org.bouncycastle.jcajce.provider.asymmetric.ec.KeyAgreementSpi$ECKAEGwithSHA224KDF",
            EC.generalEcAttributes
         );
         var1.addAlgorithm(
            "KeyAgreement",
            BSIObjectIdentifiers.ecka_eg_X963kdf_SHA256,
            "org.bouncycastle.jcajce.provider.asymmetric.ec.KeyAgreementSpi$ECKAEGwithSHA256KDF",
            EC.generalEcAttributes
         );
         var1.addAlgorithm(
            "KeyAgreement",
            BSIObjectIdentifiers.ecka_eg_X963kdf_SHA384,
            "org.bouncycastle.jcajce.provider.asymmetric.ec.KeyAgreementSpi$ECKAEGwithSHA384KDF",
            EC.generalEcAttributes
         );
         var1.addAlgorithm(
            "KeyAgreement",
            BSIObjectIdentifiers.ecka_eg_X963kdf_SHA512,
            "org.bouncycastle.jcajce.provider.asymmetric.ec.KeyAgreementSpi$ECKAEGwithSHA512KDF",
            EC.generalEcAttributes
         );
         var1.addAlgorithm(
            "KeyAgreement",
            BSIObjectIdentifiers.ecka_eg_X963kdf_RIPEMD160,
            "org.bouncycastle.jcajce.provider.asymmetric.ec.KeyAgreementSpi$ECKAEGwithRIPEMD160KDF",
            EC.generalEcAttributes
         );
         var1.addAlgorithm(
            "KeyAgreement.ECKAEGWITHRIPEMD160KDF",
            "org.bouncycastle.jcajce.provider.asymmetric.ec.KeyAgreementSpi$ECKAEGwithRIPEMD160KDF",
            EC.generalEcAttributes
         );
         this.registerOid(var1, X9ObjectIdentifiers.id_ecPublicKey, "EC", new KeyFactorySpi.EC());
         this.registerOid(var1, X9ObjectIdentifiers.dhSinglePass_stdDH_sha1kdf_scheme, "EC", new KeyFactorySpi.EC());
         this.registerOid(var1, X9ObjectIdentifiers.dhSinglePass_cofactorDH_sha1kdf_scheme, "EC", new KeyFactorySpi.EC());
         this.registerOid(var1, SECObjectIdentifiers.dhSinglePass_stdDH_sha224kdf_scheme, "EC", new KeyFactorySpi.EC());
         this.registerOid(var1, SECObjectIdentifiers.dhSinglePass_cofactorDH_sha224kdf_scheme, "EC", new KeyFactorySpi.EC());
         this.registerOid(var1, SECObjectIdentifiers.dhSinglePass_stdDH_sha256kdf_scheme, "EC", new KeyFactorySpi.EC());
         this.registerOid(var1, SECObjectIdentifiers.dhSinglePass_cofactorDH_sha256kdf_scheme, "EC", new KeyFactorySpi.EC());
         this.registerOid(var1, SECObjectIdentifiers.dhSinglePass_stdDH_sha384kdf_scheme, "EC", new KeyFactorySpi.EC());
         this.registerOid(var1, SECObjectIdentifiers.dhSinglePass_cofactorDH_sha384kdf_scheme, "EC", new KeyFactorySpi.EC());
         this.registerOid(var1, SECObjectIdentifiers.dhSinglePass_stdDH_sha512kdf_scheme, "EC", new KeyFactorySpi.EC());
         this.registerOid(var1, SECObjectIdentifiers.dhSinglePass_cofactorDH_sha512kdf_scheme, "EC", new KeyFactorySpi.EC());
         this.registerOidAlgorithmParameters(var1, X9ObjectIdentifiers.id_ecPublicKey, "EC");
         this.registerOidAlgorithmParameters(var1, X9ObjectIdentifiers.dhSinglePass_stdDH_sha1kdf_scheme, "EC");
         this.registerOidAlgorithmParameters(var1, X9ObjectIdentifiers.dhSinglePass_cofactorDH_sha1kdf_scheme, "EC");
         this.registerOidAlgorithmParameters(var1, SECObjectIdentifiers.dhSinglePass_stdDH_sha224kdf_scheme, "EC");
         this.registerOidAlgorithmParameters(var1, SECObjectIdentifiers.dhSinglePass_cofactorDH_sha224kdf_scheme, "EC");
         this.registerOidAlgorithmParameters(var1, SECObjectIdentifiers.dhSinglePass_stdDH_sha256kdf_scheme, "EC");
         this.registerOidAlgorithmParameters(var1, SECObjectIdentifiers.dhSinglePass_cofactorDH_sha256kdf_scheme, "EC");
         this.registerOidAlgorithmParameters(var1, SECObjectIdentifiers.dhSinglePass_stdDH_sha384kdf_scheme, "EC");
         this.registerOidAlgorithmParameters(var1, SECObjectIdentifiers.dhSinglePass_cofactorDH_sha384kdf_scheme, "EC");
         this.registerOidAlgorithmParameters(var1, SECObjectIdentifiers.dhSinglePass_stdDH_sha512kdf_scheme, "EC");
         this.registerOidAlgorithmParameters(var1, SECObjectIdentifiers.dhSinglePass_cofactorDH_sha512kdf_scheme, "EC");
         if (!Properties.isOverrideSet("org.bouncycastle.ec.disable_mqv")) {
            var1.addAlgorithm("KeyAgreement.ECMQV", "org.bouncycastle.jcajce.provider.asymmetric.ec.KeyAgreementSpi$MQV", EC.generalEcAttributes);
            var1.addAlgorithm(
               "KeyAgreement.ECMQVWITHSHA1CKDF", "org.bouncycastle.jcajce.provider.asymmetric.ec.KeyAgreementSpi$MQVwithSHA1CKDF", EC.generalEcAttributes
            );
            var1.addAlgorithm(
               "KeyAgreement.ECMQVWITHSHA224CKDF", "org.bouncycastle.jcajce.provider.asymmetric.ec.KeyAgreementSpi$MQVwithSHA224CKDF", EC.generalEcAttributes
            );
            var1.addAlgorithm(
               "KeyAgreement.ECMQVWITHSHA256CKDF", "org.bouncycastle.jcajce.provider.asymmetric.ec.KeyAgreementSpi$MQVwithSHA256CKDF", EC.generalEcAttributes
            );
            var1.addAlgorithm(
               "KeyAgreement.ECMQVWITHSHA384CKDF", "org.bouncycastle.jcajce.provider.asymmetric.ec.KeyAgreementSpi$MQVwithSHA384CKDF", EC.generalEcAttributes
            );
            var1.addAlgorithm(
               "KeyAgreement.ECMQVWITHSHA512CKDF", "org.bouncycastle.jcajce.provider.asymmetric.ec.KeyAgreementSpi$MQVwithSHA512CKDF", EC.generalEcAttributes
            );
            var1.addAlgorithm(
               "KeyAgreement.ECMQVWITHSHA1KDF", "org.bouncycastle.jcajce.provider.asymmetric.ec.KeyAgreementSpi$MQVwithSHA1KDF", EC.generalEcAttributes
            );
            var1.addAlgorithm(
               "KeyAgreement.ECMQVWITHSHA224KDF", "org.bouncycastle.jcajce.provider.asymmetric.ec.KeyAgreementSpi$MQVwithSHA224KDF", EC.generalEcAttributes
            );
            var1.addAlgorithm(
               "KeyAgreement.ECMQVWITHSHA256KDF", "org.bouncycastle.jcajce.provider.asymmetric.ec.KeyAgreementSpi$MQVwithSHA256KDF", EC.generalEcAttributes
            );
            var1.addAlgorithm(
               "KeyAgreement.ECMQVWITHSHA384KDF", "org.bouncycastle.jcajce.provider.asymmetric.ec.KeyAgreementSpi$MQVwithSHA384KDF", EC.generalEcAttributes
            );
            var1.addAlgorithm(
               "KeyAgreement.ECMQVWITHSHA512KDF", "org.bouncycastle.jcajce.provider.asymmetric.ec.KeyAgreementSpi$MQVwithSHA512KDF", EC.generalEcAttributes
            );
            var1.addAlgorithm(
               "KeyAgreement." + X9ObjectIdentifiers.mqvSinglePass_sha1kdf_scheme,
               "org.bouncycastle.jcajce.provider.asymmetric.ec.KeyAgreementSpi$MQVwithSHA1KDFAndSharedInfo",
               EC.generalEcAttributes
            );
            var1.addAlgorithm(
               "KeyAgreement." + SECObjectIdentifiers.mqvSinglePass_sha224kdf_scheme,
               "org.bouncycastle.jcajce.provider.asymmetric.ec.KeyAgreementSpi$MQVwithSHA224KDFAndSharedInfo",
               EC.generalEcAttributes
            );
            var1.addAlgorithm(
               "KeyAgreement." + SECObjectIdentifiers.mqvSinglePass_sha256kdf_scheme,
               "org.bouncycastle.jcajce.provider.asymmetric.ec.KeyAgreementSpi$MQVwithSHA256KDFAndSharedInfo",
               EC.generalEcAttributes
            );
            var1.addAlgorithm(
               "KeyAgreement." + SECObjectIdentifiers.mqvSinglePass_sha384kdf_scheme,
               "org.bouncycastle.jcajce.provider.asymmetric.ec.KeyAgreementSpi$MQVwithSHA384KDFAndSharedInfo",
               EC.generalEcAttributes
            );
            var1.addAlgorithm(
               "KeyAgreement." + SECObjectIdentifiers.mqvSinglePass_sha512kdf_scheme,
               "org.bouncycastle.jcajce.provider.asymmetric.ec.KeyAgreementSpi$MQVwithSHA512KDFAndSharedInfo",
               EC.generalEcAttributes
            );
            this.registerOid(var1, X9ObjectIdentifiers.mqvSinglePass_sha1kdf_scheme, "ECMQV", new KeyFactorySpi.ECMQV());
            this.registerOidAlgorithmParameters(var1, X9ObjectIdentifiers.mqvSinglePass_sha1kdf_scheme, "EC");
            this.registerOid(var1, SECObjectIdentifiers.mqvSinglePass_sha224kdf_scheme, "ECMQV", new KeyFactorySpi.ECMQV());
            this.registerOidAlgorithmParameters(var1, SECObjectIdentifiers.mqvSinglePass_sha224kdf_scheme, "EC");
            this.registerOid(var1, SECObjectIdentifiers.mqvSinglePass_sha256kdf_scheme, "ECMQV", new KeyFactorySpi.ECMQV());
            this.registerOidAlgorithmParameters(var1, SECObjectIdentifiers.mqvSinglePass_sha256kdf_scheme, "EC");
            this.registerOid(var1, SECObjectIdentifiers.mqvSinglePass_sha384kdf_scheme, "ECMQV", new KeyFactorySpi.ECMQV());
            this.registerOidAlgorithmParameters(var1, SECObjectIdentifiers.mqvSinglePass_sha384kdf_scheme, "EC");
            this.registerOid(var1, SECObjectIdentifiers.mqvSinglePass_sha512kdf_scheme, "ECMQV", new KeyFactorySpi.ECMQV());
            this.registerOidAlgorithmParameters(var1, SECObjectIdentifiers.mqvSinglePass_sha512kdf_scheme, "EC");
            var1.addAlgorithm("KeyFactory.ECMQV", "org.bouncycastle.jcajce.provider.asymmetric.ec.KeyFactorySpi$ECMQV");
            var1.addAlgorithm("KeyPairGenerator.ECMQV", "org.bouncycastle.jcajce.provider.asymmetric.ec.KeyPairGeneratorSpi$ECMQV");
         }

         var1.addAlgorithm("KeyFactory.EC", "org.bouncycastle.jcajce.provider.asymmetric.ec.KeyFactorySpi$EC");
         var1.addAlgorithm("KeyFactory.ECDSA", "org.bouncycastle.jcajce.provider.asymmetric.ec.KeyFactorySpi$ECDSA");
         var1.addAlgorithm("KeyFactory.ECDH", "org.bouncycastle.jcajce.provider.asymmetric.ec.KeyFactorySpi$ECDH");
         var1.addAlgorithm("KeyFactory.ECDHC", "org.bouncycastle.jcajce.provider.asymmetric.ec.KeyFactorySpi$ECDHC");
         var1.addAlgorithm("KeyPairGenerator.EC", "org.bouncycastle.jcajce.provider.asymmetric.ec.KeyPairGeneratorSpi$EC");
         var1.addAlgorithm("KeyPairGenerator.ECDSA", "org.bouncycastle.jcajce.provider.asymmetric.ec.KeyPairGeneratorSpi$ECDSA");
         var1.addAlgorithm("KeyPairGenerator.ECDH", "org.bouncycastle.jcajce.provider.asymmetric.ec.KeyPairGeneratorSpi$ECDH");
         var1.addAlgorithm("KeyPairGenerator.ECDHWITHSHA1KDF", "org.bouncycastle.jcajce.provider.asymmetric.ec.KeyPairGeneratorSpi$ECDH");
         var1.addAlgorithm("KeyPairGenerator.ECDHC", "org.bouncycastle.jcajce.provider.asymmetric.ec.KeyPairGeneratorSpi$ECDHC");
         var1.addAlgorithm("KeyPairGenerator.ECIES", "org.bouncycastle.jcajce.provider.asymmetric.ec.KeyPairGeneratorSpi$ECDH");
         var1.addAlgorithm("Cipher.ECIES", "org.bouncycastle.jcajce.provider.asymmetric.ec.IESCipher$ECIES", EC.generalEcAttributes);
         var1.addAlgorithm("Cipher.ECIESwithSHA1", "org.bouncycastle.jcajce.provider.asymmetric.ec.IESCipher$ECIES", EC.generalEcAttributes);
         var1.addAlgorithm("Cipher.ECIESWITHSHA1", "org.bouncycastle.jcajce.provider.asymmetric.ec.IESCipher$ECIES", EC.generalEcAttributes);
         var1.addAlgorithm("Cipher.ECIESwithSHA256", "org.bouncycastle.jcajce.provider.asymmetric.ec.IESCipher$ECIESwithSHA256", EC.generalEcAttributes);
         var1.addAlgorithm("Cipher.ECIESWITHSHA256", "org.bouncycastle.jcajce.provider.asymmetric.ec.IESCipher$ECIESwithSHA256", EC.generalEcAttributes);
         var1.addAlgorithm("Cipher.ECIESwithSHA384", "org.bouncycastle.jcajce.provider.asymmetric.ec.IESCipher$ECIESwithSHA384", EC.generalEcAttributes);
         var1.addAlgorithm("Cipher.ECIESWITHSHA384", "org.bouncycastle.jcajce.provider.asymmetric.ec.IESCipher$ECIESwithSHA384", EC.generalEcAttributes);
         var1.addAlgorithm("Cipher.ECIESwithSHA512", "org.bouncycastle.jcajce.provider.asymmetric.ec.IESCipher$ECIESwithSHA512", EC.generalEcAttributes);
         var1.addAlgorithm("Cipher.ECIESWITHSHA512", "org.bouncycastle.jcajce.provider.asymmetric.ec.IESCipher$ECIESwithSHA512", EC.generalEcAttributes);
         var1.addAlgorithm("Cipher.ECIESwithAES-CBC", "org.bouncycastle.jcajce.provider.asymmetric.ec.IESCipher$ECIESwithAESCBC", EC.generalEcAttributes);
         var1.addAlgorithm("Cipher.ECIESWITHAES-CBC", "org.bouncycastle.jcajce.provider.asymmetric.ec.IESCipher$ECIESwithAESCBC", EC.generalEcAttributes);
         var1.addAlgorithm("Cipher.ECIESwithSHA1andAES-CBC", "org.bouncycastle.jcajce.provider.asymmetric.ec.IESCipher$ECIESwithAESCBC", EC.generalEcAttributes);
         var1.addAlgorithm("Cipher.ECIESWITHSHA1ANDAES-CBC", "org.bouncycastle.jcajce.provider.asymmetric.ec.IESCipher$ECIESwithAESCBC", EC.generalEcAttributes);
         var1.addAlgorithm(
            "Cipher.ECIESwithSHA256andAES-CBC", "org.bouncycastle.jcajce.provider.asymmetric.ec.IESCipher$ECIESwithSHA256andAESCBC", EC.generalEcAttributes
         );
         var1.addAlgorithm(
            "Cipher.ECIESWITHSHA256ANDAES-CBC", "org.bouncycastle.jcajce.provider.asymmetric.ec.IESCipher$ECIESwithSHA256andAESCBC", EC.generalEcAttributes
         );
         var1.addAlgorithm(
            "Cipher.ECIESwithSHA384andAES-CBC", "org.bouncycastle.jcajce.provider.asymmetric.ec.IESCipher$ECIESwithSHA384andAESCBC", EC.generalEcAttributes
         );
         var1.addAlgorithm(
            "Cipher.ECIESWITHSHA384ANDAES-CBC", "org.bouncycastle.jcajce.provider.asymmetric.ec.IESCipher$ECIESwithSHA384andAESCBC", EC.generalEcAttributes
         );
         var1.addAlgorithm(
            "Cipher.ECIESwithSHA512andAES-CBC", "org.bouncycastle.jcajce.provider.asymmetric.ec.IESCipher$ECIESwithSHA512andAESCBC", EC.generalEcAttributes
         );
         var1.addAlgorithm(
            "Cipher.ECIESWITHSHA512ANDAES-CBC", "org.bouncycastle.jcajce.provider.asymmetric.ec.IESCipher$ECIESwithSHA512andAESCBC", EC.generalEcAttributes
         );
         var1.addAlgorithm("Cipher.ECIESwithDESEDE-CBC", "org.bouncycastle.jcajce.provider.asymmetric.ec.IESCipher$ECIESwithDESedeCBC", EC.generalEcAttributes);
         var1.addAlgorithm("Cipher.ECIESWITHDESEDE-CBC", "org.bouncycastle.jcajce.provider.asymmetric.ec.IESCipher$ECIESwithDESedeCBC", EC.generalEcAttributes);
         var1.addAlgorithm(
            "Cipher.ECIESwithSHA1andDESEDE-CBC", "org.bouncycastle.jcajce.provider.asymmetric.ec.IESCipher$ECIESwithDESedeCBC", EC.generalEcAttributes
         );
         var1.addAlgorithm(
            "Cipher.ECIESWITHSHA1ANDDESEDE-CBC", "org.bouncycastle.jcajce.provider.asymmetric.ec.IESCipher$ECIESwithDESedeCBC", EC.generalEcAttributes
         );
         var1.addAlgorithm(
            "Cipher.ECIESwithSHA256andDESEDE-CBC",
            "org.bouncycastle.jcajce.provider.asymmetric.ec.IESCipher$ECIESwithSHA256andDESedeCBC",
            EC.generalEcAttributes
         );
         var1.addAlgorithm(
            "Cipher.ECIESWITHSHA256ANDDESEDE-CBC",
            "org.bouncycastle.jcajce.provider.asymmetric.ec.IESCipher$ECIESwithSHA256andDESedeCBC",
            EC.generalEcAttributes
         );
         var1.addAlgorithm(
            "Cipher.ECIESwithSHA384andDESEDE-CBC",
            "org.bouncycastle.jcajce.provider.asymmetric.ec.IESCipher$ECIESwithSHA384andDESedeCBC",
            EC.generalEcAttributes
         );
         var1.addAlgorithm(
            "Cipher.ECIESWITHSHA384ANDDESEDE-CBC",
            "org.bouncycastle.jcajce.provider.asymmetric.ec.IESCipher$ECIESwithSHA384andDESedeCBC",
            EC.generalEcAttributes
         );
         var1.addAlgorithm(
            "Cipher.ECIESwithSHA512andDESEDE-CBC",
            "org.bouncycastle.jcajce.provider.asymmetric.ec.IESCipher$ECIESwithSHA512andDESedeCBC",
            EC.generalEcAttributes
         );
         var1.addAlgorithm(
            "Cipher.ECIESWITHSHA512ANDDESEDE-CBC",
            "org.bouncycastle.jcajce.provider.asymmetric.ec.IESCipher$ECIESwithSHA512andDESedeCBC",
            EC.generalEcAttributes
         );
         var1.addAlgorithm("Cipher.ETSIKEMWITHSHA256", "org.bouncycastle.jcajce.provider.asymmetric.ec.IESKEMCipher$KEMwithSHA256", EC.generalEcAttributes);
         var1.addAlgorithm("Signature.ECDSA", "org.bouncycastle.jcajce.provider.asymmetric.ec.SignatureSpi$ecDSA", EC.generalEcAttributes);
         var1.addAlgorithm("Signature.NONEwithECDSA", "org.bouncycastle.jcajce.provider.asymmetric.ec.SignatureSpi$ecDSAnone", EC.generalEcAttributes);
         var1.addAlgorithm("Alg.Alias.Signature.SHA1withECDSA", "ECDSA");
         var1.addAlgorithm("Alg.Alias.Signature.ECDSAwithSHA1", "ECDSA");
         var1.addAlgorithm("Alg.Alias.Signature.SHA1WITHECDSA", "ECDSA");
         var1.addAlgorithm("Alg.Alias.Signature.ECDSAWITHSHA1", "ECDSA");
         var1.addAlgorithm("Alg.Alias.Signature.SHA1WithECDSA", "ECDSA");
         var1.addAlgorithm("Alg.Alias.Signature.ECDSAWithSHA1", "ECDSA");
         var1.addAlgorithm("Alg.Alias.Signature.1.2.840.10045.4.1", "ECDSA");
         var1.addAlgorithm("Alg.Alias.Signature." + TeleTrusTObjectIdentifiers.ecSignWithSha1, "ECDSA");
         var1.addAlgorithm("Signature.ECDDSA", "org.bouncycastle.jcajce.provider.asymmetric.ec.SignatureSpi$ecDetDSA", EC.generalEcAttributes);
         var1.addAlgorithm("Signature.SHA1WITHECDDSA", "org.bouncycastle.jcajce.provider.asymmetric.ec.SignatureSpi$ecDetDSA", EC.generalEcAttributes);
         var1.addAlgorithm("Signature.SHA224WITHECDDSA", "org.bouncycastle.jcajce.provider.asymmetric.ec.SignatureSpi$ecDetDSA224", EC.generalEcAttributes);
         var1.addAlgorithm("Signature.SHA256WITHECDDSA", "org.bouncycastle.jcajce.provider.asymmetric.ec.SignatureSpi$ecDetDSA256", EC.generalEcAttributes);
         var1.addAlgorithm("Signature.SHA384WITHECDDSA", "org.bouncycastle.jcajce.provider.asymmetric.ec.SignatureSpi$ecDetDSA384", EC.generalEcAttributes);
         var1.addAlgorithm("Signature.SHA512WITHECDDSA", "org.bouncycastle.jcajce.provider.asymmetric.ec.SignatureSpi$ecDetDSA512", EC.generalEcAttributes);
         var1.addAlgorithm(
            "Signature.SHA3-224WITHECDDSA", "org.bouncycastle.jcajce.provider.asymmetric.ec.SignatureSpi$ecDetDSASha3_224", EC.generalEcAttributes
         );
         var1.addAlgorithm(
            "Signature.SHA3-256WITHECDDSA", "org.bouncycastle.jcajce.provider.asymmetric.ec.SignatureSpi$ecDetDSASha3_256", EC.generalEcAttributes
         );
         var1.addAlgorithm(
            "Signature.SHA3-384WITHECDDSA", "org.bouncycastle.jcajce.provider.asymmetric.ec.SignatureSpi$ecDetDSASha3_384", EC.generalEcAttributes
         );
         var1.addAlgorithm(
            "Signature.SHA3-512WITHECDDSA", "org.bouncycastle.jcajce.provider.asymmetric.ec.SignatureSpi$ecDetDSASha3_512", EC.generalEcAttributes
         );
         var1.addAlgorithm("Alg.Alias.Signature.DETECDSA", "ECDDSA");
         var1.addAlgorithm("Alg.Alias.Signature.SHA1WITHDETECDSA", "SHA1WITHECDDSA");
         var1.addAlgorithm("Alg.Alias.Signature.SHA224WITHDETECDSA", "SHA224WITHECDDSA");
         var1.addAlgorithm("Alg.Alias.Signature.SHA256WITHDETECDSA", "SHA256WITHECDDSA");
         var1.addAlgorithm("Alg.Alias.Signature.SHA384WITHDETECDSA", "SHA384WITHECDDSA");
         var1.addAlgorithm("Alg.Alias.Signature.SHA512WITHDETECDSA", "SHA512WITHECDDSA");
         this.addSignatureAlgorithm(
            var1,
            "SHA224",
            "ECDSA",
            "org.bouncycastle.jcajce.provider.asymmetric.ec.SignatureSpi$ecDSA224",
            X9ObjectIdentifiers.ecdsa_with_SHA224,
            EC.generalEcAttributes
         );
         this.addSignatureAlgorithm(
            var1,
            "SHA256",
            "ECDSA",
            "org.bouncycastle.jcajce.provider.asymmetric.ec.SignatureSpi$ecDSA256",
            X9ObjectIdentifiers.ecdsa_with_SHA256,
            EC.generalEcAttributes
         );
         this.addSignatureAlgorithm(
            var1,
            "SHA384",
            "ECDSA",
            "org.bouncycastle.jcajce.provider.asymmetric.ec.SignatureSpi$ecDSA384",
            X9ObjectIdentifiers.ecdsa_with_SHA384,
            EC.generalEcAttributes
         );
         this.addSignatureAlgorithm(
            var1,
            "SHA512",
            "ECDSA",
            "org.bouncycastle.jcajce.provider.asymmetric.ec.SignatureSpi$ecDSA512",
            X9ObjectIdentifiers.ecdsa_with_SHA512,
            EC.generalEcAttributes
         );
         this.addSignatureAlgorithm(
            var1,
            "SHA3-224",
            "ECDSA",
            "org.bouncycastle.jcajce.provider.asymmetric.ec.SignatureSpi$ecDSASha3_224",
            NISTObjectIdentifiers.id_ecdsa_with_sha3_224,
            EC.generalEcAttributes
         );
         this.addSignatureAlgorithm(
            var1,
            "SHA3-256",
            "ECDSA",
            "org.bouncycastle.jcajce.provider.asymmetric.ec.SignatureSpi$ecDSASha3_256",
            NISTObjectIdentifiers.id_ecdsa_with_sha3_256,
            EC.generalEcAttributes
         );
         this.addSignatureAlgorithm(
            var1,
            "SHA3-384",
            "ECDSA",
            "org.bouncycastle.jcajce.provider.asymmetric.ec.SignatureSpi$ecDSASha3_384",
            NISTObjectIdentifiers.id_ecdsa_with_sha3_384,
            EC.generalEcAttributes
         );
         this.addSignatureAlgorithm(
            var1,
            "SHA3-512",
            "ECDSA",
            "org.bouncycastle.jcajce.provider.asymmetric.ec.SignatureSpi$ecDSASha3_512",
            NISTObjectIdentifiers.id_ecdsa_with_sha3_512,
            EC.generalEcAttributes
         );
         this.addSignatureAlgorithm(
            var1,
            "SHAKE128",
            "ECDSA",
            "org.bouncycastle.jcajce.provider.asymmetric.ec.SignatureSpi$ecDSAShake128",
            CMSObjectIdentifiers.id_ecdsa_with_shake128,
            EC.generalEcAttributes
         );
         this.addSignatureAlgorithm(
            var1,
            "SHAKE256",
            "ECDSA",
            "org.bouncycastle.jcajce.provider.asymmetric.ec.SignatureSpi$ecDSAShake256",
            CMSObjectIdentifiers.id_ecdsa_with_shake256,
            EC.generalEcAttributes
         );
         this.addSignatureAlgorithm(
            var1,
            "RIPEMD160",
            "ECDSA",
            "org.bouncycastle.jcajce.provider.asymmetric.ec.SignatureSpi$ecDSARipeMD160",
            TeleTrusTObjectIdentifiers.ecSignWithRipemd160,
            EC.generalEcAttributes
         );
         var1.addAlgorithm("Signature.SHA1WITHECNR", "org.bouncycastle.jcajce.provider.asymmetric.ec.SignatureSpi$ecNR", EC.generalEcAttributes);
         var1.addAlgorithm("Signature.SHA224WITHECNR", "org.bouncycastle.jcajce.provider.asymmetric.ec.SignatureSpi$ecNR224", EC.generalEcAttributes);
         var1.addAlgorithm("Signature.SHA256WITHECNR", "org.bouncycastle.jcajce.provider.asymmetric.ec.SignatureSpi$ecNR256", EC.generalEcAttributes);
         var1.addAlgorithm("Signature.SHA384WITHECNR", "org.bouncycastle.jcajce.provider.asymmetric.ec.SignatureSpi$ecNR384", EC.generalEcAttributes);
         var1.addAlgorithm("Signature.SHA512WITHECNR", "org.bouncycastle.jcajce.provider.asymmetric.ec.SignatureSpi$ecNR512", EC.generalEcAttributes);
         this.addSignatureAlgorithm(
            var1,
            "SHA1",
            "CVC-ECDSA",
            "org.bouncycastle.jcajce.provider.asymmetric.ec.SignatureSpi$ecCVCDSA",
            EACObjectIdentifiers.id_TA_ECDSA_SHA_1,
            EC.generalEcAttributes
         );
         this.addSignatureAlgorithm(
            var1,
            "SHA224",
            "CVC-ECDSA",
            "org.bouncycastle.jcajce.provider.asymmetric.ec.SignatureSpi$ecCVCDSA224",
            EACObjectIdentifiers.id_TA_ECDSA_SHA_224,
            EC.generalEcAttributes
         );
         this.addSignatureAlgorithm(
            var1,
            "SHA256",
            "CVC-ECDSA",
            "org.bouncycastle.jcajce.provider.asymmetric.ec.SignatureSpi$ecCVCDSA256",
            EACObjectIdentifiers.id_TA_ECDSA_SHA_256,
            EC.generalEcAttributes
         );
         this.addSignatureAlgorithm(
            var1,
            "SHA384",
            "CVC-ECDSA",
            "org.bouncycastle.jcajce.provider.asymmetric.ec.SignatureSpi$ecCVCDSA384",
            EACObjectIdentifiers.id_TA_ECDSA_SHA_384,
            EC.generalEcAttributes
         );
         this.addSignatureAlgorithm(
            var1,
            "SHA512",
            "CVC-ECDSA",
            "org.bouncycastle.jcajce.provider.asymmetric.ec.SignatureSpi$ecCVCDSA512",
            EACObjectIdentifiers.id_TA_ECDSA_SHA_512,
            EC.generalEcAttributes
         );
         this.addSignatureAlgorithm(
            var1,
            "SHA1",
            "PLAIN-ECDSA",
            "org.bouncycastle.jcajce.provider.asymmetric.ec.SignatureSpi$ecCVCDSA",
            BSIObjectIdentifiers.ecdsa_plain_SHA1,
            EC.generalEcAttributes
         );
         this.addSignatureAlgorithm(
            var1,
            "SHA224",
            "PLAIN-ECDSA",
            "org.bouncycastle.jcajce.provider.asymmetric.ec.SignatureSpi$ecCVCDSA224",
            BSIObjectIdentifiers.ecdsa_plain_SHA224,
            EC.generalEcAttributes
         );
         this.addSignatureAlgorithm(
            var1,
            "SHA256",
            "PLAIN-ECDSA",
            "org.bouncycastle.jcajce.provider.asymmetric.ec.SignatureSpi$ecCVCDSA256",
            BSIObjectIdentifiers.ecdsa_plain_SHA256,
            EC.generalEcAttributes
         );
         this.addSignatureAlgorithm(
            var1,
            "SHA384",
            "PLAIN-ECDSA",
            "org.bouncycastle.jcajce.provider.asymmetric.ec.SignatureSpi$ecCVCDSA384",
            BSIObjectIdentifiers.ecdsa_plain_SHA384,
            EC.generalEcAttributes
         );
         this.addSignatureAlgorithm(
            var1,
            "SHA512",
            "PLAIN-ECDSA",
            "org.bouncycastle.jcajce.provider.asymmetric.ec.SignatureSpi$ecCVCDSA512",
            BSIObjectIdentifiers.ecdsa_plain_SHA512,
            EC.generalEcAttributes
         );
         this.addSignatureAlgorithm(
            var1,
            "RIPEMD160",
            "PLAIN-ECDSA",
            "org.bouncycastle.jcajce.provider.asymmetric.ec.SignatureSpi$ecPlainDSARP160",
            BSIObjectIdentifiers.ecdsa_plain_RIPEMD160,
            EC.generalEcAttributes
         );
         this.addSignatureAlgorithm(
            var1,
            "SHA3-224",
            "PLAIN-ECDSA",
            "org.bouncycastle.jcajce.provider.asymmetric.ec.SignatureSpi$ecCVCDSA3_224",
            BSIObjectIdentifiers.ecdsa_plain_SHA3_224,
            EC.generalEcAttributes
         );
         this.addSignatureAlgorithm(
            var1,
            "SHA3-256",
            "PLAIN-ECDSA",
            "org.bouncycastle.jcajce.provider.asymmetric.ec.SignatureSpi$ecCVCDSA3_256",
            BSIObjectIdentifiers.ecdsa_plain_SHA3_256,
            EC.generalEcAttributes
         );
         this.addSignatureAlgorithm(
            var1,
            "SHA3-384",
            "PLAIN-ECDSA",
            "org.bouncycastle.jcajce.provider.asymmetric.ec.SignatureSpi$ecCVCDSA3_384",
            BSIObjectIdentifiers.ecdsa_plain_SHA3_384,
            EC.generalEcAttributes
         );
         this.addSignatureAlgorithm(
            var1,
            "SHA3-512",
            "PLAIN-ECDSA",
            "org.bouncycastle.jcajce.provider.asymmetric.ec.SignatureSpi$ecCVCDSA3_512",
            BSIObjectIdentifiers.ecdsa_plain_SHA3_512,
            EC.generalEcAttributes
         );
      }
   }
}
