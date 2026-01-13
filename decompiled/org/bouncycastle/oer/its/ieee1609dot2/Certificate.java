package org.bouncycastle.oer.its.ieee1609dot2;

import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Signature;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.UINT8;

public class Certificate extends CertificateBase {
   public Certificate(UINT8 var1, CertificateType var2, IssuerIdentifier var3, ToBeSignedCertificate var4, Signature var5) {
      super(var1, var2, var3, var4, var5);
   }

   public Certificate(CertificateBase var1) {
      this(var1.getVersion(), var1.getType(), var1.getIssuer(), var1.getToBeSigned(), var1.getSignature());
   }

   protected Certificate(ASN1Sequence var1) {
      super(var1);
   }

   public static Certificate getInstance(Object var0) {
      if (var0 instanceof Certificate) {
         return (Certificate)var0;
      } else {
         return var0 != null ? new Certificate(ASN1Sequence.getInstance(var0)) : null;
      }
   }
}
