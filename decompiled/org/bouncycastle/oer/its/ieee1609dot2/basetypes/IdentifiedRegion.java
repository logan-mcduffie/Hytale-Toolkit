package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERTaggedObject;

public class IdentifiedRegion extends ASN1Object implements ASN1Choice, RegionInterface {
   public static final int countryOnly = 0;
   public static final int countryAndRegions = 1;
   public static final int countryAndSubregions = 2;
   private final int choice;
   private final ASN1Encodable identifiedRegion;

   public IdentifiedRegion(int var1, ASN1Encodable var2) {
      this.choice = var1;
      this.identifiedRegion = var2;
   }

   private IdentifiedRegion(ASN1TaggedObject var1) {
      this.choice = var1.getTagNo();
      switch (this.choice) {
         case 0:
            this.identifiedRegion = CountryOnly.getInstance(var1.getExplicitBaseObject());
            break;
         case 1:
            this.identifiedRegion = CountryAndRegions.getInstance(var1.getExplicitBaseObject());
            break;
         case 2:
            this.identifiedRegion = CountryAndSubregions.getInstance(var1.getExplicitBaseObject());
            break;
         default:
            throw new IllegalArgumentException("invalid choice value " + this.choice);
      }
   }

   public static IdentifiedRegion countryOnly(CountryOnly var0) {
      return new IdentifiedRegion(0, var0);
   }

   public static IdentifiedRegion countryAndRegions(CountryAndRegions var0) {
      return new IdentifiedRegion(1, var0);
   }

   public static IdentifiedRegion countryAndSubregions(CountryAndSubregions var0) {
      return new IdentifiedRegion(2, var0);
   }

   public static IdentifiedRegion getInstance(Object var0) {
      if (var0 instanceof IdentifiedRegion) {
         return (IdentifiedRegion)var0;
      } else {
         return var0 != null ? new IdentifiedRegion(ASN1TaggedObject.getInstance(var0, 128)) : null;
      }
   }

   public int getChoice() {
      return this.choice;
   }

   public ASN1Encodable getIdentifiedRegion() {
      return this.identifiedRegion;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERTaggedObject(this.choice, this.identifiedRegion);
   }
}
