package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.oer.its.ItsUtils;

public class SequenceOfRegionAndSubregions extends ASN1Object {
   private final List<RegionAndSubregions> regionAndSubregions;

   public SequenceOfRegionAndSubregions(List<RegionAndSubregions> var1) {
      this.regionAndSubregions = Collections.unmodifiableList(var1);
   }

   private SequenceOfRegionAndSubregions(ASN1Sequence var1) {
      ArrayList var2 = new ArrayList();
      Iterator var3 = var1.iterator();

      while (var3.hasNext()) {
         var2.add(RegionAndSubregions.getInstance(var3.next()));
      }

      this.regionAndSubregions = Collections.unmodifiableList(var2);
   }

   public static SequenceOfRegionAndSubregions getInstance(Object var0) {
      if (var0 instanceof SequenceOfRegionAndSubregions) {
         return (SequenceOfRegionAndSubregions)var0;
      } else {
         return var0 != null ? new SequenceOfRegionAndSubregions(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public List<RegionAndSubregions> getRegionAndSubregions() {
      return this.regionAndSubregions;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return ItsUtils.toSequence(this.regionAndSubregions);
   }
}
