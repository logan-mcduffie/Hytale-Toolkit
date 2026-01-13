package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;

public class RecipientEncryptedKey extends ASN1Object {
   private KeyAgreeRecipientIdentifier identifier;
   private ASN1OctetString encryptedKey;

   private RecipientEncryptedKey(ASN1Sequence var1) {
      this.identifier = KeyAgreeRecipientIdentifier.getInstance(var1.getObjectAt(0));
      this.encryptedKey = (ASN1OctetString)var1.getObjectAt(1);
   }

   public static RecipientEncryptedKey getInstance(ASN1TaggedObject var0, boolean var1) {
      return getInstance(ASN1Sequence.getInstance(var0, var1));
   }

   public static RecipientEncryptedKey getInstance(Object var0) {
      if (var0 instanceof RecipientEncryptedKey) {
         return (RecipientEncryptedKey)var0;
      } else {
         return var0 != null ? new RecipientEncryptedKey(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public RecipientEncryptedKey(KeyAgreeRecipientIdentifier var1, ASN1OctetString var2) {
      this.identifier = var1;
      this.encryptedKey = var2;
   }

   public KeyAgreeRecipientIdentifier getIdentifier() {
      return this.identifier;
   }

   public ASN1OctetString getEncryptedKey() {
      return this.encryptedKey;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERSequence(this.identifier, this.encryptedKey);
   }
}
