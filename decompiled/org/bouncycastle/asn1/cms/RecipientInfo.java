package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERTaggedObject;

public class RecipientInfo extends ASN1Object implements ASN1Choice {
   ASN1Encodable info;

   public RecipientInfo(KeyTransRecipientInfo var1) {
      this.info = var1;
   }

   public RecipientInfo(KeyAgreeRecipientInfo var1) {
      this.info = new DERTaggedObject(false, 1, var1);
   }

   public RecipientInfo(KEKRecipientInfo var1) {
      this.info = new DERTaggedObject(false, 2, var1);
   }

   public RecipientInfo(PasswordRecipientInfo var1) {
      this.info = new DERTaggedObject(false, 3, var1);
   }

   public RecipientInfo(OtherRecipientInfo var1) {
      this.info = new DERTaggedObject(false, 4, var1);
   }

   public RecipientInfo(ASN1Primitive var1) {
      this.info = var1;
   }

   public static RecipientInfo getInstance(Object var0) {
      if (var0 == null || var0 instanceof RecipientInfo) {
         return (RecipientInfo)var0;
      } else if (var0 instanceof ASN1Sequence) {
         return new RecipientInfo((ASN1Sequence)var0);
      } else if (var0 instanceof ASN1TaggedObject) {
         return new RecipientInfo((ASN1TaggedObject)var0);
      } else {
         throw new IllegalArgumentException("unknown object in factory: " + var0.getClass().getName());
      }
   }

   /** @deprecated */
   public ASN1Integer getVersion() {
      if (!(this.info instanceof ASN1TaggedObject)) {
         return KeyTransRecipientInfo.getInstance(this.info).getVersion();
      } else {
         ASN1TaggedObject var1 = (ASN1TaggedObject)this.info;
         if (var1.hasContextTag()) {
            switch (var1.getTagNo()) {
               case 1:
                  return KeyAgreeRecipientInfo.getInstance(var1, false).getVersion();
               case 2:
                  return this.getKEKInfo(var1).getVersion();
               case 3:
                  return PasswordRecipientInfo.getInstance(var1, false).getVersion();
               case 4:
                  return new ASN1Integer(0L);
            }
         }

         throw new IllegalStateException("unknown tag");
      }
   }

   public boolean isTagged() {
      return this.info instanceof ASN1TaggedObject;
   }

   public ASN1Encodable getInfo() {
      if (!(this.info instanceof ASN1TaggedObject)) {
         return KeyTransRecipientInfo.getInstance(this.info);
      } else {
         ASN1TaggedObject var1 = (ASN1TaggedObject)this.info;
         if (var1.hasContextTag()) {
            switch (var1.getTagNo()) {
               case 1:
                  return KeyAgreeRecipientInfo.getInstance(var1, false);
               case 2:
                  return this.getKEKInfo(var1);
               case 3:
                  return PasswordRecipientInfo.getInstance(var1, false);
               case 4:
                  return OtherRecipientInfo.getInstance(var1, false);
            }
         }

         throw new IllegalStateException("unknown tag");
      }
   }

   private KEKRecipientInfo getKEKInfo(ASN1TaggedObject var1) {
      boolean var2 = var1.isExplicit();
      return KEKRecipientInfo.getInstance(var1, var2);
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return this.info.toASN1Primitive();
   }

   boolean isKeyTransV0() {
      if (this.info instanceof ASN1TaggedObject) {
         return false;
      } else {
         KeyTransRecipientInfo var1 = KeyTransRecipientInfo.getInstance(this.info);
         return var1.getVersion().hasValue(0);
      }
   }

   boolean isPasswordOrOther() {
      if (this.info instanceof ASN1TaggedObject) {
         ASN1TaggedObject var1 = (ASN1TaggedObject)this.info;
         if (var1.hasContextTag()) {
            switch (var1.getTagNo()) {
               case 3:
               case 4:
                  return true;
            }
         }
      }

      return false;
   }
}
