package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1IA5String;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.BERSequence;
import org.bouncycastle.asn1.DERIA5String;

public class TimeStampedData extends ASN1Object {
   private ASN1Integer version;
   private ASN1IA5String dataUri;
   private MetaData metaData;
   private ASN1OctetString content;
   private Evidence temporalEvidence;

   public TimeStampedData(ASN1IA5String var1, MetaData var2, ASN1OctetString var3, Evidence var4) {
      this.version = new ASN1Integer(1L);
      this.dataUri = var1;
      this.metaData = var2;
      this.content = var3;
      this.temporalEvidence = var4;
   }

   private TimeStampedData(ASN1Sequence var1) {
      this.version = ASN1Integer.getInstance(var1.getObjectAt(0));
      int var2 = 1;
      if (var1.getObjectAt(var2) instanceof ASN1IA5String) {
         this.dataUri = ASN1IA5String.getInstance(var1.getObjectAt(var2++));
      }

      if (var1.getObjectAt(var2) instanceof MetaData || var1.getObjectAt(var2) instanceof ASN1Sequence) {
         this.metaData = MetaData.getInstance(var1.getObjectAt(var2++));
      }

      if (var1.getObjectAt(var2) instanceof ASN1OctetString) {
         this.content = ASN1OctetString.getInstance(var1.getObjectAt(var2++));
      }

      this.temporalEvidence = Evidence.getInstance(var1.getObjectAt(var2));
   }

   public static TimeStampedData getInstance(Object var0) {
      return var0 != null && !(var0 instanceof TimeStampedData) ? new TimeStampedData(ASN1Sequence.getInstance(var0)) : (TimeStampedData)var0;
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

   public ASN1OctetString getContent() {
      return this.content;
   }

   public Evidence getTemporalEvidence() {
      return this.temporalEvidence;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(5);
      var1.add(this.version);
      if (this.dataUri != null) {
         var1.add(this.dataUri);
      }

      if (this.metaData != null) {
         var1.add(this.metaData);
      }

      if (this.content != null) {
         var1.add(this.content);
      }

      var1.add(this.temporalEvidence);
      return new BERSequence(var1);
   }
}
