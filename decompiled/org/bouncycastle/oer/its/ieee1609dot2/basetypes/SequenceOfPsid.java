package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.oer.its.ItsUtils;

public class SequenceOfPsid extends ASN1Object {
   private final List<Psid> psids;

   public SequenceOfPsid(List<Psid> var1) {
      this.psids = Collections.unmodifiableList(var1);
   }

   private SequenceOfPsid(ASN1Sequence var1) {
      ArrayList var2 = new ArrayList();
      Iterator var3 = var1.iterator();

      while (var3.hasNext()) {
         var2.add(Psid.getInstance(var3.next()));
      }

      this.psids = Collections.unmodifiableList(var2);
   }

   public static SequenceOfPsid getInstance(Object var0) {
      if (var0 instanceof SequenceOfPsid) {
         return (SequenceOfPsid)var0;
      } else {
         return var0 != null ? new SequenceOfPsid(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public static SequenceOfPsid.Builder builder() {
      return new SequenceOfPsid.Builder();
   }

   public List<Psid> getPsids() {
      return this.psids;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return ItsUtils.toSequence(this.psids);
   }

   public static class Builder {
      private List<Psid> items = new ArrayList<>();

      public SequenceOfPsid.Builder setItems(List<Psid> var1) {
         this.items = var1;
         return this;
      }

      public SequenceOfPsid.Builder setItem(Psid... var1) {
         for (int var2 = 0; var2 != var1.length; var2++) {
            Psid var3 = var1[var2];
            this.items.add(var3);
         }

         return this;
      }

      public SequenceOfPsid createSequenceOfPsidSsp() {
         return new SequenceOfPsid(this.items);
      }
   }
}
