package org.bouncycastle.asn1.pkcs;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DLSequence;
import org.bouncycastle.asn1.DLTaggedObject;

public class SafeBag extends ASN1Object {
   private ASN1ObjectIdentifier bagId;
   private ASN1Encodable bagValue;
   private ASN1Set bagAttributes;

   public SafeBag(ASN1ObjectIdentifier var1, ASN1Encodable var2) {
      this.bagId = var1;
      this.bagValue = var2;
      this.bagAttributes = null;
   }

   public SafeBag(ASN1ObjectIdentifier var1, ASN1Encodable var2, ASN1Set var3) {
      this.bagId = var1;
      this.bagValue = var2;
      this.bagAttributes = var3;
   }

   public static SafeBag getInstance(Object var0) {
      if (var0 instanceof SafeBag) {
         return (SafeBag)var0;
      } else {
         return var0 != null ? new SafeBag(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   private SafeBag(ASN1Sequence var1) {
      this.bagId = (ASN1ObjectIdentifier)var1.getObjectAt(0);
      this.bagValue = ((ASN1TaggedObject)var1.getObjectAt(1)).getExplicitBaseObject();
      if (var1.size() == 3) {
         this.bagAttributes = (ASN1Set)var1.getObjectAt(2);
      }
   }

   public ASN1ObjectIdentifier getBagId() {
      return this.bagId;
   }

   public ASN1Encodable getBagValue() {
      return this.bagValue;
   }

   public ASN1Set getBagAttributes() {
      return this.bagAttributes;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(3);
      var1.add(this.bagId);
      var1.add(new DLTaggedObject(true, 0, this.bagValue));
      if (this.bagAttributes != null) {
         var1.add(this.bagAttributes);
      }

      return new DLSequence(var1);
   }
}
