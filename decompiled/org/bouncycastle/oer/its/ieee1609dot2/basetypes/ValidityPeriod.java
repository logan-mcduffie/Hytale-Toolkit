package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class ValidityPeriod extends ASN1Object {
   private final Time32 start;
   private final Duration duration;

   public ValidityPeriod(Time32 var1, Duration var2) {
      this.start = var1;
      this.duration = var2;
   }

   public static ValidityPeriod getInstance(Object var0) {
      if (var0 instanceof ValidityPeriod) {
         return (ValidityPeriod)var0;
      } else {
         return var0 != null ? new ValidityPeriod(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   private ValidityPeriod(ASN1Sequence var1) {
      if (var1.size() != 2) {
         throw new IllegalArgumentException("expected sequence size of 2");
      } else {
         this.start = Time32.getInstance(var1.getObjectAt(0));
         this.duration = Duration.getInstance(var1.getObjectAt(1));
      }
   }

   public static ValidityPeriod.Builder builder() {
      return new ValidityPeriod.Builder();
   }

   public Time32 getStart() {
      return this.start;
   }

   public Duration getDuration() {
      return this.duration;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERSequence(new ASN1Encodable[]{this.start, this.duration});
   }

   @Override
   public String toString() {
      return "ValidityPeriod[" + this.start + " " + this.duration + "]";
   }

   public static class Builder {
      private Time32 start;
      private Duration duration;

      public ValidityPeriod.Builder setStart(Time32 var1) {
         this.start = var1;
         return this;
      }

      public ValidityPeriod.Builder setDuration(Duration var1) {
         this.duration = var1;
         return this;
      }

      public ValidityPeriod createValidityPeriod() {
         return new ValidityPeriod(this.start, this.duration);
      }
   }
}
