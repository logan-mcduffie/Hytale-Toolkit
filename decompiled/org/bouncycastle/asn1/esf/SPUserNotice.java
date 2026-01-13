package org.bouncycastle.asn1.esf;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1String;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.DisplayText;
import org.bouncycastle.asn1.x509.NoticeReference;

public class SPUserNotice extends ASN1Object {
   private NoticeReference noticeRef;
   private DisplayText explicitText;

   public static SPUserNotice getInstance(Object var0) {
      if (var0 instanceof SPUserNotice) {
         return (SPUserNotice)var0;
      } else {
         return var0 != null ? new SPUserNotice(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   private SPUserNotice(ASN1Sequence var1) {
      Enumeration var2 = var1.getObjects();

      while (var2.hasMoreElements()) {
         ASN1Encodable var3 = (ASN1Encodable)var2.nextElement();
         if (!(var3 instanceof DisplayText) && !(var3 instanceof ASN1String)) {
            if (!(var3 instanceof NoticeReference) && !(var3 instanceof ASN1Sequence)) {
               throw new IllegalArgumentException("Invalid element in 'SPUserNotice': " + var3.getClass().getName());
            }

            this.noticeRef = NoticeReference.getInstance(var3);
         } else {
            this.explicitText = DisplayText.getInstance(var3);
         }
      }
   }

   public SPUserNotice(NoticeReference var1, DisplayText var2) {
      this.noticeRef = var1;
      this.explicitText = var2;
   }

   public NoticeReference getNoticeRef() {
      return this.noticeRef;
   }

   public DisplayText getExplicitText() {
      return this.explicitText;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(2);
      if (this.noticeRef != null) {
         var1.add(this.noticeRef);
      }

      if (this.explicitText != null) {
         var1.add(this.explicitText);
      }

      return new DERSequence(var1);
   }
}
