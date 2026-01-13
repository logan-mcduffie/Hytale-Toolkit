package org.bouncycastle.oer.its.etsi102941;

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

public class SequenceOfCrlEntry extends ASN1Object {
   private final List<CrlEntry> crlEntries;

   public SequenceOfCrlEntry(List<CrlEntry> var1) {
      this.crlEntries = Collections.unmodifiableList(var1);
   }

   private SequenceOfCrlEntry(ASN1Sequence var1) {
      ArrayList var2 = new ArrayList();
      Iterator var3 = var1.iterator();

      while (var3.hasNext()) {
         var2.add(CrlEntry.getInstance(var3.next()));
      }

      this.crlEntries = Collections.unmodifiableList(var2);
   }

   public static SequenceOfCrlEntry.Builder builder() {
      return new SequenceOfCrlEntry.Builder();
   }

   public static SequenceOfCrlEntry getInstance(Object var0) {
      if (var0 instanceof SequenceOfCrlEntry) {
         return (SequenceOfCrlEntry)var0;
      } else {
         return var0 != null ? new SequenceOfCrlEntry(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public List<CrlEntry> getCrlEntries() {
      return this.crlEntries;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERSequence(this.crlEntries.toArray(new ASN1Encodable[0]));
   }

   public static class Builder {
      private final List<CrlEntry> items = new ArrayList<>();

      public SequenceOfCrlEntry.Builder addCrlEntry(CrlEntry... var1) {
         this.items.addAll(Arrays.asList(var1));
         return this;
      }

      public SequenceOfCrlEntry build() {
         return new SequenceOfCrlEntry(this.items);
      }
   }
}
