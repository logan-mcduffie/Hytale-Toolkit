package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.oer.its.ItsUtils;

public class CircularRegion extends ASN1Object implements RegionInterface {
   private final TwoDLocation center;
   private final UINT16 radius;

   public CircularRegion(TwoDLocation var1, UINT16 var2) {
      this.center = var1;
      this.radius = var2;
   }

   private CircularRegion(ASN1Sequence var1) {
      if (var1.size() != 2) {
         throw new IllegalArgumentException("expected sequence size of 2");
      } else {
         this.center = TwoDLocation.getInstance(var1.getObjectAt(0));
         this.radius = UINT16.getInstance(var1.getObjectAt(1));
      }
   }

   public static CircularRegion getInstance(Object var0) {
      if (var0 instanceof CircularRegion) {
         return (CircularRegion)var0;
      } else {
         return var0 != null ? new CircularRegion(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public TwoDLocation getCenter() {
      return this.center;
   }

   public UINT16 getRadius() {
      return this.radius;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return ItsUtils.toSequence(this.center, this.radius);
   }

   public static CircularRegion.Builder builder() {
      return new CircularRegion.Builder();
   }

   public static class Builder {
      private TwoDLocation center;
      private UINT16 radius;

      public CircularRegion.Builder setCenter(TwoDLocation var1) {
         this.center = var1;
         return this;
      }

      public CircularRegion.Builder setRadius(UINT16 var1) {
         this.radius = var1;
         return this;
      }

      public CircularRegion createCircularRegion() {
         return new CircularRegion(this.center, this.radius);
      }
   }
}
