package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERTaggedObject;

public class SignerIdentifier extends ASN1Object implements ASN1Choice {
   private ASN1Encodable id;

   public SignerIdentifier(IssuerAndSerialNumber var1) {
      this.id = var1;
   }

   public SignerIdentifier(ASN1OctetString var1) {
      this.id = new DERTaggedObject(false, 0, var1);
   }

   public SignerIdentifier(ASN1Primitive var1) {
      this.id = var1;
   }

   public static SignerIdentifier getInstance(Object var0) {
      if (var0 == null || var0 instanceof SignerIdentifier) {
         return (SignerIdentifier)var0;
      } else if (var0 instanceof IssuerAndSerialNumber) {
         return new SignerIdentifier((IssuerAndSerialNumber)var0);
      } else if (var0 instanceof ASN1OctetString) {
         return new SignerIdentifier((ASN1OctetString)var0);
      } else if (var0 instanceof ASN1Primitive) {
         return new SignerIdentifier((ASN1Primitive)var0);
      } else {
         throw new IllegalArgumentException("Illegal object in SignerIdentifier: " + var0.getClass().getName());
      }
   }

   public boolean isTagged() {
      return this.id instanceof ASN1TaggedObject;
   }

   public ASN1Encodable getId() {
      return (ASN1Encodable)(this.id instanceof ASN1TaggedObject ? ASN1OctetString.getInstance((ASN1TaggedObject)this.id, false) : this.id);
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return this.id.toASN1Primitive();
   }
}
