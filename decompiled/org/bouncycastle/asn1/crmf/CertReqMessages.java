package org.bouncycastle.asn1.crmf;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class CertReqMessages extends ASN1Object {
   private ASN1Sequence content;

   private CertReqMessages(ASN1Sequence var1) {
      this.content = var1;
   }

   public static CertReqMessages getInstance(Object var0) {
      if (var0 instanceof CertReqMessages) {
         return (CertReqMessages)var0;
      } else {
         return var0 != null ? new CertReqMessages(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public CertReqMessages(CertReqMsg var1) {
      this.content = new DERSequence(var1);
   }

   public CertReqMessages(CertReqMsg[] var1) {
      this.content = new DERSequence(var1);
   }

   public CertReqMsg[] toCertReqMsgArray() {
      CertReqMsg[] var1 = new CertReqMsg[this.content.size()];

      for (int var2 = 0; var2 != var1.length; var2++) {
         var1[var2] = CertReqMsg.getInstance(this.content.getObjectAt(var2));
      }

      return var1;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return this.content;
   }
}
