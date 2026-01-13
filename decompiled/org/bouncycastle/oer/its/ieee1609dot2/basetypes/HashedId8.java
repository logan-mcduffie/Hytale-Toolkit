package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import org.bouncycastle.asn1.ASN1OctetString;

public class HashedId8 extends HashedId {
   public HashedId8(byte[] var1) {
      super(var1);
      if (var1.length != 8) {
         throw new IllegalArgumentException("hash id not 8 bytes");
      }
   }

   public static HashedId8 getInstance(Object var0) {
      if (var0 instanceof HashedId8) {
         return (HashedId8)var0;
      } else if (var0 != null) {
         byte[] var1 = ASN1OctetString.getInstance(var0).getOctets();
         return new HashedId8(var1);
      } else {
         return null;
      }
   }
}
