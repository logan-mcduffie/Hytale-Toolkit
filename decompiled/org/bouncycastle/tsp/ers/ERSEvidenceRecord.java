package org.bouncycastle.tsp.ers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.cms.SignedData;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.tsp.ArchiveTimeStamp;
import org.bouncycastle.asn1.tsp.ArchiveTimeStampChain;
import org.bouncycastle.asn1.tsp.ArchiveTimeStampSequence;
import org.bouncycastle.asn1.tsp.EvidenceRecord;
import org.bouncycastle.asn1.tsp.TSTInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cms.SignerInformationVerifier;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.tsp.TSPException;
import org.bouncycastle.tsp.TimeStampRequest;
import org.bouncycastle.tsp.TimeStampRequestGenerator;
import org.bouncycastle.tsp.TimeStampResponse;
import org.bouncycastle.util.io.Streams;

public class ERSEvidenceRecord {
   private final EvidenceRecord evidenceRecord;
   private final DigestCalculatorProvider digestCalculatorProvider;
   private final ERSArchiveTimeStamp firstArchiveTimeStamp;
   private final ERSArchiveTimeStamp lastArchiveTimeStamp;
   private final byte[] previousChainsDigest;
   private final DigestCalculator digCalc;
   private final ArchiveTimeStamp primaryArchiveTimeStamp;

   public ERSEvidenceRecord(InputStream var1, DigestCalculatorProvider var2) throws TSPException, ERSException, IOException {
      this(EvidenceRecord.getInstance(Streams.readAll(var1)), var2);
   }

   public ERSEvidenceRecord(byte[] var1, DigestCalculatorProvider var2) throws TSPException, ERSException {
      this(EvidenceRecord.getInstance(var1), var2);
   }

   public ERSEvidenceRecord(EvidenceRecord var1, DigestCalculatorProvider var2) throws TSPException, ERSException {
      this.evidenceRecord = var1;
      this.digestCalculatorProvider = var2;
      ArchiveTimeStampSequence var3 = var1.getArchiveTimeStampSequence();
      ArchiveTimeStampChain[] var4 = var3.getArchiveTimeStampChains();
      this.primaryArchiveTimeStamp = var4[0].getArchiveTimestamps()[0];
      this.validateChains(var4);
      ArchiveTimeStampChain var5 = var4[var4.length - 1];
      ArchiveTimeStamp[] var6 = var5.getArchiveTimestamps();
      this.lastArchiveTimeStamp = new ERSArchiveTimeStamp(var6[var6.length - 1], var2);
      if (var4.length > 1) {
         try {
            ASN1EncodableVector var7 = new ASN1EncodableVector();

            for (int var8 = 0; var8 != var4.length - 1; var8++) {
               var7.add(var4[var8]);
            }

            this.digCalc = var2.get(this.lastArchiveTimeStamp.getDigestAlgorithmIdentifier());
            OutputStream var10 = this.digCalc.getOutputStream();
            var10.write(new DERSequence(var7).getEncoded("DER"));
            var10.close();
            this.previousChainsDigest = this.digCalc.getDigest();
         } catch (Exception var9) {
            throw new ERSException(var9.getMessage(), var9);
         }
      } else {
         this.digCalc = null;
         this.previousChainsDigest = null;
      }

      this.firstArchiveTimeStamp = new ERSArchiveTimeStamp(this.previousChainsDigest, var6[0], var2);
   }

   private void validateChains(ArchiveTimeStampChain[] var1) throws ERSException, TSPException {
      for (int var2 = 0; var2 != var1.length; var2++) {
         ArchiveTimeStamp[] var3 = var1[var2].getArchiveTimestamps();
         ArchiveTimeStamp var4 = var3[0];
         AlgorithmIdentifier var5 = var3[0].getDigestAlgorithmIdentifier();

         for (int var6 = 1; var6 != var3.length; var6++) {
            ArchiveTimeStamp var7 = var3[var6];
            if (!var5.equals(var7.getDigestAlgorithmIdentifier())) {
               throw new ERSException("invalid digest algorithm in chain");
            }

            ContentInfo var8 = var7.getTimeStamp();
            if (!var8.getContentType().equals(CMSObjectIdentifiers.signedData)) {
               throw new TSPException("cannot identify TSTInfo");
            }

            TSTInfo var9 = this.extractTimeStamp(var8);

            try {
               DigestCalculator var10 = this.digestCalculatorProvider.get(var5);
               ERSArchiveTimeStamp var11 = new ERSArchiveTimeStamp(var7, var10);
               var11.validatePresent(new ERSByteData(var4.getTimeStamp().getEncoded("DER")), var9.getGenTime().getDate());
            } catch (Exception var12) {
               throw new ERSException("invalid timestamp renewal found: " + var12.getMessage(), var12);
            }

            var4 = var7;
         }
      }
   }

   ArchiveTimeStamp[] getArchiveTimeStamps() {
      ArchiveTimeStampSequence var1 = this.evidenceRecord.getArchiveTimeStampSequence();
      ArchiveTimeStampChain[] var2 = var1.getArchiveTimeStampChains();
      ArchiveTimeStampChain var3 = var2[var2.length - 1];
      return var3.getArchiveTimestamps();
   }

   public byte[] getPrimaryRootHash() throws TSPException, ERSException {
      ContentInfo var1 = this.primaryArchiveTimeStamp.getTimeStamp();
      if (var1.getContentType().equals(CMSObjectIdentifiers.signedData)) {
         TSTInfo var2 = this.extractTimeStamp(var1);
         return var2.getMessageImprint().getHashedMessage();
      } else {
         throw new ERSException("cannot identify TSTInfo for digest");
      }
   }

   private TSTInfo extractTimeStamp(ContentInfo var1) throws TSPException {
      SignedData var2 = SignedData.getInstance(var1.getContent());
      if (var2.getEncapContentInfo().getContentType().equals(PKCSObjectIdentifiers.id_ct_TSTInfo)) {
         return TSTInfo.getInstance(ASN1OctetString.getInstance(var2.getEncapContentInfo().getContent()).getOctets());
      } else {
         throw new TSPException("cannot parse time stamp");
      }
   }

   public boolean isRelatedTo(ERSEvidenceRecord var1) {
      return this.primaryArchiveTimeStamp.getTimeStamp().equals(var1.primaryArchiveTimeStamp.getTimeStamp());
   }

   public boolean isContaining(ERSData var1, Date var2) throws ERSException {
      return this.firstArchiveTimeStamp.isContaining(var1, var2);
   }

   public void validatePresent(ERSData var1, Date var2) throws ERSException {
      this.firstArchiveTimeStamp.validatePresent(var1, var2);
   }

   public void validatePresent(boolean var1, byte[] var2, Date var3) throws ERSException {
      this.firstArchiveTimeStamp.validatePresent(var1, var2, var3);
   }

   public X509CertificateHolder getSigningCertificate() {
      return this.lastArchiveTimeStamp.getSigningCertificate();
   }

   public void validate(SignerInformationVerifier var1) throws TSPException {
      if (this.firstArchiveTimeStamp != this.lastArchiveTimeStamp) {
         ArchiveTimeStamp[] var2 = this.getArchiveTimeStamps();

         for (int var3 = 0; var3 != var2.length - 1; var3++) {
            try {
               this.lastArchiveTimeStamp.validatePresent(new ERSByteData(var2[var3].getTimeStamp().getEncoded("DER")), this.lastArchiveTimeStamp.getGenTime());
            } catch (Exception var5) {
               throw new TSPException("unable to process previous ArchiveTimeStamps", var5);
            }
         }
      }

      this.lastArchiveTimeStamp.validate(var1);
   }

   public EvidenceRecord toASN1Structure() {
      return this.evidenceRecord;
   }

   public byte[] getEncoded() throws IOException {
      return this.evidenceRecord.getEncoded();
   }

   public TimeStampRequest generateTimeStampRenewalRequest(TimeStampRequestGenerator var1) throws TSPException, ERSException {
      return this.generateTimeStampRenewalRequest(var1, null);
   }

   public TimeStampRequest generateTimeStampRenewalRequest(TimeStampRequestGenerator var1, BigInteger var2) throws ERSException, TSPException {
      ERSArchiveTimeStampGenerator var3 = this.buildTspRenewalGenerator();

      try {
         return var3.generateTimeStampRequest(var1, var2);
      } catch (IOException var5) {
         throw new ERSException(var5.getMessage(), var5);
      }
   }

   public ERSEvidenceRecord renewTimeStamp(TimeStampResponse var1) throws ERSException, TSPException {
      ERSArchiveTimeStampGenerator var2 = this.buildTspRenewalGenerator();
      ArchiveTimeStamp var3 = var2.generateArchiveTimeStamp(var1).toASN1Structure();

      try {
         return new ERSEvidenceRecord(this.evidenceRecord.addArchiveTimeStamp(var3, false), this.digestCalculatorProvider);
      } catch (IllegalArgumentException var5) {
         throw new ERSException(var5.getMessage(), var5);
      }
   }

   private ERSArchiveTimeStampGenerator buildTspRenewalGenerator() throws ERSException {
      DigestCalculator var1;
      try {
         var1 = this.digestCalculatorProvider.get(this.lastArchiveTimeStamp.getDigestAlgorithmIdentifier());
      } catch (OperatorCreationException var8) {
         throw new ERSException(var8.getMessage(), var8);
      }

      ArchiveTimeStamp[] var2 = this.getArchiveTimeStamps();
      if (!var1.getAlgorithmIdentifier().equals(var2[0].getDigestAlgorithmIdentifier())) {
         throw new ERSException("digest mismatch for timestamp renewal");
      } else {
         ERSArchiveTimeStampGenerator var3 = new ERSArchiveTimeStampGenerator(var1);
         ArrayList var4 = new ArrayList(var2.length);

         for (int var5 = 0; var5 != var2.length; var5++) {
            try {
               var4.add(new ERSByteData(var2[var5].getTimeStamp().getEncoded("DER")));
            } catch (IOException var7) {
               throw new ERSException("unable to process previous ArchiveTimeStamps", var7);
            }
         }

         ERSDataGroup var9 = new ERSDataGroup(var4);
         var3.addData(var9);
         return var3;
      }
   }

   public TimeStampRequest generateHashRenewalRequest(DigestCalculator var1, ERSData var2, TimeStampRequestGenerator var3) throws ERSException, TSPException, IOException {
      return this.generateHashRenewalRequest(var1, var2, var3, null);
   }

   public TimeStampRequest generateHashRenewalRequest(DigestCalculator var1, ERSData var2, TimeStampRequestGenerator var3, BigInteger var4) throws ERSException, TSPException, IOException {
      try {
         this.firstArchiveTimeStamp.validatePresent(var2, new Date());
      } catch (Exception var6) {
         throw new ERSException("attempt to hash renew on invalid data");
      }

      ERSArchiveTimeStampGenerator var5 = new ERSArchiveTimeStampGenerator(var1);
      var5.addData(var2);
      var5.addPreviousChains(this.evidenceRecord.getArchiveTimeStampSequence());
      return var5.generateTimeStampRequest(var3, var4);
   }

   public ERSEvidenceRecord renewHash(DigestCalculator var1, ERSData var2, TimeStampResponse var3) throws ERSException, TSPException {
      try {
         this.firstArchiveTimeStamp.validatePresent(var2, new Date());
      } catch (Exception var8) {
         throw new ERSException("attempt to hash renew on invalid data");
      }

      try {
         ERSArchiveTimeStampGenerator var4 = new ERSArchiveTimeStampGenerator(var1);
         var4.addData(var2);
         var4.addPreviousChains(this.evidenceRecord.getArchiveTimeStampSequence());
         ArchiveTimeStamp var5 = var4.generateArchiveTimeStamp(var3).toASN1Structure();
         return new ERSEvidenceRecord(this.evidenceRecord.addArchiveTimeStamp(var5, true), this.digestCalculatorProvider);
      } catch (IOException var6) {
         throw new ERSException(var6.getMessage(), var6);
      } catch (IllegalArgumentException var7) {
         throw new ERSException(var7.getMessage(), var7);
      }
   }

   DigestCalculatorProvider getDigestAlgorithmProvider() {
      return this.digestCalculatorProvider;
   }
}
