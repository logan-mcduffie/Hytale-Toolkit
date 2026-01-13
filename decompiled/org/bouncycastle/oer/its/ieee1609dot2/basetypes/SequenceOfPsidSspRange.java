package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class SequenceOfPsidSspRange extends ASN1Object {
   private final List<PsidSspRange> psidSspRanges;

   public SequenceOfPsidSspRange(List<PsidSspRange> var1) {
      this.psidSspRanges = Collections.unmodifiableList(var1);
   }

   private SequenceOfPsidSspRange(ASN1Sequence var1) {
      ArrayList var2 = new ArrayList();
      Iterator var3 = var1.iterator();

      while (var3.hasNext()) {
         var2.add(PsidSspRange.getInstance(var3.next()));
      }

      this.psidSspRanges = Collections.unmodifiableList(var2);
   }

   public static SequenceOfPsidSspRange getInstance(Object var0) {
      if (var0 instanceof SequenceOfPsidSspRange) {
         return (SequenceOfPsidSspRange)var0;
      } else {
         return var0 != null ? new SequenceOfPsidSspRange(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public List<PsidSspRange> getPsidSspRanges() {
      return this.psidSspRanges;
   }

   public static SequenceOfPsidSspRange.Builder builder() {
      return new SequenceOfPsidSspRange.Builder();
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector();
      Iterator var2 = this.psidSspRanges.iterator();

      while (var2.hasNext()) {
         var1.add((ASN1Encodable)var2.next());
      }

      return new DERSequence(var1);
   }

   public static class Builder {
      private final ArrayList<PsidSspRange> psidSspRanges = new ArrayList<>();

      public SequenceOfPsidSspRange.Builder add(PsidSspRange... var1) {
         this.psidSspRanges.addAll(Arrays.asList(var1));
         return this;
      }

      public SequenceOfPsidSspRange build() {
         return new SequenceOfPsidSspRange(this.psidSspRanges);
      }
   }
}
