package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class CountryAndSubregions extends ASN1Object {
   private final CountryOnly country;
   private final SequenceOfRegionAndSubregions regionAndSubregions;

   public CountryAndSubregions(CountryOnly var1, SequenceOfRegionAndSubregions var2) {
      this.country = var1;
      this.regionAndSubregions = var2;
   }

   private CountryAndSubregions(ASN1Sequence var1) {
      if (var1.size() != 2) {
         throw new IllegalArgumentException("expected sequence size of 2");
      } else {
         this.country = CountryOnly.getInstance(var1.getObjectAt(0));
         this.regionAndSubregions = SequenceOfRegionAndSubregions.getInstance(var1.getObjectAt(1));
      }
   }

   public static CountryAndSubregions getInstance(Object var0) {
      if (var0 instanceof CountryAndSubregions) {
         return (CountryAndSubregions)var0;
      } else {
         return var0 != null ? new CountryAndSubregions(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public CountryOnly getCountry() {
      return this.country;
   }

   public SequenceOfRegionAndSubregions getRegionAndSubregions() {
      return this.regionAndSubregions;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERSequence(new ASN1Encodable[]{this.country, this.regionAndSubregions});
   }

   public static CountryAndSubregions.Builder builder() {
      return new CountryAndSubregions.Builder();
   }

   public static class Builder {
      private CountryOnly country;
      private SequenceOfRegionAndSubregions regionAndSubregions;

      public CountryAndSubregions.Builder setCountry(CountryOnly var1) {
         this.country = var1;
         return this;
      }

      public CountryAndSubregions.Builder setRegionAndSubregions(SequenceOfRegionAndSubregions var1) {
         this.regionAndSubregions = var1;
         return this;
      }

      public CountryAndSubregions createCountryAndSubregions() {
         return new CountryAndSubregions(this.country, this.regionAndSubregions);
      }
   }
}
