package org.bouncycastle.asn1.cmc;

import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1UTF8String;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERUTF8String;

public class CMCStatusInfoV2Builder {
   private final CMCStatus cMCStatus;
   private final ASN1Sequence bodyList;
   private ASN1UTF8String statusString;
   private OtherStatusInfo otherInfo;

   public CMCStatusInfoV2Builder(CMCStatus var1, BodyPartID var2) {
      this.cMCStatus = var1;
      this.bodyList = new DERSequence(var2);
   }

   public CMCStatusInfoV2Builder(CMCStatus var1, BodyPartID[] var2) {
      this.cMCStatus = var1;
      this.bodyList = new DERSequence(var2);
   }

   public CMCStatusInfoV2Builder setStatusString(String var1) {
      this.statusString = new DERUTF8String(var1);
      return this;
   }

   public CMCStatusInfoV2Builder setOtherInfo(CMCFailInfo var1) {
      this.otherInfo = new OtherStatusInfo(var1);
      return this;
   }

   public CMCStatusInfoV2Builder setOtherInfo(ExtendedFailInfo var1) {
      this.otherInfo = new OtherStatusInfo(var1);
      return this;
   }

   public CMCStatusInfoV2Builder setOtherInfo(PendInfo var1) {
      this.otherInfo = new OtherStatusInfo(var1);
      return this;
   }

   public CMCStatusInfoV2 build() {
      return new CMCStatusInfoV2(this.cMCStatus, this.bodyList, this.statusString, this.otherInfo);
   }
}
