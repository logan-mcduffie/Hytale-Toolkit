package org.bouncycastle.asn1.crmf;

import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.cms.EnvelopedData;

public class POPOPrivKey extends ASN1Object implements ASN1Choice {
   public static final int thisMessage = 0;
   public static final int subsequentMessage = 1;
   public static final int dhMAC = 2;
   public static final int agreeMAC = 3;
   public static final int encryptedKey = 4;
   private int tagNo;
   private ASN1Encodable obj;

   private POPOPrivKey(ASN1TaggedObject var1) {
      this.tagNo = var1.getTagNo();
      switch (this.tagNo) {
         case 0:
            this.obj = ASN1BitString.getInstance(var1, false);
            break;
         case 1:
            this.obj = SubsequentMessage.valueOf(ASN1Integer.getInstance(var1, false).intValueExact());
            break;
         case 2:
            this.obj = ASN1BitString.getInstance(var1, false);
            break;
         case 3:
            this.obj = PKMACValue.getInstance(var1, false);
            break;
         case 4:
            this.obj = EnvelopedData.getInstance(var1, false);
            break;
         default:
            throw new IllegalArgumentException("unknown tag in POPOPrivKey");
      }
   }

   public static POPOPrivKey getInstance(Object var0) {
      if (var0 instanceof POPOPrivKey) {
         return (POPOPrivKey)var0;
      } else {
         return var0 != null ? new POPOPrivKey(ASN1TaggedObject.getInstance(var0)) : null;
      }
   }

   public static POPOPrivKey getInstance(ASN1TaggedObject var0, boolean var1) {
      return getInstance(ASN1TaggedObject.getInstance(var0, true));
   }

   public POPOPrivKey(PKMACValue var1) {
      this.tagNo = 3;
      this.obj = var1;
   }

   public POPOPrivKey(SubsequentMessage var1) {
      this.tagNo = 1;
      this.obj = var1;
   }

   public int getType() {
      return this.tagNo;
   }

   public ASN1Encodable getValue() {
      return this.obj;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERTaggedObject(false, this.tagNo, this.obj);
   }
}
