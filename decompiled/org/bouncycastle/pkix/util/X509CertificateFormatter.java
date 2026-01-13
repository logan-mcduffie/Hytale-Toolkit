package org.bouncycastle.pkix.util;

import java.io.FileReader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.edec.EdECObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.util.ASN1Dump;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.ExtendedKeyUsage;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.operator.DefaultSignatureNameFinder;
import org.bouncycastle.util.Strings;
import org.bouncycastle.util.encoders.Hex;

public class X509CertificateFormatter {
   private static Map<ASN1ObjectIdentifier, String> oidMap = new HashMap<>();
   private static Map<ASN1ObjectIdentifier, String> keyAlgMap = new HashMap<>();
   private static Map<KeyPurposeId, String> extUsageMap = new HashMap<>();
   private static Map<Integer, String> usageMap = new HashMap<>();
   private static final String spaceStr = "                                                              ";

   private static String oidToLabel(ASN1ObjectIdentifier var0) {
      String var1 = oidMap.get(var0);
      return var1 != null ? var1 : var0.getId();
   }

   private static String keyAlgToLabel(ASN1ObjectIdentifier var0) {
      String var1 = keyAlgMap.get(var0);
      return var1 != null ? var1 : var0.getId();
   }

   private static String spaces(int var0) {
      return "                                                              ".substring(0, var0);
   }

   private static String indent(String var0, String var1, String var2) {
      StringBuilder var3 = new StringBuilder();
      byte var5 = 0;
      var1 = var1.substring(0, var1.length() - var2.length());

      int var4;
      while ((var4 = var1.indexOf(var2)) > 0) {
         var3.append(var1.substring(var5, var4));
         var3.append(var2);
         var3.append(var0);
         if (var5 < var1.length()) {
            var1 = var1.substring(var4 + var2.length());
         }
      }

      if (var3.length() == 0) {
         return var1;
      } else {
         var3.append(var1);
         return var3.toString();
      }
   }

   static void prettyPrintData(byte[] var0, StringBuilder var1, String var2) {
      if (var0.length > 20) {
         var1.append(Hex.toHexString(var0, 0, 20)).append(var2);
         format(var1, var0, var2);
      } else {
         var1.append(Hex.toHexString(var0)).append(var2);
      }
   }

   static void format(StringBuilder var0, byte[] var1, String var2) {
      for (byte var3 = 20; var3 < var1.length; var3 += 20) {
         if (var3 < var1.length - 20) {
            var0.append("                       ").append(Hex.toHexString(var1, var3, 20)).append(var2);
         } else {
            var0.append("                       ").append(Hex.toHexString(var1, var3, var1.length - var3)).append(var2);
         }
      }
   }

   public static String asString(X509CertificateHolder var0) {
      StringBuilder var1 = new StringBuilder();
      String var2 = Strings.lineSeparator();
      String var3 = new DefaultSignatureNameFinder().getAlgorithmName(var0.getSignatureAlgorithm());
      var3 = var3.replace("WITH", "with");
      String var4 = keyAlgToLabel(var0.getSubjectPublicKeyInfo().getAlgorithm().getAlgorithm());
      var1.append("  [0]         Version: ").append(var0.getVersionNumber()).append(var2);
      var1.append("         SerialNumber: ").append(var0.getSerialNumber()).append(var2);
      var1.append("             IssuerDN: ").append(var0.getIssuer()).append(var2);
      var1.append("           Start Date: ").append(var0.getNotBefore()).append(var2);
      var1.append("           Final Date: ").append(var0.getNotAfter()).append(var2);
      var1.append("            SubjectDN: ").append(var0.getSubject()).append(var2);
      var1.append("           Public Key: ").append(var4).append(var2);
      var1.append("                       ");
      prettyPrintData(var0.getSubjectPublicKeyInfo().getPublicKeyData().getOctets(), var1, var2);
      Extensions var5 = var0.getExtensions();
      if (var5 != null) {
         Enumeration var6 = var5.oids();
         if (var6.hasMoreElements()) {
            var1.append("           Extensions: ").append(var2);
         }

         while (var6.hasMoreElements()) {
            ASN1ObjectIdentifier var7 = (ASN1ObjectIdentifier)var6.nextElement();
            Extension var8 = var5.getExtension(var7);
            if (var8.getExtnValue() != null) {
               byte[] var9 = var8.getExtnValue().getOctets();
               ASN1InputStream var10 = new ASN1InputStream(var9);
               String var11 = "                       ";

               try {
                  String var12 = oidToLabel(var7);
                  var1.append(var11).append(var12);
                  var1.append(": critical(").append(var8.isCritical()).append(") ").append(var2);
                  var11 = var11 + spaces(2 + var12.length());
                  if (var7.equals(Extension.basicConstraints)) {
                     BasicConstraints var21 = BasicConstraints.getInstance(var10.readObject());
                     var1.append(var11).append("isCA : " + var21.isCA()).append(var2);
                     if (var21.isCA()) {
                        var1.append(spaces(2 + var12.length()));
                        var1.append("pathLenConstraint : " + var21.getPathLenConstraint()).append(var2);
                     }
                  } else if (var7.equals(Extension.keyUsage)) {
                     KeyUsage var20 = KeyUsage.getInstance(var10.readObject());
                     var1.append(var11);
                     boolean var22 = true;

                     for (int var24 : usageMap.keySet()) {
                        if (var20.hasUsages(var24)) {
                           if (!var22) {
                              var1.append(", ");
                           } else {
                              var22 = false;
                           }

                           var1.append(usageMap.get(var24));
                        }
                     }

                     var1.append(var2);
                  } else if (!var7.equals(Extension.extendedKeyUsage)) {
                     var1.append(var11).append("value = ").append(indent(var11 + spaces(8), ASN1Dump.dumpAsString(var10.readObject()), var2)).append(var2);
                  } else {
                     ExtendedKeyUsage var13 = ExtendedKeyUsage.getInstance(var10.readObject());
                     var1.append(var11);
                     boolean var14 = true;

                     for (KeyPurposeId var16 : extUsageMap.keySet()) {
                        if (var13.hasKeyPurposeId(var16)) {
                           if (!var14) {
                              var1.append(", ");
                           } else {
                              var14 = false;
                           }

                           var1.append(extUsageMap.get(var16));
                        }
                     }

                     var1.append(var2);
                  }
               } catch (Exception var17) {
                  var1.append(var7.getId());
                  var1.append(" value = ").append("*****").append(var2);
               }
            } else {
               var1.append(var2);
            }
         }
      }

      var1.append("  Signature Algorithm: ").append(var3).append(var2);
      var1.append("            Signature: ");
      prettyPrintData(var0.getSignature(), var1, var2);
      return var1.toString();
   }

   public static void main(String[] var0) throws Exception {
      PEMParser var1 = new PEMParser(new FileReader(var0[0]));
      System.out.println(asString((X509CertificateHolder)var1.readObject()));
   }

   static {
      oidMap.put(Extension.subjectDirectoryAttributes, "subjectDirectoryAttributes");
      oidMap.put(Extension.subjectKeyIdentifier, "subjectKeyIdentifier");
      oidMap.put(Extension.keyUsage, "keyUsage");
      oidMap.put(Extension.privateKeyUsagePeriod, "privateKeyUsagePeriod");
      oidMap.put(Extension.subjectAlternativeName, "subjectAlternativeName");
      oidMap.put(Extension.issuerAlternativeName, "issuerAlternativeName");
      oidMap.put(Extension.basicConstraints, "basicConstraints");
      oidMap.put(Extension.cRLNumber, "cRLNumber");
      oidMap.put(Extension.reasonCode, "reasonCode");
      oidMap.put(Extension.instructionCode, "instructionCode");
      oidMap.put(Extension.invalidityDate, "invalidityDate");
      oidMap.put(Extension.deltaCRLIndicator, "deltaCRLIndicator");
      oidMap.put(Extension.issuingDistributionPoint, "issuingDistributionPoint");
      oidMap.put(Extension.certificateIssuer, "certificateIssuer");
      oidMap.put(Extension.nameConstraints, "nameConstraints");
      oidMap.put(Extension.cRLDistributionPoints, "cRLDistributionPoints");
      oidMap.put(Extension.certificatePolicies, "certificatePolicies");
      oidMap.put(Extension.policyMappings, "policyMappings");
      oidMap.put(Extension.authorityKeyIdentifier, "authorityKeyIdentifier");
      oidMap.put(Extension.policyConstraints, "policyConstraints");
      oidMap.put(Extension.extendedKeyUsage, "extendedKeyUsage");
      oidMap.put(Extension.freshestCRL, "freshestCRL");
      oidMap.put(Extension.inhibitAnyPolicy, "inhibitAnyPolicy");
      oidMap.put(Extension.authorityInfoAccess, "authorityInfoAccess");
      oidMap.put(Extension.subjectInfoAccess, "subjectInfoAccess");
      oidMap.put(Extension.logoType, "logoType");
      oidMap.put(Extension.biometricInfo, "biometricInfo");
      oidMap.put(Extension.qCStatements, "qCStatements");
      oidMap.put(Extension.auditIdentity, "auditIdentity");
      oidMap.put(Extension.noRevAvail, "noRevAvail");
      oidMap.put(Extension.targetInformation, "targetInformation");
      oidMap.put(Extension.expiredCertsOnCRL, "expiredCertsOnCRL");
      usageMap.put(128, "digitalSignature");
      usageMap.put(64, "nonRepudiation");
      usageMap.put(32, "keyEncipherment");
      usageMap.put(16, "dataEncipherment");
      usageMap.put(8, "keyAgreement");
      usageMap.put(4, "keyCertSign");
      usageMap.put(2, "cRLSign");
      usageMap.put(1, "encipherOnly");
      usageMap.put(32768, "decipherOnly");
      extUsageMap.put(KeyPurposeId.anyExtendedKeyUsage, "anyExtendedKeyUsage");
      extUsageMap.put(KeyPurposeId.id_kp_serverAuth, "id_kp_serverAuth");
      extUsageMap.put(KeyPurposeId.id_kp_clientAuth, "id_kp_clientAuth");
      extUsageMap.put(KeyPurposeId.id_kp_codeSigning, "id_kp_codeSigning");
      extUsageMap.put(KeyPurposeId.id_kp_emailProtection, "id_kp_emailProtection");
      extUsageMap.put(KeyPurposeId.id_kp_ipsecEndSystem, "id_kp_ipsecEndSystem");
      extUsageMap.put(KeyPurposeId.id_kp_ipsecTunnel, "id_kp_ipsecTunnel");
      extUsageMap.put(KeyPurposeId.id_kp_ipsecUser, "id_kp_ipsecUser");
      extUsageMap.put(KeyPurposeId.id_kp_timeStamping, "id_kp_timeStamping");
      extUsageMap.put(KeyPurposeId.id_kp_OCSPSigning, "id_kp_OCSPSigning");
      extUsageMap.put(KeyPurposeId.id_kp_dvcs, "id_kp_dvcs");
      extUsageMap.put(KeyPurposeId.id_kp_sbgpCertAAServerAuth, "id_kp_sbgpCertAAServerAuth");
      extUsageMap.put(KeyPurposeId.id_kp_scvp_responder, "id_kp_scvp_responder");
      extUsageMap.put(KeyPurposeId.id_kp_eapOverPPP, "id_kp_eapOverPPP");
      extUsageMap.put(KeyPurposeId.id_kp_eapOverLAN, "id_kp_eapOverLAN");
      extUsageMap.put(KeyPurposeId.id_kp_scvpServer, "id_kp_scvpServer");
      extUsageMap.put(KeyPurposeId.id_kp_scvpClient, "id_kp_scvpClient");
      extUsageMap.put(KeyPurposeId.id_kp_ipsecIKE, "id_kp_ipsecIKE");
      extUsageMap.put(KeyPurposeId.id_kp_capwapAC, "id_kp_capwapAC");
      extUsageMap.put(KeyPurposeId.id_kp_capwapWTP, "id_kp_capwapWTP");
      extUsageMap.put(KeyPurposeId.id_kp_cmcCA, "id_kp_cmcCA");
      extUsageMap.put(KeyPurposeId.id_kp_cmcRA, "id_kp_cmcRA");
      extUsageMap.put(KeyPurposeId.id_kp_cmKGA, "id_kp_cmKGA");
      extUsageMap.put(KeyPurposeId.id_kp_smartcardlogon, "id_kp_smartcardlogon");
      extUsageMap.put(KeyPurposeId.id_kp_macAddress, "id_kp_macAddress");
      extUsageMap.put(KeyPurposeId.id_kp_msSGC, "id_kp_msSGC");
      extUsageMap.put(KeyPurposeId.id_kp_nsSGC, "id_kp_nsSGC");
      keyAlgMap.put(PKCSObjectIdentifiers.rsaEncryption, "rsaEncryption");
      keyAlgMap.put(X9ObjectIdentifiers.id_ecPublicKey, "id_ecPublicKey");
      keyAlgMap.put(EdECObjectIdentifiers.id_Ed25519, "id_Ed25519");
      keyAlgMap.put(EdECObjectIdentifiers.id_Ed448, "id_Ed448");
   }
}
