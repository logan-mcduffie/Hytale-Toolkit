package org.bouncycastle.oer.its.ieee1609dot2;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.EciesP256EncryptedKey;

public class EncryptedDataEncryptionKey extends ASN1Object implements ASN1Choice {
   public static final int eciesNistP256 = 0;
   public static final int eciesBrainpoolP256r1 = 1;
   private final int choice;
   private final ASN1Encodable encryptedDataEncryptionKey;

   public EncryptedDataEncryptionKey(int var1, ASN1Encodable var2) {
      this.choice = var1;
      this.encryptedDataEncryptionKey = var2;
   }

   private EncryptedDataEncryptionKey(ASN1TaggedObject var1) {
      this.choice = var1.getTagNo();
      switch (var1.getTagNo()) {
         case 0:
         case 1:
            this.encryptedDataEncryptionKey = EciesP256EncryptedKey.getInstance(var1.getExplicitBaseObject());
            return;
         default:
            throw new IllegalArgumentException("invalid choice value " + var1.getTagNo());
      }
   }

   public static EncryptedDataEncryptionKey getInstance(Object var0) {
      if (var0 instanceof EncryptedDataEncryptionKey) {
         return (EncryptedDataEncryptionKey)var0;
      } else {
         return var0 != null ? new EncryptedDataEncryptionKey(ASN1TaggedObject.getInstance(var0, 128)) : null;
      }
   }

   public int getChoice() {
      return this.choice;
   }

   public ASN1Encodable getEncryptedDataEncryptionKey() {
      return this.encryptedDataEncryptionKey;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERTaggedObject(this.choice, this.encryptedDataEncryptionKey);
   }

   public static EncryptedDataEncryptionKey eciesNistP256(EciesP256EncryptedKey var0) {
      return new EncryptedDataEncryptionKey(0, var0);
   }

   public static EncryptedDataEncryptionKey eciesBrainpoolP256r1(EciesP256EncryptedKey var0) {
      return new EncryptedDataEncryptionKey(1, var0);
   }
}
