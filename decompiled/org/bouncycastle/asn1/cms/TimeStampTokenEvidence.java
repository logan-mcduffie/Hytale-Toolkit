package org.bouncycastle.asn1.cms;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;

public class TimeStampTokenEvidence extends ASN1Object {
   private TimeStampAndCRL[] timeStampAndCRLs;

   public TimeStampTokenEvidence(TimeStampAndCRL[] var1) {
      this.timeStampAndCRLs = this.copy(var1);
   }

   public TimeStampTokenEvidence(TimeStampAndCRL var1) {
      this.timeStampAndCRLs = new TimeStampAndCRL[1];
      this.timeStampAndCRLs[0] = var1;
   }

   private TimeStampTokenEvidence(ASN1Sequence var1) {
      this.timeStampAndCRLs = new TimeStampAndCRL[var1.size()];
      int var2 = 0;
      Enumeration var3 = var1.getObjects();

      while (var3.hasMoreElements()) {
         this.timeStampAndCRLs[var2++] = TimeStampAndCRL.getInstance(var3.nextElement());
      }
   }

   public static TimeStampTokenEvidence getInstance(ASN1TaggedObject var0, boolean var1) {
      return getInstance(ASN1Sequence.getInstance(var0, var1));
   }

   public static TimeStampTokenEvidence getInstance(Object var0) {
      if (var0 instanceof TimeStampTokenEvidence) {
         return (TimeStampTokenEvidence)var0;
      } else {
         return var0 != null ? new TimeStampTokenEvidence(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public TimeStampAndCRL[] toTimeStampAndCRLArray() {
      return this.copy(this.timeStampAndCRLs);
   }

   private TimeStampAndCRL[] copy(TimeStampAndCRL[] var1) {
      TimeStampAndCRL[] var2 = new TimeStampAndCRL[var1.length];
      System.arraycopy(var1, 0, var2, 0, var2.length);
      return var2;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(this.timeStampAndCRLs.length);

      for (int var2 = 0; var2 != this.timeStampAndCRLs.length; var2++) {
         var1.add(this.timeStampAndCRLs[var2]);
      }

      return new DERSequence(var1);
   }
}
