package org.bouncycastle.asn1.misc;

import org.bouncycastle.asn1.ASN1IA5String;
import org.bouncycastle.asn1.DERIA5String;

public class NetscapeRevocationURL extends DERIA5String {
   public NetscapeRevocationURL(ASN1IA5String var1) {
      super(var1.getString());
   }

   @Override
   public String toString() {
      return "NetscapeRevocationURL: " + this.getString();
   }
}
