package org.bouncycastle.cms;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.BERSequence;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DLSet;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.cms.SignedData;
import org.bouncycastle.asn1.cms.SignerInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cert.X509AttributeCertificateHolder;
import org.bouncycastle.cert.X509CRLHolder;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.operator.DefaultDigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.util.Encodable;
import org.bouncycastle.util.Store;

public class CMSSignedData implements Encodable {
   private static final CMSSignedHelper HELPER = CMSSignedHelper.INSTANCE;
   private static final DefaultDigestAlgorithmIdentifierFinder DIGEST_ALG_ID_FINDER = new DefaultDigestAlgorithmIdentifierFinder();
   SignedData signedData;
   ContentInfo contentInfo;
   CMSTypedData signedContent;
   SignerInformationStore signerInfoStore;
   private Map hashes;

   private CMSSignedData(CMSSignedData var1) {
      this.signedData = var1.signedData;
      this.contentInfo = var1.contentInfo;
      this.signedContent = var1.signedContent;
      this.signerInfoStore = var1.signerInfoStore;
   }

   public CMSSignedData(byte[] var1) throws CMSException {
      this(CMSUtils.readContentInfo(var1));
   }

   public CMSSignedData(CMSProcessable var1, byte[] var2) throws CMSException {
      this(var1, CMSUtils.readContentInfo(var2));
   }

   public CMSSignedData(Map var1, byte[] var2) throws CMSException {
      this(var1, CMSUtils.readContentInfo(var2));
   }

   public CMSSignedData(CMSProcessable var1, InputStream var2) throws CMSException {
      this(var1, CMSUtils.readContentInfo(new ASN1InputStream(var2)));
   }

   public CMSSignedData(InputStream var1) throws CMSException {
      this(CMSUtils.readContentInfo(var1));
   }

   public CMSSignedData(final CMSProcessable var1, ContentInfo var2) throws CMSException {
      if (var1 instanceof CMSTypedData) {
         this.signedContent = (CMSTypedData)var1;
      } else {
         this.signedContent = new CMSTypedData() {
            @Override
            public ASN1ObjectIdentifier getContentType() {
               return CMSSignedData.this.signedData.getEncapContentInfo().getContentType();
            }

            @Override
            public void write(OutputStream var1x) throws IOException, CMSException {
               var1.write(var1x);
            }

            @Override
            public Object getContent() {
               return var1.getContent();
            }
         };
      }

      this.contentInfo = var2;
      this.signedData = this.getSignedData();
   }

   public CMSSignedData(Map var1, ContentInfo var2) throws CMSException {
      this.hashes = var1;
      this.contentInfo = var2;
      this.signedData = this.getSignedData();
   }

   public CMSSignedData(ContentInfo var1) throws CMSException {
      this.contentInfo = var1;
      this.signedData = this.getSignedData();
      ASN1Encodable var2 = this.signedData.getEncapContentInfo().getContent();
      if (var2 != null) {
         if (var2 instanceof ASN1OctetString) {
            this.signedContent = new CMSProcessableByteArray(this.signedData.getEncapContentInfo().getContentType(), ((ASN1OctetString)var2).getOctets());
         } else {
            this.signedContent = new PKCS7ProcessableObject(this.signedData.getEncapContentInfo().getContentType(), var2);
         }
      } else {
         this.signedContent = null;
      }
   }

   private SignedData getSignedData() throws CMSException {
      try {
         return SignedData.getInstance(this.contentInfo.getContent());
      } catch (ClassCastException var2) {
         throw new CMSException("Malformed content.", var2);
      } catch (IllegalArgumentException var3) {
         throw new CMSException("Malformed content.", var3);
      }
   }

   public int getVersion() {
      return this.signedData.getVersion().intValueExact();
   }

   public SignerInformationStore getSignerInfos() {
      if (this.signerInfoStore == null) {
         ASN1Set var1 = this.signedData.getSignerInfos();
         ArrayList var2 = new ArrayList();

         for (int var3 = 0; var3 != var1.size(); var3++) {
            SignerInfo var4 = SignerInfo.getInstance(var1.getObjectAt(var3));
            ASN1ObjectIdentifier var5 = this.signedData.getEncapContentInfo().getContentType();
            if (this.hashes == null) {
               var2.add(new SignerInformation(var4, var5, this.signedContent, null));
            } else {
               Object var6 = this.hashes.keySet().iterator().next();
               byte[] var7 = var6 instanceof String
                  ? (byte[])this.hashes.get(var4.getDigestAlgorithm().getAlgorithm().getId())
                  : (byte[])this.hashes.get(var4.getDigestAlgorithm().getAlgorithm());
               var2.add(new SignerInformation(var4, var5, null, var7));
            }
         }

         this.signerInfoStore = new SignerInformationStore(var2);
      }

      return this.signerInfoStore;
   }

   public boolean isDetachedSignature() {
      return this.signedData.getEncapContentInfo().getContent() == null && this.signedData.getSignerInfos().size() > 0;
   }

   public boolean isCertificateManagementMessage() {
      return this.signedData.getEncapContentInfo().getContent() == null && this.signedData.getSignerInfos().size() == 0;
   }

   public Store<X509CertificateHolder> getCertificates() {
      return HELPER.getCertificates(this.signedData.getCertificates());
   }

   public Store<X509CRLHolder> getCRLs() {
      return HELPER.getCRLs(this.signedData.getCRLs());
   }

   public Store<X509AttributeCertificateHolder> getAttributeCertificates() {
      return HELPER.getAttributeCertificates(this.signedData.getCertificates());
   }

   public Store getOtherRevocationInfo(ASN1ObjectIdentifier var1) {
      return HELPER.getOtherRevocationInfo(var1, this.signedData.getCRLs());
   }

   public Set<AlgorithmIdentifier> getDigestAlgorithmIDs() {
      HashSet var1 = new HashSet();
      Enumeration var2 = this.signedData.getDigestAlgorithms().getObjects();

      while (var2.hasMoreElements()) {
         var1.add(AlgorithmIdentifier.getInstance(var2.nextElement()));
      }

      return Collections.unmodifiableSet(var1);
   }

   public String getSignedContentTypeOID() {
      return this.signedData.getEncapContentInfo().getContentType().getId();
   }

   public CMSTypedData getSignedContent() {
      return this.signedContent;
   }

   public ContentInfo toASN1Structure() {
      return this.contentInfo;
   }

   @Override
   public byte[] getEncoded() throws IOException {
      return this.contentInfo.getEncoded();
   }

   public byte[] getEncoded(String var1) throws IOException {
      return this.contentInfo.getEncoded(var1);
   }

   public boolean verifySignatures(SignerInformationVerifierProvider var1) throws CMSException {
      return this.verifySignatures(var1, false);
   }

   public boolean verifySignatures(SignerInformationVerifierProvider var1, boolean var2) throws CMSException {
      for (SignerInformation var5 : this.getSignerInfos().getSigners()) {
         try {
            SignerInformationVerifier var6 = var1.get(var5.getSID());
            if (!var5.verify(var6)) {
               return false;
            }

            if (!var2) {
               Collection var7 = var5.getCounterSignatures().getSigners();
               Iterator var8 = var7.iterator();

               while (var8.hasNext()) {
                  if (!this.verifyCounterSignature((SignerInformation)var8.next(), var1)) {
                     return false;
                  }
               }
            }
         } catch (OperatorCreationException var9) {
            throw new CMSException("failure in verifier provider: " + var9.getMessage(), var9);
         }
      }

      return true;
   }

   private boolean verifyCounterSignature(SignerInformation var1, SignerInformationVerifierProvider var2) throws OperatorCreationException, CMSException {
      SignerInformationVerifier var3 = var2.get(var1.getSID());
      if (!var1.verify(var3)) {
         return false;
      } else {
         Collection var4 = var1.getCounterSignatures().getSigners();
         Iterator var5 = var4.iterator();

         while (var5.hasNext()) {
            if (!this.verifyCounterSignature((SignerInformation)var5.next(), var2)) {
               return false;
            }
         }

         return true;
      }
   }

   public static CMSSignedData addDigestAlgorithm(CMSSignedData var0, AlgorithmIdentifier var1) {
      return addDigestAlgorithm(var0, var1, DIGEST_ALG_ID_FINDER);
   }

   public static CMSSignedData addDigestAlgorithm(CMSSignedData var0, AlgorithmIdentifier var1, DigestAlgorithmIdentifierFinder var2) {
      Set var3 = var0.getDigestAlgorithmIDs();
      AlgorithmIdentifier var4 = HELPER.fixDigestAlgID(var1, var2);
      if (var3.contains(var4)) {
         return var0;
      } else {
         CMSSignedData var5 = new CMSSignedData(var0);
         HashSet var6 = new HashSet();
         Iterator var7 = var3.iterator();

         while (var7.hasNext()) {
            var6.add(HELPER.fixDigestAlgID((AlgorithmIdentifier)var7.next(), var2));
         }

         var6.add(var4);
         ASN1Set var8 = CMSUtils.convertToDlSet(var6);
         ASN1Sequence var9 = (ASN1Sequence)var0.signedData.toASN1Primitive();
         ASN1EncodableVector var10 = new ASN1EncodableVector(var9.size());
         var10.add(var9.getObjectAt(0));
         var10.add(var8);

         for (int var11 = 2; var11 != var9.size(); var11++) {
            var10.add(var9.getObjectAt(var11));
         }

         var5.signedData = SignedData.getInstance(new BERSequence(var10));
         var5.contentInfo = new ContentInfo(var5.contentInfo.getContentType(), var5.signedData);
         return var5;
      }
   }

   public static CMSSignedData replaceSigners(CMSSignedData var0, SignerInformationStore var1) {
      return replaceSigners(var0, var1, DIGEST_ALG_ID_FINDER);
   }

   public static CMSSignedData replaceSigners(CMSSignedData var0, SignerInformationStore var1, DigestAlgorithmIdentifierFinder var2) {
      CMSSignedData var3 = new CMSSignedData(var0);
      var3.signerInfoStore = var1;
      HashSet var4 = new HashSet();
      Collection var5 = var1.getSigners();
      ASN1EncodableVector var6 = new ASN1EncodableVector(var5.size());

      for (SignerInformation var8 : var5) {
         CMSUtils.addDigestAlgs(var4, var8, var2);
         var6.add(var8.toASN1Structure());
      }

      Set var16 = var0.getDigestAlgorithmIDs();
      AlgorithmIdentifier[] var9 = var16.toArray(new AlgorithmIdentifier[var16.size()]);
      AlgorithmIdentifier[] var10 = var4.toArray(new AlgorithmIdentifier[var4.size()]);
      compareAndReplaceAlgIds(var9, var10);
      DLSet var11 = new DLSet(var10);
      DLSet var12 = new DLSet(var6);
      ASN1Sequence var13 = (ASN1Sequence)var0.signedData.toASN1Primitive();
      var6 = new ASN1EncodableVector(var13.size());
      var6.add(var13.getObjectAt(0));
      var6.add(var11);

      for (int var14 = 2; var14 != var13.size() - 1; var14++) {
         var6.add(var13.getObjectAt(var14));
      }

      var6.add(var12);
      var3.signedData = SignedData.getInstance(new BERSequence(var6));
      var3.contentInfo = new ContentInfo(var3.contentInfo.getContentType(), var3.signedData);
      return var3;
   }

   private static void compareAndReplaceAlgIds(AlgorithmIdentifier[] var0, AlgorithmIdentifier[] var1) {
      for (int var2 = 0; var2 != var1.length; var2++) {
         AlgorithmIdentifier var3 = var1[var2];

         for (int var4 = 0; var4 != var0.length; var4++) {
            AlgorithmIdentifier var5 = var0[var4];
            if (var3.getAlgorithm().equals(var5.getAlgorithm()) && (var3.getParameters() == null || DERNull.INSTANCE.equals(var3.getParameters()))) {
               var1[var2] = var5;
               break;
            }
         }
      }
   }

   public static CMSSignedData replaceCertificatesAndCRLs(CMSSignedData var0, Store var1, Store var2, Store var3) throws CMSException {
      CMSSignedData var4 = new CMSSignedData(var0);
      ASN1Set var5 = null;
      ASN1Set var6 = null;
      if (var1 != null || var2 != null) {
         ArrayList var7 = new ArrayList();
         if (var1 != null) {
            var7.addAll(CMSUtils.getCertificatesFromStore(var1));
         }

         if (var2 != null) {
            var7.addAll(CMSUtils.getAttributeCertificatesFromStore(var2));
         }

         ASN1Set var8 = CMSUtils.createBerSetFromList(var7);
         if (var8.size() != 0) {
            var5 = var8;
         }
      }

      if (var3 != null) {
         ASN1Set var9 = CMSUtils.createBerSetFromList(CMSUtils.getCRLsFromStore(var3));
         if (var9.size() != 0) {
            var6 = var9;
         }
      }

      var4.signedData = new SignedData(
         var0.signedData.getDigestAlgorithms(), var0.signedData.getEncapContentInfo(), var5, var6, var0.signedData.getSignerInfos()
      );
      var4.contentInfo = new ContentInfo(var4.contentInfo.getContentType(), var4.signedData);
      return var4;
   }
}
