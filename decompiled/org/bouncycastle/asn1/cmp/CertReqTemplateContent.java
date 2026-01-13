package org.bouncycastle.asn1.cmp;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.crmf.CertTemplate;

public class CertReqTemplateContent extends ASN1Object {
   private final CertTemplate certTemplate;
   private final ASN1Sequence keySpec;

   private CertReqTemplateContent(ASN1Sequence var1) {
      if (var1.size() != 1 && var1.size() != 2) {
         throw new IllegalArgumentException("expected sequence size of 1 or 2");
      } else {
         this.certTemplate = CertTemplate.getInstance(var1.getObjectAt(0));
         if (var1.size() > 1) {
            this.keySpec = ASN1Sequence.getInstance(var1.getObjectAt(1));
         } else {
            this.keySpec = null;
         }
      }
   }

   public CertReqTemplateContent(CertTemplate var1, ASN1Sequence var2) {
      this.certTemplate = var1;
      this.keySpec = var2;
   }

   public static CertReqTemplateContent getInstance(Object var0) {
      if (var0 instanceof CertReqTemplateContent) {
         return (CertReqTemplateContent)var0;
      } else {
         return var0 != null ? new CertReqTemplateContent(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public CertTemplate getCertTemplate() {
      return this.certTemplate;
   }

   public ASN1Sequence getKeySpec() {
      return this.keySpec;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(2);
      var1.add(this.certTemplate);
      if (this.keySpec != null) {
         var1.add(this.keySpec);
      }

      return new DERSequence(var1);
   }
}
