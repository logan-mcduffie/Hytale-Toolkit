package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.oer.its.ItsUtils;
import org.bouncycastle.util.Arrays;

public class EcdsaP256Signature extends ASN1Object {
   private final EccP256CurvePoint rSig;
   private final ASN1OctetString sSig;

   public EcdsaP256Signature(EccP256CurvePoint var1, ASN1OctetString var2) {
      this.rSig = var1;
      this.sSig = var2;
   }

   private EcdsaP256Signature(ASN1Sequence var1) {
      if (var1.size() != 2) {
         throw new IllegalArgumentException("expected sequence size of 2");
      } else {
         this.rSig = EccP256CurvePoint.getInstance(var1.getObjectAt(0));
         this.sSig = ASN1OctetString.getInstance(var1.getObjectAt(1));
      }
   }

   public static EcdsaP256Signature getInstance(Object var0) {
      if (var0 instanceof EcdsaP256Signature) {
         return (EcdsaP256Signature)var0;
      } else {
         return var0 != null ? new EcdsaP256Signature(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public static EcdsaP256Signature.Builder builder() {
      return new EcdsaP256Signature.Builder();
   }

   public EccP256CurvePoint getRSig() {
      return this.rSig;
   }

   public ASN1OctetString getSSig() {
      return this.sSig;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return ItsUtils.toSequence(this.rSig, this.sSig);
   }

   public static class Builder {
      private EccP256CurvePoint rSig;
      private ASN1OctetString sSig;

      public EcdsaP256Signature.Builder setRSig(EccP256CurvePoint var1) {
         this.rSig = var1;
         return this;
      }

      public EcdsaP256Signature.Builder setSSig(byte[] var1) {
         this.sSig = new DEROctetString(Arrays.clone(var1));
         return this;
      }

      public EcdsaP256Signature.Builder setSSig(ASN1OctetString var1) {
         this.sSig = var1;
         return this;
      }

      public EcdsaP256Signature createEcdsaP256Signature() {
         return new EcdsaP256Signature(this.rSig, this.sSig);
      }
   }
}
