package org.bouncycastle.oer.its.ieee1609dot2;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Null;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.SequenceOfPsidSspRange;

public class SubjectPermissions extends ASN1Object implements ASN1Choice {
   public static final int explicit = 0;
   public static final int all = 1;
   private final ASN1Encodable subjectPermissions;
   private final int choice;

   SubjectPermissions(int var1, ASN1Encodable var2) {
      this.subjectPermissions = var2;
      this.choice = var1;
   }

   public static SubjectPermissions explicit(SequenceOfPsidSspRange var0) {
      return new SubjectPermissions(0, var0);
   }

   public static SubjectPermissions all() {
      return new SubjectPermissions(1, DERNull.INSTANCE);
   }

   private SubjectPermissions(ASN1TaggedObject var1) {
      this.choice = var1.getTagNo();
      switch (this.choice) {
         case 0:
            this.subjectPermissions = SequenceOfPsidSspRange.getInstance(var1.getExplicitBaseObject());
            break;
         case 1:
            this.subjectPermissions = ASN1Null.getInstance(var1.getExplicitBaseObject());
            break;
         default:
            throw new IllegalArgumentException("invalid choice value " + this.choice);
      }
   }

   public static SubjectPermissions getInstance(Object var0) {
      if (var0 instanceof SubjectPermissions) {
         return (SubjectPermissions)var0;
      } else {
         return var0 != null ? new SubjectPermissions(ASN1TaggedObject.getInstance(var0, 128)) : null;
      }
   }

   public ASN1Encodable getSubjectPermissions() {
      return this.subjectPermissions;
   }

   public int getChoice() {
      return this.choice;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERTaggedObject(this.choice, this.subjectPermissions);
   }
}
