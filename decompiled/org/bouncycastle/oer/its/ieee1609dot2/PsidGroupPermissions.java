package org.bouncycastle.oer.its.ieee1609dot2;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.oer.OEROptional;

public class PsidGroupPermissions extends ASN1Object {
   private final SubjectPermissions subjectPermissions;
   private final ASN1Integer minChainLength;
   private final ASN1Integer chainLengthRange;
   private final EndEntityType eeType;

   private PsidGroupPermissions(ASN1Sequence var1) {
      if (var1.size() != 4) {
         throw new IllegalArgumentException("expected sequence size of 4");
      } else {
         this.subjectPermissions = SubjectPermissions.getInstance(var1.getObjectAt(0));
         this.minChainLength = OEROptional.getInstance(var1.getObjectAt(1)).getObject(ASN1Integer.class);
         this.chainLengthRange = OEROptional.getInstance(var1.getObjectAt(2)).getObject(ASN1Integer.class);
         this.eeType = OEROptional.getInstance(var1.getObjectAt(3)).getObject(EndEntityType.class);
      }
   }

   public PsidGroupPermissions(SubjectPermissions var1, ASN1Integer var2, ASN1Integer var3, EndEntityType var4) {
      this.subjectPermissions = var1;
      this.minChainLength = var2;
      this.chainLengthRange = var3;
      this.eeType = var4;
   }

   public static PsidGroupPermissions getInstance(Object var0) {
      if (var0 instanceof PsidGroupPermissions) {
         return (PsidGroupPermissions)var0;
      } else {
         return var0 != null ? new PsidGroupPermissions(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public static PsidGroupPermissions.Builder builder() {
      return new PsidGroupPermissions.Builder();
   }

   public SubjectPermissions getSubjectPermissions() {
      return this.subjectPermissions;
   }

   public ASN1Integer getMinChainLength() {
      return this.minChainLength;
   }

   public EndEntityType getEeType() {
      return this.eeType;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERSequence(
         new ASN1Encodable[]{
            this.subjectPermissions,
            OEROptional.getInstance(this.minChainLength),
            OEROptional.getInstance(this.chainLengthRange),
            OEROptional.getInstance(this.eeType)
         }
      );
   }

   public ASN1Integer getChainLengthRange() {
      return this.chainLengthRange;
   }

   public static class Builder {
      private SubjectPermissions subjectPermissions;
      private ASN1Integer minChainLength;
      private ASN1Integer chainLengthRange;
      private EndEntityType eeType;

      public PsidGroupPermissions.Builder setSubjectPermissions(SubjectPermissions var1) {
         this.subjectPermissions = var1;
         return this;
      }

      public PsidGroupPermissions.Builder setMinChainLength(BigInteger var1) {
         this.minChainLength = new ASN1Integer(var1);
         return this;
      }

      public PsidGroupPermissions.Builder setMinChainLength(long var1) {
         this.minChainLength = new ASN1Integer(var1);
         return this;
      }

      public PsidGroupPermissions.Builder setChainLengthRange(ASN1Integer var1) {
         this.chainLengthRange = var1;
         return this;
      }

      public PsidGroupPermissions.Builder setMinChainLength(ASN1Integer var1) {
         this.minChainLength = var1;
         return this;
      }

      public PsidGroupPermissions.Builder setChainLengthRange(BigInteger var1) {
         this.chainLengthRange = new ASN1Integer(var1);
         return this;
      }

      public PsidGroupPermissions.Builder setChainLengthRange(long var1) {
         this.chainLengthRange = new ASN1Integer(var1);
         return this;
      }

      public PsidGroupPermissions.Builder setEeType(EndEntityType var1) {
         this.eeType = var1;
         return this;
      }

      public PsidGroupPermissions createPsidGroupPermissions() {
         return new PsidGroupPermissions(this.subjectPermissions, this.minChainLength, this.chainLengthRange, this.eeType);
      }
   }
}
