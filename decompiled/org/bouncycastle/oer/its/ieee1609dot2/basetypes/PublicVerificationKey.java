package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERTaggedObject;

public class PublicVerificationKey extends ASN1Object implements ASN1Choice {
   public static final int ecdsaNistP256 = 0;
   public static final int ecdsaBrainpoolP256r1 = 1;
   public static final int ecdsaBrainpoolP384r1 = 2;
   private final int choice;
   private final ASN1Encodable publicVerificationKey;

   public PublicVerificationKey(int var1, ASN1Encodable var2) {
      this.choice = var1;
      this.publicVerificationKey = var2;
   }

   private PublicVerificationKey(ASN1TaggedObject var1) {
      this.choice = var1.getTagNo();
      switch (this.choice) {
         case 0:
         case 1:
            this.publicVerificationKey = EccP256CurvePoint.getInstance(var1.getExplicitBaseObject());
            return;
         case 2:
            this.publicVerificationKey = EccP384CurvePoint.getInstance(var1.getExplicitBaseObject());
            return;
         default:
            throw new IllegalArgumentException("invalid choice value " + var1.getTagNo());
      }
   }

   public static PublicVerificationKey ecdsaNistP256(EccP256CurvePoint var0) {
      return new PublicVerificationKey(0, var0);
   }

   public static PublicVerificationKey ecdsaBrainpoolP256r1(EccP256CurvePoint var0) {
      return new PublicVerificationKey(1, var0);
   }

   public static PublicVerificationKey ecdsaBrainpoolP384r1(EccP384CurvePoint var0) {
      return new PublicVerificationKey(2, var0);
   }

   public static PublicVerificationKey getInstance(Object var0) {
      if (var0 instanceof PublicVerificationKey) {
         return (PublicVerificationKey)var0;
      } else {
         return var0 != null ? new PublicVerificationKey(ASN1TaggedObject.getInstance(var0, 128)) : null;
      }
   }

   public static PublicVerificationKey.Builder builder() {
      return new PublicVerificationKey.Builder();
   }

   public int getChoice() {
      return this.choice;
   }

   public ASN1Encodable getPublicVerificationKey() {
      return this.publicVerificationKey;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERTaggedObject(this.choice, this.publicVerificationKey);
   }

   public static class Builder {
      private int choice;
      private ASN1Encodable curvePoint;

      public PublicVerificationKey.Builder setChoice(int var1) {
         this.choice = var1;
         return this;
      }

      public PublicVerificationKey.Builder setCurvePoint(EccCurvePoint var1) {
         this.curvePoint = var1;
         return this;
      }

      public PublicVerificationKey.Builder ecdsaNistP256(EccP256CurvePoint var1) {
         this.curvePoint = var1;
         return this;
      }

      public PublicVerificationKey.Builder ecdsaBrainpoolP256r1(EccP256CurvePoint var1) {
         this.curvePoint = var1;
         return this;
      }

      public PublicVerificationKey.Builder ecdsaBrainpoolP384r1(EccP384CurvePoint var1) {
         this.curvePoint = var1;
         return this;
      }

      public PublicVerificationKey.Builder extension(byte[] var1) {
         this.curvePoint = new DEROctetString(var1);
         return this;
      }

      public PublicVerificationKey createPublicVerificationKey() {
         return new PublicVerificationKey(this.choice, this.curvePoint);
      }
   }
}
