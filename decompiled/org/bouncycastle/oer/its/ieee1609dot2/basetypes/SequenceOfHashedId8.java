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

public class SequenceOfHashedId8 extends ASN1Object {
   private final List<HashedId8> hashedId8s;

   public SequenceOfHashedId8(List<HashedId8> var1) {
      this.hashedId8s = Collections.unmodifiableList(var1);
   }

   private SequenceOfHashedId8(ASN1Sequence var1) {
      ArrayList var2 = new ArrayList();
      Iterator var3 = var1.iterator();

      while (var3.hasNext()) {
         var2.add(HashedId8.getInstance(var3.next()));
      }

      this.hashedId8s = Collections.unmodifiableList(var2);
   }

   public static SequenceOfHashedId8.Builder builder() {
      return new SequenceOfHashedId8.Builder();
   }

   public static SequenceOfHashedId8 getInstance(Object var0) {
      if (var0 instanceof SequenceOfHashedId8) {
         return (SequenceOfHashedId8)var0;
      } else {
         return var0 != null ? new SequenceOfHashedId8(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public List<HashedId8> getHashedId8s() {
      return this.hashedId8s;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERSequence(this.hashedId8s.toArray(new ASN1Encodable[0]));
   }

   public static class Builder {
      private final List<HashedId8> items = new ArrayList<>();

      public SequenceOfHashedId8.Builder addHashId8(HashedId8... var1) {
         this.items.addAll(Arrays.asList(var1));
         return this;
      }

      public SequenceOfHashedId8 build() {
         return new SequenceOfHashedId8(this.items);
      }
   }
}
