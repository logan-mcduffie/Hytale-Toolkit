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

public class RecipientKeyIdentifier extends ASN1Object {
   private ASN1OctetString subjectKeyIdentifier;
   private ASN1GeneralizedTime date;
   private OtherKeyAttribute other;

   public RecipientKeyIdentifier(ASN1OctetString var1, ASN1GeneralizedTime var2, OtherKeyAttribute var3) {
      this.subjectKeyIdentifier = var1;
      this.date = var2;
      this.other = var3;
   }

   public RecipientKeyIdentifier(byte[] var1, ASN1GeneralizedTime var2, OtherKeyAttribute var3) {
      this.subjectKeyIdentifier = new DEROctetString(var1);
      this.date = var2;
      this.other = var3;
   }

   public RecipientKeyIdentifier(byte[] var1) {
      this(var1, null, null);
   }

   private RecipientKeyIdentifier(ASN1Sequence var1) {
      this.subjectKeyIdentifier = ASN1OctetString.getInstance(var1.getObjectAt(0));
      switch (var1.size()) {
         case 1:
            break;
         case 2:
            if (var1.getObjectAt(1) instanceof ASN1GeneralizedTime) {
               this.date = ASN1GeneralizedTime.getInstance(var1.getObjectAt(1));
            } else {
               this.other = OtherKeyAttribute.getInstance(var1.getObjectAt(2));
            }
            break;
         case 3:
            this.date = ASN1GeneralizedTime.getInstance(var1.getObjectAt(1));
            this.other = OtherKeyAttribute.getInstance(var1.getObjectAt(2));
            break;
         default:
            throw new IllegalArgumentException("Invalid RecipientKeyIdentifier");
      }
   }

   public static RecipientKeyIdentifier getInstance(ASN1TaggedObject var0, boolean var1) {
      return getInstance(ASN1Sequence.getInstance(var0, var1));
   }

   public static RecipientKeyIdentifier getInstance(Object var0) {
      if (var0 instanceof RecipientKeyIdentifier) {
         return (RecipientKeyIdentifier)var0;
      } else {
         return var0 != null ? new RecipientKeyIdentifier(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public ASN1OctetString getSubjectKeyIdentifier() {
      return this.subjectKeyIdentifier;
   }

   public ASN1GeneralizedTime getDate() {
      return this.date;
   }

   public OtherKeyAttribute getOtherKeyAttribute() {
      return this.other;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(3);
      var1.add(this.subjectKeyIdentifier);
      if (this.date != null) {
         var1.add(this.date);
      }

      if (this.other != null) {
         var1.add(this.other);
      }

      return new DERSequence(var1);
   }
}
