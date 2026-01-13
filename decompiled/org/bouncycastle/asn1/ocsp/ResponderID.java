package org.bouncycastle.asn1.ocsp;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x500.X500Name;

public class ResponderID extends ASN1Object implements ASN1Choice {
   private ASN1Encodable value;

   public ResponderID(ASN1OctetString var1) {
      this.value = var1;
   }

   public ResponderID(X500Name var1) {
      this.value = var1;
   }

   public static ResponderID getInstance(Object var0) {
      if (var0 instanceof ResponderID) {
         return (ResponderID)var0;
      } else if (var0 instanceof DEROctetString) {
         return new ResponderID((DEROctetString)var0);
      } else if (var0 instanceof ASN1TaggedObject) {
         ASN1TaggedObject var1 = (ASN1TaggedObject)var0;
         return var1.getTagNo() == 1 ? new ResponderID(X500Name.getInstance(var1, true)) : new ResponderID(ASN1OctetString.getInstance(var1, true));
      } else {
         return new ResponderID(X500Name.getInstance(var0));
      }
   }

   public static ResponderID getInstance(ASN1TaggedObject var0, boolean var1) {
      if (!var1) {
         throw new IllegalArgumentException("choice item must be explicitly tagged");
      } else {
         return getInstance(var0.getExplicitBaseObject());
      }
   }

   public byte[] getKeyHash() {
      if (this.value instanceof ASN1OctetString) {
         ASN1OctetString var1 = (ASN1OctetString)this.value;
         return var1.getOctets();
      } else {
         return null;
      }
   }

   public X500Name getName() {
      return this.value instanceof ASN1OctetString ? null : X500Name.getInstance(this.value);
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return this.value instanceof ASN1OctetString ? new DERTaggedObject(true, 2, this.value) : new DERTaggedObject(true, 1, this.value);
   }
}
