package org.bouncycastle.tsp.cms;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import org.bouncycastle.asn1.ASN1IA5String;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.cms.Evidence;
import org.bouncycastle.asn1.cms.TimeStampAndCRL;
import org.bouncycastle.asn1.cms.TimeStampTokenEvidence;
import org.bouncycastle.asn1.cms.TimeStampedData;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.tsp.TimeStampToken;

public class CMSTimeStampedData {
   private TimeStampedData timeStampedData;
   private ContentInfo contentInfo;
   private TimeStampDataUtil util;

   public CMSTimeStampedData(ContentInfo var1) {
      this.initialize(var1);
   }

   public CMSTimeStampedData(InputStream var1) throws IOException {
      try {
         this.initialize(ContentInfo.getInstance(new ASN1InputStream(var1).readObject()));
      } catch (ClassCastException var3) {
         throw new IOException("Malformed content: " + var3);
      } catch (IllegalArgumentException var4) {
         throw new IOException("Malformed content: " + var4);
      }
   }

   public CMSTimeStampedData(byte[] var1) throws IOException {
      this(new ByteArrayInputStream(var1));
   }

   private void initialize(ContentInfo var1) {
      this.contentInfo = var1;
      if (CMSObjectIdentifiers.timestampedData.equals(var1.getContentType())) {
         this.timeStampedData = TimeStampedData.getInstance(var1.getContent());
         this.util = new TimeStampDataUtil(this.timeStampedData);
      } else {
         throw new IllegalArgumentException("Malformed content - type must be " + CMSObjectIdentifiers.timestampedData.getId());
      }
   }

   public byte[] calculateNextHash(DigestCalculator var1) throws CMSException {
      return this.util.calculateNextHash(var1);
   }

   public CMSTimeStampedData addTimeStamp(TimeStampToken var1) throws CMSException {
      TimeStampAndCRL[] var2 = this.util.getTimeStamps();
      TimeStampAndCRL[] var3 = new TimeStampAndCRL[var2.length + 1];
      System.arraycopy(var2, 0, var3, 0, var2.length);
      var3[var2.length] = new TimeStampAndCRL(var1.toCMSSignedData().toASN1Structure());
      return new CMSTimeStampedData(
         new ContentInfo(
            CMSObjectIdentifiers.timestampedData,
            new TimeStampedData(
               this.timeStampedData.getDataUriIA5(),
               this.timeStampedData.getMetaData(),
               this.timeStampedData.getContent(),
               new Evidence(new TimeStampTokenEvidence(var3))
            )
         )
      );
   }

   public byte[] getContent() {
      return this.timeStampedData.getContent() != null ? this.timeStampedData.getContent().getOctets() : null;
   }

   public URI getDataUri() throws URISyntaxException {
      ASN1IA5String var1 = this.timeStampedData.getDataUriIA5();
      return var1 != null ? new URI(var1.getString()) : null;
   }

   public String getFileName() {
      return this.util.getFileName();
   }

   public String getMediaType() {
      return this.util.getMediaType();
   }

   public AttributeTable getOtherMetaData() {
      return this.util.getOtherMetaData();
   }

   public TimeStampToken[] getTimeStampTokens() throws CMSException {
      return this.util.getTimeStampTokens();
   }

   public void initialiseMessageImprintDigestCalculator(DigestCalculator var1) throws CMSException {
      this.util.initialiseMessageImprintDigestCalculator(var1);
   }

   public DigestCalculator getMessageImprintDigestCalculator(DigestCalculatorProvider var1) throws OperatorCreationException {
      return this.util.getMessageImprintDigestCalculator(var1);
   }

   public void validate(DigestCalculatorProvider var1, byte[] var2) throws ImprintDigestInvalidException, CMSException {
      this.util.validate(var1, var2);
   }

   public void validate(DigestCalculatorProvider var1, byte[] var2, TimeStampToken var3) throws ImprintDigestInvalidException, CMSException {
      this.util.validate(var1, var2, var3);
   }

   public byte[] getEncoded() throws IOException {
      return this.contentInfo.getEncoded();
   }
}
