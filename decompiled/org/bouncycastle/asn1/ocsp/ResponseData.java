package org.bouncycastle.asn1.ocsp;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.X509Extensions;

public class ResponseData extends ASN1Object {
   private static final ASN1Integer V1 = new ASN1Integer(0L);
   private boolean versionPresent;
   private ASN1Integer version;
   private ResponderID responderID;
   private ASN1GeneralizedTime producedAt;
   private ASN1Sequence responses;
   private Extensions responseExtensions;

   public ResponseData(ASN1Integer var1, ResponderID var2, ASN1GeneralizedTime var3, ASN1Sequence var4, Extensions var5) {
      this.version = var1;
      this.responderID = var2;
      this.producedAt = var3;
      this.responses = var4;
      this.responseExtensions = var5;
   }

   /** @deprecated */
   public ResponseData(ResponderID var1, ASN1GeneralizedTime var2, ASN1Sequence var3, X509Extensions var4) {
      this(V1, var1, ASN1GeneralizedTime.getInstance(var2), var3, Extensions.getInstance(var4));
   }

   public ResponseData(ResponderID var1, ASN1GeneralizedTime var2, ASN1Sequence var3, Extensions var4) {
      this(V1, var1, var2, var3, var4);
   }

   private ResponseData(ASN1Sequence var1) {
      int var2 = 0;
      if (var1.getObjectAt(0) instanceof ASN1TaggedObject) {
         ASN1TaggedObject var3 = (ASN1TaggedObject)var1.getObjectAt(0);
         if (var3.getTagNo() == 0) {
            this.versionPresent = true;
            this.version = ASN1Integer.getInstance((ASN1TaggedObject)var1.getObjectAt(0), true);
            var2++;
         } else {
            this.version = V1;
         }
      } else {
         this.version = V1;
      }

      this.responderID = ResponderID.getInstance(var1.getObjectAt(var2++));
      this.producedAt = ASN1GeneralizedTime.getInstance(var1.getObjectAt(var2++));
      this.responses = (ASN1Sequence)var1.getObjectAt(var2++);
      if (var1.size() > var2) {
         this.responseExtensions = Extensions.getInstance((ASN1TaggedObject)var1.getObjectAt(var2), true);
      }
   }

   public static ResponseData getInstance(ASN1TaggedObject var0, boolean var1) {
      return getInstance(ASN1Sequence.getInstance(var0, var1));
   }

   public static ResponseData getInstance(Object var0) {
      if (var0 instanceof ResponseData) {
         return (ResponseData)var0;
      } else {
         return var0 != null ? new ResponseData(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public ASN1Integer getVersion() {
      return this.version;
   }

   public ResponderID getResponderID() {
      return this.responderID;
   }

   public ASN1GeneralizedTime getProducedAt() {
      return this.producedAt;
   }

   public ASN1Sequence getResponses() {
      return this.responses;
   }

   public Extensions getResponseExtensions() {
      return this.responseExtensions;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(5);
      if (this.versionPresent || !this.version.equals(V1)) {
         var1.add(new DERTaggedObject(true, 0, this.version));
      }

      var1.add(this.responderID);
      var1.add(this.producedAt);
      var1.add(this.responses);
      if (this.responseExtensions != null) {
         var1.add(new DERTaggedObject(true, 1, this.responseExtensions));
      }

      return new DERSequence(var1);
   }
}
