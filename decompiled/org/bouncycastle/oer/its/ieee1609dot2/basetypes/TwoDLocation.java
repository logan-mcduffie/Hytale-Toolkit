package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class TwoDLocation extends ASN1Object {
   private final Latitude latitude;
   private final Longitude longitude;

   public TwoDLocation(Latitude var1, Longitude var2) {
      this.latitude = var1;
      this.longitude = var2;
   }

   private TwoDLocation(ASN1Sequence var1) {
      if (var1.size() != 2) {
         throw new IllegalArgumentException("expected sequence size of 2");
      } else {
         this.latitude = Latitude.getInstance(var1.getObjectAt(0));
         this.longitude = Longitude.getInstance(var1.getObjectAt(1));
      }
   }

   public static TwoDLocation getInstance(Object var0) {
      if (var0 instanceof TwoDLocation) {
         return (TwoDLocation)var0;
      } else {
         return var0 != null ? new TwoDLocation(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERSequence(new ASN1Encodable[]{this.latitude, this.longitude});
   }

   public Latitude getLatitude() {
      return this.latitude;
   }

   public Longitude getLongitude() {
      return this.longitude;
   }

   public static TwoDLocation.Builder builder() {
      return new TwoDLocation.Builder();
   }

   public static class Builder {
      private Latitude latitude;
      private Longitude longitude;

      public TwoDLocation.Builder setLatitude(Latitude var1) {
         this.latitude = var1;
         return this;
      }

      public TwoDLocation.Builder setLongitude(Longitude var1) {
         this.longitude = var1;
         return this;
      }

      public TwoDLocation createTwoDLocation() {
         return new TwoDLocation(this.latitude, this.longitude);
      }
   }
}
