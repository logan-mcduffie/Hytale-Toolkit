package org.bouncycastle.asn1.ocsp;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.X509Extensions;

public class TBSRequest extends ASN1Object {
   private static final ASN1Integer V1 = new ASN1Integer(0L);
   ASN1Integer version;
   GeneralName requestorName;
   ASN1Sequence requestList;
   Extensions requestExtensions;
   boolean versionSet;

   /** @deprecated */
   public TBSRequest(GeneralName var1, ASN1Sequence var2, X509Extensions var3) {
      this.version = V1;
      this.requestorName = var1;
      this.requestList = var2;
      this.requestExtensions = Extensions.getInstance(var3);
   }

   public TBSRequest(GeneralName var1, ASN1Sequence var2, Extensions var3) {
      this.version = V1;
      this.requestorName = var1;
      this.requestList = var2;
      this.requestExtensions = var3;
   }

   private TBSRequest(ASN1Sequence var1) {
      int var2 = 0;
      if (var1.getObjectAt(0) instanceof ASN1TaggedObject) {
         ASN1TaggedObject var3 = (ASN1TaggedObject)var1.getObjectAt(0);
         if (var3.getTagNo() == 0) {
            this.versionSet = true;
            this.version = ASN1Integer.getInstance((ASN1TaggedObject)var1.getObjectAt(0), true);
            var2++;
         } else {
            this.version = V1;
         }
      } else {
         this.version = V1;
      }

      if (var1.getObjectAt(var2) instanceof ASN1TaggedObject) {
         this.requestorName = GeneralName.getInstance((ASN1TaggedObject)var1.getObjectAt(var2++), true);
      }

      this.requestList = (ASN1Sequence)var1.getObjectAt(var2++);
      if (var1.size() == var2 + 1) {
         this.requestExtensions = Extensions.getInstance((ASN1TaggedObject)var1.getObjectAt(var2), true);
      }
   }

   public static TBSRequest getInstance(ASN1TaggedObject var0, boolean var1) {
      return getInstance(ASN1Sequence.getInstance(var0, var1));
   }

   public static TBSRequest getInstance(Object var0) {
      if (var0 instanceof TBSRequest) {
         return (TBSRequest)var0;
      } else {
         return var0 != null ? new TBSRequest(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public ASN1Integer getVersion() {
      return this.version;
   }

   public GeneralName getRequestorName() {
      return this.requestorName;
   }

   public ASN1Sequence getRequestList() {
      return this.requestList;
   }

   public Extensions getRequestExtensions() {
      return this.requestExtensions;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(4);
      if (!this.version.equals(V1) || this.versionSet) {
         var1.add(new DERTaggedObject(true, 0, this.version));
      }

      if (this.requestorName != null) {
         var1.add(new DERTaggedObject(true, 1, this.requestorName));
      }

      var1.add(this.requestList);
      if (this.requestExtensions != null) {
         var1.add(new DERTaggedObject(true, 2, this.requestExtensions));
      }

      return new DERSequence(var1);
   }
}
