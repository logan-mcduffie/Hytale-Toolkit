package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.util.Arrays;

public class BitmapSsp extends ASN1Object {
   private final DEROctetString string;

   public BitmapSsp(byte[] var1) {
      this.string = new DEROctetString(Arrays.clone(var1));
   }

   public BitmapSsp(DEROctetString var1) {
      this.string = var1;
   }

   public static BitmapSsp getInstance(Object var0) {
      if (var0 instanceof BitmapSsp) {
         return (BitmapSsp)var0;
      } else {
         return var0 != null ? new BitmapSsp(DEROctetString.getInstance(var0).getOctets()) : null;
      }
   }

   public DEROctetString getString() {
      return this.string;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return this.string;
   }
}
