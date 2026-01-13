package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class RectangularRegion extends ASN1Object implements RegionInterface {
   private final TwoDLocation northWest;
   private final TwoDLocation southEast;

   public RectangularRegion(TwoDLocation var1, TwoDLocation var2) {
      this.northWest = var1;
      this.southEast = var2;
   }

   private RectangularRegion(ASN1Sequence var1) {
      if (var1.size() != 2) {
         throw new IllegalArgumentException("expected sequence size of 2");
      } else {
         this.northWest = TwoDLocation.getInstance(var1.getObjectAt(0));
         this.southEast = TwoDLocation.getInstance(var1.getObjectAt(1));
      }
   }

   public static RectangularRegion getInstance(Object var0) {
      if (var0 instanceof RectangularRegion) {
         return (RectangularRegion)var0;
      } else {
         return var0 != null ? new RectangularRegion(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public TwoDLocation getNorthWest() {
      return this.northWest;
   }

   public TwoDLocation getSouthEast() {
      return this.southEast;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERSequence(new ASN1Encodable[]{this.northWest, this.southEast});
   }

   public static RectangularRegion.Builder builder() {
      return new RectangularRegion.Builder();
   }

   public static class Builder {
      private TwoDLocation northWest;
      private TwoDLocation southEast;

      public RectangularRegion.Builder setNorthWest(TwoDLocation var1) {
         this.northWest = var1;
         return this;
      }

      public RectangularRegion.Builder setSouthEast(TwoDLocation var1) {
         this.southEast = var1;
         return this;
      }

      public RectangularRegion createRectangularRegion() {
         return new RectangularRegion(this.northWest, this.southEast);
      }
   }
}
