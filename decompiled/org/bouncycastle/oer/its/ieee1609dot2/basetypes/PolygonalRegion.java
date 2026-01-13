package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.oer.its.ItsUtils;

public class PolygonalRegion extends ASN1Object implements RegionInterface {
   private final List<TwoDLocation> twoDLocations;

   public PolygonalRegion(List<TwoDLocation> var1) {
      this.twoDLocations = Collections.unmodifiableList(var1);
   }

   private PolygonalRegion(ASN1Sequence var1) {
      ArrayList var2 = new ArrayList();
      Iterator var3 = var1.iterator();

      while (var3.hasNext()) {
         var2.add(TwoDLocation.getInstance(var3.next()));
      }

      this.twoDLocations = Collections.unmodifiableList(var2);
   }

   public static PolygonalRegion getInstance(Object var0) {
      if (var0 instanceof PolygonalRegion) {
         return (PolygonalRegion)var0;
      } else {
         return var0 != null ? new PolygonalRegion(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public List<TwoDLocation> getTwoDLocations() {
      return this.twoDLocations;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return ItsUtils.toSequence(this.twoDLocations);
   }

   public static class Builder {
      private List<TwoDLocation> locations = new ArrayList<>();

      public PolygonalRegion.Builder setLocations(List<TwoDLocation> var1) {
         this.locations = var1;
         return this;
      }

      public PolygonalRegion.Builder setLocations(TwoDLocation... var1) {
         this.locations.addAll(Arrays.asList(var1));
         return this;
      }

      public PolygonalRegion createPolygonalRegion() {
         return new PolygonalRegion(this.locations);
      }
   }
}
