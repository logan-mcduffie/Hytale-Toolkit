package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERTaggedObject;

public class EncryptionKey extends ASN1Object implements ASN1Choice {
   public static final int publicOption = 0;
   public static final int symmetric = 1;
   private final int choice;
   private final ASN1Encodable encryptionKey;

   public static EncryptionKey getInstance(Object var0) {
      if (var0 instanceof EncryptionKey) {
         return (EncryptionKey)var0;
      } else {
         return var0 != null ? new EncryptionKey(ASN1TaggedObject.getInstance(var0, 128)) : null;
      }
   }

   public EncryptionKey(int var1, ASN1Encodable var2) {
      this.choice = var1;
      switch (var1) {
         case 0:
         case 1:
            this.encryptionKey = var2;
            return;
         default:
            throw new IllegalArgumentException("invalid choice value " + var1);
      }
   }

   public static EncryptionKey publicOption(PublicEncryptionKey var0) {
      return new EncryptionKey(0, var0);
   }

   public static EncryptionKey symmetric(SymmetricEncryptionKey var0) {
      return new EncryptionKey(1, var0);
   }

   private EncryptionKey(ASN1TaggedObject var1) {
      this(var1.getTagNo(), var1.getExplicitBaseObject());
   }

   public int getChoice() {
      return this.choice;
   }

   public ASN1Encodable getEncryptionKey() {
      return this.encryptionKey;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERTaggedObject(this.choice, this.encryptionKey);
   }
}
