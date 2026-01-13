package org.bouncycastle.asn1.crmf;

import org.bouncycastle.asn1.ASN1Boolean;
import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERTaggedObject;

public class PKIArchiveOptions extends ASN1Object implements ASN1Choice {
   public static final int encryptedPrivKey = 0;
   public static final int keyGenParameters = 1;
   public static final int archiveRemGenPrivKey = 2;
   private ASN1Encodable value;

   public static PKIArchiveOptions getInstance(Object var0) {
      if (var0 == null || var0 instanceof PKIArchiveOptions) {
         return (PKIArchiveOptions)var0;
      } else if (var0 instanceof ASN1TaggedObject) {
         return new PKIArchiveOptions(ASN1TaggedObject.getInstance(var0, 128));
      } else {
         throw new IllegalArgumentException("unknown object: " + var0);
      }
   }

   private PKIArchiveOptions(ASN1TaggedObject var1) {
      switch (var1.getTagNo()) {
         case 0:
            this.value = EncryptedKey.getInstance(var1.getExplicitBaseObject());
            break;
         case 1:
            this.value = ASN1OctetString.getInstance(var1, false);
            break;
         case 2:
            this.value = ASN1Boolean.getInstance(var1, false);
            break;
         default:
            throw new IllegalArgumentException("unknown tag number: " + var1.getTagNo());
      }
   }

   public PKIArchiveOptions(EncryptedKey var1) {
      this.value = var1;
   }

   public PKIArchiveOptions(ASN1OctetString var1) {
      this.value = var1;
   }

   public PKIArchiveOptions(boolean var1) {
      this.value = ASN1Boolean.getInstance(var1);
   }

   public int getType() {
      if (this.value instanceof EncryptedKey) {
         return 0;
      } else {
         return this.value instanceof ASN1OctetString ? 1 : 2;
      }
   }

   public ASN1Encodable getValue() {
      return this.value;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      if (this.value instanceof EncryptedKey) {
         return new DERTaggedObject(true, 0, this.value);
      } else {
         return this.value instanceof ASN1OctetString ? new DERTaggedObject(false, 1, this.value) : new DERTaggedObject(false, 2, this.value);
      }
   }
}
