package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.util.Arrays;

public class EciesP256EncryptedKey extends ASN1Object {
   private final EccP256CurvePoint v;
   private final ASN1OctetString c;
   private final ASN1OctetString t;

   public EciesP256EncryptedKey(EccP256CurvePoint var1, ASN1OctetString var2, ASN1OctetString var3) {
      this.v = var1;
      this.c = var2;
      this.t = var3;
   }

   public static EciesP256EncryptedKey getInstance(Object var0) {
      if (var0 instanceof EciesP256EncryptedKey) {
         return (EciesP256EncryptedKey)var0;
      } else {
         return var0 != null ? new EciesP256EncryptedKey(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   private EciesP256EncryptedKey(ASN1Sequence var1) {
      if (var1.size() != 3) {
         throw new IllegalArgumentException("expected sequence size of 3");
      } else {
         this.v = EccP256CurvePoint.getInstance(var1.getObjectAt(0));
         this.c = ASN1OctetString.getInstance(var1.getObjectAt(1));
         this.t = ASN1OctetString.getInstance(var1.getObjectAt(2));
      }
   }

   public EccP256CurvePoint getV() {
      return this.v;
   }

   public ASN1OctetString getC() {
      return this.c;
   }

   public ASN1OctetString getT() {
      return this.t;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERSequence(new ASN1Encodable[]{this.v, this.c, this.t});
   }

   public static EciesP256EncryptedKey.Builder builder() {
      return new EciesP256EncryptedKey.Builder();
   }

   public static class Builder {
      private EccP256CurvePoint v;
      private ASN1OctetString c;
      private ASN1OctetString t;

      public EciesP256EncryptedKey.Builder setV(EccP256CurvePoint var1) {
         this.v = var1;
         return this;
      }

      public EciesP256EncryptedKey.Builder setC(ASN1OctetString var1) {
         this.c = var1;
         return this;
      }

      public EciesP256EncryptedKey.Builder setC(byte[] var1) {
         this.c = new DEROctetString(Arrays.clone(var1));
         return this;
      }

      public EciesP256EncryptedKey.Builder setT(ASN1OctetString var1) {
         this.t = var1;
         return this;
      }

      public EciesP256EncryptedKey.Builder setT(byte[] var1) {
         this.t = new DEROctetString(Arrays.clone(var1));
         return this;
      }

      public EciesP256EncryptedKey createEciesP256EncryptedKey() {
         return new EciesP256EncryptedKey(this.v, this.c, this.t);
      }
   }
}
