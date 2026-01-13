package org.bouncycastle.oer;

import org.bouncycastle.asn1.ASN1Encodable;

public interface Switch {
   Element result(SwitchIndexer var1);

   ASN1Encodable[] keys();
}
