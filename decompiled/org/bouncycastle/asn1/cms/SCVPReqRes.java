package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;

public class SCVPReqRes extends ASN1Object {
   private final ContentInfo request;
   private final ContentInfo response;

   public static SCVPReqRes getInstance(Object var0) {
      if (var0 instanceof SCVPReqRes) {
         return (SCVPReqRes)var0;
      } else {
         return var0 != null ? new SCVPReqRes(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   private SCVPReqRes(ASN1Sequence var1) {
      if (var1.getObjectAt(0) instanceof ASN1TaggedObject) {
         this.request = ContentInfo.getInstance(ASN1TaggedObject.getInstance(var1.getObjectAt(0)), true);
         this.response = ContentInfo.getInstance(var1.getObjectAt(1));
      } else {
         this.request = null;
         this.response = ContentInfo.getInstance(var1.getObjectAt(0));
      }
   }

   public SCVPReqRes(ContentInfo var1) {
      this.request = null;
      this.response = var1;
   }

   public SCVPReqRes(ContentInfo var1, ContentInfo var2) {
      this.request = var1;
      this.response = var2;
   }

   public ContentInfo getRequest() {
      return this.request;
   }

   public ContentInfo getResponse() {
      return this.response;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(2);
      if (this.request != null) {
         var1.add(new DERTaggedObject(true, 0, this.request));
      }

      var1.add(this.response);
      return new DERSequence(var1);
   }
}
