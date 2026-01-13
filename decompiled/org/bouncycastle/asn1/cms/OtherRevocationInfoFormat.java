package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;

public class OtherRevocationInfoFormat extends ASN1Object {
   private ASN1ObjectIdentifier otherRevInfoFormat;
   private ASN1Encodable otherRevInfo;

   public OtherRevocationInfoFormat(ASN1ObjectIdentifier var1, ASN1Encodable var2) {
      this.otherRevInfoFormat = var1;
      this.otherRevInfo = var2;
   }

   private OtherRevocationInfoFormat(ASN1Sequence var1) {
      this.otherRevInfoFormat = ASN1ObjectIdentifier.getInstance(var1.getObjectAt(0));
      this.otherRevInfo = var1.getObjectAt(1);
   }

   public static OtherRevocationInfoFormat getInstance(ASN1TaggedObject var0, boolean var1) {
      return getInstance(ASN1Sequence.getInstance(var0, var1));
   }

   public static OtherRevocationInfoFormat getInstance(Object var0) {
      if (var0 instanceof OtherRevocationInfoFormat) {
         return (OtherRevocationInfoFormat)var0;
      } else {
         return var0 != null ? new OtherRevocationInfoFormat(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public ASN1ObjectIdentifier getInfoFormat() {
      return this.otherRevInfoFormat;
   }

   public ASN1Encodable getInfo() {
      return this.otherRevInfo;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERSequence(this.otherRevInfoFormat, this.otherRevInfo);
   }
}
