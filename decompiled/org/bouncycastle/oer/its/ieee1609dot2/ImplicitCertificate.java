package org.bouncycastle.oer.its.ieee1609dot2;

import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Signature;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.UINT8;

public class ImplicitCertificate extends CertificateBase {
   public ImplicitCertificate(CertificateBase var1) {
      this(var1.getVersion(), var1.getIssuer(), var1.getToBeSigned(), var1.getSignature());
   }

   public ImplicitCertificate(UINT8 var1, IssuerIdentifier var2, ToBeSignedCertificate var3, Signature var4) {
      super(var1, CertificateType.implicit, var2, var3, var4);
   }

   private ImplicitCertificate(ASN1Sequence var1) {
      super(var1);
      if (!this.getType().equals(CertificateType.implicit)) {
         throw new IllegalArgumentException("object was certificate base but the type was not implicit");
      }
   }

   public static ImplicitCertificate getInstance(Object var0) {
      if (var0 instanceof ImplicitCertificate) {
         return (ImplicitCertificate)var0;
      } else {
         return var0 != null ? new ImplicitCertificate(ASN1Sequence.getInstance(var0)) : null;
      }
   }
}
