package org.bouncycastle.oer.its.ieee1609dot2;

import java.util.Iterator;
import org.bouncycastle.asn1.ASN1Null;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.oer.OEROptional;
import org.bouncycastle.oer.its.ItsUtils;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.CrlSeries;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.GeographicRegion;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.HashedId3;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.PublicEncryptionKey;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.SequenceOfPsidSsp;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.SubjectAssurance;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.ValidityPeriod;

public class ToBeSignedCertificate extends ASN1Object {
   private final CertificateId id;
   private final HashedId3 cracaId;
   private final CrlSeries crlSeries;
   private final ValidityPeriod validityPeriod;
   private final GeographicRegion region;
   private final SubjectAssurance assuranceLevel;
   private final SequenceOfPsidSsp appPermissions;
   private final SequenceOfPsidGroupPermissions certIssuePermissions;
   private final SequenceOfPsidGroupPermissions certRequestPermissions;
   private final ASN1Null canRequestRollover;
   private final PublicEncryptionKey encryptionKey;
   private final VerificationKeyIndicator verifyKeyIndicator;

   public ToBeSignedCertificate(
      CertificateId var1,
      HashedId3 var2,
      CrlSeries var3,
      ValidityPeriod var4,
      GeographicRegion var5,
      SubjectAssurance var6,
      SequenceOfPsidSsp var7,
      SequenceOfPsidGroupPermissions var8,
      SequenceOfPsidGroupPermissions var9,
      ASN1Null var10,
      PublicEncryptionKey var11,
      VerificationKeyIndicator var12
   ) {
      this.id = var1;
      this.cracaId = var2;
      this.crlSeries = var3;
      this.validityPeriod = var4;
      this.region = var5;
      this.assuranceLevel = var6;
      this.appPermissions = var7;
      this.certIssuePermissions = var8;
      this.certRequestPermissions = var9;
      this.canRequestRollover = var10;
      this.encryptionKey = var11;
      this.verifyKeyIndicator = var12;
   }

   private ToBeSignedCertificate(ASN1Sequence var1) {
      Iterator var2 = ASN1Sequence.getInstance(var1).iterator();
      if (var1.size() != 12) {
         throw new IllegalArgumentException("expected sequence size of 12");
      } else {
         this.id = CertificateId.getInstance(var2.next());
         this.cracaId = HashedId3.getInstance(var2.next());
         this.crlSeries = CrlSeries.getInstance(var2.next());
         this.validityPeriod = ValidityPeriod.getInstance(var2.next());
         this.region = OEROptional.getValue(GeographicRegion.class, var2.next());
         this.assuranceLevel = OEROptional.getValue(SubjectAssurance.class, var2.next());
         this.appPermissions = OEROptional.getValue(SequenceOfPsidSsp.class, var2.next());
         this.certIssuePermissions = OEROptional.getValue(SequenceOfPsidGroupPermissions.class, var2.next());
         this.certRequestPermissions = OEROptional.getValue(SequenceOfPsidGroupPermissions.class, var2.next());
         this.canRequestRollover = OEROptional.getValue(ASN1Null.class, var2.next());
         this.encryptionKey = OEROptional.getValue(PublicEncryptionKey.class, var2.next());
         this.verifyKeyIndicator = VerificationKeyIndicator.getInstance(var2.next());
      }
   }

   public static ToBeSignedCertificate getInstance(Object var0) {
      if (var0 instanceof ToBeSignedCertificate) {
         return (ToBeSignedCertificate)var0;
      } else {
         return var0 != null ? new ToBeSignedCertificate(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public CertificateId getId() {
      return this.id;
   }

   public HashedId3 getCracaId() {
      return this.cracaId;
   }

   public CrlSeries getCrlSeries() {
      return this.crlSeries;
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

   public SequenceOfPsidGroupPermissions getCertRequestPermissions() {
      return this.certRequestPermissions;
   }

   public ASN1Null getCanRequestRollover() {
      return this.canRequestRollover;
   }

   public PublicEncryptionKey getEncryptionKey() {
      return this.encryptionKey;
   }

   public VerificationKeyIndicator getVerifyKeyIndicator() {
      return this.verifyKeyIndicator;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return ItsUtils.toSequence(
         this.id,
         this.cracaId,
         this.crlSeries,
         this.validityPeriod,
         OEROptional.getInstance(this.region),
         OEROptional.getInstance(this.assuranceLevel),
         OEROptional.getInstance(this.appPermissions),
         OEROptional.getInstance(this.certIssuePermissions),
         OEROptional.getInstance(this.certRequestPermissions),
         OEROptional.getInstance(this.canRequestRollover),
         OEROptional.getInstance(this.encryptionKey),
         this.verifyKeyIndicator
      );
   }

   public static ToBeSignedCertificate.Builder builder() {
      return new ToBeSignedCertificate.Builder();
   }

   public static class Builder {
      private CertificateId id;
      private HashedId3 cracaId;
      private CrlSeries crlSeries;
      private ValidityPeriod validityPeriod;
      private GeographicRegion region;
      private SubjectAssurance assuranceLevel;
      private SequenceOfPsidSsp appPermissions;
      private SequenceOfPsidGroupPermissions certIssuePermissions;
      private SequenceOfPsidGroupPermissions certRequestPermissions;
      private ASN1Null canRequestRollover;
      private PublicEncryptionKey encryptionKey;
      private VerificationKeyIndicator verifyKeyIndicator;

      public Builder() {
      }

      public Builder(ToBeSignedCertificate.Builder var1) {
         this.id = var1.id;
         this.cracaId = var1.cracaId;
         this.crlSeries = var1.crlSeries;
         this.validityPeriod = var1.validityPeriod;
         this.region = var1.region;
         this.assuranceLevel = var1.assuranceLevel;
         this.appPermissions = var1.appPermissions;
         this.certIssuePermissions = var1.certIssuePermissions;
         this.certRequestPermissions = var1.certRequestPermissions;
         this.canRequestRollover = var1.canRequestRollover;
         this.encryptionKey = var1.encryptionKey;
         this.verifyKeyIndicator = var1.verifyKeyIndicator;
      }

      public Builder(ToBeSignedCertificate var1) {
         this.id = var1.id;
         this.cracaId = var1.cracaId;
         this.crlSeries = var1.crlSeries;
         this.validityPeriod = var1.validityPeriod;
         this.region = var1.region;
         this.assuranceLevel = var1.assuranceLevel;
         this.appPermissions = var1.appPermissions;
         this.certIssuePermissions = var1.certIssuePermissions;
         this.certRequestPermissions = var1.certRequestPermissions;
         this.canRequestRollover = var1.canRequestRollover;
         this.encryptionKey = var1.encryptionKey;
         this.verifyKeyIndicator = var1.verifyKeyIndicator;
      }

      public ToBeSignedCertificate.Builder setId(CertificateId var1) {
         this.id = var1;
         return this;
      }

      public ToBeSignedCertificate.Builder setCracaId(HashedId3 var1) {
         this.cracaId = var1;
         return this;
      }

      public ToBeSignedCertificate.Builder setCrlSeries(CrlSeries var1) {
         this.crlSeries = var1;
         return this;
      }

      public ToBeSignedCertificate.Builder setValidityPeriod(ValidityPeriod var1) {
         this.validityPeriod = var1;
         return this;
      }

      public ToBeSignedCertificate.Builder setRegion(GeographicRegion var1) {
         this.region = var1;
         return this;
      }

      public ToBeSignedCertificate.Builder setAssuranceLevel(SubjectAssurance var1) {
         this.assuranceLevel = var1;
         return this;
      }

      public ToBeSignedCertificate.Builder setAppPermissions(SequenceOfPsidSsp var1) {
         this.appPermissions = var1;
         return this;
      }

      public ToBeSignedCertificate.Builder setCertIssuePermissions(SequenceOfPsidGroupPermissions var1) {
         this.certIssuePermissions = var1;
         return this;
      }

      public ToBeSignedCertificate.Builder setCertRequestPermissions(SequenceOfPsidGroupPermissions var1) {
         this.certRequestPermissions = var1;
         return this;
      }

      public ToBeSignedCertificate.Builder setCanRequestRollover() {
         this.canRequestRollover = DERNull.INSTANCE;
         return this;
      }

      public ToBeSignedCertificate.Builder setEncryptionKey(PublicEncryptionKey var1) {
         this.encryptionKey = var1;
         return this;
      }

      public ToBeSignedCertificate.Builder setVerifyKeyIndicator(VerificationKeyIndicator var1) {
         this.verifyKeyIndicator = var1;
         return this;
      }

      public ToBeSignedCertificate createToBeSignedCertificate() {
         return new ToBeSignedCertificate(
            this.id,
            this.cracaId,
            this.crlSeries,
            this.validityPeriod,
            this.region,
            this.assuranceLevel,
            this.appPermissions,
            this.certIssuePermissions,
            this.certRequestPermissions,
            this.canRequestRollover,
            this.encryptionKey,
            this.verifyKeyIndicator
         );
      }
   }
}
