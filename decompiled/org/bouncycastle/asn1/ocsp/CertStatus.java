package org.bouncycastle.asn1.ocsp;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Null;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Util;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DERTaggedObject;

public class CertStatus extends ASN1Object implements ASN1Choice {
   private int tagNo;
   private ASN1Encodable value;

   public CertStatus() {
      this.tagNo = 0;
      this.value = DERNull.INSTANCE;
   }

   public CertStatus(RevokedInfo var1) {
      this.tagNo = 1;
      this.value = var1;
   }

   public CertStatus(int var1, ASN1Encodable var2) {
      this.tagNo = var1;
      this.value = var2;
   }

   private CertStatus(ASN1TaggedObject var1) {
      int var2 = var1.getTagNo();
      switch (var2) {
         case 0:
            this.value = ASN1Null.getInstance(var1, false);
            break;
         case 1:
            this.value = RevokedInfo.getInstance(var1, false);
            break;
         case 2:
            this.value = ASN1Null.getInstance(var1, false);
            break;
         default:
            throw new IllegalArgumentException("Unknown tag encountered: " + ASN1Util.getTagText(var1));
      }

      this.tagNo = var2;
   }

   public static CertStatus getInstance(Object var0) {
      if (var0 == null || var0 instanceof CertStatus) {
         return (CertStatus)var0;
      } else if (var0 instanceof ASN1TaggedObject) {
         return new CertStatus((ASN1TaggedObject)var0);
      } else {
         throw new IllegalArgumentException("unknown object in factory: " + var0.getClass().getName());
      }
   }

   public static CertStatus getInstance(ASN1TaggedObject var0, boolean var1) {
      return getInstance(var0.getExplicitBaseTagged());
   }

   public int getTagNo() {
      return this.tagNo;
   }

   public ASN1Encodable getStatus() {
      return this.value;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERTaggedObject(false, this.tagNo, this.value);
   }
}
