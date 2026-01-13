package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class SequenceOfUint8 extends ASN1Object {
   private final List<UINT8> uint8s;

   public SequenceOfUint8(List<UINT8> var1) {
      this.uint8s = Collections.unmodifiableList(var1);
   }

   private SequenceOfUint8(ASN1Sequence var1) {
      ArrayList var2 = new ArrayList();
      Iterator var3 = var1.iterator();

      while (var3.hasNext()) {
         var2.add(UINT8.getInstance(var3.next()));
      }

      this.uint8s = Collections.unmodifiableList(var2);
   }

   public static SequenceOfUint8.Builder builder() {
      return new SequenceOfUint8.Builder();
   }

   public static SequenceOfUint8 getInstance(Object var0) {
      if (var0 instanceof SequenceOfUint8) {
         return (SequenceOfUint8)var0;
      } else {
         return var0 != null ? new SequenceOfUint8(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public List<UINT8> getUint8s() {
      return this.uint8s;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector();

      for (UINT8 var3 : this.uint8s) {
         var1.add(var3.toASN1Primitive());
      }

      return new DERSequence(var1);
   }

   public static class Builder {
      private final List<UINT8> items = new ArrayList<>();

      public SequenceOfUint8.Builder addHashId3(UINT8... var1) {
         this.items.addAll(Arrays.asList(var1));
         return this;
      }

      public SequenceOfUint8 build() {
         return new SequenceOfUint8(this.items);
      }
   }
}
