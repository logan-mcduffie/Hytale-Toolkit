package org.bouncycastle.tsp.cms;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.asn1.BEROctetString;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.cms.Evidence;
import org.bouncycastle.asn1.cms.TimeStampAndCRL;
import org.bouncycastle.asn1.cms.TimeStampTokenEvidence;
import org.bouncycastle.asn1.cms.TimeStampedData;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.tsp.TimeStampToken;
import org.bouncycastle.util.io.Streams;

public class CMSTimeStampedDataGenerator extends CMSTimeStampedGenerator {
   public CMSTimeStampedData generate(TimeStampToken var1) throws CMSException {
      return this.generate(var1, (InputStream)null);
   }

   public CMSTimeStampedData generate(TimeStampToken var1, byte[] var2) throws CMSException {
      return this.generate(var1, new ByteArrayInputStream(var2));
   }

   public CMSTimeStampedData generate(TimeStampToken var1, InputStream var2) throws CMSException {
      BEROctetString var3 = null;
      if (var2 != null) {
         ByteArrayOutputStream var4 = new ByteArrayOutputStream();

         try {
            Streams.pipeAll(var2, var4);
         } catch (IOException var7) {
            throw new CMSException("exception encapsulating content: " + var7.getMessage(), var7);
         }

         if (var4.size() != 0) {
            var3 = new BEROctetString(var4.toByteArray());
         }
      }

      TimeStampAndCRL var8 = new TimeStampAndCRL(var1.toCMSSignedData().toASN1Structure());
      DERIA5String var5 = null;
      if (this.dataUri != null) {
         var5 = new DERIA5String(this.dataUri.toString());
      }

      TimeStampedData var6 = new TimeStampedData(var5, this.metaData, var3, new Evidence(new TimeStampTokenEvidence(var8)));
      return new CMSTimeStampedData(new ContentInfo(CMSObjectIdentifiers.timestampedData, var6));
   }
}
