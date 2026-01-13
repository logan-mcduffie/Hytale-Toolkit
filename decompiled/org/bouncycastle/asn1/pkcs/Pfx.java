package org.bouncycastle.asn1.pkcs;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.BERSequence;

public class Pfx extends ASN1Object implements PKCSObjectIdentifiers {
   private ContentInfo contentInfo;
   private MacData macData = null;

   private Pfx(ASN1Sequence var1) {
      ASN1Integer var2 = ASN1Integer.getInstance(var1.getObjectAt(0));
      if (!var2.hasValue(3)) {
         throw new IllegalArgumentException("wrong version for PFX PDU");
      } else {
         this.contentInfo = ContentInfo.getInstance(var1.getObjectAt(1));
         if (var1.size() == 3) {
            this.macData = MacData.getInstance(var1.getObjectAt(2));
         }
      }
   }

   public static Pfx getInstance(Object var0) {
      if (var0 instanceof Pfx) {
         return (Pfx)var0;
      } else {
         return var0 != null ? new Pfx(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public Pfx(ContentInfo var1, MacData var2) {
      this.contentInfo = var1;
      this.macData = var2;
   }

   public ContentInfo getAuthSafe() {
      return this.contentInfo;
   }

   public MacData getMacData() {
      return this.macData;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(3);
      var1.add(new ASN1Integer(3L));
      var1.add(this.contentInfo);
      if (this.macData != null) {
         var1.add(this.macData);
      }

      return new BERSequence(var1);
   }
}
