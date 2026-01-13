package org.bouncycastle.asn1.esf;

import org.bouncycastle.asn1.ASN1IA5String;
import org.bouncycastle.asn1.ASN1Primitive;

public class SPuri {
   private ASN1IA5String uri;

   public static SPuri getInstance(Object var0) {
      if (var0 instanceof SPuri) {
         return (SPuri)var0;
      } else {
         return var0 instanceof ASN1IA5String ? new SPuri(ASN1IA5String.getInstance(var0)) : null;
      }
   }

   public SPuri(ASN1IA5String var1) {
      this.uri = var1;
   }

   public ASN1IA5String getUriIA5() {
      return this.uri;
   }

   public ASN1Primitive toASN1Primitive() {
      return this.uri.toASN1Primitive();
   }
}
