package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.oer.its.ItsUtils;

public class RegionAndSubregions extends ASN1Object implements RegionInterface {
   private final UINT8 region;
   private final SequenceOfUint16 subregions;

   public RegionAndSubregions(UINT8 var1, SequenceOfUint16 var2) {
      this.region = var1;
      this.subregions = var2;
   }

   private RegionAndSubregions(ASN1Sequence var1) {
      if (var1.size() != 2) {
         throw new IllegalArgumentException("expected sequence size of 2");
      } else {
         this.region = UINT8.getInstance(var1.getObjectAt(0));
         this.subregions = SequenceOfUint16.getInstance(var1.getObjectAt(1));
      }
   }

   public UINT8 getRegion() {
      return this.region;
   }

   public SequenceOfUint16 getSubregions() {
      return this.subregions;
   }

   public static RegionAndSubregions getInstance(Object var0) {
      if (var0 instanceof RegionAndSubregions) {
         return (RegionAndSubregions)var0;
      } else {
         return var0 != null ? new RegionAndSubregions(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return ItsUtils.toSequence(this.region, this.subregions);
   }

   public static RegionAndSubregions.Builder builder() {
      return new RegionAndSubregions.Builder();
   }

   public static class Builder {
      private UINT8 region;
      private SequenceOfUint16 subRegions;

      public RegionAndSubregions.Builder setRegion(UINT8 var1) {
         this.region = var1;
         return this;
      }

      public RegionAndSubregions.Builder setSubregions(SequenceOfUint16 var1) {
         this.subRegions = var1;
         return this;
      }

      public RegionAndSubregions createRegionAndSubregions() {
         return new RegionAndSubregions(this.region, this.subRegions);
      }
   }
}
