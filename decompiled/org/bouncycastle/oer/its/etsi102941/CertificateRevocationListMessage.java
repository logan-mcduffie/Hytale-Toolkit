package org.bouncycastle.oer.its.etsi102941;

import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.oer.its.etsi103097.EtsiTs103097DataSigned;
import org.bouncycastle.oer.its.ieee1609dot2.Ieee1609Dot2Content;

public class CertificateRevocationListMessage extends EtsiTs103097DataSigned {
   public CertificateRevocationListMessage(Ieee1609Dot2Content var1) {
      super(var1);
   }

   protected CertificateRevocationListMessage(ASN1Sequence var1) {
      super(var1);
   }

   public static CertificateRevocationListMessage getInstance(Object var0) {
      if (var0 instanceof CertificateRevocationListMessage) {
         return (CertificateRevocationListMessage)var0;
      } else {
         return var0 != null ? new CertificateRevocationListMessage(ASN1Sequence.getInstance(var0)) : null;
      }
   }
}
