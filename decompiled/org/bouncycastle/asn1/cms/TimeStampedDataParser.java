package org.bouncycastle.asn1.cms;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1IA5String;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1OctetStringParser;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1SequenceParser;
import org.bouncycastle.asn1.DERIA5String;

public class TimeStampedDataParser {
   private ASN1Integer version;
   private ASN1IA5String dataUri;
   private MetaData metaData;
   private ASN1OctetStringParser content;
   private Evidence temporalEvidence;
   private ASN1SequenceParser parser;

   private TimeStampedDataParser(ASN1SequenceParser var1) throws IOException {
      this.parser = var1;
      this.version = ASN1Integer.getInstance(var1.readObject());
      ASN1Encodable var2 = var1.readObject();
      if (var2 instanceof ASN1IA5String) {
         this.dataUri = ASN1IA5String.getInstance(var2);
         var2 = var1.readObject();
      }

      if (var2 instanceof MetaData || var2 instanceof ASN1SequenceParser) {
         this.metaData = MetaData.getInstance(var2.toASN1Primitive());
         var2 = var1.readObject();
      }

      if (var2 instanceof ASN1OctetStringParser) {
         this.content = (ASN1OctetStringParser)var2;
      }
   }

   public static TimeStampedDataParser getInstance(Object var0) throws IOException {
      if (var0 instanceof ASN1Sequence) {
         return new TimeStampedDataParser(((ASN1Sequence)var0).parser());
      } else {
         return var0 instanceof ASN1SequenceParser ? new TimeStampedDataParser((ASN1SequenceParser)var0) : null;
      }
   }

   public int getVersion() {
      return this.version.getValue().intValue();
   }

   /** @deprecated */
   public DERIA5String getDataUri() {
      return null != this.dataUri && !(this.dataUri instanceof DERIA5String) ? new DERIA5String(this.dataUri.getString(), false) : (DERIA5String)this.dataUri;
   }

   public ASN1IA5String getDataUriIA5() {
      return this.dataUri;
   }

   public MetaData getMetaData() {
      return this.metaData;
   }

   public ASN1OctetStringParser getContent() {
      return this.content;
   }

   public Evidence getTemporalEvidence() throws IOException {
      if (this.temporalEvidence == null) {
         this.temporalEvidence = Evidence.getInstance(this.parser.readObject().toASN1Primitive());
      }

      return this.temporalEvidence;
   }
}
