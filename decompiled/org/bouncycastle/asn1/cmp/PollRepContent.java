package org.bouncycastle.asn1.cmp;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class PollRepContent extends ASN1Object {
   private final ASN1Integer[] certReqId;
   private final ASN1Integer[] checkAfter;
   private final PKIFreeText[] reason;

   private PollRepContent(ASN1Sequence var1) {
      this.certReqId = new ASN1Integer[var1.size()];
      this.checkAfter = new ASN1Integer[var1.size()];
      this.reason = new PKIFreeText[var1.size()];

      for (int var2 = 0; var2 != var1.size(); var2++) {
         ASN1Sequence var3 = ASN1Sequence.getInstance(var1.getObjectAt(var2));
         this.certReqId[var2] = ASN1Integer.getInstance(var3.getObjectAt(0));
         this.checkAfter[var2] = ASN1Integer.getInstance(var3.getObjectAt(1));
         if (var3.size() > 2) {
            this.reason[var2] = PKIFreeText.getInstance(var3.getObjectAt(2));
         }
      }
   }

   public PollRepContent(ASN1Integer var1, ASN1Integer var2) {
      this(var1, var2, null);
   }

   public PollRepContent(ASN1Integer var1, ASN1Integer var2, PKIFreeText var3) {
      this.certReqId = new ASN1Integer[1];
      this.checkAfter = new ASN1Integer[1];
      this.reason = new PKIFreeText[1];
      this.certReqId[0] = var1;
      this.checkAfter[0] = var2;
      this.reason[0] = var3;
   }

   public static PollRepContent getInstance(Object var0) {
      if (var0 instanceof PollRepContent) {
         return (PollRepContent)var0;
      } else {
         return var0 != null ? new PollRepContent(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public int size() {
      return this.certReqId.length;
   }

   public ASN1Integer getCertReqId(int var1) {
      return this.certReqId[var1];
   }

   public ASN1Integer getCheckAfter(int var1) {
      return this.checkAfter[var1];
   }

   public PKIFreeText getReason(int var1) {
      return this.reason[var1];
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(this.certReqId.length);

      for (int var2 = 0; var2 != this.certReqId.length; var2++) {
         ASN1EncodableVector var3 = new ASN1EncodableVector(3);
         var3.add(this.certReqId[var2]);
         var3.add(this.checkAfter[var2]);
         if (this.reason[var2] != null) {
            var3.add(this.reason[var2]);
         }

         var1.add(new DERSequence(var3));
      }

      return new DERSequence(var1);
   }
}
