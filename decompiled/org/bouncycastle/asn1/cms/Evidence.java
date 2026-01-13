package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.tsp.EvidenceRecord;

public class Evidence extends ASN1Object implements ASN1Choice {
   private TimeStampTokenEvidence tstEvidence;
   private EvidenceRecord ersEvidence;
   private ASN1Sequence otherEvidence;

   public Evidence(TimeStampTokenEvidence var1) {
      this.tstEvidence = var1;
   }

   public Evidence(EvidenceRecord var1) {
      this.ersEvidence = var1;
   }

   private Evidence(ASN1TaggedObject var1) {
      if (var1.getTagNo() == 0) {
         this.tstEvidence = TimeStampTokenEvidence.getInstance(var1, false);
      } else if (var1.getTagNo() == 1) {
         this.ersEvidence = EvidenceRecord.getInstance(var1, false);
      } else {
         if (var1.getTagNo() != 2) {
            throw new IllegalArgumentException("unknown tag in Evidence");
         }

         this.otherEvidence = ASN1Sequence.getInstance(var1, false);
      }
   }

   public static Evidence getInstance(Object var0) {
      if (var0 == null || var0 instanceof Evidence) {
         return (Evidence)var0;
      } else if (var0 instanceof ASN1TaggedObject) {
         return new Evidence(ASN1TaggedObject.getInstance(var0, 128));
      } else {
         throw new IllegalArgumentException("unknown object in getInstance");
      }
   }

   public static Evidence getInstance(ASN1TaggedObject var0, boolean var1) {
      if (!var1) {
         throw new IllegalArgumentException("choice item must be explicitly tagged");
      } else {
         return getInstance(var0.getExplicitBaseObject());
      }
   }

   public TimeStampTokenEvidence getTstEvidence() {
      return this.tstEvidence;
   }

   public EvidenceRecord getErsEvidence() {
      return this.ersEvidence;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      if (this.tstEvidence != null) {
         return new DERTaggedObject(false, 0, this.tstEvidence);
      } else {
         return this.ersEvidence != null ? new DERTaggedObject(false, 1, this.ersEvidence) : new DERTaggedObject(false, 2, this.otherEvidence);
      }
   }
}
