package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1String;
import org.bouncycastle.asn1.ASN1UTF8String;
import org.bouncycastle.asn1.DERUTF8String;

public class Hostname extends ASN1Object {
   private final String hostName;

   public Hostname(String var1) {
      this.hostName = var1;
   }

   private Hostname(ASN1String var1) {
      this.hostName = var1.getString();
   }

   public static Hostname getInstance(Object var0) {
      if (var0 instanceof Hostname) {
         return (Hostname)var0;
      } else {
         return var0 != null ? new Hostname(ASN1UTF8String.getInstance(var0)) : null;
      }
   }

   public String getHostName() {
      return this.hostName;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERUTF8String(this.hostName);
   }
}
