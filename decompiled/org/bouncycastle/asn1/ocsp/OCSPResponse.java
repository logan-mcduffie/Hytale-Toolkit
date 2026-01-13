package org.bouncycastle.asn1.ocsp;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;

public class OCSPResponse extends ASN1Object {
   OCSPResponseStatus responseStatus;
   ResponseBytes responseBytes;

   public OCSPResponse(OCSPResponseStatus var1, ResponseBytes var2) {
      this.responseStatus = var1;
      this.responseBytes = var2;
   }

   private OCSPResponse(ASN1Sequence var1) {
      this.responseStatus = OCSPResponseStatus.getInstance(var1.getObjectAt(0));
      if (var1.size() == 2) {
         this.responseBytes = ResponseBytes.getInstance((ASN1TaggedObject)var1.getObjectAt(1), true);
      }
   }

   public static OCSPResponse getInstance(ASN1TaggedObject var0, boolean var1) {
      return getInstance(ASN1Sequence.getInstance(var0, var1));
   }

   public static OCSPResponse getInstance(Object var0) {
      if (var0 instanceof OCSPResponse) {
         return (OCSPResponse)var0;
      } else {
         return var0 != null ? new OCSPResponse(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public OCSPResponseStatus getResponseStatus() {
      return this.responseStatus;
   }

   public ResponseBytes getResponseBytes() {
      return this.responseBytes;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(2);
      var1.add(this.responseStatus);
      if (this.responseBytes != null) {
         var1.add(new DERTaggedObject(true, 0, this.responseBytes));
      }

      return new DERSequence(var1);
   }
}
