package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Util;
import org.bouncycastle.asn1.DERTaggedObject;

public class KeyAgreeRecipientIdentifier extends ASN1Object implements ASN1Choice {
   private IssuerAndSerialNumber issuerSerial;
   private RecipientKeyIdentifier rKeyID;

   public static KeyAgreeRecipientIdentifier getInstance(ASN1TaggedObject var0, boolean var1) {
      if (!var1) {
         throw new IllegalArgumentException("choice item must be explicitly tagged");
      } else {
         return getInstance(var0.getExplicitBaseObject());
      }
   }

   public static KeyAgreeRecipientIdentifier getInstance(Object var0) {
      if (var0 == null || var0 instanceof KeyAgreeRecipientIdentifier) {
         return (KeyAgreeRecipientIdentifier)var0;
      } else if (var0 instanceof ASN1TaggedObject) {
         ASN1TaggedObject var1 = (ASN1TaggedObject)var0;
         if (var1.hasContextTag(0)) {
            return new KeyAgreeRecipientIdentifier(RecipientKeyIdentifier.getInstance(var1, false));
         } else {
            throw new IllegalArgumentException("Invalid KeyAgreeRecipientIdentifier tag: " + ASN1Util.getTagText(var1));
         }
      } else {
         return new KeyAgreeRecipientIdentifier(IssuerAndSerialNumber.getInstance(var0));
      }
   }

   public KeyAgreeRecipientIdentifier(IssuerAndSerialNumber var1) {
      this.issuerSerial = var1;
      this.rKeyID = null;
   }

   public KeyAgreeRecipientIdentifier(RecipientKeyIdentifier var1) {
      this.issuerSerial = null;
      this.rKeyID = var1;
   }

   public IssuerAndSerialNumber getIssuerAndSerialNumber() {
      return this.issuerSerial;
   }

   public RecipientKeyIdentifier getRKeyID() {
      return this.rKeyID;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return (ASN1Primitive)(this.issuerSerial != null ? this.issuerSerial.toASN1Primitive() : new DERTaggedObject(false, 0, this.rKeyID));
   }
}
