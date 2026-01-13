package org.bouncycastle.oer.its.etsi102941;

import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.oer.its.ieee1609dot2.HashedData;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Time32;

public class ToBeSignedLinkCertificateRca extends ToBeSignedLinkCertificate {
   public ToBeSignedLinkCertificateRca(Time32 var1, HashedData var2) {
      super(var1, var2);
   }

   protected ToBeSignedLinkCertificateRca(ASN1Sequence var1) {
      super(var1);
   }

   private ToBeSignedLinkCertificateRca(ToBeSignedLinkCertificate var1) {
      super(var1.getExpiryTime(), var1.getCertificateHash());
   }

   public static ToBeSignedLinkCertificateRca getInstance(Object var0) {
      if (var0 instanceof ToBeSignedLinkCertificateRca) {
         return (ToBeSignedLinkCertificateRca)var0;
      } else if (var0 instanceof ToBeSignedLinkCertificate) {
         return new ToBeSignedLinkCertificateRca((ToBeSignedLinkCertificate)var0);
      } else {
         return var0 != null ? new ToBeSignedLinkCertificateRca(ASN1Sequence.getInstance(var0)) : null;
      }
   }
}
