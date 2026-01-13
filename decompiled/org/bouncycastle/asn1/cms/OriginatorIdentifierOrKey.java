package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;

public class OriginatorIdentifierOrKey extends ASN1Object implements ASN1Choice {
   private ASN1Encodable id;

   public OriginatorIdentifierOrKey(IssuerAndSerialNumber var1) {
      this.id = var1;
   }

   /** @deprecated */
   public OriginatorIdentifierOrKey(ASN1OctetString var1) {
      this(new SubjectKeyIdentifier(var1.getOctets()));
   }

   public OriginatorIdentifierOrKey(SubjectKeyIdentifier var1) {
      this.id = new DERTaggedObject(false, 0, var1);
   }

   public OriginatorIdentifierOrKey(OriginatorPublicKey var1) {
      this.id = new DERTaggedObject(false, 1, var1);
   }

   /** @deprecated */
   public OriginatorIdentifierOrKey(ASN1Primitive var1) {
      this.id = var1;
   }

   public static OriginatorIdentifierOrKey getInstance(ASN1TaggedObject var0, boolean var1) {
      if (!var1) {
         throw new IllegalArgumentException("Can't implicitly tag OriginatorIdentifierOrKey");
      } else {
         return getInstance(var0.getExplicitBaseObject());
      }
   }

   public static OriginatorIdentifierOrKey getInstance(Object var0) {
      if (var0 == null || var0 instanceof OriginatorIdentifierOrKey) {
         return (OriginatorIdentifierOrKey)var0;
      } else if (var0 instanceof IssuerAndSerialNumber) {
         return new OriginatorIdentifierOrKey((IssuerAndSerialNumber)var0);
      } else if (var0 instanceof ASN1Sequence) {
         return new OriginatorIdentifierOrKey(IssuerAndSerialNumber.getInstance(var0));
      } else {
         if (var0 instanceof ASN1TaggedObject) {
            ASN1TaggedObject var1 = (ASN1TaggedObject)var0;
            if (var1.hasContextTag(0)) {
               return new OriginatorIdentifierOrKey(SubjectKeyIdentifier.getInstance(var1, false));
            }

            if (var1.hasContextTag(1)) {
               return new OriginatorIdentifierOrKey(OriginatorPublicKey.getInstance(var1, false));
            }
         }

         throw new IllegalArgumentException("Invalid OriginatorIdentifierOrKey: " + var0.getClass().getName());
      }
   }

   public ASN1Encodable getId() {
      return this.id;
   }

   public IssuerAndSerialNumber getIssuerAndSerialNumber() {
      return this.id instanceof IssuerAndSerialNumber ? (IssuerAndSerialNumber)this.id : null;
   }

   public SubjectKeyIdentifier getSubjectKeyIdentifier() {
      if (this.id instanceof ASN1TaggedObject) {
         ASN1TaggedObject var1 = (ASN1TaggedObject)this.id;
         if (var1.hasContextTag(0)) {
            return SubjectKeyIdentifier.getInstance(var1, false);
         }
      }

      return null;
   }

   public OriginatorPublicKey getOriginatorKey() {
      if (this.id instanceof ASN1TaggedObject) {
         ASN1TaggedObject var1 = (ASN1TaggedObject)this.id;
         if (var1.hasContextTag(1)) {
            return OriginatorPublicKey.getInstance(var1, false);
         }
      }

      return null;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return this.id.toASN1Primitive();
   }
}
