package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import org.bouncycastle.asn1.ASN1OctetString;

public class HashedId3 extends HashedId {
   public HashedId3(byte[] var1) {
      super(var1);
      if (var1.length != 3) {
         throw new IllegalArgumentException("hash id not 3 bytes");
      }
   }

   public static HashedId3 getInstance(Object var0) {
      if (var0 instanceof HashedId3) {
         return (HashedId3)var0;
      } else if (var0 != null) {
         byte[] var1 = ASN1OctetString.getInstance(var0).getOctets();
         return new HashedId3(var1);
      } else {
         return null;
      }
   }
}
