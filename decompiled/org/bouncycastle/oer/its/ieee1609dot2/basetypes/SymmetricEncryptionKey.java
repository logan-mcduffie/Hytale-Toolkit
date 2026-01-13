package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERTaggedObject;

public class SymmetricEncryptionKey extends ASN1Object implements ASN1Choice {
   public static final int aes128ccm = 0;
   private final int choice;
   private final ASN1Encodable symmetricEncryptionKey;

   public SymmetricEncryptionKey(int var1, ASN1Encodable var2) {
      this.choice = var1;
      this.symmetricEncryptionKey = var2;
   }

   private SymmetricEncryptionKey(ASN1TaggedObject var1) {
      this.choice = var1.getTagNo();
      if (this.choice == 0) {
         ASN1OctetString var2 = DEROctetString.getInstance(var1.getExplicitBaseObject());
         if (var2.getOctets().length != 16) {
            throw new IllegalArgumentException("aes128ccm string not 16 bytes");
         } else {
            this.symmetricEncryptionKey = var2;
         }
      } else {
         throw new IllegalArgumentException("invalid choice value " + this.choice);
      }
   }

   public static SymmetricEncryptionKey getInstance(Object var0) {
      if (var0 instanceof SymmetricEncryptionKey) {
         return (SymmetricEncryptionKey)var0;
      } else {
         return var0 != null ? new SymmetricEncryptionKey(ASN1TaggedObject.getInstance(var0, 128)) : null;
      }
   }

   public static SymmetricEncryptionKey aes128ccm(byte[] var0) {
      return new SymmetricEncryptionKey(0, new DEROctetString(var0));
   }

   public static SymmetricEncryptionKey aes128ccm(ASN1OctetString var0) {
      return new SymmetricEncryptionKey(0, var0);
   }

   public int getChoice() {
      return this.choice;
   }

   public ASN1Encodable getSymmetricEncryptionKey() {
      return this.symmetricEncryptionKey;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERTaggedObject(this.choice, this.symmetricEncryptionKey);
   }
}
