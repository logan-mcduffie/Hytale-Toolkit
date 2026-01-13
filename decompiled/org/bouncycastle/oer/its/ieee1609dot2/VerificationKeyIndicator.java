package org.bouncycastle.oer.its.ieee1609dot2;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.EccP256CurvePoint;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.PublicVerificationKey;

public class VerificationKeyIndicator extends ASN1Object implements ASN1Choice {
   public static final int verificationKey = 0;
   public static final int reconstructionValue = 1;
   private final int choice;
   private final ASN1Encodable verificationKeyIndicator;

   public VerificationKeyIndicator(int var1, ASN1Encodable var2) {
      this.choice = var1;
      this.verificationKeyIndicator = var2;
   }

   private VerificationKeyIndicator(ASN1TaggedObject var1) {
      this.choice = var1.getTagNo();
      switch (this.choice) {
         case 0:
            this.verificationKeyIndicator = PublicVerificationKey.getInstance(var1.getExplicitBaseObject());
            break;
         case 1:
            this.verificationKeyIndicator = EccP256CurvePoint.getInstance(var1.getExplicitBaseObject());
            break;
         default:
            throw new IllegalArgumentException("invalid choice value " + this.choice);
      }
   }

   public static VerificationKeyIndicator verificationKey(PublicVerificationKey var0) {
      return new VerificationKeyIndicator(0, var0);
   }

   public static VerificationKeyIndicator reconstructionValue(EccP256CurvePoint var0) {
      return new VerificationKeyIndicator(1, var0);
   }

   public static VerificationKeyIndicator getInstance(Object var0) {
      if (var0 instanceof VerificationKeyIndicator) {
         return (VerificationKeyIndicator)var0;
      } else {
         return var0 != null ? new VerificationKeyIndicator(ASN1TaggedObject.getInstance(var0, 128)) : null;
      }
   }

   public int getChoice() {
      return this.choice;
   }

   public ASN1Encodable getVerificationKeyIndicator() {
      return this.verificationKeyIndicator;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERTaggedObject(this.choice, this.verificationKeyIndicator);
   }
}
