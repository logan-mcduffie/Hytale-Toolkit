package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.DEROctetString;

public class LinkageValue extends DEROctetString {
   public LinkageValue(byte[] var1) {
      super(var1);
   }

   public LinkageValue(ASN1Encodable var1) throws IOException {
      super(var1);
   }

   public static LinkageValue getInstance(Object var0) {
      if (var0 instanceof LinkageValue) {
         return (LinkageValue)var0;
      } else {
         return var0 != null ? new LinkageValue(ASN1OctetString.getInstance(var0).getOctets()) : null;
      }
   }
}
