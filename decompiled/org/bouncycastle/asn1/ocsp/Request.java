package org.bouncycastle.asn1.ocsp;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.Extensions;

public class Request extends ASN1Object {
   CertID reqCert;
   Extensions singleRequestExtensions;

   public Request(CertID var1, Extensions var2) {
      this.reqCert = var1;
      this.singleRequestExtensions = var2;
   }

   private Request(ASN1Sequence var1) {
      this.reqCert = CertID.getInstance(var1.getObjectAt(0));
      if (var1.size() == 2) {
         this.singleRequestExtensions = Extensions.getInstance((ASN1TaggedObject)var1.getObjectAt(1), true);
      }
   }

   public static Request getInstance(ASN1TaggedObject var0, boolean var1) {
      return getInstance(ASN1Sequence.getInstance(var0, var1));
   }

   public static Request getInstance(Object var0) {
      if (var0 instanceof Request) {
         return (Request)var0;
      } else {
         return var0 != null ? new Request(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public CertID getReqCert() {
      return this.reqCert;
   }

   public Extensions getSingleRequestExtensions() {
      return this.singleRequestExtensions;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(2);
      var1.add(this.reqCert);
      if (this.singleRequestExtensions != null) {
         var1.add(new DERTaggedObject(true, 0, this.singleRequestExtensions));
      }

      return new DERSequence(var1);
   }
}
