package org.bouncycastle.asn1.util;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Primitive;

/** @deprecated */
public class DERDump extends ASN1Dump {
   public static String dumpAsString(ASN1Primitive var0) {
      StringBuilder var1 = new StringBuilder();
      _dumpAsString("", false, var0, var1);
      return var1.toString();
   }

   public static String dumpAsString(ASN1Encodable var0) {
      return dumpAsString(var0.toASN1Primitive());
   }
}
