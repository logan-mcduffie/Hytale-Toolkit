package org.bouncycastle.tsp.ers;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import org.bouncycastle.asn1.tsp.ArchiveTimeStamp;
import org.bouncycastle.asn1.tsp.PartialHashtree;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cms.SignerInformationVerifier;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.tsp.TSPException;
import org.bouncycastle.tsp.TimeStampToken;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Store;

public class ERSArchiveTimeStamp {
   private final ArchiveTimeStamp archiveTimeStamp;
   private final DigestCalculator digCalc;
   private final TimeStampToken timeStampToken;
   private final byte[] previousChainsDigest;
   private ERSRootNodeCalculator rootNodeCalculator = new BinaryTreeRootCalculator();

   public ERSArchiveTimeStamp(byte[] var1, DigestCalculatorProvider var2) throws TSPException, ERSException {
      this(ArchiveTimeStamp.getInstance(var1), var2);
   }

   public ERSArchiveTimeStamp(ArchiveTimeStamp var1, DigestCalculatorProvider var2) throws TSPException, ERSException {
      this.previousChainsDigest = null;

      try {
         this.archiveTimeStamp = var1;
         this.timeStampToken = new TimeStampToken(var1.getTimeStamp());
         this.digCalc = var2.get(var1.getDigestAlgorithmIdentifier());
      } catch (IOException var4) {
         throw new ERSException(var4.getMessage(), var4);
      } catch (OperatorCreationException var5) {
         throw new ERSException(var5.getMessage(), var5);
      }
   }

   ERSArchiveTimeStamp(ArchiveTimeStamp var1, DigestCalculator var2) throws TSPException, ERSException {
      this.previousChainsDigest = null;

      try {
         this.archiveTimeStamp = var1;
         this.timeStampToken = new TimeStampToken(var1.getTimeStamp());
         this.digCalc = var2;
      } catch (IOException var4) {
         throw new ERSException(var4.getMessage(), var4);
      }
   }

   ERSArchiveTimeStamp(byte[] var1, ArchiveTimeStamp var2, DigestCalculatorProvider var3) throws TSPException, ERSException {
      this.previousChainsDigest = var1;

      try {
         this.archiveTimeStamp = var2;
         this.timeStampToken = new TimeStampToken(var2.getTimeStamp());
         this.digCalc = var3.get(var2.getDigestAlgorithmIdentifier());
      } catch (IOException var5) {
         throw new ERSException(var5.getMessage(), var5);
      } catch (OperatorCreationException var6) {
         throw new ERSException(var6.getMessage(), var6);
      }
   }

   public AlgorithmIdentifier getDigestAlgorithmIdentifier() {
      return this.archiveTimeStamp.getDigestAlgorithmIdentifier();
   }

   public void validatePresent(ERSData var1, Date var2) throws ERSException {
      this.validatePresent(var1 instanceof ERSDataGroup, var1.getHash(this.digCalc, this.previousChainsDigest), var2);
   }

   public boolean isContaining(ERSData var1, Date var2) throws ERSException {
      if (this.timeStampToken.getTimeStampInfo().getGenTime().after(var2)) {
         throw new ArchiveTimeStampValidationException("timestamp generation time is in the future");
      } else {
         try {
            this.validatePresent(var1, var2);
            return true;
         } catch (Exception var4) {
            return false;
         }
      }
   }

   public void validatePresent(boolean var1, byte[] var2, Date var3) throws ERSException {
      if (this.timeStampToken.getTimeStampInfo().getGenTime().after(var3)) {
         throw new ArchiveTimeStampValidationException("timestamp generation time is in the future");
      } else {
         this.checkContainsHashValue(var1, var2, this.digCalc);
         PartialHashtree[] var4 = this.archiveTimeStamp.getReducedHashTree();
         byte[] var5;
         if (var4 != null) {
            var5 = this.rootNodeCalculator.recoverRootHash(this.digCalc, this.archiveTimeStamp.getReducedHashTree());
         } else {
            var5 = var2;
         }

         this.checkTimeStampValid(this.timeStampToken, var5);
      }
   }

   public TimeStampToken getTimeStampToken() {
      return this.timeStampToken;
   }

   public X509CertificateHolder getSigningCertificate() {
      Store var1 = this.timeStampToken.getCertificates();
      if (var1 != null) {
         Collection var2 = var1.getMatches(this.timeStampToken.getSID());
         if (!var2.isEmpty()) {
            return (X509CertificateHolder)var2.iterator().next();
         }
      }

      return null;
   }

   public void validate(SignerInformationVerifier var1) throws TSPException {
      this.timeStampToken.validate(var1);
   }

   void checkContainsHashValue(boolean var1, byte[] var2, DigestCalculator var3) throws ArchiveTimeStampValidationException {
      PartialHashtree[] var4 = this.archiveTimeStamp.getReducedHashTree();
      if (var4 != null) {
         PartialHashtree var5 = var4[0];
         if (var1 || !var5.containsHash(var2)) {
            if (var5.getValueCount() <= 1 || !Arrays.areEqual(var2, ERSUtil.calculateBranchHash(var3, var5.getValues()))) {
               throw new ArchiveTimeStampValidationException("object hash not found");
            }
         }
      } else if (!Arrays.areEqual(var2, this.timeStampToken.getTimeStampInfo().getMessageImprintDigest())) {
         throw new ArchiveTimeStampValidationException("object hash not found in wrapped timestamp");
      }
   }

   void checkTimeStampValid(TimeStampToken var1, byte[] var2) throws ArchiveTimeStampValidationException {
      if (var2 != null && !Arrays.areEqual(var2, var1.getTimeStampInfo().getMessageImprintDigest())) {
         throw new ArchiveTimeStampValidationException("timestamp hash does not match root");
      }
   }

   public Date getGenTime() {
      return this.timeStampToken.getTimeStampInfo().getGenTime();
   }

   public Date getExpiryTime() {
      X509CertificateHolder var1 = this.getSigningCertificate();
      return var1 != null ? var1.getNotAfter() : null;
   }

   public ArchiveTimeStamp toASN1Structure() {
      return this.archiveTimeStamp;
   }

   public byte[] getEncoded() throws IOException {
      return this.archiveTimeStamp.getEncoded();
   }

   public static ERSArchiveTimeStamp fromTimeStampToken(TimeStampToken var0, DigestCalculatorProvider var1) throws TSPException, ERSException {
      return new ERSArchiveTimeStamp(new ArchiveTimeStamp(var0.toCMSSignedData().toASN1Structure()), var1);
   }
}
