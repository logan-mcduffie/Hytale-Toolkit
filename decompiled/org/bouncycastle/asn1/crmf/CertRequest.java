package org.bouncycastle.asn1.crmf;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class CertRequest extends ASN1Object {
   private ASN1Integer certReqId;
   private CertTemplate certTemplate;
   private Controls controls;

   private CertRequest(ASN1Sequence var1) {
      this.certReqId = new ASN1Integer(ASN1Integer.getInstance(var1.getObjectAt(0)).getValue());
      this.certTemplate = CertTemplate.getInstance(var1.getObjectAt(1));
      if (var1.size() > 2) {
         this.controls = Controls.getInstance(var1.getObjectAt(2));
      }
   }

   public static CertRequest getInstance(Object var0) {
      if (var0 instanceof CertRequest) {
         return (CertRequest)var0;
      } else {
         return var0 != null ? new CertRequest(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public CertRequest(int var1, CertTemplate var2, Controls var3) {
      this(new ASN1Integer(var1), var2, var3);
   }

   public CertRequest(ASN1Integer var1, CertTemplate var2, Controls var3) {
      this.certReqId = var1;
      this.certTemplate = var2;
      this.controls = var3;
   }

   public ASN1Integer getCertReqId() {
      return this.certReqId;
   }

   public CertTemplate getCertTemplate() {
      return this.certTemplate;
   }

   public Controls getControls() {
      return this.controls;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(3);
      var1.add(this.certReqId);
      var1.add(this.certTemplate);
      if (this.controls != null) {
         var1.add(this.controls);
      }

      return new DERSequence(var1);
   }
}
