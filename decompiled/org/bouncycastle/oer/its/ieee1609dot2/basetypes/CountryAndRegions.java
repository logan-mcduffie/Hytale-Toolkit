package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.oer.its.ItsUtils;

public class CountryAndRegions extends ASN1Object implements RegionInterface {
   private final CountryOnly countryOnly;
   private final SequenceOfUint8 regions;

   public CountryAndRegions(CountryOnly var1, SequenceOfUint8 var2) {
      this.countryOnly = var1;
      this.regions = SequenceOfUint8.getInstance(var2);
   }

   private CountryAndRegions(ASN1Sequence var1) {
      if (var1.size() != 2) {
         throw new IllegalArgumentException("expected sequence size of 2");
      } else {
         this.countryOnly = CountryOnly.getInstance(var1.getObjectAt(0));
         this.regions = SequenceOfUint8.getInstance(var1.getObjectAt(1));
      }
   }

   public static CountryAndRegions getInstance(Object var0) {
      if (var0 instanceof CountryAndRegions) {
         return (CountryAndRegions)var0;
      } else {
         return var0 != null ? new CountryAndRegions(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public static CountryAndRegions.Builder builder() {
      return new CountryAndRegions.Builder();
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return ItsUtils.toSequence(this.countryOnly, this.regions);
   }

   public CountryOnly getCountryOnly() {
      return this.countryOnly;
   }

   public SequenceOfUint8 getRegions() {
      return this.regions;
   }

   public static class Builder {
      private SequenceOfUint8 regionList;
      private CountryOnly countryOnly;

      public CountryAndRegions.Builder setCountryOnly(CountryOnly var1) {
         this.countryOnly = var1;
         return this;
      }

      public CountryAndRegions.Builder setRegions(SequenceOfUint8 var1) {
         this.regionList = var1;
         return this;
      }

      public CountryAndRegions createCountryAndRegions() {
         return new CountryAndRegions(this.countryOnly, this.regionList);
      }
   }
}
