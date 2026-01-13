package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.oer.its.ItsUtils;

public class SequenceOfIdentifiedRegion extends ASN1Object {
   private final List<IdentifiedRegion> identifiedRegions;

   public SequenceOfIdentifiedRegion(List<IdentifiedRegion> var1) {
      this.identifiedRegions = Collections.unmodifiableList(var1);
   }

   private SequenceOfIdentifiedRegion(ASN1Sequence var1) {
      ArrayList var2 = new ArrayList();
      Iterator var3 = var1.iterator();

      while (var3.hasNext()) {
         var2.add(IdentifiedRegion.getInstance(var3.next()));
      }

      this.identifiedRegions = Collections.unmodifiableList(var2);
   }

   public static SequenceOfIdentifiedRegion getInstance(Object var0) {
      if (var0 instanceof SequenceOfIdentifiedRegion) {
         return (SequenceOfIdentifiedRegion)var0;
      } else {
         return var0 != null ? new SequenceOfIdentifiedRegion(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public List<IdentifiedRegion> getIdentifiedRegions() {
      return this.identifiedRegions;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return ItsUtils.toSequence(this.identifiedRegions);
   }
}
