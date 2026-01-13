package org.bouncycastle.oer.its.ieee1609dot2;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERTaggedObject;

public class SymmetricCiphertext extends ASN1Object implements ASN1Choice {
   public static final int aes128ccm = 0;
   private final int choice;
   private final ASN1Encodable symmetricCiphertext;

   public SymmetricCiphertext(int var1, ASN1Encodable var2) {
      this.choice = var1;
      this.symmetricCiphertext = var2;
   }

   private SymmetricCiphertext(ASN1TaggedObject var1) {
      this.choice = var1.getTagNo();
      switch (this.choice) {
         case 0:
            this.symmetricCiphertext = AesCcmCiphertext.getInstance(var1.getExplicitBaseObject());
            return;
         default:
            throw new IllegalArgumentException("invalid choice value " + this.choice);
      }
   }

   public static SymmetricCiphertext aes128ccm(AesCcmCiphertext var0) {
      return new SymmetricCiphertext(0, var0);
   }

   public static SymmetricCiphertext getInstance(Object var0) {
      if (var0 instanceof SymmetricCiphertext) {
         return (SymmetricCiphertext)var0;
      } else {
         return var0 != null ? new SymmetricCiphertext(ASN1TaggedObject.getInstance(var0, 128)) : null;
      }
   }

   public int getChoice() {
      return this.choice;
   }

   public ASN1Encodable getSymmetricCiphertext() {
      return this.symmetricCiphertext;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERTaggedObject(this.choice, this.symmetricCiphertext);
   }
}
