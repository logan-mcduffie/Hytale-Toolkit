package org.bouncycastle.asn1.x509;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1PrintableString;
import org.bouncycastle.util.encoders.Hex;

public abstract class X509NameEntryConverter {
   protected ASN1Primitive convertHexEncoded(String var1, int var2) throws IOException {
      return ASN1Primitive.fromByteArray(Hex.decodeStrict(var1, var2, var1.length() - var2));
   }

   protected boolean canBePrintable(String var1) {
      return ASN1PrintableString.isPrintableString(var1);
   }

   public abstract ASN1Primitive getConvertedValue(ASN1ObjectIdentifier var1, String var2);
}
