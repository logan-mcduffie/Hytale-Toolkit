package org.bouncycastle.oer.its.ieee1609dot2;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERTaggedObject;

public class RecipientInfo extends ASN1Object implements ASN1Choice {
   public static final int pskRecipInfo = 0;
   public static final int symmRecipInfo = 1;
   public static final int certRecipInfo = 2;
   public static final int signedDataRecipInfo = 3;
   public static final int rekRecipInfo = 4;
   private final int choice;
   private final ASN1Encodable recipientInfo;

   public RecipientInfo(int var1, ASN1Encodable var2) {
      this.choice = var1;
      this.recipientInfo = var2;
   }

   private RecipientInfo(ASN1TaggedObject var1) {
      this.choice = var1.getTagNo();
      switch (this.choice) {
         case 0:
            this.recipientInfo = PreSharedKeyRecipientInfo.getInstance(var1.getExplicitBaseObject());
            break;
         case 1:
            this.recipientInfo = SymmRecipientInfo.getInstance(var1.getExplicitBaseObject());
            break;
         case 2:
         case 3:
         case 4:
            this.recipientInfo = PKRecipientInfo.getInstance(var1.getExplicitBaseObject());
            break;
         default:
            throw new IllegalArgumentException("invalid choice value " + this.choice);
      }
   }

   public static RecipientInfo getInstance(Object var0) {
      if (var0 instanceof RecipientInfo) {
         return (RecipientInfo)var0;
      } else {
         return var0 != null ? new RecipientInfo(ASN1TaggedObject.getInstance(var0, 128)) : null;
      }
   }

   public int getChoice() {
      return this.choice;
   }

   public ASN1Encodable getRecipientInfo() {
      return this.recipientInfo;
   }

   public static RecipientInfo pskRecipInfo(PreSharedKeyRecipientInfo var0) {
      return new RecipientInfo(0, var0);
   }

   public static RecipientInfo symmRecipInfo(SymmRecipientInfo var0) {
      return new RecipientInfo(1, var0);
   }

   public static RecipientInfo certRecipInfo(PKRecipientInfo var0) {
      return new RecipientInfo(2, var0);
   }

   public static RecipientInfo signedDataRecipInfo(PKRecipientInfo var0) {
      return new RecipientInfo(3, var0);
   }

   public static RecipientInfo rekRecipInfo(PKRecipientInfo var0) {
      return new RecipientInfo(4, var0);
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERTaggedObject(this.choice, this.recipientInfo);
   }
}
