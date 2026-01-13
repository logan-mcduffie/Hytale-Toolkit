package org.bouncycastle.asn1;

import java.io.IOException;

public interface ASN1ExternalParser extends ASN1Encodable, InMemoryRepresentable {
   ASN1Encodable readObject() throws IOException;
}
