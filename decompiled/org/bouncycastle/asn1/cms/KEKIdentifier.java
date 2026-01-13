package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;

public class KEKIdentifier extends ASN1Object {
   private ASN1OctetString keyIdentifier;
   private ASN1GeneralizedTime date;
   private OtherKeyAttribute other;

   public KEKIdentifier(byte[] var1, ASN1GeneralizedTime var2, OtherKeyAttribute var3) {
      this.keyIdentifier = new DEROctetString(var1);
      this.date = var2;
      this.other = var3;
   }

   private KEKIdentifier(ASN1Sequence var1) {
      this.keyIdentifier = (ASN1OctetString)var1.getObjectAt(0);
      switch (var1.size()) {
         case 1:
            break;
         case 2:
            if (var1.getObjectAt(1) instanceof ASN1GeneralizedTime) {
               this.date = (ASN1GeneralizedTime)var1.getObjectAt(1);
            } else {
               this.other = OtherKeyAttribute.getInstance(var1.getObjectAt(1));
            }
            break;
         case 3:
            this.date = (ASN1GeneralizedTime)var1.getObjectAt(1);
            this.other = OtherKeyAttribute.getInstance(var1.getObjectAt(2));
            break;
         default:
            throw new IllegalArgumentException("Invalid KEKIdentifier");
      }
   }

   public static KEKIdentifier getInstance(ASN1TaggedObject var0, boolean var1) {
      return getInstance(ASN1Sequence.getInstance(var0, var1));
   }

   public static KEKIdentifier getInstance(Object var0) {
      if (var0 == null || var0 instanceof KEKIdentifier) {
         return (KEKIdentifier)var0;
      } else if (var0 instanceof ASN1Sequence) {
         return new KEKIdentifier((ASN1Sequence)var0);
      } else {
         throw new IllegalArgumentException("Invalid KEKIdentifier: " + var0.getClass().getName());
      }
   }

   public ASN1OctetString getKeyIdentifier() {
      return this.keyIdentifier;
   }

   public ASN1GeneralizedTime getDate() {
      return this.date;
   }

   public OtherKeyAttribute getOther() {
      return this.other;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(3);
      var1.add(this.keyIdentifier);
      if (this.date != null) {
         var1.add(this.date);
      }

      if (this.other != null) {
         var1.add(this.other);
      }

      return new DERSequence(var1);
   }
}
