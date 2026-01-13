package org.bouncycastle.tsp.cms;

import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.cms.Evidence;
import org.bouncycastle.asn1.cms.TimeStampAndCRL;
import org.bouncycastle.asn1.cms.TimeStampedData;
import org.bouncycastle.asn1.cms.TimeStampedDataParser;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.tsp.TSPException;
import org.bouncycastle.tsp.TimeStampToken;
import org.bouncycastle.tsp.TimeStampTokenInfo;
import org.bouncycastle.util.Arrays;

class TimeStampDataUtil {
   private final TimeStampAndCRL[] timeStamps;
   private final MetaDataUtil metaDataUtil;

   TimeStampDataUtil(TimeStampedData var1) {
      this.metaDataUtil = new MetaDataUtil(var1.getMetaData());
      Evidence var2 = var1.getTemporalEvidence();
      this.timeStamps = var2.getTstEvidence().toTimeStampAndCRLArray();
   }

   TimeStampDataUtil(TimeStampedDataParser var1) throws IOException {
      this.metaDataUtil = new MetaDataUtil(var1.getMetaData());
      Evidence var2 = var1.getTemporalEvidence();
      this.timeStamps = var2.getTstEvidence().toTimeStampAndCRLArray();
   }

   TimeStampToken getTimeStampToken(TimeStampAndCRL var1) throws CMSException {
      ContentInfo var2 = var1.getTimeStampToken();

      try {
         return new TimeStampToken(var2);
      } catch (IOException var4) {
         throw new CMSException("unable to parse token data: " + var4.getMessage(), var4);
      } catch (TSPException var5) {
         if (var5.getCause() instanceof CMSException) {
            throw (CMSException)var5.getCause();
         } else {
            throw new CMSException("token data invalid: " + var5.getMessage(), var5);
         }
      } catch (IllegalArgumentException var6) {
         throw new CMSException("token data invalid: " + var6.getMessage(), var6);
      }
   }

   void initialiseMessageImprintDigestCalculator(DigestCalculator var1) throws CMSException {
      this.metaDataUtil.initialiseMessageImprintDigestCalculator(var1);
   }

   DigestCalculator getMessageImprintDigestCalculator(DigestCalculatorProvider var1) throws OperatorCreationException {
      try {
         TimeStampToken var2 = this.getTimeStampToken(this.timeStamps[0]);
         TimeStampTokenInfo var3 = var2.getTimeStampInfo();
         ASN1ObjectIdentifier var4 = var3.getMessageImprintAlgOID();
         DigestCalculator var5 = var1.get(new AlgorithmIdentifier(var4));
         this.initialiseMessageImprintDigestCalculator(var5);
         return var5;
      } catch (CMSException var6) {
         throw new OperatorCreationException("unable to extract algorithm ID: " + var6.getMessage(), var6);
      }
   }

   TimeStampToken[] getTimeStampTokens() throws CMSException {
      TimeStampToken[] var1 = new TimeStampToken[this.timeStamps.length];

      for (int var2 = 0; var2 < this.timeStamps.length; var2++) {
         var1[var2] = this.getTimeStampToken(this.timeStamps[var2]);
      }

      return var1;
   }

   TimeStampAndCRL[] getTimeStamps() {
      return this.timeStamps;
   }

   byte[] calculateNextHash(DigestCalculator var1) throws CMSException {
      TimeStampAndCRL var2 = this.timeStamps[this.timeStamps.length - 1];
      OutputStream var3 = var1.getOutputStream();

      try {
         var3.write(var2.getEncoded("DER"));
         var3.close();
         return var1.getDigest();
      } catch (IOException var5) {
         throw new CMSException("exception calculating hash: " + var5.getMessage(), var5);
      }
   }

   void validate(DigestCalculatorProvider var1, byte[] var2) throws ImprintDigestInvalidException, CMSException {
      byte[] var3 = var2;

      for (int var4 = 0; var4 < this.timeStamps.length; var4++) {
         try {
            TimeStampToken var5 = this.getTimeStampToken(this.timeStamps[var4]);
            if (var4 > 0) {
               TimeStampTokenInfo var6 = var5.getTimeStampInfo();
               DigestCalculator var7 = var1.get(var6.getHashAlgorithm());
               var7.getOutputStream().write(this.timeStamps[var4 - 1].getEncoded("DER"));
               var3 = var7.getDigest();
            }

            this.compareDigest(var5, var3);
         } catch (IOException var8) {
            throw new CMSException("exception calculating hash: " + var8.getMessage(), var8);
         } catch (OperatorCreationException var9) {
            throw new CMSException("cannot create digest: " + var9.getMessage(), var9);
         }
      }
   }

   void validate(DigestCalculatorProvider var1, byte[] var2, TimeStampToken var3) throws ImprintDigestInvalidException, CMSException {
      byte[] var4 = var2;

      byte[] var5;
      try {
         var5 = var3.getEncoded();
      } catch (IOException var10) {
         throw new CMSException("exception encoding timeStampToken: " + var10.getMessage(), var10);
      }

      for (int var6 = 0; var6 < this.timeStamps.length; var6++) {
         try {
            TimeStampToken var7 = this.getTimeStampToken(this.timeStamps[var6]);
            if (var6 > 0) {
               TimeStampTokenInfo var8 = var7.getTimeStampInfo();
               DigestCalculator var9 = var1.get(var8.getHashAlgorithm());
               var9.getOutputStream().write(this.timeStamps[var6 - 1].getEncoded("DER"));
               var4 = var9.getDigest();
            }

            this.compareDigest(var7, var4);
            if (Arrays.areEqual(var7.getEncoded(), var5)) {
               return;
            }
         } catch (IOException var11) {
            throw new CMSException("exception calculating hash: " + var11.getMessage(), var11);
         } catch (OperatorCreationException var12) {
            throw new CMSException("cannot create digest: " + var12.getMessage(), var12);
         }
      }

      throw new ImprintDigestInvalidException("passed in token not associated with timestamps present", var3);
   }

   private void compareDigest(TimeStampToken var1, byte[] var2) throws ImprintDigestInvalidException {
      TimeStampTokenInfo var3 = var1.getTimeStampInfo();
      byte[] var4 = var3.getMessageImprintDigest();
      if (!Arrays.areEqual(var2, var4)) {
         throw new ImprintDigestInvalidException("hash calculated is different from MessageImprintDigest found in TimeStampToken", var1);
      }
   }

   String getFileName() {
      return this.metaDataUtil.getFileName();
   }

   String getMediaType() {
      return this.metaDataUtil.getMediaType();
   }

   AttributeTable getOtherMetaData() {
      return new AttributeTable(this.metaDataUtil.getOtherMetaData());
   }
}
