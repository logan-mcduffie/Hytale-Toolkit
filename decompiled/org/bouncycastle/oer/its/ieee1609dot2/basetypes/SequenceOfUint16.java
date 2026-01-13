package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class SequenceOfUint16 extends ASN1Object {
   private final List<UINT16> uint16s;

   public SequenceOfUint16(List<UINT16> var1) {
      this.uint16s = Collections.unmodifiableList(var1);
   }

   private SequenceOfUint16(ASN1Sequence var1) {
      ArrayList var2 = new ArrayList();
      Iterator var3 = var1.iterator();

      while (var3.hasNext()) {
         var2.add(UINT16.getInstance(var3.next()));
      }

      this.uint16s = Collections.unmodifiableList(var2);
   }

   public static SequenceOfUint16.Builder builder() {
      return new SequenceOfUint16.Builder();
   }

   public static SequenceOfUint16 getInstance(Object var0) {
      if (var0 instanceof SequenceOfUint16) {
         return (SequenceOfUint16)var0;
      } else {
         return var0 != null ? new SequenceOfUint16(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public List<UINT16> getUint16s() {
      return this.uint16s;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERSequence(this.uint16s.toArray(new ASN1Encodable[0]));
   }

   public static class Builder {
      private final List<UINT16> items = new ArrayList<>();

      public SequenceOfUint16.Builder addHashId3(UINT16... var1) {
         this.items.addAll(Arrays.asList(var1));
         return this;
      }

      public SequenceOfUint16 build() {
         return new SequenceOfUint16(this.items);
      }
   }
}
