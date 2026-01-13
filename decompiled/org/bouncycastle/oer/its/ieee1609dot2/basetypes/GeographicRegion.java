package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERTaggedObject;

public class GeographicRegion extends ASN1Object implements ASN1Choice {
   public static final int circularRegion = 0;
   public static final int rectangularRegion = 1;
   public static final int polygonalRegion = 2;
   public static final int identifiedRegion = 3;
   private final int choice;
   private final ASN1Encodable geographicRegion;

   public GeographicRegion(int var1, ASN1Encodable var2) {
      this.choice = var1;
      this.geographicRegion = var2;
   }

   private GeographicRegion(ASN1TaggedObject var1) {
      this.choice = var1.getTagNo();
      switch (this.choice) {
         case 0:
            this.geographicRegion = CircularRegion.getInstance(var1.getExplicitBaseObject());
            break;
         case 1:
            this.geographicRegion = SequenceOfRectangularRegion.getInstance(var1.getExplicitBaseObject());
            break;
         case 2:
            this.geographicRegion = PolygonalRegion.getInstance(var1.getExplicitBaseObject());
            break;
         case 3:
            this.geographicRegion = SequenceOfIdentifiedRegion.getInstance(var1.getExplicitBaseObject());
            break;
         default:
            throw new IllegalArgumentException("invalid choice value " + this.choice);
      }
   }

   public static GeographicRegion circularRegion(CircularRegion var0) {
      return new GeographicRegion(0, var0);
   }

   public static GeographicRegion rectangularRegion(SequenceOfRectangularRegion var0) {
      return new GeographicRegion(1, var0);
   }

   public static GeographicRegion polygonalRegion(PolygonalRegion var0) {
      return new GeographicRegion(2, var0);
   }

   public static GeographicRegion identifiedRegion(SequenceOfIdentifiedRegion var0) {
      return new GeographicRegion(3, var0);
   }

   public static GeographicRegion getInstance(Object var0) {
      if (var0 instanceof GeographicRegion) {
         return (GeographicRegion)var0;
      } else {
         return var0 != null ? new GeographicRegion(ASN1TaggedObject.getInstance(var0, 128)) : null;
      }
   }

   public int getChoice() {
      return this.choice;
   }

   public ASN1Encodable getGeographicRegion() {
      return this.geographicRegion;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERTaggedObject(this.choice, this.geographicRegion);
   }
}
