package org.bouncycastle.asn1.ess;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.PolicyInformation;

public class OtherSigningCertificate extends ASN1Object {
   ASN1Sequence certs;
   ASN1Sequence policies;

   public static OtherSigningCertificate getInstance(Object var0) {
      if (var0 instanceof OtherSigningCertificate) {
         return (OtherSigningCertificate)var0;
      } else {
         return var0 != null ? new OtherSigningCertificate(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   private OtherSigningCertificate(ASN1Sequence var1) {
      if (var1.size() >= 1 && var1.size() <= 2) {
         this.certs = ASN1Sequence.getInstance(var1.getObjectAt(0));
         if (var1.size() > 1) {
            this.policies = ASN1Sequence.getInstance(var1.getObjectAt(1));
         }
      } else {
         throw new IllegalArgumentException("Bad sequence size: " + var1.size());
      }
   }

   public OtherSigningCertificate(OtherCertID var1) {
      this.certs = new DERSequence(var1);
   }

   public OtherCertID[] getCerts() {
      OtherCertID[] var1 = new OtherCertID[this.certs.size()];

      for (int var2 = 0; var2 != this.certs.size(); var2++) {
         var1[var2] = OtherCertID.getInstance(this.certs.getObjectAt(var2));
      }

      return var1;
   }

   public PolicyInformation[] getPolicies() {
      if (this.policies == null) {
         return null;
      } else {
         PolicyInformation[] var1 = new PolicyInformation[this.policies.size()];

         for (int var2 = 0; var2 != this.policies.size(); var2++) {
            var1[var2] = PolicyInformation.getInstance(this.policies.getObjectAt(var2));
         }

         return var1;
      }
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(2);
      var1.add(this.certs);
      if (this.policies != null) {
         var1.add(this.policies);
      }

      return new DERSequence(var1);
   }
}
