package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;

public class OtherName extends ASN1Object {
   private final ASN1ObjectIdentifier typeID;
   private final ASN1Encodable value;

   public static OtherName getInstance(Object var0) {
      if (var0 instanceof OtherName) {
         return (OtherName)var0;
      } else {
         return var0 != null ? new OtherName(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public OtherName(ASN1ObjectIdentifier var1, ASN1Encodable var2) {
      this.typeID = var1;
      this.value = var2;
   }

   private OtherName(ASN1Sequence var1) {
      this.typeID = ASN1ObjectIdentifier.getInstance(var1.getObjectAt(0));
      this.value = ASN1TaggedObject.getInstance(var1.getObjectAt(1)).getExplicitBaseObject();
   }

   public ASN1ObjectIdentifier getTypeID() {
      return this.typeID;
   }

   public ASN1Encodable getValue() {
      return this.value;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERSequence(this.typeID, new DERTaggedObject(true, 0, this.value));
   }
}
