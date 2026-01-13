package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERTaggedObject;

public class Signature extends ASN1Object implements ASN1Choice {
   public static final int ecdsaNistP256Signature = 0;
   public static final int ecdsaBrainpoolP256r1Signature = 1;
   public static final int ecdsaBrainpoolP384r1Signature = 2;
   private final int choice;
   private final ASN1Encodable signature;

   public Signature(int var1, ASN1Encodable var2) {
      this.choice = var1;
      this.signature = var2;
   }

   public static Signature ecdsaNistP256Signature(EcdsaP256Signature var0) {
      return new Signature(0, var0);
   }

   public static Signature ecdsaBrainpoolP256r1Signature(EcdsaP256Signature var0) {
      return new Signature(1, var0);
   }

   public static Signature ecdsaBrainpoolP384r1Signature(EcdsaP384Signature var0) {
      return new Signature(2, var0);
   }

   private Signature(ASN1TaggedObject var1) {
      this.choice = var1.getTagNo();
      switch (this.choice) {
         case 0:
         case 1:
            this.signature = EcdsaP256Signature.getInstance(var1.getExplicitBaseObject());
            break;
         case 2:
            this.signature = EcdsaP384Signature.getInstance(var1.getExplicitBaseObject());
            break;
         default:
            throw new IllegalArgumentException("invalid choice value " + var1.getTagNo());
      }
   }

   public static Signature getInstance(Object var0) {
      if (var0 instanceof Signature) {
         return (Signature)var0;
      } else {
         return var0 != null ? new Signature(ASN1TaggedObject.getInstance(var0, 128)) : null;
      }
   }

   public int getChoice() {
      return this.choice;
   }

   public ASN1Encodable getSignature() {
      return this.signature;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERTaggedObject(this.choice, this.signature);
   }
}
