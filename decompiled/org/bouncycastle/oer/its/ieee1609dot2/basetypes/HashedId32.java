package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import org.bouncycastle.asn1.ASN1OctetString;

public class HashedId32 extends HashedId {
   public HashedId32(byte[] var1) {
      super(var1);
      if (var1.length != 32) {
         throw new IllegalArgumentException("hash id not 32 bytes");
      }
   }

   public static HashedId32 getInstance(Object var0) {
      if (var0 instanceof HashedId32) {
         return (HashedId32)var0;
      } else if (var0 != null) {
         byte[] var1 = ASN1OctetString.getInstance(var0).getOctets();
         return new HashedId32(var1);
      } else {
         return null;
      }
   }
}
