package org.bouncycastle.asn1.cmc;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class ExtendedFailInfo extends ASN1Object {
   private final ASN1ObjectIdentifier failInfoOID;
   private final ASN1Encodable failInfoValue;

   public ExtendedFailInfo(ASN1ObjectIdentifier var1, ASN1Encodable var2) {
      this.failInfoOID = var1;
      this.failInfoValue = var2;
   }

   private ExtendedFailInfo(ASN1Sequence var1) {
      if (var1.size() != 2) {
         throw new IllegalArgumentException("Sequence must be 2 elements.");
      } else {
         this.failInfoOID = ASN1ObjectIdentifier.getInstance(var1.getObjectAt(0));
         this.failInfoValue = var1.getObjectAt(1);
      }
   }

   public static ExtendedFailInfo getInstance(Object var0) {
      if (var0 instanceof ExtendedFailInfo) {
         return (ExtendedFailInfo)var0;
      } else {
         if (var0 instanceof ASN1Encodable) {
            ASN1Primitive var1 = ((ASN1Encodable)var0).toASN1Primitive();
            if (var1 instanceof ASN1Sequence) {
               return new ExtendedFailInfo((ASN1Sequence)var1);
            }
         } else if (var0 instanceof byte[]) {
            return getInstance(ASN1Sequence.getInstance(var0));
         }

         return null;
      }
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERSequence(new ASN1Encodable[]{this.failInfoOID, this.failInfoValue});
   }

   public ASN1ObjectIdentifier getFailInfoOID() {
      return this.failInfoOID;
   }

   public ASN1Encodable getFailInfoValue() {
      return this.failInfoValue;
   }
}
