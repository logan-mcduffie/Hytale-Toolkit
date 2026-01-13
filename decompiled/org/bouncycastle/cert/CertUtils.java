package org.bouncycastle.cert;

import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.AttributeCertificate;
import org.bouncycastle.asn1.x509.AttributeCertificateInfo;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x509.CertificateList;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.ExtensionsGenerator;
import org.bouncycastle.asn1.x509.TBSCertList;
import org.bouncycastle.asn1.x509.TBSCertificate;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.util.Properties;

class CertUtils {
   private static Set EMPTY_SET = Collections.unmodifiableSet(new HashSet());
   private static List EMPTY_LIST = Collections.unmodifiableList(new ArrayList());

   static ASN1Primitive parseNonEmptyASN1(byte[] var0) throws IOException {
      ASN1Primitive var1 = ASN1Primitive.fromByteArray(var0);
      if (var1 == null) {
         throw new IOException("no content found");
      } else {
         return var1;
      }
   }

   static X509CertificateHolder generateFullCert(ContentSigner var0, TBSCertificate var1) {
      try {
         return new X509CertificateHolder(generateStructure(var1, var0.getAlgorithmIdentifier(), generateSig(var0, var1)));
      } catch (IOException var3) {
         throw new IllegalStateException("cannot produce certificate signature");
      }
   }

   static X509AttributeCertificateHolder generateFullAttrCert(ContentSigner var0, AttributeCertificateInfo var1) {
      try {
         return new X509AttributeCertificateHolder(generateAttrStructure(var1, var0.getAlgorithmIdentifier(), generateSig(var0, var1)));
      } catch (IOException var3) {
         throw new IllegalStateException("cannot produce attribute certificate signature");
      }
   }

   private static Certificate generateStructure(TBSCertificate var0, AlgorithmIdentifier var1, byte[] var2) {
      ASN1EncodableVector var3 = new ASN1EncodableVector();
      var3.add(var0);
      var3.add(var1);
      var3.add(new DERBitString(var2));
      return Certificate.getInstance(new DERSequence(var3));
   }

   private static AttributeCertificate generateAttrStructure(AttributeCertificateInfo var0, AlgorithmIdentifier var1, byte[] var2) {
      ASN1EncodableVector var3 = new ASN1EncodableVector();
      var3.add(var0);
      var3.add(var1);
      var3.add(new DERBitString(var2));
      return AttributeCertificate.getInstance(new DERSequence(var3));
   }

   private static CertificateList generateCRLStructure(TBSCertList var0, AlgorithmIdentifier var1, byte[] var2) {
      ASN1EncodableVector var3 = new ASN1EncodableVector();
      var3.add(var0);
      var3.add(var1);
      var3.add(new DERBitString(var2));
      return CertificateList.getInstance(new DERSequence(var3));
   }

   static Set getCriticalExtensionOIDs(Extensions var0) {
      return var0 == null ? EMPTY_SET : Collections.unmodifiableSet(new HashSet<>(Arrays.asList(var0.getCriticalExtensionOIDs())));
   }

   static Set getNonCriticalExtensionOIDs(Extensions var0) {
      return var0 == null ? EMPTY_SET : Collections.unmodifiableSet(new HashSet<>(Arrays.asList(var0.getNonCriticalExtensionOIDs())));
   }

   static List getExtensionOIDs(Extensions var0) {
      return var0 == null ? EMPTY_LIST : Collections.unmodifiableList(Arrays.asList(var0.getExtensionOIDs()));
   }

   static void addExtension(ExtensionsGenerator var0, ASN1ObjectIdentifier var1, boolean var2, ASN1Encodable var3) throws CertIOException {
      try {
         var0.addExtension(var1, var2, var3);
      } catch (IOException var5) {
         throw new CertIOException("cannot encode extension: " + var5.getMessage(), var5);
      }
   }

   static DERBitString booleanToBitString(boolean[] var0) {
      byte[] var1 = new byte[(var0.length + 7) / 8];

      for (int var2 = 0; var2 != var0.length; var2++) {
         var1[var2 / 8] = (byte)(var1[var2 / 8] | (var0[var2] ? 1 << 7 - var2 % 8 : 0));
      }

      int var3 = var0.length % 8;
      return var3 == 0 ? new DERBitString(var1) : new DERBitString(var1, 8 - var3);
   }

   static boolean[] bitStringToBoolean(ASN1BitString var0) {
      if (var0 != null) {
         byte[] var1 = var0.getBytes();
         boolean[] var2 = new boolean[var1.length * 8 - var0.getPadBits()];

         for (int var3 = 0; var3 != var2.length; var3++) {
            var2[var3] = (var1[var3 / 8] & 128 >>> var3 % 8) != 0;
         }

         return var2;
      } else {
         return null;
      }
   }

   static Date recoverDate(ASN1GeneralizedTime var0) {
      try {
         return var0.getDate();
      } catch (ParseException var2) {
         throw new IllegalStateException("unable to recover date: " + var2.getMessage());
      }
   }

   static boolean isAlgIdEqual(AlgorithmIdentifier var0, AlgorithmIdentifier var1) {
      if (!var0.getAlgorithm().equals(var1.getAlgorithm())) {
         return false;
      } else {
         if (Properties.isOverrideSet("org.bouncycastle.x509.allow_absent_equiv_NULL")) {
            if (var0.getParameters() == null) {
               if (var1.getParameters() != null && !var1.getParameters().equals(DERNull.INSTANCE)) {
                  return false;
               }

               return true;
            }

            if (var1.getParameters() == null) {
               if (var0.getParameters() != null && !var0.getParameters().equals(DERNull.INSTANCE)) {
                  return false;
               }

               return true;
            }
         }

         if (var0.getParameters() != null) {
            return var0.getParameters().equals(var1.getParameters());
         } else {
            return var1.getParameters() != null ? var1.getParameters().equals(var0.getParameters()) : true;
         }
      }
   }

   static ExtensionsGenerator doReplaceExtension(ExtensionsGenerator var0, Extension var1) {
      boolean var2 = false;
      Extensions var3 = var0.generate();
      var0 = new ExtensionsGenerator();
      Enumeration var4 = var3.oids();

      while (var4.hasMoreElements()) {
         ASN1ObjectIdentifier var5 = (ASN1ObjectIdentifier)var4.nextElement();
         if (var5.equals(var1.getExtnId())) {
            var2 = true;
            var0.addExtension(var1);
         } else {
            var0.addExtension(var3.getExtension(var5));
         }
      }

      if (!var2) {
         throw new IllegalArgumentException("replace - original extension (OID = " + var1.getExtnId() + ") not found");
      } else {
         return var0;
      }
   }

   static ExtensionsGenerator doRemoveExtension(ExtensionsGenerator var0, ASN1ObjectIdentifier var1) {
      boolean var2 = false;
      Extensions var3 = var0.generate();
      var0 = new ExtensionsGenerator();
      Enumeration var4 = var3.oids();

      while (var4.hasMoreElements()) {
         ASN1ObjectIdentifier var5 = (ASN1ObjectIdentifier)var4.nextElement();
         if (var5.equals(var1)) {
            var2 = true;
         } else {
            var0.addExtension(var3.getExtension(var5));
         }
      }

      if (!var2) {
         throw new IllegalArgumentException("remove - extension (OID = " + var1 + ") not found");
      } else {
         return var0;
      }
   }

   private static byte[] generateSig(ContentSigner var0, ASN1Object var1) throws IOException {
      OutputStream var2 = var0.getOutputStream();
      var1.encodeTo(var2, "DER");
      var2.close();
      return var0.getSignature();
   }

   static ASN1TaggedObject trimExtensions(int var0, Extensions var1) {
      ASN1Sequence var2 = ASN1Sequence.getInstance(var1.toASN1Primitive());
      ASN1EncodableVector var3 = new ASN1EncodableVector();

      for (int var4 = 0; var4 != var2.size(); var4++) {
         ASN1Sequence var5 = ASN1Sequence.getInstance(var2.getObjectAt(var4));
         if (!Extension.altSignatureValue.equals(var5.getObjectAt(0))) {
            var3.add(var5);
         }
      }

      return new DERTaggedObject(true, var0, new DERSequence(var3));
   }
}
