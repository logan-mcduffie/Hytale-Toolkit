package org.bouncycastle.asn1.isismtt.x509;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.GeneralName;

public class AdmissionSyntax extends ASN1Object {
   private GeneralName admissionAuthority;
   private ASN1Sequence contentsOfAdmissions;

   public static AdmissionSyntax getInstance(Object var0) {
      if (var0 == null || var0 instanceof AdmissionSyntax) {
         return (AdmissionSyntax)var0;
      } else if (var0 instanceof ASN1Sequence) {
         return new AdmissionSyntax((ASN1Sequence)var0);
      } else {
         throw new IllegalArgumentException("illegal object in getInstance: " + var0.getClass().getName());
      }
   }

   private AdmissionSyntax(ASN1Sequence var1) {
      switch (var1.size()) {
         case 1:
            this.contentsOfAdmissions = DERSequence.getInstance(var1.getObjectAt(0));
            break;
         case 2:
            this.admissionAuthority = GeneralName.getInstance(var1.getObjectAt(0));
            this.contentsOfAdmissions = DERSequence.getInstance(var1.getObjectAt(1));
            break;
         default:
            throw new IllegalArgumentException("Bad sequence size: " + var1.size());
      }
   }

   public AdmissionSyntax(GeneralName var1, ASN1Sequence var2) {
      this.admissionAuthority = var1;
      this.contentsOfAdmissions = var2;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(2);
      if (this.admissionAuthority != null) {
         var1.add(this.admissionAuthority);
      }

      var1.add(this.contentsOfAdmissions);
      return new DERSequence(var1);
   }

   public GeneralName getAdmissionAuthority() {
      return this.admissionAuthority;
   }

   public Admissions[] getContentsOfAdmissions() {
      Admissions[] var1 = new Admissions[this.contentsOfAdmissions.size()];
      int var2 = 0;
      Enumeration var3 = this.contentsOfAdmissions.getObjects();

      while (var3.hasMoreElements()) {
         var1[var2++] = Admissions.getInstance(var3.nextElement());
      }

      return var1;
   }
}
