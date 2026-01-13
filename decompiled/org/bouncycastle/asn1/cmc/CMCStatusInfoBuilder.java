package org.bouncycastle.asn1.cmc;

import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1UTF8String;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERUTF8String;

public class CMCStatusInfoBuilder {
   private final CMCStatus cMCStatus;
   private final ASN1Sequence bodyList;
   private ASN1UTF8String statusString;
   private CMCStatusInfo.OtherInfo otherInfo;

   public CMCStatusInfoBuilder(CMCStatus var1, BodyPartID var2) {
      this.cMCStatus = var1;
      this.bodyList = new DERSequence(var2);
   }

   public CMCStatusInfoBuilder(CMCStatus var1, BodyPartID[] var2) {
      this.cMCStatus = var1;
      this.bodyList = new DERSequence(var2);
   }

   public CMCStatusInfoBuilder setStatusString(String var1) {
      this.statusString = new DERUTF8String(var1);
      return this;
   }

   public CMCStatusInfoBuilder setOtherInfo(CMCFailInfo var1) {
      this.otherInfo = new CMCStatusInfo.OtherInfo(var1);
      return this;
   }

   public CMCStatusInfoBuilder setOtherInfo(PendInfo var1) {
      this.otherInfo = new CMCStatusInfo.OtherInfo(var1);
      return this;
   }

   public CMCStatusInfo build() {
      return new CMCStatusInfo(this.cMCStatus, this.bodyList, this.statusString, this.otherInfo);
   }
}
