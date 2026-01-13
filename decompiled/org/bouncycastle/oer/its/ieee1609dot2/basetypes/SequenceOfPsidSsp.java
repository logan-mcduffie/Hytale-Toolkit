package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.oer.its.ItsUtils;

public class SequenceOfPsidSsp extends ASN1Object {
   private final List<PsidSsp> psidSsps;

   public SequenceOfPsidSsp(List<PsidSsp> var1) {
      this.psidSsps = Collections.unmodifiableList(var1);
   }

   private SequenceOfPsidSsp(ASN1Sequence var1) {
      ArrayList var2 = new ArrayList();
      Iterator var3 = var1.iterator();

      while (var3.hasNext()) {
         var2.add(PsidSsp.getInstance(var3.next()));
      }

      this.psidSsps = Collections.unmodifiableList(var2);
   }

   public static SequenceOfPsidSsp getInstance(Object var0) {
      if (var0 instanceof SequenceOfPsidSsp) {
         return (SequenceOfPsidSsp)var0;
      } else {
         return var0 != null ? new SequenceOfPsidSsp(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public static SequenceOfPsidSsp.Builder builder() {
      return new SequenceOfPsidSsp.Builder();
   }

   public List<PsidSsp> getPsidSsps() {
      return this.psidSsps;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return ItsUtils.toSequence(this.psidSsps);
   }

   public static class Builder {
      private List<PsidSsp> items = new ArrayList<>();

      public SequenceOfPsidSsp.Builder setItems(List<PsidSsp> var1) {
         this.items = var1;
         return this;
      }

      public SequenceOfPsidSsp.Builder setItem(PsidSsp... var1) {
         for (int var2 = 0; var2 != var1.length; var2++) {
            PsidSsp var3 = var1[var2];
            this.items.add(var3);
         }

         return this;
      }

      public SequenceOfPsidSsp createSequenceOfPsidSsp() {
         return new SequenceOfPsidSsp(this.items);
      }
   }
}
