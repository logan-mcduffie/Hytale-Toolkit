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

public class SequenceOfHashedId3 extends ASN1Object {
   private final List<HashedId3> hashedId3s;

   public SequenceOfHashedId3(List<HashedId3> var1) {
      this.hashedId3s = Collections.unmodifiableList(var1);
   }

   private SequenceOfHashedId3(ASN1Sequence var1) {
      ArrayList var2 = new ArrayList();
      Iterator var3 = var1.iterator();

      while (var3.hasNext()) {
         var2.add(HashedId3.getInstance(var3.next()));
      }

      this.hashedId3s = Collections.unmodifiableList(var2);
   }

   public static SequenceOfHashedId3.Builder builder() {
      return new SequenceOfHashedId3.Builder();
   }

   public static SequenceOfHashedId3 getInstance(Object var0) {
      if (var0 instanceof SequenceOfHashedId3) {
         return (SequenceOfHashedId3)var0;
      } else {
         return var0 != null ? new SequenceOfHashedId3(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public List<HashedId3> getHashedId3s() {
      return this.hashedId3s;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERSequence(this.hashedId3s.toArray(new ASN1Encodable[0]));
   }

   public static class Builder {
      private final List<HashedId3> items = new ArrayList<>();

      public SequenceOfHashedId3.Builder addHashId3(HashedId3... var1) {
         this.items.addAll(Arrays.asList(var1));
         return this;
      }

      public SequenceOfHashedId3 build() {
         return new SequenceOfHashedId3(this.items);
      }
   }
}
