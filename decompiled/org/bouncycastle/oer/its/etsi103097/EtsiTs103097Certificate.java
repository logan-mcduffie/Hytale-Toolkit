package org.bouncycastle.oer.its.etsi103097;

import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.oer.its.ieee1609dot2.ExplicitCertificate;
import org.bouncycastle.oer.its.ieee1609dot2.IssuerIdentifier;
import org.bouncycastle.oer.its.ieee1609dot2.ToBeSignedCertificate;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Signature;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.UINT8;

public class EtsiTs103097Certificate extends ExplicitCertificate {
   public EtsiTs103097Certificate(UINT8 var1, IssuerIdentifier var2, ToBeSignedCertificate var3, Signature var4) {
      super(var1, var2, var3, var4);
   }

   protected EtsiTs103097Certificate(ASN1Sequence var1) {
      super(var1);
   }

   public static EtsiTs103097Certificate getInstance(Object var0) {
      if (var0 instanceof EtsiTs103097Certificate) {
         return (EtsiTs103097Certificate)var0;
      } else {
         return var0 != null ? new EtsiTs103097Certificate(ASN1Sequence.getInstance(var0)) : null;
      }
   }
}
