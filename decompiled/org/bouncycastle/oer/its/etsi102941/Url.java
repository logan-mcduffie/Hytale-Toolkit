package org.bouncycastle.oer.its.etsi102941;

import org.bouncycastle.asn1.ASN1IA5String;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERIA5String;

public class Url extends ASN1Object {
   private final String url;

   public Url(String var1) {
      this.url = var1;
   }

   private Url(ASN1IA5String var1) {
      this.url = var1.getString();
   }

   public static Url getInstance(Object var0) {
      if (var0 instanceof Url) {
         return (Url)var0;
      } else {
         return var0 != null ? new Url(ASN1IA5String.getInstance(var0)) : null;
      }
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERIA5String(this.url);
   }

   public String getUrl() {
      return this.url;
   }
}
