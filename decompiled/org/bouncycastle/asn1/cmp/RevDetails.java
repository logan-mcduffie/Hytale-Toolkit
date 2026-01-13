package org.bouncycastle.asn1.cmp;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.crmf.CertTemplate;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.X509Extensions;

public class RevDetails extends ASN1Object {
   private final CertTemplate certDetails;
   private Extensions crlEntryDetails;

   private RevDetails(ASN1Sequence var1) {
      this.certDetails = CertTemplate.getInstance(var1.getObjectAt(0));
      if (var1.size() > 1) {
         this.crlEntryDetails = Extensions.getInstance(var1.getObjectAt(1));
      }
   }

   public RevDetails(CertTemplate var1) {
      this.certDetails = var1;
   }

   /** @deprecated */
   public RevDetails(CertTemplate var1, X509Extensions var2) {
      this.certDetails = var1;
      this.crlEntryDetails = Extensions.getInstance(var2.toASN1Primitive());
   }

   public RevDetails(CertTemplate var1, Extensions var2) {
      this.certDetails = var1;
      this.crlEntryDetails = var2;
   }

   public static RevDetails getInstance(Object var0) {
      if (var0 instanceof RevDetails) {
         return (RevDetails)var0;
      } else {
         return var0 != null ? new RevDetails(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public CertTemplate getCertDetails() {
      return this.certDetails;
   }

   public Extensions getCrlEntryDetails() {
      return this.crlEntryDetails;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(2);
      var1.add(this.certDetails);
      if (this.crlEntryDetails != null) {
         var1.add(this.crlEntryDetails);
      }

      return new DERSequence(var1);
   }
}
