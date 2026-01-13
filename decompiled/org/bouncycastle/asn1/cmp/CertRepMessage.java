package org.bouncycastle.asn1.cmp;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;

public class CertRepMessage extends ASN1Object {
   private final ASN1Sequence caPubs;
   private final ASN1Sequence response;

   private CertRepMessage(ASN1Sequence var1) {
      int var2 = 0;
      if (var1.size() > 1) {
         this.caPubs = ASN1Sequence.getInstance((ASN1TaggedObject)var1.getObjectAt(var2++), true);
      } else {
         this.caPubs = null;
      }

      this.response = ASN1Sequence.getInstance(var1.getObjectAt(var2));
   }

   public CertRepMessage(CMPCertificate[] var1, CertResponse[] var2) {
      if (var2 == null) {
         throw new IllegalArgumentException("'response' cannot be null");
      } else {
         if (var1 != null && var1.length != 0) {
            this.caPubs = new DERSequence(var1);
         } else {
            this.caPubs = null;
         }

         this.response = new DERSequence(var2);
      }
   }

   public static CertRepMessage getInstance(Object var0) {
      if (var0 instanceof CertRepMessage) {
         return (CertRepMessage)var0;
      } else {
         return var0 != null ? new CertRepMessage(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public CMPCertificate[] getCaPubs() {
      if (this.caPubs == null) {
         return null;
      } else {
         CMPCertificate[] var1 = new CMPCertificate[this.caPubs.size()];

         for (int var2 = 0; var2 != var1.length; var2++) {
            var1[var2] = CMPCertificate.getInstance(this.caPubs.getObjectAt(var2));
         }

         return var1;
      }
   }

   public CertResponse[] getResponse() {
      CertResponse[] var1 = new CertResponse[this.response.size()];

      for (int var2 = 0; var2 != var1.length; var2++) {
         var1[var2] = CertResponse.getInstance(this.response.getObjectAt(var2));
      }

      return var1;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(2);
      if (this.caPubs != null) {
         var1.add(new DERTaggedObject(true, 1, this.caPubs));
      }

      var1.add(this.response);
      return new DERSequence(var1);
   }
}
