package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERBitString;

public class AltSignatureValue extends ASN1Object {
   private final ASN1BitString signature;

   public static AltSignatureValue getInstance(ASN1TaggedObject var0, boolean var1) {
      return getInstance(ASN1BitString.getInstance(var0, var1));
   }

   public static AltSignatureValue getInstance(Object var0) {
      if (var0 instanceof AltSignatureValue) {
         return (AltSignatureValue)var0;
      } else {
         return var0 != null ? new AltSignatureValue(ASN1BitString.getInstance(var0)) : null;
      }
   }

   public static AltSignatureValue fromExtensions(Extensions var0) {
      return getInstance(Extensions.getExtensionParsedValue(var0, Extension.altSignatureValue));
   }

   private AltSignatureValue(ASN1BitString var1) {
      this.signature = var1;
   }

   public AltSignatureValue(byte[] var1) {
      this.signature = new DERBitString(var1);
   }

   public ASN1BitString getSignature() {
      return this.signature;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return this.signature;
   }
}
