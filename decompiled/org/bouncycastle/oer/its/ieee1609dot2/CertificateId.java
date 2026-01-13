package org.bouncycastle.oer.its.ieee1609dot2;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Null;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Hostname;

public class CertificateId extends ASN1Object implements ASN1Choice {
   public static final int linkageData = 0;
   public static final int name = 1;
   public static final int binaryId = 2;
   public static final int none = 3;
   private final int choice;
   private final ASN1Encodable certificateId;

   public CertificateId(int var1, ASN1Encodable var2) {
      this.choice = var1;
      this.certificateId = var2;
   }

   private CertificateId(ASN1TaggedObject var1) {
      this.choice = var1.getTagNo();
      switch (this.choice) {
         case 0:
            this.certificateId = LinkageData.getInstance(var1.getExplicitBaseObject());
            break;
         case 1:
            this.certificateId = Hostname.getInstance(var1.getExplicitBaseObject());
            break;
         case 2:
            this.certificateId = DEROctetString.getInstance(var1.getExplicitBaseObject());
            break;
         case 3:
            this.certificateId = ASN1Null.getInstance(var1.getExplicitBaseObject());
            break;
         default:
            throw new IllegalArgumentException("invalid choice value " + this.choice);
      }
   }

   public static CertificateId linkageData(LinkageData var0) {
      return new CertificateId(0, var0);
   }

   public static CertificateId name(Hostname var0) {
      return new CertificateId(1, var0);
   }

   public static CertificateId binaryId(ASN1OctetString var0) {
      return new CertificateId(2, var0);
   }

   public static CertificateId binaryId(byte[] var0) {
      return new CertificateId(2, new DEROctetString(var0));
   }

   public static CertificateId none() {
      return new CertificateId(3, DERNull.INSTANCE);
   }

   public static CertificateId getInstance(Object var0) {
      if (var0 instanceof CertificateId) {
         return (CertificateId)var0;
      } else {
         return var0 != null ? new CertificateId(ASN1TaggedObject.getInstance(var0, 128)) : null;
      }
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERTaggedObject(this.choice, this.certificateId).toASN1Primitive();
   }

   public int getChoice() {
      return this.choice;
   }

   public ASN1Encodable getCertificateId() {
      return this.certificateId;
   }
}
