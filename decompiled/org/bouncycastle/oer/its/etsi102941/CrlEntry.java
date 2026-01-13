package org.bouncycastle.oer.its.etsi102941;

import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.HashedId;

public class CrlEntry extends HashedId {
   public CrlEntry(byte[] var1) {
      super(var1);
      if (var1.length != 8) {
         throw new IllegalArgumentException("expected 8 bytes");
      }
   }

   private CrlEntry(ASN1OctetString var1) {
      super(var1.getOctets());
   }

   public static CrlEntry getInstance(Object var0) {
      if (var0 instanceof CrlEntry) {
         return (CrlEntry)var0;
      } else {
         return var0 != null ? new CrlEntry(ASN1OctetString.getInstance(var0)) : null;
      }
   }
}
