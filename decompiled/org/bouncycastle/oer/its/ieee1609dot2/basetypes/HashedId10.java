package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import org.bouncycastle.asn1.ASN1OctetString;

public class HashedId10 extends HashedId {
   public HashedId10(byte[] var1) {
      super(var1);
      if (var1.length != 10) {
         throw new IllegalArgumentException("hash id not 10 bytes");
      }
   }

   public static HashedId10 getInstance(Object var0) {
      if (var0 instanceof HashedId10) {
         return (HashedId10)var0;
      } else if (var0 != null) {
         byte[] var1 = ASN1OctetString.getInstance(var0).getOctets();
         return new HashedId10(var1);
      } else {
         return null;
      }
   }
}
