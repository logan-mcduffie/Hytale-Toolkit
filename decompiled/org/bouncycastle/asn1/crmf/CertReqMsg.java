package org.bouncycastle.asn1.crmf;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;

public class CertReqMsg extends ASN1Object {
   private CertRequest certReq;
   private ProofOfPossession pop;
   private ASN1Sequence regInfo;

   private CertReqMsg(ASN1Sequence var1) {
      Enumeration var2 = var1.getObjects();
      this.certReq = CertRequest.getInstance(var2.nextElement());

      while (var2.hasMoreElements()) {
         Object var3 = var2.nextElement();
         if (!(var3 instanceof ASN1TaggedObject) && !(var3 instanceof ProofOfPossession)) {
            this.regInfo = ASN1Sequence.getInstance(var3);
         } else {
            this.pop = ProofOfPossession.getInstance(var3);
         }
      }
   }

   public static CertReqMsg getInstance(Object var0) {
      if (var0 instanceof CertReqMsg) {
         return (CertReqMsg)var0;
      } else {
         return var0 != null ? new CertReqMsg(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public static CertReqMsg getInstance(ASN1TaggedObject var0, boolean var1) {
      return getInstance(ASN1Sequence.getInstance(var0, var1));
   }

   public CertReqMsg(CertRequest var1, ProofOfPossession var2, AttributeTypeAndValue[] var3) {
      if (var1 == null) {
         throw new IllegalArgumentException("'certReq' cannot be null");
      } else {
         this.certReq = var1;
         this.pop = var2;
         if (var3 != null) {
            this.regInfo = new DERSequence(var3);
         }
      }
   }

   public CertRequest getCertReq() {
      return this.certReq;
   }

   public ProofOfPossession getPop() {
      return this.pop;
   }

   /** @deprecated */
   public ProofOfPossession getPopo() {
      return this.pop;
   }

   public AttributeTypeAndValue[] getRegInfo() {
      if (this.regInfo == null) {
         return null;
      } else {
         AttributeTypeAndValue[] var1 = new AttributeTypeAndValue[this.regInfo.size()];

         for (int var2 = 0; var2 != var1.length; var2++) {
            var1[var2] = AttributeTypeAndValue.getInstance(this.regInfo.getObjectAt(var2));
         }

         return var1;
      }
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(3);
      var1.add(this.certReq);
      this.addOptional(var1, this.pop);
      this.addOptional(var1, this.regInfo);
      return new DERSequence(var1);
   }

   private void addOptional(ASN1EncodableVector var1, ASN1Encodable var2) {
      if (var2 != null) {
         var1.add(var2);
      }
   }
}
