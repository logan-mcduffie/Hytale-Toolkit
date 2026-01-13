package org.bouncycastle.tsp.cms;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import org.bouncycastle.asn1.ASN1IA5String;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.asn1.cms.ContentInfoParser;
import org.bouncycastle.asn1.cms.TimeStampedDataParser;
import org.bouncycastle.cms.CMSContentInfoParser;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.tsp.TimeStampToken;
import org.bouncycastle.util.io.Streams;

public class CMSTimeStampedDataParser extends CMSContentInfoParser {
   private TimeStampedDataParser timeStampedData;
   private TimeStampDataUtil util;

   public CMSTimeStampedDataParser(InputStream var1) throws CMSException {
      super(var1);
      this.initialize(this._contentInfo);
   }

   public CMSTimeStampedDataParser(byte[] var1) throws CMSException {
      this(new ByteArrayInputStream(var1));
   }

   private void initialize(ContentInfoParser var1) throws CMSException {
      try {
         if (CMSObjectIdentifiers.timestampedData.equals(var1.getContentType())) {
            this.timeStampedData = TimeStampedDataParser.getInstance(var1.getContent(16));
         } else {
            throw new IllegalArgumentException("Malformed content - type must be " + CMSObjectIdentifiers.timestampedData.getId());
         }
      } catch (IOException var3) {
         throw new CMSException("parsing exception: " + var3.getMessage(), var3);
      }
   }

   public byte[] calculateNextHash(DigestCalculator var1) throws CMSException {
      return this.util.calculateNextHash(var1);
   }

   public InputStream getContent() {
      return this.timeStampedData.getContent() != null ? this.timeStampedData.getContent().getOctetStream() : null;
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

   public void initialiseMessageImprintDigestCalculator(DigestCalculator var1) throws CMSException {
      this.util.initialiseMessageImprintDigestCalculator(var1);
   }

   public DigestCalculator getMessageImprintDigestCalculator(DigestCalculatorProvider var1) throws OperatorCreationException {
      try {
         this.parseTimeStamps();
      } catch (CMSException var3) {
         throw new OperatorCreationException("unable to extract algorithm ID: " + var3.getMessage(), var3);
      }

      return this.util.getMessageImprintDigestCalculator(var1);
   }

   public TimeStampToken[] getTimeStampTokens() throws CMSException {
      this.parseTimeStamps();
      return this.util.getTimeStampTokens();
   }

   public void validate(DigestCalculatorProvider var1, byte[] var2) throws ImprintDigestInvalidException, CMSException {
      this.parseTimeStamps();
      this.util.validate(var1, var2);
   }

   public void validate(DigestCalculatorProvider var1, byte[] var2, TimeStampToken var3) throws ImprintDigestInvalidException, CMSException {
      this.parseTimeStamps();
      this.util.validate(var1, var2, var3);
   }

   private void parseTimeStamps() throws CMSException {
      try {
         if (this.util == null) {
            InputStream var1 = this.getContent();
            if (var1 != null) {
               Streams.drain(var1);
            }

            this.util = new TimeStampDataUtil(this.timeStampedData);
         }
      } catch (IOException var2) {
         throw new CMSException("unable to parse evidence block: " + var2.getMessage(), var2);
      }
   }
}
