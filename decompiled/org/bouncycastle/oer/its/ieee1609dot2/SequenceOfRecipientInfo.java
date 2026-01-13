package org.bouncycastle.oer.its.ieee1609dot2;

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

public class SequenceOfRecipientInfo extends ASN1Object {
   private final List<RecipientInfo> recipientInfos;

   public SequenceOfRecipientInfo(List<RecipientInfo> var1) {
      this.recipientInfos = Collections.unmodifiableList(var1);
   }

   private SequenceOfRecipientInfo(ASN1Sequence var1) {
      ArrayList var2 = new ArrayList();
      Iterator var3 = var1.iterator();

      while (var3.hasNext()) {
         var2.add(RecipientInfo.getInstance(var3.next()));
      }

      this.recipientInfos = Collections.unmodifiableList(var2);
   }

   public static SequenceOfRecipientInfo getInstance(Object var0) {
      if (var0 instanceof SequenceOfRecipientInfo) {
         return (SequenceOfRecipientInfo)var0;
      } else {
         return var0 != null ? new SequenceOfRecipientInfo(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector();

      for (RecipientInfo var3 : this.recipientInfos) {
         var1.add(var3);
      }

      return new DERSequence(var1);
   }

   public List<RecipientInfo> getRecipientInfos() {
      return this.recipientInfos;
   }

   public static SequenceOfRecipientInfo.Builder builder() {
      return new SequenceOfRecipientInfo.Builder();
   }

   public static class Builder {
      private List<RecipientInfo> recipientInfos;

      public SequenceOfRecipientInfo.Builder setRecipientInfos(List<RecipientInfo> var1) {
         this.recipientInfos = var1;
         return this;
      }

      public SequenceOfRecipientInfo.Builder addRecipients(RecipientInfo... var1) {
         if (this.recipientInfos == null) {
            this.recipientInfos = new ArrayList<>();
         }

         this.recipientInfos.addAll(Arrays.asList(var1));
         return this;
      }

      public SequenceOfRecipientInfo createSequenceOfRecipientInfo() {
         return new SequenceOfRecipientInfo(this.recipientInfos);
      }
   }
}
