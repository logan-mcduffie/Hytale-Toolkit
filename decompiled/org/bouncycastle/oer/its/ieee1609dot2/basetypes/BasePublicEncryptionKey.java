package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERTaggedObject;

public class BasePublicEncryptionKey extends ASN1Object implements ASN1Choice {
   public static final int eciesNistP256 = 0;
   public static final int eciesBrainpoolP256r1 = 1;
   private final int choice;
   private final ASN1Encodable basePublicEncryptionKey;

   private BasePublicEncryptionKey(ASN1TaggedObject var1) {
      this.choice = var1.getTagNo();
      switch (this.choice) {
         case 0:
         case 1:
            this.basePublicEncryptionKey = EccP256CurvePoint.getInstance(var1.getExplicitBaseObject());
            return;
         default:
            throw new IllegalArgumentException("invalid choice value " + var1.getTagNo());
      }
   }

   public BasePublicEncryptionKey(int var1, ASN1Encodable var2) {
      this.choice = var1;
      this.basePublicEncryptionKey = var2;
   }

   public static BasePublicEncryptionKey getInstance(Object var0) {
      if (var0 instanceof BasePublicEncryptionKey) {
         return (BasePublicEncryptionKey)var0;
      } else {
         return var0 != null ? new BasePublicEncryptionKey(ASN1TaggedObject.getInstance(var0, 128)) : null;
      }
   }

   public static BasePublicEncryptionKey eciesNistP256(EccP256CurvePoint var0) {
      return new BasePublicEncryptionKey(0, var0);
   }

   public static BasePublicEncryptionKey eciesBrainpoolP256r1(EccP256CurvePoint var0) {
      return new BasePublicEncryptionKey(1, var0);
   }

   public int getChoice() {
      return this.choice;
   }

   public ASN1Encodable getBasePublicEncryptionKey() {
      return this.basePublicEncryptionKey;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERTaggedObject(this.choice, this.basePublicEncryptionKey);
   }

   public static class Builder {
      private int choice;
      private ASN1Encodable value;

      public BasePublicEncryptionKey.Builder setChoice(int var1) {
         this.choice = var1;
         return this;
      }

      public BasePublicEncryptionKey.Builder setValue(EccCurvePoint var1) {
         this.value = var1;
         return this;
      }

      public BasePublicEncryptionKey createBasePublicEncryptionKey() {
         return new BasePublicEncryptionKey(this.choice, this.value);
      }
   }
}
