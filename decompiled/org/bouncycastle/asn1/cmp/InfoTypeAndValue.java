package org.bouncycastle.asn1.cmp;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class InfoTypeAndValue extends ASN1Object {
   private final ASN1ObjectIdentifier infoType;
   private final ASN1Encodable infoValue;

   private InfoTypeAndValue(ASN1Sequence var1) {
      this.infoType = ASN1ObjectIdentifier.getInstance(var1.getObjectAt(0));
      if (var1.size() > 1) {
         this.infoValue = var1.getObjectAt(1);
      } else {
         this.infoValue = null;
      }
   }

   public InfoTypeAndValue(ASN1ObjectIdentifier var1) {
      this(var1, null);
   }

   public InfoTypeAndValue(ASN1ObjectIdentifier var1, ASN1Encodable var2) {
      if (var1 == null) {
         throw new NullPointerException("'infoType' cannot be null");
      } else {
         this.infoType = var1;
         this.infoValue = var2;
      }
   }

   public static InfoTypeAndValue getInstance(Object var0) {
      if (var0 instanceof InfoTypeAndValue) {
         return (InfoTypeAndValue)var0;
      } else {
         return var0 != null ? new InfoTypeAndValue(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public ASN1ObjectIdentifier getInfoType() {
      return this.infoType;
   }

   public ASN1Encodable getInfoValue() {
      return this.infoValue;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(2);
      var1.add(this.infoType);
      if (this.infoValue != null) {
         var1.add(this.infoValue);
      }

      return new DERSequence(var1);
   }
}
