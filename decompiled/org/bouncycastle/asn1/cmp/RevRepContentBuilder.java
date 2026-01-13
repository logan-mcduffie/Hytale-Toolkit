package org.bouncycastle.asn1.cmp;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.crmf.CertId;
import org.bouncycastle.asn1.x509.CertificateList;

public class RevRepContentBuilder {
   private final ASN1EncodableVector status = new ASN1EncodableVector();
   private final ASN1EncodableVector revCerts = new ASN1EncodableVector();
   private final ASN1EncodableVector crls = new ASN1EncodableVector();

   public RevRepContentBuilder add(PKIStatusInfo var1) {
      this.status.add(var1);
      return this;
   }

   public RevRepContentBuilder add(PKIStatusInfo var1, CertId var2) {
      if (this.status.size() != this.revCerts.size()) {
         throw new IllegalStateException("status and revCerts sequence must be in common order");
      } else {
         this.status.add(var1);
         this.revCerts.add(var2);
         return this;
      }
   }

   public RevRepContentBuilder addCrl(CertificateList var1) {
      this.crls.add(var1);
      return this;
   }

   public RevRepContent build() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(3);
      var1.add(new DERSequence(this.status));
      if (this.revCerts.size() != 0) {
         var1.add(new DERTaggedObject(true, 0, new DERSequence(this.revCerts)));
      }

      if (this.crls.size() != 0) {
         var1.add(new DERTaggedObject(true, 1, new DERSequence(this.crls)));
      }

      return RevRepContent.getInstance(new DERSequence(var1));
   }
}
