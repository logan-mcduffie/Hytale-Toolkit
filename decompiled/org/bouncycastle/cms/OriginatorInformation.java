package org.bouncycastle.cms;

import org.bouncycastle.asn1.cms.OriginatorInfo;
import org.bouncycastle.util.Store;

public class OriginatorInformation {
   private OriginatorInfo originatorInfo;

   OriginatorInformation(OriginatorInfo var1) {
      this.originatorInfo = var1;
   }

   public Store getCertificates() {
      return CMSSignedHelper.INSTANCE.getCertificates(this.originatorInfo.getCertificates());
   }

   public Store getCRLs() {
      return CMSSignedHelper.INSTANCE.getCRLs(this.originatorInfo.getCRLs());
   }

   public OriginatorInfo toASN1Structure() {
      return this.originatorInfo;
   }
}
