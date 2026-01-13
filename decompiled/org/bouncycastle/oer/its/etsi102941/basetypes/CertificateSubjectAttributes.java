package org.bouncycastle.oer.its.etsi102941.basetypes;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.oer.OEROptional;
import org.bouncycastle.oer.its.ieee1609dot2.CertificateId;
import org.bouncycastle.oer.its.ieee1609dot2.SequenceOfPsidGroupPermissions;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.GeographicRegion;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.SequenceOfPsidSsp;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.SubjectAssurance;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.ValidityPeriod;

public class CertificateSubjectAttributes extends ASN1Object {
   private final CertificateId id;
   private final ValidityPeriod validityPeriod;
   private final GeographicRegion region;
   private final SubjectAssurance assuranceLevel;
   private final SequenceOfPsidSsp appPermissions;
   private final SequenceOfPsidGroupPermissions certIssuePermissions;

   public CertificateSubjectAttributes(
      CertificateId var1, ValidityPeriod var2, GeographicRegion var3, SubjectAssurance var4, SequenceOfPsidSsp var5, SequenceOfPsidGroupPermissions var6
   ) {
      this.id = var1;
      this.validityPeriod = var2;
      this.region = var3;
      this.assuranceLevel = var4;
      this.appPermissions = var5;
      this.certIssuePermissions = var6;
   }

   private CertificateSubjectAttributes(ASN1Sequence var1) {
      if (var1.size() != 6) {
         throw new IllegalArgumentException("expected sequence size of 6");
      } else {
         this.id = OEROptional.getValue(CertificateId.class, var1.getObjectAt(0));
         this.validityPeriod = OEROptional.getValue(ValidityPeriod.class, var1.getObjectAt(1));
         this.region = OEROptional.getValue(GeographicRegion.class, var1.getObjectAt(2));
         this.assuranceLevel = OEROptional.getValue(SubjectAssurance.class, var1.getObjectAt(3));
         this.appPermissions = OEROptional.getValue(SequenceOfPsidSsp.class, var1.getObjectAt(4));
         this.certIssuePermissions = OEROptional.getValue(SequenceOfPsidGroupPermissions.class, var1.getObjectAt(5));
      }
   }

   public static CertificateSubjectAttributes getInstance(Object var0) {
      if (var0 instanceof CertificateSubjectAttributes) {
         return (CertificateSubjectAttributes)var0;
      } else {
         return var0 != null ? new CertificateSubjectAttributes(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public CertificateId getId() {
      return this.id;
   }

   public ValidityPeriod getValidityPeriod() {
      return this.validityPeriod;
   }

   public GeographicRegion getRegion() {
      return this.region;
   }

   public SubjectAssurance getAssuranceLevel() {
      return this.assuranceLevel;
   }

   public SequenceOfPsidSsp getAppPermissions() {
      return this.appPermissions;
   }

   public SequenceOfPsidGroupPermissions getCertIssuePermissions() {
      return this.certIssuePermissions;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERSequence(
         new ASN1Encodable[]{
            OEROptional.getInstance(this.id),
            OEROptional.getInstance(this.validityPeriod),
            OEROptional.getInstance(this.region),
            OEROptional.getInstance(this.assuranceLevel),
            OEROptional.getInstance(this.appPermissions),
            OEROptional.getInstance(this.certIssuePermissions)
         }
      );
   }
}
