package org.bouncycastle.cms;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1SequenceParser;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1SetParser;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.BEROctetString;
import org.bouncycastle.asn1.BEROctetStringGenerator;
import org.bouncycastle.asn1.BERSequenceGenerator;
import org.bouncycastle.asn1.BERSet;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.DLSet;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.cms.EncryptedContentInfo;
import org.bouncycastle.asn1.cms.OriginatorInfo;
import org.bouncycastle.asn1.cms.OtherRevocationInfoFormat;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.ocsp.OCSPResponse;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.rosstandart.RosstandartObjectIdentifiers;
import org.bouncycastle.asn1.sec.SECObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.cert.X509AttributeCertificateHolder;
import org.bouncycastle.cert.X509CRLHolder;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.operator.DigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.OutputAEADEncryptor;
import org.bouncycastle.operator.OutputEncryptor;
import org.bouncycastle.util.Store;
import org.bouncycastle.util.Strings;
import org.bouncycastle.util.io.Streams;
import org.bouncycastle.util.io.TeeInputStream;
import org.bouncycastle.util.io.TeeOutputStream;

class CMSUtils {
   private static final Set<String> des = new HashSet<>();
   private static final Set mqvAlgs = new HashSet();
   private static final Set ecAlgs = new HashSet();
   private static final Set gostAlgs = new HashSet();

   static boolean isMQV(ASN1ObjectIdentifier var0) {
      return mqvAlgs.contains(var0);
   }

   static boolean isEC(ASN1ObjectIdentifier var0) {
      return ecAlgs.contains(var0);
   }

   static boolean isGOST(ASN1ObjectIdentifier var0) {
      return gostAlgs.contains(var0);
   }

   static boolean isRFC2631(ASN1ObjectIdentifier var0) {
      return var0.equals(PKCSObjectIdentifiers.id_alg_ESDH) || var0.equals(PKCSObjectIdentifiers.id_alg_SSDH);
   }

   static boolean isDES(String var0) {
      String var1 = Strings.toUpperCase(var0);
      return des.contains(var1);
   }

   static boolean isEquivalent(AlgorithmIdentifier var0, AlgorithmIdentifier var1) {
      if (var0 != null && var1 != null) {
         if (!var0.getAlgorithm().equals(var1.getAlgorithm())) {
            return false;
         } else {
            ASN1Encodable var2 = var0.getParameters();
            ASN1Encodable var3 = var1.getParameters();
            return var2 != null ? var2.equals(var3) || var2.equals(DERNull.INSTANCE) && var3 == null : var3 == null || var3.equals(DERNull.INSTANCE);
         }
      } else {
         return false;
      }
   }

   static ContentInfo readContentInfo(byte[] var0) throws CMSException {
      return readContentInfo(new ASN1InputStream(var0));
   }

   static ContentInfo readContentInfo(InputStream var0) throws CMSException {
      return readContentInfo(new ASN1InputStream(var0));
   }

   static ASN1Set convertToDlSet(Set<AlgorithmIdentifier> var0) {
      return new DLSet((AlgorithmIdentifier[])var0.toArray(new AlgorithmIdentifier[var0.size()]));
   }

   static void addDigestAlgs(Set<AlgorithmIdentifier> var0, SignerInformation var1, DigestAlgorithmIdentifierFinder var2) {
      var0.add(CMSSignedHelper.INSTANCE.fixDigestAlgID(var1.getDigestAlgorithmID(), var2));

      for (SignerInformation var5 : var1.getCounterSignatures()) {
         var0.add(CMSSignedHelper.INSTANCE.fixDigestAlgID(var5.getDigestAlgorithmID(), var2));
      }
   }

   static List getCertificatesFromStore(Store var0) throws CMSException {
      ArrayList var1 = new ArrayList();

      try {
         for (X509CertificateHolder var3 : var0.getMatches(null)) {
            var1.add(var3.toASN1Structure());
         }

         return var1;
      } catch (ClassCastException var4) {
         throw new CMSException("error processing certs", var4);
      }
   }

   static List getAttributeCertificatesFromStore(Store var0) throws CMSException {
      ArrayList var1 = new ArrayList();

      try {
         for (X509AttributeCertificateHolder var3 : var0.getMatches(null)) {
            var1.add(new DERTaggedObject(false, 2, var3.toASN1Structure()));
         }

         return var1;
      } catch (ClassCastException var4) {
         throw new CMSException("error processing certs", var4);
      }
   }

   static List getCRLsFromStore(Store var0) throws CMSException {
      ArrayList var1 = new ArrayList();

      try {
         for (Object var3 : var0.getMatches(null)) {
            if (var3 instanceof X509CRLHolder) {
               X509CRLHolder var4 = (X509CRLHolder)var3;
               var1.add(var4.toASN1Structure());
            } else if (var3 instanceof OtherRevocationInfoFormat) {
               OtherRevocationInfoFormat var6 = OtherRevocationInfoFormat.getInstance(var3);
               validateInfoFormat(var6);
               var1.add(new DERTaggedObject(false, 1, var6));
            } else if (var3 instanceof ASN1TaggedObject) {
               var1.add(var3);
            }
         }

         return var1;
      } catch (ClassCastException var5) {
         throw new CMSException("error processing certs", var5);
      }
   }

   static void validateInfoFormat(OtherRevocationInfoFormat var0) {
      if (CMSObjectIdentifiers.id_ri_ocsp_response.equals(var0.getInfoFormat())) {
         OCSPResponse var1 = OCSPResponse.getInstance(var0.getInfo());
         if (0 != var1.getResponseStatus().getIntValue()) {
            throw new IllegalArgumentException("cannot add unsuccessful OCSP response to CMS SignedData");
         }
      }
   }

   static Collection getOthersFromStore(ASN1ObjectIdentifier var0, Store var1) {
      ArrayList var2 = new ArrayList();

      for (ASN1Encodable var4 : var1.getMatches(null)) {
         OtherRevocationInfoFormat var5 = new OtherRevocationInfoFormat(var0, var4);
         validateInfoFormat(var5);
         var2.add(new DERTaggedObject(false, 1, var5));
      }

      return var2;
   }

   static ASN1Set createBerSetFromList(List var0) {
      ASN1EncodableVector var1 = new ASN1EncodableVector();
      Iterator var2 = var0.iterator();

      while (var2.hasNext()) {
         var1.add((ASN1Encodable)var2.next());
      }

      return new BERSet(var1);
   }

   static ASN1Set createDlSetFromList(List var0) {
      ASN1EncodableVector var1 = new ASN1EncodableVector();
      Iterator var2 = var0.iterator();

      while (var2.hasNext()) {
         var1.add((ASN1Encodable)var2.next());
      }

      return new DLSet(var1);
   }

   static ASN1Set createDerSetFromList(List var0) {
      ASN1EncodableVector var1 = new ASN1EncodableVector();
      Iterator var2 = var0.iterator();

      while (var2.hasNext()) {
         var1.add((ASN1Encodable)var2.next());
      }

      return new DERSet(var1);
   }

   static OutputStream createBEROctetOutputStream(OutputStream var0, int var1, boolean var2, int var3) throws IOException {
      BEROctetStringGenerator var4 = new BEROctetStringGenerator(var0, var1, var2);
      return var3 != 0 ? var4.getOctetOutputStream(new byte[var3]) : var4.getOctetOutputStream();
   }

   private static ContentInfo readContentInfo(ASN1InputStream var0) throws CMSException {
      try {
         ContentInfo var1 = ContentInfo.getInstance(var0.readObject());
         if (var1 == null) {
            throw new CMSException("No content found.");
         } else {
            return var1;
         }
      } catch (IOException var2) {
         throw new CMSException("IOException reading content.", var2);
      } catch (ClassCastException var3) {
         throw new CMSException("Malformed content.", var3);
      } catch (IllegalArgumentException var4) {
         throw new CMSException("Malformed content.", var4);
      }
   }

   public static byte[] streamToByteArray(InputStream var0) throws IOException {
      return Streams.readAll(var0);
   }

   public static byte[] streamToByteArray(InputStream var0, int var1) throws IOException {
      return Streams.readAllLimited(var0, var1);
   }

   static InputStream attachDigestsToInputStream(Collection var0, InputStream var1) {
      Object var2 = var1;

      for (DigestCalculator var4 : var0) {
         var2 = new TeeInputStream((InputStream)var2, var4.getOutputStream());
      }

      return (InputStream)var2;
   }

   static OutputStream attachSignersToOutputStream(Collection var0, OutputStream var1) {
      OutputStream var2 = var1;

      for (SignerInfoGenerator var4 : var0) {
         var2 = getSafeTeeOutputStream(var2, var4.getCalculatingOutputStream());
      }

      return var2;
   }

   static OutputStream getSafeOutputStream(OutputStream var0) {
      return (OutputStream)(var0 == null ? new NullOutputStream() : var0);
   }

   static OutputStream getSafeTeeOutputStream(OutputStream var0, OutputStream var1) {
      return (OutputStream)(var0 == null ? getSafeOutputStream(var1) : (var1 == null ? getSafeOutputStream(var0) : new TeeOutputStream(var0, var1)));
   }

   static EncryptedContentInfo getEncryptedContentInfo(CMSTypedData var0, OutputEncryptor var1, byte[] var2) {
      return getEncryptedContentInfo(var0.getContentType(), var1.getAlgorithmIdentifier(), var2);
   }

   static EncryptedContentInfo getEncryptedContentInfo(ASN1ObjectIdentifier var0, AlgorithmIdentifier var1, byte[] var2) {
      BEROctetString var3 = new BEROctetString(var2);
      return new EncryptedContentInfo(var0, var1, var3);
   }

   static ASN1EncodableVector getRecipentInfos(GenericKey var0, List var1) throws CMSException {
      ASN1EncodableVector var2 = new ASN1EncodableVector();

      for (RecipientInfoGenerator var4 : var1) {
         var2.add(var4.generate(var0));
      }

      return var2;
   }

   static void addRecipientInfosToGenerator(ASN1EncodableVector var0, BERSequenceGenerator var1, boolean var2) throws IOException {
      if (var2) {
         var1.getRawOutputStream().write(new BERSet(var0).getEncoded());
      } else {
         var1.getRawOutputStream().write(new DERSet(var0).getEncoded());
      }
   }

   static void addOriginatorInfoToGenerator(BERSequenceGenerator var0, OriginatorInfo var1) throws IOException {
      if (var1 != null) {
         var0.addObject((ASN1Primitive)(new DERTaggedObject(false, 0, var1)));
      }
   }

   static void addAttriSetToGenerator(BERSequenceGenerator var0, CMSAttributeTableGenerator var1, int var2, Map var3) throws IOException {
      if (var1 != null) {
         var0.addObject((ASN1Primitive)(new DERTaggedObject(false, var2, new BERSet(var1.getAttributes(var3).toASN1EncodableVector()))));
      }
   }

   static ASN1Set processAuthAttrSet(CMSAttributeTableGenerator var0, OutputAEADEncryptor var1) throws IOException {
      DERSet var2 = null;
      if (var0 != null) {
         AttributeTable var3 = var0.getAttributes(Collections.EMPTY_MAP);
         var2 = new DERSet(var3.toASN1EncodableVector());
         var1.getAADStream().write(var2.getEncoded("DER"));
      }

      return var2;
   }

   static AttributeTable getAttributesTable(ASN1SetParser var0) throws IOException {
      if (var0 == null) {
         return null;
      } else {
         ASN1EncodableVector var1 = new ASN1EncodableVector();

         ASN1Encodable var2;
         while ((var2 = var0.readObject()) != null) {
            ASN1SequenceParser var3 = (ASN1SequenceParser)var2;
            var1.add(var3.toASN1Primitive());
         }

         return new AttributeTable(new DERSet(var1));
      }
   }

   static ASN1Set getAttrDLSet(CMSAttributeTableGenerator var0) {
      return var0 != null ? new DLSet(var0.getAttributes(Collections.EMPTY_MAP).toASN1EncodableVector()) : null;
   }

   static ASN1Set getAttrBERSet(CMSAttributeTableGenerator var0) {
      return var0 != null ? new BERSet(var0.getAttributes(Collections.EMPTY_MAP).toASN1EncodableVector()) : null;
   }

   static byte[] encodeObj(ASN1Encodable var0) throws IOException {
      return var0 != null ? var0.toASN1Primitive().getEncoded() : null;
   }

   static {
      des.add("DES");
      des.add("DESEDE");
      des.add(OIWObjectIdentifiers.desCBC.getId());
      des.add(PKCSObjectIdentifiers.des_EDE3_CBC.getId());
      des.add(PKCSObjectIdentifiers.id_alg_CMS3DESwrap.getId());
      mqvAlgs.add(X9ObjectIdentifiers.mqvSinglePass_sha1kdf_scheme);
      mqvAlgs.add(SECObjectIdentifiers.mqvSinglePass_sha224kdf_scheme);
      mqvAlgs.add(SECObjectIdentifiers.mqvSinglePass_sha256kdf_scheme);
      mqvAlgs.add(SECObjectIdentifiers.mqvSinglePass_sha384kdf_scheme);
      mqvAlgs.add(SECObjectIdentifiers.mqvSinglePass_sha512kdf_scheme);
      ecAlgs.add(X9ObjectIdentifiers.dhSinglePass_cofactorDH_sha1kdf_scheme);
      ecAlgs.add(X9ObjectIdentifiers.dhSinglePass_stdDH_sha1kdf_scheme);
      ecAlgs.add(SECObjectIdentifiers.dhSinglePass_cofactorDH_sha224kdf_scheme);
      ecAlgs.add(SECObjectIdentifiers.dhSinglePass_stdDH_sha224kdf_scheme);
      ecAlgs.add(SECObjectIdentifiers.dhSinglePass_cofactorDH_sha256kdf_scheme);
      ecAlgs.add(SECObjectIdentifiers.dhSinglePass_stdDH_sha256kdf_scheme);
      ecAlgs.add(SECObjectIdentifiers.dhSinglePass_cofactorDH_sha384kdf_scheme);
      ecAlgs.add(SECObjectIdentifiers.dhSinglePass_stdDH_sha384kdf_scheme);
      ecAlgs.add(SECObjectIdentifiers.dhSinglePass_cofactorDH_sha512kdf_scheme);
      ecAlgs.add(SECObjectIdentifiers.dhSinglePass_stdDH_sha512kdf_scheme);
      gostAlgs.add(CryptoProObjectIdentifiers.gostR3410_2001_CryptoPro_ESDH);
      gostAlgs.add(RosstandartObjectIdentifiers.id_tc26_agreement_gost_3410_12_256);
      gostAlgs.add(RosstandartObjectIdentifiers.id_tc26_agreement_gost_3410_12_512);
   }
}
