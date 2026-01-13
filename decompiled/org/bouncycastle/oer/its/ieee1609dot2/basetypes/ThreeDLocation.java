package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class ThreeDLocation extends ASN1Object {
   private final Latitude latitude;
   private final Longitude longitude;
   private final Elevation elevation;

   public ThreeDLocation(Latitude var1, Longitude var2, Elevation var3) {
      this.latitude = var1;
      this.longitude = var2;
      this.elevation = var3;
   }

   private ThreeDLocation(ASN1Sequence var1) {
      if (var1.size() != 3) {
         throw new IllegalArgumentException("expected sequence size of 3");
      } else {
         this.latitude = Latitude.getInstance(var1.getObjectAt(0));
         this.longitude = Longitude.getInstance(var1.getObjectAt(1));
         this.elevation = Elevation.getInstance(var1.getObjectAt(2));
      }
   }

   public static ThreeDLocation getInstance(Object var0) {
      if (var0 instanceof ThreeDLocation) {
         return (ThreeDLocation)var0;
      } else {
         return var0 != null ? new ThreeDLocation(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public static ThreeDLocation.Builder builder() {
      return new ThreeDLocation.Builder();
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERSequence(new ASN1Encodable[]{this.latitude, this.longitude, this.elevation});
   }

   public Latitude getLatitude() {
      return this.latitude;
   }

   public Longitude getLongitude() {
      return this.longitude;
   }

   public Elevation getElevation() {
      return this.elevation;
   }

   public static class Builder {
      private Latitude latitude;
      private Longitude longitude;
      private Elevation elevation;

      public ThreeDLocation.Builder setLatitude(Latitude var1) {
         this.latitude = var1;
         return this;
      }

      public ThreeDLocation.Builder setLongitude(Longitude var1) {
         this.longitude = var1;
         return this;
      }

      public ThreeDLocation.Builder setElevation(Elevation var1) {
         this.elevation = var1;
         return this;
      }

      public ThreeDLocation createThreeDLocation() {
         return new ThreeDLocation(this.latitude, this.longitude, this.elevation);
      }
   }
}
