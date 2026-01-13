package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.oer.its.ItsUtils;
import org.bouncycastle.util.Arrays;

public class EcdsaP384Signature extends ASN1Object {
   private final EccP384CurvePoint rSig;
   private final ASN1OctetString sSig;

   public EcdsaP384Signature(EccP384CurvePoint var1, ASN1OctetString var2) {
      this.rSig = var1;
      this.sSig = var2;
   }

   private EcdsaP384Signature(ASN1Sequence var1) {
      if (var1.size() != 2) {
         throw new IllegalArgumentException("expected sequence size of 2");
      } else {
         this.rSig = EccP384CurvePoint.getInstance(var1.getObjectAt(0));
         this.sSig = ASN1OctetString.getInstance(var1.getObjectAt(1));
      }
   }

   public static EcdsaP384Signature getInstance(Object var0) {
      if (var0 instanceof EcdsaP384Signature) {
         return (EcdsaP384Signature)var0;
      } else {
         return var0 != null ? new EcdsaP384Signature(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public EccP384CurvePoint getRSig() {
      return this.rSig;
   }

   public ASN1OctetString getSSig() {
      return this.sSig;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return ItsUtils.toSequence(this.rSig, this.sSig);
   }

   public static EcdsaP384Signature.Builder builder() {
      return new EcdsaP384Signature.Builder();
   }

   public static class Builder {
      private EccP384CurvePoint rSig;
      private ASN1OctetString sSig;

      public EcdsaP384Signature.Builder setRSig(EccP384CurvePoint var1) {
         this.rSig = var1;
         return this;
      }

      public EcdsaP384Signature.Builder setSSig(ASN1OctetString var1) {
         this.sSig = var1;
         return this;
      }

      public EcdsaP384Signature.Builder setSSig(byte[] var1) {
         return this.setSSig(new DEROctetString(Arrays.clone(var1)));
      }

      public EcdsaP384Signature createEcdsaP384Signature() {
         return new EcdsaP384Signature(this.rSig, this.sSig);
      }
   }
}
