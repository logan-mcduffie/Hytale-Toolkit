package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.util.Arrays;

public class LaId extends ASN1Object {
   private final byte[] laId;

   public LaId(byte[] var1) {
      this.laId = var1;
      this.assertLength();
   }

   private LaId(ASN1OctetString var1) {
      this(var1.getOctets());
   }

   public static LaId getInstance(Object var0) {
      if (var0 instanceof LaId) {
         return (LaId)var0;
      } else {
         return var0 != null ? new LaId(DEROctetString.getInstance(var0)) : null;
      }
   }

   private void assertLength() {
      if (this.laId.length != 2) {
         throw new IllegalArgumentException("laId must be 2 octets");
      }
   }

   public byte[] getLaId() {
      return Arrays.clone(this.laId);
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DEROctetString(this.laId);
   }
}
