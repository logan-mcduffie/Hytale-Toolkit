package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.oer.its.ItsUtils;

public class SequenceOfRectangularRegion extends ASN1Object {
   private final List<RectangularRegion> rectangularRegions;

   public SequenceOfRectangularRegion(List<RectangularRegion> var1) {
      this.rectangularRegions = Collections.unmodifiableList(var1);
   }

   private SequenceOfRectangularRegion(ASN1Sequence var1) {
      ArrayList var2 = new ArrayList();
      Iterator var3 = var1.iterator();

      while (var3.hasNext()) {
         var2.add(RectangularRegion.getInstance(var3.next()));
      }

      this.rectangularRegions = Collections.unmodifiableList(var2);
   }

   public static SequenceOfRectangularRegion getInstance(Object var0) {
      if (var0 instanceof SequenceOfRectangularRegion) {
         return (SequenceOfRectangularRegion)var0;
      } else {
         return var0 != null ? new SequenceOfRectangularRegion(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public List<RectangularRegion> getRectangularRegions() {
      return this.rectangularRegions;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return ItsUtils.toSequence(this.rectangularRegions);
   }
}
