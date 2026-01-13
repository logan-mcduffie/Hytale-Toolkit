package org.bouncycastle.asn1.ocsp;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;

public class OCSPRequest extends ASN1Object {
   TBSRequest tbsRequest;
   Signature optionalSignature;

   public OCSPRequest(TBSRequest var1, Signature var2) {
      this.tbsRequest = var1;
      this.optionalSignature = var2;
   }

   private OCSPRequest(ASN1Sequence var1) {
      this.tbsRequest = TBSRequest.getInstance(var1.getObjectAt(0));
      if (var1.size() == 2) {
         this.optionalSignature = Signature.getInstance((ASN1TaggedObject)var1.getObjectAt(1), true);
      }
   }

   public static OCSPRequest getInstance(ASN1TaggedObject var0, boolean var1) {
      return getInstance(ASN1Sequence.getInstance(var0, var1));
   }

   public static OCSPRequest getInstance(Object var0) {
      if (var0 instanceof OCSPRequest) {
         return (OCSPRequest)var0;
      } else {
         return var0 != null ? new OCSPRequest(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public TBSRequest getTbsRequest() {
      return this.tbsRequest;
   }

   public Signature getOptionalSignature() {
      return this.optionalSignature;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(2);
      var1.add(this.tbsRequest);
      if (this.optionalSignature != null) {
         var1.add(new DERTaggedObject(true, 0, this.optionalSignature));
      }

      return new DERSequence(var1);
   }
}
