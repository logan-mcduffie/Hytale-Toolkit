package org.bouncycastle.asn1.esf;

import org.bouncycastle.asn1.ASN1Null;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERNull;

public class SignaturePolicyIdentifier extends ASN1Object {
   private SignaturePolicyId signaturePolicyId;
   private boolean isSignaturePolicyImplied;

   public static SignaturePolicyIdentifier getInstance(Object var0) {
      if (var0 instanceof SignaturePolicyIdentifier) {
         return (SignaturePolicyIdentifier)var0;
      } else if (var0 instanceof ASN1Null || hasEncodedTagValue(var0, 5)) {
         return new SignaturePolicyIdentifier();
      } else {
         return var0 != null ? new SignaturePolicyIdentifier(SignaturePolicyId.getInstance(var0)) : null;
      }
   }

   public SignaturePolicyIdentifier() {
      this.isSignaturePolicyImplied = true;
   }

   public SignaturePolicyIdentifier(SignaturePolicyId var1) {
      this.signaturePolicyId = var1;
      this.isSignaturePolicyImplied = false;
   }

   public SignaturePolicyId getSignaturePolicyId() {
      return this.signaturePolicyId;
   }

   public boolean isSignaturePolicyImplied() {
      return this.isSignaturePolicyImplied;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return (ASN1Primitive)(this.isSignaturePolicyImplied ? DERNull.INSTANCE : this.signaturePolicyId.toASN1Primitive());
   }
}
