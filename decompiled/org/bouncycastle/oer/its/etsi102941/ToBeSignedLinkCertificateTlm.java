package org.bouncycastle.oer.its.etsi102941;

import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.oer.its.ieee1609dot2.HashedData;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Time32;

public class ToBeSignedLinkCertificateTlm extends ToBeSignedLinkCertificate {
   public ToBeSignedLinkCertificateTlm(Time32 var1, HashedData var2) {
      super(var1, var2);
   }

   protected ToBeSignedLinkCertificateTlm(ASN1Sequence var1) {
      super(var1);
   }

   private ToBeSignedLinkCertificateTlm(ToBeSignedLinkCertificate var1) {
      super(var1.getExpiryTime(), var1.getCertificateHash());
   }

   public static ToBeSignedLinkCertificateTlm getInstance(Object var0) {
      if (var0 instanceof ToBeSignedLinkCertificateTlm) {
         return (ToBeSignedLinkCertificateTlm)var0;
      } else if (var0 instanceof ToBeSignedLinkCertificate) {
         return new ToBeSignedLinkCertificateTlm((ToBeSignedLinkCertificate)var0);
      } else {
         return var0 != null ? new ToBeSignedLinkCertificateTlm(ASN1Sequence.getInstance(var0)) : null;
      }
   }
}
